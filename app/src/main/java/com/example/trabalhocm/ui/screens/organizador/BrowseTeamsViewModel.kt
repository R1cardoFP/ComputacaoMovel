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
            val currentUserId = SupabaseClient.client.auth.currentUserOrNull()?.id

            val equipasResult = repository.listarTodasEquipas()
            val membrosResult = currentUserId?.let { repository.obterMinhasEquipas(it) }

            if (equipasResult.isSuccess) {
                val equipasDB = equipasResult.getOrNull() ?: emptyList()

                val minhasEquipasIds = membrosResult?.getOrNull()
                    ?.filter { it.estadoConvite == "aceite" }
                    ?.map { it.idEquipa } ?: emptyList()

                teams = equipasDB.map { eq ->
                    val modalidadeRes = when(eq.idModalidade) {
                        1L -> R.string.sport_football
                        2L -> R.string.sport_basketball
                        3L -> R.string.sport_volleyball
                        else -> R.string.sport_default
                    }

                    UiTeam(
                        id = eq.id,
                        name = eq.nome,
                        divisionRes = modalidadeRes,
                        wins = 0,
                        losses = 0,
                        streak = "-",
                        isMyTeam = minhasEquipasIds.contains(eq.id)
                    )
                }
            }
            isLoading = false
        }
    }
}