package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhocm.data.repository.JogoRepository
import com.example.trabalhocm.data.repository.MatchControlInfo
import kotlinx.coroutines.launch

class LiveMatchControlViewModel : ViewModel() {
    private val repository = JogoRepository()

    var jogo by mutableStateOf<MatchControlInfo?>(null)
    var isLoading by mutableStateOf(true)
    var isProcessing by mutableStateOf(false)
    var errorMessage by mutableStateOf("")
    var terminado by mutableStateOf(false)

    /** idJogo > 0 carrega esse jogo; idJogo == 0 procura o primeiro em direto. */
    fun carregar(idJogo: Long) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""

            val idAlvo = if (idJogo > 0L) {
                idJogo
            } else {
                repository.obterPrimeiroJogoEmDireto().getOrNull() ?: 0L
            }

            if (idAlvo == 0L) {
                jogo = null
                isLoading = false
                return@launch
            }

            repository.obterControloJogo(idAlvo)
                .onSuccess {
                    jogo = it
                    isLoading = false
                }
                .onFailure { erro ->
                    errorMessage = erro.message ?: "Erro ao carregar jogo."
                    isLoading = false
                }
        }
    }

    fun alterarPontos(casa: Boolean, delta: Int) {
        val atual = jogo ?: return
        val idEquipa = if (casa) atual.idEquipaCasa else atual.idEquipaFora
        val novos = ((if (casa) atual.pontosCasa else atual.pontosFora) + delta).coerceAtLeast(0)

        // Atualização otimista para a UI responder logo
        jogo = if (casa) atual.copy(pontosCasa = novos) else atual.copy(pontosFora = novos)

        viewModelScope.launch {
            repository.atualizarPontos(atual.idJogo, idEquipa, novos)
                .onFailure { erro ->
                    errorMessage = erro.message ?: "Erro ao atualizar pontos."
                    carregar(atual.idJogo)
                }
        }
    }

    fun iniciarJogo() = mudarEstado("em_direto")

    fun terminarJogo() {
        val atual = jogo ?: return
        viewModelScope.launch {
            isProcessing = true
            errorMessage = ""

            repository.finalizarJogo(atual.idJogo)
                .onSuccess {
                    isProcessing = false
                    jogo = atual.copy(estado = "terminado")
                    terminado = true
                }
                .onFailure { erro ->
                    isProcessing = false
                    errorMessage = erro.message ?: "Erro ao terminar o jogo."
                }
        }
    }

    private fun mudarEstado(novoEstado: String, resultadoFinal: String? = null) {
        val atual = jogo ?: return
        viewModelScope.launch {
            isProcessing = true
            errorMessage = ""

            repository.atualizarEstadoJogo(atual.idJogo, novoEstado, resultadoFinal)
                .onSuccess {
                    isProcessing = false
                    jogo = atual.copy(estado = novoEstado)
                }
                .onFailure { erro ->
                    isProcessing = false
                    errorMessage = erro.message ?: "Erro ao mudar o estado do jogo."
                }
        }
    }
}
