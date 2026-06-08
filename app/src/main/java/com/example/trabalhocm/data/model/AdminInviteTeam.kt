package com.example.trabalhocm.data.model

data class AdminInviteTeamsData(
    val tournamentId: String,
    val tournamentName: String,
    val slotsRemaining: Int,
    val maxTeams: Int,
    val teams: List<AdminInviteTeam>,
    val sentInvitations: List<AdminInviteTeam>
)

data class AdminInviteTeam(
    val teamId: String,
    val teamName: String,
    val captainName: String,
    val division: String,
    val isInvited: Boolean,
    val registrationStatus: String,
    val invitedAgo: String
)