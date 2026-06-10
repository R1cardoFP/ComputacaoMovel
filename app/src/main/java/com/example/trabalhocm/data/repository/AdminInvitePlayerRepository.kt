package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.AdminInvitePlayerData
import com.example.trabalhocm.data.model.AdminInvitePlayerTeam
import com.example.trabalhocm.data.model.AdminInvitePlayerUser
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class AdminInvitePlayerRepository {

    private val client = SupabaseClient.client

    suspend fun carregarDados(teamId: String): Result<AdminInvitePlayerData> {
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

            val nomeEquipa = equipa.text("nome").ifBlank { "Team" }

            val team = AdminInvitePlayerTeam(
                id = teamId,
                nome = nomeEquipa,
                sigla = equipa.nestedText("dados_equipa", "sigla").ifBlank {
                    initials(nomeEquipa)
                },
                modalidade = modalidadeNome
            )

            val membrosDaEquipa = client.from("membro_equipa")
                .select {
                    filter {
                        eq("id_equipa", idEquipa)
                    }
                }
                .decodeList<JsonObject>()

            val idsPendentes = membrosDaEquipa
                .filter { membro ->
                    membro.text("estado_convite") == "pendente"
                }
                .mapNotNull { membro ->
                    membro.text("id_utilizador").takeIf { it.isNotBlank() }
                }
                .distinct()

            val idsJaAssociadosOuConvidados = membrosDaEquipa
                .mapNotNull { membro ->
                    membro.text("id_utilizador").takeIf { it.isNotBlank() }
                }
                .toSet()

            val papeisJogador = client.from("utilizador_papel")
                .select {
                    filter {
                        eq("id_papel", 3)
                    }
                }
                .decodeList<JsonObject>()

            val idsJogadoresDisponiveis = papeisJogador
                .mapNotNull { papel ->
                    papel.text("id_utilizador").takeIf { it.isNotBlank() }
                }
                .filter { idUtilizador ->
                    idUtilizador !in idsJaAssociadosOuConvidados
                }
                .distinct()

            val availablePlayers = carregarUtilizadores(idsJogadoresDisponiveis)
            val invitedPlayers = carregarUtilizadores(idsPendentes)

            AdminInvitePlayerData(
                team = team,
                availablePlayers = availablePlayers,
                invitedPlayers = invitedPlayers
            )
        }
    }

    suspend fun enviarConvite(
        teamId: String,
        playerId: String,
        mensagem: String
    ): Result<Unit> {
        return runCatching {
            val idEquipa = teamId.toLongOrNull()
                ?: throw Exception("ID da equipa inválido.")

            val body = buildJsonObject {
                put("id_equipa", idEquipa)
                put("id_utilizador", playerId)
                put("estado_convite", "pendente")

                if (mensagem.isNotBlank()) {
                    put("mensagem", mensagem)
                }
            }

            client.from("membro_equipa")
                .insert(body)
        }
    }

    private suspend fun carregarUtilizadores(ids: List<String>): List<AdminInvitePlayerUser> {
        if (ids.isEmpty()) {
            return emptyList()
        }

        return client.from("utilizador")
            .select {
                filter {
                    isIn("id", ids)
                }
            }
            .decodeList<JsonObject>()
            .map { user ->
                val nome = user.text("nome").ifBlank { "Player" }

                AdminInvitePlayerUser(
                    id = user.text("id"),
                    nome = nome,
                    email = user.text("email"),
                    initials = initials(nome)
                )
            }
            .sortedBy { it.nome.lowercase() }
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