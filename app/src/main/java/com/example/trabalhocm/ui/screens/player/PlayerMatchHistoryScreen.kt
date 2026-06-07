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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.repository.PlayerMatchHistoryItem
import com.example.trabalhocm.data.repository.PlayerMatchHistoryRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite

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
            .background(Color(0xFFF4F5FA))
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
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Text(
                text = "PERSONAL ARCHIVE",
                color = Color(0xFF0757C8),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "My Matches",
                color = BrandBlue,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "All matches you have personally participated in.",
                color = Color(0xFF6D7486),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            PlayerMatchHistoryFilterTabs(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BrandGreen)
                    }
                }

                errorMessage.isNotBlank() -> {
                    PlayerMatchHistoryMessageCard(
                        text = "Erro: $errorMessage",
                        color = Color(0xFFD01818)
                    )
                }

                filteredMatches.isEmpty() -> {
                    PlayerMatchHistoryMessageCard(
                        text = "Não existem partidas para este filtro.",
                        color = BrandBlue
                    )
                }

                else -> {
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
            .padding(horizontal = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "‹",
            color = BrandWhite,
            fontSize = 34.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable {
                onBackClick()
            }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "My Matches",
            color = BrandWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "♧",
            color = BrandWhite,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PlayerMatchHistoryFilterTabs(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PlayerMatchHistoryFilterChip(
            text = "All",
            selected = selectedFilter == "All",
            onClick = { onFilterSelected("All") }
        )

        PlayerMatchHistoryFilterChip(
            text = "Wins",
            selected = selectedFilter == "Wins",
            onClick = { onFilterSelected("Wins") }
        )

        PlayerMatchHistoryFilterChip(
            text = "Losses",
            selected = selectedFilter == "Losses",
            onClick = { onFilterSelected("Losses") }
        )

        PlayerMatchHistoryFilterChip(
            text = "Draws",
            selected = selectedFilter == "Draws",
            onClick = { onFilterSelected("Draws") }
        )
    }
}

@Composable
private fun PlayerMatchHistoryFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(30.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (selected) Color(0xFF0757C8) else Color(0xFFEAF0FB))
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) BrandWhite else Color(0xFF0757C8),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PlayerMatchHistoryCard(
    match: PlayerMatchHistoryItem
) {
    val resultColor = when (match.resultado) {
        "WIN" -> BrandGreen
        "LOSS" -> Color(0xFFE53935)
        "DRAW" -> Color(0xFF6D7486)
        else -> Color(0xFF6D7486)
    }

    val resultBackground = resultColor.copy(alpha = 0.12f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlayerMatchHistoryResultBadge(
                    text = match.resultado,
                    backgroundColor = resultBackground,
                    textColor = resultColor
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = match.torneioNome,
                    color = BrandBlue,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlayerMatchHistoryTeamBlock(
                    teamName = match.minhaEquipaNome
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${match.meusPontos} - ${match.adversarioPontos}",
                    color = BrandBlue,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                PlayerMatchHistoryTeamBlock(
                    teamName = match.adversarioNome
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📅 ${playerMatchHistoryFormatDate(match.data)}",
                    color = Color(0xFF6D7486),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "📍 ${match.local}",
                    color = Color(0xFF6D7486),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = playerMatchHistoryShortResult(match),
                    color = BrandGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
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
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PlayerMatchHistoryTeamBlock(
    teamName: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(78.dp)
    ) {
        Box(
            modifier = Modifier
                .height(44.dp)
                .width(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFEAF0FB)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = playerMatchHistoryInitials(teamName),
                color = BrandBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = teamName,
            color = BrandBlue,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2
        )
    }
}

@Composable
private fun PlayerMatchHistoryMessageCard(
    text: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(18.dp)
        )
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