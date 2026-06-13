package com.example.trabalhocm.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhocm.R
import com.example.trabalhocm.data.repository.EquipaRepository
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

data class UiTeam(
    val id: Long,
    val name: String,
    val divisionRes: Int?,
    val modalidadeNome: String,
    val wins: Int,
    val losses: Int,
    val streak: String,
    val isMyTeam: Boolean
)

class BrowseTeamsViewModel : ViewModel() {
    private val repository = EquipaRepository()

    var teams by mutableStateOf<List<UiTeam>>(emptyList())
    var isLoading by mutableStateOf(true)

    init {
        carregarEquipas()
    }

    fun carregarEquipas() {
        viewModelScope.launch {
            isLoading = true

            repository.listarEquipasComInfo()
                .onSuccess { lista ->
                    teams = lista.map { info ->
                        val modalidadeRes = when (info.equipa.idModalidade) {
                            1L -> R.string.sport_football
                            2L -> R.string.sport_basketball
                            3L -> R.string.sport_volleyball
                            else -> R.string.sport_default
                        }

                        UiTeam(
                            id = info.equipa.id,
                            name = info.equipa.nome,
                            divisionRes = modalidadeRes,
                            modalidadeNome = info.modalidadeNome,
                            wins = info.vitorias,
                            losses = info.derrotas,
                            streak = info.streak,
                            isMyTeam = info.utilizadorPertence
                        )
                    }
                }

            isLoading = false
        }
    }
}