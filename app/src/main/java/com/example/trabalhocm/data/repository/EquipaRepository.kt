package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.Equipa
import com.example.trabalhocm.data.model.MembroEquipa
import com.example.trabalhocm.data.model.Modalidade
import com.example.trabalhocm.data.model.Utilizador
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

data class EquipaComInfo(
    val equipa: Equipa,
    val modalidadeNome: String,
    val divisao: String,
    val iniciais: String,
    val cidade: String,
    val vitorias: Int,
    val derrotas: Int,
    val streak: String,
    val streakGood: Boolean,
    val utilizadorPertence: Boolean,
    val utilizadorCapitao: Boolean = false
)

data class EquipaDetalhesInfo(
    val equipaInfo: EquipaComInfo,
    val winRate: Double,
    val totalGolos: Int,
    val jogosDisputados: Int,
    val empates: Int,
    val membros: List<MembroEquipaDetalhesInfo>
)

data class MembroEquipaDetalhesInfo(
    val utilizador: Utilizador,
    val papel: String,
    val estadoConvite: String,
    val isCaptain: Boolean,
    val posicao: String = "Forward"
)

data class EquipaGestaoInfo(
    val equipaInfo: EquipaComInfo,
    val membros: List<MembroEquipaGestaoInfo>
)

data class MembroEquipaGestaoInfo(
    val utilizador: Utilizador,
    val papel: String,
    val posicao: String,
    val estadoConvite: String,
    val isCaptain: Boolean
)

data class UtilizadorConviteInfo(
    val utilizador: Utilizador,
    val jaPertenceEquipa: Boolean,
    val estadoConvite: String?
)

data class ConviteEquipaInfo(
    val idEquipa: Long,
    val nomeEquipa: String,
    val iniciais: String,
    val divisao: String,
    val cidade: String,
    val modalidadeNome: String,
    val mensagem: String?,
    val posicao: String,
    val estadoConvite: String
)

class EquipaRepository {
    private val client = SupabaseClient.client
    private val authRepository = AuthRepository()

    suspend fun listarTodasEquipas(): Result<List<Equipa>> {
        return runCatching {
            client.from("equipa")
                .select()
                .decodeList<Equipa>()
        }
    }

