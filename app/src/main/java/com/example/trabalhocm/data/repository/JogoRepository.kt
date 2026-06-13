package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.Equipa
import com.example.trabalhocm.data.model.EventoJogo
import com.example.trabalhocm.data.model.Jogo
import com.example.trabalhocm.data.model.JogoEquipa
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import java.time.OffsetDateTime

data class EquipaSimples(
    val id: Long,
    val nome: String
)

data class MatchControlInfo(
    val idJogo: Long,
    val torneioNome: String,
    val data: String,
    val hora: String,
    val local: String,
    val estado: String,
    val idEquipaCasa: Long,
    val equipaCasa: String,
    val pontosCasa: Int,
    val idEquipaFora: Long,
    val equipaFora: String,
    val pontosFora: Int
)

class JogoRepository {
    private val client = SupabaseClient.client

    suspend fun listarJogos(): Result<List<Jogo>> = runCatching {
        client.from("jogo").select().decodeList<Jogo>()
    }

    suspend fun listarJogoEquipas(): Result<List<JogoEquipa>> = runCatching {
        client.from("jogo_equipa").select().decodeList<JogoEquipa>()
    }

    suspend fun listarMeusEventos(userId: String): Result<List<EventoJogo>> = runCatching {
        client.from("evento_jogo")
            .select { filter { eq("id_jogador", userId) } }
            .decodeList<EventoJogo>()
    }

    /** Equipas com inscrição aprovada num torneio — candidatas a entrar num jogo. */
    suspend fun listarEquipasAprovadas(idTorneio: Long): Result<List<EquipaSimples>> = runCatching {
        val inscricoes = client.from("torneio_equipa")
            .select {
                filter {
                    eq("id_torneio", idTorneio)
                    eq("estado", "aprovada")
                }
            }
            .decodeList<JsonObject>()

        val idsEquipas = inscricoes.mapNotNull {
            it["id_equipa"]?.jsonPrimitive?.longOrNull
        }

        if (idsEquipas.isEmpty()) {
            emptyList()
        } else {
            client.from("equipa")
                .select { filter { isIn("id", idsEquipas) } }
                .decodeList<Equipa>()
                .map { EquipaSimples(it.id, it.nome) }
                .sortedBy { it.nome.lowercase() }
        }
    }

    /** Cria um jogo agendado com as duas equipas (casa/fora) e devolve o id do jogo. */
    suspend fun criarJogo(
        idTorneio: Long,
        idEquipaCasa: Long,
        idEquipaFora: Long,
        data: String,
        hora: String,
        local: String?
    ): Result<Long> = runCatching {
        val jogo = client.from("jogo")
            .insert(
                NovoJogoRequest(
                    idTorneio = idTorneio,
                    data = data,
                    hora = hora,
                    local = local
                )
            ) { select() }
            .decodeSingle<Jogo>()

        client.from("jogo_equipa")
            .insert(
                listOf(
                    NovoJogoEquipaRequest(jogo.id, idEquipaCasa, "casa"),
                    NovoJogoEquipaRequest(jogo.id, idEquipaFora, "fora")
                )
            )

        jogo.id
    }

    /** Carrega o estado de um jogo para o ecrã de controlo ao vivo. */
    suspend fun obterControloJogo(idJogo: Long): Result<MatchControlInfo> = runCatching {
        val jogo = client.from("jogo")
            .select { filter { eq("id", idJogo) } }
            .decodeSingle<Jogo>()

        val jogoEquipas = client.from("jogo_equipa")
            .select { filter { eq("id_jogo", idJogo) } }
            .decodeList<JogoEquipa>()

        val casa = jogoEquipas.firstOrNull { it.papelEquipa == "casa" }
            ?: throw Exception("Jogo sem equipa da casa definida.")
        val fora = jogoEquipas.firstOrNull { it.papelEquipa == "fora" }
            ?: throw Exception("Jogo sem equipa visitante definida.")

        val equipas = client.from("equipa").select().decodeList<Equipa>().associateBy { it.id }

        val torneioNome = client.from("torneio")
            .select { filter { eq("id", jogo.idTorneio) } }
            .decodeList<JsonObject>()
            .firstOrNull()
            ?.get("nome")?.jsonPrimitive?.contentOrNull
            ?: "Torneio"

        MatchControlInfo(
            idJogo = jogo.id,
            torneioNome = torneioNome,
            data = jogo.data,
            hora = jogo.hora,
            local = jogo.local ?: "",
            estado = jogo.estadoJogo,
            idEquipaCasa = casa.idEquipa,
            equipaCasa = equipas[casa.idEquipa]?.nome ?: "Equipa Casa",
            pontosCasa = casa.pontosMarcados,
            idEquipaFora = fora.idEquipa,
            equipaFora = equipas[fora.idEquipa]?.nome ?: "Equipa Fora",
            pontosFora = fora.pontosMarcados
        )
    }

