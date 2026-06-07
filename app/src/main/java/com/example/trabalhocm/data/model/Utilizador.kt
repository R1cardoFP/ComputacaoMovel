package com.example.trabalhocm.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Utilizador(
    val id: String,
    val username: String,
    val nome: String,
    val email: String,

    @SerialName("foto_url")
    val fotoUrl: String? = null,

    @SerialName("raio_km")
    val raioKm: Int = 25,

    @SerialName("created_at")
    val dataCriacao: String? = null


)