package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

data class PlayerHomeData(
    val liveMatch: PlayerHomeLiveMatch?,
    val activeTournaments: List<PlayerHomeTournament>,
    val upcomingFixtures: List<PlayerHomeFixture>,
    val playerOfWeek: PlayerHomePlayerStats?,
    val currentUserRank: Int?,
    val rankVariationText: String
)

data class PlayerHomeLiveMatch(
    val idJogo: Long,
    val torneioNome: String,
    val equipaCasa: String,
    val equipaFora: String,
    val pontosCasa: Int,
    val pontosFora: Int,
    val minuto: Int,
    val local: String
)

data class PlayerHomeTournament(
    val idTorneio: Long,
    val nome: String,
    val papel: String,
    val progresso: Float,
    val progressoTexto: String,
    val estado: String
)

data class PlayerHomeFixture(
    val idJogo: Long,
    val data: String,
    val hora: String,
    val equipaCasa: String,
    val equipaFora: String,
    val estadoJogo: String
)

data class PlayerHomePlayerStats(
    val idUtilizador: String,
    val nome: String,
    val username: String,
    val fotoUrl: String?,
    val vitorias: Int,
    val empates: Int,
    val derrotas: Int,
    val numJogos: Int,
    val pontuacao: Int,
    val rating: Double
)

class PlayerHomeRepository {

    private val client = SupabaseClient.client

    suspend fun carregarDadosHome(): Result<PlayerHomeData> {
        return runCatching {
            val userIdAtual = client.auth.currentUserOrNull()?.id

            val jogos = client.from("jogo")
                .select()
                .decodeList<JsonObject>()

            val jogoEquipas = client.from("jogo_equipa")
                .select()
                .decodeList<JsonObject>()

            val equipas = client.from("equipa")
                .select()
                .decodeList<JsonObject>()

            val torneios = client.from("torneio")
                .select()
                .decodeList<JsonObject>()

            val utilizadores = client.from("utilizador")
                .select()
                .decodeList<JsonObject>()

            val estatisticas = runCatching {
                client.from("estatistica_jogador")
                    .select()
                    .decodeList<JsonObject>()
            }.getOrDefault(emptyList())

            val equipasPorId = equipas.associateBy { it.homeLongValue("id") }
            val torneiosPorId = torneios.associateBy { it.homeLongValue("id") }
            val utilizadoresPorId = utilizadores.associateBy { it.homeStringValue("id").orEmpty() }
            val jogoEquipasPorJogo = jogoEquipas.groupBy { it.homeLongValue("id_jogo") }

            val liveMatch = obterLiveMatch(
                jogos = jogos,
                jogoEquipasPorJogo = jogoEquipasPorJogo,
                equipasPorId = equipasPorId,
                torneiosPorId = torneiosPorId
            )

            val activeTournaments = obterTorneiosAtivos(
                torneios = torneios,
                userIdAtual = userIdAtual
            )

            val upcomingFixtures = obterUpcomingFixtures(
                jogos = jogos,
                jogoEquipasPorJogo = jogoEquipasPorJogo,
                equipasPorId = equipasPorId
            )

            val ranking = obterRankingJogadores(
                estatisticas = estatisticas,
                utilizadoresPorId = utilizadoresPorId
            )

            val playerOfWeek = ranking.firstOrNull()

            val currentUserRank = if (userIdAtual != null) {
                ranking.indexOfFirst { it.idUtilizador == userIdAtual }
                    .takeIf { it >= 0 }
                    ?.plus(1)
            } else {
                null
            }

            PlayerHomeData(
                liveMatch = liveMatch,
                activeTournaments = activeTournaments,
                upcomingFixtures = upcomingFixtures,
                playerOfWeek = playerOfWeek,
                currentUserRank = currentUserRank,
                rankVariationText = "↗ 12%"
            )
        }
    }

