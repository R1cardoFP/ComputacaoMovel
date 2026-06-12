package com.example.trabalhocm.data.model

data class AdminPlayerDetails(
    val id: String,
    val nome: String,
    val email: String,
    val papel: String,
    val equipa: String,
    val equipaId: String,
    val equipas: List<String>,
    val initials: String,
    val age: String,
    val height: String,
    val number: String,
    val position: String,
    val goals: Int = 0,
    val assists: Int = 0,
    val points: Int = 0,
    val memberSince: String,
    val lastActive: String,
    val twoFactorEnabled: Boolean = false,
    val accountStatus: String,
    val suspended: Boolean,
    val deleted: Boolean
)