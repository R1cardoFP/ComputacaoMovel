package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.AdminManageRegistrationData
import com.example.trabalhocm.data.model.AdminRegistrationTeam
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

class AdminRegistrationRepository {

    private val client = SupabaseClient.client

    suspend fun carregarGestaoInscricoes(tournamentId: String): Result<AdminManageRegistrationData> {
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

            val inscricoes = client.from("torneio_equipa")
                .select {
                    filter {
                        eq("id_torneio", idTorneio)
                    }
                }
                .decodeList<JsonObject>()

            val equipas = client.from("equipa")
                .select()
                .decodeList<JsonObject>()

            val modalidades = client.from("modalidade")
                .select()
                .decodeList<JsonObject>()

            val pendingTeams = mutableListOf<AdminRegistrationTeam>()
            val approvedTeams = mutableListOf<AdminRegistrationTeam>()
            val rejectedTeams = mutableListOf<AdminRegistrationTeam>()

            inscricoes.forEach { inscricao ->
                val idEquipa = inscricao.longValue("id_equipa") ?: return@forEach

                val equipa = equipas.firstOrNull { equipa ->
                    equipa.longValue("id") == idEquipa
                } ?: return@forEach

                val idModalidade = equipa.intValue("id_modalidade")

                val modalidadeNome = modalidades
                    .firstOrNull { modalidade ->
                        modalidade.intValue("id") == idModalidade
                    }
                    ?.text("nome")
                    ?: "Modalidade"

                val team = AdminRegistrationTeam(
                    registrationId = inscricao.text("id"),
                    teamId = idEquipa.toString(),
                    teamName = equipa.text("nome").ifBlank { "Equipa sem nome" },
                    captainName = equipa.nestedText("dados_equipa", "capitao", "captain", "sigla")
                        .ifBlank { "Captain" },
                    division = modalidadeNome,
                    playersCount = equipa.intValue(
                        "players_count",
                        "num_jogadores",
                        "numero_jogadores"
                    ) ?: 0,
                    maxPlayers = equipa.intValue(
                        "max_players",
                        "max_jogadores"
                    ) ?: 11,
                    winRate = equipa.text("win_rate", "taxa_vitorias").ifBlank { "0%" },
                    appliedAgo = formatTimeAgo(inscricao.text("created_at")),
                    paymentStatus = inscricao.text("pagamento_estado").ifBlank { "pendente" }
                )

                when (inscricao.text("estado").lowercase()) {
                    "pendente" -> pendingTeams.add(team)
                    "aprovada" -> approvedTeams.add(team)
                    "rejeitada" -> rejectedTeams.add(team)
                }
            }

            AdminManageRegistrationData(
                tournamentId = tournamentId,
                tournamentName = torneio.text("nome").ifBlank { "Tournament" },
                status = torneio.text("estado").ifBlank { "aberto" },
                registeredTeams = approvedTeams.size,
                maxTeams = torneio.intValue("max_equipas", "limite_equipas") ?: 16,
                registrationClosesText = "Registration closes soon",
                pendingTeams = pendingTeams,
                approvedTeams = approvedTeams,
                rejectedTeams = rejectedTeams
            )
        }
    }

    suspend fun aprovarInscricao(registrationId: String): Result<Unit> {
        return runCatching {
            val id = registrationId.toLongOrNull()
                ?: throw Exception("ID da inscrição inválido.")

            val dados = buildJsonObject {
                put("estado", "aprovada")
                put("pagamento_estado", "pago")
            }

            client.from("torneio_equipa")
                .update(dados) {
                    filter {
                        eq("id", id)
                    }
                }
        }
    }

    suspend fun rejeitarInscricao(registrationId: String): Result<Unit> {
        return runCatching {
            val id = registrationId.toLongOrNull()
                ?: throw Exception("ID da inscrição inválido.")

            val dados = buildJsonObject {
                put("estado", "rejeitada")
            }

            client.from("torneio_equipa")
                .update(dados) {
                    filter {
                        eq("id", id)
                    }
                }
        }
    }

    suspend fun removerInscricao(registrationId: String): Result<Unit> {
        return runCatching {
            val id = registrationId.toLongOrNull()
                ?: throw Exception("ID da inscrição inválido.")

            client.from("torneio_equipa")
                .delete {
                    filter {
                        eq("id", id)
                    }
                }
        }
    }

    private fun formatTimeAgo(createdAt: String): String {
        return try {
            val created = OffsetDateTime.parse(createdAt)
            val now = OffsetDateTime.now()

            val hours = ChronoUnit.HOURS.between(created, now)
            val days = ChronoUnit.DAYS.between(created, now)

            when {
                hours < 1 -> "just now"
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

            val fromNumber = primitive
                ?.contentOrNull
                ?.toLongOrNull()

            if (fromNumber != null) {
                return fromNumber
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