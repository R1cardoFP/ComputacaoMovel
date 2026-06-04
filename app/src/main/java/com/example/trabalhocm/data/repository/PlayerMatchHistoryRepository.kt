package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

data class PlayerMatchHistoryItem(
    val idJogo: Long,
    val minhaEquipaNome: String,
    val adversarioNome: String,
    val meusPontos: Int,
    val adversarioPontos: Int,
    val data: String,
    val hora: String,
    val local: String,
    val torneioNome: String,
    val resultado: String
)

class PlayerMatchHistoryRepository {

    private val client = SupabaseClient.client

    suspend fun listarHistoricoJogador(): Result<List<PlayerMatchHistoryItem>> {
        return runCatching {
            val authRepository = AuthRepository()
            val utilizadorAtual = authRepository.obterUtilizadorAtual().getOrNull()

            val jogos = client.from("jogo")
                .select()
                .decodeList<JsonObject>()

            val jogoEquipas = client.from("jogo_equipa")
                .select()
                .decodeList<JsonObject>()

            val equipas = client.from("equipa")
                .select()
                .decodeList<JsonObject>()

            val membrosEquipa = client.from("membro_equipa")
                .select()
                .decodeList<JsonObject>()

            val torneios = client.from("torneio")
                .select()
                .decodeList<JsonObject>()

            val equipasPorId = equipas.associateBy { it.longValue("id") }
            val torneiosPorId = torneios.associateBy { it.longValue("id") }
            val jogoEquipasPorJogo = jogoEquipas.groupBy { it.longValue("id_jogo") }

            var minhasEquipasIds = if (utilizadorAtual != null) {
                membrosEquipa
                    .filter {
                        it.stringValue("id_utilizador") == utilizadorAtual.id &&
                                it.stringValue("estado_convite").orEmpty().lowercase() in listOf(
                            "aceite",
                            "aceito",
                            "accepted"
                        )
                    }
                    .map { it.longValue("id_equipa") }
                    .toSet()
            } else {
                emptySet()
            }

            /*
             * Fallback para testes:
             * Se estiveres a entrar diretamente no ecrã sem login, ou se o utilizador atual
             * não for o mesmo usado nos inserts, mostra os jogos das equipas associadas
             * na tabela membro_equipa.
             */
            if (minhasEquipasIds.isEmpty()) {
                minhasEquipasIds = membrosEquipa
                    .map { it.longValue("id_equipa") }
                    .toSet()
            }

            jogos.mapNotNull { jogo ->
                val idJogo = jogo.longValue("id")
                val linhasDoJogo = jogoEquipasPorJogo[idJogo].orEmpty()

                if (linhasDoJogo.size < 2) {
                    return@mapNotNull null
                }

                val minhaLinha = linhasDoJogo.firstOrNull {
                    it.longValue("id_equipa") in minhasEquipasIds
                } ?: return@mapNotNull null

                val adversarioLinha = linhasDoJogo.firstOrNull {
                    it.longValue("id_equipa") != minhaLinha.longValue("id_equipa")
                } ?: return@mapNotNull null

                val minhaEquipaId = minhaLinha.longValue("id_equipa")
                val adversarioId = adversarioLinha.longValue("id_equipa")

                val minhaEquipa = equipasPorId[minhaEquipaId]
                val adversario = equipasPorId[adversarioId]

                val meusPontos = minhaLinha.intValue("pontos_marcados")
                val adversarioPontos = adversarioLinha.intValue("pontos_marcados")

                val resultado = when {
                    meusPontos > adversarioPontos -> "WIN"
                    meusPontos < adversarioPontos -> "LOSS"
                    else -> "DRAW"
                }

                val torneio = torneiosPorId[jogo.longValue("id_torneio")]

                PlayerMatchHistoryItem(
                    idJogo = idJogo,
                    minhaEquipaNome = minhaEquipa?.stringValue("nome") ?: "Minha equipa",
                    adversarioNome = adversario?.stringValue("nome") ?: "Adversário",
                    meusPontos = meusPontos,
                    adversarioPontos = adversarioPontos,
                    data = jogo.stringValue("data") ?: "Data por definir",
                    hora = jogo.stringValue("hora")?.take(5) ?: "Hora por definir",
                    local = jogo.stringValue("local") ?: "Local por definir",
                    torneioNome = torneio?.stringValue("nome") ?: "League Match",
                    resultado = resultado
                )
            }.sortedByDescending { it.data }
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