package com.example.trabalhocm.data.model

data class AdminTournamentDetails(
    val id: String,
    val nome: String,
    val descricao: String,
    val estado: String,
    val modalidade: String,
    val organizerName: String,
    val dataInicio: String,
    val dataFim: String,
    val inscricoesFecham: String,
    val formato: String,
    val local: String,
    val premio: String,
    val season: String,
    val teamsCount: Int
)