    /** Procura o primeiro jogo "live": em_direto OU já dentro da janela início..+90min. */
    suspend fun obterPrimeiroJogoEmDireto(): Result<Long?> = runCatching {
        val agora = java.time.LocalDateTime.now()
        client.from("jogo")
            .select()
            .decodeList<Jogo>()
            .firstOrNull { jogoEstaLivePorTempo(it, agora) }
            ?.id
    }

    suspend fun atualizarPontos(idJogo: Long, idEquipa: Long, novosPontos: Int): Result<Unit> = runCatching {
        val linhas = client.from("jogo_equipa")
            .update(JsonObject(mapOf("pontos_marcados" to JsonPrimitive(novosPontos)))) {
                select()
                filter {
                    eq("id_jogo", idJogo)
                    eq("id_equipa", idEquipa)
                }
            }
            .decodeList<JogoEquipa>()

        if (linhas.isEmpty()) {
            throw Exception("Não tens permissão para editar este jogo (só o organizador do torneio pode).")
        }
    }

    suspend fun atualizarEstadoJogo(
        idJogo: Long,
        estado: String,
        resultadoFinal: String? = null
    ): Result<Unit> = runCatching {
        val campos = buildMap {
            put("estado_jogo", JsonPrimitive(estado))
            if (resultadoFinal != null) {
                put("resultado_final", JsonPrimitive(resultadoFinal))
            }
        }

        val linhas = client.from("jogo")
            .update(JsonObject(campos)) {
                select()
                filter { eq("id", idJogo) }
            }
            .decodeList<Jogo>()

        if (linhas.isEmpty()) {
            throw Exception("Não tens permissão para alterar este jogo (só o organizador do torneio pode).")
        }
    }

    /**
     * Termina um jogo: regista o resultado e recalcula a classificação do torneio,
     * as estatísticas de equipa e as estatísticas dos jogadores de ambas as equipas.
     * Idempotente: se o jogo já estiver terminado, não faz nada (evita duplo cálculo).
     */
    suspend fun finalizarJogo(idJogo: Long): Result<Unit> = runCatching {
        val jogo = client.from("jogo")
            .select { filter { eq("id", idJogo) } }
            .decodeSingle<Jogo>()

        if (jogo.estadoJogo == "terminado") return@runCatching

        val jogoEquipas = client.from("jogo_equipa")
            .select { filter { eq("id_jogo", idJogo) } }
            .decodeList<JogoEquipa>()

        val casa = jogoEquipas.firstOrNull { it.papelEquipa == "casa" }
            ?: throw Exception("Jogo sem equipa da casa.")
        val fora = jogoEquipas.firstOrNull { it.papelEquipa == "fora" }
            ?: throw Exception("Jogo sem equipa visitante.")

        val pc = casa.pontosMarcados
        val pf = fora.pontosMarcados
        val resCasa = if (pc > pf) Resultado.VITORIA else if (pc < pf) Resultado.DERROTA else Resultado.EMPATE
        val resFora = if (pf > pc) Resultado.VITORIA else if (pf < pc) Resultado.DERROTA else Resultado.EMPATE

        // 1. Marcar terminado PRIMEIRO. Se isto falhar (ex.: não és o organizador deste
        //    torneio), aborta já — não conta estatísticas nenhumas, evitando duplo registo.
        atualizarEstadoJogo(idJogo, "terminado", "$pc-$pf").getOrThrow()

        val idModalidade = client.from("torneio")
            .select { filter { eq("id", jogo.idTorneio) } }
            .decodeList<JsonObject>()
            .firstOrNull()
            ?.get("id_modalidade")?.jsonPrimitive?.longOrNull
            ?: 0L

        // 2. Classificação do torneio
        atualizarClassificacao(jogo.idTorneio, casa.idEquipa, resCasa)
        atualizarClassificacao(jogo.idTorneio, fora.idEquipa, resFora)

        // 3. Estatísticas de equipa
        atualizarEstatisticaEquipa(casa.idEquipa, resCasa)
        atualizarEstatisticaEquipa(fora.idEquipa, resFora)

        // 4. Estatísticas dos jogadores (membros aceites)
        atualizarEstatisticaJogadores(casa.idEquipa, idModalidade, resCasa)
        atualizarEstatisticaJogadores(fora.idEquipa, idModalidade, resFora)
    }

