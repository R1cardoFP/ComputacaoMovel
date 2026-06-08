package com.example.trabalhocm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Torneio(
    val id: Long = 0L,
    val nome: String,
    val descricao: String? = null,
    val regras: String? = null,
    val local: String? = null,
    @SerialName("data_inicio") val dataInicio: String,
    @SerialName("data_fim") val dataFim: String? = null,
    val formato: String,
    @SerialName("taxa_inscricao") val taxaInscricao: Double = 0.0,
    val premio: Double = 0.0,
    val estado: String = "rascunho",
    @SerialName("id_organizador") val idOrganizador: String = "",
    @SerialName("id_modalidade") val idModalidade: Long
)