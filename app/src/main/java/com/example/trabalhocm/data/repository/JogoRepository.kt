package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.Equipa
import com.example.trabalhocm.data.model.EventoJogo
import com.example.trabalhocm.data.model.Jogo
import com.example.trabalhocm.data.model.JogoEquipa
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

data class EquipaSimples(
    val id: Long,
    val nome: String
)

data class MatchControlInfo(
    val idJogo: Long,
    val torneioNome: String,
    val data: String,
    val hora: String,
    val local: String,
    val estado: String,
    val idEquipaCasa: Long,
    val equipaCasa: String,
    val pontosCasa: Int,
    val idEquipaFora: Long,
    val equipaFora: String,
    val pontosFora: Int
)

class JogoRepository {
    private val client = SupabaseClient.client

    suspend fun listarJogos(): Result<List<Jogo>> = runCatching {
        client.from("jogo").select().decodeList<Jogo>()
    }

    suspend fun listarJogoEquipas(): Result<List<JogoEquipa>> = runCatching {
        client.from("jogo_equipa").select().decodeList<JogoEquipa>()
    }

    suspend fun listarMeusEventos(userId: String): Result<List<EventoJogo>> = runCatching {
        client.from("evento_jogo")
            .select { filter { eq("id_jogador", userId) } }
            .decodeList<EventoJogo>()
    }

    /** Equipas com inscrição aprovada num torneio — candidatas a entrar num jogo. */
    suspend fun listarEquipasAprovadas(idTorneio: Long): Result<List<EquipaSimples>> = runCatching {
        val inscricoes = client.from("torneio_equipa")
            .select {
                filter {
                    eq("id_torneio", idTorneio)
                    eq("estado", "aprovada")
                }
            }
            .decodeList<JsonObject>()

        val idsEquipas = inscricoes.mapNotNull {
            it["id_equipa"]?.jsonPrimitive?.longOrNull
        }

        if (idsEquipas.isEmpty()) {
            emptyList()
        } else {
            client.from("equipa")
                .select { filter { isIn("id", idsEquipas) } }
                .decodeList<Equipa>()
                .map { EquipaSimples(it.id, it.nome) }
                .sortedBy { it.nome.lowercase() }
        }
    }

    /** Cria um jogo agendado com as duas equipas (casa/fora) e devolve o id do jogo. */
    suspend fun criarJogo(
        idTorneio: Long,
        idEquipaCasa: Long,
        idEquipaFora: Long,
        data: String,
        hora: String,
        local: String?
    ): Result<Long> = runCatching {
        val jogo = client.from("jogo")
            .insert(
                NovoJogoRequest(
                    idTorneio = idTorneio,
                    data = data,
                    hora = hora,
                    local = local
                )
            ) { select() }
            .decodeSingle<Jogo>()

        client.from("jogo_equipa")
            .insert(
                listOf(
                    NovoJogoEquipaRequest(jogo.id, idEquipaCasa, "casa"),
                    NovoJogoEquipaRequest(jogo.id, idEquipaFora, "fora")
                )
            )

        jogo.id
    }

    /** Carrega o estado de um jogo para o ecrã de controlo ao vivo. */
    suspend fun obterControloJogo(idJogo: Long): Result<MatchControlInfo> = runCatching {
        val jogo = client.from("jogo")
            .select { filter { eq("id", idJogo) } }
            .decodeSingle<Jogo>()

        val jogoEquipas = client.from("jogo_equipa")
            .select { filter { eq("id_jogo", idJogo) } }
            .decodeList<JogoEquipa>()

        val casa = jogoEquipas.firstOrNull { it.papelEquipa == "casa" }
            ?: throw Exception("Jogo sem equipa da casa definida.")
        val fora = jogoEquipas.firstOrNull { it.papelEquipa == "fora" }
            ?: throw Exception("Jogo sem equipa visitante definida.")

        val equipas = client.from("equipa").select().decodeList<Equipa>().associateBy { it.id }

        val torneioNome = client.from("torneio")
            .select { filter { eq("id", jogo.idTorneio) } }
            .decodeList<JsonObject>()
            .firstOrNull()
            ?.get("nome")?.jsonPrimitive?.contentOrNull
            ?: "Torneio"

        MatchControlInfo(
            idJogo = jogo.id,
            torneioNome = torneioNome,
            data = jogo.data,
            hora = jogo.hora,
            local = jogo.local ?: "",
            estado = jogo.estadoJogo,
            idEquipaCasa = casa.idEquipa,
            equipaCasa = equipas[casa.idEquipa]?.nome ?: "Equipa Casa",
            pontosCasa = casa.pontosMarcados,
            idEquipaFora = fora.idEquipa,
            equipaFora = equipas[fora.idEquipa]?.nome ?: "Equipa Fora",
            pontosFora = fora.pontosMarcados
        )
    }

    /** Procura o primeiro jogo em direto (para o atalho do match center sem id). */
    suspend fun obterPrimeiroJogoEmDireto(): Result<Long?> = runCatching {
        client.from("jogo")
            .select { filter { eq("estado_jogo", "em_direto") } }
            .decodeList<Jogo>()
            .firstOrNull()
            ?.id
    }

    suspend fun atualizarPontos(idJogo: Long, idEquipa: Long, novosPontos: Int): Result<Unit> = runCatching {
        client.from("jogo_equipa")
            .update(JsonObject(mapOf("pontos_marcados" to JsonPrimitive(novosPontos)))) {
                filter {
                    eq("id_jogo", idJogo)
                    eq("id_equipa", idEquipa)
                }
            }
    }

    suspend fun atualizarEstadoJogo(
        idJogo: Long,
        estado: String,
        resultadoFinal: String? = null
    ): Result<Unit> = runCatching {
        val campos = buildMap {
            put("estado_jogo", JsonPrimitive(estado))
            if (resultadoFinal != null) {
                put("resultado_final", JsonPrimitive(resultadoFinal))
            }
        }

        client.from("jogo")
            .update(JsonObject(campos)) {
                filter { eq("id", idJogo) }
            }
    }
}

@Serializable
private data class NovoJogoRequest(
    @SerialName("id_torneio") val idTorneio: Long,
    val data: String,
    val hora: String,
    val local: String? = null,
    @SerialName("estado_jogo") val estadoJogo: String = "agendado"
)

@Serializable
private data class NovoJogoEquipaRequest(
    @SerialName("id_jogo") val idJogo: Long,
    @SerialName("id_equipa") val idEquipa: Long,
    @SerialName("papel_equipa") val papelEquipa: String,
    @SerialName("pontos_marcados") val pontosMarcados: Int = 0
)
