package com.example.trabalhocm.data.model

data class AdminCasualMatch(
    val id: String,
    val title: String,
    val modalidade: String,
    val local: String,
    val date: String,
    val time: String,
    val status: String,
    val statusFilter: String,
    val acceptedPlayers: Int,
    val maxPlayers: Int,
    val sectionTitle: String,
    val isLive: Boolean,
    val homeTeamName: String = "Team A",
    val awayTeamName: String = "Team B",
    val homeScore: Int = 0,
    val awayScore: Int = 0
)