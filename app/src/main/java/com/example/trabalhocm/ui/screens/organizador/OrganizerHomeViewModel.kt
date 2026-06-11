package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhocm.data.repository.OrganizerHomeFixture
import com.example.trabalhocm.data.repository.OrganizerHomeLiveMatch
import com.example.trabalhocm.data.repository.OrganizerHomePlayerStats
import com.example.trabalhocm.data.repository.OrganizerHomeRepository
import com.example.trabalhocm.data.repository.OrganizerHomeTournament
import kotlinx.coroutines.launch

class OrganizerHomeViewModel : ViewModel() {

    private val repository = OrganizerHomeRepository()

    var liveMatch by mutableStateOf<OrganizerHomeLiveMatch?>(null)
        private set

    var activeTournaments by mutableStateOf<List<OrganizerHomeTournament>>(emptyList())
        private set

    var upcomingFixtures by mutableStateOf<List<OrganizerHomeFixture>>(emptyList())
        private set

    var playerOfWeek by mutableStateOf<OrganizerHomePlayerStats?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf("")
        private set

    init {
        carregarDashboard()
    }

    fun carregarDashboard() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""

            repository.carregarDadosHome()
                .onSuccess { dados ->
                    liveMatch = dados.liveMatch
                    activeTournaments = dados.activeTournaments
                    upcomingFixtures = dados.upcomingFixtures
                    playerOfWeek = dados.playerOfWeek
                }
                .onFailure { erro ->
                    liveMatch = null
                    activeTournaments = emptyList()
                    upcomingFixtures = emptyList()
                    playerOfWeek = null
                    errorMessage = erro.message ?: "Erro ao carregar dashboard do organizador."
                }

            isLoading = false
        }
    }
}