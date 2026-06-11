package com.example.trabalhocm.data.model

data class AdminLiveCasualMatch(
    val id: String,
    val title: String,
    val modalidade: String,
    val homeTeamName: String,
    val awayTeamName: String,
    val homeScore: Int,
    val awayScore: Int,
    val currentMinute: Int,
    val isCanceled: Boolean,
    val players: List<AdminLiveCasualPlayer>,
    val points: List<AdminLiveCasualPoint>
)

data class AdminLiveCasualPlayer(
    val id: String,
    val nome: String,
    val email: String,
    val initials: String,
    val teamSide: String,
    val teamName: String
)

data class AdminLiveCasualPoint(
    val id: String,
    val minute: Int,
    val scorerName: String,
    val scorerInitials: String,
    val teamSide: String,
    val teamName: String
)