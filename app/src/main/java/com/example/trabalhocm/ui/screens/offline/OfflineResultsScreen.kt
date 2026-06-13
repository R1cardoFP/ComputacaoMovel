package com.example.trabalhocm.ui.screens.offline

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.local.offline.OfflineResultEntity
import com.example.trabalhocm.data.repository.OfflineResultRepository
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import com.example.trabalhocm.ui.theme.CardBg
import com.example.trabalhocm.ui.theme.DarkBlue
import com.example.trabalhocm.ui.theme.InputBg
import com.example.trabalhocm.ui.theme.TealGreen
import com.example.trabalhocm.ui.theme.TextGray
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        containerColor = BgLight,
        topBar = {
            OfflineResultsTopBar(onBackClick = onBackClick)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BgLight)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            OfflineHeroCard(
                pendingCount = resultadosPendentes.size
            )

            if (mensagem.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))

                OfflineMessageCard(
                    message = mensagem,
                    isError = mensagem.contains("Erro", ignoreCase = true) ||
                            mensagem.contains("Preenche", ignoreCase = true)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    SectionHeader(
                        title = "Novo resultado",
                        subtitle = "Regista os dados principais do jogo para sincronizar mais tarde."
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    OfflineInput(
                        label = "Nome do jogo",
                        value = nomeJogo,
                        onValueChange = { nomeJogo = it },
                        placeholder = "Ex: Elite 5v5 Pickup",
                        keyboardType = KeyboardType.Text
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OfflineInput(
                            label = "Equipa A",
                            value = resultadoA,
                            onValueChange = { resultadoA = it },
                            placeholder = "0",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )

                        OfflineInput(
                            label = "Equipa B",
                            value = resultadoB,
                            onValueChange = { resultadoB = it },
                            placeholder = "0",
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OfflineInput(
                        label = "Observações",
                        value = observacoes,
                        onValueChange = { observacoes = it },
                        placeholder = "Ex: Resultado registado sem Internet",
                        keyboardType = KeyboardType.Text
                    )

                    Spacer(modifier = Modifier.height(20.dp))

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
                            .height(52.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TealGreen,
                            contentColor = BrandWhite,
                            disabledContainerColor = TealGreen.copy(alpha = 0.55f),
                            disabledContentColor = BrandWhite
                        )
                    ) {
                        Text(
                            text = "Guardar offline",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    SectionHeader(
                        title = "Sincronização",
                        subtitle = "Envia para a API todos os resultados que ficaram guardados localmente."
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Pendentes",
                            color = TextGray,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Surface(
                            color = InputBg,
                            shape = RoundedCornerShape(50.dp)
                        ) {
                            Text(
                                text = "${resultadosPendentes.size} resultado(s)",
                                color = DarkBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            )
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
                            .height(52.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkBlue,
                            contentColor = BrandWhite,
                            disabledContainerColor = DarkBlue.copy(alpha = 0.55f),
                            disabledContentColor = BrandWhite
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = BrandWhite,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Text(
                                text = "Sincronizar com API",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(
                title = "Resultados pendentes",
                subtitle = "Lista de resultados guardados localmente neste dispositivo."
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (resultadosPendentes.isEmpty()) {
                EmptyPendingResultsCard()
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

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OfflineResultsTopBar(
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Offline Results",
                color = BrandWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Text(
                    text = "‹",
                    color = BrandWhite,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DarkBlue
        )
    )
}

@Composable
fun OfflineHeroCard(
    pendingCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Surface(
                color = BrandWhite.copy(alpha = 0.12f),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = "OFFLINE MODE",
                    color = BrandWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Registo de resultados",
                color = BrandWhite,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Guarda resultados localmente quando não existe ligação à Internet e sincroniza mais tarde com a API.",
                color = BrandWhite.copy(alpha = 0.82f),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OfflineHeroMetric(
                    label = "Pendentes",
                    value = pendingCount.toString(),
                    modifier = Modifier.weight(1f)
                )

                OfflineHeroMetric(
                    label = "Estado",
                    value = "Local",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun OfflineHeroMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = BrandWhite.copy(alpha = 0.10f),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = value,
                color = BrandWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = label,
                color = BrandWhite.copy(alpha = 0.72f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String
) {
    Column {
        Text(
            text = title,
            color = DarkBlue,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = subtitle,
            color = TextGray,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium
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
            color = TextGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            singleLine = true,
            placeholder = {
                Text(
                    text = placeholder,
                    color = TextGray.copy(alpha = 0.65f),
                    fontSize = 13.sp
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = InputBg,
                unfocusedContainerColor = InputBg,
                disabledContainerColor = InputBg,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = DarkBlue,
                unfocusedTextColor = DarkBlue,
                cursorColor = TealGreen
            )
        )
    }
}

@Composable
fun OfflineMessageCard(
    message: String,
    isError: Boolean
) {
    val color = if (isError) Color(0xFFD01818) else TealGreen
    val backgroundColor = if (isError) Color(0xFFFFEEEE) else Color(0xFFEAF8F2)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = message,
                color = color,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun EmptyPendingResultsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(InputBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "0",
                    color = DarkBlue,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Não existem resultados pendentes.",
                color = DarkBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Quando guardares resultados offline, eles vão aparecer aqui.",
                color = TextGray,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun OfflineResultPendingCard(
    resultado: OfflineResultEntity,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(InputBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = resultado.nomeJogo.take(1).uppercase(),
                        color = DarkBlue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = resultado.nomeJogo,
                        color = DarkBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Resultado pendente",
                        color = TextGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    color = InputBg,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        text = "${resultado.resultadoEquipaA} - ${resultado.resultadoEquipaB}",
                        color = DarkBlue,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }

            if (resultado.observacoes.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = BgLight,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = resultado.observacoes,
                        color = TextGray,
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = onRemoveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFD01818).copy(alpha = 0.35f)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFD01818),
                    containerColor = Color.Transparent
                )
            ) {
                Text(
                    text = "Remover pendente",
                    fontSize = 13.sp,
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