    private fun obterLiveMatch(
        jogos: List<JsonObject>,
        jogoEquipasPorJogo: Map<Long, List<JsonObject>>,
        equipasPorId: Map<Long, JsonObject>,
        torneiosPorId: Map<Long, JsonObject>
    ): PlayerHomeLiveMatch? {
        val jogo = jogos.firstOrNull {
            it.homeStringValue("estado_jogo") == "em_direto"
        } ?: return null

        val idJogo = jogo.homeLongValue("id")
        val equipasDoJogo = jogoEquipasPorJogo[idJogo].orEmpty()

        val equipaCasaLinha = equipasDoJogo.firstOrNull {
            it.homeStringValue("papel_equipa") == "casa"
        }

        val equipaForaLinha = equipasDoJogo.firstOrNull {
            it.homeStringValue("papel_equipa") == "fora"
        }

        if (equipaCasaLinha == null || equipaForaLinha == null) {
            return null
        }

        val equipaCasa = equipasPorId[equipaCasaLinha.homeLongValue("id_equipa")]
        val equipaFora = equipasPorId[equipaForaLinha.homeLongValue("id_equipa")]
        val torneio = torneiosPorId[jogo.homeLongValue("id_torneio")]

        return PlayerHomeLiveMatch(
            idJogo = idJogo,
            torneioNome = torneio?.homeStringValue("nome") ?: "Live Match",
            equipaCasa = equipaCasa?.homeStringValue("nome") ?: "Equipa Casa",
            equipaFora = equipaFora?.homeStringValue("nome") ?: "Equipa Fora",
            pontosCasa = equipaCasaLinha.homeIntValue("pontos_marcados"),
            pontosFora = equipaForaLinha.homeIntValue("pontos_marcados"),
            minuto = 75,
            local = jogo.homeStringValue("local") ?: "Local por definir"
        )
    }

    private fun obterTorneiosAtivos(
        torneios: List<JsonObject>,
        userIdAtual: String?
    ): List<PlayerHomeTournament> {
        return torneios
            .filter { torneio ->
                val estado = torneio.homeStringValue("estado")?.lowercase().orEmpty()

                estado == "aberto" ||
                        estado == "em_decorrer" ||
                        estado == "ativo"
            }
            .sortedBy { it.homeStringValue("data_inicio").orEmpty() }
            .take(2)
            .map { torneio ->
                val idOrganizador = torneio.homeStringValue("id_organizador")
                val papel = if (userIdAtual != null && idOrganizador == userIdAtual) {
                    "ORGANIZER"
                } else {
                    "PLAYER"
                }

                val progresso = calcularProgressoTorneio(
                    dataInicio = torneio.homeStringValue("data_inicio"),
                    dataFim = torneio.homeStringValue("data_fim")
                )

                PlayerHomeTournament(
                    idTorneio = torneio.homeLongValue("id"),
                    nome = torneio.homeStringValue("nome") ?: "Torneio",
                    papel = papel,
                    progresso = progresso,
                    progressoTexto = "${(progresso * 100).roundToInt()}%",
                    estado = torneio.homeStringValue("estado") ?: "aberto"
                )
            }
    }

    private fun obterUpcomingFixtures(
        jogos: List<JsonObject>,
        jogoEquipasPorJogo: Map<Long, List<JsonObject>>,
        equipasPorId: Map<Long, JsonObject>
    ): List<PlayerHomeFixture> {
        return jogos
            .filter { jogo ->
                jogo.homeStringValue("estado_jogo") == "agendado"
            }
            .sortedWith(
                compareBy<JsonObject> {
                    homeParseDate(it.homeStringValue("data")) ?: LocalDate.MAX
                }.thenBy {
                    homeParseTime(it.homeStringValue("hora")) ?: LocalTime.MAX
                }
            )
            .take(2)
            .mapNotNull { jogo ->
                val idJogo = jogo.homeLongValue("id")
                val equipasDoJogo = jogoEquipasPorJogo[idJogo].orEmpty()

                val equipaCasaLinha = equipasDoJogo.firstOrNull {
                    it.homeStringValue("papel_equipa") == "casa"
                }

                val equipaForaLinha = equipasDoJogo.firstOrNull {
                    it.homeStringValue("papel_equipa") == "fora"
                }

                if (equipaCasaLinha == null || equipaForaLinha == null) {
                    return@mapNotNull null
                }

                val equipaCasa = equipasPorId[equipaCasaLinha.homeLongValue("id_equipa")]
                val equipaFora = equipasPorId[equipaForaLinha.homeLongValue("id_equipa")]

                PlayerHomeFixture(
                    idJogo = idJogo,
                    data = jogo.homeStringValue("data") ?: "",
                    hora = jogo.homeStringValue("hora") ?: "",
                    equipaCasa = equipaCasa?.homeStringValue("nome") ?: "Equipa Casa",
                    equipaFora = equipaFora?.homeStringValue("nome") ?: "Equipa Fora",
                    estadoJogo = jogo.homeStringValue("estado_jogo") ?: "agendado"
                )
            }
    }

