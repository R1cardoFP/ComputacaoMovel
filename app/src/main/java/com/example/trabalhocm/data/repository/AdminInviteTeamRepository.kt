package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.AdminInviteTeam
import com.example.trabalhocm.data.model.AdminInviteTeamsData
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit

class AdminInviteTeamRepository {

    private val client = SupabaseClient.client

    suspend fun carregarEquipasParaConvite(tournamentId: String): Result<AdminInviteTeamsData> {
        return runCatching {
            val idTorneio = tournamentId.toLongOrNull()
                ?: throw Exception("ID do torneio inválido.")

            val torneio = client.from("torneio")
                .select {
                    filter {
                        eq("id", idTorneio)
                    }
                }
                .decodeSingle<JsonObject>()

            val idModalidade = torneio.intValue("id_modalidade")
                ?: throw Exception("Torneio sem modalidade associada.")

            val modalidade = client.from("modalidade")
                .select {
                    filter {
                        eq("id", idModalidade)
                    }
                }
                .decodeSingle<JsonObject>()

            val modalidadeNome = modalidade.text("nome").ifBlank { "Modalidade" }

            val equipas = client.from("equipa")
                .select {
                    filter {
                        eq("id_modalidade", idModalidade)
                    }
                }
                .decodeList<JsonObject>()

            val inscricoes = client.from("torneio_equipa")
                .select {
                    filter {
                        eq("id_torneio", idTorneio)
                    }
                }
                .decodeList<JsonObject>()

            val inscricoesPorEquipa = inscricoes.associateBy {
                it.longValue("id_equipa")
            }

            val teams = equipas.map { equipa ->
                val idEquipa = equipa.longValue("id") ?: 0L
                val inscricao = inscricoesPorEquipa[idEquipa]

                val estado = inscricao?.text("estado") ?: ""
                val isInvited = estado.equals("pendente", ignoreCase = true) ||
                        estado.equals("aprovada", ignoreCase = true)

                AdminInviteTeam(
                    teamId = idEquipa.toString(),
                    teamName = equipa.text("nome").ifBlank { "Equipa sem nome" },
                    captainName = equipa.nestedText("dados_equipa", "capitao", "captain", "sigla")
                        .ifBlank { "Sem capitão" },
                    division = modalidadeNome,
                    isInvited = isInvited,
                    registrationStatus = estado,
                    invitedAgo = formatTimeAgo(inscricao?.text("created_at") ?: "")
                )
            }.sortedBy { it.teamName.lowercase() }

            val sentInvitations = teams.filter {
                it.registrationStatus.equals("pendente", ignoreCase = true)
            }

            val registeredOrInvitedCount = inscricoes.count {
                it.text("estado").equals("pendente", ignoreCase = true) ||
                        it.text("estado").equals("aprovada", ignoreCase = true)
            }

            val maxTeams = torneio.intValue("max_equipas", "limite_equipas") ?: 16

            AdminInviteTeamsData(
                tournamentId = tournamentId,
                tournamentName = torneio.text("nome").ifBlank { "Tournament" },
                slotsRemaining = (maxTeams - registeredOrInvitedCount).coerceAtLeast(0),
                maxTeams = maxTeams,
                teams = teams,
                sentInvitations = sentInvitations
            )
        }
    }

    suspend fun convidarEquipa(
        tournamentId: String,
        teamId: String
    ): Result<Unit> {
        return runCatching {
            val idTorneio = tournamentId.toLongOrNull()
                ?: throw Exception("ID do torneio inválido.")

            val idEquipa = teamId.toLongOrNull()
                ?: throw Exception("ID da equipa inválido.")

            val dados = buildJsonObject {
                put("id_torneio", idTorneio)
                put("id_equipa", idEquipa)
                put("estado", "pendente")
                put("pagamento_estado", "pendente")
                put("mensagem", "Convite enviado pelo organizador.")
            }

            client.from("torneio_equipa")
                .insert(dados)
        }
    }

    private fun formatTimeAgo(createdAt: String): String {
        if (createdAt.isBlank()) {
            return "recently"
        }

        return try {
            val created = OffsetDateTime.parse(createdAt)
            val now = OffsetDateTime.now()

            val minutes = ChronoUnit.MINUTES.between(created, now)
            val hours = ChronoUnit.HOURS.between(created, now)
            val days = ChronoUnit.DAYS.between(created, now)

            when {
                minutes < 1 -> "just now"
                minutes < 60 -> "${minutes} min ago"
                hours < 24 -> "${hours}h ago"
                days == 1L -> "1 day ago"
                else -> "${days} days ago"
            }
        } catch (e: Exception) {
            "recently"
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

    private fun JsonObject.longValue(vararg keys: String): Long? {
        keys.forEach { key ->
            val primitive = this[key]?.jsonPrimitive

            val fromText = primitive
                ?.contentOrNull
                ?.toLongOrNull()

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
}