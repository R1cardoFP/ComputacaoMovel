package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

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

            jogos
                .filter { jogo ->
                    jogo.stringValue("estado_jogo") == "em_direto"
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
                        minuto = 75,
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