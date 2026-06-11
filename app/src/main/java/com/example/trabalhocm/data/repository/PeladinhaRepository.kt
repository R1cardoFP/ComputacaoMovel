package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.Modalidade
import com.example.trabalhocm.data.model.Peladinha
import com.example.trabalhocm.data.model.PeladinhaParticipante
import com.example.trabalhocm.data.model.Utilizador
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class PeladinhaComInfo(
    val peladinha: Peladinha,
    val modalidadeNome: String,
    val jogadoresInscritos: Int,
    val utilizadorJaInscrito: Boolean = false
)

data class PeladinhaDetalhesInfo(
    val peladinha: Peladinha,
    val modalidadeNome: String,
    val jogadoresInscritos: Int,
    val organizador: Utilizador?,
    val participantes: List<Utilizador>,
    val utilizadorAtualId: String?
)

class PeladinhaRepository {

    private val client = SupabaseClient.client
    private val authRepository = AuthRepository()

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

            val utilizadorAtualId = obterIdUtilizadorAtualOuPrimeiroTeste()

            peladinhas.map { peladinha ->
                val participantesValidos = participantes.filter {
                    it.idPeladinha == peladinha.id &&
                            it.estadoParticipacao.lowercase() != "recusado"
                }

                val totalInscritos = participantesValidos.size

                val utilizadorJaInscrito = participantesValidos.any {
                    it.idUtilizador == utilizadorAtualId
                }

                PeladinhaComInfo(
                    peladinha = peladinha,
                    modalidadeNome = modalidadesPorId[peladinha.idModalidade]?.nome ?: "Modalidade",
                    jogadoresInscritos = totalInscritos,
                    utilizadorJaInscrito = utilizadorJaInscrito
                )
            }
        }
    }

    suspend fun criarPeladinha(
        idModalidade: Long,
        data: String?,
        hora: String?,
        local: String?,
        maxJogadores: Int,
        nivel: String,
        tipoInscricao: String
    ): Result<Unit> {
        return runCatching {
            val idOrganizador = obterIdUtilizadorAtualOuPrimeiroTeste()

            if (data.isNullOrBlank()) {
                error("Indica a data da partida.")
            }

            if (hora.isNullOrBlank()) {
                error("Indica a hora da partida.")
            }

            if (local.isNullOrBlank()) {
                error("Indica o local da partida.")
            }

            if (maxJogadores <= 0) {
                error("Indica um número válido de jogadores.")
            }

            val descricaoGerada = "Nível: $nivel | Inscrição: $tipoInscricao"

            val novaPeladinha = CriarPeladinhaRequest(
                idModalidade = idModalidade,
                idOrganizador = idOrganizador,
                data = data,
                hora = hora,
                preco = 0.0,
                descricao = descricaoGerada,
                maxJogadores = maxJogadores,
                estado = "aberta",
                local = local
            )

            client.from("peladinha")
                .insert(novaPeladinha)
        }
    }

    suspend fun obterDetalhesPeladinha(idPeladinha: Long): Result<PeladinhaDetalhesInfo> {
        return runCatching {
            val peladinhas = client.from("peladinha")
                .select()
                .decodeList<Peladinha>()

            val peladinha = peladinhas.firstOrNull { it.id == idPeladinha }
                ?: error("Partida casual não encontrada.")

            val modalidades = client.from("modalidade")
                .select()
                .decodeList<Modalidade>()

            val utilizadores = client.from("utilizador")
                .select()
                .decodeList<Utilizador>()

            val participantes = runCatching {
                client.from("peladinha_participante")
                    .select()
                    .decodeList<PeladinhaParticipante>()
            }.getOrDefault(emptyList())

            val modalidadeNome = modalidades.firstOrNull {
                it.id == peladinha.idModalidade
            }?.nome ?: "Modalidade"

            val organizador = utilizadores.firstOrNull {
                it.id == peladinha.idOrganizador
            }

            val participantesDaPeladinha = participantes.filter {
                it.idPeladinha == peladinha.id &&
                        it.estadoParticipacao.lowercase() != "recusado"
            }

            val idsParticipantes = participantesDaPeladinha.map {
                it.idUtilizador
            }.toSet()

            val utilizadoresParticipantes = utilizadores.filter {
                it.id in idsParticipantes
            }

            val utilizadorAtualId = obterIdUtilizadorAtualOuPrimeiroTeste()

            PeladinhaDetalhesInfo(
                peladinha = peladinha,
                modalidadeNome = modalidadeNome,
                jogadoresInscritos = participantesDaPeladinha.size,
                organizador = organizador,
                participantes = utilizadoresParticipantes,
                utilizadorAtualId = utilizadorAtualId
            )
        }
    }

    suspend fun entrarNaPeladinha(idPeladinha: Long): Result<Unit> {
        return runCatching {
            val idUtilizador = obterIdUtilizadorAtualOuPrimeiroTeste()

            val detalhes = obterDetalhesPeladinha(idPeladinha).getOrThrow()
            val peladinha = detalhes.peladinha

            val estadoNormalizado = peladinha.estado.lowercase()

            val podeEntrar = estadoNormalizado in listOf(
                "aberta",
                "em_direto",
                "live"
            )

            if (!podeEntrar) {
                error("Esta partida não está disponível para inscrição.")
            }

            if (detalhes.jogadoresInscritos >= peladinha.maxJogadores && peladinha.maxJogadores > 0) {
                error("Esta partida já está cheia.")
            }

            val participantes = runCatching {
                client.from("peladinha_participante")
                    .select()
                    .decodeList<PeladinhaParticipante>()
            }.getOrDefault(emptyList())

            val jaParticipa = participantes.any {
                it.idPeladinha == idPeladinha &&
                        it.idUtilizador == idUtilizador &&
                        it.estadoParticipacao.lowercase() != "recusado"
            }

            if (jaParticipa) {
                error("Já estás inscrito nesta partida.")
            }

            val novoParticipante = PeladinhaParticipante(
                idUtilizador = idUtilizador,
                idPeladinha = idPeladinha,
                estadoParticipacao = "aceite"
            )

            client.from("peladinha_participante")
                .insert(novoParticipante)
        }
    }

    suspend fun sairDaPeladinha(idPeladinha: Long): Result<Unit> {
        return runCatching {
            val idUtilizador = obterIdUtilizadorAtualOuPrimeiroTeste()

            client.from("peladinha_participante")
                .delete {
                    filter {
                        eq("id_utilizador", idUtilizador)
                        eq("id_peladinha", idPeladinha)
                    }
                }
        }
    }

    private suspend fun obterIdUtilizadorAtualOuNull(): String? {
        return authRepository.obterUtilizadorAtual()
            .getOrNull()
            ?.id
    }

    private suspend fun obterIdUtilizadorAtualOuPrimeiroTeste(): String {
        val utilizadorAtual = authRepository.obterUtilizadorAtual().getOrNull()

        if (utilizadorAtual != null) {
            return utilizadorAtual.id
        }

        val utilizadores = client.from("utilizador")
            .select()
            .decodeList<Utilizador>()

        return utilizadores.firstOrNull()?.id
            ?: error("Não foi possível identificar o utilizador.")
    }
}

@Serializable
private data class CriarPeladinhaRequest(
    @SerialName("id_modalidade")
    val idModalidade: Long,

    @SerialName("id_organizador")
    val idOrganizador: String,

    val data: String? = null,
    val hora: String? = null,
    val preco: Double? = null,
    val descricao: String? = null,

    @SerialName("max_jogadores")
    val maxJogadores: Int = 0,

    val estado: String = "aberta",
    val local: String? = null
)