    suspend fun listarEquipasComInfo(): Result<List<EquipaComInfo>> {
        return runCatching {
            val equipasJson = client.from("equipa")
                .select()
                .decodeList<JsonObject>()

            val modalidades = client.from("modalidade")
                .select()
                .decodeList<Modalidade>()

            val estatisticas = runCatching {
                client.from("estatistica_equipa")
                    .select()
                    .decodeList<JsonObject>()
            }.getOrDefault(emptyList())

            val membros = runCatching {
                client.from("membro_equipa")
                    .select()
                    .decodeList<JsonObject>()
            }.getOrDefault(emptyList())

            val utilizadorAtualId = obterIdUtilizadorAtualOuNull()

            val modalidadesPorId = modalidades.associateBy { it.id }
            val estatisticasPorEquipa = estatisticas.groupBy { it.longValue("id_equipa") }

            val membrosAceitesDoUtilizador = membros.filter { membro ->
                val mesmoUtilizador = membro.stringValue("id_utilizador") == utilizadorAtualId
                val estado = membro.stringValue("estado_convite") ?: "pendente"

                mesmoUtilizador && estado.lowercase() == "aceite"
            }

            val idsEquipasDoUtilizador = membrosAceitesDoUtilizador
                .map { it.longValue("id_equipa") }
                .toSet()

            val idsEquipasOndeUtilizadorECapitao = membrosAceitesDoUtilizador
                .filter { membro ->
                    membroEhCapitao(membro)
                }
                .map { it.longValue("id_equipa") }
                .toSet()

            equipasJson.map { equipaJson ->
                val id = equipaJson.longValue("id")
                val nome = equipaJson.stringValue("nome") ?: "Equipa"
                val logoUrl = equipaJson.stringValue("logo_url")
                val idModalidade = equipaJson.longValue("id_modalidade")

                val dadosEquipa = runCatching {
                    equipaJson["dados_equipa"]?.jsonObject
                }.getOrNull()

                val divisao = dadosEquipa?.stringValue("divisao") ?: "Division A"
                val cidade = dadosEquipa?.stringValue("cidade") ?: "Cidade por definir"
                val iniciais = dadosEquipa?.stringValue("sigla") ?: gerarIniciaisEquipa(nome)

                val estatistica = estatisticasPorEquipa[id]?.firstOrNull()

                val vitorias = estatistica?.intValue("vitorias") ?: 0
                val derrotas = estatistica?.intValue("derrotas") ?: 0

                val streakGood = vitorias >= derrotas
                val streak = when {
                    vitorias == 0 && derrotas == 0 -> "-"
                    streakGood -> "W${(vitorias - derrotas).coerceAtLeast(1)}"
                    else -> "L${(derrotas - vitorias).coerceAtLeast(1)}"
                }

                EquipaComInfo(
                    equipa = Equipa(
                        id = id,
                        nome = nome,
                        logoUrl = logoUrl,
                        idModalidade = idModalidade
                    ),
                    modalidadeNome = modalidadesPorId[idModalidade]?.nome ?: "Modalidade",
                    divisao = divisao,
                    iniciais = iniciais,
                    cidade = cidade,
                    vitorias = vitorias,
                    derrotas = derrotas,
                    streak = streak,
                    streakGood = streakGood,
                    utilizadorPertence = id in idsEquipasDoUtilizador,
                    utilizadorCapitao = id in idsEquipasOndeUtilizadorECapitao
                )
            }.sortedWith(
                compareByDescending<EquipaComInfo> {
                    it.utilizadorPertence
                }.thenByDescending {
                    it.utilizadorCapitao
                }.thenBy {
                    it.equipa.nome.lowercase()
                }
            )
        }
    }

    suspend fun obterDetalhesEquipa(idEquipa: Long): Result<EquipaDetalhesInfo> {
        return runCatching {
            val equipaInfo = listarEquipasComInfo()
                .getOrThrow()
                .firstOrNull { it.equipa.id == idEquipa }
                ?: error("Equipa não encontrada.")

            val estatisticas = runCatching {
                client.from("estatistica_equipa")
                    .select()
                    .decodeList<JsonObject>()
            }.getOrDefault(emptyList())

            val estatistica = estatisticas.firstOrNull {
                it.longValue("id_equipa") == idEquipa
            }

            val membrosGestao = listarMembrosGestaoDaEquipa(
                idEquipa = idEquipa,
                incluirPendentes = false
            )

            val membrosDetalhes = membrosGestao.map { membro ->
                MembroEquipaDetalhesInfo(
                    utilizador = membro.utilizador,
                    papel = membro.papel,
                    estadoConvite = membro.estadoConvite,
                    isCaptain = membro.isCaptain,
                    posicao = membro.posicao
                )
            }

            val vitorias = estatistica?.intValue("vitorias") ?: equipaInfo.vitorias
            val derrotas = estatistica?.intValue("derrotas") ?: equipaInfo.derrotas
            val empates = estatistica?.intValue("empates") ?: 0

            val jogosDisputados = estatistica?.intValue("num_jogos")
                ?: estatistica?.intValue("jogos_disputados")
                ?: (vitorias + empates + derrotas)

            val totalGolos = estatistica?.intValue("total_golos")
                ?: estatistica?.intValue("golos")
                ?: estatistica?.intValue("golos_marcados")
                ?: estatistica?.intValue("pontos_marcados")
                ?: 0

            val winRate = if (jogosDisputados > 0) {
                (vitorias.toDouble() / jogosDisputados.toDouble()) * 100.0
            } else {
                0.0
            }

            EquipaDetalhesInfo(
                equipaInfo = equipaInfo.copy(
                    vitorias = vitorias,
                    derrotas = derrotas
                ),
                winRate = winRate,
                totalGolos = totalGolos,
                jogosDisputados = jogosDisputados,
                empates = empates,
                membros = membrosDetalhes
            )
        }
    }

