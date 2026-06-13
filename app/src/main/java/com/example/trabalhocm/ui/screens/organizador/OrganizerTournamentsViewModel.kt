package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhocm.data.model.Torneio
import com.example.trabalhocm.data.remote.SupabaseClient
import com.example.trabalhocm.data.repository.JogoRepository
import com.example.trabalhocm.data.repository.TorneioRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.time.LocalDate

class OrganizerTournamentsViewModel : ViewModel() {
    private val repository = TorneioRepository()
    private val jogoRepository = JogoRepository()

    var torneios by mutableStateOf<List<Torneio>>(emptyList())
    var isLoading by mutableStateOf(true)
    var currentUserId by mutableStateOf<String?>(null)

    // Contadores reais por torneio
    var equipasPorTorneio by mutableStateOf<Map<Long, Int>>(emptyMap())
    var jogosHojePorTorneio by mutableStateOf<Map<Long, Int>>(emptyMap())

    fun carregarTorneios() {
        viewModelScope.launch {
            isLoading = true
            currentUserId = SupabaseClient.client.auth.currentUserOrNull()?.id

            repository.listarTorneios()
                .onSuccess { lista ->
                    torneios = lista.reversed()

                    // Nº de equipas inscritas por torneio
                    val inscricoes = runCatching {
                        SupabaseClient.client.from("torneio_equipa")
                            .select()
                            .decodeList<JsonObject>()
                    }.getOrDefault(emptyList())

                    equipasPorTorneio = inscricoes
                        .groupingBy { it["id_torneio"]?.jsonPrimitive?.longOrNull ?: 0L }
                        .eachCount()

                    // Nº de jogos de hoje por torneio
                    val hoje = LocalDate.now().toString()
                    val jogos = jogoRepository.listarJogos().getOrNull().orEmpty()
                    jogosHojePorTorneio = jogos
                        .filter { it.data.take(10) == hoje }
                        .groupingBy { it.idTorneio }
                        .eachCount()

                    isLoading = false
                }
                .onFailure {
                    isLoading = false
                }
        }
    }
}