    private suspend fun atualizarClassificacao(idTorneio: Long, idEquipa: Long, r: Resultado) {
        val agora = OffsetDateTime.now().toString()
        val existente = client.from("classificacao")
            .select {
                filter {
                    eq("id_torneio", idTorneio)
                    eq("id_equipa", idEquipa)
                }
            }
            .decodeList<JsonObject>()
            .firstOrNull()

        val vitorias = existente.intOf("vitorias") + if (r == Resultado.VITORIA) 1 else 0
        val empates = existente.intOf("empates") + if (r == Resultado.EMPATE) 1 else 0
        val derrotas = existente.intOf("derrotas") + if (r == Resultado.DERROTA) 1 else 0
        val pontos = existente.intOf("pontos") + r.pontos

        if (existente != null) {
            client.from("classificacao")
                .update(buildJsonObject {
                    put("pontos", pontos)
                    put("vitorias", vitorias)
                    put("empates", empates)
                    put("derrotas", derrotas)
                    put("updated_at", agora)
                }) {
                    filter { eq("id", existente.longOf("id")) }
                }
        } else {
            client.from("classificacao")
                .insert(buildJsonObject {
                    put("id_torneio", idTorneio)
                    put("id_equipa", idEquipa)
                    put("pontos", pontos)
                    put("vitorias", vitorias)
                    put("empates", empates)
                    put("derrotas", derrotas)
                    put("created_at", agora)
                    put("updated_at", agora)
                })
        }
    }

    private suspend fun atualizarEstatisticaEquipa(idEquipa: Long, r: Resultado) {
        val agora = OffsetDateTime.now().toString()
        val existente = client.from("estatistica_equipa")
            .select { filter { eq("id_equipa", idEquipa) } }
            .decodeList<JsonObject>()
            .firstOrNull()

        val vitorias = existente.intOf("vitorias") + if (r == Resultado.VITORIA) 1 else 0
        val empates = existente.intOf("empates") + if (r == Resultado.EMPATE) 1 else 0
        val derrotas = existente.intOf("derrotas") + if (r == Resultado.DERROTA) 1 else 0
        val pontos = existente.intOf("pontos") + r.pontos
        val numJogos = existente.intOf("num_jogos") + 1

        if (existente != null) {
            client.from("estatistica_equipa")
                .update(buildJsonObject {
                    put("vitorias", vitorias)
                    put("empates", empates)
                    put("derrotas", derrotas)
                    put("pontos", pontos)
                    put("num_jogos", numJogos)
                    put("updated_at", agora)
                }) {
                    filter { eq("id_equipa", idEquipa) }
                }
        } else {
            client.from("estatistica_equipa")
                .insert(buildJsonObject {
                    put("id_equipa", idEquipa)
                    put("vitorias", vitorias)
                    put("empates", empates)
                    put("derrotas", derrotas)
                    put("pontos", pontos)
                    put("num_jogos", numJogos)
                    put("created_at", agora)
                    put("updated_at", agora)
                })
        }
    }

