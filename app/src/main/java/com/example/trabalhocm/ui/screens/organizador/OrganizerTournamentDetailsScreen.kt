package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trabalhocm.R
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerTournamentDetailsScreen(
    idTorneio: Long,
    viewModel: OrganizerTournamentDetailsViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onEditClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    LaunchedEffect(idTorneio) {
        viewModel.carregarDetalhes(idTorneio)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_details), color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.desc_back), tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Outlined.Edit, contentDescription = stringResource(R.string.desc_edit), tint = Color.White)
                    }
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = stringResource(R.string.desc_notifications), tint = Color.White)
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
        if (viewModel.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TealGreen)
            }
            return@Scaffold
        }

        val detalhes = viewModel.detalhes

        if (detalhes == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = viewModel.errorMessage.ifBlank { stringResource(R.string.msg_no_tournaments_found) },
                    color = TextGray,
                    fontSize = 14.sp
                )
            }
            return@Scaffold
        }

        val torneio = detalhes.torneio

        val sportName = when (torneio.idModalidade) {
            1L -> stringResource(R.string.sport_football)
            2L -> stringResource(R.string.sport_basketball)
            3L -> stringResource(R.string.sport_volleyball)
            else -> stringResource(R.string.sport_default)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(DarkBlue, Color(0xFF0F2B5B))))
                    .padding(24.dp)
            ) {
                Column {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TourneyBadge("• ${torneio.estado.uppercase()}", TealGreen, TealGreen.copy(alpha = 0.1f))
                        TourneyBadge(sportName.uppercase(), TealGreen, TealGreen.copy(alpha = 0.1f))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(torneio.nome, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 34.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        HeroStat(stringResource(R.string.label_season_caps), torneio.dataInicio.take(4))
                        HeroStat(stringResource(R.string.label_prize_pool_caps), "${torneio.premio} €")
                        HeroStat(stringResource(R.string.label_teams_caps), "${detalhes.numEquipas}")
                    }
                }
            }

            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                if (!torneio.descricao.isNullOrBlank() || !torneio.regras.isNullOrBlank()) {
                    Card(colors = CardDefaults.cardColors(containerColor = CardBg), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(stringResource(R.string.title_about), color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)

                            if (!torneio.descricao.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(torneio.descricao, color = TextGray, fontSize = 14.sp, lineHeight = 20.sp)
                            }

                            if (!torneio.regras.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(stringResource(R.string.label_rules_caps), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(torneio.regras, color = TextGray, fontSize = 14.sp, lineHeight = 20.sp)
                            }
                        }
                    }
                }

                Card(colors = CardDefaults.cardColors(containerColor = CardBg), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.title_schedule), color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        DetailRow(stringResource(R.string.label_start_date), torneio.dataInicio)
                        DetailRow(stringResource(R.string.label_end_date), torneio.dataFim ?: stringResource(R.string.val_tbd))
                        DetailRow(stringResource(R.string.label_format), torneio.formato.uppercase())
                        DetailRow(stringResource(R.string.label_entry_fee), "${torneio.taxaInscricao} €")
                    }
                }

                if (!torneio.local.isNullOrBlank()) {
                    Card(colors = CardDefaults.cardColors(containerColor = CardBg), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Outlined.Place, contentDescription = null, tint = PrimaryBlue)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(torneio.local, color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Card(colors = CardDefaults.cardColors(containerColor = CardBg), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(stringResource(R.string.title_standings), color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))

                        if (detalhes.classificacao.isEmpty()) {
                            Text(stringResource(R.string.msg_no_standings), color = TextGray, fontSize = 13.sp)
                        } else {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Row(modifier = Modifier.weight(1f)) {
                                    Text(stringResource(R.string.col_pos), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(40.dp))
                                    Text(stringResource(R.string.col_team), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Text(stringResource(R.string.col_p), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    Text(stringResource(R.string.col_pts), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = InputBg)
                            Spacer(modifier = Modifier.height(12.dp))

                            detalhes.classificacao.forEach { linha ->
                                StandingRow(
                                    pos = linha.posicao.toString().padStart(2, '0'),
                                    team = linha.nomeEquipa,
                                    p = linha.numJogos.toString(),
                                    pts = linha.pontos.toString()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun HeroStat(label: String, value: String) {
    Column {
        Text(label, color = TealGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextGray, fontSize = 14.sp)
        Text(value, color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun StandingRow(pos: String, team: String, p: String, pts: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Text(pos, color = TealGreen, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.width(40.dp))
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(InputBg),
                contentAlignment = Alignment.Center
            ) {
                Text(team.take(1).uppercase(), color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(team, color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(p, color = TextGray, fontSize = 14.sp)
            Text(pts, color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