    suspend fun obterGestaoEquipa(idEquipa: Long): Result<EquipaGestaoInfo> {
        return runCatching {
            val equipaInfo = listarEquipasComInfo()
                .getOrThrow()
                .firstOrNull { it.equipa.id == idEquipa }
                ?: error("Equipa não encontrada.")

            if (!equipaInfo.utilizadorCapitao) {
                error("Apenas o capitão pode gerir esta equipa.")
            }

            val membros = listarMembrosGestaoDaEquipa(
                idEquipa = idEquipa,
                incluirPendentes = true
            )

            EquipaGestaoInfo(
                equipaInfo = equipaInfo,
                membros = membros
            )
        }
    }

    suspend fun listarConvitesPendentesDoUtilizador(): Result<List<ConviteEquipaInfo>> {
        return runCatching {
            val idUtilizador = obterIdUtilizadorAtualOuPrimeiroTeste()

            val membros = runCatching {
                client.from("membro_equipa")
                    .select()
                    .decodeList<JsonObject>()
            }.getOrDefault(emptyList())

            val convitesPendentes = membros.filter { membro ->
                val mesmoUtilizador = membro.stringValue("id_utilizador") == idUtilizador
                val estado = membro.stringValue("estado_convite") ?: ""

                mesmoUtilizador && estado.lowercase() == "pendente"
            }

            val equipasInfo = listarEquipasComInfo().getOrThrow()
            val equipasPorId = equipasInfo.associateBy { it.equipa.id }

            convitesPendentes.mapNotNull { convite ->
                val idEquipa = convite.longValue("id_equipa")
                val equipaInfo = equipasPorId[idEquipa] ?: return@mapNotNull null

                ConviteEquipaInfo(
                    idEquipa = idEquipa,
                    nomeEquipa = equipaInfo.equipa.nome,
                    iniciais = equipaInfo.iniciais,
                    divisao = equipaInfo.divisao,
                    cidade = equipaInfo.cidade,
                    modalidadeNome = equipaInfo.modalidadeNome,
                    mensagem = convite.stringValue("mensagem"),
                    posicao = convite.stringValue("posicao") ?: "Forward",
                    estadoConvite = convite.stringValue("estado_convite") ?: "pendente"
                )
            }
        }
    }

    suspend fun aceitarConviteEquipa(idEquipa: Long): Result<Unit> {
        return responderConviteEquipa(
            idEquipa = idEquipa,
            novoEstado = "aceite"
        )
    }

    suspend fun recusarConviteEquipa(idEquipa: Long): Result<Unit> {
        return responderConviteEquipa(
            idEquipa = idEquipa,
            novoEstado = "recusado"
        )
    }

    private suspend fun responderConviteEquipa(
        idEquipa: Long,
        novoEstado: String
    ): Result<Unit> {
        return runCatching {
            val idUtilizador = obterIdUtilizadorAtualOuPrimeiroTeste()

            val updateJson = if (novoEstado == "aceite") {
                JsonObject(
                    mapOf(
                        "estado_convite" to JsonPrimitive("aceite"),
                        "papel" to JsonPrimitive("player"),
                        "is_capitao" to JsonPrimitive(false)
                    )
                )
            } else {
                JsonObject(
                    mapOf(
                        "estado_convite" to JsonPrimitive("recusado"),
                        "is_capitao" to JsonPrimitive(false)
                    )
                )
            }

            client.from("membro_equipa")
                .update(updateJson) {
                    filter {
                        eq("id_equipa", idEquipa)
                        eq("id_utilizador", idUtilizador)
                    }
                }
        }
    }

