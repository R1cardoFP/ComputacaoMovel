package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.AdminTournament
import com.example.trabalhocm.data.model.AdminTournamentDetails
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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

    suspend fun obterDetalhesTorneio(tournamentId: String): Result<AdminTournamentDetails> {
        return runCatching {
            val id = tournamentId.toIntOrNull()
                ?: throw Exception("ID do torneio inválido.")

            val torneio = client.from("torneio")
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingle<TorneioArquivoAdminDto>()

            val modalidadeNome = torneio.idModalidade?.let { idModalidade ->
                client.from("modalidade")
                    .select {
                        filter {
                            eq("id", idModalidade)
                        }
                    }
                    .decodeSingle<ModalidadeArquivoAdminDto>()
                    .nome
            } ?: "Unknown"

            val organizadorNome = torneio.idOrganizador?.let { idOrganizador ->
                client.from("utilizador")
                    .select {
                        filter {
                            eq("id", idOrganizador)
                        }
                    }
                    .decodeSingle<OrganizadorArquivoAdminDto>()
                    .nome
            } ?: "Unknown organizer"

            AdminTournamentDetails(
                id = torneio.id.toString(),
                nome = torneio.nome,
                descricao = torneio.descricao ?: "Sem descrição disponível.",
                estado = torneio.estado ?: "IN PROGRESS",
                modalidade = modalidadeNome.uppercase(),
                organizerName = organizadorNome,
                dataInicio = formatDateLong(torneio.dataInicio),
                dataFim = formatDateLong(torneio.dataFim),
                inscricoesFecham = formatDateLong(torneio.dataInicio),
                formato = torneio.formato ?: "Não definido",
                local = torneio.local ?: "Local não definido",
                premio = formatPrize(torneio.premio),
                season = formatSeason(torneio.dataInicio, torneio.dataFim),
                teamsCount = 0
            )
        }
    }

    suspend fun apagarTorneio(tournamentId: String): Result<Unit> {
        return runCatching {
            val id = tournamentId.toIntOrNull()
                ?: throw Exception("ID do torneio inválido.")

            client.from("torneio")
                .delete {
                    filter {
                        eq("id", id)
                    }
                }
        }
    }

    suspend fun atualizarTorneioAdmin(
        tournamentId: String,
        nome: String,
        modalidade: String,
        dataInicio: String,
        dataFim: String,
        formato: String,
        local: String,
        descricao: String,
        premio: String,
        estado: String
    ): Result<Unit> {
        return runCatching {
            val id = tournamentId.toIntOrNull()
                ?: throw Exception("ID do torneio inválido.")

            val premioValor = limparPremio(premio)
            val idModalidade = obterIdModalidadePorNome(modalidade)
                ?: throw Exception("Modalidade inválida: $modalidade")

            val dados = buildJsonObject {
                put("nome", nome)
                put("data_inicio", normalizarData(dataInicio))
                put("data_fim", normalizarData(dataFim))
                put("formato", normalizarFormato(formato))
                put("local", local)
                put("descricao", descricao)
                put("premio", premioValor)
                put("estado", calcularEstadoAutomatico(dataInicio, dataFim, estado))
                put("id_modalidade", idModalidade)
            }

            client.from("torneio")
                .update(dados) {
                    filter {
                        eq("id", id)
                    }
                }
        }
    }

    suspend fun cancelarTorneio(tournamentId: String): Result<Unit> {
        return runCatching {
            val id = tournamentId.toIntOrNull()
                ?: throw Exception("ID do torneio inválido.")

            val dados = buildJsonObject {
                put("estado", "cancelado")
            }

            client.from("torneio")
                .update(dados) {
                    filter {
                        eq("id", id)
                    }
                }
        }
    }

    private suspend fun obterIdModalidadePorNome(nomeModalidade: String): Int? {
        val modalidades = client.from("modalidade")
            .select()
            .decodeList<ModalidadeArquivoAdminDto>()

        val texto = nomeModalidade.lowercase().trim()

        val nomePretendido = when {
            texto.contains("fut") ||
                    texto.contains("foot") ||
                    texto.contains("soccer") -> "futebol"

            texto.contains("basquet") ||
                    texto.contains("basket") -> "basquetebol"

            texto.contains("volei") ||
                    texto.contains("voleibol") ||
                    texto.contains("volley") -> "voleibol"

            else -> texto
        }

        return modalidades.firstOrNull { modalidade ->
            modalidade.nome.lowercase().trim() == nomePretendido
        }?.id
    }

    private fun limparPremio(valor: String): Double {
        val texto = valor
            .replace("€", "")
            .replace("k", "000", ignoreCase = true)
            .replace(",", ".")
            .replace(" ", "")
            .trim()

        return texto.toDoubleOrNull() ?: 0.0
    }

    private fun normalizarData(data: String): String {
        val limpa = data.trim()

        if (limpa.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            return limpa
        }

        val partes = limpa.split(" ")
        if (partes.size == 3) {
            val dia = partes[0].padStart(2, '0')
            val mes = when (partes[1].lowercase()) {
                "jan" -> "01"
                "feb" -> "02"
                "mar" -> "03"
                "apr" -> "04"
                "may" -> "05"
                "jun" -> "06"
                "jul" -> "07"
                "aug" -> "08"
                "sep" -> "09"
                "oct" -> "10"
                "nov" -> "11"
                "dec" -> "12"
                else -> "01"
            }
            val ano = partes[2]

            return "$ano-$mes-$dia"
        }

        return limpa
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

    private fun formatDateLong(data: String?): String {
        if (data.isNullOrBlank()) {
            return "Não definido"
        }

        return try {
            val partes = data.take(10).split("-")
            if (partes.size != 3) return data.take(10)

            val ano = partes[0]
            val mes = partes[1]
            val dia = partes[2]

            val mesTexto = when (mes) {
                "01" -> "Jan"
                "02" -> "Feb"
                "03" -> "Mar"
                "04" -> "Apr"
                "05" -> "May"
                "06" -> "Jun"
                "07" -> "Jul"
                "08" -> "Aug"
                "09" -> "Sep"
                "10" -> "Oct"
                "11" -> "Nov"
                "12" -> "Dec"
                else -> mes
            }

            "$dia $mesTexto $ano"
        } catch (e: Exception) {
            data.take(10)
        }
    }

    private fun normalizarFormato(formato: String): String {
        val texto = formato.lowercase().trim()

        return when {
            texto.contains("league") || texto.contains("liga") -> "liga"
            texto.contains("knockout") || texto.contains("elimin") -> "eliminatorias"
            texto.contains("groups") || texto.contains("grupo") -> "grupos"
            else -> "liga"
        }
    }

    private fun normalizarEstado(estado: String): String {
        val texto = estado.lowercase().trim()

        return when {
            texto.contains("rascunho") ||
                    texto.contains("draft") -> "rascunho"

            texto.contains("open") ||
                    texto.contains("aberto") -> "aberto"

            texto.contains("live") ||
                    texto.contains("curso") ||
                    texto.contains("decorrer") ||
                    texto.contains("progress") ||
                    texto.contains("andamento") -> "em_decorrer"

            texto.contains("completed") ||
                    texto.contains("complete") ||
                    texto.contains("terminado") ||
                    texto.contains("concluido") ||
                    texto.contains("concluído") ||
                    texto.contains("archived") -> "terminado"

            texto.contains("cancelled") ||
                    texto.contains("canceled") ||
                    texto.contains("cancelado") -> "cancelado"

            else -> "aberto"
        }
    }

    private fun calcularEstadoAutomatico(
        dataInicio: String,
        dataFim: String,
        estadoAtual: String
    ): String {
        val estadoNormalizado = estadoAtual.lowercase().trim()

        if (estadoNormalizado == "cancelado") {
            return "cancelado"
        }

        return try {
            val hoje = java.time.LocalDate.now()
            val inicio = java.time.LocalDate.parse(normalizarData(dataInicio))
            val fim = java.time.LocalDate.parse(normalizarData(dataFim))

            when {
                hoje.isBefore(inicio) -> "aberto"
                hoje.isAfter(fim) -> "terminado"
                else -> "em_decorrer"
            }
        } catch (e: Exception) {
            "aberto"
        }
    }
}

@Serializable
private data class TorneioArquivoAdminDto(
    val id: Int,
    val nome: String,
    val descricao: String? = null,
    val formato: String? = null,
    val local: String? = null,

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