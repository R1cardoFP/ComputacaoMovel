package com.example.trabalhocm.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.trabalhocm.data.model.AdminCasualMatch
import com.example.trabalhocm.data.repository.AdminCasualMatchRepository
import com.example.trabalhocm.ui.theme.AppIcons
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import com.example.trabalhocm.ui.theme.CardBg
import com.example.trabalhocm.ui.theme.ErrorRed
import com.example.trabalhocm.ui.theme.PrimaryBlue
import com.example.trabalhocm.ui.theme.TextGray
import com.example.trabalhocm.R
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.time.ZoneId

@Composable
fun AdminMatchesCalendarScreen(
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onViewDetailsClick: (String) -> Unit = {},
    onEditClick: (String) -> Unit = {},
    onWatchLiveClick: (String) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { AdminCasualMatchRepository() }

    var matches by remember { mutableStateOf<List<AdminCasualMatch>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }

    val errorLoadingCalendarText = stringResource(R.string.admin_matches_error_loading_calendar)
    val selectedDateFormatter = DateTimeFormatter.ofPattern(
        stringResource(R.string.admin_matches_date_format),
        Locale.getDefault()
    )

    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = ""

        repository.listarPeladinhasAdmin()
            .onSuccess {
                matches = it
            }
            .onFailure {
                errorMessage = "$errorLoadingCalendarText: ${it.message}"
            }

        isLoading = false
    }

    val matchesByDate = remember(matches) {
        matches.mapNotNull { match ->
            parseMatchDate(match.date)?.let { date ->
                date to match
            }
        }.groupBy(
            keySelector = { it.first },
            valueTransform = { it.second }
        )
    }

    val selectedMatches = matchesByDate[selectedDate].orEmpty()
        .sortedBy { it.time }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            AdminMatchesCalendarTopBar(
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick
            )
        },
        bottomBar = {
            AdminMatchesCalendarBottomBar(
                selected = "matches",
                onHomeClick = onHomeClick,
                onTournamentsClick = onTournamentsClick,
                onMatchesClick = onMatchesClick,
                onTeamsClick = onTeamsClick,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandGreen)
                }
            }

            errorMessage.isNotBlank() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage,
                        color = ErrorRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(
                        start = 18.dp,
                        end = 18.dp,
                        top = 16.dp,
                        bottom = 28.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.admin_matches_console).uppercase(),
                            color = BrandGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = stringResource(R.string.admin_matches_title),
                            color = BrandBlue,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = stringResource(R.string.admin_matches_description),
                            color = TextGray,
                            fontSize = 12.sp
                        )
                    }

                    item {
                        Text(
                            text = stringResource(R.string.admin_matches_calendar_label).uppercase(),
                            color = TextGray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    item {
                        CalendarCard(
                            selectedDate = selectedDate,
                            currentMonth = currentMonth,
                            matchesByDate = matchesByDate,
                            onPreviousMonthClick = {
                                currentMonth = currentMonth.minusMonths(1)
                            },
                            onNextMonthClick = {
                                currentMonth = currentMonth.plusMonths(1)
                            },
                            onDateSelected = { date ->
                                selectedDate = date
                                currentMonth = YearMonth.from(date)
                            }
                        )
                    }

                    item {
                        Text(
                            text = selectedDate.format(selectedDateFormatter),
                            color = BrandBlue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (selectedMatches.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(9.dp),
                                colors = CardDefaults.cardColors(containerColor = CardBg),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.admin_matches_no_matches_day),
                                    color = TextGray,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    } else {
                        items(selectedMatches) { match ->
                            CalendarMatchCard(
                                match = match,
                                onViewDetailsClick = {
                                    if (match.isLive) {
                                        onWatchLiveClick(match.id)
                                    } else {
                                        onViewDetailsClick(match.id)
                                    }
                                },
                                onEditClick = {
                                    if (match.isLive) {
                                        onWatchLiveClick(match.id)
                                    } else {
                                        onEditClick(match.id)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarCard(
    selectedDate: LocalDate,
    currentMonth: YearMonth,
    matchesByDate: Map<LocalDate, List<AdminCasualMatch>>,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    val days = calendarDaysForMonth(currentMonth)
    val today = LocalDate.now(ZoneId.of("Europe/Lisbon"))
    val monthFormatter = DateTimeFormatter.ofPattern(
        stringResource(R.string.admin_matches_month_format),
        Locale.getDefault()
    )
    val weekDays = listOf(
        stringResource(R.string.admin_weekday_mon),
        stringResource(R.string.admin_weekday_tue),
        stringResource(R.string.admin_weekday_wed),
        stringResource(R.string.admin_weekday_thu),
        stringResource(R.string.admin_weekday_fri),
        stringResource(R.string.admin_weekday_sat),
        stringResource(R.string.admin_weekday_sun)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentMonth.format(monthFormatter),
                    color = BrandBlue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                SmallCalendarButton(
                    icon = AppIcons.Back,
                    onClick = onPreviousMonthClick
                )

                Spacer(modifier = Modifier.width(6.dp))

                SmallCalendarButton(
                    icon = AppIcons.ChevronRight,
                    onClick = onNextMonthClick
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                weekDays.forEach { day ->
                    Text(
                        text = day,
                        color = TextGray,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(7.dp))

            days.chunked(7).forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    week.forEach { calendarDay ->
                        CalendarDayCell(
                            date = calendarDay.date,
                            isCurrentMonth = calendarDay.isCurrentMonth,
                            isSelected = calendarDay.date == selectedDate,
                            isToday = calendarDay.date == today,
                            hasMatches = matchesByDate[calendarDay.date].orEmpty().isNotEmpty(),
                            onClick = {
                                onDateSelected(calendarDay.date)
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))
            }
        }
    }
}

@Composable
private fun SmallCalendarButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(28.dp)
            .background(
                color = Color(0xFFF1F5F9),
                shape = CircleShape
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = BrandBlue,
            modifier = Modifier.size(15.dp)
        )
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate,
    isCurrentMonth: Boolean,
    isSelected: Boolean,
    isToday: Boolean,
    hasMatches: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = when {
        isSelected -> PrimaryBlue
        isToday -> Color(0xFFE0E7FF)
        isCurrentMonth -> Color(0xFFF1F5F9)
        else -> Color(0xFFE5E7EB)
    }

    val textColor = when {
        isSelected -> BrandWhite
        isToday -> PrimaryBlue
        isCurrentMonth -> BrandBlue
        else -> TextGray
    }

    val border = if (isToday && !isSelected) {
        BorderStroke(1.dp, PrimaryBlue)
    } else {
        null
    }

    Card(
        modifier = modifier
            .height(38.dp)
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        border = border,
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
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

                if (hasMatches) {
                    Spacer(modifier = Modifier.height(2.dp))

                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isSelected -> BrandWhite
                                    isToday -> PrimaryBlue
                                    else -> BrandGreen
                                }
                            )
                    )
                } else {
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }
}

@Composable
private fun CalendarMatchCard(
    match: AdminCasualMatch,
    onViewDetailsClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "${match.time.take(5)} · GMT+1",
            color = TextGray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(9.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(13.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CalendarStatusBadge(match = match)

                    Spacer(modifier = Modifier.width(6.dp))

                    SportBadge(text = match.modalidade)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CalendarTeamColumn(
                        name = match.homeTeamName,
                        modifier = Modifier.weight(1f)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(72.dp)
                    ) {
                        Text(
                            text = if (match.isLive) {
                                "${match.homeScore} - ${match.awayScore}"
                            } else {
                                "VS"
                            },
                            color = if (match.isLive) ErrorRed else BrandBlue,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = if (match.isLive) {
                                stringResource(R.string.admin_matches_live_now)
                            } else {
                                calendarMatchStatusText(match.status)
                            },
                            color = TextGray,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }

                    CalendarTeamColumn(
                        name = match.awayTeamName,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(11.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = AppIcons.Location,
                        contentDescription = null,
                        tint = TextGray,
                        modifier = Modifier.size(14.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = match.local,
                        color = TextGray,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onViewDetailsClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        shape = RoundedCornerShape(5.dp),
                        border = BorderStroke(1.dp, PrimaryBlue),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (match.isLive) BrandGreen else BrandWhite,
                            contentColor = if (match.isLive) BrandWhite else PrimaryBlue
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = if (match.isLive) {
                                stringResource(R.string.admin_matches_watch_live).uppercase()
                            } else {
                                stringResource(R.string.admin_matches_view_details).uppercase()
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }

                    Button(
                        onClick = onEditClick,
                        enabled = match.status != "CANCELED",
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue,
                            contentColor = BrandWhite,
                            disabledContainerColor = TextGray,
                            disabledContentColor = BrandWhite
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.admin_matches_edit).uppercase(),
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
private fun calendarMatchStatusText(status: String): String {
    return when (status.uppercase()) {
        "CANCELED" -> stringResource(R.string.admin_matches_status_canceled)
        "CLOSED" -> stringResource(R.string.admin_matches_status_closed)
        else -> stringResource(R.string.admin_matches_status_upcoming)
    }
}

@Composable
private fun CalendarStatusBadge(match: AdminCasualMatch) {
    val text = when {
        match.isLive -> stringResource(R.string.admin_matches_status_live_now).uppercase()
        match.status == "CANCELED" -> stringResource(R.string.admin_matches_status_canceled).uppercase()
        match.status == "CLOSED" -> stringResource(R.string.admin_matches_status_closed).uppercase()
        else -> stringResource(R.string.admin_matches_status_upcoming).uppercase()
    }

    val background = when {
        match.isLive -> Color(0xFFEAF8F5)
        match.status == "CANCELED" -> Color(0xFFFEE2E2)
        match.status == "CLOSED" -> Color(0xFFFEF3C7)
        else -> Color(0xFFE0E7FF)
    }

    val color = when {
        match.isLive -> BrandGreen
        match.status == "CANCELED" -> ErrorRed
        match.status == "CLOSED" -> Color(0xFFEAB308)
        else -> PrimaryBlue
    }

    Box(
        modifier = Modifier
            .background(
                color = background,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SportBadge(text: String) {
    Box(
        modifier = Modifier
            .background(
                color = Color(0xFFE0E7FF),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text.uppercase(),
            color = PrimaryBlue,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
private fun CalendarTeamColumn(
    name: String,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(45.dp)
                .clip(CircleShape)
                .background(PrimaryBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials(name),
                color = BrandWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = name,
            color = BrandBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AdminMatchesCalendarTopBar(
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandBlue)
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                onBackClick()
            }
        ) {
            Icon(
                imageVector = AppIcons.Back,
                contentDescription = stringResource(R.string.admin_common_back),
                tint = BrandWhite,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.admin_matches_calendar_top_title),
                color = BrandWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Icon(
            imageVector = AppIcons.Notifications,
            contentDescription = stringResource(R.string.admin_common_notifications),
            tint = BrandWhite,
            modifier = Modifier
                .size(23.dp)
                .clickable {
                    onNotificationsClick()
                }
        )
    }
}

@Composable
private fun AdminMatchesCalendarBottomBar(
    selected: String,
    onHomeClick: () -> Unit,
    onTournamentsClick: () -> Unit,
    onMatchesClick: () -> Unit,
    onTeamsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandWhite)
            .navigationBarsPadding()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomCalendarItem(AppIcons.Home, stringResource(R.string.admin_nav_home).uppercase(), selected == "home", onHomeClick)
        BottomCalendarItem(AppIcons.Tournaments, stringResource(R.string.admin_nav_tournaments).uppercase(), selected == "tournaments", onTournamentsClick)
        BottomCalendarItem(AppIcons.Games, stringResource(R.string.admin_nav_matches).uppercase(), selected == "matches", onMatchesClick)
        BottomCalendarItem(AppIcons.Teams, stringResource(R.string.admin_nav_teams).uppercase(), selected == "teams", onTeamsClick)
        BottomCalendarItem(AppIcons.Profile, stringResource(R.string.admin_nav_profile).uppercase(), selected == "profile", onProfileClick)
    }
}

@Composable
private fun BottomCalendarItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) PrimaryBlue else TextGray

    Column(
        modifier = Modifier.clickable {
            onClick()
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = color,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp
        )
    }
}

private data class CalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean
)

private fun calendarDaysForMonth(month: YearMonth): List<CalendarDay> {
    val firstDay = month.atDay(1)
    val daysToSubtract = firstDay.dayOfWeek.value - 1
    val startDate = firstDay.minusDays(daysToSubtract.toLong())

    return (0 until 42).map { index ->
        val date = startDate.plusDays(index.toLong())

        CalendarDay(
            date = date,
            isCurrentMonth = YearMonth.from(date) == month
        )
    }
}

private fun parseMatchDate(value: String): LocalDate? {
    return try {
        LocalDate.parse(value.take(10))
    } catch (e: Exception) {
        null
    }
}

private fun initials(name: String): String {
    val parts = name.trim()
        .split(" ")
        .filter { it.isNotBlank() }

    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts.first().take(2).uppercase()
        else -> "${parts.first().take(1)}${parts.last().take(1)}".uppercase()
    }
}