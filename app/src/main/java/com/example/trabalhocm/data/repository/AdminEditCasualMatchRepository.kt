package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.AdminEditCasualMatch
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import java.time.OffsetDateTime

class AdminEditCasualMatchRepository {

    private val client = SupabaseClient.client

    suspend fun obterPeladinha(matchId: String): Result<AdminEditCasualMatch> {
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

            AdminEditCasualMatch(
                id = matchId,
                title = peladinha.text("descricao").ifBlank { "Casual Match" },
                modalidade = modalidadeNome,
                date = peladinha.text("data"),
                time = peladinha.text("hora").take(5),
                local = peladinha.text("local").ifBlank { "Location not defined" },
                estado = peladinha.text("estado").ifBlank { "aberta" }
            )
        }
    }

    suspend fun atualizarPeladinha(
        matchId: String,
        title: String,
        date: String,
        time: String,
        local: String
    ): Result<Unit> {
        return runCatching {
            val idPeladinha = matchId.toLongOrNull()
                ?: throw Exception("ID da peladinha inválido.")

            val timeToSave = if (time.length == 5) {
                "$time:00"
            } else {
                time
            }

            client.from("peladinha")
                .update(
                    mapOf(
                        "descricao" to title,
                        "data" to date,
                        "hora" to timeToSave,
                        "local" to local,
                        "updated_at" to OffsetDateTime.now().toString()
                    )
                ) {
                    filter {
                        eq("id", idPeladinha)
                    }
                }
        }
    }

    suspend fun cancelarPeladinha(matchId: String): Result<Unit> {
        return runCatching {
            val idPeladinha = matchId.toLongOrNull()
                ?: throw Exception("ID da peladinha inválido.")

            client.from("peladinha")
                .update(
                    mapOf(
                        "estado" to "cancelada",
                        "updated_at" to OffsetDateTime.now().toString()
                    )
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
}