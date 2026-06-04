package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.AdminNotification
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
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
                        timeText = formatTime(notificacao.criadaEm),
                        createdAt = notificacao.criadaEm,
                        userId = notificacao.idUtilizador,
                        tournamentId = notificacao.idTorneio
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
    val criadaEm: String,

    @SerialName("id_utilizador")
    val idUtilizador: String? = null,

    @SerialName("id_torneio")
    val idTorneio: String? = null
)

@Serializable
private data class AtualizarNotificacaoDto(
    val lida: Boolean
)