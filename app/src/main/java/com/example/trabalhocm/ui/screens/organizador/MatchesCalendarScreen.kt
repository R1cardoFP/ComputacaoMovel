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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val today = 14
    var selectedDay by remember { mutableStateOf(14) }

    val allMatches = listOf(
        MatchEvent(14, "14:00", "FOOTBALL", "Sporting", "Vianense", "2 - 1", "72'", "Emirates Stadium • Matchday 26", true),
        MatchEvent(14, "16:30", "BASKETBALL", "Benfica", "Sporting", "VS", "in 2h", "Etihad Stadium • Matchday 26", false),
        MatchEvent(14, "16:30", "VOLLEYBALL", "Porto", "Sporting", "VS", "in 2h", "Anfield • Matchday 26", false),
        MatchEvent(14, "19:00", "VOLLEYBALL", "Benfica", "Vianense", "VS", "in 5h", "Stadium of Light • Matchday 26", false),

        MatchEvent(3, "11:00", "BASKETBALL", "Porto", "Benfica", "VS", "at 11:00", "Dragon Arena • Matchday 26", false),
        MatchEvent(3, "18:00", "VOLLEYBALL", "Sporting", "Vianense", "VS", "at 18:00", "Green Stadium • Matchday 26", false)
    )

    val daysWithEvents = allMatches.map { it.day }.distinct()

    // Filtra os jogos para mostrar apenas os do dia selecionado
    val filteredMatches = allMatches.filter { it.day == selectedDay }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendar", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Color.White)
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
            // CABEÇALHO
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text("PREMIER LEAGUE", color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Matches Calendar", color = DarkBlue, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Browse matches scheduled for today and set reminders.", color = TextGray, fontSize = 14.sp)
            }

            // CALENDÁRIO
            item {
                Text("CALENDAR", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                CalendarWidget(
                    selectedDay = selectedDay,
                    today = today,
                    daysWithEvents = daysWithEvents,
                    onDaySelected = { selectedDay = it }
                )
            }

            // LISTA DE JOGOS
            if (filteredMatches.isEmpty()) {
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
    selectedDay: Int,
    today: Int,
    daysWithEvents: List<Int>,
    onDaySelected: (Int) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header do Calendário (Mês e Botões)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("May 2026", color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SmallIconButton(Icons.AutoMirrored.Filled.KeyboardArrowLeft)
                    SmallIconButton(Icons.AutoMirrored.Filled.KeyboardArrowRight)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dias da Semana
            val weekDays = listOf("DOM", "SEG", "TER", "QUA", "QUI", "SEX", "SAB")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                weekDays.forEach { day ->
                    Text(day, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grelha de Dias
            val calendarGrid = listOf(
                listOf(27 to false, 28 to false, 29 to false, 30 to false, 1 to true, 2 to true, 3 to true),
                listOf(4 to true, 5 to true, 6 to true, 7 to true, 8 to true, 9 to true, 10 to true),
                listOf(11 to true, 12 to true, 13 to true, 14 to true, 15 to true, 16 to true, 17 to true),
                listOf(18 to true, 19 to true, 20 to true, 21 to true, 22 to true, 23 to true, 24 to true),
                listOf(25 to true, 26 to true, 27 to true, 28 to true, 29 to true, 30 to true, 31 to true)
            )

            calendarGrid.forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    week.forEach { (day, isCurrentMonth) ->
                        val isSelected = (day == selectedDay && isCurrentMonth)
                        val isToday = (day == today && isCurrentMonth)
                        val hasEvent = daysWithEvents.contains(day) && isCurrentMonth

                        DayCell(
                            day = day,
                            isCurrentMonth = isCurrentMonth,
                            isSelected = isSelected,
                            isToday = isToday,
                            hasEvent = hasEvent,
                            onClick = { if (isCurrentMonth) onDaySelected(day) }
                        )
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
        Text("${match.time} • GMT+1", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                        text = if (match.isLive) "LIVE NOW" else "UPCOMING",
                        color = if (match.isLive) TealGreen else PrimaryBlue
                    )
                    Surface(color = InputBg, shape = RoundedCornerShape(12.dp)) {
                        Text(match.sport, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Equipas e Score
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

                // Localização
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Place, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(match.location, color = TextGray, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botões de Ação
                if (match.isLive) {
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = TealGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("WATCH LIVE", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
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
                            Text("SET REMINDER", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(48.dp)
                        ) {
                            Text("DETAILS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
            Text("No tournaments", color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("There are no tournaments on this day.", color = TextGray, fontSize = 14.sp)
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
fun SmallIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = CardBg,
        border = BorderStroke(1.dp, InputBg),
        modifier = Modifier.size(32.dp).clickable { }
    ) {
        Icon(icon, contentDescription = null, tint = DarkBlue, modifier = Modifier.padding(6.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun MatchesCalendarScreenPreview() {
    MaterialTheme {
        MatchesCalendarScreen()
    }
}