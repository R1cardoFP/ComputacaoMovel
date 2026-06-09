package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.auth.auth
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.OffsetDateTime
import java.time.ZoneId

class PlayerOrganizerRepository {
    private val client = SupabaseClient.client
    private val zonaPortugal = ZoneId.of("Europe/Lisbon")

    suspend fun submeterPedido(
        modalidade: String,
        experiencia: String,
        frequencia: String,
        motivo: String
    ): Result<Unit> {
        return runCatching {
            // 1. Obter o ID do utilizador com sessão iniciada
            val userId = client.auth.currentUserOrNull()?.id
                ?: throw Exception("Utilizador não autenticado.")

            // 2. Preparar os dados para envio (sem o id do pedido, pois a base de dados gera-o)
            val novoPedido = InserirPedidoOrganizadorDto(
                idUtilizador = userId,
                modalidade = modalidade,
                experiencia = experiencia,
                frequencia = frequencia,
                motivo = motivo,
                estado = "PENDING",
                criadaEm = OffsetDateTime.now(zonaPortugal).toString(),
                atualizadaEm = OffsetDateTime.now(zonaPortugal).toString()
            )

            // 3. Fazer o insert na base de dados
            client.from("pedido_organizador").insert(novoPedido)
        }
    }
}

// DTO exclusivo para inserção
@Serializable
data class InserirPedidoOrganizadorDto(
    @SerialName("id_utilizador") val idUtilizador: String,
    val modalidade: String,
    val experiencia: String,
    val frequencia: String,
    val motivo: String,
    val estado: String = "PENDING",
    @SerialName("criada_em") val criadaEm: String,
    @SerialName("atualizada_em") val atualizadaEm: String
)