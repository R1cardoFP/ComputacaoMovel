package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhocm.data.remote.SupabaseClient
import com.example.trabalhocm.data.repository.AuthRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

data class UiNotificacao(
    val id: Long,
    val titulo: String,
    val mensagem: String,
    val tipo: String,
    val data: String,
    val lida: Boolean
)

class OrganizerNotificationsViewModel : ViewModel() {
    private val authRepository = AuthRepository()

    var notificacoes by mutableStateOf<List<UiNotificacao>>(emptyList())
    var isLoading by mutableStateOf(true)

    init {
        carregar()
    }

    fun carregar() {
        viewModelScope.launch {
            isLoading = true
            val userId = SupabaseClient.client.auth.currentUserOrNull()?.id

            if (userId == null) {
                isLoading = false
                return@launch
            }

            authRepository.obterNotificacoes(userId)
                .onSuccess { lista ->
                    notificacoes = lista.map {
                        UiNotificacao(
                            id = it.id,
                            titulo = it.titulo,
                            mensagem = it.mensagem,
                            tipo = it.tipo,
                            data = it.data,
                            lida = it.lida
                        )
                    }
                    isLoading = false
                }
                .onFailure {
                    isLoading = false
                }
        }
    }
}
