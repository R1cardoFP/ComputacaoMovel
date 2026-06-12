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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trabalhocm.R
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
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
    viewModel: OrganizerMatchesViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val tabAll = stringResource(R.string.tab_all)
    val tabWins = stringResource(R.string.tab_wins)
    val tabLosses = stringResource(R.string.tab_losses)
    val tabDraws = stringResource(R.string.tab_draws)

    val tabs = listOf(tabAll, tabWins, tabLosses, tabDraws)
    var selectedTab by remember { mutableStateOf(tabs[0]) }

    val allMatches = viewModel.matches
    val isLoading = viewModel.isLoading

    val filteredMatches = allMatches.filter { match ->
        when (selectedTab) {
            tabWins -> match.result == MatchResult.WIN
            tabLosses -> match.result == MatchResult.LOSS
            tabDraws -> match.result == MatchResult.DRAW
            else -> true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_my_matches), color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.desc_back), tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.carregarJogos() }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = stringResource(R.string.desc_refresh), tint = Color.White)
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
                Text(stringResource(R.string.tag_personal_archive), color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(R.string.title_my_matches), color = DarkBlue, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.desc_personal_archive), color = TextGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))

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

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TealGreen)
                    }
                }
            } else if (filteredMatches.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.msg_no_matches_found), color = TextGray)
                    }
                }
            } else {
                items(filteredMatches) { match ->
                    MatchArchiveCard(match)
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun MatchArchiveCard(match: MatchArchiveItem) {
    val badgeWin = stringResource(R.string.badge_win)
    val badgeLoss = stringResource(R.string.badge_loss)
    val badgeDraw = stringResource(R.string.badge_draw)

    val (badgeBg, badgeText, badgeString) = when (match.result) {
        MatchResult.WIN -> Triple(TealGreen.copy(alpha = 0.15f), TealGreen, badgeWin)
        MatchResult.LOSS -> Triple(ErrorRed.copy(alpha = 0.15f), ErrorRed, badgeLoss)
        MatchResult.DRAW -> Triple(InputBg, TextGray, badgeDraw)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                Text(
                    text = match.score,
                    color = DarkBlue,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

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