package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhocm.data.repository.EquipaDetalhesInfo
import com.example.trabalhocm.data.repository.EquipaRepository
import kotlinx.coroutines.launch

class TeamDetailsViewModel : ViewModel() {
    private val repository = EquipaRepository()

    var detalhes by mutableStateOf<EquipaDetalhesInfo?>(null)
    var isLoading by mutableStateOf(true)
    var errorMessage by mutableStateOf("")

    fun carregar(idEquipa: Long) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""

            repository.obterDetalhesEquipa(idEquipa)
                .onSuccess {
                    detalhes = it
                    isLoading = false
                }
                .onFailure { erro ->
                    errorMessage = erro.message ?: "Erro ao carregar equipa."
                    isLoading = false
                }
        }
    }
}
