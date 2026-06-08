package com.example.trabalhocm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Equipa(
    val id: Long = 0L,
    val nome: String,
    @SerialName("logo_url") val logoUrl: String? = null,
    @SerialName("id_modalidade") val idModalidade: Long
)