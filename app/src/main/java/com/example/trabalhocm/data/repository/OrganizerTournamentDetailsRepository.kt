package com.example.trabalhocm.data.repository

import com.example.trabalhocm.data.model.Torneio
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull

data class OrganizerTournamentDetails(
    val torneio: Torneio,
    val numEquipas: Int,
    val classificacao: List<ClassificacaoLinha>
)

data class ClassificacaoLinha(
    val posicao: Int,
    val nomeEquipa: String,
    val numJogos: Int,
    val pontos: Int
)

class OrganizerTournamentDetailsRepository {

    private val client = SupabaseClient.client

    suspend fun carregarDetalhes(idTorneio: Long): Result<OrganizerTournamentDetails> {
        return runCatching {
            val torneio = client.from("torneio")
                .select {
                    filter {
                        eq("id", idTorneio)
                    }
                }
                .decodeSingle<Torneio>()

            val equipasInscritas = client.from("torneio_equipa")
                .select {
                    filter {
                        eq("id_torneio", idTorneio)
                    }
                }
                .decodeList<JsonObject>()

            val linhasClassificacao = client.from("classificacao")
                .select {
                    filter {
                        eq("id_torneio", idTorneio)
                    }
                }
                .decodeList<JsonObject>()

            val equipas = client.from("equipa")
                .select()
                .decodeList<JsonObject>()

            val equipasPorId = equipas.associateBy {
                it.detLongValue("id")
            }

            val classificacao = linhasClassificacao
                .sortedWith(
                    compareBy<JsonObject> {
                        it.detIntValueOrNull("posicao") ?: Int.MAX_VALUE
                    }.thenByDescending {
                        it.detIntValue("pontos")
                    }
                )
                .mapIndexed { indice, linha ->
                    val equipa = equipasPorId[linha.detLongValue("id_equipa")]

                    val vitorias = linha.detIntValue("vitorias")
                    val empates = linha.detIntValue("empates")
                    val derrotas = linha.detIntValue("derrotas")

                    ClassificacaoLinha(
                        posicao = linha.detIntValueOrNull("posicao") ?: (indice + 1),
                        nomeEquipa = equipa?.detStringValue("nome") ?: "Equipa",
                        numJogos = vitorias + empates + derrotas,
                        pontos = linha.detIntValue("pontos")
                    )
                }

            OrganizerTournamentDetails(
                torneio = torneio,
                numEquipas = equipasInscritas.size,
                classificacao = classificacao
            )
        }
    }
}

private fun JsonObject.detStringValue(key: String): String? {
    return this[key]?.jsonPrimitive?.contentOrNull
}

private fun JsonObject.detLongValue(key: String): Long {
    return this[key]?.jsonPrimitive?.longOrNull ?: 0L
}

private fun JsonObject.detIntValue(key: String): Int {
    return this[key]?.jsonPrimitive?.intOrNull ?: 0
}

private fun JsonObject.detIntValueOrNull(key: String): Int? {
    return this[key]?.jsonPrimitive?.intOrNull
}
