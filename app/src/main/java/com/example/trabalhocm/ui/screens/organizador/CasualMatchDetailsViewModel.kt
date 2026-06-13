package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhocm.data.repository.PeladinhaDetalhesInfo
import com.example.trabalhocm.data.repository.PeladinhaRepository
import kotlinx.coroutines.launch

class CasualMatchDetailsViewModel : ViewModel() {
    private val repository = PeladinhaRepository()

    var detalhes by mutableStateOf<PeladinhaDetalhesInfo?>(null)
    var isLoading by mutableStateOf(true)
    var errorMessage by mutableStateOf("")

    fun carregar(idPeladinha: Long) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""

            repository.obterDetalhesPeladinha(idPeladinha)
                .onSuccess {
                    detalhes = it
                    isLoading = false
                }
                .onFailure { erro ->
                    errorMessage = erro.message ?: "Erro ao carregar a peladinha."
                    isLoading = false
                }
        }
    }
}
