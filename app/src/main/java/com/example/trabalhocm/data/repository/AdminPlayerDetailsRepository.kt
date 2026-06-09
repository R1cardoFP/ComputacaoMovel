package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.AdminPlayerDetails
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import java.time.OffsetDateTime

class AdminPlayerDetailsRepository {

    private val client = SupabaseClient.client

    suspend fun obterDetalhesJogador(
        playerId: String,
        teamId: String? = null
    ): Result<AdminPlayerDetails> {
        return runCatching {
            val user = client.from("utilizador")
                .select {
                    filter {
                        eq("id", playerId)
                    }
                }
                .decodeSingle<JsonObject>()

            val utilizadorPapeis = client.from("utilizador_papel")
                .select {
                    filter {
                        eq("id_utilizador", playerId)
                    }
                }
                .decodeList<JsonObject>()

            val papeis = client.from("papel")
                .select()
                .decodeList<JsonObject>()

            val membrosEquipa = client.from("membro_equipa")
                .select {
                    filter {
                        eq("id_utilizador", playerId)
                        eq("estado_convite", "aceite")
                    }
                }
                .decodeList<JsonObject>()

            val idsEquipas = membrosEquipa
                .mapNotNull { membro ->
                    membro.text("id_equipa").toLongOrNull()
                }
                .distinct()

            val equipasJson = if (idsEquipas.isNotEmpty()) {
                client.from("equipa")
                    .select {
                        filter {
                            isIn("id", idsEquipas)
                        }
                    }
                    .decodeList<JsonObject>()
            } else {
                emptyList()
            }

            val equipasNomes = equipasJson
                .mapNotNull { equipa ->
                    equipa.text("nome").takeIf { it.isNotBlank() }
                }

            val membroEquipaContexto = if (!teamId.isNullOrBlank()) {
                membrosEquipa.firstOrNull { membro ->
                    membro.text("id_equipa") == teamId
                }
            } else {
                membrosEquipa.firstOrNull()
            }

            val equipaId = membroEquipaContexto
                ?.text("id_equipa")
                ?: idsEquipas.firstOrNull()?.toString().orEmpty()

            val equipaNome = if (equipaId.isNotBlank()) {
                equipasJson
                    .firstOrNull { equipa ->
                        equipa.text("id") == equipaId
                    }
                    ?.text("nome")
                    ?.ifBlank { null }
                    ?: "No team"
            } else {
                "No team"
            }

            val idPapel = utilizadorPapeis
                .firstOrNull()
                ?.text("id_papel")
                ?: ""

            val papelNome = papeis
                .firstOrNull { papel ->
                    papel.text("id") == idPapel
                }
                ?.text("nome_papel")
                ?: "Player"

            val nome = user.text("nome").ifBlank { "User" }
            val email = user.text("email")

            AdminPlayerDetails(
                id = playerId,
                nome = nome,
                email = email,
                papel = papelNome,
                equipa = equipaNome,
                equipaId = equipaId,
                equipas = equipasNomes,
                initials = initials(nome),
                age = user.nestedText("dados_pessoais", "age").ifBlank { "-" },
                height = user.nestedText("dados_pessoais", "height").ifBlank { "-" },
                number = user.nestedText("dados_pessoais", "number").ifBlank { "-" },
                position = user.nestedText("dados_pessoais", "position").ifBlank { papelNome },
                goals = user.nestedInt("dados_pessoais", "goals") ?: 0,
                assists = user.nestedInt("dados_pessoais", "assists") ?: 0,
                memberSince = user.nestedText("dados_pessoais", "member_since").ifBlank { "-" },
                lastActive = user.nestedText("dados_pessoais", "last_active").ifBlank { "-" },
                twoFactorEnabled = user.nestedBoolean("dados_pessoais", "two_factor_enabled") ?: false,
                accountStatus = user.nestedText("dados_pessoais", "account_status").ifBlank { "active" },
                suspended = user.nestedBoolean("dados_pessoais", "suspended") ?: false,
                deleted = user.nestedBoolean("dados_pessoais", "deleted") ?: false
            )
        }
    }

    suspend fun suspenderUtilizador(playerId: String): Result<Unit> {
        return runCatching {
            atualizarEstadoUtilizador(
                playerId = playerId,
                accountStatus = "suspended",
                suspended = true,
                deleted = false
            )
        }
    }

    suspend fun reativarUtilizador(playerId: String): Result<Unit> {
        return runCatching {
            atualizarEstadoUtilizador(
                playerId = playerId,
                accountStatus = "active",
                suspended = false,
                deleted = false
            )
        }
    }

    private suspend fun atualizarEstadoUtilizador(
        playerId: String,
        accountStatus: String,
        suspended: Boolean,
        deleted: Boolean
    ) {
        val user = client.from("utilizador")
            .select {
                filter {
                    eq("id", playerId)
                }
            }
            .decodeSingle<JsonObject>()

        val dadosAtuais = user["dados_pessoais"]?.jsonObject ?: buildJsonObject {}

        val dadosAtualizados = buildJsonObject {
            dadosAtuais.forEach { entry ->
                put(entry.key, entry.value)
            }

            put("account_status", accountStatus)
            put("suspended", suspended)
            put("deleted", deleted)

            if (suspended) {
                put("suspended_at", OffsetDateTime.now().toString())
            } else {
                put("reactivated_at", OffsetDateTime.now().toString())
            }
        }

        val body = buildJsonObject {
            put("dados_pessoais", dadosAtualizados)
        }

        client.from("utilizador")
            .update(body) {
                filter {
                    eq("id", playerId)
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

    private fun JsonObject.nestedInt(objectKey: String, vararg keys: String): Int? {
        val obj = this[objectKey]?.jsonObject ?: return null

        keys.forEach { key ->
            val primitive = obj[key]?.jsonPrimitive

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

    private fun JsonObject.nestedBoolean(objectKey: String, vararg keys: String): Boolean? {
        val obj = this[objectKey]?.jsonObject ?: return null

        keys.forEach { key ->
            val primitive = obj[key]?.jsonPrimitive

            val direct = primitive?.booleanOrNull
            if (direct != null) {
                return direct
            }

            val fromText = primitive
                ?.contentOrNull
                ?.toBooleanStrictOrNull()

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