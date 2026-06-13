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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.model.AdminCasualMatch
import com.example.trabalhocm.data.repository.AdminCasualMatchRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.AppIcons
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.CardBg
import com.example.trabalhocm.ui.theme.DarkBlue
import com.example.trabalhocm.ui.theme.ErrorRed
import com.example.trabalhocm.ui.theme.InputBg
import com.example.trabalhocm.ui.theme.PrimaryBlue
import com.example.trabalhocm.ui.theme.TealGreen
import com.example.trabalhocm.ui.theme.TextGray
import com.example.trabalhocm.ui.theme.WarningYellow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCasualMatchesScreen(
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
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
    var selectedFilter by remember { mutableStateOf("All") }
    var searchText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val errorLoadingMatchesText = stringResource(R.string.admin_casual_matches_error_loading)

    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = ""

        repository.listarPeladinhasAdmin()
            .onSuccess {
                matches = it
            }
            .onFailure {
                errorMessage = "$errorLoadingMatchesText: ${it.message}"
            }

        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.admin_casual_matches_title),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.desc_back),
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = AppIcons.Notifications,
                            contentDescription = stringResource(R.string.admin_common_notifications),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        bottomBar = {
            MatchLeagueBottomBar(
                selectedTab = "MATCHES",
                onHomeClick = onHomeClick,
                onTournamentsClick = onTournamentsClick,
                onMatchesClick = onMatchesClick,
                onTeamsClick = onTeamsClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = BgLight
    ) { innerPadding ->
        AdminCasualMatchesContent(
            matches = matches,
            selectedFilter = selectedFilter,
            searchText = searchText,
            isLoading = isLoading,
            errorMessage = errorMessage,
            innerPadding = innerPadding,
            onFilterChange = { selectedFilter = it },
            onSearchChange = { searchText = it },
            onCalendarClick = onCalendarClick,
            onViewDetailsClick = onViewDetailsClick,
            onEditClick = onEditClick,
            onWatchLiveClick = onWatchLiveClick
        )
    }
}

