package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.*

data class JoinedPlayer(val name: String, val joinedTime: String, val isYou: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasualMatchDetailsScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val players = listOf(
        JoinedPlayer("Cristiano Ronaldo", stringResource(R.string.joined_2h_ago), isYou = true),
        JoinedPlayer("André Lima", stringResource(R.string.joined_yesterday)),
        JoinedPlayer("Joana Costa", stringResource(R.string.joined_yesterday)),
        JoinedPlayer("Maria Santos", stringResource(R.string.joined_2_days_ago))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_match_details), color = Color.White, fontWeight = FontWeight.Bold) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Column {
                Text(stringResource(R.string.tag_pickup_game), color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Beach Volley Mix", color = DarkBlue, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.desc_match_subtitle), color = TextGray, fontSize = 14.sp, lineHeight = 20.sp)
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MatchDetailsTag(stringResource(R.string.tag_live_now), TealGreen, TealGreen.copy(alpha = 0.1f))
                        MatchDetailsTag(stringResource(R.string.tag_open_registration), PrimaryBlue, PrimaryBlue.copy(alpha = 0.1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    MatchDetailsTag(stringResource(R.string.sport_volleyball), TextGray, InputBg)

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(stringResource(R.string.label_spots_left), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Text("4", color = PrimaryBlue, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Column {
                            Text(stringResource(R.string.label_joined), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Text("8", color = DarkBlue, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Column {
                            Text(stringResource(R.string.label_capacity), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Text("10", color = DarkBlue, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(stringResource(R.string.label_registration), color = DarkBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text("8/10", color = DarkBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0.8f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = TealGreen,
                        trackColor = InputBg,
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.DateRange, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.title_schedule), color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailRow(stringResource(R.string.label_date), "14 May 2026")
                    DetailRow(stringResource(R.string.label_start_time), "19:30")
                    DetailRow(stringResource(R.string.label_end_time), "21:30")
                    DetailRow(stringResource(R.string.label_duration), stringResource(R.string.val_2_hours), valueColor = PrimaryBlue)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.title_match_info), color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailRow(stringResource(R.string.label_skill_level), stringResource(R.string.val_intermediary), isBadge = true)
                    DetailRow(stringResource(R.string.label_format), "4 vs 4")
                    DetailRow(stringResource(R.string.label_equipment), stringResource(R.string.val_provided))
                    DetailRow(stringResource(R.string.label_cost), "€ 5.00", valueColor = TealGreen)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Place, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.title_location), color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Riverside Beach Courts", color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Text("Av. 25 de abril, Viana do Castelo", color = TextGray, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF94A3B8)),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            color = ErrorRed,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Place, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Riverside Courts", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, InputBg),
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Icon(Icons.Outlined.Place, contentDescription = null, tint = DarkBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.btn_open_in_maps), color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Person, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.title_host), color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(InputBg), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = TextGray)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("João Silva", color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(stringResource(R.string.mock_host_stats), color = TextGray, fontSize = 12.sp)
                        }
                    }
                }
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
                        Text(stringResource(R.string.title_joined_players), color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Surface(color = PrimaryBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                            Text("8 / 10", color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    players.forEach { player ->
                        JoinedPlayerRow(player)
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedButton(
                        onClick = { },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, InputBg),
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Text(stringResource(R.string.btn_load_more), color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Column {
                Text(stringResource(R.string.title_about_match), color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    stringResource(R.string.desc_about_match),
                    color = TextGray, fontSize = 14.sp, lineHeight = 20.sp
                )
            }

            Surface(
                color = PrimaryBlue.copy(alpha = 0.05f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Outlined.Info, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(stringResource(R.string.desc_disclaimer), color = PrimaryBlue, fontSize = 12.sp, lineHeight = 16.sp)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = TealGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.btn_join_match), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                OutlinedButton(
                    onClick = { },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, InputBg),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = CardBg),
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = DarkBlue, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.btn_share_match), color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MatchDetailsTag(text: String, textColor: Color, bgColor: Color) {
    Surface(color = bgColor, shape = RoundedCornerShape(12.dp)) {
        Text(text, color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
    }
}

@Composable
private fun DetailRow(label: String, value: String, valueColor: Color = DarkBlue, isBadge: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextGray, fontSize = 14.sp)
        if (isBadge) {
            Surface(color = PrimaryBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                Text(value, color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        } else {
            Text(value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun JoinedPlayerRow(player: JoinedPlayer) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(InputBg), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Person, contentDescription = null, tint = TextGray)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(player.name, color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(player.joinedTime, color = TextGray, fontSize = 12.sp)
            }
        }

        if (player.isYou) {
            Surface(color = TealGreen.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                Text(stringResource(R.string.badge_you), color = TealGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CasualMatchDetailsScreenPreview() {
    MaterialTheme {
        CasualMatchDetailsScreen()
    }
}