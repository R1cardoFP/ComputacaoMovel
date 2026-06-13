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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.model.AdminTournament
import com.example.trabalhocm.data.repository.AdminTournamentRepository
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
fun AdminTournamentArchiveScreen(
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { AdminTournamentRepository() }

    var tournaments by remember { mutableStateOf<List<AdminTournament>>(emptyList()) }
    var searchText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    val errorLoadingText = stringResource(R.string.admin_archive_error_loading)

    LaunchedEffect(Unit) {
        repository.listarTorneiosAdmin()
            .onSuccess {
                tournaments = it
            }
            .onFailure {
                errorMessage = "$errorLoadingText: ${it.message}"
            }

        isLoading = false
    }

    val filteredTournaments = tournaments.filter { tournament ->
        tournament.nome.contains(searchText, ignoreCase = true) ||
                tournament.modalidade.contains(searchText, ignoreCase = true) ||
                tournament.organizerName.contains(searchText, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = AppIcons.Back,
                            contentDescription = stringResource(R.string.admin_common_back),
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.admin_archive_top_title),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
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
                selectedTab = "TOURNAMENTS",
                onHomeClick = onHomeClick,
                onTournamentsClick = onTournamentsClick,
                onMatchesClick = onMatchesClick,
                onTeamsClick = onTeamsClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = BgLight
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TealGreen)
            }
        } else {
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
                        text = stringResource(R.string.admin_archive_console).uppercase(),
                        color = TealGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.6.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.admin_archive_title),
                        color = DarkBlue,
                        fontSize = 28.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.admin_archive_description),
                        color = TextGray,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }

                item {
                    ArchiveSummaryCard(
                        totalTournaments = tournaments.size,
                        completedCount = tournaments.count { tournament ->
                            val status = tournament.estado.lowercase()
                            status.contains("terminado") ||
                                    status.contains("completed") ||
                                    status.contains("archived")
                        },
                        cancelledCount = tournaments.count { tournament ->
                            val status = tournament.estado.lowercase()
                            status.contains("cancelado") ||
                                    status.contains("canceled") ||
                                    status.contains("cancelled")
                        }
                    )
                }

                item {
                    ArchiveSearchBox(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                        }
                    )
                }

                if (errorMessage.isNotBlank()) {
                    item {
                        ArchiveMessageCard(
                            text = errorMessage,
                            isError = true
                        )
                    }
                }

                if (filteredTournaments.isEmpty()) {
                    item {
                        ArchiveEmptyStateCard(
                            text = stringResource(R.string.admin_archive_no_tournaments_found)
                        )
                    }
                } else {
                    items(filteredTournaments) { tournament ->
                        AdminTournamentArchiveCard(
                            tournament = tournament
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun ArchiveSummaryCard(
    totalTournaments: Int,
    completedCount: Int,
    cancelledCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.admin_archive_console).uppercase(),
                        color = TealGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.admin_archive_title),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.Tournaments,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ArchiveSummaryItem(
                    label = "TOTAL",
                    value = totalTournaments.toString(),
                    modifier = Modifier.weight(1f)
                )

                ArchiveSummaryItem(
                    label = "CONCLUÍDOS",
                    value = completedCount.toString(),
                    modifier = Modifier.weight(1f)
                )

                ArchiveSummaryItem(
                    label = "CANCELADOS",
                    value = cancelledCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ArchiveSummaryItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White.copy(alpha = 0.1f),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                color = Color.White.copy(alpha = 0.76f),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
private fun ArchiveSearchBox(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = stringResource(R.string.admin_archive_search_placeholder),
                color = TextGray,
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = AppIcons.Search,
                contentDescription = stringResource(R.string.admin_archive_search_content_description),
                tint = TextGray,
                modifier = Modifier.size(18.dp)
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
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
private fun ArchiveMessageCard(
    text: String,
    isError: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) ErrorRed.copy(alpha = 0.08f) else TealGreen.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            color = if (isError) ErrorRed else TealGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(14.dp)
        )
    }
}

@Composable
private fun ArchiveEmptyStateCard(
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, InputBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(InputBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AppIcons.Tournaments,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = text,
                color = TextGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun AdminTournamentArchiveCard(
    tournament: AdminTournament
) {
    val statusColor = archiveStatusColor(tournament.estado)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(tournamentSportColor(tournament.modalidade).copy(alpha = 0.14f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tournamentInitials(tournament.nome),
                            color = tournamentSportColor(tournament.modalidade),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tournament.nome,
                            color = DarkBlue,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            lineHeight = 21.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = tournament.organizerName,
                            color = TextGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                ArchiveBadge(
                    text = archiveStatusText(tournament.estado),
                    textColor = statusColor,
                    bgColor = statusColor.copy(alpha = 0.1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ArchiveBadge(
                    text = tournament.modalidade.uppercase(),
                    textColor = PrimaryBlue,
                    bgColor = PrimaryBlue.copy(alpha = 0.1f)
                )

                ArchiveBadge(
                    text = tournament.season.uppercase(),
                    textColor = TextGray,
                    bgColor = InputBg
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                color = BgLight,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TournamentInfo(
                        label = stringResource(R.string.admin_archive_champion).uppercase(),
                        value = tournament.champion,
                        modifier = Modifier.weight(1f)
                    )

                    TournamentInfo(
                        label = stringResource(R.string.admin_archive_prize).uppercase(),
                        value = tournament.prize,
                        modifier = Modifier.weight(1f)
                    )

                    TournamentInfo(
                        label = "JOGOS",
                        value = tournament.matchesCount.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun archiveStatusText(status: String): String {
    val normalized = status.lowercase()

    return when {
        normalized.contains("aberto") || normalized.contains("open") ->
            stringResource(R.string.admin_status_open).uppercase()

        normalized.contains("decorrer") || normalized.contains("live") ->
            stringResource(R.string.admin_status_live).uppercase()

        normalized.contains("terminado") || normalized.contains("completed") || normalized.contains("archived") ->
            stringResource(R.string.admin_status_completed).uppercase()

        normalized.contains("cancelado") || normalized.contains("canceled") || normalized.contains("cancelled") ->
            stringResource(R.string.admin_status_cancelled).uppercase()

        normalized.contains("rascunho") || normalized.contains("draft") ->
            stringResource(R.string.admin_status_draft).uppercase()

        else -> status.uppercase()
    }
}

private fun archiveStatusColor(status: String): Color {
    val normalized = status.lowercase()

    return when {
        normalized.contains("aberto") || normalized.contains("open") -> TealGreen
        normalized.contains("decorrer") || normalized.contains("live") -> ErrorRed
        normalized.contains("terminado") || normalized.contains("completed") || normalized.contains("archived") -> TextGray
        normalized.contains("cancelado") || normalized.contains("canceled") || normalized.contains("cancelled") -> ErrorRed
        normalized.contains("rascunho") || normalized.contains("draft") -> WarningYellow
        else -> PrimaryBlue
    }
}

@Composable
private fun ArchiveBadge(
    text: String,
    textColor: Color,
    bgColor: Color
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            letterSpacing = 0.4.sp
        )
    }
}

@Composable
private fun TournamentInfo(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            color = TextGray,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = value,
            color = DarkBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 15.sp
        )
    }
}

private fun tournamentSportColor(modalidade: String): Color {
    return when {
        modalidade.contains("futebol", ignoreCase = true) ||
                modalidade.contains("football", ignoreCase = true) ||
                modalidade.contains("soccer", ignoreCase = true) -> TealGreen

        modalidade.contains("basquetebol", ignoreCase = true) ||
                modalidade.contains("basketball", ignoreCase = true) ||
                modalidade.contains("basket", ignoreCase = true) -> PrimaryBlue

        modalidade.contains("voleibol", ignoreCase = true) ||
                modalidade.contains("volleyball", ignoreCase = true) ||
                modalidade.contains("volley", ignoreCase = true) -> TextGray

        else -> PrimaryBlue
    }
}

private fun tournamentInitials(name: String): String {
    val words = name
        .trim()
        .split(" ")
        .filter { it.isNotBlank() }

    return when {
        words.size >= 2 -> "${words[0].first()}${words[1].first()}".uppercase()
        words.size == 1 && words[0].length >= 2 -> words[0].take(2).uppercase()
        words.size == 1 -> words[0].take(1).uppercase()
        else -> "T"
    }
}

@Preview(showBackground = true)
@Composable
fun AdminTournamentArchiveScreenPreview() {
    MaterialTheme {
        AdminTournamentArchiveScreen()
    }
}
