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

data class PerformanceInsights(
    val playerOfWeek: PlayerHomePlayerStats?,
    val top3Players: List<PlayerHomePlayerStats>,
    val currentUserRank: Int?,
    val rankVariationText: String
)

data class PlayerHomeData(
    val liveMatch: PlayerHomeLiveMatch?,
    val activeTournaments: List<PlayerHomeTournament>,
    val upcomingFixtures: List<PlayerHomeFixture>,
    val perfAll: PerformanceInsights,
    val perfFootball: PerformanceInsights,
    val perfBasketball: PerformanceInsights,
    val perfVolleyball: PerformanceInsights
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
    private val authRepository = AuthRepository()

    suspend fun carregarDadosHome(): Result<PlayerHomeData> {
        return runCatching {
            val userIdAtual = authRepository.obterUtilizadorAtual().getOrNull()?.id

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

            // Criar rankings separados por modalidade
            val rankingAll = obterRankingJogadores(estatisticas, utilizadores)
            val rankingFut = obterRankingJogadores(estatisticas.filter { it.homeIntValue("id_modalidade") == 1 }, utilizadores)
            val rankingBasq = obterRankingJogadores(estatisticas.filter { it.homeIntValue("id_modalidade") == 2 }, utilizadores)
            val rankingVol = obterRankingJogadores(estatisticas.filter { it.homeIntValue("id_modalidade") == 3 }, utilizadores)

            fun criarPerformance(ranking: List<PlayerHomePlayerStats>): PerformanceInsights {
                val playerOfWeek = ranking.firstOrNull { it.numJogos > 0 }
                val top3 = ranking.filter { it.numJogos > 0 }.take(3)

                val currentUserRank = if (userIdAtual != null) {
                    val idx = ranking.indexOfFirst { it.idUtilizador == userIdAtual }
                    if (idx >= 0) idx + 1 else ranking.size + 1
                } else null

                return PerformanceInsights(
                    playerOfWeek = playerOfWeek,
                    top3Players = top3,
                    currentUserRank = currentUserRank,
                    rankVariationText = "↗ 12%"
                )
            }

            PlayerHomeData(
                liveMatch = liveMatch,
                activeTournaments = activeTournaments,
                upcomingFixtures = upcomingFixtures,
                perfAll = criarPerformance(rankingAll),
                perfFootball = criarPerformance(rankingFut),
                perfBasketball = criarPerformance(rankingBasq),
                perfVolleyball = criarPerformance(rankingVol)
            )
        }
    }

    private fun obterLiveMatch(
        jogos: List<JsonObject>,
        jogoEquipasPorJogo: Map<Long, List<JsonObject>>,
        equipasPorId: Map<Long, JsonObject>,
        torneiosPorId: Map<Long, JsonObject>
    ): PlayerHomeLiveMatch? {
        // Considera "live" um jogo marcado em_direto OU cuja hora de início já passou
        // e ainda está dentro da duração (~90 min).
        val agora = java.time.LocalDateTime.now()
        val jogo = jogos.firstOrNull { homeJogoEstaLive(it, agora) } ?: return null

        val idJogo = jogo.homeLongValue("id")
        val equipasDoJogo = jogoEquipasPorJogo[idJogo].orEmpty()

        // Tolerância para "casa" ou "visitado", ou pega na primeira equipa que vir
        val equipaCasaLinha = equipasDoJogo.firstOrNull {
            val papel = it.homeStringValue("papel_equipa")?.lowercase()
            papel == "casa" || papel == "visitado"
        } ?: equipasDoJogo.getOrNull(0)

        // Tolerância para "fora" ou "visitante", ou pega na segunda equipa
        val equipaForaLinha = equipasDoJogo.firstOrNull {
            val papel = it.homeStringValue("papel_equipa")?.lowercase()
            papel == "fora" || papel == "visitante"
        } ?: equipasDoJogo.getOrNull(1)

        if (equipaCasaLinha == null || equipaForaLinha == null) {
            return null
        }

        val equipaCasa = equipasPorId[equipaCasaLinha.homeLongValue("id_equipa")]
        val equipaFora = equipasPorId[equipaForaLinha.homeLongValue("id_equipa")]
        val torneio = torneiosPorId[jogo.homeLongValue("id_torneio")]

        // Lê os pontos da forma mais abrangente possível
        val pontosC = equipaCasaLinha.homeIntValue("pontos_marcados").takeIf { it > 0 } ?: equipaCasaLinha.homeIntValue("pontos").takeIf { it > 0 } ?: equipaCasaLinha.homeIntValue("golos")
        val pontosF = equipaForaLinha.homeIntValue("pontos_marcados").takeIf { it > 0 } ?: equipaForaLinha.homeIntValue("pontos").takeIf { it > 0 } ?: equipaForaLinha.homeIntValue("golos")

        // Lê o minuto real da BD, ou calcula-o a partir da hora de início do jogo
        val minutoBD = jogo.homeIntValue("minuto_atual").takeIf { it > 0 } ?: jogo.homeIntValue("minuto").takeIf { it > 0 } ?: homeCalcularMinutoLive(jogo, agora)

        return PlayerHomeLiveMatch(
            idJogo = idJogo,
            torneioNome = torneio?.homeStringValue("nome") ?: "Live Match",
            equipaCasa = equipaCasa?.homeStringValue("nome") ?: "Equipa Casa",
            equipaFora = equipaFora?.homeStringValue("nome") ?: "Equipa Fora",
            pontosCasa = pontosC,
            pontosFora = pontosF,
            minuto = minutoBD,
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
        val agora = java.time.LocalDateTime.now()
        return jogos
            .filter { jogo ->
                val estado = jogo.homeStringValue("estado_jogo")?.lowercase()
                val agendado = estado == "agendado" || estado == "pendente" || estado == "scheduled"
                // Um jogo que já está a decorrer (live por tempo) não deve aparecer em "próximos"
                agendado && !homeJogoEstaLive(jogo, agora)
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
                    val papel = it.homeStringValue("papel_equipa")?.lowercase()
                    papel == "casa" || papel == "visitado"
                } ?: equipasDoJogo.getOrNull(0)

                val equipaForaLinha = equipasDoJogo.firstOrNull {
                    val papel = it.homeStringValue("papel_equipa")?.lowercase()
                    papel == "fora" || papel == "visitante"
                } ?: equipasDoJogo.getOrNull(1)

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
        utilizadores: List<JsonObject>
    ): List<PlayerHomePlayerStats> {
        val estatisticasPorUtilizador = estatisticas.groupBy { it.homeStringValue("id_utilizador").orEmpty() }

        return utilizadores.mapNotNull { utilizador ->
            val idUtilizador = utilizador.homeStringValue("id").orEmpty()
            if (idUtilizador.isBlank()) {
                return@mapNotNull null
            }

            val linhas = estatisticasPorUtilizador[idUtilizador] ?: emptyList()

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
                nome = utilizador.homeStringValue("nome") ?: "Jogador",
                username = utilizador.homeStringValue("username") ?: "player",
                fotoUrl = utilizador.homeStringValue("foto_url"),
                vitorias = vitorias,
                empates = empates,
                derrotas = derrotas,
                numJogos = numJogos,
                pontuacao = pontuacao,
                rating = rating
            )
        }.sortedWith(
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

private val HOME_ESTADOS_FINAIS = setOf("terminado", "cancelado", "concluido", "finalizado", "adiado")

private fun homeJogoInicio(jogo: JsonObject): java.time.LocalDateTime? {
    val data = homeParseDate(jogo.homeStringValue("data")) ?: return null
    val hora = homeParseTime(jogo.homeStringValue("hora")) ?: LocalTime.MIDNIGHT
    return java.time.LocalDateTime.of(data, hora)
}

// Live se em_direto, ou se a hora de início já passou e ainda não passaram ~90 min.
private fun homeJogoEstaLive(jogo: JsonObject, agora: java.time.LocalDateTime): Boolean {
    val estado = jogo.homeStringValue("estado_jogo")?.lowercase().orEmpty()
    if (estado == "em_direto" || estado == "live" || estado == "em_decorrer" || estado == "a_decorrer") return true
    if (estado in HOME_ESTADOS_FINAIS) return false
    val inicio = homeJogoInicio(jogo) ?: return false
    return !agora.isBefore(inicio) && agora.isBefore(inicio.plusMinutes(90L))
}

private fun homeCalcularMinutoLive(jogo: JsonObject, agora: java.time.LocalDateTime): Int {
    val inicio = homeJogoInicio(jogo) ?: return 45
    val mins = ChronoUnit.MINUTES.between(inicio, agora)
    return mins.coerceIn(1L, 90L).toInt()
}