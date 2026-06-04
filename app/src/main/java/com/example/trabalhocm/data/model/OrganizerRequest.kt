package com.example.trabalhocm.data.model

data class OrganizerRequest(
    val id: String,
    val userId: String,
    val name: String,
    val username: String,
    val email: String,
    val sport: String,
    val frequency: String,
    val experience: String,
    val applied: String,
    val description: String,
    val status: String,
    val createdAt: String,
    val updatedAt: String
)