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
    val goals: Int,
    val assists: Int,
    val memberSince: String,
    val lastActive: String,
    val twoFactorEnabled: Boolean,
    val accountStatus: String,
    val suspended: Boolean,
    val deleted: Boolean
)