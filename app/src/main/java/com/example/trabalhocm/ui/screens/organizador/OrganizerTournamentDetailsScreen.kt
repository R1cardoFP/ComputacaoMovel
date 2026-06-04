package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar

import com.example.trabalhocm.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerTournamentDetailsScreen(
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Details", color = Color.White, fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(DarkBlue, Color(0xFF0F2B5B))))
                    .padding(24.dp)
            ) {
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TourneyBadge("• IN PROGRESS", TealGreen, TealGreen.copy(alpha = 0.1f))
                        TourneyBadge("⚽ FOOTBALL", TealGreen, TealGreen.copy(alpha = 0.1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Premier Summer\nCup 2026", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 34.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        HeroStat("SEASON", "25/26")
                        HeroStat("PRIZE POOL", "125,000 €")
                        HeroStat("TEAMS", "24")
                    }
                }
            }

            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                Card(colors = CardDefaults.cardColors(containerColor = CardBg), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("About", color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("A flagship summer competition for Premier Tier clubs across the western region. League format with playoff finals.", color = TextGray, fontSize = 14.sp, lineHeight = 20.sp)
                    }
                }

                Card(colors = CardDefaults.cardColors(containerColor = CardBg), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Schedule", color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        DetailRow("Start Date", "15 Jun 2026")
                        DetailRow("End Date", "30 Aug 2026")
                        DetailRow("Registration Closes", "01 Jun 2026")
                        DetailRow("Format", "League System")
                    }
                }

                Card(colors = CardDefaults.cardColors(containerColor = CardBg), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Outlined.Place, contentDescription = null, tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Estádio Cidade de Barcelos", color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("Barcelos, Portugal", color = TextGray, fontSize = 12.sp)
                        }
                    }
                }

                Card(colors = CardDefaults.cardColors(containerColor = CardBg), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Standings", color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(modifier = Modifier.weight(1f)) {
                                Text("POS", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(40.dp))
                                Text("TEAM", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Text("P", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("PTS", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Divider(color = InputBg)
                        Spacer(modifier = Modifier.height(12.dp))

                        StandingRow("01", "Porto", "🔵", "12", "31")
                        StandingRow("02", "Sporting", "🟢", "12", "29")
                        StandingRow("03", "Benfica", "🔴", "12", "27")
                        StandingRow("04", "Vianense", "🟡", "12", "23")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun HeroStat(label: String, value: String) {
    Column {
        Text(label, color = TealGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextGray, fontSize = 14.sp)
        Text(value, color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun StandingRow(pos: String, team: String, icon: String, p: String, pts: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Text(pos, color = TealGreen, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.width(40.dp))
            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                Text(icon, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(team, color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(p, color = TextGray, fontSize = 14.sp)
            Text(pts, color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Preview(showBackground = true, name = "Organizer Tournament Details")
@Composable
fun OrganizerTournamentDetailsScreenPreview() {
    MaterialTheme {
        OrganizerTournamentDetailsScreen()
    }
}