    suspend fun pesquisarJogadoresParaConvite(
        idEquipa: Long,
        pesquisa: String
    ): Result<List<UtilizadorConviteInfo>> {
        return runCatching {
            val utilizadores = client.from("utilizador")
                .select()
                .decodeList<Utilizador>()

            val membros = runCatching {
                client.from("membro_equipa")
                    .select()
                    .decodeList<JsonObject>()
            }.getOrDefault(emptyList())

            val membrosDaEquipa = membros.filter {
                it.longValue("id_equipa") == idEquipa
            }

            val membrosPorUtilizador = membrosDaEquipa.associateBy {
                it.stringValue("id_utilizador")
            }

            val textoPesquisa = pesquisa.trim().lowercase()

            utilizadores
                .filter { utilizador ->
                    val textoUtilizador =
                        "${utilizador.nome} ${utilizador.username} ${utilizador.email}".lowercase()

                    textoPesquisa.isBlank() || textoUtilizador.contains(textoPesquisa)
                }
                .map { utilizador ->
                    val membro = membrosPorUtilizador[utilizador.id]
                    val estado = membro?.stringValue("estado_convite")

                    val jaExisteRelacao = estado != null &&
                            estado.lowercase() in listOf("aceite", "pendente")

                    UtilizadorConviteInfo(
                        utilizador = utilizador,
                        jaPertenceEquipa = jaExisteRelacao,
                        estadoConvite = estado
                    )
                }
                .sortedWith(
                    compareBy<UtilizadorConviteInfo> {
                        it.jaPertenceEquipa
                    }.thenBy {
                        it.utilizador.nome.lowercase()
                    }
                )
        }
    }

    suspend fun convidarJogadorParaEquipa(
        idEquipa: Long,
        idUtilizador: String,
        mensagem: String? = null,
        posicao: String = "Forward"
    ): Result<Unit> {
        return runCatching {
            val membros = runCatching {
                client.from("membro_equipa")
                    .select()
                    .decodeList<JsonObject>()
            }.getOrDefault(emptyList())

            val membroExistente = membros.firstOrNull {
                it.longValue("id_equipa") == idEquipa &&
                        it.stringValue("id_utilizador") == idUtilizador
            }

            val estadoAtual = membroExistente?.stringValue("estado_convite")

            if (estadoAtual != null && estadoAtual.lowercase() in listOf("aceite", "pendente")) {
                error("Este jogador já pertence à equipa ou já foi convidado.")
            }

            if (membroExistente != null && estadoAtual?.lowercase() == "recusado") {
                client.from("membro_equipa")
                    .update(
                        JsonObject(
                            mapOf(
                                "estado_convite" to JsonPrimitive("pendente"),
                                "papel" to JsonPrimitive("player"),
                                "posicao" to JsonPrimitive(posicao),
                                "is_capitao" to JsonPrimitive(false),
                                "mensagem" to JsonPrimitive(mensagem ?: "")
                            )
                        )
                    ) {
                        filter {
                            eq("id_equipa", idEquipa)
                            eq("id_utilizador", idUtilizador)
                        }
                    }
            } else {
                val novoMembro = NovoMembroEquipaRequest(
                    idEquipa = idEquipa,
                    idUtilizador = idUtilizador,
                    papel = "player",
                    estadoConvite = "pendente",
                    posicao = posicao,
                    isCapitao = false,
                    mensagem = mensagem
                )

                client.from("membro_equipa")
                    .insert(novoMembro)
            }
        }
    }

    suspend fun tornarJogadorCapitao(
        idEquipa: Long,
        idUtilizador: String
    ): Result<Unit> {
        return runCatching {
            client.from("membro_equipa")
                .update(
                    JsonObject(
                        mapOf(
                            "is_capitao" to JsonPrimitive(false),
                            "papel" to JsonPrimitive("player")
                        )
                    )
                ) {
                    filter {
                        eq("id_equipa", idEquipa)
                    }
                }

            client.from("membro_equipa")
                .update(
                    JsonObject(
                        mapOf(
                            "is_capitao" to JsonPrimitive(true),
                            "papel" to JsonPrimitive("capitao"),
                            "estado_convite" to JsonPrimitive("aceite")
                        )
                    )
                ) {
                    filter {
                        eq("id_equipa", idEquipa)
                        eq("id_utilizador", idUtilizador)
                    }
                }
        }
    }

