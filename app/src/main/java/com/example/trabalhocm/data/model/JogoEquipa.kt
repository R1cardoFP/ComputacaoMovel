package com.example.trabalhocm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class JogoEquipa(
    @SerialName("id_equipa") val idEquipa: Long,
    @SerialName("id_jogo") val idJogo: Long,
    @SerialName("papel_equipa") val papelEquipa: String,
    @SerialName("pontos_marcados") val pontosMarcados: Int = 0
)