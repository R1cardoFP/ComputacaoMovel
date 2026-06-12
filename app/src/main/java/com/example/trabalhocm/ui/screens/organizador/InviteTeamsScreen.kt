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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trabalhocm.R
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar

private val DarkBlue = Color(0xFF0B1F3A)
private val PrimaryBlue = Color(0xFF2563EB)
private val TealGreen = Color(0xFF059669)
private val ErrorRed = Color(0xFFDC2626)
private val TextGray = Color(0xFF64748B)
private val BgLight = Color(0xFFF8FAFC)
private val CardBg = Color(0xFFFFFFFF)
private val InputBg = Color(0xFFF1F5F9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InviteTeamsScreen(
    idTorneio: Long,
    viewModel: InviteTeamsViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(idTorneio) {
        viewModel.carregar(idTorneio)
    }

    val dados = viewModel.dados

    val teams = dados?.teams.orEmpty().filter {
        it.teamName.contains(searchQuery, ignoreCase = true) ||
                it.captainName.contains(searchQuery, ignoreCase = true)
    }
    val sentInvitations = dados?.sentInvitations.orEmpty()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_registration), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.desc_back), tint = Color.White) }
                },
                actions = {
                    IconButton(onClick = { }) { Icon(Icons.Outlined.Notifications, contentDescription = stringResource(R.string.desc_notifications), tint = Color.White) }
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
        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TealGreen)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(stringResource(R.string.tag_organizer_tool), color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(R.string.title_invite_teams), color = DarkBlue, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(R.string.desc_invite_teams_subtitle), color = TextGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (viewModel.errorMessage.isNotBlank()) {
                item {
                    Text(viewModel.errorMessage, color = ErrorRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            item {
                Card(colors = CardDefaults.cardColors(containerColor = DarkBlue), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column {
                            Text(stringResource(R.string.label_slots_remaining), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text("${dados?.slotsRemaining ?: 0}", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                                Text(" / ${dados?.maxTeams ?: 0}", color = TextGray, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 6.dp))
                            }
                        }
                        Surface(color = Color(0xFFD1FAE5), shape = RoundedCornerShape(12.dp)) {
                            Text(stringResource(R.string.badge_invite_only), color = TealGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
                        }
                    }
                }
            }

            item {
                Text(stringResource(R.string.label_search_teams_caps), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = searchQuery, onValueChange = { searchQuery = it },
                    placeholder = { Text(stringResource(R.string.placeholder_search_team_captain), color = TextGray, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp)) },
                    modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(8.dp))
                        .background(CardBg),
                    colors = TextFieldDefaults.colors(focusedContainerColor = CardBg, unfocusedContainerColor = CardBg, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            item { Text(stringResource(R.string.label_suggested_teams), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp) }

            if (teams.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.msg_no_teams_invite), color = TextGray, fontSize = 13.sp)
                    }
                }
            } else {
                items(teams, key = { it.teamId }) { team ->
                    Card(colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(1.dp), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(InputBg), contentAlignment = Alignment.Center) { Text(team.teamName.take(2).uppercase(), fontWeight = FontWeight.Bold) }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(team.teamName, color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(stringResource(R.string.format_captain_tier, team.captainName, team.division), color = TextGray, fontSize = 10.sp)
                                }
                            }
                            if (team.isInvited) {
                                Surface(color = InputBg, shape = RoundedCornerShape(6.dp)) {
                                    Text(stringResource(R.string.btn_invited), color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                                }
                            } else {
                                Button(
                                    onClick = { viewModel.convidar(team.teamId) },
                                    enabled = !viewModel.isProcessing,
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = TealGreen),
                                    contentPadding = PaddingValues(horizontal = 16.dp)
                                ) {
                                    Text(stringResource(R.string.btn_invite), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.label_sent_invitations_1), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Surface(color = Color(0xFFEEF2FF), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (sentInvitations.isEmpty()) {
                                stringResource(R.string.msg_invited_awaiting)
                            } else {
                                sentInvitations.joinToString(", ") { it.teamName }
                            },
                            color = PrimaryBlue, fontSize = 12.sp, lineHeight = 16.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