    suspend fun removerJogadorDaEquipa(
        idEquipa: Long,
        idUtilizador: String
    ): Result<Unit> {
        return runCatching {
            client.from("membro_equipa")
                .delete {
                    filter {
                        eq("id_equipa", idEquipa)
                        eq("id_utilizador", idUtilizador)
                    }
                }
        }
    }

    suspend fun alterarPosicaoJogador(
        idEquipa: Long,
        idUtilizador: String,
        novaPosicao: String
    ): Result<Unit> {
        return runCatching {
            client.from("membro_equipa")
                .update(
                    JsonObject(
                        mapOf(
                            "posicao" to JsonPrimitive(novaPosicao)
                        )
                    )
                ) {
                    filter {
                        eq("id_equipa", idEquipa)
                        eq("id_utilizador", idUtilizador)
                    }
                }
        }
    }

    suspend fun criarEquipa(
        nome: String,
        iniciais: String,
        cidade: String,
        modalidadeNome: String
    ): Result<Unit> {
        return runCatching {
            val nomeLimpo = nome.trim()
            val iniciaisLimpas = iniciais.trim().uppercase()
            val cidadeLimpa = cidade.trim()

            if (nomeLimpo.isBlank()) {
                error("Indica o nome da equipa.")
            }

            if (iniciaisLimpas.isBlank()) {
                error("Indica as iniciais da equipa.")
            }

            if (cidadeLimpa.isBlank()) {
                error("Indica a cidade da equipa.")
            }

            val idUtilizador = obterIdUtilizadorAtualOuPrimeiroTeste()
            val idModalidade = obterIdModalidadePorNome(modalidadeNome)

            val dadosEquipa = JsonObject(
                mapOf(
                    "cidade" to JsonPrimitive(cidadeLimpa),
                    "divisao" to JsonPrimitive("Division A"),
                    "sigla" to JsonPrimitive(iniciaisLimpas)
                )
            )

            val novaEquipa = NovaEquipaRequest(
                nome = nomeLimpo,
                dadosEquipa = dadosEquipa,
                logoUrl = null,
                idModalidade = idModalidade
            )

            client.from("equipa")
                .insert(novaEquipa)

            val equipaCriada = client.from("equipa")
                .select {
                    filter {
                        eq("nome", nomeLimpo)
                    }
                }
                .decodeList<Equipa>()
                .maxByOrNull { it.id }
                ?: error("A equipa foi criada, mas não foi possível obter o ID.")

            val novoMembro = NovoMembroEquipaRequest(
                idEquipa = equipaCriada.id,
                idUtilizador = idUtilizador,
                papel = "capitao",
                estadoConvite = "aceite",
                posicao = "Forward",
                isCapitao = true,
                mensagem = null
            )

            client.from("membro_equipa")
                .insert(novoMembro)
        }
    }

    suspend fun obterMinhasEquipas(userId: String): Result<List<MembroEquipa>> {
        return runCatching {
            client.from("membro_equipa")
                .select {
                    filter {
                        eq("id_utilizador", userId)
                    }
                }
                .decodeList<MembroEquipa>()
        }
    }

