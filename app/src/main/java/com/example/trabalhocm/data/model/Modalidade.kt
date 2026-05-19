package com.example.trabalhocm.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Modalidade(
    val id: Long,
    val nome: String,
    val descricao: String? = null
)