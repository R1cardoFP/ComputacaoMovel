package com.example.trabalhocm.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.repository.PeladinhaComInfo
import com.example.trabalhocm.data.repository.PeladinhaRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite

@Composable
fun PlayerMatchesScreen(
    onCalendarClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onFiltersClick: () -> Unit = {},
    onDetailsClick: () -> Unit = {},
    onJoinMatchClick: () -> Unit = {},
    onAskOrganizerClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { PeladinhaRepository() }

    var peladinhas by remember { mutableStateOf<List<PeladinhaComInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var mensagemErro by remember { mutableStateOf("") }
    var search by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        isLoading = true
        mensagemErro = ""

        val resultado = repository.listarPeladinhasComInfo()

        resultado
            .onSuccess {
                peladinhas = it
            }
            .onFailure {
                mensagemErro = it.message ?: "Erro ao carregar partidas casuais."
            }

        isLoading = false
    }

    val peladinhasFiltradas = peladinhas.filter { item ->
        val texto = "${item.peladinha.descricao} ${item.peladinha.local} ${item.modalidadeNome} ${item.peladinha.estado}"
        search.isBlank() || texto.contains(search, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        MatchesTopBar()

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 18.dp)
        ) {
            Text(
                text = "Match Center",
                color = BrandBlue,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Track live games, follow your team and join casual\npickup matches.",
                color = Color(0xFF6D7486),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MatchesActionButton(
                    text = "▣  CALENDAR",
                    onClick = onCalendarClick,
                    modifier = Modifier.weight(1f)
                )

                MatchesActionButton(
                    text = "◷  HISTORY",
                    onClick = onHistoryClick,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            MatchesSearchAndFiltersCard(
                search = search,
                onSearchChange = { search = it },
                onFiltersClick = onFiltersClick
            )

            Spacer(modifier = Modifier.height(22.dp))

            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = BrandGreen
                    )
                }
            } else if (mensagemErro.isNotBlank()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "Erro: $mensagemErro",
                        color = Color(0xFFD01818),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            } else if (peladinhasFiltradas.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "Ainda não existem partidas casuais disponíveis.",
                        color = BrandBlue,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            } else {
                peladinhasFiltradas.forEach { item ->
                    CasualMatchCard(
                        item = item,
                        onDetailsClick = onDetailsClick,
                        onJoinMatchClick = onJoinMatchClick
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                }
            }

            HostMatchCard(
                onAskOrganizerClick = onAskOrganizerClick
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        MatchLeagueBottomBar(
            selectedTab = "MATCHES",
            onHomeClick = onHomeClick,
            onTournamentsClick = onTournamentsClick,
            onMatchesClick = onMatchesClick,
            onTeamsClick = onTeamsClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun MatchesTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(BrandBlue)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Matches",
            color = BrandWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "♧",
            color = BrandWhite,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MatchesActionButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFEFF1F6),
            contentColor = BrandBlue
        )
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MatchesSearchAndFiltersCard(
    search: String,
    onSearchChange: (String) -> Unit,
    onFiltersClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            OutlinedTextField(
                value = search,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Search tournaments...",
                        color = Color(0xFF9EA4B3),
                        fontSize = 12.sp
                    )
                },
                leadingIcon = {
                    Text(
                        text = "⌕",
                        color = Color(0xFF8D94A3),
                        fontSize = 16.sp
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                shape = RoundedCornerShape(7.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BrandWhite,
                    unfocusedContainerColor = BrandWhite,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = BrandGreen,
                    focusedTextColor = BrandBlue,
                    unfocusedTextColor = BrandBlue
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MatchesFilterChip(
                    text = "Status: All",
                    modifier = Modifier.weight(1f)
                )

                MatchesFilterChip(
                    text = "Region: All",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onFiltersClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(
                    text = "≡  FILTERS",
                    color = BrandBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun MatchesFilterChip(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(Color(0xFFEFF1F6)),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            color = BrandBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 14.dp)
        )
    }
}

@Composable
fun CasualMatchCard(
    item: PeladinhaComInfo,
    onDetailsClick: () -> Unit,
    onJoinMatchClick: () -> Unit
) {
    val peladinha = item.peladinha
    val estadoNormalizado = peladinha.estado.lowercase()
    val jogadores = item.jogadoresInscritos
    val maxJogadores = peladinha.maxJogadores
    val progresso = if (maxJogadores > 0) {
        jogadores.toFloat() / maxJogadores.toFloat()
    } else {
        0f
    }

    val statusText = when (estadoNormalizado) {
        "aberta" -> "OPEN"
        "fechada" -> if (jogadores >= maxJogadores && maxJogadores > 0) "FULL" else "CLOSED"
        "terminada" -> "FINISHED"
        else -> peladinha.estado.uppercase()
    }

    val statusColor = when (estadoNormalizado) {
        "aberta" -> BrandGreen
        "fechada" -> Color(0xFFD39A00)
        "terminada" -> Color(0xFF7D8497)
        else -> Color(0xFF0757C8)
    }

    val buttonText = when (estadoNormalizado) {
        "aberta" -> "JOIN MATCH"
        "fechada" -> "JOIN WAITING LIST"
        "terminada" -> "FINISHED"
        else -> "JOIN MATCH"
    }

    val buttonEnabled = estadoNormalizado == "aberta"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                MatchSmallBadge(
                    text = "● $statusText",
                    backgroundColor = statusColor.copy(alpha = 0.12f),
                    textColor = statusColor
                )

                MatchSmallBadge(
                    text = "CASUAL",
                    backgroundColor = Color(0xFFEFF1F6),
                    textColor = Color(0xFF6D7486)
                )

                MatchSmallBadge(
                    text = item.modalidadeNome.uppercase(),
                    backgroundColor = Color(0xFFEFF1F6),
                    textColor = Color(0xFF6D7486)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = peladinha.descricao ?: "Partida casual",
                color = BrandBlue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = formatarDataHoraLocal(
                    data = peladinha.data,
                    hora = peladinha.hora,
                    local = peladinha.local
                ),
                color = Color(0xFF6D7486),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PLAYERS JOINED",
                            color = Color(0xFF6D7486),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Text(
                            text = "$jogadores/$maxJogadores",
                            color = BrandBlue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { progresso.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        color = statusColor,
                        trackColor = Color(0xFFE8EAF2)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDetailsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text(
                        text = "VIEW DETAILS",
                        color = Color(0xFF0757C8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onJoinMatchClick,
                    enabled = buttonEnabled,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(3.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen,
                        contentColor = BrandWhite,
                        disabledContainerColor = Color(0xFFD4D9E3),
                        disabledContentColor = Color(0xFF7D8497)
                    )
                ) {
                    Text(
                        text = buttonText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun MatchSmallBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .padding(horizontal = 9.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.7.sp
        )
    }
}

@Composable
fun HostMatchCard(
    onAskOrganizerClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "⊕",
                color = Color(0xFF49617F),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Host a Match?",
                color = BrandBlue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Can't find what you're looking for? Create your\nown casual match.",
                color = Color(0xFF6D7486),
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onAskOrganizerClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandBlue,
                    contentColor = BrandWhite
                )
            ) {
                Text(
                    text = "ASK TO BE ORGANIZER",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun formatarDataHoraLocal(
    data: String?,
    hora: String?,
    local: String?
): String {
    val dataTexto = data ?: "Data por definir"
    val horaTexto = hora?.take(5) ?: "Hora por definir"
    val localTexto = local ?: "Local por definir"

    return "$dataTexto $horaTexto · $localTexto"
}

@Preview(showBackground = true, name = "Player Matches Screen")
@Composable
fun PlayerMatchesScreenPreview() {
    PlayerMatchesScreen()
}