    private suspend fun listarMembrosGestaoDaEquipa(
        idEquipa: Long,
        incluirPendentes: Boolean
    ): List<MembroEquipaGestaoInfo> {
        val membrosJson = runCatching {
            client.from("membro_equipa")
                .select()
                .decodeList<JsonObject>()
        }.getOrDefault(emptyList())

        val utilizadores = client.from("utilizador")
            .select()
            .decodeList<Utilizador>()

        val utilizadoresPorId = utilizadores.associateBy { it.id }

        val membrosDaEquipa = membrosJson.filter { membro ->
            val pertenceEquipa = membro.longValue("id_equipa") == idEquipa
            val estado = membro.stringValue("estado_convite") ?: "pendente"
            val estadoNormalizado = estado.lowercase()

            val estadoValido = if (incluirPendentes) {
                estadoNormalizado in listOf("aceite", "pendente")
            } else {
                estadoNormalizado == "aceite"
            }

            pertenceEquipa && estadoValido
        }

        return membrosDaEquipa.mapNotNull { membro ->
            val idUtilizador = membro.stringValue("id_utilizador") ?: return@mapNotNull null
            val utilizador = utilizadoresPorId[idUtilizador] ?: return@mapNotNull null

            val papel = membro.stringValue("papel") ?: "player"
            val posicao = membro.stringValue("posicao") ?: "Forward"
            val estadoConvite = membro.stringValue("estado_convite") ?: "pendente"

            val isCaptain = estadoConvite.lowercase() == "aceite" &&
                    membroEhCapitao(membro)

            MembroEquipaGestaoInfo(
                utilizador = utilizador,
                papel = papel,
                posicao = posicao,
                estadoConvite = estadoConvite,
                isCaptain = isCaptain
            )
        }.sortedWith(
            compareByDescending<MembroEquipaGestaoInfo> {
                it.isCaptain
            }.thenBy {
                it.estadoConvite.lowercase() != "aceite"
            }.thenBy {
                it.posicao.lowercase()
            }.thenBy {
                it.utilizador.nome.lowercase()
            }
        )
    }

    private fun membroEhCapitao(membro: JsonObject): Boolean {
        val papel = membro.stringValue("papel") ?: ""

        return membro.booleanValue("is_capitao") ||
                papel.lowercase().contains("capitao") ||
                papel.lowercase().contains("captain")
    }

    private suspend fun obterIdModalidadePorNome(nomeModalidade: String): Long {
        val modalidades = client.from("modalidade")
            .select()
            .decodeList<Modalidade>()

        val nomeNormalizado = nomeModalidade.lowercase()

        val modalidade = modalidades.firstOrNull { modalidade ->
            val nome = modalidade.nome.lowercase()

            when {
                nomeNormalizado.contains("football") || nomeNormalizado.contains("futebol") ->
                    nome.contains("futebol") || nome.contains("football")

                nomeNormalizado.contains("volleyball") || nomeNormalizado.contains("voleibol") ->
                    nome.contains("voleibol") || nome.contains("volleyball")

                nomeNormalizado.contains("basketball") || nomeNormalizado.contains("basquetebol") ->
                    nome.contains("basquetebol") || nome.contains("basketball")

                else -> nome == nomeNormalizado
            }
        }

        return modalidade?.id
            ?: modalidades.firstOrNull()?.id
            ?: error("Não existem modalidades na base de dados.")
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

    private fun gerarIniciaisEquipa(nome: String): String {
        val palavras = nome
            .split(" ")
            .filter { it.isNotBlank() }

        return when {
            palavras.isEmpty() -> "?"
            palavras.size == 1 -> palavras.first().take(2).uppercase()
            else -> palavras.take(2).joinToString("") {
                it.first().uppercaseChar().toString()
            }
        }
    }
}

@Serializable
private data class NovaEquipaRequest(
    val nome: String,

    @SerialName("dados_equipa")
    val dadosEquipa: JsonObject,

    @SerialName("logo_url")
    val logoUrl: String? = null,

    @SerialName("id_modalidade")
    val idModalidade: Long
)

@Serializable
private data class NovoMembroEquipaRequest(
    @SerialName("id_equipa")
    val idEquipa: Long,

    @SerialName("id_utilizador")
    val idUtilizador: String,

    val papel: String = "player",

    @SerialName("estado_convite")
    val estadoConvite: String = "pendente",

    val posicao: String = "Forward",

    @SerialName("is_capitao")
    val isCapitao: Boolean = false,

    val mensagem: String? = null
)

private fun JsonObject.stringValue(key: String): String? {
    return this[key]?.jsonPrimitive?.contentOrNull
}

private fun JsonObject.longValue(key: String): Long {
    return this[key]?.jsonPrimitive?.longOrNull ?: 0L
}

private fun JsonObject.intValue(key: String): Int {
    return this[key]?.jsonPrimitive?.intOrNull ?: 0
}

private fun JsonObject.booleanValue(key: String): Boolean {
    return this[key]?.jsonPrimitive?.booleanOrNull ?: false
}