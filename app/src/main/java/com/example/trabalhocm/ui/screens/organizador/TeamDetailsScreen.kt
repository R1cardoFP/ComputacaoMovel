package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.*

private val LightRedBadge = Color(0xFFFEE2E2)

data class TeamPlayer(
    val name: String,
    val position: String,
    val number: Int,
    val isCaptain: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailsScreen(
    isUserTeam: Boolean = false,
    onBackClick: () -> Unit = {},
    onInvitePlayerClick: () -> Unit = {},
    onViewPlayerProfileClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val roster = listOf(
        TeamPlayer("Cristiano Ronaldo", stringResource(R.string.role_striker), 9),
        TeamPlayer("Bruno Fernandes", stringResource(R.string.role_midfielder), 10, isCaptain = true),
        TeamPlayer("Rúben Dias", stringResource(R.string.role_defender), 4),
        TeamPlayer("Diogo Costa", stringResource(R.string.role_goalkeeper), 1)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_team_details), color = Color.White, fontWeight = FontWeight.Bold) },
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
            MatchLeagueBottomBar(
                selectedTab = "TEAMS",
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
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkBlue)
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("SLB", color = ErrorRed, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Surface(color = Color(0xFF374151), shape = RoundedCornerShape(12.dp)) {
                                Text(stringResource(R.string.mock_premier_division), color = Color(0xFFFCA5A5), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Benfica", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(stringResource(R.string.mock_est_2018), color = Color.LightGray, fontSize = 12.sp)
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(stringResource(R.string.label_season_win_rate), color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("68.5 %", color = DarkBlue, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Box(
                                modifier = Modifier.size(48.dp).clip(CircleShape).border(2.dp, InputBg, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("↗", color = TealGreen, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardBg),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.label_total_goals), color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("42", color = DarkBlue, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(stringResource(R.string.mock_plus_5_month), color = TealGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = CardBg),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.DateRange, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.label_matches_played), color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("24", color = DarkBlue, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(stringResource(R.string.mock_w_d_l), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.title_active_roster), color = DarkBlue, fontSize = 20.sp, fontWeight = FontWeight.Bold)

                    Button(
                        onClick = onInvitePlayerClick,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.btn_invite_player), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            items(roster) { player ->
                Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                    RosterPlayerCard(player = player, onViewProfile = onViewPlayerProfileClick)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun RosterPlayerCard(player: TeamPlayer, onViewProfile: () -> Unit) {
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
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            strokeWidth = 12f
                        )
                    }
                }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(48.dp).clip(CircleShape).background(InputBg),
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
                    Text(player.name, color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)

                    if (player.isCaptain) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = LightRedBadge, shape = RoundedCornerShape(12.dp)) {
                            Text(stringResource(R.string.badge_captain), color = ErrorRed, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("${player.position} • #${player.number}", color = TextGray, fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = onViewProfile,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, InputBg),
                modifier = Modifier.height(32.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Text(stringResource(R.string.btn_view_profile_roster), color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TeamDetailsScreenPreview() {
    MaterialTheme {
        TeamDetailsScreen()
    }
}