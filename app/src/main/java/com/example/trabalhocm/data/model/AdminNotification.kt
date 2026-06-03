package com.example.trabalhocm.data.model

data class AdminNotification(
    val id: String,
    val title: String,
    val description: String,
    val type: String,
    val actionText: String?,
    val unread: Boolean,
    val timeText: String
)