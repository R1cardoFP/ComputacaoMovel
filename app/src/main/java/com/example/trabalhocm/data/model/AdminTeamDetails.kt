package com.example.trabalhocm.data.model

data class AdminTeamDetails(
    val id: String,
    val nome: String,
    val sigla: String,
    val modalidade: String,
    val local: String,
    val seasonWinRate: String,
    val totalGoals: Int,
    val matchesPlayed: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val players: List<AdminTeamPlayer>
)

data class AdminTeamPlayer(
    val id: String,
    val nome: String,
    val email: String,
    val isCaptain: Boolean = false
)