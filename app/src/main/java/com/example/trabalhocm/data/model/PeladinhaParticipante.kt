package com.example.trabalhocm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PeladinhaParticipante(
    @SerialName("id_utilizador")
    val idUtilizador: String,

    @SerialName("id_peladinha")
    val idPeladinha: Long,

    @SerialName("estado_participacao")
    val estadoParticipacao: String
)