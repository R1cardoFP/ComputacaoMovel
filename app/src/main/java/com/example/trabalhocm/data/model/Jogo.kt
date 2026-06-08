package com.example.trabalhocm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Jogo(
    val id: Long = 0L,
    @SerialName("id_torneio") val idTorneio: Long,
    val data: String,
    val hora: String,
    val local: String? = null,
    @SerialName("estado_jogo") val estadoJogo: String = "agendado",
    @SerialName("resultado_final") val resultadoFinal: String? = null
)