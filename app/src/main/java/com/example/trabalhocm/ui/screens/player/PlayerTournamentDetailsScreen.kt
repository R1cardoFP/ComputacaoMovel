package com.example.trabalhocm.ui.screens.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.repository.AuthRepository
import com.example.trabalhocm.data.repository.Torneio
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite

@Composable
fun PlayerTournamentDetailsScreen(
    idTorneio: Long = 0L,
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val authRepository = remember { AuthRepository() }
    var torneioAtual by remember { mutableStateOf<Torneio?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Vai à BD buscar os detalhes do torneio pelo ID
    LaunchedEffect(idTorneio) {
        if (idTorneio != 0L) {
            authRepository.obterTorneioDetalhes(idTorneio).onSuccess { torneio ->
                torneioAtual = torneio
                isLoading = false
            }.onFailure {
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        if (isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandGreen)
            }
        } else if (torneioAtual != null) {
            val t = torneioAtual!!
            val estadoAberto = t.estado?.lowercase() == "aberto"

            val modalidadeNome = when (t.idModalidade) {
                1 -> "FOOTBALL"
                2 -> "BASKETBALL"
                3 -> "VOLLEYBALL"
                else -> "SPORT"
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                TournamentDetailsHeader(
                    onBackClick = onBackClick,
                    estado = if (estadoAberto) "OPEN" else (t.estado?.uppercase() ?: "LIVE"),
                    estadoCor = if (estadoAberto) BrandGreen else Color(0xFFE53935),
                    modalidade = modalidadeNome,
                    titulo = t.nome,
                    premio = t.premio?.toString() ?: "N/A"
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 22.dp, vertical = 18.dp)
                ) {
                    TournamentDetailsAboutCard(descricao = t.descricao ?: "Sem descrição fornecida.")

                    Spacer(modifier = Modifier.height(14.dp))

                    TournamentDetailsScheduleCard(
                        dataInicio = t.dataInicio ?: "TBD",
                        dataFim = t.dataFim ?: "TBD",
                        formato = t.formato ?: "TBD"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    TournamentDetailsLocationCard(local = t.local ?: "TBD")

                    Spacer(modifier = Modifier.height(14.dp))

                    TournamentDetailsStandingsCard()

                    Spacer(modifier = Modifier.height(22.dp))
                }
            }
        } else {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Erro ao carregar detalhes do torneio.", color = Color.Gray)
            }
        }

        MatchLeagueBottomBar(
            selectedTab = "TOURNAMENTS",
            onHomeClick = onHomeClick,
            onTournamentsClick = onTournamentsClick,
            onMatchesClick = onMatchesClick,
            onTeamsClick = onTeamsClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun TournamentDetailsHeader(
    onBackClick: () -> Unit,
    estado: String,
    estadoCor: Color,
    modalidade: String,
    titulo: String,
    premio: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(270.dp)
            .background(
                Brush.verticalGradient(
                    listOf(BrandBlue, Color(0xFF071A30))
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    color = BrandWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onBackClick() }
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Details",
                    color = BrandWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 18.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DetailHeaderBadge(text = estado, color = estadoCor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "● $modalidade",
                        color = BrandGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = titulo,
                    color = BrandWhite,
                    fontSize = 26.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(26.dp)) {
                    DetailHeaderInfo(label = "PRIZE POOL", value = "$premio €")
                }
            }
        }
    }
}

@Composable
fun DetailHeaderBadge(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(BrandWhite)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "● $text",
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun DetailHeaderInfo(label: String, value: String) {
    Column {
        Text(
            text = label,
            color = Color(0xFF9EA8BA),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.4.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            color = BrandWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TournamentDetailsAboutCard(descricao: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite)
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)) {
            Text(text = "About", color = BrandBlue, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = descricao,
                color = Color(0xFF6D7486),
                fontSize = 13.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun TournamentDetailsScheduleCard(dataInicio: String, dataFim: String, formato: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite)
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)) {
            Text(text = "Schedule", color = BrandBlue, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(14.dp))
            ScheduleRow("Start Date", dataInicio)
            ScheduleRow("End Date", dataFim)
            ScheduleRow("Format", formato.uppercase())
        }
    }
}

@Composable
fun ScheduleRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color(0xFF6D7486), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Text(text = value, color = BrandBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TournamentDetailsLocationCard(local: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite)
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)) {
            Text(text = "Location", color = BrandBlue, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("⊙", color = Color(0xFF0757C8), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = local, color = BrandBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// O quadro de classificação mantive-o estático pois precisaria de uma tabela à parte de equipas
@Composable
fun TournamentDetailsStandingsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite)
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)) {
            Text("Standings", color = BrandBlue, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("POS", color = Color(0xFF7D8497), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp))
                Text("TEAM", color = Color(0xFF7D8497), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text("P", color = Color(0xFF7D8497), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(32.dp))
                Text("PTS", color = Color(0xFF7D8497), fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(38.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            StandingTeamRow("01", "Porto", "12", "31", Color(0xFF0757C8), "porto")
            StandingTeamRow("02", "Sporting", "12", "29", BrandGreen, "sporting")
        }
    }
}

@Composable
fun StandingTeamRow(position: String, team: String, played: String, points: String, accent: Color, logoType: String) {
    Row(
        modifier = Modifier.fillMaxWidth().height(38.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(position, color = if (position == "01") BrandGreen else BrandBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp))
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            StandingLogo(logoType, accent)
            Spacer(modifier = Modifier.width(8.dp))
            Text(team, color = BrandBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Text(played, color = Color(0xFF6D7486), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(32.dp))
        Text(points, color = Color(0xFF0757C8), fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(38.dp))
    }
}

@Composable
fun StandingLogo(logoType: String, accent: Color) {
    if (logoType == "sporting") {
        Image(painter = painterResource(R.drawable.team_sporting), contentDescription = null, modifier = Modifier.size(24.dp), contentScale = ContentScale.Fit)
    } else {
        Box(
            modifier = Modifier.size(24.dp).clip(CircleShape).background(accent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(if (logoType == "porto") "P" else "B", color = accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, name = "Player Tournament Details Screen")
@Composable
fun PlayerTournamentDetailsScreenPreview() {
    PlayerTournamentDetailsScreen()
}