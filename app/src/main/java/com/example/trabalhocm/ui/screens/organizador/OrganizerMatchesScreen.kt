package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar

// IMPORT DAS CORES CENTRALIZADAS!
import com.example.trabalhocm.ui.theme.*

enum class MatchResult { WIN, LOSS, DRAW }

data class MatchArchiveItem(
    val id: Int,
    val result: MatchResult,
    val context: String,
    val team1Name: String,
    val team2Name: String,
    val score: String,
    val date: String,
    val location: String,
    val stats: String,
    val isStatsHighlight: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerMatchesScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    // Filtros
    val tabs = listOf("All", "Wins", "Losses", "Draws")
    var selectedTab by remember { mutableStateOf(tabs[0]) }

    // Dados baseados no Figma
    val allMatches = listOf(
        MatchArchiveItem(
            id = 1,
            result = MatchResult.WIN,
            context = "Premier Summer Cup • Group A",
            team1Name = "FC Mancos",
            team2Name = "Vianense",
            score = "3 - 1",
            date = "14 Oct 2026",
            location = "Estádio Cidade",
            stats = "2 G • 1 A",
            isStatsHighlight = true
        ),
        MatchArchiveItem(
            id = 2,
            result = MatchResult.DRAW,
            context = "League Match",
            team1Name = "FC Mancos",
            team2Name = "Porto",
            score = "0 - 0",
            date = "10 Oct 2026",
            location = "Estádio do Dragão",
            stats = "Full Time",
            isStatsHighlight = false
        ),
        MatchArchiveItem(
            id = 3,
            result = MatchResult.LOSS,
            context = "Quarter Finals • Copa Inverno",
            team1Name = "FC Mancos",
            team2Name = "Benfica",
            score = "1 - 4",
            date = "03 Oct 2026",
            location = "Estádio da Luz",
            stats = "1 G",
            isStatsHighlight = true
        ),
        MatchArchiveItem(
            id = 4,
            result = MatchResult.WIN,
            context = "Exhibition",
            team1Name = "Porto",
            team2Name = "Sporting",
            score = "2 - 0",
            date = "28 Sep 2026",
            location = "José Alvalade",
            stats = "1 G • 1 A",
            isStatsHighlight = true
        )
    )

    // Lógica mágica de filtrar a lista
    val filteredMatches = allMatches.filter { match ->
        when (selectedTab) {
            "Wins" -> match.result == MatchResult.WIN
            "Losses" -> match.result == MatchResult.LOSS
            "Draws" -> match.result == MatchResult.DRAW
            else -> true // "All"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Matches", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        bottomBar = {
            MatchLeagueBottomBar(
                selectedTab = "MATCHES",
                onHomeClick = onHomeClick,
                onTournamentsClick = onTournamentsClick,
                onMatchesClick = onMatchesClick,
                onTeamsClick = onTeamsClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = BgLight
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text("PERSONAL ARCHIVE", color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("My Matches", color = DarkBlue, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("All matches you have personally participated in.", color = TextGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))

                // TABS DE FILTRO
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabs.forEach { tab ->
                        val isSelected = selectedTab == tab
                        Surface(
                            color = if (isSelected) PrimaryBlue else PrimaryBlue.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { selectedTab = tab }
                        ) {
                            Text(
                                text = tab,
                                color = if (isSelected) Color.White else PrimaryBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // LISTA DE JOGOS FILTRADA
            items(filteredMatches) { match ->
                MatchArchiveCard(match)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun MatchArchiveCard(match: MatchArchiveItem) {
    // Definir as cores consoante o resultado
    val (badgeBg, badgeText, badgeString) = when (match.result) {
        MatchResult.WIN -> Triple(TealGreen.copy(alpha = 0.15f), TealGreen, "WIN")
        MatchResult.LOSS -> Triple(ErrorRed.copy(alpha = 0.15f), ErrorRed, "LOSS")
        MatchResult.DRAW -> Triple(InputBg, TextGray, "DRAW")
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // TOPO: Badge e Contexto
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(color = badgeBg, shape = RoundedCornerShape(12.dp)) {
                    Text(
                        text = badgeString,
                        color = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(match.context, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // MEIO: Equipas e Resultado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Equipa 1
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(match.team1Name, color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }

                // Resultado
                Text(
                    text = match.score,
                    color = DarkBlue,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // Equipa 2
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(InputBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = PrimaryBlue)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(match.team2Name, color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider(color = InputBg)
            Spacer(modifier = Modifier.height(16.dp))

            // RODAPÉ: Data, Local e Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.DateRange, contentDescription = null, tint = TextGray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(match.date, color = TextGray, fontSize = 10.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Place, contentDescription = null, tint = TextGray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(match.location, color = TextGray, fontSize = 10.sp)
                }

                Text(
                    text = match.stats,
                    color = if (match.isStatsHighlight) TealGreen else TextGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrganizerMatchesScreenPreview() {
    MaterialTheme {
        OrganizerMatchesScreen()
    }
}