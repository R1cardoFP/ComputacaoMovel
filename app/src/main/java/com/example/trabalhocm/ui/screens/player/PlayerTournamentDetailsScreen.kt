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
import com.example.trabalhocm.data.repository.TorneioRepository
import com.example.trabalhocm.data.repository.EstatisticaEquipaLiga
import com.example.trabalhocm.data.repository.Torneio // IMPORTANTE: Agora usa o modelo correto!
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
    val torneioRepository = remember { TorneioRepository() }

    var torneioAtual by remember { mutableStateOf<Torneio?>(null) }
    var classificacao by remember { mutableStateOf<List<EstatisticaEquipaLiga>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(idTorneio) {
        if (idTorneio != 0L) {
            authRepository.obterTorneioDetalhes(idTorneio).onSuccess { torneio ->
                torneioAtual = torneio

                if (torneio.formato?.lowercase() == "liga") {
                    torneioRepository.obterClassificacaoTorneio(idTorneio).onSuccess { standings ->
                        classificacao = standings
                    }
                }

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
            val estadoTexto = t.estado?.toString() ?: "LIVE"
            val estadoAberto = estadoTexto.lowercase() == "aberto"

            val modalidadeNome = when (t.idModalidade?.toString()) {
                "1" -> "FOOTBALL"
                "2" -> "BASKETBALL"
                "3" -> "VOLLEYBALL"
                else -> "SPORT"
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                TournamentDetailsHeader(
                    onBackClick = onBackClick,
                    estado = if (estadoAberto) "OPEN" else estadoTexto.uppercase(),
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
                    val desc = t.descricao?.takeIf { it.isNotBlank() } ?: "No description provided."
                    TournamentDetailsAboutCard(descricao = desc)

                    Spacer(modifier = Modifier.height(14.dp))

                    TournamentDetailsScheduleCard(
                        dataInicio = t.dataInicio?.takeIf { it.isNotBlank() } ?: "TBD",
                        dataFim = t.dataFim?.takeIf { it.isNotBlank() } ?: "TBD",
                        formato = t.formato?.takeIf { it.isNotBlank() } ?: "TBD"
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    TournamentDetailsLocationCard(local = t.local?.takeIf { it.isNotBlank() } ?: "TBD")

                    Spacer(modifier = Modifier.height(14.dp))

                    if (t.formato?.lowercase() == "liga") {
                        TournamentDetailsStandingsCard(classificacao = classificacao)
                        Spacer(modifier = Modifier.height(22.dp))
                    }
                }
            }
        } else {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Error loading tournament details.", color = Color.Gray)
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

@Composable
fun TournamentDetailsStandingsCard(classificacao: List<EstatisticaEquipaLiga>) {
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

            if (classificacao.isEmpty()) {
                Text(
                    text = "No teams registered or no data available.",
                    color = Color(0xFF6D7486),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
            } else {
                classificacao.forEachIndexed { index, equipa ->
                    val positionText = (index + 1).toString().padStart(2, '0')
                    val isFirst = index == 0

                    StandingTeamRow(
                        position = positionText,
                        team = equipa.nomeEquipa,
                        played = equipa.jogosDisputados.toString(),
                        points = equipa.pontos.toString(),
                        accent = if (isFirst) BrandGreen else Color(0xFF0757C8)
                    )
                }
            }
        }
    }
}

@Composable
fun StandingTeamRow(position: String, team: String, played: String, points: String, accent: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().height(38.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(position, color = if (position == "01") BrandGreen else BrandBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp))
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            StandingLogo(team, accent)
            Spacer(modifier = Modifier.width(8.dp))
            Text(team, color = BrandBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Text(played, color = Color(0xFF6D7486), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(32.dp))
        Text(points, color = Color(0xFF0757C8), fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(38.dp))
    }
}

@Composable
fun StandingLogo(nomeEquipa: String, accent: Color) {
    val nomeNormalizado = nomeEquipa.lowercase()

    if (nomeNormalizado.contains("sporting")) {
        Image(painter = painterResource(R.drawable.team_sporting), contentDescription = null, modifier = Modifier.size(24.dp), contentScale = ContentScale.Fit)
    } else {
        Box(
            modifier = Modifier.size(24.dp).clip(CircleShape).background(accent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = nomeEquipa.take(1).uppercase(),
                color = accent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, name = "Player Tournament Details Screen")
@Composable
fun PlayerTournamentDetailsScreenPreview() {
    PlayerTournamentDetailsScreen()
}