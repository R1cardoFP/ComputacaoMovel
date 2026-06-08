package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trabalhocm.data.model.Torneio
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar

private val DarkBlue = Color(0xFF152238)
private val EmeraldGreen = Color(0xFF0E8A6F)
private val BgLight = Color(0xFFF7F7F9)
private val TextGray = Color(0xFF6B7280)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: OrganizerHomeViewModel = viewModel(),
    onVerTorneios: () -> Unit = {},
    onCreateTournamentClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.carregarDashboard() }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notificações", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        bottomBar = {
            MatchLeagueBottomBar(
                selectedTab = "HOME",
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            LiveMatchSection()
            QuickActionsSection(onCreateTournamentClick = onCreateTournamentClick)

            ActiveTournamentsSection(
                tournaments = viewModel.activeTournaments,
                isLoading = viewModel.isLoading,
                onViewAllClick = onTournamentsClick
            )

            UpcomingFixturesSection()
            PerformanceInsightsSection()
        }
    }
}

@Composable
fun ActiveTournamentsSection(
    tournaments: List<Torneio>,
    isLoading: Boolean,
    onViewAllClick: () -> Unit
) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            SectionTitle("MY ACTIVE TOURNAMENTS")
            Text(
                "VIEW ALL",
                color = Color(0xFF2B5BFE),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EmeraldGreen)
            }
        } else if (tournaments.isEmpty()) {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                Text("No active tournaments at the moment.", color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(16.dp))
            }
        } else {
            tournaments.forEach { torneio ->
                TournamentCard(
                    title = torneio.nome,
                    role = "ORGANIZER",
                    progress = if (torneio.estado == "aberto") 25 else 75,
                    color = if (torneio.estado == "aberto") EmeraldGreen else Color(0xFF2B5BFE)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun QuickActionsSection(onCreateTournamentClick: () -> Unit) {
    Column {
        SectionTitle("QUICK ACTIONS")
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(modifier = Modifier.weight(1f), icon = Icons.Default.Star, title = "CREATE TOURNAMENT", tint = EmeraldGreen, onClick = onCreateTournamentClick)
            QuickActionCard(modifier = Modifier.weight(1f), icon = Icons.Default.Add, title = "CREATE CASUAL\nMATCH", tint = EmeraldGreen)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            QuickActionCard(modifier = Modifier.weight(1f), icon = Icons.Default.Share, title = "LIVE MATCHS", tint = Color(0xFF6366F1))
            QuickActionCard(modifier = Modifier.weight(1f), icon = Icons.Default.Person, title = "CREATE TEAM", tint = EmeraldGreen)
        }
    }
}

@Composable
fun QuickActionCard(modifier: Modifier = Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, tint: Color, onClick: () -> Unit = {}) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                color = tint.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.padding(12.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun LiveMatchSection() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, EmeraldGreen.copy(alpha = 0.5f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(EmeraldGreen))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("LIVE NOW", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text("PREMIER LEAGUE • GW 26", color = Color.Gray, fontSize = 10.sp, letterSpacing = 1.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Star, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(64.dp))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("1 - 2", color = Color.White, fontSize = 48.sp, fontWeight = FontWeight.Bold)
                    Text("83'", color = EmeraldGreen, fontSize = 14.sp)
                }

                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Yellow, modifier = Modifier.size(64.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Text("Sporting", color = Color.White)
                Text("Vianense", color = Color.White)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Text("WATCH STREAM")
            }
        }
    }
}

@Composable
fun TournamentCard(title: String, role: String, progress: Int, color: Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(color))
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(title, fontSize = 18.sp, color = DarkBlue)
                        Text(role, fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                    Icon(Icons.Default.Star, contentDescription = null, tint = color.copy(alpha = 0.3f))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("PROGRESS", fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Bold)
                    Text("$progress%", fontSize = 10.sp, color = TextGray, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = progress / 100f,
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                    color = color,
                    trackColor = color.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
fun UpcomingFixturesSection() {
    Column {
        SectionTitle("UPCOMING FIXTURES")
        Spacer(modifier = Modifier.height(12.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column {
                FixtureItem("SEP 22", "19:45")
                HorizontalDivider(color = BgLight, thickness = 1.dp)
                FixtureItem("SEP 24", "21:00")
            }
        }
    }
}

@Composable
fun FixtureItem(date: String, time: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(date, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(time, color = TextGray, fontSize = 12.sp)
        }
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Icon(Icons.Default.Star, contentDescription = null, tint = EmeraldGreen)
            Text("VS", color = Color(0xFFC3C6CF), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Icon(Icons.Default.Star, contentDescription = null, tint = Color.Blue)
        }
        Surface(color = BgLight, shape = RoundedCornerShape(8.dp)) {
            Icon(Icons.Outlined.Notifications, contentDescription = null, tint = DarkBlue, modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun PerformanceInsightsSection() {
    Column {
        SectionTitle("PERFORMANCE INSIGHTS")
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkBlue),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("PLAYER OF THE WEEK", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(3.dp, EmeraldGreen.copy(alpha = 0.3f)),
                    color = Color.Gray,
                    modifier = Modifier.size(100.dp)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.padding(24.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("CRISTIANO RONALDO", color = Color.White, fontSize = 20.sp)
                Text("Arabias • ATTK", color = EmeraldGreen, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    StatColumn("04", "GOALS")
                    StatColumn("02", "ASSISTS")
                    StatColumn("9.4", "RATING")
                }
            }
        }
    }
}

@Composable
fun StatColumn(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(label, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color(0xFF6B7280),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
}