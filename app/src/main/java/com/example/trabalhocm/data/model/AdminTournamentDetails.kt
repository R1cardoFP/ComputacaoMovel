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
    val teamsCount: Int,
    val classificacao: List<AdminTournamentStanding> = emptyList()
)

data class AdminTournamentStanding(
    val posicao: Int,
    val equipa: String,
    val jogos: Int,
    val vitorias: Int,
    val empates: Int,
    val derrotas: Int,
    val pontos: Int
)
