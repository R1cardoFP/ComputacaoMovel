package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.AdminTournament
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class AdminTournamentRepository {

    private val client = SupabaseClient.client

    suspend fun listarTorneiosAdmin(): Result<List<AdminTournament>> {
        return runCatching {
            val torneios = client.from("torneio")
                .select()
                .decodeList<TorneioArquivoAdminDto>()

            val modalidades = client.from("modalidade")
                .select()
                .decodeList<ModalidadeArquivoAdminDto>()

            val organizadores = client.from("utilizador")
                .select()
                .decodeList<OrganizadorArquivoAdminDto>()

            torneios.map { torneio ->
                val modalidadeNome = modalidades
                    .firstOrNull { it.id == torneio.idModalidade }
                    ?.nome
                    ?: "Unknown"

                val organizadorNome = organizadores
                    .firstOrNull { it.id == torneio.idOrganizador }
                    ?.nome
                    ?: "Unknown organizer"

                AdminTournament(
                    id = torneio.id.toString(),
                    nome = torneio.nome,
                    organizerName = organizadorNome,
                    modalidade = modalidadeNome.uppercase(),
                    matchesCount = 0,
                    champion = "Por definir",
                    prize = formatPrize(torneio.premio),
                    season = formatSeason(torneio.dataInicio, torneio.dataFim),
                    estado = torneio.estado ?: "ARCHIVED"
                )
            }.sortedBy { it.nome.lowercase() }
        }
    }

    private fun formatPrize(valor: Double?): String {
        if (valor == null || valor <= 0.0) {
            return "€0"
        }

        return if (valor >= 1000) {
            "€${(valor / 1000).toInt()}k"
        } else {
            "€${valor.toInt()}"
        }
    }

    private fun formatSeason(dataInicio: String?, dataFim: String?): String {
        val anoInicio = dataInicio?.take(4)?.toIntOrNull()
        val anoFim = dataFim?.take(4)?.toIntOrNull()

        return when {
            anoInicio != null && anoFim != null -> {
                "${anoInicio.toString().takeLast(2)}/${anoFim.toString().takeLast(2)}"
            }

            anoInicio != null -> {
                val anoSeguinte = anoInicio + 1
                "${anoInicio.toString().takeLast(2)}/${anoSeguinte.toString().takeLast(2)}"
            }

            else -> {
                "--/--"
            }
        }
    }
}

@Serializable
private data class TorneioArquivoAdminDto(
    val id: Int,
    val nome: String,

    @SerialName("id_organizador")
    val idOrganizador: String? = null,

    @SerialName("id_modalidade")
    val idModalidade: Int? = null,

    @SerialName("data_inicio")
    val dataInicio: String? = null,

    @SerialName("data_fim")
    val dataFim: String? = null,

    val premio: Double? = null,
    val estado: String? = null
)

@Serializable
private data class ModalidadeArquivoAdminDto(
    val id: Int,
    val nome: String
)

@Serializable
private data class OrganizadorArquivoAdminDto(
    val id: String,
    val nome: String
)