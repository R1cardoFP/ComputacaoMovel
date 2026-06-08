package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhocm.data.repository.TorneioRepository
import com.example.trabalhocm.data.model.Torneio
import kotlinx.coroutines.launch

class CreateTournamentViewModel : ViewModel() {
    private val repository = TorneioRepository()

    var tournamentName by mutableStateOf("")
    var description by mutableStateOf("")
    var selectedSport by mutableStateOf("Football")
    var selectedFormat by mutableStateOf("League System")
    var startDate by mutableStateOf("")
    var endDate by mutableStateOf("")
    var registrationDeadline by mutableStateOf("")
    var maxParticipants by mutableStateOf("32")
    var registrationFormat by mutableStateOf("Open Registration")
    var venue by mutableStateOf("")
    var entryFee by mutableStateOf("0.00")
    var prize1 by mutableStateOf("")
    var prize2 by mutableStateOf("")
    var prize3 by mutableStateOf("")
    var notes by mutableStateOf("")
    var selectedRole by mutableStateOf("Participate as Player")

    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    fun publishTournament(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""

            val p1 = prize1.filter { it.isDigit() }.toLongOrNull() ?: 0L
            val p2 = prize2.filter { it.isDigit() }.toLongOrNull() ?: 0L
            val p3 = prize3.filter { it.isDigit() }.toLongOrNull() ?: 0L
            val totalPrize = (p1 + p2 + p3).toDouble()

            val sportId = when (selectedSport) {
                "Football" -> 1L
                "Basketball" -> 2L
                "Volleyball" -> 3L
                else -> 1L
            }

            val dbFormato = when (selectedFormat) {
                "League System", "Regular Season + Playoffs" -> "liga"
                "Knockout", "Single Elimination Bracket", "Double Elimination" -> "eliminatorias"
                "Group Stage + Knockout", "Pool Play + Playoffs", "3x3 Pool Play" -> "grupos"
                else -> "liga"
            }

            val inputSdf = java.text.SimpleDateFormat("dd/MM/yy", java.util.Locale.getDefault())
            val outputSdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())

            val dbDataInicio = try {
                val date = inputSdf.parse(startDate)
                if (date != null) outputSdf.format(date) else "2026-01-01"
            } catch (e: Exception) { "2026-01-01" }

            val dbDataFim = try {
                val date = inputSdf.parse(endDate)
                if (date != null) outputSdf.format(date) else null
            } catch (e: Exception) { null }

            val torneioDados = Torneio(
                id = 0L,
                nome = tournamentName,
                descricao = description,
                regras = notes,
                local = venue,
                dataInicio = dbDataInicio,
                dataFim = dbDataFim,
                formato = dbFormato,
                taxaInscricao = entryFee.toDoubleOrNull() ?: 0.0,
                premio = totalPrize,
                estado = "aberto",
                idOrganizador = "",
                idModalidade = sportId
            )

            repository.criarTorneio(torneioDados)
                .onSuccess {
                    isLoading = false
                    onSuccess()
                }
                .onFailure { erro ->
                    isLoading = false
                    errorMessage = erro.message ?: "Erro desconhecido ao criar torneio."
                }
        }
    }
}