package com.example.trabalhocm.ui.screens.admin

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import androidx.compose.foundation.BorderStroke

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

    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = ""

        repository.listarPeladinhasAdmin()
            .onSuccess {
                matches = it
            }
            .onFailure {
                errorMessage = "Error loading matches: ${it.message}"
            }

        isLoading = false
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            AdminCasualMatchesTopBar(
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick
            )
        },
        bottomBar = {
            AdminCasualMatchesBottomBar(
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
                AdminCasualMatchesContent(
                    matches = matches,
                    selectedFilter = selectedFilter,
                    searchText = searchText,
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
    }
}

@Composable
private fun AdminCasualMatchesContent(
    matches: List<AdminCasualMatch>,
    selectedFilter: String,
    searchText: String,
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
                text = "ADMIN CONSOLE",
                color = BrandGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Casual Matches",
                color = BrandBlue,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Monitor every match across the platform in real time.",
                color = TextGray,
                fontSize = 12.sp
            )
        }

        item {
            Button(
                onClick = onCalendarClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(7.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = BrandWhite
                )
            ) {
                Icon(
                    imageVector = AppIcons.Calendar,
                    contentDescription = "Calendar",
                    tint = BrandWhite,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "MATCHES CALENDAR",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                singleLine = true,
                shape = RoundedCornerShape(9.dp),
                leadingIcon = {
                    Icon(
                        imageVector = AppIcons.Search,
                        contentDescription = "Search",
                        tint = TextGray,
                        modifier = Modifier.size(18.dp)
                    )
                },
                placeholder = {
                    Text(
                        text = "Search matches...",
                        color = TextGray,
                        fontSize = 12.sp
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = BrandWhite,
                    unfocusedContainerColor = BrandWhite,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("All", "Live", "Open", "Canceled").forEach { filter ->
                    MatchFilterChip(
                        text = filter,
                        selected = selectedFilter == filter,
                        onClick = {
                            onFilterChange(filter)
                        }
                    )
                }
            }
        }

        if (filteredMatches.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(9.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "No casual matches found.",
                        color = TextGray,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            }
        } else {
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
}

@Composable
private fun MatchFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(
                color = if (selected) PrimaryBlue else Color(0xFFE9EEF8),
                shape = RoundedCornerShape(6.dp)
            )
            .clickable {
                onClick()
            }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) BrandWhite else PrimaryBlue,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
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
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = match.status)

                Spacer(modifier = Modifier.width(8.dp))

                SmallMatchBadge(text = match.modalidade.uppercase())
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = match.title,
                color = BrandBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${formatDate(match.date)} · ${formatTime(match.time)} · ${match.local}",
                color = TextGray,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "PLAYERS JOINED",
                    color = TextGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                Text(
                    text = "${match.acceptedPlayers}/${match.maxPlayers}",
                    color = BrandBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(
                        color = Color(0xFFE5E7EB),
                        shape = RoundedCornerShape(20.dp)
                    )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress)
                        .height(5.dp)
                        .background(
                            color = if (match.status == "FULL") Color(0xFFEAB308) else PrimaryBlue,
                            shape = RoundedCornerShape(20.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(13.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        if (match.isLive) {
                            onWatchLiveClick(match.id)
                        } else {
                            onViewDetailsClick(match.id)
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(5.dp),
                    border = if (match.isLive) {
                        null
                    } else {
                        BorderStroke(1.dp, PrimaryBlue)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (match.isLive) BrandGreen else BrandWhite,
                        contentColor = if (match.isLive) BrandWhite else PrimaryBlue
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = if (match.isLive) "WATCH LIVE" else "VIEW DETAILS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = {
                        onEditClick(match.id)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryBlue,
                        contentColor = BrandWhite
                    )
                ) {
                    Text(
                        text = "EDIT",
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
        "LIVE" -> Color(0xFFEAF8F5)
        "OPEN" -> Color(0xFFEAF8F5)
        "CLOSED" -> Color(0xFFFEF3C7)
        "CANCELED" -> Color(0xFFFEE2E2)
        else -> Color(0xFFE5E7EB)
    }

    val textColor = when (status) {
        "LIVE" -> BrandGreen
        "OPEN" -> BrandGreen
        "CLOSED" -> Color(0xFFEAB308)
        "CANCELED" -> ErrorRed
        else -> TextGray
    }

    val text = when (status) {
        "LIVE" -> "LIVE NOW"
        "OPEN" -> "OPEN"
        "CLOSED" -> "CLOSED"
        "CANCELED" -> "CANCELED"
        else -> status
    }

    Box(
        modifier = Modifier
            .background(
                color = background,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SmallMatchBadge(text: String) {
    Box(
        modifier = Modifier
            .background(
                color = Color(0xFFE9EEF8),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = TextGray,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
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

@Composable
private fun AdminCasualMatchesTopBar(
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
                contentDescription = "Voltar",
                tint = BrandWhite,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Casual Matches",
                color = BrandWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Icon(
            imageVector = AppIcons.Notifications,
            contentDescription = "Notificações",
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
private fun AdminCasualMatchesBottomBar(
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
        BottomCasualMatchItem(AppIcons.Home, "HOME", selected == "home", onHomeClick)
        BottomCasualMatchItem(AppIcons.Tournaments, "TOURNAMENTS", selected == "tournaments", onTournamentsClick)
        BottomCasualMatchItem(AppIcons.Games, "MATCHES", selected == "matches", onMatchesClick)
        BottomCasualMatchItem(AppIcons.Teams, "TEAMS", selected == "teams", onTeamsClick)
        BottomCasualMatchItem(AppIcons.Profile, "PROFILE", selected == "profile", onProfileClick)
    }
}

@Composable
private fun BottomCasualMatchItem(
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