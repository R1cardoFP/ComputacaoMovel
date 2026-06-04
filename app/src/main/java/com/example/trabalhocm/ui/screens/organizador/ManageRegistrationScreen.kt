package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar

private val DarkBlue = Color(0xFF0B1F3A)
private val PrimaryBlue = Color(0xFF2563EB)
private val TealGreen = Color(0xFF059669)
private val ErrorRed = Color(0xFFDC2626)
private val LightRed = Color(0xFFFEE2E2)
private val WarningYellow = Color(0xFFD97706)
private val LightYellow = Color(0xFFFEF3C7)
private val TextGray = Color(0xFF64748B)
private val BgLight = Color(0xFFF8FAFC)
private val CardBg = Color(0xFFFFFFFF)
private val InputBg = Color(0xFFF1F5F9)

data class TeamApplication(
    val name: String,
    val captain: String,
    val tier: String,
    val players: String,
    val winRate: String,
    val applied: String,
    val isPaid: Boolean,
    val amount: String,
    val status: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageRegistrationScreen(
    onBackClick: () -> Unit = {},
    onInviteTeamClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("Pending") }

    val applications = listOf(
        TeamApplication("SC Braga", "M. Silva", "Premier Tier", "11 / 11", "72%", "2h ago", true, "Paid €100", "Pending"),
        TeamApplication("SC Vianense", "J. Costa", "Division A", "10 / 11", "58%", "5h ago", false, "Unpaid", "Pending"),
        TeamApplication("Sporting", "A. Lima", "Premier Tier", "11 / 11", "65%", "1d ago", true, "Paid €100", "Approved"),
        TeamApplication("Benfica", "P. Neves", "Premier Tier", "11 / 11", "70%", "1d ago", true, "Paid €100", "Approved"),
        TeamApplication("Porto", "R. Costa", "Premier Tier", "11 / 11", "68%", "2d ago", true, "Paid €100", "Approved")
    )

    val filteredApps = applications.filter {
        (selectedTab == "All" || it.status == selectedTab) &&
                it.name.contains(searchQuery, ignoreCase = true)
    }

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
                Text("Manage Registration", color = DarkBlue, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Review applications and confirm participating teams.", color = TextGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Status Card
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(DarkBlue, Color(0xFF0F2B5B))), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Premier Summer Cup 2026", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Surface(color = TealGreen.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp)) {
                                Text("• OPEN", color = TealGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("TEAMS REGISTERED", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("12", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                            Text(" / 16", color = TextGray, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 6.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(progress = { 12f / 16f }, modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)), color = TealGreen, trackColor = Color.White.copy(alpha = 0.1f))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("⏱ Registration closes in 3 days", color = WarningYellow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Button(
                    onClick = onInviteTeamClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier.width(160.dp).height(40.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("INVITE TEAM", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            item {
                TextField(
                    value = searchQuery, onValueChange = { searchQuery = it },
                    placeholder = { Text("Search teams or captains...", color = TextGray, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp)) },
                    modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(focusedContainerColor = InputBg, unfocusedContainerColor = InputBg, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                )
            }

            // Tabs
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("All" to 15, "Pending" to 3, "Approved" to 12).forEach { (tab, count) ->
                        val isSelected = selectedTab == tab
                        Surface(
                            color = if (isSelected) PrimaryBlue else CardBg,
                            shape = RoundedCornerShape(8.dp),
                            border = if (!isSelected) BorderStroke(1.dp, InputBg) else null,
                            modifier = Modifier.weight(1f).height(40.dp).clickable { selectedTab = tab }
                        ) {
                            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                Text(tab, color = if (isSelected) Color.White else TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(color = if (isSelected) Color.White.copy(alpha = 0.2f) else InputBg, shape = RoundedCornerShape(4.dp)) {
                                    Text("$count", color = if (isSelected) Color.White else TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Section Title
            item {
                Spacer(modifier = Modifier.height(8.dp))
                val title = if (selectedTab == "All") "ALL APPLICATIONS" else if (selectedTab == "Pending") "PENDING APPROVAL (3)" else "APPROVED TEAMS (12)"
                Text(title, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            }

            // List
            items(filteredApps) { app ->
                if (app.status == "Pending") {
                    PendingTeamCard(app)
                } else {
                    ApprovedTeamCard(app)
                }
            }

            item {
                Surface(color = Color(0xFFEEF2FF), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("When the tournament fills up, new applications go to the waitlist automatically.", color = DarkBlue, fontSize = 12.sp, lineHeight = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun PendingTeamCard(app: TeamApplication) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(8.dp), elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
            .drawBehind { drawLine(color = ErrorRed, start = Offset(0f, 0f), end = Offset(0f, size.height), strokeWidth = 12f) }
    ) {
        Column(modifier = Modifier.padding(16.dp).padding(start = 4.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(InputBg), contentAlignment = Alignment.Center) { Text(app.name.take(2), fontWeight = FontWeight.Bold) }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(app.name, color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Captain: ${app.captain} • ${app.tier}", color = TextGray, fontSize = 10.sp)
                    }
                }
                Surface(color = LightYellow, shape = RoundedCornerShape(12.dp)) {
                    Text("PENDING", color = WarningYellow, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = InputBg)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("PLAYERS", color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(app.players, color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("APPLIED", color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(app.applied, color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("WIN RATE", color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(app.winRate, color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("PAYMENT", color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (app.isPaid) Icons.Default.Check else Icons.Default.Close, contentDescription = null, tint = if (app.isPaid) TealGreen else ErrorRed, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(app.amount, color = if (app.isPaid) TealGreen else ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = {}, shape = RoundedCornerShape(6.dp), colors = ButtonDefaults.buttonColors(containerColor = LightRed),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text("× REJECT", color = ErrorRed, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                if (app.isPaid) {
                    Button(onClick = {}, shape = RoundedCornerShape(6.dp), colors = ButtonDefaults.buttonColors(containerColor = TealGreen), modifier = Modifier.weight(1f).height(40.dp)) {
                        Text("✓ APPROVE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                } else {
                    OutlinedButton(onClick = {}, shape = RoundedCornerShape(6.dp), border = BorderStroke(1.dp, PrimaryBlue), modifier = Modifier.weight(1f).height(40.dp)) {
                        Text("⊙ DETAILS", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ApprovedTeamCard(app: TeamApplication) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(8.dp), elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
            .drawBehind { drawLine(color = TealGreen, start = Offset(0f, 0f), end = Offset(0f, size.height), strokeWidth = 12f) }
    ) {
        Row(modifier = Modifier.padding(16.dp).padding(start = 4.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(InputBg), contentAlignment = Alignment.Center) { Text(app.name.take(2), fontWeight = FontWeight.Bold) }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(app.name, color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(color = TealGreen.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) { Text("CONFIRMED", color = TealGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) }
                        Surface(color = Color(0xFFEEF2FF), shape = RoundedCornerShape(12.dp)) { Text("PAID", color = PrimaryBlue, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) }
                    }
                }
            }
            IconButton(onClick = {}, modifier = Modifier.size(36.dp).background(LightRed, RoundedCornerShape(8.dp))) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed, modifier = Modifier.size(18.dp))
            }
        }
    }
}