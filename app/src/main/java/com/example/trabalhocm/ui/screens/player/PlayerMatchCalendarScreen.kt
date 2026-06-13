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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.repository.PlayerCalendarMatchInfo
import com.example.trabalhocm.data.repository.PlayerMatchCalendarRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val CalendarBg = Color(0xFFF4F7FB)
private val CalendarCardBg = Color.White
private val CalendarDarkBlue = Color(0xFF0B1F4D)
private val CalendarPrimaryBlue = Color(0xFF0757C8)
private val CalendarInputBg = Color(0xFFF0F3FA)
private val CalendarTextGray = Color(0xFF6D7486)
private val CalendarSoftGreen = Color(0xFFEAF8F0)
private val CalendarSoftBlue = Color(0xFFEAF0FB)

@Composable
fun PlayerMatchCalendarScreen(
    onBackClick: () -> Unit = {},
    onWatchLiveClick: (Long) -> Unit = {},
    onDetailsClick: (Long) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { PlayerMatchCalendarRepository() }

    var jogos by remember { mutableStateOf<List<PlayerCalendarMatchInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var mensagemErro by remember { mutableStateOf("") }

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedMonth by remember { mutableStateOf(YearMonth.from(LocalDate.now())) }

    val lembretes = remember { mutableStateListOf<Long>() }

    LaunchedEffect(Unit) {
        isLoading = true
        mensagemErro = ""

        repository.listarJogosCalendario()
            .onSuccess { lista ->
                jogos = lista

                val primeiraData = lista.firstOrNull()?.dataLocal
                if (primeiraData != null) {
                    selectedDate = primeiraData
                    selectedMonth = YearMonth.from(primeiraData)
                }
            }
            .onFailure {
                mensagemErro = it.message ?: "Erro ao carregar calendário."
            }

        isLoading = false
    }

    val jogosDoMes = jogos.filter {
        it.dataLocal?.let { data ->
            YearMonth.from(data) == selectedMonth
        } ?: false
    }

    val jogosDoDia = jogos.filter {
        it.dataLocal == selectedDate
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CalendarBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        CalendarTopBar(
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            CalendarHeaderCard(
                selectedDate = selectedDate,
                matchesToday = jogosDoDia.size,
                matchesMonth = jogosDoMes.size
            )

            Spacer(modifier = Modifier.height(20.dp))

            SectionLabel(text = "CALENDAR")

            Spacer(modifier = Modifier.height(10.dp))

            CalendarMonthCard(
                selectedMonth = selectedMonth,
                selectedDate = selectedDate,
                jogosDoMes = jogosDoMes,
                onPreviousMonth = {
                    selectedMonth = selectedMonth.minusMonths(1)
                    selectedDate = selectedMonth.atDay(1)
                },
                onNextMonth = {
                    selectedMonth = selectedMonth.plusMonths(1)
                    selectedDate = selectedMonth.atDay(1)
                },
                onDayClick = { date ->
                    selectedDate = date
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            SelectedDayCard(
                selectedDate = selectedDate,
                matchesCount = jogosDoDia.size,
                firstMatchTime = jogosDoDia.firstOrNull()?.hora?.take(5)
            )

            Spacer(modifier = Modifier.height(14.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BrandGreen)
                    }
                }

                mensagemErro.isNotBlank() -> {
                    MessageCard(
                        message = "Erro: $mensagemErro",
                        isError = true
                    )
                }

                jogosDoDia.isEmpty() -> {
                    CalendarEmptyCard()
                }

                else -> {
                    jogosDoDia.forEach { jogo ->
                        CalendarMatchCard(
                            jogo = jogo,
                            reminderSet = lembretes.contains(jogo.idJogo),
                            onReminderClick = {
                                if (lembretes.contains(jogo.idJogo)) {
                                    lembretes.remove(jogo.idJogo)
                                } else {
                                    lembretes.add(jogo.idJogo)
                                }
                            },
                            onDetailsClick = {
                                onDetailsClick(jogo.idJogo)
                            },
                            onWatchLiveClick = {
                                onWatchLiveClick(jogo.idJogo)
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))
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
fun CalendarTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .background(CalendarDarkBlue)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f))
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "‹",
                color = BrandWhite,
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "Match Calendar",
                color = BrandWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Fixtures and reminders",
                color = BrandWhite.copy(alpha = 0.72f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .width(40.dp)
                .height(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "●",
                color = BrandGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CalendarHeaderCard(
    selectedDate: LocalDate,
    matchesToday: Int,
    matchesMonth: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CalendarDarkBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "PREMIER LEAGUE",
                color = BrandGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Matches Calendar",
                color = BrandWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Browse scheduled matches, open details and set reminders.",
                color = BrandWhite.copy(alpha = 0.76f),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                HeaderMetric(
                    label = "Selected day",
                    value = selectedDate.format(DateTimeFormatter.ofPattern("dd MMM", Locale.ENGLISH)),
                    modifier = Modifier.weight(1f)
                )

                HeaderMetric(
                    label = "Today matches",
                    value = matchesToday.toString(),
                    modifier = Modifier.weight(1f)
                )

                HeaderMetric(
                    label = "This month",
                    value = matchesMonth.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun HeaderMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .padding(horizontal = 10.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = BrandWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label.uppercase(),
            color = BrandWhite.copy(alpha = 0.68f),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp,
            textAlign = TextAlign.Center,
            lineHeight = 10.sp
        )
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        color = CalendarTextGray,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.2.sp
    )
}

@Composable
fun SelectedDayCard(
    selectedDate: LocalDate,
    matchesCount: Int,
    firstMatchTime: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CalendarCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CalendarSoftBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = selectedDate.dayOfMonth.toString(),
                    color = CalendarPrimaryBlue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = selectedDate.format(DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy", Locale.ENGLISH)),
                    color = CalendarDarkBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (firstMatchTime != null) {
                        "$firstMatchTime · GMT+1"
                    } else {
                        "No match time available"
                    },
                    color = CalendarTextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            CalendarBadge(
                text = "$matchesCount MATCHES",
                backgroundColor = if (matchesCount > 0) CalendarSoftGreen else CalendarInputBg,
                textColor = if (matchesCount > 0) BrandGreen else CalendarTextGray
            )
        }
    }
}

@Composable
fun CalendarMonthCard(
    selectedMonth: YearMonth,
    selectedDate: LocalDate,
    jogosDoMes: List<PlayerCalendarMatchInfo>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (LocalDate) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)
    val diasComJogos = jogosDoMes.mapNotNull { it.dataLocal }.toSet()
    val today = LocalDate.now()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CalendarCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = selectedMonth.atDay(1).format(formatter),
                        color = CalendarDarkBlue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${jogosDoMes.size} matches this month",
                        color = CalendarTextGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                CalendarSmallButton(
                    text = "‹",
                    onClick = onPreviousMonth
                )

                Spacer(modifier = Modifier.width(8.dp))

                CalendarSmallButton(
                    text = "›",
                    onClick = onNextMonth
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("DOM", "SEG", "TER", "QUA", "QUI", "SEX", "SÁB").forEach {
                    Text(
                        text = it,
                        color = CalendarTextGray,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(34.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val dias = gerarDiasCalendario(selectedMonth)

            dias.chunked(7).forEach { semana ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    semana.forEach { data ->
                        if (data == null) {
                            Spacer(
                                modifier = Modifier
                                    .width(34.dp)
                                    .height(38.dp)
                            )
                        } else {
                            CalendarDayCell(
                                date = data,
                                selected = data == selectedDate,
                                isToday = data == today,
                                hasMatch = diasComJogos.contains(data),
                                onClick = {
                                    onDayClick(data)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(7.dp))
            }
        }
    }
}

@Composable
fun CalendarSmallButton(
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(34.dp)
            .height(34.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CalendarInputBg)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = CalendarDarkBlue,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CalendarDayCell(
    date: LocalDate,
    selected: Boolean,
    isToday: Boolean,
    hasMatch: Boolean,
    onClick: () -> Unit
) {
    val background = when {
        selected -> CalendarDarkBlue
        isToday -> CalendarSoftGreen
        else -> CalendarInputBg
    }

    val textColor = when {
        selected -> BrandWhite
        isToday -> BrandGreen
        else -> CalendarDarkBlue
    }

    Box(
        modifier = Modifier
            .width(34.dp)
            .height(38.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                color = textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Box(
                modifier = Modifier
                    .width(if (hasMatch) 5.dp else 3.dp)
                    .height(if (hasMatch) 5.dp else 3.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            hasMatch && selected -> BrandWhite
                            hasMatch -> BrandGreen
                            else -> Color.Transparent
                        }
                    )
            )
        }
    }
}

@Composable
fun CalendarMatchCard(
    jogo: PlayerCalendarMatchInfo,
    reminderSet: Boolean,
    onReminderClick: () -> Unit,
    onDetailsClick: () -> Unit,
    onWatchLiveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CalendarCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CalendarBadge(
                    text = if (jogo.isLive) "● LIVE NOW" else if (jogo.isFinished) "● COMPLETED" else "● UPCOMING",
                    backgroundColor = if (jogo.isLive) CalendarSoftGreen else CalendarSoftBlue,
                    textColor = if (jogo.isLive) BrandGreen else CalendarPrimaryBlue
                )

                CalendarBadge(
                    text = jogo.modalidadeNome.uppercase(),
                    backgroundColor = CalendarInputBg,
                    textColor = CalendarTextGray
                )

                if (reminderSet) {
                    CalendarBadge(
                        text = "REMINDER SET",
                        backgroundColor = CalendarSoftGreen,
                        textColor = BrandGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CalendarTeamBlock(
                    teamName = jogo.equipaCasa,
                    modifier = Modifier.weight(1f)
                )

                MatchScoreBlock(jogo = jogo)

                CalendarTeamBlock(
                    teamName = jogo.equipaFora,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CalendarInputBg)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "⌖ ${jogo.local}",
                    color = CalendarTextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (jogo.isLive) {
                Button(
                    onClick = onWatchLiveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen,
                        contentColor = BrandWhite
                    )
                ) {
                    Text(
                        text = "WATCH LIVE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onReminderClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = CalendarPrimaryBlue
                        )
                    ) {
                        Text(
                            text = if (reminderSet) "REMINDER SET" else "SET REMINDER",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = onDetailsClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CalendarDarkBlue,
                            contentColor = BrandWhite
                        )
                    ) {
                        Text(
                            text = "DETAILS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MatchScoreBlock(jogo: PlayerCalendarMatchInfo) {
    Box(
        modifier = Modifier
            .width(82.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(CalendarSoftBlue)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (jogo.isLive || jogo.isFinished) {
                Text(
                    text = "${jogo.pontosCasa} - ${jogo.pontosFora}",
                    color = CalendarDarkBlue,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                if (jogo.isLive) {
                    Text(
                        text = "75'",
                        color = Color(0xFFE53935),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Text(
                    text = "VS",
                    color = CalendarDarkBlue,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = jogo.hora.take(5),
                    color = CalendarTextGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CalendarTeamBlock(
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
                .background(CalendarSoftBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = calendarTeamInitials(teamName),
                color = CalendarDarkBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = teamName,
            color = CalendarDarkBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 13.sp
        )
    }
}

@Composable
fun CalendarBadge(
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
fun MessageCard(
    message: String,
    isError: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) Color(0xFFFFF1F1) else CalendarSoftGreen
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = message,
            color = if (isError) Color(0xFFD01818) else BrandGreen,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun CalendarEmptyCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CalendarCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 34.dp, horizontal = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(58.dp)
                    .height(58.dp)
                    .clip(CircleShape)
                    .background(CalendarInputBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "–",
                    color = CalendarTextGray,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "No matches scheduled",
                color = CalendarDarkBlue,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "There are no matches on this selected day.",
                color = CalendarTextGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

fun gerarDiasCalendario(month: YearMonth): List<LocalDate?> {
    val primeiroDia = month.atDay(1)
    val totalDias = month.lengthOfMonth()

    val espacosAntes = primeiroDia.dayOfWeek.value % 7

    val lista = mutableListOf<LocalDate?>()

    repeat(espacosAntes) {
        lista.add(null)
    }

    for (dia in 1..totalDias) {
        lista.add(month.atDay(dia))
    }

    while (lista.size % 7 != 0) {
        lista.add(null)
    }

    return lista
}

fun calendarTeamInitials(teamName: String): String {
    val words = teamName.split(" ").filter { it.isNotBlank() }

    return when {
        words.isEmpty() -> "?"
        words.size == 1 -> words.first().take(2).uppercase()
        else -> words.take(2).joinToString("") { it.first().uppercaseChar().toString() }
    }
}
