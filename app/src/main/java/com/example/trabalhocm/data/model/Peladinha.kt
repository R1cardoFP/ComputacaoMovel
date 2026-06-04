package com.example.trabalhocm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Peladinha(
    val id: Long,

    @SerialName("id_modalidade")
    val idModalidade: Long,

    @SerialName("id_organizador")
    val idOrganizador: String? = null,

    val data: String? = null,
    val hora: String? = null,
    val preco: Double? = null,
    val descricao: String? = null,

    @SerialName("max_jogadores")
    val maxJogadores: Int = 0,

    val estado: String,
    val local: String? = null
)