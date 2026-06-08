package com.example.trabalhocm.data.model

data class AdminManageRegistrationData(
    val tournamentId: String,
    val tournamentName: String,
    val status: String,
    val registeredTeams: Int,
    val maxTeams: Int,
    val registrationClosesText: String,
    val pendingTeams: List<AdminRegistrationTeam>,
    val approvedTeams: List<AdminRegistrationTeam>,
    val rejectedTeams: List<AdminRegistrationTeam>
)

data class AdminRegistrationTeam(
    val registrationId: String,
    val teamId: String,
    val teamName: String,
    val captainName: String,
    val division: String,
    val playersCount: Int,
    val maxPlayers: Int,
    val winRate: String,
    val appliedAgo: String,
    val paymentStatus: String
)