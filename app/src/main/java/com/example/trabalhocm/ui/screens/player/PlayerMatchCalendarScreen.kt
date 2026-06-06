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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
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
            .background(Color(0xFFF4F5FA))
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
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Text(
                text = "PREMIER LEAGUE",
                color = Color(0xFF0757C8),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.3.sp
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "Matches Calendar",
                color = BrandBlue,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Browse matches scheduled for today and set\nreminders.",
                color = Color(0xFF51607A),
                fontSize = 12.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "CALENDAR",
                color = Color(0xFF6D7486),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.height(14.dp))

            val horaPrimeiroJogo = jogosDoDia.firstOrNull()?.hora?.take(5)

            Text(
                text = if (horaPrimeiroJogo != null) {
                    "$horaPrimeiroJogo · GMT+1"
                } else {
                    selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.ENGLISH))
                },
                color = Color(0xFF6D7486),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

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
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "Erro: $mensagemErro",
                            color = Color(0xFFD01818),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
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
            text = "Calendar",
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
fun CalendarMonthCard(
    selectedMonth: YearMonth,
    selectedDate: LocalDate,
    jogosDoMes: List<PlayerCalendarMatchInfo>,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDayClick: (LocalDate) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH)

    val diasComJogos = jogosDoMes.mapNotNull { it.dataLocal }.toSet()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedMonth.atDay(1).format(formatter),
                    color = BrandBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                CalendarSmallButton(
                    text = "‹",
                    onClick = onPreviousMonth
                )

                Spacer(modifier = Modifier.width(6.dp))

                CalendarSmallButton(
                    text = "›",
                    onClick = onNextMonth
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("DOM", "SEG", "TER", "QUA", "QUI", "SEX", "SÁB").forEach {
                    Text(
                        text = it,
                        color = Color(0xFF6D7486),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(34.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            val dias = gerarDiasCalendario(selectedMonth)

            dias.chunked(7).forEach { semana ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    semana.forEach { data ->
                        if (data == null) {
                            Spacer(modifier = Modifier.width(34.dp).height(34.dp))
                        } else {
                            CalendarDayCell(
                                date = data,
                                selected = data == selectedDate,
                                hasMatch = diasComJogos.contains(data),
                                onClick = {
                                    onDayClick(data)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
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
            .width(28.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(Color(0xFFF0F3FA))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = BrandBlue,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CalendarDayCell(
    date: LocalDate,
    selected: Boolean,
    hasMatch: Boolean,
    onClick: () -> Unit
) {
    val background = if (selected) Color(0xFF0757C8) else Color(0xFFF0F3FA)
    val textColor = if (selected) BrandWhite else BrandBlue

    Box(
        modifier = Modifier
            .width(34.dp)
            .height(34.dp)
            .clip(RoundedCornerShape(8.dp))
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

            if (hasMatch) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(4.dp)
                        .clip(CircleShape)
                        .background(if (selected) BrandWhite else BrandGreen)
                )
            }
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
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                CalendarBadge(
                    text = if (jogo.isLive) "● LIVE NOW" else if (jogo.isFinished) "● COMPLETED" else "● UPCOMING",
                    backgroundColor = if (jogo.isLive) BrandGreen.copy(alpha = 0.12f) else Color(0xFFEAF0FB),
                    textColor = if (jogo.isLive) BrandGreen else Color(0xFF0757C8)
                )

                CalendarBadge(
                    text = jogo.modalidadeNome.uppercase(),
                    backgroundColor = Color(0xFFEFF1F6),
                    textColor = Color(0xFF6D7486)
                )

                if (reminderSet) {
                    CalendarBadge(
                        text = "REMINDER SET",
                        backgroundColor = BrandGreen.copy(alpha = 0.12f),
                        textColor = BrandGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CalendarTeamBlock(
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
                            fontSize = 26.sp,
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
                            color = BrandBlue,
                            fontSize = 24.sp,
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

                CalendarTeamBlock(
                    teamName = jogo.equipaFora,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "⌖ ${jogo.local}",
                color = Color(0xFF6D7486),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (jogo.isLive) {
                Button(
                    onClick = onWatchLiveClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen,
                        contentColor = BrandWhite
                    )
                ) {
                    Text(
                        text = "WATCH LIVE",
                        fontSize = 11.sp,
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
                            .height(42.dp),
                        shape = RoundedCornerShape(5.dp)
                    ) {
                        Text(
                            text = if (reminderSet) "REMINDER SET" else "SET REMINDER",
                            color = Color(0xFF0757C8),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onDetailsClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0757C8),
                            contentColor = BrandWhite
                        )
                    ) {
                        Text(
                            text = "DETAILS",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
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
                .width(42.dp)
                .height(42.dp)
                .clip(CircleShape)
                .background(Color(0xFFEAF0FB)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = calendarTeamInitials(teamName),
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
fun CalendarBadge(
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
fun CalendarEmptyCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No tournaments",
                color = BrandBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "There are no tournaments on this day.",
                color = Color(0xFF6D7486),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
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