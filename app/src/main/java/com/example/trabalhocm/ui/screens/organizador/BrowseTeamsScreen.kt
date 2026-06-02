package com.example.trabalhocm.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Notifications
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
import com.example.trabalhocm.ui.theme.*

data class Team(
    val name: String,
    val division: String?,
    val wins: Int,
    val losses: Int,
    val streak: String,
    val isMyTeam: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowseTeamsScreen(
    onCreateTeamClick: () -> Unit = {},
    onManageTeamClick: () -> Unit = {},
    onViewDetailsClick: (Boolean) -> Unit = {}, // <-- O PARÂMETRO QUE FALTAVA!
    onHomeClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val tabs = listOf("All Teams", "Division A", "Division B")
    var selectedTab by remember { mutableStateOf(tabs[0]) }

    val allTeams = listOf(
        Team("FC Mancos", null, 21, 4, "W3", isMyTeam = true),
        Team("Benfica", "DIVISION B", 24, 6, "W5"),
        Team("Porto", "DIVISION A", 18, 12, "L2"),
        Team("Vianense", "DIVISION A", 15, 15, "W1"),
        Team("Sporting", "DIVISION B", 8, 22, "L5")
    )

    val filteredTeams = allTeams.filter { team ->
        val matchesSearch = team.name.contains(searchQuery, ignoreCase = true)
        val matchesTab = when (selectedTab) {
            "Division A" -> team.division == "DIVISION A" || team.isMyTeam
            "Division B" -> team.division == "DIVISION B" || team.isMyTeam
            else -> true
        }
        matchesSearch && matchesTab
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Teams", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        bottomBar = {
            Column(modifier = Modifier.background(BgLight)) {
                Button(
                    onClick = onCreateTeamClick,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                        .height(48.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CREATE TEAM", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                    }
                }
                MatchLeagueBottomBar(selectedTab = "TEAMS", onHomeClick = onHomeClick)
            }
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
                Text("PREMIER LEAGUE TEAMS", color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Browse Teams", color = DarkBlue, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Discover all active teams across the league ecosystem.", color = TextGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search for teams...", color = Color.LightGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CardBg,
                        unfocusedContainerColor = CardBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tabs.forEach { tab ->
                        val isSelected = selectedTab == tab
                        Surface(
                            color = if (isSelected) PrimaryBlue else CardBg,
                            shape = RoundedCornerShape(8.dp),
                            border = if (isSelected) null else BorderStroke(1.dp, InputBg),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = tab }
                        ) {
                            Text(
                                text = tab,
                                color = if (isSelected) Color.White else PrimaryBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(filteredTeams) { team ->
                if (team.isMyTeam) {
                    MyTeamCard(
                        team = team,
                        onManageTeamClick = onManageTeamClick,
                        onViewDetailsClick = { onViewDetailsClick(true) } // Passa que é a tua equipa
                    )
                } else {
                    RegularTeamCard(
                        team = team,
                        onViewDetailsClick = { onViewDetailsClick(false) } // Passa que é equipa dos outros
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun MyTeamCard(
    team: Team,
    onManageTeamClick: () -> Unit = {},
    onViewDetailsClick: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, TealGreen),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(DarkBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(team.name.take(1), color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(team.name, color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(color = TealGreen.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                        Text("YOUR TEAM", color = TealGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("WINS", team.wins.toString(), PrimaryBlue)
                StatItem("LOSSES", team.losses.toString(), DarkBlue)
                StatItem("STREAK", team.streak, TealGreen)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetailsClick,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, PrimaryBlue),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text("VIEW DETAILS", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Button(
                    onClick = onManageTeamClick,
                    colors = ButtonDefaults.buttonColors(containerColor = TealGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text("MANAGE TEAM", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun RegularTeamCard(
    team: Team,
    onViewDetailsClick: () -> Unit = {}
) {
    val streakColor = if (team.streak.startsWith("W")) TealGreen else ErrorRed

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(InputBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(team.name.take(1), color = DarkBlue, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(team.name, color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(color = PrimaryBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                        Text(team.division ?: "", color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("WINS", team.wins.toString(), PrimaryBlue)
                StatItem("LOSSES", team.losses.toString(), DarkBlue)
                StatItem("STREAK", team.streak, streakColor)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onViewDetailsClick,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, PrimaryBlue),
                modifier = Modifier.fillMaxWidth().height(40.dp)
            ) {
                Text("VIEW DETAILS →", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, valueColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = valueColor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Preview(showBackground = true)
@Composable
fun BrowseTeamsScreenPreview() {
    MaterialTheme {
        BrowseTeamsScreen()
    }
}