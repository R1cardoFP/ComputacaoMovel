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
import com.example.trabalhocm.data.repository.PlayerMatchHistoryItem
import com.example.trabalhocm.data.repository.PlayerMatchHistoryRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite

private val PlayerHistoryBg = Color(0xFFF4F6FA)
private val PlayerHistoryCard = Color.White
private val PlayerHistoryInputBg = Color(0xFFF1F4F8)
private val PlayerHistoryTextGray = Color(0xFF596579)
private val PlayerHistoryTextLight = Color(0xFF8A94A6)
private val PlayerHistoryDanger = Color(0xFFE53935)
private val PlayerHistoryDraw = Color(0xFF6D7486)

@Composable
fun PlayerMatchHistoryScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { PlayerMatchHistoryRepository() }

    var selectedFilter by remember { mutableStateOf("All") }
    var matches by remember { mutableStateOf<List<PlayerMatchHistoryItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = ""

        repository.listarHistoricoJogador()
            .onSuccess {
                matches = it
            }
            .onFailure {
                errorMessage = it.message ?: "Erro ao carregar histórico de partidas."
            }

        isLoading = false
    }

    val filteredMatches = matches.filter { match ->
        when (selectedFilter) {
            "Wins" -> match.resultado == "WIN"
            "Losses" -> match.resultado == "LOSS"
            "Draws" -> match.resultado == "DRAW"
            else -> true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlayerHistoryBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        PlayerMatchHistoryTopBar(
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            PlayerMatchHistoryHeroCard(matches = matches)

            Spacer(modifier = Modifier.height(18.dp))

            PlayerMatchHistoryFilterTabs(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    PlayerMatchHistoryLoadingCard()
                }

                errorMessage.isNotBlank() -> {
                    PlayerMatchHistoryMessageCard(
                        title = "Erro ao carregar",
                        text = errorMessage,
                        color = PlayerHistoryDanger
                    )
                }

                filteredMatches.isEmpty() -> {
                    PlayerMatchHistoryMessageCard(
                        title = "Sem partidas",
                        text = "Não existem partidas para este filtro.",
                        color = PlayerHistoryTextGray
                    )
                }

                else -> {
                    Text(
                        text = "Histórico de partidas",
                        color = BrandBlue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    filteredMatches.forEach { match ->
                        PlayerMatchHistoryCard(match = match)

                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }

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
private fun PlayerMatchHistoryTopBar(
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
                text = "Match History",
                color = BrandWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            )

            Text(
                text = "Consulta o teu arquivo pessoal",
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
private fun PlayerMatchHistoryHeroCard(
    matches: List<PlayerMatchHistoryItem>
) {
    val wins = matches.count { it.resultado == "WIN" }
    val losses = matches.count { it.resultado == "LOSS" }
    val draws = matches.count { it.resultado == "DRAW" }

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
                        text = "PERSONAL ARCHIVE",
                        color = BrandGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "My Matches",
                        color = BrandWhite,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "All matches you have personally participated in.",
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
                        text = matches.size.toString(),
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
                PlayerMatchHistoryHeroStat(
                    label = "Wins",
                    value = wins.toString(),
                    modifier = Modifier.weight(1f)
                )

                PlayerMatchHistoryHeroStat(
                    label = "Draws",
                    value = draws.toString(),
                    modifier = Modifier.weight(1f)
                )

                PlayerMatchHistoryHeroStat(
                    label = "Losses",
                    value = losses.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PlayerMatchHistoryHeroStat(
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
private fun PlayerMatchHistoryFilterTabs(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PlayerHistoryCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = "Filtrar resultados",
                color = BrandBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PlayerMatchHistoryFilterChip(
                    text = "All",
                    selected = selectedFilter == "All",
                    onClick = { onFilterSelected("All") },
                    modifier = Modifier.weight(1f)
                )

                PlayerMatchHistoryFilterChip(
                    text = "Wins",
                    selected = selectedFilter == "Wins",
                    onClick = { onFilterSelected("Wins") },
                    modifier = Modifier.weight(1f)
                )

                PlayerMatchHistoryFilterChip(
                    text = "Losses",
                    selected = selectedFilter == "Losses",
                    onClick = { onFilterSelected("Losses") },
                    modifier = Modifier.weight(1f)
                )

                PlayerMatchHistoryFilterChip(
                    text = "Draws",
                    selected = selectedFilter == "Draws",
                    onClick = { onFilterSelected("Draws") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun PlayerMatchHistoryFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (selected) BrandBlue else PlayerHistoryInputBg)
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) BrandWhite else BrandBlue,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PlayerMatchHistoryCard(
    match: PlayerMatchHistoryItem
) {
    val resultColor = when (match.resultado) {
        "WIN" -> BrandGreen
        "LOSS" -> PlayerHistoryDanger
        "DRAW" -> PlayerHistoryDraw
        else -> PlayerHistoryDraw
    }

    val resultText = when (match.resultado) {
        "WIN" -> "WIN"
        "LOSS" -> "LOSS"
        "DRAW" -> "DRAW"
        else -> match.resultado
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = PlayerHistoryCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlayerMatchHistoryResultBadge(
                    text = resultText,
                    backgroundColor = resultColor.copy(alpha = 0.12f),
                    textColor = resultColor
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = match.torneioNome,
                    color = BrandBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlayerMatchHistoryTeamBlock(
                    teamName = match.minhaEquipaNome,
                    modifier = Modifier.weight(1f)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 10.dp)
                ) {
                    Text(
                        text = "${match.meusPontos} - ${match.adversarioPontos}",
                        color = BrandBlue,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(PlayerHistoryInputBg)
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = playerMatchHistoryShortResult(match),
                            color = resultColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                PlayerMatchHistoryTeamBlock(
                    teamName = match.adversarioNome,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PlayerHistoryInputBg)
                    .padding(12.dp)
            ) {
                Column {
                    PlayerMatchHistoryInfoRow(
                        label = "Data",
                        value = playerMatchHistoryFormatDate(match.data)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PlayerMatchHistoryInfoRow(
                        label = "Local",
                        value = match.local
                    )
                }
            }
        }
    }
}

@Composable
private fun PlayerMatchHistoryResultBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp
        )
    }
}

@Composable
private fun PlayerMatchHistoryTeamBlock(
    teamName: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(PlayerHistoryInputBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = playerMatchHistoryInitials(teamName),
                color = BrandBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = teamName,
            color = BrandBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PlayerMatchHistoryInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = PlayerHistoryTextLight,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(52.dp)
        )

        Text(
            text = value,
            color = PlayerHistoryTextGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun PlayerMatchHistoryLoadingCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = PlayerHistoryCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = BrandGreen)
        }
    }
}

@Composable
private fun PlayerMatchHistoryMessageCard(
    title: String,
    text: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = PlayerHistoryCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "!",
                    color = color,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                color = BrandBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = text,
                color = PlayerHistoryTextGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun playerMatchHistoryInitials(teamName: String): String {
    val words = teamName
        .split(" ")
        .filter { it.isNotBlank() }

    return when {
        words.isEmpty() -> "?"
        words.size == 1 -> words.first().take(2).uppercase()
        else -> words.take(2).joinToString("") { it.first().uppercaseChar().toString() }
    }
}

private fun playerMatchHistoryFormatDate(date: String): String {
    return when {
        date.length >= 10 -> {
            val parts = date.take(10).split("-")
            if (parts.size == 3) {
                "${parts[2]} Oct 2026"
            } else {
                date
            }
        }

        else -> date
    }
}

private fun playerMatchHistoryShortResult(match: PlayerMatchHistoryItem): String {
    return when (match.resultado) {
        "WIN" -> "${match.meusPontos} G - ${match.adversarioPontos} A"
        "LOSS" -> "${match.meusPontos} G"
        "DRAW" -> "Full Time"
        else -> "Full Time"
    }
}

@Preview(showBackground = true, name = "Player Match History Screen")
@Composable
fun PlayerMatchHistoryScreenPreview() {
    PlayerMatchHistoryScreen()
}
