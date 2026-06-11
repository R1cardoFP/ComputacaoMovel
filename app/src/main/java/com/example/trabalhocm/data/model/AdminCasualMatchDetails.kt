package com.example.trabalhocm.data.model

data class AdminCasualMatchDetails(
    val id: String,
    val title: String,
    val description: String,
    val modalidade: String,
    val date: String,
    val time: String,
    val local: String,
    val estado: String,
    val maxPlayers: Int,
    val acceptedPlayers: Int,
    val hostName: String,
    val hostEmail: String,
    val hostInitials: String,
    val hostedMatchesCount: Int,
    val players: List<AdminCasualMatchPlayer>
)

data class AdminCasualMatchPlayer(
    val id: String,
    val nome: String,
    val email: String,
    val initials: String
)