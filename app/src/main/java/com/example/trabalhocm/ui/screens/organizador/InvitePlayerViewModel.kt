package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhocm.data.repository.EquipaRepository
import com.example.trabalhocm.data.repository.UtilizadorConviteInfo
import kotlinx.coroutines.launch

class InvitePlayerViewModel : ViewModel() {
    private val repository = EquipaRepository()

    private var idEquipa: Long = 0L

    var resultados by mutableStateOf<List<UtilizadorConviteInfo>>(emptyList())
    var isLoading by mutableStateOf(true)
    var isProcessing by mutableStateOf(false)
    var errorMessage by mutableStateOf("")
    var sucesso by mutableStateOf(false)

    fun iniciar(idEquipa: Long) {
        this.idEquipa = idEquipa
        pesquisar("")
    }

    fun pesquisar(query: String) {
        viewModelScope.launch {
            isLoading = true
            repository.pesquisarJogadoresParaConvite(idEquipa, query)
                .onSuccess {
                    resultados = it
                    isLoading = false
                }
                .onFailure { erro ->
                    errorMessage = erro.message ?: "Erro ao procurar jogadores."
                    isLoading = false
                }
        }
    }

    fun convidar(idUtilizador: String, posicao: String, mensagem: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isProcessing = true
            errorMessage = ""
            sucesso = false

            repository.convidarJogadorParaEquipa(
                idEquipa = idEquipa,
                idUtilizador = idUtilizador,
                mensagem = mensagem.ifBlank { null },
                posicao = posicao
            )
                .onSuccess {
                    isProcessing = false
                    sucesso = true
                    pesquisar("")
                    onSuccess()
                }
                .onFailure { erro ->
                    isProcessing = false
                    errorMessage = erro.message ?: "Não foi possível enviar o convite."
                }
        }
    }
}
