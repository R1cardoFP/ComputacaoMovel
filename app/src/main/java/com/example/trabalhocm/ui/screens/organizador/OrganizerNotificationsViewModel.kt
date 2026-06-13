package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trabalhocm.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.time.OffsetDateTime

data class UiNotificacao(
    val id: Long,
    val titulo: String,
    val mensagem: String,
    val tipo: String,
    val data: String,
    val lida: Boolean
)

class OrganizerNotificationsViewModel : ViewModel() {

    private val client = SupabaseClient.client

    var notificacoes by mutableStateOf<List<UiNotificacao>>(emptyList())
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf("")
        private set

    init {
        carregar()
    }

    fun carregar() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = ""

            val userId = client.auth.currentUserOrNull()?.id

            if (userId == null) {
                notificacoes = emptyList()
                errorMessage = "Utilizador não autenticado."
                isLoading = false
                return@launch
            }

            runCatching {
                val torneios = client.from("torneio")
                    .select {
                        filter {
                            eq("id_organizador", userId)
                        }
                    }
                    .decodeList<JsonObject>()

                val idsTorneios = torneios
                    .map { it.valorLong("id") }
                    .filter { it > 0L }
                    .toSet()

                if (idsTorneios.isEmpty()) {
                    return@runCatching emptyList<UiNotificacao>()
                }

                val torneiosPorId = torneios.associateBy { it.valorLong("id") }

                val inscricoes = client.from("inscricao")
                    .select()
                    .decodeList<JsonObject>()
                    .filter { inscricao ->
                        inscricao.valorLong("id_torneio") in idsTorneios
                    }

                val equipas = client.from("equipa")
                    .select()
                    .decodeList<JsonObject>()

                val equipasPorId = equipas.associateBy { it.valorLong("id") }

                inscricoes
                    .sortedByDescending { inscricao ->
                        inscricao.valorTexto("data_inscricao")
                            ?: inscricao.valorTexto("criado_em")
                            ?: inscricao.valorTexto("created_at")
                            ?: ""
                    }
                    .mapIndexed { index, inscricao ->
                        val idTorneio = inscricao.valorLong("id_torneio")
                        val idEquipa = inscricao.valorLong("id_equipa")

                        val torneio = torneiosPorId[idTorneio]
                        val equipa = equipasPorId[idEquipa]

                        val nomeTorneio = torneio?.valorTexto("nome") ?: "torneio"
                        val nomeEquipa = equipa?.valorTexto("nome") ?: "Equipa"
                        val estado = inscricao.valorTexto("estado")
                            ?: inscricao.valorTexto("estado_inscricao")
                            ?: "pendente"

                        val data = inscricao.valorTexto("data_inscricao")
                            ?: inscricao.valorTexto("criado_em")
                            ?: inscricao.valorTexto("created_at")
                            ?: OffsetDateTime.now().toString()

                        UiNotificacao(
                            id = inscricao.valorLong("id").takeIf { it > 0L } ?: (index + 1).toLong(),
                            titulo = "Nova equipa inscrita",
                            mensagem = "A equipa $nomeEquipa entrou no torneio $nomeTorneio. Estado da inscrição: ${estado.uppercase()}.",
                            tipo = "team",
                            data = data,
                            lida = false
                        )
                    }
            }.onSuccess { lista ->
                notificacoes = lista
                isLoading = false
            }.onFailure { erro ->
                notificacoes = emptyList()
                errorMessage = erro.message ?: "Erro ao carregar notificações do organizador."
                isLoading = false
            }
        }
    }
}

private fun JsonObject.valorTexto(key: String): String? {
    return this[key]?.jsonPrimitive?.contentOrNull
}

private fun JsonObject.valorLong(key: String): Long {
    return this[key]?.jsonPrimitive?.longOrNull ?: 0L
}