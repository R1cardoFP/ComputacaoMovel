package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhocm.data.remote.SupabaseClient
import com.example.trabalhocm.data.repository.EquipaRepository
import com.example.trabalhocm.data.repository.JogoRepository
import com.example.trabalhocm.data.repository.TorneioRepository
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

class OrganizerMatchesViewModel : ViewModel() {
    private val jogoRepository = JogoRepository()
    private val equipaRepository = EquipaRepository()
    private val torneioRepository = TorneioRepository()

    var matches by mutableStateOf<List<MatchArchiveItem>>(emptyList())
    var isLoading by mutableStateOf(true)

    init {
        carregarJogos()
    }

    fun carregarJogos() {
        viewModelScope.launch {
            isLoading = true
            val currentUserId = SupabaseClient.client.auth.currentUserOrNull()?.id

            val jogosDb = jogoRepository.listarJogos().getOrNull() ?: emptyList()
            val jogoEquipasDb = jogoRepository.listarJogoEquipas().getOrNull() ?: emptyList()
            val torneiosDb = torneioRepository.listarTorneios().getOrNull() ?: emptyList()
            val equipasDb = equipaRepository.listarTodasEquipas().getOrNull() ?: emptyList()

            val minhasEquipasIds = currentUserId?.let {
                equipaRepository.obterMinhasEquipas(it).getOrNull()
                    ?.filter { rel -> rel.estadoConvite == "aceite" }
                    ?.map { rel -> rel.idEquipa }
            } ?: emptyList()

            val meusEventos = currentUserId?.let {
                jogoRepository.listarMeusEventos(it).getOrNull()
            } ?: emptyList()

            val uiMatches = jogosDb.map { jogo ->
                val torneio = torneiosDb.find { it.id == jogo.idTorneio }
                val contexto = torneio?.nome ?: "Friendly Match"

                val equipasNesteJogo = jogoEquipasDb.filter { it.idJogo == jogo.id }
                val equipaCasaRel = equipasNesteJogo.find { it.papelEquipa == "casa" }
                val equipaForaRel = equipasNesteJogo.find { it.papelEquipa == "fora" }

                val nomeCasa = equipasDb.find { it.id == equipaCasaRel?.idEquipa }?.nome ?: "TBD"
                val nomeFora = equipasDb.find { it.id == equipaForaRel?.idEquipa }?.nome ?: "TBD"

                val pontosCasa = equipaCasaRel?.pontosMarcados ?: 0
                val pontosFora = equipaForaRel?.pontosMarcados ?: 0


                var resultadoMatch = MatchResult.DRAW
                val isNaCasa = minhasEquipasIds.contains(equipaCasaRel?.idEquipa)
                val isNoFora = minhasEquipasIds.contains(equipaForaRel?.idEquipa)

                if (isNaCasa) {
                    resultadoMatch = when {
                        pontosCasa > pontosFora -> MatchResult.WIN
                        pontosCasa < pontosFora -> MatchResult.LOSS
                        else -> MatchResult.DRAW
                    }
                } else if (isNoFora) {
                    resultadoMatch = when {
                        pontosFora > pontosCasa -> MatchResult.WIN
                        pontosFora < pontosCasa -> MatchResult.LOSS
                        else -> MatchResult.DRAW
                    }
                }

                val eventosNesteJogo = meusEventos.filter { it.idJogo == jogo.id }
                val golos = eventosNesteJogo.count { it.tipoEvento.lowercase() == "golo" || it.tipoEvento.lowercase() == "ponto" }
                val stats = if (golos > 0) "$golos G" else if (jogo.estadoJogo == "terminado") "Full Time" else "Scheduled"

                MatchArchiveItem(
                    id = jogo.id.toInt(),
                    result = resultadoMatch,
                    context = contexto,
                    team1Name = nomeCasa,
                    team2Name = nomeFora,
                    score = if (jogo.estadoJogo == "agendado") "vs" else "$pontosCasa - $pontosFora",
                    date = jogo.data,
                    location = jogo.local ?: "TBD",
                    stats = stats,
                    isStatsHighlight = golos > 0
                )
            }

            matches = uiMatches.filter { it.team1Name != "TBD" }.reversed()
            isLoading = false
        }
    }
}