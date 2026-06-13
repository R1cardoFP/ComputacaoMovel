package com.example.trabalhocm.ui.screens.offline

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.local.offline.OfflineResultEntity
import com.example.trabalhocm.data.repository.OfflineResultRepository
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import kotlinx.coroutines.launch

@Composable
fun OfflineResultsScreen(
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember { OfflineResultRepository(context) }
    val scope = rememberCoroutineScope()

    val resultadosPendentes by repository.resultadosPendentes.collectAsState(initial = emptyList())

    var nomeJogo by remember { mutableStateOf("") }
    var resultadoA by remember { mutableStateOf("") }
    var resultadoB by remember { mutableStateOf("") }
    var observacoes by remember { mutableStateOf("") }

    var mensagem by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        OfflineResultsTopBar(
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 20.dp)
        ) {
            Text(
                text = "OFFLINE MODE",
                color = Color(0xFF4167C8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Registo de resultados",
                color = BrandBlue,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Guarda resultados localmente quando não existe ligação à Internet e sincroniza mais tarde com a API.",
                color = Color(0xFF51607A),
                fontSize = 13.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = BrandWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp)
                ) {
                    OfflineInput(
                        label = "NOME DO JOGO",
                        value = nomeJogo,
                        onValueChange = { nomeJogo = it },
                        placeholder = "Ex: Elite 5v5 Pickup",
                        keyboardType = KeyboardType.Text
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OfflineInput(
                            label = "EQUIPA A",
                            value = resultadoA,
                            onValueChange = { resultadoA = it },
                            placeholder = "0",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )

                        OfflineInput(
                            label = "EQUIPA B",
                            value = resultadoB,
                            onValueChange = { resultadoB = it },
                            placeholder = "0",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OfflineInput(
                        label = "OBSERVAÇÕES",
                        value = observacoes,
                        onValueChange = { observacoes = it },
                        placeholder = "Ex: Resultado registado sem Internet",
                        keyboardType = KeyboardType.Text
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val jogo = nomeJogo.trim()
                            val pontosA = resultadoA.toIntOrNull()
                            val pontosB = resultadoB.toIntOrNull()

                            if (jogo.isBlank() || pontosA == null || pontosB == null) {
                                mensagem = "Preenche o nome do jogo e os dois resultados corretamente."
                                return@Button
                            }
                            scope.launch {
                                isLoading = true
                                mensagem = ""

                                repository.guardarResultadoOffline(
                                    nomeJogo = jogo,
                                    resultadoEquipaA = pontosA,
                                    resultadoEquipaB = pontosB,
                                    observacoes = observacoes
                                ).onSuccess {
                                    mensagem = "Resultado guardado offline com sucesso."
                                    nomeJogo = ""
                                    resultadoA = ""
                                    resultadoB = ""
                                    observacoes = ""
                                }.onFailure {
                                    mensagem = "Erro ao guardar offline: ${it.message}"
                                }

                                isLoading = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandGreen,
                            contentColor = BrandWhite
                        )
                    ) {
                        Text(
                            text = "GUARDAR OFFLINE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        isLoading = true
                        mensagem = ""

                        repository.sincronizarResultadosPendentes()
                            .onSuccess { total ->
                                mensagem = if (total > 0) {
                                    "$total resultado(s) sincronizado(s) com a API."
                                } else {
                                    "Não existem resultados pendentes para sincronizar."
                                }
                            }
                            .onFailure { erro ->
                                mensagem = "Erro na sincronização: ${erro.message}"
                            }

                       
                        isLoading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandBlue,
                    contentColor = BrandWhite
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = BrandWhite,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "SINCRONIZAR COM API",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            if (mensagem.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = mensagem,
                    color = if (mensagem.contains("Erro", ignoreCase = true)) {
                        Color(0xFFD01818)
                    } else {
                        BrandGreen
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "RESULTADOS PENDENTES",
                color = BrandBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (resultadosPendentes.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "Não existem resultados pendentes.",
                        color = Color(0xFF6D7486),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                resultadosPendentes.forEach { resultado ->
                    OfflineResultPendingCard(
                        resultado = resultado,
                        onRemoveClick = {
                            scope.launch {
                                repository.removerResultadoPendente(resultado.idLocal)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
fun OfflineResultsTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(BrandBlue)
            .padding(horizontal = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "←",
            color = BrandWhite,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(end = 14.dp)
                .clickable { onBackClick() }
        )

        Text(
            text = "Offline Results",
            color = BrandWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun OfflineInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            color = Color(0xFF7D8497),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            singleLine = true,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFFA7ACBA),
                    fontSize = 13.sp
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(5.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF1F2FB),
                unfocusedContainerColor = Color(0xFFF1F2FB),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = BrandBlue,
                unfocusedTextColor = BrandBlue,
                cursorColor = BrandGreen
            )
        )
    }
}

@Composable
fun OfflineResultPendingCard(
    resultado: OfflineResultEntity,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Jogo: ${resultado.nomeJogo}",
                color = BrandBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Resultado: ${resultado.resultadoEquipaA} - ${resultado.resultadoEquipaB}",
                color = Color(0xFF51607A),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            if (resultado.observacoes.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = resultado.observacoes,
                    color = Color(0xFF7D8497),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onRemoveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(
                    text = "REMOVER PENDENTE",
                    color = Color(0xFFD01818),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OfflineResultsScreenPreview() {
    OfflineResultsScreen()
}