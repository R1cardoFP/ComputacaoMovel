package com.example.trabalhocm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EstatisticaJogador(
    @SerialName("id_utilizador") val idUtilizador: String,
    @SerialName("id_modalidade") val idModalidade: Int, // Assumindo que a modalidade é um número (1=Fut, 2=Basq, etc)
    val vitorias: Int = 0,
    val empates: Int = 0,
    val derrotas: Int = 0,
    @SerialName("num_jogos") val numJogos: Int = 0,
    val pontuacao: Int = 0
)