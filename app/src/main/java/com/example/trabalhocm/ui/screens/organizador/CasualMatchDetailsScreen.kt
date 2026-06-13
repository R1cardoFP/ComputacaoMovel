package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.trabalhocm.data.model.Utilizador
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CasualMatchDetailsScreen(
    idPeladinha: Long,
    viewModel: CasualMatchDetailsViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    LaunchedEffect(idPeladinha) {
        viewModel.carregar(idPeladinha)
    }

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
        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TealGreen)
            }
            return@Scaffold
        }

        val detalhes = viewModel.detalhes
        if (detalhes == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp), contentAlignment = Alignment.Center) {
                Text(viewModel.errorMessage.ifBlank { stringResource(R.string.msg_no_tournaments_found) }, color = TextGray, fontSize = 14.sp)
            }
            return@Scaffold
        }

        val peladinha = detalhes.peladinha
        val inscritos = detalhes.jogadoresInscritos
        val capacidade = peladinha.maxJogadores.coerceAtLeast(1)
        val vagas = (peladinha.maxJogadores - inscritos).coerceAtLeast(0)

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
                Text(
                    peladinha.descricao?.ifBlank { null } ?: detalhes.modalidadeNome,
                    color = DarkBlue, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold
                )
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MatchDetailsTag(peladinha.estado.uppercase(), TealGreen, TealGreen.copy(alpha = 0.1f))
                        MatchDetailsTag(detalhes.modalidadeNome.uppercase(), TextGray, InputBg)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(stringResource(R.string.label_spots_left), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Text("$vagas", color = PrimaryBlue, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Column {
                            Text(stringResource(R.string.label_joined), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Text("$inscritos", color = DarkBlue, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Column {
                            Text(stringResource(R.string.label_capacity), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            Text("${peladinha.maxJogadores}", color = DarkBlue, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(stringResource(R.string.label_registration), color = DarkBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        Text("$inscritos/${peladinha.maxJogadores}", color = DarkBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { inscritos.toFloat() / capacidade.toFloat() },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                        color = TealGreen,
                        trackColor = InputBg,
                    )
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(1.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.DateRange, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.title_schedule), color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    DetailRow(stringResource(R.string.label_date), peladinha.data ?: "—")
                    DetailRow(stringResource(R.string.label_start_time), peladinha.hora?.take(5) ?: "—")
                    DetailRow(stringResource(R.string.label_cost), "€ ${peladinha.preco ?: 0.0}", valueColor = TealGreen)
                }
            }

            if (!peladinha.local.isNullOrBlank()) {
                Card(colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(1.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Place, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(peladinha.local, color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            detalhes.organizador?.let { host ->
                Card(colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(1.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.title_host), color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(InputBg), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = TextGray)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(host.nome.ifBlank { host.username }, color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(1.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.title_joined_players), color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Surface(color = PrimaryBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                            Text("$inscritos/${peladinha.maxJogadores}", color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (detalhes.participantes.isEmpty()) {
                        Text(stringResource(R.string.msg_no_members), color = TextGray, fontSize = 13.sp)
                    } else {
                        detalhes.participantes.forEach { jogador ->
                            JoinedPlayerRow(jogador, isYou = jogador.id == detalhes.utilizadorAtualId)
                            Spacer(modifier = Modifier.height(12.dp))
                        }
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
private fun DetailRow(label: String, value: String, valueColor: Color = DarkBlue) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextGray, fontSize = 14.sp)
        Text(value, color = valueColor, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun JoinedPlayerRow(jogador: Utilizador, isYou: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).clip(CircleShape).background(InputBg), contentAlignment = Alignment.Center) {
                Text(jogador.nome.take(1).uppercase().ifBlank { "?" }, color = DarkBlue, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(jogador.nome.ifBlank { jogador.username }, color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text("@${jogador.username}", color = TextGray, fontSize = 12.sp)
            }
        }

        if (isYou) {
            Surface(color = TealGreen.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                Text(stringResource(R.string.badge_you), color = TealGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
    }
}
