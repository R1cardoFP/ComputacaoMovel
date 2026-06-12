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

class OrganizerTournamentsViewModel : ViewModel() {
    private val repository = TorneioRepository()

    var torneios by mutableStateOf<List<Torneio>>(emptyList())
    var isLoading by mutableStateOf(true)
    var currentUserId by mutableStateOf<String?>(null)

    fun carregarTorneios() {
        viewModelScope.launch {
            isLoading = true
            currentUserId = SupabaseClient.client.auth.currentUserOrNull()?.id

            repository.listarTorneios()
                .onSuccess { lista ->
                    torneios = lista.reversed()
                    isLoading = false
                }
                .onFailure {
                    isLoading = false
                }
        }
    }
}