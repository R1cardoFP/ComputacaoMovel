package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhocm.data.model.Torneio
import com.example.trabalhocm.data.repository.TorneioRepository
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class OrganizerHomeViewModel : ViewModel() {
    private val repository = TorneioRepository()

    var activeTournaments by mutableStateOf<List<Torneio>>(emptyList())
    var isLoading by mutableStateOf(true)

    init {
        carregarDashboard()
    }

    fun carregarDashboard() {
        viewModelScope.launch {
            isLoading = true
            val currentUserId = SupabaseClient.client.auth.currentUserOrNull()?.id

            repository.listarTorneios()
                .onSuccess { lista ->
                    activeTournaments = lista.filter {
                        it.idOrganizador == currentUserId &&
                                (it.estado == "aberto" || it.estado == "em_decorrer")
                    }.take(3)

                    isLoading = false
                }
                .onFailure {
                    isLoading = false
                }
        }
    }
}