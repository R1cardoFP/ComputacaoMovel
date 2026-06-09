package com.example.trabalhocm.data.model

data class AdminManageTeam(
    val id: String,
    val nome: String,
    val sigla: String,
    val modalidade: String,
    val playersCount: Int,
    val players: List<AdminManageTeamPlayer>
)

data class AdminManageTeamPlayer(
    val id: String,
    val nome: String,
    val email: String,
    val initials: String
)