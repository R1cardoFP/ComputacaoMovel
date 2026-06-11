package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.AdminCasualMatch
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class AdminCasualMatchRepository {

    private val client = SupabaseClient.client
    private val lisbonZone = ZoneId.of("Europe/Lisbon")

    suspend fun listarPeladinhasAdmin(): Result<List<AdminCasualMatch>> {
        return runCatching {
            val peladinhas = client.from("peladinha")
                .select()
                .decodeList<JsonObject>()

            val modalidades = client.from("modalidade")
                .select()
                .decodeList<JsonObject>()

            val jogadoresPeladinha = client.from("peladinha_jogador")
                .select {
                    filter {
                        eq("estado", "aceite")
                    }
                }
                .decodeList<JsonObject>()

            val jogadoresPorPeladinha = jogadoresPeladinha
                .mapNotNull { jogador ->
                    jogador.text("id_peladinha").toLongOrNull()
                }
                .groupingBy { it }
                .eachCount()

            val pontosPeladinha = client.from("peladinha_ponto")
                .select()
                .decodeList<JsonObject>()

            val pontosPorPeladinha = pontosPeladinha.groupBy { ponto ->
                ponto.text("id_peladinha").toLongOrNull() ?: 0L
            }

            peladinhas.map { peladinha ->
                val id = peladinha.text("id")
                val idLong = id.toLongOrNull() ?: 0L

                val idModalidade = peladinha.intValue("id_modalidade")

                val modalidadeNome = modalidades
                    .firstOrNull { modalidade ->
                        modalidade.intValue("id") == idModalidade
                    }
                    ?.text("nome")
                    ?: "Modalidade"

                val dateText = peladinha.text("data")
                val timeText = peladinha.text("hora")

                val parsedDate = parseDate(dateText)
                val parsedTime = parseTime(timeText)

                val estado = peladinha.text("estado").lowercase().ifBlank { "aberta" }
                val maxPlayers = peladinha.intValue("max_jogadores") ?: 0
                val acceptedPlayers = jogadoresPorPeladinha[idLong] ?: 0

                val isLive = isLiveNow(
                    date = parsedDate,
                    time = parsedTime,
                    estado = estado
                )

                val status = buildStatus(
                    estado = estado,
                    isLive = isLive
                )

                val pontos = pontosPorPeladinha[idLong].orEmpty()
                val homeScore = pontos.count { it.text("equipa_lado") == "casa" }
                val awayScore = pontos.count { it.text("equipa_lado") == "fora" }

                AdminCasualMatch(
                    id = id,
                    title = peladinha.text("descricao").ifBlank { "Casual Match" },
                    modalidade = modalidadeNome,
                    local = peladinha.text("local").ifBlank { "Location not defined" },
                    date = dateText,
                    time = timeText.take(5),
                    status = status,
                    statusFilter = status,
                    acceptedPlayers = acceptedPlayers,
                    maxPlayers = maxPlayers,
                    sectionTitle = if (status == "CANCELED") {
                        "CANCELED MATCHES"
                    } else {
                        buildSectionTitle(parsedDate, isLive)
                    },
                    isLive = isLive,
                    homeTeamName = peladinha.text("equipa_casa_nome").ifBlank { "Team A" },
                    awayTeamName = peladinha.text("equipa_fora_nome").ifBlank { "Team B" },
                    homeScore = homeScore,
                    awayScore = awayScore
                )
            }.sortedWith(
                compareBy<AdminCasualMatch> { statusPriority(it.status) }
                    .thenBy { it.date }
                    .thenBy { it.time }
            )
        }
    }

    private fun buildStatus(
        estado: String,
        isLive: Boolean
    ): String {
        if (estado == "cancelada") {
            return "CANCELED"
        }

        if (isLive) {
            return "LIVE"
        }

        return when (estado) {
            "aberta" -> "OPEN"
            "fechada" -> "CLOSED"
            else -> estado.uppercase().ifBlank { "OPEN" }
        }
    }

    private fun buildSectionTitle(
        date: LocalDate?,
        isLive: Boolean
    ): String {
        val today = LocalDate.now(lisbonZone)

        if (isLive) {
            return "LIVE NOW"
        }

        return when (date) {
            today -> "UPCOMING · TODAY"
            today.plusDays(1) -> "UPCOMING · TOMORROW"
            null -> "UPCOMING"
            else -> {
                if (date.isBefore(today)) {
                    "PAST MATCHES"
                } else {
                    "UPCOMING · ${date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}"
                }
            }
        }
    }

    private fun isLiveNow(
        date: LocalDate?,
        time: LocalTime?,
        estado: String
    ): Boolean {
        if (date == null || time == null) {
            return false
        }

        if (estado == "cancelada") {
            return false
        }

        return try {
            val start = LocalDateTime.of(date, time)
            val now = LocalDateTime.now(lisbonZone)
            val minutes = ChronoUnit.MINUTES.between(start, now)

            minutes in 0..90
        } catch (e: Exception) {
            false
        }
    }

    private fun statusPriority(status: String): Int {
        return when (status) {
            "LIVE" -> 0
            "OPEN" -> 1
            "CLOSED" -> 2
            "CANCELED" -> 3
            else -> 4
        }
    }

    private fun parseDate(value: String): LocalDate? {
        return try {
            LocalDate.parse(value.take(10))
        } catch (e: Exception) {
            null
        }
    }

    private fun parseTime(value: String): LocalTime? {
        return try {
            val cleanValue = value.take(8)

            if (cleanValue.length >= 8) {
                LocalTime.parse(cleanValue)
            } else {
                LocalTime.parse(cleanValue.take(5))
            }
        } catch (e: Exception) {
            null
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
}