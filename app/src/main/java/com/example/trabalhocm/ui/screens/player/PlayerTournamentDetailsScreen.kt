package com.example.trabalhocm.ui.screens.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.model.Jogo
import com.example.trabalhocm.data.repository.AuthRepository
import com.example.trabalhocm.data.repository.EstatisticaEquipaLiga
import com.example.trabalhocm.data.repository.Torneio
import com.example.trabalhocm.data.repository.TorneioRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite

private val ScreenBg = Color(0xFFF4F6FB)
private val CardBg = Color.White
private val InputBg = Color(0xFFEFF3F8)
private val TextMuted = Color(0xFF6D7486)
private val BorderLine = Color(0xFFE6EAF2)
private val SoftGreen = Color(0xFFEAF8F5)
private val SoftBlue = Color(0xFFEAF1FF)
private val SoftRed = Color(0xFFFFECEC)

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
    var jogosEliminatorias by remember { mutableStateOf<List<Jogo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(idTorneio) {
        if (idTorneio != 0L) {
            authRepository.obterTorneioDetalhes(idTorneio).onSuccess { torneio ->
                torneioAtual = torneio

                if (torneio.formato?.lowercase() == "liga") {
                    torneioRepository.obterClassificacaoTorneio(idTorneio).onSuccess { standings ->
                        classificacao = standings
                    }
                } else if (torneio.formato?.lowercase() == "eliminatorias" || torneio.formato?.lowercase() == "knockout") {
                    torneioRepository.obterJogosEliminatorias(idTorneio).onSuccess { jogos ->
                        jogosEliminatorias = jogos
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
            .background(ScreenBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        TournamentDetailsTopBar(onBackClick = onBackClick)

        if (isLoading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
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
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                TournamentDetailsHeader(
                    estado = if (estadoAberto) "OPEN" else estadoTexto.uppercase(),
                    estadoCor = if (estadoAberto) BrandGreen else Color(0xFFE53935),
                    modalidade = modalidadeNome,
                    titulo = t.nome,
                    premio = t.premio?.toString() ?: "N/A"
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                } else if (t.formato?.lowercase() == "eliminatorias" || t.formato?.lowercase() == "knockout") {
                    TournamentBracketCard(jogos = jogosEliminatorias)
                    Spacer(modifier = Modifier.height(22.dp))
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                TournamentFeedbackCard(
                    title = "Não foi possível carregar o torneio",
                    message = "Error loading tournament details."
                )
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
fun TournamentDetailsTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(CardBg)
                .border(1.dp, BorderLine, CircleShape)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "‹",
                color = BrandBlue,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = "Tournament",
                color = BrandBlue,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Detalhes da competição",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun TournamentDetailsHeader(
    estado: String,
    estadoCor: Color,
    modalidade: String,
    titulo: String,
    premio: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(BrandBlue, Color(0xFF071A30))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    DetailHeaderBadge(text = estado, color = estadoCor)
                    Spacer(modifier = Modifier.width(8.dp))
                    DetailHeaderBadge(
                        text = modalidade,
                        color = BrandGreen,
                        softColor = Color.White.copy(alpha = 0.12f),
                        textPrefix = "● "
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = titulo,
                    color = BrandWhite,
                    fontSize = 27.sp,
                    lineHeight = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TournamentHeaderStat(
                        label = "PRIZE POOL",
                        value = "$premio €",
                        modifier = Modifier.weight(1f)
                    )
                    TournamentHeaderStat(
                        label = "TYPE",
                        value = "TOURNAMENT",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun DetailHeaderBadge(
    text: String,
    color: Color,
    softColor: Color = BrandWhite,
    textPrefix: String = "● "
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(softColor)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$textPrefix$text",
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun TournamentHeaderStat(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.11f))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column {
            Text(
                text = label,
                color = Color(0xFFB8C2D6),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = value,
                color = BrandWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
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
    TournamentSectionCard(title = "About") {
        Text(
            text = descricao,
            color = TextMuted,
            fontSize = 13.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun TournamentDetailsScheduleCard(dataInicio: String, dataFim: String, formato: String) {
    TournamentSectionCard(title = "Schedule") {
        ScheduleRow("Start Date", dataInicio)
        ScheduleRow("End Date", dataFim)
        ScheduleRow("Format", formato.uppercase())
    }
}

@Composable
fun ScheduleRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextMuted, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(InputBg)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Text(text = value, color = BrandBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TournamentDetailsLocationCard(local: String) {
    TournamentSectionCard(title = "Location") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SoftBlue)
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(BrandWhite),
                contentAlignment = Alignment.Center
            ) {
                Text("⊙", color = Color(0xFF0757C8), fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = "Local do torneio", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = local, color = BrandBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TournamentDetailsStandingsCard(classificacao: List<EstatisticaEquipaLiga>) {
    TournamentSectionCard(title = "Standings") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(InputBg)
                .padding(horizontal = 10.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("POS", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(36.dp))
            Text("TEAM", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("P", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(32.dp))
            Text("PTS", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(38.dp))
        }
        Spacer(modifier = Modifier.height(10.dp))

        if (classificacao.isEmpty()) {
            TournamentEmptyState(message = "No teams registered or no data available.")
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

@Composable
fun StandingTeamRow(position: String, team: String, played: String, points: String, accent: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (position == "01") SoftGreen else Color.Transparent)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            position,
            color = if (position == "01") BrandGreen else BrandBlue,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(36.dp)
        )
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            StandingLogo(team, accent)
            Spacer(modifier = Modifier.width(8.dp))
            Text(team, color = BrandBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Text(played, color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(32.dp))
        Text(points, color = Color(0xFF0757C8), fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(38.dp))
    }
}

@Composable
fun StandingLogo(nomeEquipa: String, accent: Color) {
    val nomeNormalizado = nomeEquipa.lowercase()

    if (nomeNormalizado.contains("sporting")) {
        Image(
            painter = painterResource(R.drawable.team_sporting),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            contentScale = ContentScale.Fit
        )
    } else {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.15f)),
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

@Composable
fun TournamentBracketCard(jogos: List<Jogo>) {
    TournamentSectionCard(title = "Knockout Stage") {
        if (jogos.isEmpty()) {
            TournamentEmptyState(message = "No knockout fixtures scheduled yet.")
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BracketMatchNode("TBD", "TBD", "Q1", true)
                    BracketMatchNode("TBD", "TBD", "Q2", true)
                }

                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .height(80.dp)
                        .drawBehind {
                            drawLine(
                                color = BorderLine,
                                start = Offset(0f, size.height / 4),
                                end = Offset(size.width / 2, size.height / 4),
                                strokeWidth = 2.dp.toPx()
                            )
                            drawLine(
                                color = BorderLine,
                                start = Offset(0f, size.height * 0.75f),
                                end = Offset(size.width / 2, size.height * 0.75f),
                                strokeWidth = 2.dp.toPx()
                            )
                            drawLine(
                                color = BorderLine,
                                start = Offset(size.width / 2, size.height / 4),
                                end = Offset(size.width / 2, size.height * 0.75f),
                                strokeWidth = 2.dp.toPx()
                            )
                            drawLine(
                                color = BorderLine,
                                start = Offset(size.width / 2, size.height / 2),
                                end = Offset(size.width, size.height / 2),
                                strokeWidth = 2.dp.toPx()
                            )
                        }
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    BracketMatchNode("TBD", "TBD", "SF1", false)
                }
            }
        }
    }
}

@Composable
fun BracketMatchNode(team1: String, team2: String, label: String, isLeft: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = InputBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(team1, color = BrandBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("-", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(BorderLine)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(team2, color = BrandBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("-", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun TournamentSectionCard(title: String, content: @Composable Column.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(BrandGreen)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = BrandBlue,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
fun TournamentEmptyState(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(InputBg)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun TournamentFeedbackCard(title: String, message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = SoftRed),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(title, color = Color(0xFFB3261E), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(message, color = Color(0xFFB3261E), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true, name = "Player Tournament Details Screen")
@Composable
fun PlayerTournamentDetailsScreenPreview() {
    PlayerTournamentDetailsScreen()
}
