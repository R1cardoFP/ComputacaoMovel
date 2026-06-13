package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

data class LiveMatchInfo(
    val idJogo: Long,
    val equipaCasa: String,
    val equipaFora: String,
    val pontosCasa: Int,
    val pontosFora: Int,
    val minuto: Int,
    val local: String,
    val torneioNome: String
)

data class LiveMatchEventInfo(
    val id: Long,
    val idJogo: Long,
    val minuto: Int,
    val tipoEvento: String
)

class LiveMatchRepository {

    private val client = SupabaseClient.client

    suspend fun listarJogosEmDireto(): Result<List<LiveMatchInfo>> {
        return runCatching {
            val jogos = client.from("jogo")
                .select()
                .decodeList<JsonObject>()

            val jogoEquipas = client.from("jogo_equipa")
                .select()
                .decodeList<JsonObject>()

            val equipas = client.from("equipa")
                .select()
                .decodeList<JsonObject>()

            val torneios = client.from("torneio")
                .select()
                .decodeList<JsonObject>()

            val equipasPorId = equipas.associateBy { it.longValue("id") }
            val torneiosPorId = torneios.associateBy { it.longValue("id") }
            val jogoEquipasPorJogo = jogoEquipas.groupBy { it.longValue("id_jogo") }

            val agora = LocalDateTime.now()

            jogos
                .filter { jogo ->
                    jogoEstaLive(jogo, agora)
                }
                .mapNotNull { jogo ->
                    val idJogo = jogo.longValue("id")
                    val equipasDoJogo = jogoEquipasPorJogo[idJogo].orEmpty()

                    val equipaCasaLinha = equipasDoJogo.firstOrNull {
                        it.stringValue("papel_equipa") == "casa"
                    }

                    val equipaForaLinha = equipasDoJogo.firstOrNull {
                        it.stringValue("papel_equipa") == "fora"
                    }

                    if (equipaCasaLinha == null || equipaForaLinha == null) {
                        return@mapNotNull null
                    }

                    val equipaCasa = equipasPorId[equipaCasaLinha.longValue("id_equipa")]
                    val equipaFora = equipasPorId[equipaForaLinha.longValue("id_equipa")]
                    val torneio = torneiosPorId[jogo.longValue("id_torneio")]

                    LiveMatchInfo(
                        idJogo = idJogo,
                        equipaCasa = equipaCasa?.stringValue("nome") ?: "Equipa Casa",
                        equipaFora = equipaFora?.stringValue("nome") ?: "Equipa Fora",
                        pontosCasa = equipaCasaLinha.intValue("pontos_marcados"),
                        pontosFora = equipaForaLinha.intValue("pontos_marcados"),
                        minuto = calcularMinutoLive(jogo, agora),
                        local = jogo.stringValue("local") ?: "Local por definir",
                        torneioNome = torneio?.stringValue("nome") ?: "Atlantic Cup 2026"
                    )
                }
        }
    }

    suspend fun obterJogoEmDiretoPorId(idJogo: Long): Result<LiveMatchInfo?> {
        return runCatching {
            listarJogosEmDireto().getOrThrow()
                .firstOrNull { it.idJogo == idJogo }
        }
    }

    suspend fun listarEventosDoJogo(idJogo: Long): Result<List<LiveMatchEventInfo>> {
        return runCatching {
            client.from("evento_jogo")
                .select()
                .decodeList<JsonObject>()
                .filter { evento ->
                    evento.longValue("id_jogo") == idJogo
                }
                .map { evento ->
                    LiveMatchEventInfo(
                        id = evento.longValue("id"),
                        idJogo = evento.longValue("id_jogo"),
                        minuto = evento.intValue("minuto"),
                        tipoEvento = evento.stringValue("tipo_evento") ?: "Evento"
                    )
                }
                .sortedByDescending { it.minuto }
        }
    }
}

private fun JsonObject.stringValue(key: String): String? {
    return this[key]?.jsonPrimitive?.contentOrNull
}

private fun JsonObject.longValue(key: String): Long {
    return this[key]?.jsonPrimitive?.longOrNull ?: 0L
}

private fun JsonObject.intValue(key: String): Int {
    return this[key]?.jsonPrimitive?.intOrNull ?: 0
}

private const val DURACAO_JOGO_MIN = 90L

// Estados que indicam que o jogo já não está a decorrer.
private val ESTADOS_FINAIS = setOf("terminado", "cancelado", "concluido", "finalizado", "adiado")

private fun jogoInicioLive(jogo: JsonObject): LocalDateTime? {
    val data = runCatching { LocalDate.parse(jogo.stringValue("data")?.take(10).orEmpty()) }.getOrNull() ?: return null
    val hora = runCatching { LocalTime.parse(jogo.stringValue("hora")?.take(5).orEmpty()) }.getOrNull() ?: LocalTime.MIDNIGHT
    return LocalDateTime.of(data, hora)
}

// Um jogo está "live" se estiver marcado em_direto, OU se a sua hora de início já
// passou e ainda não passaram ~90 min (duração de um jogo de futebol).
private fun jogoEstaLive(jogo: JsonObject, agora: LocalDateTime): Boolean {
    val estado = jogo.stringValue("estado_jogo")?.lowercase().orEmpty()
    if (estado == "em_direto" || estado == "live" || estado == "a_decorrer" || estado == "em_decorrer") return true
    if (estado in ESTADOS_FINAIS) return false
    val inicio = jogoInicioLive(jogo) ?: return false
    return !agora.isBefore(inicio) && agora.isBefore(inicio.plusMinutes(DURACAO_JOGO_MIN))
}

private fun calcularMinutoLive(jogo: JsonObject, agora: LocalDateTime): Int {
    val inicio = jogoInicioLive(jogo) ?: return 75
    val mins = ChronoUnit.MINUTES.between(inicio, agora)
    return mins.coerceIn(1L, DURACAO_JOGO_MIN).toInt()
}