    private fun obterRankingJogadores(
        estatisticas: List<JsonObject>,
        utilizadoresPorId: Map<String, JsonObject>
    ): List<PlayerHomePlayerStats> {
        return estatisticas
            .groupBy { it.homeStringValue("id_utilizador").orEmpty() }
            .mapNotNull { (idUtilizador, linhas) ->
                if (idUtilizador.isBlank()) {
                    return@mapNotNull null
                }

                val utilizador = utilizadoresPorId[idUtilizador]

                val vitorias = linhas.sumOf { it.homeIntValue("vitorias") }
                val empates = linhas.sumOf { it.homeIntValue("empates") }
                val derrotas = linhas.sumOf { it.homeIntValue("derrotas") }
                val numJogos = linhas.sumOf { it.homeIntValue("num_jogos") }
                val pontuacao = linhas.sumOf { it.homeIntValue("pontuacao") }

                val rating = calcularRating(
                    vitorias = vitorias,
                    empates = empates,
                    derrotas = derrotas,
                    pontuacao = pontuacao
                )

                PlayerHomePlayerStats(
                    idUtilizador = idUtilizador,
                    nome = utilizador?.homeStringValue("nome") ?: "Jogador",
                    username = utilizador?.homeStringValue("username") ?: "player",
                    fotoUrl = utilizador?.homeStringValue("foto_url"),
                    vitorias = vitorias,
                    empates = empates,
                    derrotas = derrotas,
                    numJogos = numJogos,
                    pontuacao = pontuacao,
                    rating = rating
                )
            }
            .sortedWith(
                compareByDescending<PlayerHomePlayerStats> { it.pontuacao }
                    .thenByDescending { it.vitorias }
                    .thenBy { it.derrotas }
            )
    }

    private fun calcularProgressoTorneio(
        dataInicio: String?,
        dataFim: String?
    ): Float {
        val inicio = homeParseDate(dataInicio) ?: return 0.15f
        val fim = homeParseDate(dataFim) ?: return 0.50f
        val hoje = LocalDate.now()

        if (hoje.isBefore(inicio)) {
            return 0.05f
        }

        if (hoje.isAfter(fim)) {
            return 1f
        }

        val totalDias = ChronoUnit.DAYS.between(inicio, fim).coerceAtLeast(1)
        val diasPassados = ChronoUnit.DAYS.between(inicio, hoje).coerceAtLeast(0)

        return (diasPassados.toFloat() / totalDias.toFloat()).coerceIn(0f, 1f)
    }

    private fun calcularRating(
        vitorias: Int,
        empates: Int,
        derrotas: Int,
        pontuacao: Int
    ): Double {
        val base = 5.0
        val bonusVitorias = vitorias * 0.45
        val bonusEmpates = empates * 0.15
        val penalizacao = derrotas * 0.20
        val bonusPontuacao = pontuacao / 100.0

        return (base + bonusVitorias + bonusEmpates + bonusPontuacao - penalizacao)
            .coerceIn(0.0, 9.9)
    }
}

private fun JsonObject.homeStringValue(key: String): String? {
    return this[key]?.jsonPrimitive?.contentOrNull
}

private fun JsonObject.homeLongValue(key: String): Long {
    return this[key]?.jsonPrimitive?.longOrNull ?: 0L
}

private fun JsonObject.homeIntValue(key: String): Int {
    return this[key]?.jsonPrimitive?.intOrNull ?: 0
}

private fun JsonObject.homeDoubleValue(key: String): Double {
    return this[key]?.jsonPrimitive?.doubleOrNull ?: 0.0
}

private fun homeParseDate(value: String?): LocalDate? {
    return runCatching {
        LocalDate.parse(value?.take(10).orEmpty())
    }.getOrNull()
}

private fun homeParseTime(value: String?): LocalTime? {
    return runCatching {
        LocalTime.parse(value?.take(5).orEmpty())
    }.getOrNull()
}