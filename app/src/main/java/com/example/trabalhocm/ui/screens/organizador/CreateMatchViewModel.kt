package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhocm.data.repository.EquipaSimples
import com.example.trabalhocm.data.repository.JogoRepository
import kotlinx.coroutines.launch

class CreateMatchViewModel : ViewModel() {
    private val repository = JogoRepository()

    private var torneioId: Long = 0L

    var equipas by mutableStateOf<List<EquipaSimples>>(emptyList())
    var isLoading by mutableStateOf(true)
    var isSaving by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    var equipaCasa by mutableStateOf<EquipaSimples?>(null)
    var equipaFora by mutableStateOf<EquipaSimples?>(null)
    var data by mutableStateOf("")
    var hora by mutableStateOf("")
    var local by mutableStateOf("")

    fun carregar(idTorneio: Long) {
        torneioId = idTorneio
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""

            repository.listarEquipasAprovadas(idTorneio)
                .onSuccess {
                    equipas = it
                    isLoading = false
                }
                .onFailure { erro ->
                    errorMessage = erro.message ?: "Erro ao carregar equipas."
                    isLoading = false
                }
        }
    }

    fun criarJogo(onSuccess: (Long) -> Unit) {
        val casa = equipaCasa
        val fora = equipaFora

        if (casa == null || fora == null || casa.id == fora.id) {
            errorMessage = "SELECT_BOTH"
            return
        }

        if (data.isBlank() || hora.isBlank()) {
            errorMessage = "MISSING_DATE"
            return
        }

        viewModelScope.launch {
            isSaving = true
            errorMessage = ""

            repository.criarJogo(
                idTorneio = torneioId,
                idEquipaCasa = casa.id,
                idEquipaFora = fora.id,
                data = data.trim(),
                hora = hora.trim(),
                local = local.ifBlank { null }
            )
                .onSuccess { idJogo ->
                    isSaving = false
                    onSuccess(idJogo)
                }
                .onFailure { erro ->
                    isSaving = false
                    errorMessage = erro.message ?: "Erro ao criar jogo."
                }
        }
    }
}
