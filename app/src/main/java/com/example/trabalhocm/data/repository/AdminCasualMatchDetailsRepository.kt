package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.AdminCasualMatchDetails
import com.example.trabalhocm.data.model.AdminCasualMatchPlayer
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

class AdminCasualMatchDetailsRepository {

    private val client = SupabaseClient.client

    suspend fun obterDetalhesPeladinha(matchId: String): Result<AdminCasualMatchDetails> {
        return runCatching {
            val idPeladinha = matchId.toLongOrNull()
                ?: throw Exception("ID da peladinha inválido.")

            val peladinha = client.from("peladinha")
                .select {
                    filter {
                        eq("id", idPeladinha)
                    }
                }
                .decodeSingle<JsonObject>()

            val idModalidade = peladinha.intValue("id_modalidade")
            val idOrganizador = peladinha.text("id_organizador")

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

            val host = if (idOrganizador.isNotBlank()) {
                client.from("utilizador")
                    .select {
                        filter {
                            eq("id", idOrganizador)
                        }
                    }
                    .decodeSingle<JsonObject>()
            } else {
                null
            }

            val hostName = host?.text("nome")?.ifBlank { "Host" } ?: "Host"
            val hostEmail = host?.text("email") ?: ""

            val hostedMatchesCount = if (idOrganizador.isNotBlank()) {
                client.from("peladinha")
                    .select {
                        filter {
                            eq("id_organizador", idOrganizador)
                        }
                    }
                    .decodeList<JsonObject>()
                    .size
            } else {
                0
            }

            val jogadoresPeladinha = client.from("peladinha_jogador")
                .select {
                    filter {
                        eq("id_peladinha", idPeladinha)
                        eq("estado", "aceite")
                    }
                }
                .decodeList<JsonObject>()

            val idsJogadores = jogadoresPeladinha
                .mapNotNull { jogador ->
                    jogador.text("id_utilizador").takeIf { it.isNotBlank() }
                }
                .distinct()

            val jogadores = if (idsJogadores.isNotEmpty()) {
                client.from("utilizador")
                    .select {
                        filter {
                            isIn("id", idsJogadores)
                        }
                    }
                    .decodeList<JsonObject>()
                    .map { user ->
                        val nome = user.text("nome").ifBlank { "Player" }

                        AdminCasualMatchPlayer(
                            id = user.text("id"),
                            nome = nome,
                            email = user.text("email"),
                            initials = initials(nome)
                        )
                    }
                    .sortedBy { it.nome.lowercase() }
            } else {
                emptyList()
            }

            val title = peladinha.text("descricao").ifBlank { "Casual Match" }

            AdminCasualMatchDetails(
                id = matchId,
                title = title,
                description = "Friendly casual match at ${peladinha.text("local").ifBlank { "the selected location" }}. Open to all levels.",
                modalidade = modalidadeNome,
                date = peladinha.text("data"),
                time = peladinha.text("hora"),
                local = peladinha.text("local").ifBlank { "Location not defined" },
                estado = peladinha.text("estado").ifBlank { "aberta" },
                maxPlayers = peladinha.intValue("max_jogadores") ?: 0,
                acceptedPlayers = jogadores.size,
                hostName = hostName,
                hostEmail = hostEmail,
                hostInitials = initials(hostName),
                hostedMatchesCount = hostedMatchesCount,
                players = jogadores
            )
        }
    }

    suspend fun cancelarPeladinha(matchId: String): Result<Unit> {
        return runCatching {
            val idPeladinha = matchId.toLongOrNull()
                ?: throw Exception("ID da peladinha inválido.")

            client.from("peladinha")
                .update(
                    mapOf("estado" to "cancelada")
                ) {
                    filter {
                        eq("id", idPeladinha)
                    }
                }
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