@Composable
private fun AdminCasualMatchesContent(
    matches: List<AdminCasualMatch>,
    selectedFilter: String,
    searchText: String,
    isLoading: Boolean,
    errorMessage: String,
    innerPadding: PaddingValues,
    onFilterChange: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onCalendarClick: () -> Unit,
    onViewDetailsClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onWatchLiveClick: (String) -> Unit
) {
    val filteredMatches = matches.filter { match ->
        val query = searchText.trim()

        val matchesSearch = query.isBlank() ||
                match.title.contains(query, ignoreCase = true) ||
                match.modalidade.contains(query, ignoreCase = true) ||
                match.local.contains(query, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Live" -> match.status == "LIVE"
            "Open" -> match.status == "OPEN"
            "Canceled" -> match.status == "CANCELED"
            else -> true
        }

        matchesSearch && matchesFilter
    }

    val groupedMatches = filteredMatches.groupBy { it.sectionTitle }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 24.dp),
        contentPadding = PaddingValues(bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(R.string.admin_casual_matches_console).uppercase(),
                color = PrimaryBlue,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.admin_casual_matches_title),
                color = DarkBlue,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 36.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.admin_casual_matches_description),
                color = TextGray,
                fontSize = 14.sp
            )
        }

        item {
            CalendarActionCard(onCalendarClick = onCalendarClick)
        }

        item {
            AdminCasualMatchSearchBox(
                value = searchText,
                onValueChange = onSearchChange
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MatchFilterChip(
                    text = stringResource(R.string.admin_casual_matches_filter_all),
                    selected = selectedFilter == "All",
                    modifier = Modifier.weight(1f),
                    onClick = { onFilterChange("All") }
                )
                MatchFilterChip(
                    text = stringResource(R.string.admin_casual_matches_filter_live),
                    selected = selectedFilter == "Live",
                    modifier = Modifier.weight(1f),
                    onClick = { onFilterChange("Live") }
                )
                MatchFilterChip(
                    text = stringResource(R.string.admin_casual_matches_filter_open),
                    selected = selectedFilter == "Open",
                    modifier = Modifier.weight(1f),
                    onClick = { onFilterChange("Open") }
                )
                MatchFilterChip(
                    text = stringResource(R.string.admin_casual_matches_filter_canceled),
                    selected = selectedFilter == "Canceled",
                    modifier = Modifier.weight(1f),
                    onClick = { onFilterChange("Canceled") }
                )
            }
        }

        when {
            isLoading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = TealGreen)
                    }
                }
            }

            errorMessage.isNotBlank() -> {
                item {
                    AdminCasualMatchMessageCard(
                        text = errorMessage,
                        isError = true
                    )
                }
            }

            filteredMatches.isEmpty() -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.admin_casual_matches_no_found),
                            color = TextGray,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            else -> {
                groupedMatches.forEach { group ->
                    item {
                        Text(
                            text = "${group.key} (${group.value.size})",
                            color = TextGray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    items(group.value) { match ->
                        CasualMatchCard(
                            match = match,
                            onViewDetailsClick = onViewDetailsClick,
                            onEditClick = onEditClick,
                            onWatchLiveClick = onWatchLiveClick
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun CalendarActionCard(
    onCalendarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.Calendar,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = stringResource(R.string.admin_casual_matches_calendar_button),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.admin_casual_matches_description),
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Button(
                onClick = onCalendarClick,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = stringResource(R.string.admin_casual_matches_calendar_button).uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AdminCasualMatchSearchBox(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = AppIcons.Search,
                contentDescription = stringResource(R.string.admin_casual_matches_search_content_description),
                tint = TextGray,
                modifier = Modifier.size(18.dp)
            )
        },
        placeholder = {
            Text(
                text = stringResource(R.string.admin_casual_matches_search_placeholder),
                color = TextGray,
                fontSize = 14.sp
            )
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = CardBg,
            unfocusedContainerColor = CardBg,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = DarkBlue,
            unfocusedTextColor = DarkBlue,
            cursorColor = TealGreen
        )
    )
}

@Composable
private fun MatchFilterChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = if (selected) PrimaryBlue else CardBg,
        shape = RoundedCornerShape(12.dp),
        border = if (selected) null else BorderStroke(1.dp, InputBg),
        modifier = modifier
            .height(36.dp)
            .clickable { onClick() }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = if (selected) Color.White else PrimaryBlue,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AdminCasualMatchMessageCard(
    text: String,
    isError: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) ErrorRed.copy(alpha = 0.10f) else TealGreen.copy(alpha = 0.10f)
        )
    ) {
        Text(
            text = text,
            color = if (isError) ErrorRed else TealGreen,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun CasualMatchCard(
    match: AdminCasualMatch,
    onViewDetailsClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onWatchLiveClick: (String) -> Unit
) {
    val progress = if (match.maxPlayers > 0) {
        (match.acceptedPlayers.toFloat() / match.maxPlayers.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusBadge(status = match.status)
                    SmallMatchBadge(text = match.modalidade.uppercase())
                }

                Text(
                    text = match.sectionTitle,
                    color = TextGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = match.title,
                color = DarkBlue,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = AppIcons.Calendar,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${formatDate(match.date)} · ${formatTime(match.time)}",
                    color = TextGray,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    imageVector = Icons.Outlined.Place,
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
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = InputBg)
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.admin_casual_matches_players_joined).uppercase(),
                        color = DarkBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${match.acceptedPlayers}/${match.maxPlayers}",
                        color = PrimaryBlue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(InputBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.Games,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = casualMatchProgressColor(match.status),
                trackColor = InputBg
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        if (match.isLive) {
                            onWatchLiveClick(match.id)
                        } else {
                            onViewDetailsClick(match.id)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (match.isLive) TealGreen else PrimaryBlue),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (match.isLive) TealGreen.copy(alpha = 0.10f) else CardBg,
                        contentColor = if (match.isLive) TealGreen else PrimaryBlue
                    )
                ) {
                    Text(
                        text = if (match.isLive) {
                            stringResource(R.string.admin_casual_matches_watch_live).uppercase()
                        } else {
                            stringResource(R.string.admin_casual_matches_view_details).uppercase()
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = { onEditClick(match.id) },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.admin_casual_matches_edit).uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String) {
    val background = when (status) {
        "LIVE" -> TealGreen.copy(alpha = 0.15f)
        "OPEN" -> TealGreen.copy(alpha = 0.15f)
        "CLOSED" -> WarningYellow.copy(alpha = 0.18f)
        "FULL" -> WarningYellow.copy(alpha = 0.18f)
        "CANCELED" -> ErrorRed.copy(alpha = 0.15f)
        else -> InputBg
    }

    val textColor = when (status) {
        "LIVE" -> TealGreen
        "OPEN" -> TealGreen
        "CLOSED" -> WarningYellow
        "FULL" -> WarningYellow
        "CANCELED" -> ErrorRed
        else -> TextGray
    }

    val text = when (status) {
        "LIVE" -> stringResource(R.string.admin_casual_matches_status_live_now).uppercase()
        "OPEN" -> stringResource(R.string.admin_casual_matches_status_open).uppercase()
        "CLOSED" -> stringResource(R.string.admin_casual_matches_status_closed).uppercase()
        "CANCELED" -> stringResource(R.string.admin_casual_matches_status_canceled).uppercase()
        else -> status.uppercase()
    }

    Surface(
        color = background,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun SmallMatchBadge(text: String) {
    Surface(
        color = InputBg,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            color = TextGray,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

private fun casualMatchProgressColor(status: String): Color {
    return when (status) {
        "LIVE" -> TealGreen
        "FULL", "CLOSED" -> WarningYellow
        "CANCELED" -> ErrorRed
        else -> PrimaryBlue
    }
}

private fun formatDate(value: String): String {
    return try {
        val date = java.time.LocalDate.parse(value.take(10))
        date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    } catch (e: Exception) {
        value
    }
}

private fun formatTime(value: String): String {
    return value.take(5)
}
