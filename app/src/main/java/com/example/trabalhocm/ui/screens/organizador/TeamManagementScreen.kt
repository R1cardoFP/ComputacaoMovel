package com.example.trabalhocm.ui.screens

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trabalhocm.R
import com.example.trabalhocm.data.repository.MembroEquipaGestaoInfo
import com.example.trabalhocm.ui.screens.organizador.TeamManagementViewModel

private val DarkBlue = Color(0xFF111827)
private val PrimaryBlue = Color(0xFF0346B8)
private val TextGray = Color(0xFF64748B)
private val BgLight = Color(0xFFF8FAFC)
private val CardBg = Color(0xFFFFFFFF)
private val InputBg = Color(0xFFF1F5F9)
private val LightRedBadge = Color(0xFFFEE2E2)
private val ErrorRed = Color(0xFFDC2626)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamManagementScreen(
    idEquipa: Long,
    viewModel: TeamManagementViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onInviteClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(idEquipa) {
        viewModel.carregar(idEquipa)
    }

    val gestao = viewModel.gestao
    val info = gestao?.equipaInfo

    val membros = gestao?.membros.orEmpty().filter {
        val nome = it.utilizador.nome.ifBlank { it.utilizador.username }
        nome.contains(searchQuery, ignoreCase = true)
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
        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
            return@Scaffold
        }

        if (gestao == null || info == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp), contentAlignment = Alignment.Center) {
                Text(
                    viewModel.errorMessage.ifBlank { stringResource(R.string.msg_no_teams_found) },
                    color = TextGray, fontSize = 14.sp
                )
            }
            return@Scaffold
        }

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

            if (viewModel.errorMessage.isNotBlank()) {
                item {
                    Text(viewModel.errorMessage, color = ErrorRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
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
                            Text(info.iniciais, color = Color.White, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(info.equipa.nome, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(info.modalidadeNome, color = Color.LightGray, fontSize = 12.sp)
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
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.format_roster_count, membros.size),
                    color = TextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            if (membros.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(stringResource(R.string.msg_no_members), color = TextGray, fontSize = 13.sp)
                    }
                }
            } else {
                items(membros) { membro ->
                    PlayerCard(
                        membro = membro,
                        enabled = !viewModel.isProcessing,
                        onMakeCaptain = { viewModel.tornarCapitao(membro.utilizador.id) },
                        onRemove = { viewModel.removerMembro(membro.utilizador.id) }
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
fun PlayerCard(
    membro: MembroEquipaGestaoInfo,
    enabled: Boolean,
    onMakeCaptain: () -> Unit,
    onRemove: () -> Unit
) {
    val nome = membro.utilizador.nome.ifBlank { membro.utilizador.username }
    var menuExpanded by remember { mutableStateOf(false) }
    val pendente = membro.estadoConvite.lowercase() != "aceite"

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
                    if (membro.isCaptain) {
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
                    text = nome.split(" ").take(2).joinToString("") { it.take(1) }.uppercase(),
                    color = DarkBlue,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(nome, color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)

                    if (membro.isCaptain) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = LightRedBadge, shape = RoundedCornerShape(12.dp)) {
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
                Text(
                    text = if (pendente) stringResource(R.string.badge_pending) else membro.posicao,
                    color = TextGray,
                    fontSize = 12.sp
                )
            }

            if (!membro.isCaptain) {
                Box {
                    IconButton(onClick = { menuExpanded = true }, enabled = enabled) {
                        Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.desc_options), tint = TextGray)
                    }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.action_make_captain)) },
                            onClick = {
                                menuExpanded = false
                                onMakeCaptain()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.action_remove_member), color = ErrorRed) },
                            onClick = {
                                menuExpanded = false
                                onRemove()
                            }
                        )
                    }
                }
            }
        }
    }
}
