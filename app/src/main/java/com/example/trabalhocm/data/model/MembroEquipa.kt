package com.example.trabalhocm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MembroEquipa(
    @SerialName("id_equipa") val idEquipa: Long,
    @SerialName("id_utilizador") val idUtilizador: String,
    @SerialName("estado_convite") val estadoConvite: String
)