package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar

private val DarkBlue = Color(0xFF0B1F3A)
private val PrimaryBlue = Color(0xFF2563EB)
private val TealGreen = Color(0xFF059669)
private val TextGray = Color(0xFF64748B)
private val BgLight = Color(0xFFF8FAFC)
private val CardBg = Color(0xFFFFFFFF)
private val InputBg = Color(0xFFF1F5F9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerTournamentHistoryScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tournament History", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White) }
                },
                actions = {
                    IconButton(onClick = { }) { Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        bottomBar = {
            MatchLeagueBottomBar(
                selectedTab = "TOURNAMENTS",
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
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text("YOUR RECORDS", color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("My Tournament History", color = DarkBlue, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Tournaments you have organized or participated in.", color = TextGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                HistoryCard(
                    borderColor = TealGreen,
                    badges = listOf("COMPLETED" to Color(0xFFD1FAE5) to TealGreen, "CHAMPION" to Color(0xFFDBEAFE) to PrimaryBlue),
                    title = "Premier Summer Cup 2026",
                    subtitle = "Football · League System · Season 25/26",
                    stats = listOf("MY ROLE" to "Organizer", "FINAL POS." to "1st", "PLAYED" to "14", "GOALS" to "28"),
                    footer = "Champion: FC Mancos · Prize €125 000"
                )
            }

            item {
                HistoryCard(
                    borderColor = PrimaryBlue,
                    badges = listOf("ARCHIVED" to InputBg to TextGray, "RUNNER-UP" to Color(0xFFDBEAFE) to PrimaryBlue),
                    title = "Liga Regional Sul 2025",
                    subtitle = "Basketball · Knockout · Season 24/25",
                    stats = listOf("MY ROLE" to "Player", "FINAL POS." to "2nd", "PLAYED" to "9", "PTS/GAME" to "14.2")
                )
            }

            item {
                HistoryCard(
                    borderColor = TextGray,
                    badges = listOf("ARCHIVED" to InputBg to TextGray),
                    title = "Copa Inverno 2024",
                    subtitle = "Football · Group + Knockout · Season 23/24",
                    stats = listOf("MY ROLE" to "Player", "FINAL POS." to "QF", "PLAYED" to "5", "GOALS" to "3")
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun HistoryCard(
    borderColor: Color,
    badges: List<Pair<Pair<String, Color>, Color>>,
    title: String,
    subtitle: String,
    stats: List<Pair<String, String>>,
    footer: String? = null
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth().drawBehind { drawLine(color = borderColor, start = Offset(0f, 0f), end = Offset(0f, size.height), strokeWidth = 12f) }
    ) {
        Column(modifier = Modifier.padding(16.dp).padding(start = 4.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                badges.forEach { (badgeData, textColor) ->
                    val (text, bgColor) = badgeData
                    Surface(color = bgColor, shape = RoundedCornerShape(12.dp)) {
                        Text(text, color = textColor, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(subtitle, color = TextGray, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                stats.forEach { (label, value) ->
                    Column {
                        Text(label, color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(value, color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            if (footer != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(footer, color = TextGray, fontSize = 11.sp)
            }
        }
    }
}