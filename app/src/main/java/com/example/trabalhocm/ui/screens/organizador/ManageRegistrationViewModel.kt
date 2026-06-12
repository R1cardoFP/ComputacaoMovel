package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhocm.data.model.AdminManageRegistrationData
import com.example.trabalhocm.data.repository.AdminRegistrationRepository
import kotlinx.coroutines.launch

class ManageRegistrationViewModel : ViewModel() {
    private val repository = AdminRegistrationRepository()

    private var torneioId: Long = 0L

    var dados by mutableStateOf<AdminManageRegistrationData?>(null)
    var isLoading by mutableStateOf(true)
    var isProcessing by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    fun carregar(idTorneio: Long) {
        torneioId = idTorneio
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""

            repository.carregarGestaoInscricoes(idTorneio.toString())
                .onSuccess {
                    dados = it
                    isLoading = false
                }
                .onFailure { erro ->
                    errorMessage = erro.message ?: "Erro ao carregar inscrições."
                    isLoading = false
                }
        }
    }

    fun aprovar(registrationId: String) = executarAcao {
        repository.aprovarInscricao(registrationId)
    }

    fun rejeitar(registrationId: String) = executarAcao {
        repository.rejeitarInscricao(registrationId)
    }

    fun remover(registrationId: String) = executarAcao {
        repository.removerInscricao(registrationId)
    }

    private fun executarAcao(acao: suspend () -> Result<Unit>) {
        viewModelScope.launch {
            isProcessing = true
            errorMessage = ""

            acao()
                .onSuccess {
                    isProcessing = false
                    carregar(torneioId)
                }
                .onFailure { erro ->
                    isProcessing = false
                    errorMessage = erro.message ?: "Não foi possível concluir a ação."
                }
        }
    }
}
