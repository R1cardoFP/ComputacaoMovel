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
import java.time.LocalDate

data class CalendarGame(
    val ano: Int,
    val mes: Int,
    val dia: Int,
    val hora: String,
    val idModalidade: Long,
    val team1: String,
    val team2: String,
    val score: String,
    val estado: String,
    val local: String,
    val isLive: Boolean
)

class OrganizerCalendarViewModel : ViewModel() {
    private val jogoRepository = JogoRepository()
    private val torneioRepository = TorneioRepository()
    private val equipaRepository = EquipaRepository()

    var jogos by mutableStateOf<List<CalendarGame>>(emptyList())
    var isLoading by mutableStateOf(true)

    init {
        carregar()
    }

    fun carregar() {
        viewModelScope.launch {
            isLoading = true
            val userId = SupabaseClient.client.auth.currentUserOrNull()?.id

            val torneios = torneioRepository.listarTorneios().getOrNull().orEmpty()
            val meusTorneios = torneios.filter { it.idOrganizador == userId }
            val idsMeusTorneios = meusTorneios.map { it.id }.toSet()
            val modalidadePorTorneio = meusTorneios.associate { it.id to it.idModalidade }

            val jogosDb = jogoRepository.listarJogos().getOrNull().orEmpty()
            val jogoEquipas = jogoRepository.listarJogoEquipas().getOrNull().orEmpty()
            val equipas = equipaRepository.listarTodasEquipas().getOrNull().orEmpty()

            val equipasPorId = equipas.associateBy { it.id }
            val jePorJogo = jogoEquipas.groupBy { it.idJogo }

            jogos = jogosDb
                .filter { it.idTorneio in idsMeusTorneios }
                .mapNotNull { jogo ->
                    val data = runCatching { LocalDate.parse(jogo.data.take(10)) }.getOrNull()
                        ?: return@mapNotNull null

                    val equipasDoJogo = jePorJogo[jogo.id].orEmpty()
                    val casa = equipasDoJogo.firstOrNull { it.papelEquipa == "casa" } ?: return@mapNotNull null
                    val fora = equipasDoJogo.firstOrNull { it.papelEquipa == "fora" } ?: return@mapNotNull null

                    val inicio = java.time.LocalDateTime.of(
                        data,
                        runCatching { java.time.LocalTime.parse(jogo.hora.take(5)) }.getOrNull()
                            ?: java.time.LocalTime.MIDNIGHT
                    )
                    val agora = java.time.LocalDateTime.now()
                    val estadoLower = jogo.estadoJogo.lowercase()
                    val isLive = when {
                        estadoLower == "em_direto" || estadoLower == "live" ||
                            estadoLower == "em_decorrer" || estadoLower == "a_decorrer" -> true
                        estadoLower in setOf("terminado", "cancelado", "concluido", "finalizado", "adiado") -> false
                        else -> !agora.isBefore(inicio) && agora.isBefore(inicio.plusMinutes(90L))
                    }
                    val score = if (!isLive && jogo.estadoJogo == "agendado") "VS"
                    else "${casa.pontosMarcados} - ${fora.pontosMarcados}"

                    CalendarGame(
                        ano = data.year,
                        mes = data.monthValue,
                        dia = data.dayOfMonth,
                        hora = jogo.hora.take(5),
                        idModalidade = modalidadePorTorneio[jogo.idTorneio] ?: 0L,
                        team1 = equipasPorId[casa.idEquipa]?.nome ?: "TBD",
                        team2 = equipasPorId[fora.idEquipa]?.nome ?: "TBD",
                        score = score,
                        estado = jogo.estadoJogo,
                        local = jogo.local ?: "",
                        isLive = isLive
                    )
                }

            isLoading = false
        }
    }
}
