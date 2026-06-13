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

private val PlayerCalendarBg = Color(0xFFF6F7FB)
private val PlayerCalendarCardBg = Color.White
private val PlayerCalendarInputBg = Color(0xFFF0F3F8)
private val PlayerCalendarTextGray = Color(0xFF657089)
private val PlayerCalendarMuted = Color(0xFF8A92A6)
private val PlayerCalendarDarkCard = Color(0xFF111827)
private val PlayerCalendarBlue = Color(0xFF0757C8)
private val PlayerCalendarRed = Color(0xFFE53935)

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
            .background(PlayerCalendarBg)
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
                .padding(horizontal = 24.dp, vertical = 18.dp)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BrandGreen)
                    }
                }

                mensagemErro.isNotBlank() -> {
                    PlayerCalendarDetailsMessageCard(
                        text = mensagemErro,
                        color = PlayerCalendarRed
                    )
                }

                jogo != null -> {
                    val info = jogo!!

                    PlayerCalendarDetailsHeroCard(info)

                    Spacer(modifier = Modifier.height(18.dp))

                    PlayerCalendarDetailsScheduleCard(info)

                    Spacer(modifier = Modifier.height(14.dp))

                    PlayerCalendarDetailsMatchInfoCard(info)

                    Spacer(modifier = Modifier.height(14.dp))

                    PlayerCalendarDetailsLocationCard(info)

                    Spacer(modifier = Modifier.height(14.dp))

                    PlayerCalendarDetailsTeamsCard(info)

                    Spacer(modifier = Modifier.height(14.dp))

                    PlayerCalendarDetailsAboutCard(info)

                    Spacer(modifier = Modifier.height(18.dp))

                    PlayerCalendarDetailsActionsCard(
                        jogo = info,
                        reminderSet = reminderSet,
                        onReminderClick = { reminderSet = !reminderSet },
                        onWatchLiveClick = { onWatchLiveClick(info.idJogo) }
                    )
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
            .height(66.dp)
            .background(PlayerCalendarDarkCard)
            .padding(horizontal = 22.dp),
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
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(start = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "♧",
            color = BrandWhite,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PlayerCalendarDetailsHeroCard(
    jogo: PlayerCalendarMatchInfo
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = PlayerCalendarDarkCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(54.dp)
                        .height(54.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.13f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚑",
                        color = BrandWhite,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = jogo.torneioNome.uppercase(),
                        color = BrandGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "${jogo.equipaCasa} vs ${jogo.equipaFora}",
                        color = BrandWhite,
                        fontSize = 22.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PlayerCalendarDetailsBadge(
                    text = when {
                        jogo.isLive -> "● LIVE NOW"
                        jogo.isFinished -> "● COMPLETED"
                        else -> "● UPCOMING"
                    },
                    backgroundColor = when {
                        jogo.isLive -> BrandGreen.copy(alpha = 0.16f)
                        jogo.isFinished -> Color.White.copy(alpha = 0.12f)
                        else -> PlayerCalendarBlue.copy(alpha = 0.18f)
                    },
                    textColor = when {
                        jogo.isLive -> BrandGreen
                        jogo.isFinished -> Color(0xFFDCE3F2)
                        else -> Color(0xFFBBD2FF)
                    }
                )

                PlayerCalendarDetailsBadge(
                    text = jogo.modalidadeNome.uppercase(),
                    backgroundColor = Color.White.copy(alpha = 0.11f),
                    textColor = Color(0xFFDCE3F2)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            PlayerCalendarDetailsScoreLine(jogo)

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlayerCalendarDetailsHeroMetric(
                    label = "DATA",
                    value = calendarDetailsFormatDate(jogo.data),
                    modifier = Modifier.weight(1f)
                )
                PlayerCalendarDetailsHeroMetric(
                    label = "HORA",
                    value = jogo.hora.take(5),
                    modifier = Modifier.weight(1f)
                )
                PlayerCalendarDetailsHeroMetric(
                    label = "JOGO",
                    value = "#${jogo.idJogo}",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun PlayerCalendarDetailsScoreLine(
    jogo: PlayerCalendarMatchInfo
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerCalendarDetailsTeamBlockDark(
            teamName = jogo.equipaCasa,
            modifier = Modifier.weight(1f)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (jogo.isLive || jogo.isFinished) {
                Text(
                    text = "${jogo.pontosCasa} - ${jogo.pontosFora}",
                    color = BrandWhite,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (jogo.isLive) "75'" else "Full Time",
                    color = if (jogo.isLive) PlayerCalendarRed else Color(0xFFDCE3F2),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "VS",
                    color = BrandWhite,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "in ${jogo.hora.take(5)}",
                    color = Color(0xFFDCE3F2),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        PlayerCalendarDetailsTeamBlockDark(
            teamName = jogo.equipaFora,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun PlayerCalendarDetailsHeroMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.13f))
            .padding(horizontal = 10.dp, vertical = 12.dp)
    ) {
        Column {
            Text(
                text = value,
                color = BrandWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = label,
                color = Color(0xFFDCE3F2),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.7.sp
            )
        }
    }
}

@Composable
fun PlayerCalendarDetailsScheduleCard(
    jogo: PlayerCalendarMatchInfo
) {
    PlayerCalendarDetailsSectionCard(
        title = "Schedule",
        icon = "▣"
    ) {
        PlayerCalendarDetailsInfoRow("Date", calendarDetailsFormatDate(jogo.data))
        PlayerCalendarDetailsInfoRow("Start Time", jogo.hora.take(5))
        PlayerCalendarDetailsInfoRow("End Time", calendarDetailsEndTime(jogo.hora))
        PlayerCalendarDetailsInfoRow("Duration", "2 hours", valueColor = PlayerCalendarBlue)
    }
}

@Composable
fun PlayerCalendarDetailsMatchInfoCard(
    jogo: PlayerCalendarMatchInfo
) {
    PlayerCalendarDetailsSectionCard(
        title = "Match Info",
        icon = "◉"
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
            valueColor = if (jogo.isLive) BrandGreen else PlayerCalendarBlue
        )
        PlayerCalendarDetailsInfoRow("Match ID", "#${jogo.idJogo}")
    }
}

@Composable
fun PlayerCalendarDetailsLocationCard(
    jogo: PlayerCalendarMatchInfo
) {
    PlayerCalendarDetailsSectionCard(
        title = "Location",
        icon = "⌖"
    ) {
        Text(
            text = jogo.local,
            color = BrandBlue,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFD8E1EE)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(PlayerCalendarRed)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
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

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = BrandBlue
            )
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
        title = "Teams",
        icon = "⚑"
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
        title = "About this match",
        icon = "i"
    ) {
        Text(
            text = "This match belongs to ${jogo.torneioNome}. It is scheduled at ${jogo.local} and will be played between ${jogo.equipaCasa} and ${jogo.equipaFora}.",
            color = PlayerCalendarTextGray,
            fontSize = 13.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PlayerCalendarDetailsActionsCard(
    jogo: PlayerCalendarMatchInfo,
    reminderSet: Boolean,
    onReminderClick: () -> Unit,
    onWatchLiveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = PlayerCalendarCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Ações",
                color = BrandBlue,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (jogo.isLive) {
                Button(
                    onClick = onWatchLiveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
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
                    onClick = onReminderClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (reminderSet) BrandGreen else PlayerCalendarBlue,
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
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp)
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
}

@Composable
fun PlayerCalendarDetailsSectionCard(
    title: String,
    icon: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = PlayerCalendarCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(34.dp)
                        .height(34.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(BrandGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
                        color = BrandGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

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
fun PlayerCalendarDetailsInfoRow(
    label: String,
    value: String,
    valueColor: Color = BrandBlue
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = PlayerCalendarTextGray,
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
                .width(48.dp)
                .height(48.dp)
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

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = teamName,
            color = BrandBlue,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PlayerCalendarDetailsTeamBlockDark(
    teamName: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(48.dp)
                .height(48.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.13f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = calendarDetailsInitials(teamName),
                color = BrandWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = teamName,
            color = BrandWhite,
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
            .padding(horizontal = 10.dp, vertical = 6.dp),
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PlayerCalendarCardBg),
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
