package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.Modalidade
import com.example.trabalhocm.data.model.Peladinha
import com.example.trabalhocm.data.model.PeladinhaParticipante
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from

data class PeladinhaComInfo(
    val peladinha: Peladinha,
    val modalidadeNome: String,
    val jogadoresInscritos: Int
)

class PeladinhaRepository {

    private val client = SupabaseClient.client

    suspend fun listarPeladinhasComInfo(): Result<List<PeladinhaComInfo>> {
        return runCatching {
            val peladinhas = client.from("peladinha")
                .select()
                .decodeList<Peladinha>()

            val modalidades = client.from("modalidade")
                .select()
                .decodeList<Modalidade>()

            val participantes = runCatching {
                client.from("peladinha_participante")
                    .select()
                    .decodeList<PeladinhaParticipante>()
            }.getOrDefault(emptyList())

            val modalidadesPorId = modalidades.associateBy { it.id }

            peladinhas.map { peladinha ->
                val totalInscritos = participantes.count {
                    it.idPeladinha == peladinha.id &&
                            it.estadoParticipacao.lowercase() != "recusado"
                }

                PeladinhaComInfo(
                    peladinha = peladinha,
                    modalidadeNome = modalidadesPorId[peladinha.idModalidade]?.nome ?: "Modalidade",
                    jogadoresInscritos = totalInscritos
                )
            }
        }
    }
}