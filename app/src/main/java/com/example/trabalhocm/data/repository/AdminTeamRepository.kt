package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.AdminTeam
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.jsonObject

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
                    wins = equipa.nestedIntValue("dados_equipa", "wins", "vitorias") ?: 0,
                    losses = equipa.nestedIntValue("dados_equipa", "losses", "derrotas") ?: 0,
                    streak = equipa.nestedTextValue("dados_equipa", "streak", "serie").ifBlank { "W0" }
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

    private fun JsonObject.nestedIntValue(objectKey: String, vararg keys: String): Int? {
        val obj = this[objectKey]?.jsonObject ?: return null

        keys.forEach { key ->
            val primitive = obj[key]?.jsonPrimitive

            val direct = primitive?.intOrNull
            if (direct != null) return direct

            val fromText = primitive?.contentOrNull?.toIntOrNull()
            if (fromText != null) return fromText
        }

        return null
    }

    private fun JsonObject.nestedTextValue(objectKey: String, vararg keys: String): String {
        val obj = this[objectKey]?.jsonObject ?: return ""

        keys.forEach { key ->
            val value = obj[key]?.jsonPrimitive?.contentOrNull

            if (!value.isNullOrBlank()) {
                return value
            }
        }

        return ""
    }
}