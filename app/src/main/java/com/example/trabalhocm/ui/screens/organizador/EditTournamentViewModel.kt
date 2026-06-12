package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhocm.data.model.Torneio
import com.example.trabalhocm.data.repository.TorneioRepository
import kotlinx.coroutines.launch

class EditTournamentViewModel : ViewModel() {
    private val repository = TorneioRepository()

    var isLoading by mutableStateOf(true)
    var isSaving by mutableStateOf(false)
    var errorMessage by mutableStateOf("")

    private var torneioId = 0L
    private var idOrganizador = ""

    var tournamentName by mutableStateOf("")
    var description by mutableStateOf("")
    var rules by mutableStateOf("")
    var venue by mutableStateOf("")
    var startDate by mutableStateOf("")
    var endDate by mutableStateOf("")
    var entryFee by mutableStateOf("0.00")
    var prizePool by mutableStateOf("0")
    var estado by mutableStateOf("aberto")
    var selectedSport by mutableStateOf("Football")
    var selectedFormat by mutableStateOf("League System")

    fun carregarTorneio(id: Long) {
        if (id == torneioId && !isLoading) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = ""

            repository.obterTorneioPorId(id)
                .onSuccess { torneio ->
                    torneioId = torneio.id
                    idOrganizador = torneio.idOrganizador

                    tournamentName = torneio.nome
                    description = torneio.descricao.orEmpty()
                    rules = torneio.regras.orEmpty()
                    venue = torneio.local.orEmpty()
                    startDate = torneio.dataInicio
                    endDate = torneio.dataFim.orEmpty()
                    entryFee = torneio.taxaInscricao.toString()
                    prizePool = torneio.premio.toString()
                    estado = torneio.estado

                    selectedSport = when (torneio.idModalidade) {
                        1L -> "Football"
                        2L -> "Basketball"
                        3L -> "Volleyball"
                        else -> "Football"
                    }

                    selectedFormat = when (torneio.formato) {
                        "liga" -> "League System"
                        "eliminatorias" -> "Knockout"
                        "grupos" -> "Group Stage + Knockout"
                        else -> "League System"
                    }

                    isLoading = false
                }
                .onFailure { erro ->
                    errorMessage = erro.message ?: "Erro ao carregar torneio."
                    isLoading = false
                }
        }
    }

    fun guardarAlteracoes(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isSaving = true
            errorMessage = ""

            val sportId = when (selectedSport) {
                "Football" -> 1L
                "Basketball" -> 2L
                "Volleyball" -> 3L
                else -> 1L
            }

            val dbFormato = when (selectedFormat) {
                "League System" -> "liga"
                "Knockout" -> "eliminatorias"
                "Group Stage + Knockout" -> "grupos"
                else -> "liga"
            }

            val torneio = Torneio(
                id = torneioId,
                nome = tournamentName,
                descricao = description.ifBlank { null },
                regras = rules.ifBlank { null },
                local = venue.ifBlank { null },
                dataInicio = startDate,
                dataFim = endDate.ifBlank { null },
                formato = dbFormato,
                taxaInscricao = entryFee.toDoubleOrNull() ?: 0.0,
                premio = prizePool.toDoubleOrNull() ?: 0.0,
                estado = estado,
                idOrganizador = idOrganizador,
                idModalidade = sportId
            )

            repository.atualizarTorneio(torneio)
                .onSuccess {
                    isSaving = false
                    onSuccess()
                }
                .onFailure { erro ->
                    isSaving = false
                    errorMessage = erro.message ?: "Erro ao guardar alterações."
                }
        }
    }

    fun apagarTorneio(onSuccess: () -> Unit) {
        viewModelScope.launch {
            isSaving = true
            errorMessage = ""

            repository.apagarTorneio(torneioId)
                .onSuccess {
                    isSaving = false
                    onSuccess()
                }
                .onFailure { erro ->
                    isSaving = false
                    errorMessage = erro.message ?: "Erro ao eliminar torneio."
                }
        }
    }
}
