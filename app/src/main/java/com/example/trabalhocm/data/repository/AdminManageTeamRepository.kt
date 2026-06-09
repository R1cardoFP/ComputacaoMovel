package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.AdminManageTeam
import com.example.trabalhocm.data.model.AdminManageTeamPlayer
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class AdminManageTeamRepository {

    private val client = SupabaseClient.client

    suspend fun obterEquipa(teamId: String): Result<AdminManageTeam> {
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
                        eq("estado_convite", "aceite")
                    }
                }
                .decodeList<JsonObject>()

            val idsUtilizadores = membros
                .mapNotNull { it.text("id_utilizador").takeIf { id -> id.isNotBlank() } }
                .distinct()

            val utilizadores = if (idsUtilizadores.isNotEmpty()) {
                client.from("utilizador")
                    .select {
                        filter {
                            isIn("id", idsUtilizadores)
                        }
                    }
                    .decodeList<JsonObject>()
            } else {
                emptyList()
            }

            val players = utilizadores
                .map { user ->
                    val nome = user.text("nome").ifBlank { "Player" }
                    val email = user.text("email")

                    AdminManageTeamPlayer(
                        id = user.text("id"),
                        nome = nome,
                        email = email,
                        initials = initials(nome)
                    )
                }
                .sortedBy { it.nome.lowercase() }

            val nomeEquipa = equipa.text("nome").ifBlank { "Team" }

            AdminManageTeam(
                id = teamId,
                nome = nomeEquipa,
                sigla = equipa.nestedText("dados_equipa", "sigla").ifBlank {
                    initials(nomeEquipa)
                },
                modalidade = modalidadeNome,
                playersCount = players.size,
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
            if (direct != null) return direct

            val fromText = primitive?.contentOrNull?.toIntOrNull()
            if (fromText != null) return fromText
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

    private fun initials(name: String): String {
        val parts = name.trim()
            .split(" ")
            .filter { it.isNotBlank() }

        return when {
            parts.isEmpty() -> "?"
            parts.size == 1 -> parts.first().take(2).uppercase()
            else -> "${parts.first().take(1)}${parts.last().take(1)}".uppercase()
        }
    }
}