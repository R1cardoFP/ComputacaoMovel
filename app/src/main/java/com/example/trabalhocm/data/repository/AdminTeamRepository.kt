package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.AdminTeam
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

class AdminTeamRepository {

    private val client = SupabaseClient.client

    suspend fun listarEquipasAdmin(): Result<List<AdminTeam>> {
        return runCatching {
            val equipas = client.from("equipa")
                .select()
                .decodeList<JsonObject>()

            val modalidades = client.from("modalidade")
                .select()
                .decodeList<JsonObject>()

            equipas.map { equipa ->
                val id = equipa.text("id")

                val idModalidade = equipa.intValue("id_modalidade", "modalidade_id")

                val modalidadeNome = modalidades
                    .firstOrNull { modalidade ->
                        modalidade.intValue("id") == idModalidade
                    }
                    ?.text("nome")
                    ?: equipa.text("modalidade").ifBlank { "Sem modalidade" }

                val divisao = equipa.text("divisao", "division", "categoria", "nivel")
                    .ifBlank { modalidadeNome }

                AdminTeam(
                    id = id,
                    nome = equipa.text("nome", "name").ifBlank { "Equipa sem nome" },
                    modalidade = modalidadeNome,
                    divisao = divisao,
                    playersCount = equipa.intValue(
                        "players_count",
                        "num_jogadores",
                        "numero_jogadores",
                        "total_jogadores"
                    ) ?: 0,
                    wins = equipa.intValue("vitorias", "wins", "win_count") ?: 0,
                    losses = equipa.intValue("derrotas", "losses", "loss_count") ?: 0,
                    streak = equipa.text("streak", "sequencia").ifBlank { "W0" }
                )
            }.sortedBy { it.nome.lowercase() }
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

            val intDirect = primitive?.intOrNull
            if (intDirect != null) {
                return intDirect
            }

            val intFromText = primitive
                ?.contentOrNull
                ?.toIntOrNull()

            if (intFromText != null) {
                return intFromText
            }
        }

        return null
    }

    suspend fun apagarEquipa(teamId: String): Result<Unit> {
        return runCatching {
            val id = teamId.toLongOrNull()
                ?: throw Exception("ID da equipa inválido.")

            client.from("equipa")
                .delete {
                    filter {
                        eq("id", id)
                    }
                }
        }
    }
}