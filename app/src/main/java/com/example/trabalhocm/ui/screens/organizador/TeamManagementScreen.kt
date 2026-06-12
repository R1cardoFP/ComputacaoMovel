package com.example.trabalhocm.ui.screens

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
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R

private val DarkBlue = Color(0xFF111827)
private val PrimaryBlue = Color(0xFF0346B8)
private val TextGray = Color(0xFF64748B)
private val BgLight = Color(0xFFF8FAFC)
private val CardBg = Color(0xFFFFFFFF)
private val InputBg = Color(0xFFF1F5F9)
private val LightBlueBadge = Color(0xFFE0E7FF)
private val ErrorRed = Color(0xFFDC2626)
private val LightRedBadge = Color(0xFFFEE2E2)

data class Player(
    val name: String,
    val position: String,
    val number: Int,
    val isCaptain: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TeamManagementScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onInviteClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    val filterAll = stringResource(R.string.filter_all)
    val roleForward = stringResource(R.string.role_forward)
    val roleMidfielder = stringResource(R.string.role_midfielder)
    val roleDefender = stringResource(R.string.role_defender)
    val roleGoalkeeper = stringResource(R.string.role_goalkeeper)

    var selectedFilter by remember(filterAll) { mutableStateOf(filterAll) }

    val filters = listOf(filterAll, roleForward, roleMidfielder, roleDefender, roleGoalkeeper)

    val roster = listOf(
        Player("Bruno Fernandes", roleMidfielder, 10, isCaptain = true),
        Player("Cristiano Ronaldo", roleForward, 9),
        Player("Rúben Dias", roleDefender, 4),
        Player("Diogo Costa", roleGoalkeeper, 1),
        Player("João Cancelo", roleDefender, 20),
        Player("Bernardo Silva", roleMidfielder, 8),
        Player("João Félix", roleForward, 11)
    )

    val filteredRoster = roster.filter { player ->
        val matchesSearch = player.name.contains(searchQuery, ignoreCase = true)
        val matchesPosition = if (selectedFilter == filterAll) true else player.position == selectedFilter
        matchesSearch && matchesPosition
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_teams), color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.desc_back), tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = stringResource(R.string.desc_notifications), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        bottomBar = {
            MatchLeagueBottomBar(selectedTab = "TEAMS", onHomeClick = onHomeClick)
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
                Text(stringResource(R.string.tag_team_management), color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.desc_team_management), color = TextGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(CardBg.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("FC", color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text("FC Mancos", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(stringResource(R.string.mock_team_subtitle), color = Color.LightGray, fontSize = 12.sp)
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = onInviteClick,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.btn_invite_player_caps), fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                    }
                }
            }

            item {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(stringResource(R.string.placeholder_search_roster), color = Color.LightGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, InputBg, RoundedCornerShape(8.dp)),
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
            }

            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { filter ->
                        val isSelected = selectedFilter == filter
                        Surface(
                            color = if (isSelected) PrimaryBlue else LightBlueBadge,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.clickable { selectedFilter = filter }
                        ) {
                            Text(
                                text = filter,
                                color = if (isSelected) Color.White else PrimaryBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.format_roster_count, filteredRoster.size),
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            items(filteredRoster) { player ->
                PlayerCard(player = player)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun PlayerCard(player: Player) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    if (player.isCaptain) {
                        drawLine(
                            color = ErrorRed,
                            start = androidx.compose.ui.geometry.Offset(0f, 0f),
                            end = androidx.compose.ui.geometry.Offset(0f, size.height),
                            strokeWidth = 12f
                        )
                    }
                }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(InputBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.name.split(" ").take(2).joinToString("") { it.take(1) },
                    color = DarkBlue,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(player.name, color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    if (player.isCaptain) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = LightRedBadge,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                stringResource(R.string.badge_captain_caps),
                                color = ErrorRed,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("${player.position} • #${player.number}", color = TextGray, fontSize = 12.sp)
            }

            IconButton(onClick = {  }) {
                Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.desc_options), tint = TextGray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TeamManagementScreenPreview() {
    MaterialTheme {
        TeamManagementScreen()
    }
}