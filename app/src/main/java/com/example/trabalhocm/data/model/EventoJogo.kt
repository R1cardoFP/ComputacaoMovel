package com.example.trabalhocm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EventoJogo(
    val id: Long = 0L,
    @SerialName("id_jogo") val idJogo: Long,
    @SerialName("id_jogador") val idJogador: String,
    @SerialName("tipo_evento") val tipoEvento: String,
    val minuto: Int
)