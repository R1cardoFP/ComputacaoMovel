package com.example.trabalhocm.data.model

data class AdminTeam(
    val id: String,
    val nome: String,
    val modalidade: String,
    val divisao: String,
    val playersCount: Int,
    val wins: Int = 0,
    val losses: Int = 0,
    val streak: String = "W0"
)