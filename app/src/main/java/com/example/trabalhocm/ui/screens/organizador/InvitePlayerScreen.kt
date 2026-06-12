package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Search
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
import com.example.trabalhocm.data.repository.UtilizadorConviteInfo
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InvitePlayerScreen(
    idEquipa: Long,
    viewModel: InvitePlayerViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSendInviteClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val roleStriker = stringResource(R.string.role_striker)
    val roleDefender = stringResource(R.string.role_defender)
    val roleMidfielder = stringResource(R.string.role_midfielder)
    val roleGoalkeeper = stringResource(R.string.role_goalkeeper)

    var searchQuery by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(roleMidfielder) }
    var personalMessage by remember { mutableStateOf("") }
    var selectedPlayerId by remember { mutableStateOf<String?>(null) }

    val msgSelectPlayer = stringResource(R.string.msg_select_player)

    LaunchedEffect(idEquipa) {
        viewModel.iniciar(idEquipa)
    }

    val showSuccessBanner = viewModel.sucesso
    val roles = listOf(roleStriker, roleDefender, roleMidfielder, roleGoalkeeper)
    val recommendedPlayers = viewModel.resultados

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_player_invite), color = Color.White, fontWeight = FontWeight.Bold) },
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
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.tag_team_recruitment), color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(R.string.title_invite_player), color = DarkBlue, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.desc_invite_player), color = TextGray, fontSize = 14.sp)
            }

            if (showSuccessBanner) {
                item {
                    Surface(
                        color = Color(0xFFECFDF5),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, TealGreen.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TealGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(stringResource(R.string.msg_invite_sent_success), color = TealGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            item {
                Column {
                    Text(stringResource(R.string.label_search_players), color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            viewModel.pesquisar(it)
                        },
                        placeholder = { Text(stringResource(R.string.placeholder_search_players), color = Color.LightGray) },
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
            }

            item {
                Column {
                    Text(stringResource(R.string.label_recommended), color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (viewModel.isLoading) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = PrimaryBlue)
                        }
                    } else if (recommendedPlayers.isEmpty()) {
                        Text(stringResource(R.string.msg_no_players_found), color = TextGray, fontSize = 13.sp)
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            recommendedPlayers.forEach { player ->
                                RecommendedPlayerCard(
                                    player = player,
                                    selected = selectedPlayerId == player.utilizador.id,
                                    onClick = {
                                        if (!player.jaPertenceEquipa) {
                                            selectedPlayerId = player.utilizador.id
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Column {
                    Text(stringResource(R.string.title_invitation_details), color = DarkBlue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(stringResource(R.string.desc_invitation_details), color = TextGray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(stringResource(R.string.label_intended_role), color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        roles.forEach { role ->
                            val isSelected = selectedRole == role
                            Surface(
                                color = if (isSelected) PrimaryBlue else CardBg,
                                shape = RoundedCornerShape(8.dp),
                                border = if (isSelected) null else BorderStroke(1.dp, InputBg),
                                modifier = Modifier.clickable { selectedRole = role }
                            ) {
                                Text(
                                    text = role,
                                    color = if (isSelected) Color.White else DarkBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(stringResource(R.string.label_personal_message), color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = personalMessage,
                        onValueChange = { personalMessage = it },
                        placeholder = { Text(stringResource(R.string.placeholder_personal_message), color = Color.LightGray) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, InputBg, RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = InputBg,
                            unfocusedContainerColor = InputBg,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        maxLines = 4
                    )
                }
            }

            if (viewModel.errorMessage.isNotBlank()) {
                item {
                    Text(viewModel.errorMessage, color = Color(0xFFDC2626), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            item {
                Button(
                    onClick = {
                        val playerId = selectedPlayerId
                        if (playerId == null) {
                            viewModel.errorMessage = msgSelectPlayer
                            return@Button
                        }
                        viewModel.convidar(playerId, selectedRole, personalMessage) {
                            selectedPlayerId = null
                            onSendInviteClick()
                        }
                    },
                    enabled = !viewModel.isProcessing,
                    colors = ButtonDefaults.buttonColors(containerColor = TealGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    if (viewModel.isProcessing) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.btn_send_invite), fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun RecommendedPlayerCard(
    player: UtilizadorConviteInfo,
    selected: Boolean,
    onClick: () -> Unit
) {
    val nome = player.utilizador.nome.ifBlank { player.utilizador.username }
    val jaNaEquipa = player.jaPertenceEquipa

    Card(
        colors = CardDefaults.cardColors(containerColor = if (selected) PrimaryBlue.copy(alpha = 0.08f) else CardBg),
        shape = RoundedCornerShape(8.dp),
        border = if (selected) BorderStroke(1.5.dp, PrimaryBlue) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !jaNaEquipa) { onClick() }
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(InputBg),
                contentAlignment = Alignment.Center
            ) {
                Text(nome.split(" ").take(2).joinToString("") { it.take(1) }.uppercase(), color = DarkBlue, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(nome, color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text("@${player.utilizador.username}", color = TextGray, fontSize = 12.sp)
            }
            if (jaNaEquipa) {
                Surface(color = InputBg, shape = RoundedCornerShape(6.dp)) {
                    Text(stringResource(R.string.badge_already_member), color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            } else if (selected) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
            }
        }
    }
}