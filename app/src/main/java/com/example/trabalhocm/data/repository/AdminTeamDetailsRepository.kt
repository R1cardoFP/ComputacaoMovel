package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.AdminTeamDetails
import com.example.trabalhocm.data.model.AdminTeamPlayer
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class AdminTeamDetailsRepository {

    private val client = SupabaseClient.client

    suspend fun obterDetalhesEquipa(teamId: String): Result<AdminTeamDetails> {
        return runCatching {
            val idEquipa = teamId.toLongOrNull()
                ?: throw Exception("ID da equipa inválido.")

            val equipa = client.from("equipa")
                .select {
                    filter {
                        eq("id", idEquipa)
                    }
                }
                .decodeSingle<JsonObject>()

            val idModalidade = equipa.intValue("id_modalidade")

            val modalidadeNome = if (idModalidade != null) {
                client.from("modalidade")
                    .select {
                        filter {
                            eq("id", idModalidade)
                        }
                    }
                    .decodeSingle<JsonObject>()
                    .text("nome")
                    .ifBlank { "Modalidade" }
            } else {
                "Modalidade"
            }

            val membros = client.from("membro_equipa")
                .select {
                    filter {
                        eq("id_equipa", idEquipa)
                    }
                }
                .decodeList<JsonObject>()

            val utilizadores = client.from("utilizador")
                .select()
                .decodeList<JsonObject>()

            val players = membros
                .filter { membro ->
                    val estado = membro.text("estado_convite")
                    estado.isBlank() || estado.equals("aceite", ignoreCase = true)
                }
                .mapNotNull { membro ->
                    val idUtilizador = membro.text("id_utilizador")

                    if (idUtilizador.isBlank()) {
                        return@mapNotNull null
                    }

                    val utilizador = utilizadores.firstOrNull { user ->
                        user.text("id") == idUtilizador
                    } ?: return@mapNotNull null

                    AdminTeamPlayer(
                        id = idUtilizador,
                        nome = utilizador.text("nome").ifBlank { "Player" },
                        email = utilizador.text("email")
                    )
                }

            val wins = equipa.intValue("vitorias", "wins")
                ?: equipa.nestedIntValue("dados_equipa", "wins", "vitorias")
                ?: 0

            val draws = equipa.intValue("empates", "draws")
                ?: equipa.nestedIntValue("dados_equipa", "draws", "empates")
                ?: 0

            val losses = equipa.intValue("derrotas", "losses")
                ?: equipa.nestedIntValue("dados_equipa", "losses", "derrotas")
                ?: 0

            val matches = equipa.intValue("jogos", "matches_played")
                ?: equipa.nestedIntValue("dados_equipa", "matches_played", "jogos")
                ?: (wins + draws + losses)

            val winRate = if (matches > 0) {
                val percent = (wins.toFloat() / matches.toFloat()) * 100f
                "%.1f%%".format(percent)
            } else {
                "0%"
            }

            AdminTeamDetails(
                id = idEquipa.toString(),
                nome = equipa.text("nome").ifBlank { "Team" },
                sigla = equipa.nestedText("dados_equipa", "sigla").ifBlank {
                    equipa.text("nome").take(3).uppercase()
                },
                modalidade = modalidadeNome,
                local = equipa.nestedText("dados_equipa", "local", "cidade").ifBlank {
                    "Location not defined"
                },
                seasonWinRate = winRate,
                totalGoals = equipa.intValue("golos", "total_goals")
                    ?: equipa.nestedIntValue("dados_equipa", "total_goals", "golos")
                    ?: 0,
                matchesPlayed = matches,
                wins = wins,
                draws = draws,
                losses = losses,
                players = players
            )
        }
    }

    private fun JsonObject.text(vararg keys: String): String {
        keys.forEach { key ->
            val value = this[key]
                ?.jsonPrimitive
                ?.contentOrNull

            if (!value.isNullOrBlank()) {
                return value
            }
        }

        return ""
    }

    private fun JsonObject.intValue(vararg keys: String): Int? {
        keys.forEach { key ->
            val primitive = this[key]?.jsonPrimitive

            val direct = primitive?.intOrNull
            if (direct != null) {
                return direct
            }

            val fromText = primitive
                ?.contentOrNull
                ?.toIntOrNull()

            if (fromText != null) {
                return fromText
            }
        }

        return null
    }

    private fun JsonObject.nestedText(objectKey: String, vararg keys: String): String {
        val obj = this[objectKey]?.jsonObject ?: return ""

        keys.forEach { key ->
            val value = obj[key]
                ?.jsonPrimitive
                ?.contentOrNull

            if (!value.isNullOrBlank()) {
                return value
            }
        }

        return ""
    }

    private fun JsonObject.nestedIntValue(objectKey: String, vararg keys: String): Int? {
        val obj = this[objectKey]?.jsonObject ?: return null

        keys.forEach { key ->
            val primitive = obj[key]?.jsonPrimitive

            val direct = primitive?.intOrNull
            if (direct != null) {
                return direct
            }

            val fromText = primitive
                ?.contentOrNull
                ?.toIntOrNull()

            if (fromText != null) {
                return fromText
            }
        }

        return null
    }
}