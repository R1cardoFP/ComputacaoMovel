package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trabalhocm.R
import com.example.trabalhocm.data.model.AdminRegistrationTeam
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageRegistrationScreen(
    idTorneio: Long,
    viewModel: ManageRegistrationViewModel = viewModel(),
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

    LaunchedEffect(idTorneio) {
        viewModel.carregar(idTorneio)
    }

    val dados = viewModel.dados

    val pending = dados?.pendingTeams.orEmpty()
    val approved = dados?.approvedTeams.orEmpty()
    val all = pending + approved + dados?.rejectedTeams.orEmpty()

    val visibleTeams = when (selectedTab) {
        "Pending" -> pending
        "Approved" -> approved
        else -> all
    }.filter {
        it.teamName.contains(searchQuery, ignoreCase = true) ||
                it.captainName.contains(searchQuery, ignoreCase = true)
    }

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
                Text(stringResource(R.string.title_manage_registration), color = DarkBlue, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(R.string.desc_manage_registration), color = TextGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (viewModel.errorMessage.isNotBlank()) {
                item {
                    Text(viewModel.errorMessage, color = ErrorRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            item {
                val registered = dados?.registeredTeams ?: 0
                val maxTeams = (dados?.maxTeams ?: 1).coerceAtLeast(1)

                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(DarkBlue, Color(0xFF0F2B5B))), RoundedCornerShape(12.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(dados?.tournamentName ?: "", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Surface(color = TealGreen.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp)) {
                                Text((dados?.status ?: "aberto").uppercase(), color = TealGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(stringResource(R.string.label_teams_registered), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text("$registered", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                            Text(" / $maxTeams", color = TextGray, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(bottom = 6.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(progress = { registered.toFloat() / maxTeams.toFloat() }, modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)), color = TealGreen, trackColor = Color.White.copy(alpha = 0.1f))
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
                    Text(stringResource(R.string.btn_invite_team_caps), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            item {
                TextField(
                    value = searchQuery, onValueChange = { searchQuery = it },
                    placeholder = { Text(stringResource(R.string.placeholder_search_teams_captains), color = TextGray, fontSize = 13.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp)) },
                    modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(focusedContainerColor = InputBg, unfocusedContainerColor = InputBg, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                )
            }

            item {
                val tabAll = stringResource(R.string.tab_all)
                val tabPending = stringResource(R.string.tab_pending)
                val tabApproved = stringResource(R.string.tab_approved)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                        "All" to (tabAll to all.size),
                        "Pending" to (tabPending to pending.size),
                        "Approved" to (tabApproved to approved.size)
                    ).forEach { (key, tabData) ->
                        val (tabName, count) = tabData
                        val isSelected = selectedTab == key
                        Surface(
                            color = if (isSelected) PrimaryBlue else CardBg,
                            shape = RoundedCornerShape(8.dp),
                            border = if (!isSelected) BorderStroke(1.dp, InputBg) else null,
                            modifier = Modifier.weight(1f).height(40.dp).clickable { selectedTab = key }
                        ) {
                            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                Text(tabName, color = if (isSelected) Color.White else TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(color = if (isSelected) Color.White.copy(alpha = 0.2f) else InputBg, shape = RoundedCornerShape(4.dp)) {
                                    Text("$count", color = if (isSelected) Color.White else TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                        }
                    }
                }
            }

            if (visibleTeams.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.msg_no_applications), color = TextGray, fontSize = 13.sp)
                    }
                }
            } else {
                items(visibleTeams, key = { it.registrationId }) { team ->
                    if (pending.any { it.registrationId == team.registrationId }) {
                        PendingTeamCard(
                            team = team,
                            isProcessing = viewModel.isProcessing,
                            onApprove = { viewModel.aprovar(team.registrationId) },
                            onReject = { viewModel.rejeitar(team.registrationId) }
                        )
                    } else {
                        ApprovedTeamCard(
                            team = team,
                            isProcessing = viewModel.isProcessing,
                            onRemove = { viewModel.remover(team.registrationId) }
                        )
                    }
                }
            }

            item {
                Surface(color = Color(0xFFEEF2FF), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(Icons.Outlined.Info, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(stringResource(R.string.msg_waitlist_info), color = DarkBlue, fontSize = 12.sp, lineHeight = 16.sp)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun PendingTeamCard(
    team: AdminRegistrationTeam,
    isProcessing: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val isPaid = team.paymentStatus.equals("pago", ignoreCase = true)

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(8.dp), elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
            .drawBehind { drawLine(color = ErrorRed, start = Offset(0f, 0f), end = Offset(0f, size.height), strokeWidth = 12f) }
    ) {
        Column(modifier = Modifier.padding(16.dp).padding(start = 4.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(InputBg), contentAlignment = Alignment.Center) { Text(team.teamName.take(2).uppercase(), fontWeight = FontWeight.Bold) }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(team.teamName, color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(stringResource(R.string.format_captain_tier, team.captainName, team.division), color = TextGray, fontSize = 10.sp)
                    }
                }
                Surface(color = LightYellow, shape = RoundedCornerShape(12.dp)) {
                    Text(stringResource(R.string.badge_pending), color = WarningYellow, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = InputBg)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.label_players), color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("${team.playersCount} / ${team.maxPlayers}", color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(stringResource(R.string.label_applied), color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(team.appliedAgo, color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.label_win_rate), color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text(team.winRate, color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(stringResource(R.string.label_payment), color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(if (isPaid) Icons.Default.Check else Icons.Default.Close, contentDescription = null, tint = if (isPaid) TealGreen else ErrorRed, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(team.paymentStatus.replaceFirstChar { it.uppercase() }, color = if (isPaid) TealGreen else ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onReject, enabled = !isProcessing, shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightRed),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text(stringResource(R.string.btn_reject), color = ErrorRed, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Button(
                    onClick = onApprove, enabled = !isProcessing, shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealGreen),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text(stringResource(R.string.btn_approve), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
private fun ApprovedTeamCard(
    team: AdminRegistrationTeam,
    isProcessing: Boolean,
    onRemove: () -> Unit
) {
    val isPaid = team.paymentStatus.equals("pago", ignoreCase = true)

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(8.dp), elevation = CardDefaults.cardElevation(1.dp),
        modifier = Modifier.fillMaxWidth()
            .drawBehind { drawLine(color = TealGreen, start = Offset(0f, 0f), end = Offset(0f, size.height), strokeWidth = 12f) }
    ) {
        Row(modifier = Modifier.padding(16.dp).padding(start = 4.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(InputBg), contentAlignment = Alignment.Center) { Text(team.teamName.take(2).uppercase(), fontWeight = FontWeight.Bold) }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(team.teamName, color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(color = TealGreen.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) { Text(stringResource(R.string.badge_confirmed), color = TealGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) }
                        if (isPaid) {
                            Surface(color = Color(0xFFEEF2FF), shape = RoundedCornerShape(12.dp)) { Text(stringResource(R.string.badge_paid), color = PrimaryBlue, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) }
                        }
                    }
                }
            }
            IconButton(onClick = onRemove, enabled = !isProcessing, modifier = Modifier.size(36.dp).background(LightRed, RoundedCornerShape(8.dp))) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.desc_delete), tint = ErrorRed, modifier = Modifier.size(18.dp))
            }
        }
    }
}
