package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.Equipa
import com.example.trabalhocm.data.model.Jogo
import com.example.trabalhocm.data.model.JogoEquipa
import com.example.trabalhocm.data.model.Torneio
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class EstatisticaEquipaLiga(
    val idEquipa: Long,
    val nomeEquipa: String,
    val jogosDisputados: Int,
    val vitorias: Int,
    val empates: Int,
    val derrotas: Int,
    val pontos: Int
)

@Serializable
private data class ClassificacaoBD(
    @SerialName("id_equipa") val idEquipa: Long,
    val vitorias: Int = 0,
    val empates: Int = 0,
    val derrotas: Int = 0,
    val pontos: Int = 0
)

@Serializable
private data class TorneioEquipaDB(
    @SerialName("id_equipa") val idEquipa: Long,
    @SerialName("id_torneio") val idTorneio: Long,
    val estado: String
)

data class JogoBracketUI(
    val idJogo: Long,
    val nomeCasa: String,
    val pontosCasa: String,
    val nomeFora: String,
    val pontosFora: String
)


class TorneioRepository {

    private val client = SupabaseClient.client

    suspend fun listarTorneios(): Result<List<Torneio>> {
        return runCatching {
            client.from("torneio")
                .select()
                .decodeList<Torneio>()
        }
    }

    suspend fun obterTorneioPorId(id: Long): Result<Torneio> {
        return runCatching {
            client.from("torneio")
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingle<Torneio>()
        }
    }

    suspend fun obterJogosEliminatorias(idTorneio: Long): Result<List<Jogo>> {
        return runCatching {
            client.from("jogo")
                .select {
                    filter {
                        eq("id_torneio", idTorneio)
                    }
                }
                .decodeList<Jogo>()
        }
    }

    suspend fun obterJogosBracket(idTorneio: Long): Result<List<JogoBracketUI>> {
        return runCatching {
            val jogos = client.from("jogo")
                .select { filter { eq("id_torneio", idTorneio) } }
                .decodeList<Jogo>()
                .sortedBy { it.id }

            if (jogos.isEmpty()) return@runCatching emptyList()

            val idsJogos = jogos.map { it.id }

            val jogoEquipas = client.from("jogo_equipa")
                .select { filter { isIn("id_jogo", idsJogos) } }
                .decodeList<JogoEquipa>()

            val idsEquipas = jogoEquipas.map { it.idEquipa }.distinct()

            val equipas = if (idsEquipas.isNotEmpty()) {
                client.from("equipa")
                    .select { filter { isIn("id", idsEquipas) } }
                    .decodeList<Equipa>()
                    .associateBy { it.id }
            } else {
                emptyMap()
            }

            jogos.map { jogo ->
                val equipasDesteJogo = jogoEquipas.filter { it.idJogo == jogo.id }
                val equipaCasa = equipasDesteJogo.find { it.papelEquipa.lowercase() == "casa" }
                val equipaFora = equipasDesteJogo.find { it.papelEquipa.lowercase() == "fora" }

                JogoBracketUI(
                    idJogo = jogo.id,
                    nomeCasa = equipaCasa?.idEquipa?.let { equipas[it]?.nome } ?: "TBD",
                    pontosCasa = equipaCasa?.pontosMarcados?.toString() ?: "-",
                    nomeFora = equipaFora?.idEquipa?.let { equipas[it]?.nome } ?: "TBD",
                    pontosFora = equipaFora?.pontosMarcados?.toString() ?: "-"
                )
            }
        }
    }

    suspend fun obterClassificacaoTorneio(idTorneio: Long): Result<List<EstatisticaEquipaLiga>> {
        return runCatching {

            val inscricoes = client.from("torneio_equipa")
                .select {
                    filter {
                        eq("id_torneio", idTorneio)
                        neq("estado", "rejeitada")
                    }
                }
                .decodeList<TorneioEquipaDB>()

            if (inscricoes.isEmpty()) return@runCatching emptyList()

            val idsEquipas = inscricoes.map { it.idEquipa }

            val equipas = client.from("equipa")
                .select {
                    filter { isIn("id", idsEquipas) }
                }
                .decodeList<Equipa>()
                .associateBy { it.id }

            val classificacoesBD = client.from("classificacao")
                .select {
                    filter {
                        isIn("id_equipa", idsEquipas)
                        eq("id_torneio", idTorneio)
                    }
                }
                .decodeList<ClassificacaoBD>()
                .associateBy { it.idEquipa }

            idsEquipas.mapNotNull { idEquipa ->
                val equipaNome = equipas[idEquipa]?.nome ?: return@mapNotNull null

                val estatistica = classificacoesBD[idEquipa] ?: ClassificacaoBD(idEquipa)

                val v = estatistica.vitorias
                val e = estatistica.empates
                val d = estatistica.derrotas

                val jogos = v + e + d

                val pontos = estatistica.pontos

                EstatisticaEquipaLiga(
                    idEquipa = idEquipa,
                    nomeEquipa = equipaNome,
                    jogosDisputados = jogos,
                    vitorias = v,
                    empates = e,
                    derrotas = d,
                    pontos = pontos
                )
            }
                .sortedWith(
                    compareByDescending<EstatisticaEquipaLiga> { it.pontos }
                        .thenByDescending { it.vitorias }
                        .thenBy { it.nomeEquipa }
                )
        }
    }

    suspend fun verificarEquipaInscrita(idTorneio: Long, idEquipa: Long): Result<Boolean> {
        return runCatching {
            val inscricoes = client.from("torneio_equipa")
                .select {
                    filter {
                        eq("id_torneio", idTorneio)
                        eq("id_equipa", idEquipa)
                        neq("estado", "rejeitada")
                    }
                }
                .decodeList<TorneioEquipaDB>()

            inscricoes.isNotEmpty()
        }
    }

    suspend fun atualizarTorneio(torneio: Torneio): Result<Unit> {
        return runCatching {
            val linhasAtualizadas = client.from("torneio")
                .update(
                    AtualizarTorneioRequest(
                        nome = torneio.nome,
                        descricao = torneio.descricao,
                        regras = torneio.regras,
                        local = torneio.local,
                        dataInicio = torneio.dataInicio,
                        dataFim = torneio.dataFim,
                        formato = torneio.formato,
                        taxaInscricao = torneio.taxaInscricao,
                        premio = torneio.premio,
                        idModalidade = torneio.idModalidade
                    )
                ) {
                    select()
                    filter {
                        eq("id", torneio.id)
                    }
                }
                .decodeList<Torneio>()

            if (linhasAtualizadas.isEmpty()) {
                throw Exception("Nothing updated — only the tournament organizer can edit it.")
            }
        }
    }

    suspend fun apagarTorneio(id: Long): Result<Unit> {
        return runCatching {
            val linhasApagadas = client.from("torneio")
                .delete {
                    select()
                    filter {
                        eq("id", id)
                    }
                }
                .decodeList<Torneio>()

            if (linhasApagadas.isEmpty()) {
                throw Exception("Nothing deleted — only the tournament organizer can delete it.")
            }
        }
    }

    suspend fun criarTorneio(torneio: Torneio): Result<Unit> {
        return runCatching {
            val userId = client.auth.currentUserOrNull()?.id
                ?: throw Exception("User not authenticated.")

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
private data class AtualizarTorneioRequest(
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

    @SerialName("id_modalidade")
    val idModalidade: Long
)

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