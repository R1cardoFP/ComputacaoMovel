package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhocm.data.repository.OrganizerTournamentDetails
import com.example.trabalhocm.data.repository.OrganizerTournamentDetailsRepository
import kotlinx.coroutines.launch

class OrganizerTournamentDetailsViewModel : ViewModel() {
    private val repository = OrganizerTournamentDetailsRepository()

    var detalhes by mutableStateOf<OrganizerTournamentDetails?>(null)
    var isLoading by mutableStateOf(true)
    var errorMessage by mutableStateOf("")

    fun carregarDetalhes(idTorneio: Long) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""

            repository.carregarDetalhes(idTorneio)
                .onSuccess { dados ->
                    detalhes = dados
                    isLoading = false
                }
                .onFailure { erro ->
                    errorMessage = erro.message ?: "Erro ao carregar detalhes do torneio."
                    isLoading = false
                }
        }
    }
}
