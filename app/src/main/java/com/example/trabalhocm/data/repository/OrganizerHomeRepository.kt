package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

data class OrganizerHomeData(
    val liveMatch: OrganizerHomeLiveMatch?,
    val activeTournaments: List<OrganizerHomeTournament>,
    val upcomingFixtures: List<OrganizerHomeFixture>,
    val playerOfWeek: OrganizerHomePlayerStats?
)

data class OrganizerHomeLiveMatch(
    val idJogo: Long,
    val torneioNome: String,
    val equipaCasa: String,
    val equipaFora: String,
    val pontosCasa: Int,
    val pontosFora: Int,
    val minuto: Int,
    val local: String
)

data class OrganizerHomeTournament(
    val idTorneio: Long,
    val nome: String,
    val progresso: Float,
    val progressoTexto: String,
    val estado: String
)

data class OrganizerHomeFixture(
    val idJogo: Long,
    val data: String,
    val hora: String,
    val equipaCasa: String,
    val equipaFora: String,
    val estadoJogo: String
)

data class OrganizerHomePlayerStats(
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

class OrganizerHomeRepository {

    private val client = SupabaseClient.client

    suspend fun carregarDadosHome(): Result<OrganizerHomeData> {
        return runCatching {
            val organizerId = client.auth.currentUserOrNull()?.id
                ?: throw Exception("Utilizador não autenticado.")

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

            val torneiosDoOrganizador = torneios.filter { torneio ->
                torneio.orgStringValue("id_organizador") == organizerId
            }

            val idsTorneiosDoOrganizador = torneiosDoOrganizador
                .map { it.orgLongValue("id") }
                .toSet()

            val equipasPorId = equipas.associateBy { it.orgLongValue("id") }
            val torneiosPorId = torneios.associateBy { it.orgLongValue("id") }
            val utilizadoresPorId = utilizadores.associateBy {
                it.orgStringValue("id").orEmpty()
            }
            val jogoEquipasPorJogo = jogoEquipas.groupBy {
                it.orgLongValue("id_jogo")
            }

            val liveMatch = obterLiveMatchDoOrganizador(
                jogos = jogos,
                idsTorneiosDoOrganizador = idsTorneiosDoOrganizador,
                jogoEquipasPorJogo = jogoEquipasPorJogo,
                equipasPorId = equipasPorId,
                torneiosPorId = torneiosPorId
            )

            val activeTournaments = obterTorneiosAtivosDoOrganizador(
                torneios = torneiosDoOrganizador
            )

            val upcomingFixtures = obterUpcomingFixturesDoOrganizador(
                jogos = jogos,
                idsTorneiosDoOrganizador = idsTorneiosDoOrganizador,
                jogoEquipasPorJogo = jogoEquipasPorJogo,
                equipasPorId = equipasPorId
            )

            val ranking = obterRankingJogadores(
                estatisticas = estatisticas,
                utilizadoresPorId = utilizadoresPorId
            )

            OrganizerHomeData(
                liveMatch = liveMatch,
                activeTournaments = activeTournaments,
                upcomingFixtures = upcomingFixtures,
                playerOfWeek = ranking.firstOrNull()
            )
        }
    }

    private fun obterLiveMatchDoOrganizador(
        jogos: List<JsonObject>,
        idsTorneiosDoOrganizador: Set<Long>,
        jogoEquipasPorJogo: Map<Long, List<JsonObject>>,
        equipasPorId: Map<Long, JsonObject>,
        torneiosPorId: Map<Long, JsonObject>
    ): OrganizerHomeLiveMatch? {
        val jogo = jogos.firstOrNull { jogo ->
            val estado = jogo.orgStringValue("estado_jogo")
            val idTorneio = jogo.orgLongValue("id_torneio")

            estado == "em_direto" && idTorneio in idsTorneiosDoOrganizador
        } ?: return null

        val idJogo = jogo.orgLongValue("id")
        val equipasDoJogo = jogoEquipasPorJogo[idJogo].orEmpty()

        val equipaCasaLinha = equipasDoJogo.firstOrNull {
            it.orgStringValue("papel_equipa") == "casa"
        }

        val equipaForaLinha = equipasDoJogo.firstOrNull {
            it.orgStringValue("papel_equipa") == "fora"
        }

        if (equipaCasaLinha == null || equipaForaLinha == null) {
            return null
        }

        val equipaCasa = equipasPorId[equipaCasaLinha.orgLongValue("id_equipa")]
        val equipaFora = equipasPorId[equipaForaLinha.orgLongValue("id_equipa")]
        val torneio = torneiosPorId[jogo.orgLongValue("id_torneio")]

        return OrganizerHomeLiveMatch(
            idJogo = idJogo,
            torneioNome = torneio?.orgStringValue("nome") ?: "Live Match",
            equipaCasa = equipaCasa?.orgStringValue("nome") ?: "Equipa Casa",
            equipaFora = equipaFora?.orgStringValue("nome") ?: "Equipa Fora",
            pontosCasa = equipaCasaLinha.orgIntValue("pontos_marcados"),
            pontosFora = equipaForaLinha.orgIntValue("pontos_marcados"),
            minuto = 75,
            local = jogo.orgStringValue("local") ?: "Local por definir"
        )
    }

    private fun obterTorneiosAtivosDoOrganizador(
        torneios: List<JsonObject>
    ): List<OrganizerHomeTournament> {
        return torneios
            .filter { torneio ->
                val estado = torneio.orgStringValue("estado")?.lowercase().orEmpty()

                estado == "aberto" ||
                        estado == "em_decorrer" ||
                        estado == "ativo"
            }
            .sortedBy {
                it.orgStringValue("data_inicio").orEmpty()
            }
            .take(3)
            .map { torneio ->
                val progresso = calcularProgressoTorneio(
                    dataInicio = torneio.orgStringValue("data_inicio"),
                    dataFim = torneio.orgStringValue("data_fim")
                )

                OrganizerHomeTournament(
                    idTorneio = torneio.orgLongValue("id"),
                    nome = torneio.orgStringValue("nome") ?: "Torneio",
                    progresso = progresso,
                    progressoTexto = "${(progresso * 100).roundToInt()}%",
                    estado = torneio.orgStringValue("estado") ?: "aberto"
                )
            }
    }

    private fun obterUpcomingFixturesDoOrganizador(
        jogos: List<JsonObject>,
        idsTorneiosDoOrganizador: Set<Long>,
        jogoEquipasPorJogo: Map<Long, List<JsonObject>>,
        equipasPorId: Map<Long, JsonObject>
    ): List<OrganizerHomeFixture> {
        return jogos
            .filter { jogo ->
                val estado = jogo.orgStringValue("estado_jogo")
                val idTorneio = jogo.orgLongValue("id_torneio")

                estado == "agendado" && idTorneio in idsTorneiosDoOrganizador
            }
            .sortedWith(
                compareBy<JsonObject> {
                    orgParseDate(it.orgStringValue("data")) ?: LocalDate.MAX
                }.thenBy {
                    orgParseTime(it.orgStringValue("hora")) ?: LocalTime.MAX
                }
            )
            .take(2)
            .mapNotNull { jogo ->
                val idJogo = jogo.orgLongValue("id")
                val equipasDoJogo = jogoEquipasPorJogo[idJogo].orEmpty()

                val equipaCasaLinha = equipasDoJogo.firstOrNull {
                    it.orgStringValue("papel_equipa") == "casa"
                }

                val equipaForaLinha = equipasDoJogo.firstOrNull {
                    it.orgStringValue("papel_equipa") == "fora"
                }

                if (equipaCasaLinha == null || equipaForaLinha == null) {
                    return@mapNotNull null
                }

                val equipaCasa = equipasPorId[equipaCasaLinha.orgLongValue("id_equipa")]
                val equipaFora = equipasPorId[equipaForaLinha.orgLongValue("id_equipa")]

                OrganizerHomeFixture(
                    idJogo = idJogo,
                    data = jogo.orgStringValue("data") ?: "",
                    hora = jogo.orgStringValue("hora") ?: "",
                    equipaCasa = equipaCasa?.orgStringValue("nome") ?: "Equipa Casa",
                    equipaFora = equipaFora?.orgStringValue("nome") ?: "Equipa Fora",
                    estadoJogo = jogo.orgStringValue("estado_jogo") ?: "agendado"
                )
            }
    }

    private fun obterRankingJogadores(
        estatisticas: List<JsonObject>,
        utilizadoresPorId: Map<String, JsonObject>
    ): List<OrganizerHomePlayerStats> {
        return estatisticas
            .groupBy {
                it.orgStringValue("id_utilizador").orEmpty()
            }
            .mapNotNull { (idUtilizador, linhas) ->
                if (idUtilizador.isBlank()) {
                    return@mapNotNull null
                }

                val utilizador = utilizadoresPorId[idUtilizador]

                val vitorias = linhas.sumOf { it.orgIntValue("vitorias") }
                val empates = linhas.sumOf { it.orgIntValue("empates") }
                val derrotas = linhas.sumOf { it.orgIntValue("derrotas") }
                val numJogos = linhas.sumOf { it.orgIntValue("num_jogos") }
                val pontuacao = linhas.sumOf { it.orgIntValue("pontuacao") }

                val rating = calcularRating(
                    vitorias = vitorias,
                    empates = empates,
                    derrotas = derrotas,
                    pontuacao = pontuacao
                )

                OrganizerHomePlayerStats(
                    idUtilizador = idUtilizador,
                    nome = utilizador?.orgStringValue("nome") ?: "Jogador",
                    username = utilizador?.orgStringValue("username") ?: "player",
                    fotoUrl = utilizador?.orgStringValue("foto_url"),
                    vitorias = vitorias,
                    empates = empates,
                    derrotas = derrotas,
                    numJogos = numJogos,
                    pontuacao = pontuacao,
                    rating = rating
                )
            }
            .sortedWith(
                compareByDescending<OrganizerHomePlayerStats> {
                    it.pontuacao
                }.thenByDescending {
                    it.vitorias
                }.thenBy {
                    it.derrotas
                }
            )
    }

    private fun calcularProgressoTorneio(
        dataInicio: String?,
        dataFim: String?
    ): Float {
        val inicio = orgParseDate(dataInicio) ?: return 0.15f
        val fim = orgParseDate(dataFim) ?: return 0.50f
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

private fun JsonObject.orgStringValue(key: String): String? {
    return this[key]?.jsonPrimitive?.contentOrNull
}

private fun JsonObject.orgLongValue(key: String): Long {
    return this[key]?.jsonPrimitive?.longOrNull ?: 0L
}

private fun JsonObject.orgIntValue(key: String): Int {
    return this[key]?.jsonPrimitive?.intOrNull ?: 0
}

private fun orgParseDate(value: String?): LocalDate? {
    return runCatching {
        LocalDate.parse(value?.take(10).orEmpty())
    }.getOrNull()
}

private fun orgParseTime(value: String?): LocalTime? {
    return runCatching {
        LocalTime.parse(value?.take(5).orEmpty())
    }.getOrNull()
}