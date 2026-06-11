package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.Torneio
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class TorneioRepository {

    private val client = SupabaseClient.client

    suspend fun listarTorneios(): Result<List<Torneio>> {
        return runCatching {
            client.from("torneio")
                .select()
                .decodeList<Torneio>()
        }
    }

    suspend fun criarTorneio(torneio: Torneio): Result<Unit> {
        return runCatching {
            val userId = client.auth.currentUserOrNull()?.id
                ?: throw Exception("Utilizador não autenticado.")

            val novoTorneio = CriarTorneioRequest(
                nome = torneio.nome,
                descricao = torneio.descricao,
                regras = torneio.regras,
                local = torneio.local,
                dataInicio = torneio.dataInicio,
                dataFim = torneio.dataFim,
                formato = torneio.formato,
                taxaInscricao = torneio.taxaInscricao,
                premio = torneio.premio,
                estado = "aberto",
                idOrganizador = userId,
                idModalidade = torneio.idModalidade
            )

            client.from("torneio")
                .insert(novoTorneio)
        }
    }
}

@Serializable
private data class CriarTorneioRequest(
    val nome: String,
    val descricao: String? = null,
    val regras: String? = null,
    val local: String? = null,

    @SerialName("data_inicio")
    val dataInicio: String,

    @SerialName("data_fim")
    val dataFim: String? = null,

    val formato: String,

    @SerialName("taxa_inscricao")
    val taxaInscricao: Double = 0.0,

    val premio: Double = 0.0,
    val estado: String = "aberto",

    @SerialName("id_organizador")
    val idOrganizador: String,

    @SerialName("id_modalidade")
    val idModalidade: Long
)