    private suspend fun atualizarEstatisticaJogadores(idEquipa: Long, idModalidade: Long, r: Resultado) {
        val agora = OffsetDateTime.now().toString()
        val membros = client.from("membro_equipa")
            .select {
                filter {
                    eq("id_equipa", idEquipa)
                    eq("estado_convite", "aceite")
                }
            }
            .decodeList<JsonObject>()

        for (membro in membros) {
            val idUtilizador = membro["id_utilizador"]?.jsonPrimitive?.contentOrNull ?: continue

            val existente = client.from("estatistica_jogador")
                .select {
                    filter {
                        eq("id_utilizador", idUtilizador)
                        eq("id_modalidade", idModalidade)
                    }
                }
                .decodeList<JsonObject>()
                .firstOrNull()

            val vitorias = existente.intOf("vitorias") + if (r == Resultado.VITORIA) 1 else 0
            val empates = existente.intOf("empates") + if (r == Resultado.EMPATE) 1 else 0
            val derrotas = existente.intOf("derrotas") + if (r == Resultado.DERROTA) 1 else 0
            val numJogos = existente.intOf("num_jogos") + 1
            val pontuacao = existente.intOf("pontuacao") + r.pontos

            if (existente != null) {
                client.from("estatistica_jogador")
                    .update(buildJsonObject {
                        put("vitorias", vitorias)
                        put("empates", empates)
                        put("derrotas", derrotas)
                        put("num_jogos", numJogos)
                        put("pontuacao", pontuacao)
                        put("updated_at", agora)
                    }) {
                        filter {
                            eq("id_utilizador", idUtilizador)
                            eq("id_modalidade", idModalidade)
                        }
                    }
            } else {
                client.from("estatistica_jogador")
                    .insert(buildJsonObject {
                        put("id_utilizador", idUtilizador)
                        put("id_modalidade", idModalidade)
                        put("vitorias", vitorias)
                        put("empates", empates)
                        put("derrotas", derrotas)
                        put("num_jogos", numJogos)
                        put("pontuacao", pontuacao)
                        put("created_at", agora)
                        put("updated_at", agora)
                    })
            }
        }
    }
}

private enum class Resultado(val pontos: Int) {
    VITORIA(3), EMPATE(1), DERROTA(0)
}

private fun JsonObject?.intOf(key: String): Int {
    return this?.get(key)?.jsonPrimitive?.intOrNull ?: 0
}

private fun JsonObject.longOf(key: String): Long {
    return this[key]?.jsonPrimitive?.longOrNull ?: 0L
}

private val JOGO_ESTADOS_FINAIS = setOf("terminado", "cancelado", "concluido", "finalizado", "adiado")

private fun jogoLiveStart(jogo: Jogo): java.time.LocalDateTime? {
    val data = runCatching { java.time.LocalDate.parse(jogo.data.take(10)) }.getOrNull() ?: return null
    val hora = runCatching { java.time.LocalTime.parse(jogo.hora.take(5)) }.getOrNull() ?: java.time.LocalTime.MIDNIGHT
    return java.time.LocalDateTime.of(data, hora)
}

// Live se em_direto, ou se a hora de início já passou e ainda não passaram ~90 min.
private fun jogoEstaLivePorTempo(jogo: Jogo, agora: java.time.LocalDateTime): Boolean {
    val estado = jogo.estadoJogo.lowercase()
    if (estado == "em_direto" || estado == "live" || estado == "em_decorrer" || estado == "a_decorrer") return true
    if (estado in JOGO_ESTADOS_FINAIS) return false
    val inicio = jogoLiveStart(jogo) ?: return false
    return !agora.isBefore(inicio) && agora.isBefore(inicio.plusMinutes(90L))
}

@Serializable
private data class NovoJogoRequest(
    @SerialName("id_torneio") val idTorneio: Long,
    val data: String,
    val hora: String,
    val local: String? = null,
    @SerialName("estado_jogo") val estadoJogo: String = "agendado"
)

@Serializable
private data class NovoJogoEquipaRequest(
    @SerialName("id_jogo") val idJogo: Long,
    @SerialName("id_equipa") val idEquipa: Long,
    @SerialName("papel_equipa") val papelEquipa: String,
    @SerialName("pontos_marcados") val pontosMarcados: Int = 0
)
