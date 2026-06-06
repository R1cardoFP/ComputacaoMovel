package com.example.trabalhocm.data.model

data class AdminTeam(
    val id: String,
    val nome: String,
    val modalidade: String,
    val divisao: String,
    val playersCount: Int,
    val wins: Int,
    val losses: Int,
    val streak: String
)