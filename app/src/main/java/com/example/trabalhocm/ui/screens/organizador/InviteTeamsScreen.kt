package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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

data class TeamInvite(val name: String, val captain: String, val tier: String, val status: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteTeamsScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    val suggestedTeams = listOf(
        TeamInvite("Benfica", "P. Neves", "Premier Tier", "None"),
        TeamInvite("Porto", "R. Costa", "Premier Tier", "None"),
        TeamInvite("Sporting", "A. Lima", "Division A", "None"),
        TeamInvite("Vianense", "J. Pereira", "Division A", "Invited"),
        TeamInvite("SC Braga", "M. Silva", "Premier Tier", "None")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registration", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
                Text("ORGANIZER TOOL", color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Invite Teams", color = DarkBlue, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Send invitations for Premier Summer Cup 2026.", color = TextGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Card(colors = CardDefaults.cardColors(containerColor = DarkBlue), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text("SLOTS REMAINING", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("8", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                                Text(" / 32", color = TextGray, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 6.dp))
                            }
                        }
                        Surface(color = Color(0xFFD1FAE5), shape = RoundedCornerShape(12.dp)) {
                            Text("INVITE ONLY", color = TealGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                        }
                    }
                }
            }

            item {
                Text("SEARCH TEAMS", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = searchQuery, onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by team name or captain...", color = TextGray, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp)) },
                    modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(8.dp))
                        .background(CardBg),
                    colors = TextFieldDefaults.colors(focusedContainerColor = CardBg, unfocusedContainerColor = CardBg, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item { Text("SUGGESTED TEAMS", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp) }

            items(suggestedTeams) { team ->
                Card(colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(1.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(InputBg), contentAlignment = Alignment.Center) { Text(team.name.take(2), fontWeight = FontWeight.Bold) }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(team.name, color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                Text("Captain: ${team.captain} • ${team.tier}", color = TextGray, fontSize = 10.sp)
                            }
                        }
                        if (team.status == "Invited") {
                            Surface(color = InputBg, shape = RoundedCornerShape(6.dp)) {
                                Text("INVITED", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                            }
                        } else {
                            Button(onClick = {}, shape = RoundedCornerShape(6.dp), colors = ButtonDefaults.buttonColors(containerColor = TealGreen), contentPadding = PaddingValues(horizontal = 16.dp)) {
                                Text("INVITE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text("SENT INVITATIONS (1)", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(color = Color(0xFFEEF2FF), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Vianense was invited 2 days ago. Awaiting response.", color = PrimaryBlue, fontSize = 12.sp, lineHeight = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}