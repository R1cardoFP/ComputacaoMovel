package com.example.trabalhocm.data.model

data class AdminNotification(
    val id: String,
    val title: String,
    val description: String,
    val type: String,
    val actionText: String?,
    val unread: Boolean,
    val timeText: String,
    val createdAt: String,
    val userId: String? = null,
    val tournamentId: String? = null,
    val teamId: String? = null
)