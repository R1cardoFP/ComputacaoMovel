package com.example.trabalhocm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.trabalhocm.data.model.Modalidade
import com.example.trabalhocm.data.repository.ModalidadeRepository

@Composable
fun HomeScreen(
    onVerTorneios: () -> Unit = {}
) {
    val repository = remember { ModalidadeRepository() }

    var modalidades by remember { mutableStateOf<List<Modalidade>>(emptyList()) }
    var mensagemErro by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val resultado = repository.listarModalidades()

        resultado
            .onSuccess {
                modalidades = it
            }
            .onFailure {
                mensagemErro = it.message ?: "Erro ao carregar modalidades."
            }

        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = "MatchLeague",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Bem-vindo à aplicação",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onVerTorneios,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver Torneios")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Modalidades disponíveis",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else if (mensagemErro.isNotBlank()) {
            Text(
                text = "Erro: $mensagemErro",
                color = MaterialTheme.colorScheme.error
            )
        } else {
            modalidades.forEach { modalidade ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = modalidade.nome,
                            style = MaterialTheme.typography.titleMedium
                        )

                        if (!modalidade.descricao.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = modalidade.descricao,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen()
    }
}