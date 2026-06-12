package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.AdminNotification
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

class AdminNotificationRepository {

    private val client = SupabaseClient.client
    private val zonaPortugal = ZoneId.of("Europe/Lisbon")

    suspend fun listarNotificacoes(): Result<List<AdminNotification>> {
        return runCatching {
            val notificacoes = client.from("notificacao_admin")
                .select()
                .decodeList<JsonObject>()

            val utilizadores = runCatching {
                client.from("utilizador")
                    .select()
                    .decodeList<JsonObject>()
            }.getOrDefault(emptyList())

            val torneios = runCatching {
                client.from("torneio")
                    .select()
                    .decodeList<JsonObject>()
            }.getOrDefault(emptyList())

            notificacoes
                .sortedByDescending { it.text("criada_em", "created_at") }
                .map { notificacao ->
                    val titulo = notificacao.text("titulo", "title").ifBlank { "Notification" }
                    val descricao = notificacao.text("descricao", "description")
                    val acaoTexto = notificacao.text("acao_texto", "action_text").takeIf { it.isNotBlank() }
                    val criadaEm = notificacao.text("criada_em", "created_at")

                    val idUtilizadorDireto = notificacao.text("id_utilizador", "user_id").takeIf { it.isNotBlank() }
                    val idTorneioDireto = notificacao.text("id_torneio", "tournament_id").takeIf { it.isNotBlank() }

                    val idUtilizadorFinal = idUtilizadorDireto
                        ?: inferirUtilizador(titulo, descricao, utilizadores)

                    val idTorneioFinal = idTorneioDireto
                        ?: inferirTorneio(titulo, descricao, torneios)

                    AdminNotification(
                        id = notificacao.text("id"),
                        title = titulo,
                        description = descricao,
                        type = notificacao.text("tipo", "type").ifBlank { "SYSTEM" },
                        actionText = acaoTexto,
                        unread = !(notificacao.booleanValue("lida", "read") ?: false),
                        timeText = formatTime(criadaEm),
                        createdAt = criadaEm,
                        userId = idUtilizadorFinal,
                        tournamentId = idTorneioFinal
                    )
                }
        }
    }

    suspend fun marcarComoLida(id: String): Result<Unit> {
        return runCatching {
            client.from("notificacao_admin")
                .update(
                    mapOf("lida" to true)
                ) {
                    filter {
                        eq("id", id)
                    }
                }
        }
    }

    private fun inferirUtilizador(
        titulo: String,
        descricao: String,
        utilizadores: List<JsonObject>
    ): String? {
        val texto = "$titulo $descricao".lowercase()

        return utilizadores.firstOrNull { utilizador ->
            val nome = utilizador.text("nome", "username").lowercase()
            val email = utilizador.text("email").lowercase()
            val primeiroNome = nome.split(" ").firstOrNull().orEmpty()

            (nome.isNotBlank() && texto.contains(nome)) ||
                    (primeiroNome.length >= 3 && texto.contains(primeiroNome)) ||
                    (email.isNotBlank() && texto.contains(email))
        }?.text("id")
    }

    private fun inferirTorneio(
        titulo: String,
        descricao: String,
        torneios: List<JsonObject>
    ): String? {
        val texto = "$titulo $descricao".lowercase()

        return torneios.firstOrNull { torneio ->
            val nome = torneio.text("nome", "name").lowercase()
            nome.isNotBlank() && texto.contains(nome)
        }?.text("id")
    }

    private fun formatTime(data: String): String {
        return try {
            val dataHora = parseDataSupabase(data)
            val formatter = DateTimeFormatter.ofPattern("HH'h'", Locale("pt", "PT"))

            dataHora.format(formatter)
        } catch (e: Exception) {
            "UNKNOWN"
        }
    }

    private fun parseDataSupabase(data: String): LocalDateTime {
        return try {
            OffsetDateTime
                .parse(data)
                .atZoneSameInstant(zonaPortugal)
                .toLocalDateTime()
        } catch (e1: Exception) {
            try {
                Instant
                    .parse(data)
                    .atZone(zonaPortugal)
                    .toLocalDateTime()
            } catch (e2: Exception) {
                LocalDateTime.parse(data)
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

    private fun JsonObject.booleanValue(vararg keys: String): Boolean? {
        keys.forEach { key ->
            val primitive = this[key]?.jsonPrimitive

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
}
