package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.OrganizerRequest
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

class AdminOrganizerRequestRepository {

    private val client = SupabaseClient.client
    private val zonaPortugal = ZoneId.of("Europe/Lisbon")

    suspend fun listarPedidos(): Result<List<OrganizerRequest>> {
        return runCatching {
            val pedidos = client.from("pedido_organizador")
                .select()
                .decodeList<PedidoOrganizadorDto>()

            pedidos
                .sortedByDescending { it.criadaEm }
                .map { pedido ->
                    val utilizador = carregarUtilizador(pedido.idUtilizador)

                    OrganizerRequest(
                        id = pedido.id,
                        userId = pedido.idUtilizador,
                        name = utilizador?.nome ?: "Utilizador",
                        username = utilizador?.username ?: "",
                        email = utilizador?.email ?: "",
                        sport = pedido.modalidade ?: "Sem modalidade",
                        frequency = pedido.frequencia ?: "Não definido",
                        experience = pedido.experiencia ?: "Não definido",
                        applied = formatAppliedTime(pedido.criadaEm),
                        description = pedido.motivo ?: "Sem descrição.",
                        status = pedido.estado,
                        createdAt = pedido.criadaEm,
                        updatedAt = pedido.atualizadaEm
                    )
                }
        }
    }

    suspend fun aprovarPedido(idPedido: String, idUtilizador: String): Result<Unit> {
        return runCatching {
            atualizarEstadoPedido(
                idPedido = idPedido,
                estado = "APPROVED"
            )

            garantirPapelOrganizador(idUtilizador)
        }
    }

    suspend fun rejeitarPedido(idPedido: String): Result<Unit> {
        return runCatching {
            atualizarEstadoPedido(
                idPedido = idPedido,
                estado = "REJECTED"
            )
        }
    }

    private suspend fun carregarUtilizador(idUtilizador: String): UtilizadorPedidoDto? {
        return client.from("utilizador")
            .select {
                filter {
                    eq("id", idUtilizador)
                }
            }
            .decodeList<UtilizadorPedidoDto>()
            .firstOrNull()
    }

    private suspend fun atualizarEstadoPedido(
        idPedido: String,
        estado: String
    ) {
        client.from("pedido_organizador")
            .update(
                AtualizarPedidoOrganizadorDto(
                    estado = estado,
                    atualizadaEm = OffsetDateTime.now(zonaPortugal).toString()
                )
            ) {
                filter {
                    eq("id", idPedido)
                }
            }
    }

    private suspend fun garantirPapelOrganizador(idUtilizador: String) {
        val papeisExistentes = client.from("utilizador_papel")
            .select {
                filter {
                    eq("id_utilizador", idUtilizador)
                    eq("id_papel", ID_PAPEL_ORGANIZADOR)
                }
            }
            .decodeList<UtilizadorPapelSelectDto>()

        if (papeisExistentes.isEmpty()) {
            client.from("utilizador_papel")
                .insert(
                    UtilizadorPapelInsertDto(
                        idUtilizador = idUtilizador,
                        idPapel = ID_PAPEL_ORGANIZADOR
                    )
                )
        }
    }

    private fun formatAppliedTime(data: String): String {
        return try {
            val dataHora = parseDataSupabase(data)
            val agora = OffsetDateTime.now(zonaPortugal)
            val duration = Duration.between(dataHora, agora)

            when {
                duration.toMinutes() < 1 -> "now"
                duration.toMinutes() < 60 -> "${duration.toMinutes()} min ago"
                duration.toHours() < 24 -> "${duration.toHours()}h ago"
                duration.toDays() == 1L -> "1 day ago"
                else -> "${duration.toDays()} days ago"
            }
        } catch (e: Exception) {
            "unknown"
        }
    }

    private fun parseDataSupabase(data: String): OffsetDateTime {
        return try {
            OffsetDateTime
                .parse(data)
                .atZoneSameInstant(zonaPortugal)
                .toOffsetDateTime()
        } catch (e1: Exception) {
            try {
                Instant
                    .parse(data)
                    .atZone(zonaPortugal)
                    .toOffsetDateTime()
            } catch (e2: Exception) {
                LocalDateTime
                    .parse(data)
                    .atZone(zonaPortugal)
                    .toOffsetDateTime()
            }
        }
    }

    companion object {
        private const val ID_PAPEL_ORGANIZADOR = 2
    }
}

@Serializable
private data class PedidoOrganizadorDto(
    val id: String,

    @SerialName("id_utilizador")
    val idUtilizador: String,

    val modalidade: String? = null,
    val frequencia: String? = null,
    val experiencia: String? = null,
    val motivo: String? = null,
    val estado: String = "PENDING",

    @SerialName("criada_em")
    val criadaEm: String,

    @SerialName("atualizada_em")
    val atualizadaEm: String
)

@Serializable
private data class UtilizadorPedidoDto(
    val id: String,
    val username: String = "",
    val nome: String = "",
    val email: String = ""
)

@Serializable
private data class AtualizarPedidoOrganizadorDto(
    val estado: String,

    @SerialName("atualizada_em")
    val atualizadaEm: String
)

@Serializable
private data class UtilizadorPapelSelectDto(
    @SerialName("id_utilizador")
    val idUtilizador: String,

    @SerialName("id_papel")
    val idPapel: Int
)

@Serializable
private data class UtilizadorPapelInsertDto(
    @SerialName("id_utilizador")
    val idUtilizador: String,

    @SerialName("id_papel")
    val idPapel: Int
)