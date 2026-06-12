package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhocm.data.model.Torneio
import com.example.trabalhocm.data.remote.SupabaseClient
import com.example.trabalhocm.data.repository.TorneioRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch
import java.time.LocalDate

class OrganizerTournamentHistoryViewModel : ViewModel() {
    private val repository = TorneioRepository()

    var torneios by mutableStateOf<List<Torneio>>(emptyList())
    var isLoading by mutableStateOf(true)

    private val estadosTerminados = listOf("terminado", "fechado", "arquivado", "concluido", "encerrado")

    init {
        carregar()
    }

    fun carregar() {
        viewModelScope.launch {
            isLoading = true
            val userId = SupabaseClient.client.auth.currentUserOrNull()?.id
            val hoje = LocalDate.now()

            repository.listarTorneios()
                .onSuccess { lista ->
                    torneios = lista
                        .filter { it.idOrganizador == userId }
                        .filter { torneio ->
                            val estado = torneio.estado.lowercase()
                            val jaTerminou = runCatching {
                                LocalDate.parse(torneio.dataFim?.take(10))
                            }.getOrNull()?.isBefore(hoje) == true

                            estado in estadosTerminados || jaTerminou
                        }
                        .sortedByDescending { it.dataInicio }
                    isLoading = false
                }
                .onFailure {
                    isLoading = false
                }
        }
    }
}
