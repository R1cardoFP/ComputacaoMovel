package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar

import com.example.trabalhocm.ui.theme.*

private val WarningYellow = Color(0xFFF59E0B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerTournamentsScreen(
    onHistoryClick: () -> Unit = {},
    onFiltersClick: () -> Unit = {},
    onCreateNewClick: () -> Unit = {},
    onDetailsClick: () -> Unit = {},
    onInviteTeamsClick: () -> Unit = {},
    onManageRegistrationClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onEditClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("List", color = Color.White, fontWeight = FontWeight.Bold) },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text("Tournament Management", color = DarkBlue, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 32.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Visualize and manage all your active and upcoming leagues.", color = TextGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onHistoryClick,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, InputBg),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = CardBg),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("HISTORY", color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onCreateNewClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text("CREATE NEW", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search tournaments...", color = TextGray, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CardBg, unfocusedContainerColor = CardBg,
                        focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(color = InputBg, shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f).height(40.dp)) {
                        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 12.dp)) {
                            Text("Status: All", color = TextGray, fontSize = 12.sp)
                        }
                    }
                    Surface(color = InputBg, shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f).height(40.dp)) {
                        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 12.dp)) {
                            Text("Region: All", color = TextGray, fontSize = 12.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onFiltersClick,
                    shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, InputBg),
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Text("FILTERS", color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                OrganizerTournamentCard(
                    status = "• LIVE", statusColor = ErrorRed, tags = listOf("PRO LEAGUE", "FOOTBALL"),
                    title = "Premier Summer Cup 2026", dates = "15 Jun — 30 Aug",
                    teams = 24, gamesToday = 6,
                    onDetailsClick = onDetailsClick,
                    onInviteTeamsClick = onInviteTeamsClick,
                    onManageRegistrationClick = onManageRegistrationClick,
                    onEditClick = onEditClick
                )
            }

            item {
                RegularTournamentCard(
                    status = "• OPEN", statusColor = TealGreen, tags = listOf("AMATEUR", "BASKETBALL"),
                    title = "Liga Regional Sul", dates = "Start: 10 Sep",
                    registered = 12, capacity = 16, actionText = "REGISTER NOW", actionColor = TealGreen,
                    onDetailsClick = onDetailsClick
                )
            }

            item {
                RegularTournamentCard(
                    status = "• SOLD OUT", statusColor = WarningYellow, tags = listOf("PRO LEAGUE", "FOOTBALL"),
                    title = "Atlantic Cup 2026", dates = "Start: 1 Oct",
                    registered = 32, capacity = 32, actionText = "ALL SLOTS FILLED", actionColor = InputBg, isActionEnabled = false,
                    onDetailsClick = onDetailsClick
                )
            }

            item {
                RegularTournamentCard(
                    status = "• INVITE ONLY", statusColor = PrimaryBlue, tags = listOf("ELITE", "VOLLEYBALL"),
                    title = "Elite Invitational 2026", dates = "Start: 12 Nov",
                    registered = null, capacity = null, actionText = "INVITE ONLY — LOCKED", actionColor = InputBg, isActionEnabled = false,
                    onDetailsClick = onDetailsClick
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun OrganizerTournamentCard(
    status: String, statusColor: Color, tags: List<String>, title: String, dates: String,
    teams: Int, gamesToday: Int, onDetailsClick: () -> Unit,
    onInviteTeamsClick: () -> Unit = {},
    onManageRegistrationClick: () -> Unit = {},
    onEditClick: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TourneyBadge(status, statusColor, statusColor.copy(alpha = 0.1f))
                    tags.forEach { TourneyBadge(it, PrimaryBlue, PrimaryBlue.copy(alpha = 0.1f)) }
                }
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = "Edit",
                    tint = TealGreen,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onEditClick() }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("YOU ORGANIZE", color = TealGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, color = DarkBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("📅 $dates", color = TextGray, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Column {
                    Text("TEAMS", color = DarkBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("$teams", color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
                Column {
                    Text("GAMES TODAY", color = DarkBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("$gamesToday", color = PrimaryBlue, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDetailsClick, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, PrimaryBlue), modifier = Modifier.weight(1f).height(40.dp)) {
                    Text("DETAILS", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
                OutlinedButton(onClick = onInviteTeamsClick, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, PrimaryBlue), modifier = Modifier.weight(1f).height(40.dp)) {
                    Text("INVITE TEAMS", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onManageRegistrationClick, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue), modifier = Modifier.fillMaxWidth().height(40.dp)) {
                Text("MANAGE REGISTRATION", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun RegularTournamentCard(
    status: String, statusColor: Color, tags: List<String>, title: String, dates: String,
    registered: Int?, capacity: Int?, actionText: String, actionColor: Color, isActionEnabled: Boolean = true,
    onDetailsClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TourneyBadge(status, statusColor, statusColor.copy(alpha = 0.1f))
                tags.forEach { TourneyBadge(it, TextGray, InputBg) }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, color = DarkBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("📅 $dates", color = TextGray, fontSize = 12.sp)

            if (registered != null && capacity != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("REGISTERED", color = DarkBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text("$registered/$capacity", color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { registered.toFloat() / capacity.toFloat() },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = statusColor, trackColor = InputBg,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDetailsClick, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, PrimaryBlue), modifier = Modifier.weight(1f).height(40.dp)) {
                    Text("DETAILS", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
                Button(
                    onClick = { }, enabled = isActionEnabled, shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = actionColor, disabledContainerColor = actionColor),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text(actionText, color = if (isActionEnabled) Color.White else TextGray, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun TourneyBadge(text: String, textColor: Color, bgColor: Color) {
    Surface(color = bgColor, shape = RoundedCornerShape(12.dp)) {
        Text(text, color = textColor, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }
}

@Preview(showBackground = true, name = "Organizer Tournaments List")
@Composable
fun OrganizerTournamentsScreenPreview() {
    MaterialTheme {
        OrganizerTournamentsScreen()
    }
}