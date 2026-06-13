package com.example.trabalhocm.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trabalhocm.R
import com.example.trabalhocm.ui.screens.organizador.OrganizerCalendarViewModel
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val DarkBlue = Color(0xFF111827)
private val PrimaryBlue = Color(0xFF0346B8)
private val TealGreen = Color(0xFF0CA789)
private val TextGray = Color(0xFF64748B)
private val BgLight = Color(0xFFF8FAFC)
private val CardBg = Color(0xFFFFFFFF)
private val InputBg = Color(0xFFF1F5F9)

data class MatchEvent(
    val day: Int,
    val time: String,
    val sport: String,
    val team1: String,
    val team2: String,
    val statusText: String,
    val subStatus: String,
    val location: String,
    val isLive: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchesCalendarScreen(
    viewModel: OrganizerCalendarViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val hoje = remember { LocalDate.now() }
    var displayedMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDay by remember { mutableStateOf(hoje.dayOfMonth) }

    val sportFootball = stringResource(R.string.sport_football)
    val sportBasketball = stringResource(R.string.sport_basketball)
    val sportVolleyball = stringResource(R.string.sport_volleyball)
    val sportDefault = stringResource(R.string.sport_default)
    val liveLabel = stringResource(R.string.badge_live_now)

    val allMatches = viewModel.jogos
        .filter { it.ano == displayedMonth.year && it.mes == displayedMonth.monthValue }
        .map { g ->
            MatchEvent(
                day = g.dia,
                time = g.hora,
                sport = when (g.idModalidade) {
                    1L -> sportFootball
                    2L -> sportBasketball
                    3L -> sportVolleyball
                    else -> sportDefault
                },
                team1 = g.team1,
                team2 = g.team2,
                statusText = g.score,
                subStatus = if (g.isLive) liveLabel else g.hora,
                location = g.local,
                isLive = g.isLive
            )
        }

    val daysWithEvents = allMatches.map { it.day }.distinct()

    val filteredMatches = allMatches.filter { it.day == selectedDay }
    val isCurrentMonth = displayedMonth == YearMonth.from(hoje)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_calendar), color = Color.White, fontWeight = FontWeight.Bold) },
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
            MatchLeagueBottomBar(selectedTab = "MATCHES", onHomeClick = onHomeClick)
        },
        containerColor = BgLight
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(stringResource(R.string.tag_premier_league), color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(R.string.title_matches_calendar), color = DarkBlue, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.desc_matches_calendar), color = TextGray, fontSize = 14.sp)
            }

            item {
                Text(stringResource(R.string.label_calendar), color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                CalendarWidget(
                    displayedMonth = displayedMonth,
                    selectedDay = selectedDay,
                    todayDay = if (isCurrentMonth) hoje.dayOfMonth else -1,
                    daysWithEvents = daysWithEvents,
                    onDaySelected = { selectedDay = it },
                    onPrevMonth = {
                        displayedMonth = displayedMonth.minusMonths(1)
                        selectedDay = 1
                    },
                    onNextMonth = {
                        displayedMonth = displayedMonth.plusMonths(1)
                        selectedDay = 1
                    }
                )
            }

            if (viewModel.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
            } else if (filteredMatches.isEmpty()) {
                item {
                    EmptyStateCard()
                }
            } else {
                items(filteredMatches) { match ->
                    MatchCard(match = match)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun CalendarWidget(
    displayedMonth: java.time.YearMonth,
    selectedDay: Int,
    todayDay: Int,
    daysWithEvents: List<Int>,
    onDaySelected: (Int) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val nomeMes = displayedMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                .replaceFirstChar { it.uppercase() }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$nomeMes ${displayedMonth.year}", color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallIconButton(Icons.AutoMirrored.Filled.KeyboardArrowLeft, onClick = onPrevMonth)
                    SmallIconButton(Icons.AutoMirrored.Filled.KeyboardArrowRight, onClick = onNextMonth)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val weekDays = listOf(
                stringResource(R.string.day_sun),
                stringResource(R.string.day_mon),
                stringResource(R.string.day_tue),
                stringResource(R.string.day_wed),
                stringResource(R.string.day_thu),
                stringResource(R.string.day_fri),
                stringResource(R.string.day_sat)
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                weekDays.forEach { day ->
                    Text(day, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grelha real do mês mostrado: dias nulos = células vazias de alinhamento
            val calendarGrid = remember(displayedMonth) {
                val primeiroDia = displayedMonth.atDay(1)
                val diasNoMes = displayedMonth.lengthOfMonth()
                val offsetInicial = primeiroDia.dayOfWeek.value % 7 // Domingo = 0

                val celulas = buildList<Int?> {
                    repeat(offsetInicial) { add(null) }
                    for (d in 1..diasNoMes) add(d)
                    while (size % 7 != 0) add(null)
                }
                celulas.chunked(7)
            }

            calendarGrid.forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    week.forEach { day ->
                        if (day == null) {
                            Spacer(modifier = Modifier.width(36.dp))
                        } else {
                            DayCell(
                                day = day,
                                isCurrentMonth = true,
                                isSelected = (day == selectedDay),
                                isToday = (day == todayDay),
                                hasEvent = daysWithEvents.contains(day),
                                onClick = { onDaySelected(day) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayCell(
    day: Int,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    hasEvent: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isToday) PrimaryBlue else if (isSelected) Color.White else if (isCurrentMonth) InputBg else Color.Transparent
    val textColor = if (isToday) Color.White else if (isCurrentMonth) DarkBlue else Color.LightGray
    val borderColor = if (isSelected && !isToday) PrimaryBlue else Color.Transparent

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(36.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(bgColor)
                .border(1.5.dp, borderColor, RoundedCornerShape(8.dp))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = day.toString(), color = textColor, fontSize = 14.sp, fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal)
        }

        Box(modifier = Modifier.height(6.dp)) {
            if (hasEvent) {
                Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(TealGreen).align(Alignment.Center))
            }
        }
    }
}

@Composable
fun MatchCard(match: MatchEvent) {
    Column {
        Text("${match.time} • ${stringResource(R.string.timezone_gmt)}", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = CardBg),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatusBadge(
                        text = if (match.isLive) stringResource(R.string.badge_live_now) else stringResource(R.string.badge_upcoming),
                        color = if (match.isLive) TealGreen else PrimaryBlue
                    )
                    Surface(color = InputBg, shape = RoundedCornerShape(12.dp)) {
                        Text(match.sport, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TeamColumn(name = match.team1)

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(match.statusText, color = DarkBlue, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                        Text(match.subStatus, color = if (match.isLive) TealGreen else TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    TeamColumn(name = match.team2)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(match.location, color = TextGray, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (match.isLive) {
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = TealGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text(stringResource(R.string.btn_watch_live), fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, PrimaryBlue),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Text(stringResource(R.string.btn_set_reminder), color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Text(stringResource(R.string.btn_details), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeamColumn(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) {
        Box(
            modifier = Modifier.size(48.dp).clip(CircleShape).background(InputBg),
            contentAlignment = Alignment.Center
        ) {
            Text(name.take(1), color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(name, color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun EmptyStateCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.title_no_tournaments), color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(stringResource(R.string.desc_no_tournaments), color = TextGray, fontSize = 14.sp)
        }
    }
}

@Composable
fun StatusBadge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
    }
}

@Composable
fun SmallIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = CardBg,
        border = BorderStroke(1.dp, InputBg),
        modifier = Modifier.size(32.dp).clickable { onClick() }
    ) {
        Icon(icon, contentDescription = null, tint = DarkBlue, modifier = Modifier.padding(6.dp))
    }
}

