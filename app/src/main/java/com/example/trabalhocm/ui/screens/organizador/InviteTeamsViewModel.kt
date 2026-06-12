package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhocm.data.model.AdminInviteTeamsData
import com.example.trabalhocm.data.repository.AdminInviteTeamRepository
import kotlinx.coroutines.launch

class InviteTeamsViewModel : ViewModel() {
    private val repository = AdminInviteTeamRepository()

    private var torneioId: Long = 0L

    var dados by mutableStateOf<AdminInviteTeamsData?>(null)
    var isLoading by mutableStateOf(true)
    var isProcessing by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    fun carregar(idTorneio: Long) {
        torneioId = idTorneio
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""

            repository.carregarEquipasParaConvite(idTorneio.toString())
                .onSuccess {
                    dados = it
                    isLoading = false
                }
                .onFailure { erro ->
                    errorMessage = erro.message ?: "Erro ao carregar equipas."
                    isLoading = false
                }
        }
    }

    fun convidar(teamId: String) {
        viewModelScope.launch {
            isProcessing = true
            errorMessage = ""

            repository.convidarEquipa(torneioId.toString(), teamId)
                .onSuccess {
                    isProcessing = false
                    carregar(torneioId)
                }
                .onFailure { erro ->
                    isProcessing = false
                    errorMessage = erro.message ?: "Não foi possível enviar o convite."
                }
        }
    }
}
