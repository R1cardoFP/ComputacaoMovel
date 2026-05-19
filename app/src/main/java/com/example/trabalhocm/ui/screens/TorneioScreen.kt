package com.example.trabalhocm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trabalhocm.data.model.Torneio
import com.example.trabalhocm.data.repository.TorneioRepository

@Composable
fun TorneiosScreen() {
    val repository = remember { TorneioRepository() }

    var torneios by remember { mutableStateOf<List<Torneio>>(emptyList()) }
    var mensagemErro by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val resultado = repository.listarTorneios()

        resultado
            .onSuccess {
                torneios = it
            }
            .onFailure {
                mensagemErro = it.message ?: "Erro ao carregar torneios."
            }

        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "Torneios",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Lista de torneios disponíveis",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (mensagemErro.isNotBlank()) {
            Text(
                text = "Erro: $mensagemErro",
                color = MaterialTheme.colorScheme.error
            )
        } else if (torneios.isEmpty()) {
            Text(
                text = "Ainda não existem torneios registados.",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            torneios.forEach { torneio ->
                TorneioCard(torneio)
            }
        }
    }
}

@Composable
fun TorneioCard(torneio: Torneio) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = torneio.nome,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = torneio.descricao ?: "Sem descrição.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Local: ${torneio.local ?: "Não definido"}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Formato: ${torneio.formato}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Estado: ${torneio.estado}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Início: ${torneio.dataInicio}",
                style = MaterialTheme.typography.bodySmall
            )

            if (!torneio.dataFim.isNullOrBlank()) {
                Text(
                    text = "Fim: ${torneio.dataFim}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}