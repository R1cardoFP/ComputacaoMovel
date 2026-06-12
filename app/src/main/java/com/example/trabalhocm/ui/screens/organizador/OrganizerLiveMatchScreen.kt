package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
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
import com.example.trabalhocm.data.repository.MatchControlInfo
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerLiveMatchScreen(
    idJogo: Long = 0L,
    viewModel: LiveMatchControlViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    LaunchedEffect(idJogo) {
        viewModel.carregar(idJogo)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_matches), color = Color.White, fontWeight = FontWeight.Bold) },
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
        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TealGreen)
            }
            return@Scaffold
        }

        val jogo = viewModel.jogo

        if (jogo == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp), contentAlignment = Alignment.Center) {
                Text(
                    viewModel.errorMessage.ifBlank { stringResource(R.string.msg_no_live_match) },
                    color = TextGray, fontSize = 14.sp
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScoreboardCard(jogo)

            if (viewModel.errorMessage.isNotBlank()) {
                Text(viewModel.errorMessage, color = ErrorRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            val editavel = jogo.estado == "em_direto"

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                ScoreControl(
                    teamName = jogo.equipaCasa,
                    points = jogo.pontosCasa,
                    enabled = editavel && !viewModel.isProcessing,
                    onAdd = { viewModel.alterarPontos(casa = true, delta = 1) },
                    onRemove = { viewModel.alterarPontos(casa = true, delta = -1) },
                    modifier = Modifier.weight(1f)
                )
                ScoreControl(
                    teamName = jogo.equipaFora,
                    points = jogo.pontosFora,
                    enabled = editavel && !viewModel.isProcessing,
                    onAdd = { viewModel.alterarPontos(casa = false, delta = 1) },
                    onRemove = { viewModel.alterarPontos(casa = false, delta = -1) },
                    modifier = Modifier.weight(1f)
                )
            }

            when (jogo.estado) {
                "agendado" -> {
                    Button(
                        onClick = { viewModel.iniciarJogo() },
                        enabled = !viewModel.isProcessing,
                        colors = ButtonDefaults.buttonColors(containerColor = TealGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text(stringResource(R.string.btn_start_match), fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 1.sp)
                    }
                }
                "em_direto" -> {
                    Button(
                        onClick = { viewModel.terminarJogo() },
                        enabled = !viewModel.isProcessing,
                        colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(52.dp)
                    ) {
                        Text(stringResource(R.string.btn_finish_match), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 1.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ScoreboardCard(jogo: MatchControlInfo) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val (badgeText, badgeColor) = when (jogo.estado) {
                "em_direto" -> stringResource(R.string.badge_live_caps) to ErrorRed
                "terminado" -> stringResource(R.string.badge_finished) to TextGray
                else -> stringResource(R.string.badge_scheduled) to PrimaryBlue
            }

            Surface(color = badgeColor, shape = RoundedCornerShape(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(badgeText, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(jogo.torneioNome, color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamBadge(jogo.equipaCasa)
                Text("${jogo.pontosCasa} : ${jogo.pontosFora}", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 4.sp)
                TeamBadge(jogo.equipaFora)
            }

            if (jogo.local.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text("📍 ${jogo.local}", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun TeamBadge(nome: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(90.dp)) {
        Box(
            modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(nome.take(3).uppercase(), color = DarkBlue, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(nome.uppercase(), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

@Composable
private fun ScoreControl(
    teamName: String,
    points: Int,
    enabled: Boolean,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(teamName, color = DarkBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Spacer(modifier = Modifier.height(12.dp))
            Text("$points", color = DarkBlue, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(
                    onClick = onRemove,
                    enabled = enabled,
                    modifier = Modifier.size(44.dp).background(InputBg, CircleShape)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "−", tint = if (enabled) DarkBlue else TextGray)
                }
                IconButton(
                    onClick = onAdd,
                    enabled = enabled,
                    modifier = Modifier.size(44.dp).background(if (enabled) TealGreen else InputBg, CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "+", tint = Color.White)
                }
            }
        }
    }
}
