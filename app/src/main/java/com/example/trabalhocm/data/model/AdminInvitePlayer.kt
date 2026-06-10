package com.example.trabalhocm.data.model

data class AdminInvitePlayerData(
    val team: AdminInvitePlayerTeam,
    val availablePlayers: List<AdminInvitePlayerUser>,
    val invitedPlayers: List<AdminInvitePlayerUser>
)

data class AdminInvitePlayerTeam(
    val id: String,
    val nome: String,
    val sigla: String,
    val modalidade: String
)

data class AdminInvitePlayerUser(
    val id: String,
    val nome: String,
    val email: String,
    val initials: String
)