package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhocm.data.repository.TorneioRepository
import com.example.trabalhocm.data.model.Torneio
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

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

            // 1. Converter os prémios
            val p1 = prize1.filter { it.isDigit() }.toLongOrNull() ?: 0L
            val p2 = prize2.filter { it.isDigit() }.toLongOrNull() ?: 0L
            val p3 = prize3.filter { it.isDigit() }.toLongOrNull() ?: 0L
            val totalPrize = (p1 + p2 + p3).toDouble()

            // 2. Mapear a modalidade corretamente para a BD
            val sportId = when (selectedSport) {
                "Football" -> 1L
                "Basketball" -> 2L
                "Volleyball" -> 3L
                else -> 1L
            }

            // 3. Mapear o formato do torneio
            val dbFormato = when (selectedFormat) {
                "League System", "Regular Season + Playoffs" -> "liga"
                "Knockout", "Single Elimination Bracket", "Double Elimination" -> "eliminatorias"
                "Group Stage + Knockout", "Pool Play + Playoffs", "3x3 Pool Play" -> "grupos"
                else -> "liga"
            }

            // 4. CORREÇÃO CRÍTICA DAS DATAS
            // O calendário do Compose manda no formato "dd/MM/yy" mas o Supabase precisa de "yyyy-MM-dd"
            val inputSdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            val outputSdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val dbDataInicio = try {
                if (startDate.isNotBlank()) {
                    val date = inputSdf.parse(startDate)
                    if (date != null) outputSdf.format(date) else "2026-01-01"
                } else {
                    "2026-01-01"
                }
            } catch (e: Exception) { "2026-01-01" }

            val dbDataFim = try {
                if (endDate.isNotBlank()) {
                    val date = inputSdf.parse(endDate)
                    if (date != null) outputSdf.format(date) else null
                } else {
                    null
                }
            } catch (e: Exception) { null }

            val torneioDados = Torneio(
                id = 0L,
                nome = tournamentName.ifBlank { "Untitled Tournament" },
                descricao = description,
                regras = notes,
                local = venue.ifBlank { "Local por definir" },
                dataInicio = dbDataInicio,
                dataFim = dbDataFim,
                formato = dbFormato,
                taxaInscricao = entryFee.replace(",", ".").toDoubleOrNull() ?: 0.0,
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