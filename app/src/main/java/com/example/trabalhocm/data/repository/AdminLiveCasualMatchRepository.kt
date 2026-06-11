package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.AdminLiveCasualMatch
import com.example.trabalhocm.data.model.AdminLiveCasualPlayer
import com.example.trabalhocm.data.model.AdminLiveCasualPoint
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class AdminLiveCasualMatchRepository {

    private val client = SupabaseClient.client

    suspend fun obterLiveMatch(matchId: String): Result<AdminLiveCasualMatch> {
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

            val homeTeamName = peladinha.text("equipa_casa_nome").ifBlank { "Team A" }
            val awayTeamName = peladinha.text("equipa_fora_nome").ifBlank { "Team B" }

            val jogadoresPeladinha = client.from("peladinha_jogador")
                .select {
                    filter {
                        eq("id_peladinha", idPeladinha)
                        eq("estado", "aceite")
                    }
                }
                .decodeList<JsonObject>()

            val jogadores = carregarJogadoresDaPeladinha(
                membros = jogadoresPeladinha,
                homeTeamName = homeTeamName,
                awayTeamName = awayTeamName
            )

            val pontosJson = client.from("peladinha_ponto")
                .select {
                    filter {
                        eq("id_peladinha", idPeladinha)
                    }
                }
                .decodeList<JsonObject>()

            val idsMarcadores = pontosJson
                .mapNotNull { ponto ->
                    ponto.text("id_utilizador").takeIf { it.isNotBlank() }
                }
                .distinct()

            val jogadoresDosPontos = carregarJogadoresSimples(idsMarcadores)
            val jogadoresPorId = (jogadores + jogadoresDosPontos).associateBy { it.id }

            val points = pontosJson
                .map { ponto ->
                    val side = ponto.text("equipa_lado")
                    val scorerId = ponto.text("id_utilizador")
                    val scorer = jogadoresPorId[scorerId]

                    AdminLiveCasualPoint(
                        id = ponto.text("id"),
                        minute = ponto.intValue("minuto") ?: 0,
                        scorerName = scorer?.nome ?: "Unknown player",
                        scorerInitials = scorer?.initials ?: "?",
                        teamSide = side,
                        teamName = if (side == "casa") homeTeamName else awayTeamName
                    )
                }
                .sortedByDescending { it.minute }

            val homeScore = points.count { it.teamSide == "casa" }
            val awayScore = points.count { it.teamSide == "fora" }

            AdminLiveCasualMatch(
                id = matchId,
                title = peladinha.text("descricao").ifBlank { "Casual Match" },
                modalidade = modalidadeNome,
                homeTeamName = homeTeamName,
                awayTeamName = awayTeamName,
                homeScore = homeScore,
                awayScore = awayScore,
                currentMinute = calculateCurrentMinute(
                    date = peladinha.text("data"),
                    time = peladinha.text("hora")
                ),
                isCanceled = peladinha.text("estado").lowercase() == "cancelada",
                players = jogadores.sortedWith(
                    compareBy<AdminLiveCasualPlayer> { it.teamSide }
                        .thenBy { it.nome.lowercase() }
                ),
                points = points
            )
        }
    }

    suspend fun adicionarPonto(
        matchId: String,
        playerId: String,
        teamSide: String,
        minute: Int
    ): Result<Unit> {
        return runCatching {
            val idPeladinha = matchId.toLongOrNull()
                ?: throw Exception("ID da peladinha inválido.")

            val body = buildJsonObject {
                put("id_peladinha", idPeladinha)
                put("id_utilizador", playerId)
                put("equipa_lado", teamSide)
                put("minuto", minute.coerceIn(0, 90))
            }

            client.from("peladinha_ponto")
                .insert(body)
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

    private suspend fun carregarJogadoresDaPeladinha(
        membros: List<JsonObject>,
        homeTeamName: String,
        awayTeamName: String
    ): List<AdminLiveCasualPlayer> {
        val ids = membros
            .mapNotNull { membro ->
                membro.text("id_utilizador").takeIf { it.isNotBlank() }
            }
            .distinct()

        if (ids.isEmpty()) {
            return emptyList()
        }

        val utilizadores = client.from("utilizador")
            .select {
                filter {
                    isIn("id", ids)
                }
            }
            .decodeList<JsonObject>()

        val membrosPorUtilizador = membros.associateBy {
            it.text("id_utilizador")
        }

        return utilizadores.map { user ->
            val nome = user.text("nome").ifBlank { "Player" }
            val userId = user.text("id")

            val side = membrosPorUtilizador[userId]
                ?.text("equipa_lado")
                ?.ifBlank { "casa" }
                ?: "casa"

            AdminLiveCasualPlayer(
                id = userId,
                nome = nome,
                email = user.text("email"),
                initials = initials(nome),
                teamSide = side,
                teamName = if (side == "casa") homeTeamName else awayTeamName
            )
        }
    }

    private suspend fun carregarJogadoresSimples(ids: List<String>): List<AdminLiveCasualPlayer> {
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

                AdminLiveCasualPlayer(
                    id = user.text("id"),
                    nome = nome,
                    email = user.text("email"),
                    initials = initials(nome),
                    teamSide = "",
                    teamName = ""
                )
            }
    }

    private fun calculateCurrentMinute(
        date: String,
        time: String
    ): Int {
        return try {
            val parsedDate = LocalDate.parse(date.take(10))
            val parsedTime = LocalTime.parse(time.take(8))
            val start = LocalDateTime.of(parsedDate, parsedTime)
            val now = LocalDateTime.now()

            ChronoUnit.MINUTES.between(start, now)
                .toInt()
                .coerceIn(0, 90)
        } catch (e: Exception) {
            0
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