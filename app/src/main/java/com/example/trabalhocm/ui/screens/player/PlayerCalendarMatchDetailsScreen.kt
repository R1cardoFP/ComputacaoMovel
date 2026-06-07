package com.example.trabalhocm.ui.screens.player

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.repository.PlayerCalendarMatchInfo
import com.example.trabalhocm.data.repository.PlayerMatchCalendarRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PlayerCalendarMatchDetailsScreen(
    idJogo: Long,
    onBackClick: () -> Unit = {},
    onWatchLiveClick: (Long) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { PlayerMatchCalendarRepository() }

    var jogo by remember { mutableStateOf<PlayerCalendarMatchInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var mensagemErro by remember { mutableStateOf("") }
    var reminderSet by remember { mutableStateOf(false) }

    LaunchedEffect(idJogo) {
        isLoading = true
        mensagemErro = ""

        repository.listarJogosCalendario()
            .onSuccess { lista ->
                jogo = lista.firstOrNull { it.idJogo == idJogo }

                if (jogo == null) {
                    mensagemErro = "Jogo não encontrado."
                }
            }
            .onFailure {
                mensagemErro = it.message ?: "Erro ao carregar detalhes do jogo."
            }

        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        PlayerCalendarDetailsTopBar(
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BrandGreen)
                    }
                }

                mensagemErro.isNotBlank() -> {
                    PlayerCalendarDetailsMessageCard(
                        text = mensagemErro,
                        color = Color(0xFFD01818)
                    )
                }

                jogo != null -> {
                    val info = jogo!!

                    Text(
                        text = info.torneioNome.uppercase(),
                        color = Color(0xFF0757C8),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.3.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "${info.equipaCasa} vs ${info.equipaFora}",
                        color = BrandBlue,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Detalhes do jogo marcado no calendário.",
                        color = Color(0xFF51607A),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    PlayerCalendarDetailsScoreCard(info)

                    Spacer(modifier = Modifier.height(14.dp))

                    PlayerCalendarDetailsScheduleCard(info)

                    Spacer(modifier = Modifier.height(14.dp))

                    PlayerCalendarDetailsMatchInfoCard(info)

                    Spacer(modifier = Modifier.height(14.dp))

                    PlayerCalendarDetailsLocationCard(info)

                    Spacer(modifier = Modifier.height(14.dp))

                    PlayerCalendarDetailsTeamsCard(info)

                    Spacer(modifier = Modifier.height(14.dp))

                    PlayerCalendarDetailsAboutCard(info)

                    Spacer(modifier = Modifier.height(14.dp))

                    if (info.isLive) {
                        Button(
                            onClick = { onWatchLiveClick(info.idJogo) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandGreen,
                                contentColor = BrandWhite
                            )
                        ) {
                            Text(
                                text = "WATCH LIVE",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Button(
                            onClick = { reminderSet = !reminderSet },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (reminderSet) BrandGreen else Color(0xFF0757C8),
                                contentColor = BrandWhite
                            )
                        ) {
                            Text(
                                text = if (reminderSet) "REMINDER SET" else "SET REMINDER",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "↗  SHARE MATCH",
                            color = BrandBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        MatchLeagueBottomBar(
            selectedTab = "MATCHES",
            onHomeClick = onHomeClick,
            onTournamentsClick = onTournamentsClick,
            onMatchesClick = onMatchesClick,
            onTeamsClick = onTeamsClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun PlayerCalendarDetailsTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(BrandBlue)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "‹",
            color = BrandWhite,
            fontSize = 34.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { onBackClick() }
        )

        Text(
            text = "Details",
            color = BrandWhite,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(start = 6.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "♧",
            color = BrandWhite,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PlayerCalendarDetailsScoreCard(
    jogo: PlayerCalendarMatchInfo
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PlayerCalendarDetailsBadge(
                    text = when {
                        jogo.isLive -> "● LIVE NOW"
                        jogo.isFinished -> "● COMPLETED"
                        else -> "● UPCOMING"
                    },
                    backgroundColor = if (jogo.isLive) BrandGreen.copy(alpha = 0.12f) else Color(0xFFEAF0FB),
                    textColor = if (jogo.isLive) BrandGreen else Color(0xFF0757C8)
                )

                PlayerCalendarDetailsBadge(
                    text = jogo.modalidadeNome.uppercase(),
                    backgroundColor = Color(0xFFEFF1F6),
                    textColor = Color(0xFF6D7486)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlayerCalendarDetailsTeamBlock(
                    teamName = jogo.equipaCasa,
                    modifier = Modifier.weight(1f)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (jogo.isLive || jogo.isFinished) {
                        Text(
                            text = "${jogo.pontosCasa} - ${jogo.pontosFora}",
                            color = BrandBlue,
                            fontSize = 31.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (jogo.isLive) {
                            Text(
                                text = "75'",
                                color = Color(0xFFE53935),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = "Full Time",
                                color = Color(0xFF6D7486),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Text(
                            text = "VS",
                            color = BrandBlue,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "in ${jogo.hora.take(5)}",
                            color = Color(0xFF6D7486),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                PlayerCalendarDetailsTeamBlock(
                    teamName = jogo.equipaFora,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun PlayerCalendarDetailsScheduleCard(
    jogo: PlayerCalendarMatchInfo
) {
    PlayerCalendarDetailsSectionCard(
        title = "▣  Schedule"
    ) {
        PlayerCalendarDetailsInfoRow("Date", calendarDetailsFormatDate(jogo.data))
        PlayerCalendarDetailsInfoRow("Start Time", jogo.hora.take(5))
        PlayerCalendarDetailsInfoRow("End Time", calendarDetailsEndTime(jogo.hora))
        PlayerCalendarDetailsInfoRow("Duration", "2 hours", valueColor = Color(0xFF0757C8))
    }
}

@Composable
fun PlayerCalendarDetailsMatchInfoCard(
    jogo: PlayerCalendarMatchInfo
) {
    PlayerCalendarDetailsSectionCard(
        title = "◉  Match Info"
    ) {
        PlayerCalendarDetailsInfoRow("Tournament", jogo.torneioNome)
        PlayerCalendarDetailsInfoRow("Sport", jogo.modalidadeNome)
        PlayerCalendarDetailsInfoRow(
            "Status",
            when {
                jogo.isLive -> "LIVE"
                jogo.isFinished -> "COMPLETED"
                else -> "UPCOMING"
            },
            valueColor = if (jogo.isLive) BrandGreen else Color(0xFF0757C8)
        )
        PlayerCalendarDetailsInfoRow("Match ID", "#${jogo.idJogo}")
    }
}

@Composable
fun PlayerCalendarDetailsLocationCard(
    jogo: PlayerCalendarMatchInfo
) {
    PlayerCalendarDetailsSectionCard(
        title = "⌖  Location"
    ) {
        Text(
            text = jogo.local,
            color = BrandBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(115.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(Color(0xFFAFC4D4)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFD01818))
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⌖  ${jogo.local}",
                    color = BrandWhite,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp),
            shape = RoundedCornerShape(5.dp)
        ) {
            Text(
                text = "⌖  OPEN IN MAPS",
                color = BrandBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PlayerCalendarDetailsTeamsCard(
    jogo: PlayerCalendarMatchInfo
) {
    PlayerCalendarDetailsSectionCard(
        title = "Teams"
    ) {
        PlayerCalendarDetailsInfoRow("Home Team", jogo.equipaCasa)
        PlayerCalendarDetailsInfoRow("Away Team", jogo.equipaFora)

        if (jogo.isLive || jogo.isFinished) {
            PlayerCalendarDetailsInfoRow("Home Score", jogo.pontosCasa.toString())
            PlayerCalendarDetailsInfoRow("Away Score", jogo.pontosFora.toString())
        }
    }
}

@Composable
fun PlayerCalendarDetailsAboutCard(
    jogo: PlayerCalendarMatchInfo
) {
    PlayerCalendarDetailsSectionCard(
        title = "About this match"
    ) {
        Text(
            text = "This match belongs to ${jogo.torneioNome}. It is scheduled at ${jogo.local} and will be played between ${jogo.equipaCasa} and ${jogo.equipaFora}.",
            color = Color(0xFF51607A),
            fontSize = 12.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PlayerCalendarDetailsSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                color = BrandBlue,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
    }
}

@Composable
fun PlayerCalendarDetailsInfoRow(
    label: String,
    value: String,
    valueColor: Color = BrandBlue
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFF6D7486),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = value,
            color = valueColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PlayerCalendarDetailsTeamBlock(
    teamName: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(44.dp)
                .height(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFEAF0FB)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = calendarDetailsInitials(teamName),
                color = BrandBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = teamName,
            color = BrandBlue,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PlayerCalendarDetailsBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .padding(horizontal = 9.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.7.sp
        )
    }
}

@Composable
fun PlayerCalendarDetailsMessageCard(
    text: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 13.sp,
            modifier = Modifier.padding(18.dp),
            fontWeight = FontWeight.Medium
        )
    }
}

fun calendarDetailsInitials(teamName: String): String {
    val words = teamName.split(" ").filter { it.isNotBlank() }

    return when {
        words.isEmpty() -> "?"
        words.size == 1 -> words.first().take(2).uppercase()
        else -> words.take(2).joinToString("") { it.first().uppercaseChar().toString() }
    }
}

fun calendarDetailsFormatDate(data: String): String {
    return runCatching {
        LocalDate.parse(data.take(10))
            .format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH))
    }.getOrDefault(data)
}

fun calendarDetailsEndTime(hora: String): String {
    val partes = hora.take(5).split(":")

    if (partes.size != 2) {
        return "Hora por definir"
    }

    val horas = partes[0].toIntOrNull() ?: return "Hora por definir"
    val minutos = partes[1]

    val horaFim = (horas + 2).coerceAtMost(23)

    return "${horaFim.toString().padStart(2, '0')}:$minutos"
}

@Preview(showBackground = true)
@Composable
fun PlayerCalendarMatchDetailsScreenPreview() {
    PlayerCalendarMatchDetailsScreen(idJogo = 1)
}