package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhocm.data.repository.EquipaGestaoInfo
import com.example.trabalhocm.data.repository.EquipaRepository
import kotlinx.coroutines.launch

class TeamManagementViewModel : ViewModel() {
    private val repository = EquipaRepository()

    private var idEquipa: Long = 0L

    var gestao by mutableStateOf<EquipaGestaoInfo?>(null)
    var isLoading by mutableStateOf(true)
    var isProcessing by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    fun carregar(idEquipa: Long) {
        this.idEquipa = idEquipa
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""

            repository.obterGestaoEquipa(idEquipa)
                .onSuccess {
                    gestao = it
                    isLoading = false
                }
                .onFailure { erro ->
                    errorMessage = erro.message ?: "Erro ao carregar a equipa."
                    isLoading = false
                }
        }
    }

    fun tornarCapitao(idUtilizador: String) = executarAcao {
        repository.tornarJogadorCapitao(idEquipa, idUtilizador)
    }

    fun removerMembro(idUtilizador: String) = executarAcao {
        repository.removerJogadorDaEquipa(idEquipa, idUtilizador)
    }

    private fun executarAcao(acao: suspend () -> Result<Unit>) {
        viewModelScope.launch {
            isProcessing = true
            errorMessage = ""

            acao()
                .onSuccess {
                    isProcessing = false
                    carregar(idEquipa)
                }
                .onFailure { erro ->
                    isProcessing = false
                    errorMessage = erro.message ?: "Não foi possível concluir a ação."
                }
        }
    }
}
