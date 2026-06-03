package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.AdminNotification
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.OffsetDateTime

class AdminNotificationRepository {

    private val client = SupabaseClient.client

    suspend fun listarNotificacoes(): Result<List<AdminNotification>> {
        return runCatching {
            val notificacoes = client.from("notificacao_admin")
                .select()
                .decodeList<AdminNotificationDto>()

            notificacoes
                .sortedByDescending { it.criadaEm }
                .map { notificacao ->
                    AdminNotification(
                        id = notificacao.id,
                        title = notificacao.titulo,
                        description = notificacao.descricao,
                        type = notificacao.tipo,
                        actionText = notificacao.acaoTexto,
                        unread = !notificacao.lida,
                        timeText = formatTime(notificacao.criadaEm)
                    )
                }
        }
    }

    suspend fun marcarComoLida(id: String): Result<Unit> {
        return runCatching {
            client.from("notificacao_admin")
                .update(
                    AtualizarNotificacaoDto(
                        lida = true
                    )
                ) {
                    filter {
                        eq("id", id)
                    }
                }
        }
    }

    private fun formatTime(data: String): String {
        return try {
            val criadaEm = OffsetDateTime.parse(data)
            val agora = OffsetDateTime.now()
            val duration = Duration.between(criadaEm, agora)

            when {
                duration.toMinutes() < 1 -> "NOW"
                duration.toMinutes() < 60 -> "${duration.toMinutes()}M AGO"
                duration.toHours() < 24 -> "${duration.toHours()}H AGO"
                duration.toDays() == 1L -> "YESTERDAY"
                else -> "${duration.toDays()}D AGO"
            }
        } catch (e: Exception) {
            "UNKNOWN"
        }
    }
}

@Serializable
private data class AdminNotificationDto(
    val id: String,
    val titulo: String,
    val descricao: String,
    val tipo: String,

    @SerialName("acao_texto")
    val acaoTexto: String? = null,

    val lida: Boolean = false,

    @SerialName("criada_em")
    val criadaEm: String
)

@Serializable
private data class AtualizarNotificacaoDto(
    val lida: Boolean
)