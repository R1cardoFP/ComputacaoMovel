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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.repository.AuthRepository
import com.example.trabalhocm.data.repository.Torneio
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite

private val TournamentHistoryBg = Color(0xFFF4F6FA)
private val TournamentHistoryCard = Color.White
private val TournamentHistoryInputBg = Color(0xFFF1F4F8)
private val TournamentHistoryTextGray = Color(0xFF596579)
private val TournamentHistoryTextLight = Color(0xFF8A94A6)
private val TournamentHistoryCompleted = Color(0xFF7D8497)
private val TournamentHistoryAccentBlue = Color(0xFF0757C8)

@Composable
fun PlayerTournamentHistoryScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val authRepository = remember { AuthRepository() }
    var historico by remember { mutableStateOf<List<Torneio>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        authRepository.obterUtilizadorAtual().onSuccess { utilizador ->
            authRepository.obterHistoricoTorneios(utilizador.id).onSuccess { lista ->
                historico = lista
                isLoading = false
            }.onFailure {
                isLoading = false
            }
        }.onFailure {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TournamentHistoryBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        TournamentHistoryTopBar(
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            TournamentHistoryHeroCard(historico = historico)

            Spacer(modifier = Modifier.height(18.dp))

            when {
                isLoading -> {
                    TournamentHistoryLoadingCard()
                }

                historico.isEmpty() -> {
                    TournamentHistoryMessageCard(
                        title = "Sem histórico",
                        text = "Ainda não participaste em nenhum torneio.",
                        color = TournamentHistoryTextGray
                    )
                }

                else -> {
                    Text(
                        text = "Histórico de torneios",
                        color = BrandBlue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    historico.forEach { torneio ->
                        val modalidadeNome = when (torneio.idModalidade) {
                            1 -> "Football"
                            2 -> "Basketball"
                            3 -> "Volleyball"
                            else -> "Sport"
                        }

                        val estadoLower = torneio.estado?.lowercase().orEmpty()
                        val isConcluido = estadoLower == "concluido" || estadoLower == "arquivado"
                        val corDestaque = if (isConcluido) TournamentHistoryCompleted else BrandGreen
                        val etiquetaEstado = if (isConcluido) "COMPLETED" else (torneio.estado?.uppercase() ?: "ACTIVE")
                        val formato = torneio.formato?.replaceFirstChar { it.uppercase() } ?: "League"
                        val premio = torneio.premio

                        TournamentHistoryCard(
                            accentColor = corDestaque,
                            status = etiquetaEstado,
                            sport = modalidadeNome,
                            title = torneio.nome,
                            subtitle = "$formato · ${torneio.dataInicio ?: "TBD"}",
                            role = "Player",
                            finalPosition = "TBD",
                            played = "-",
                            points = "-",
                            footer = if (premio != null && premio > 0.0) "Prize Pool: €$premio" else null
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        MatchLeagueBottomBar(
            selectedTab = "TOURNAMENTS",
            onHomeClick = onHomeClick,
            onTournamentsClick = onTournamentsClick,
            onMatchesClick = onMatchesClick,
            onTeamsClick = onTeamsClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
private fun TournamentHistoryTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(BrandBlue)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f))
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "‹",
                color = BrandWhite,
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 3.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "Tournament History",
                color = BrandWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            )

            Text(
                text = "Consulta o teu arquivo competitivo",
                color = BrandWhite.copy(alpha = 0.78f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "▣",
                color = BrandWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun TournamentHistoryHeroCard(
    historico: List<Torneio>
) {
    val completed = historico.count {
        val estado = it.estado?.lowercase().orEmpty()
        estado == "concluido" || estado == "arquivado"
    }
    val active = historico.size - completed
    val completionProgress = if (historico.isEmpty()) 0f else completed.toFloat() / historico.size.toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "YOUR RECORDS",
                        color = BrandGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "My Tournaments",
                        color = BrandWhite,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Tournaments you have organized or participated in.",
                        color = BrandWhite.copy(alpha = 0.78f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = historico.size.toString(),
                        color = BrandWhite,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TournamentHistoryHeroStat(
                    label = "Total",
                    value = historico.size.toString(),
                    modifier = Modifier.weight(1f)
                )

                TournamentHistoryHeroStat(
                    label = "Active",
                    value = active.toString(),
                    modifier = Modifier.weight(1f)
                )

                TournamentHistoryHeroStat(
                    label = "Completed",
                    value = completed.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.10f))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Completed tournaments",
                        color = BrandWhite.copy(alpha = 0.78f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "$completed/${historico.size}",
                        color = BrandWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { completionProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    color = BrandGreen,
                    trackColor = Color.White.copy(alpha = 0.18f)
                )
            }
        }
    }
}

@Composable
private fun TournamentHistoryHeroStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                color = BrandWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = label,
                color = BrandWhite.copy(alpha = 0.72f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun TournamentHistoryCard(
    accentColor: Color,
    status: String,
    sport: String,
    title: String,
    subtitle: String,
    role: String,
    finalPosition: String,
    played: String,
    points: String,
    footer: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = TournamentHistoryCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = sport.take(1).uppercase(),
                        color = accentColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TournamentHistoryBadge(
                            text = status,
                            color = accentColor
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        TournamentHistoryBadge(
                            text = sport.uppercase(),
                            color = TournamentHistoryAccentBlue
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = title,
                        color = BrandBlue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = subtitle,
                        color = TournamentHistoryTextGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(TournamentHistoryInputBg)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TournamentHistoryStat(
                    label = "MY ROLE",
                    value = role,
                    modifier = Modifier.weight(1f)
                )

                TournamentHistoryStat(
                    label = "FINAL POS.",
                    value = finalPosition,
                    modifier = Modifier.weight(1f)
                )

                TournamentHistoryStat(
                    label = "PLAYED",
                    value = played,
                    modifier = Modifier.weight(1f)
                )

                TournamentHistoryStat(
                    label = "POINTS",
                    value = points,
                    valueColor = TournamentHistoryAccentBlue,
                    modifier = Modifier.weight(1f)
                )
            }

            if (!footer.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BrandGreen.copy(alpha = 0.10f))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = footer,
                        color = BrandBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TournamentHistoryBadge(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TournamentHistoryStat(
    label: String,
    value: String,
    valueColor: Color = BrandBlue,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = TournamentHistoryTextLight,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            color = valueColor,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TournamentHistoryLoadingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = TournamentHistoryCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(42.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = BrandGreen)
        }
    }
}

@Composable
private fun TournamentHistoryMessageCard(
    title: String,
    text: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = TournamentHistoryCard),
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
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "i",
                    color = color,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                color = BrandBlue,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = text,
                color = TournamentHistoryTextGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}

@Preview(showBackground = true, name = "Player Tournament History Screen")
@Composable
fun PlayerTournamentHistoryScreenPreview() {
    PlayerTournamentHistoryScreen()
}
