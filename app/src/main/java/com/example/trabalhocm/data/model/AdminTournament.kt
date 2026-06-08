package com.example.trabalhocm.data.model

data class AdminTournament(
    val id: String,
    val nome: String,
    val organizerName: String,
    val modalidade: String,
    val matchesCount: Int,
    val champion: String,
    val prize: String,
    val season: String,
    val estado: String,
    val teamsCount: Int = 0,
    val maxTeams: Int = 16
)