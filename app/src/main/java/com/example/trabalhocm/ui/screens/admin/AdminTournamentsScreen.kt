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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTournamentsScreen(
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onArchiveClick: () -> Unit = {},
    onTournamentDetailsClick: (String) -> Unit = {},
    onManageRegistrationClick: (String) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { AdminTournamentRepository() }
    val scope = rememberCoroutineScope()

    var tournaments by remember { mutableStateOf<List<AdminTournament>>(emptyList()) }
    var searchText by remember { mutableStateOf("") }
    var selectedChip by remember { mutableStateOf("All") }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var actionMessage by remember { mutableStateOf("") }
    var tournamentToDelete by remember { mutableStateOf<AdminTournament?>(null) }
    var actionMessageIsError by remember { mutableStateOf(false) }

    val errorLoadingTournamentsText = stringResource(R.string.admin_tournaments_error_loading)
    val tournamentDeletedSuccessText = stringResource(R.string.admin_tournaments_delete_success)
    val deleteTournamentErrorText = stringResource(R.string.admin_tournaments_delete_error)

    fun carregarTorneios() {
        scope.launch {
            isLoading = true
            errorMessage = ""

            repository.listarTorneiosAdmin()
                .onSuccess {
                    tournaments = it
                }
                .onFailure {
                    errorMessage = "$errorLoadingTournamentsText: ${it.message}"
                }

            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        carregarTorneios()
    }

    val filteredTournaments = tournaments.filter { tournament ->
        val matchesSearch =
            tournament.nome.contains(searchText, ignoreCase = true) ||
                    tournament.modalidade.contains(searchText, ignoreCase = true) ||
                    tournament.organizerName.contains(searchText, ignoreCase = true)

        val matchesChip = when (selectedChip) {
            "Live" -> tournament.estado.equals("em_decorrer", ignoreCase = true) ||
                    tournament.estado.equals("live", ignoreCase = true)

            "Open" -> tournament.estado.equals("aberto", ignoreCase = true) ||
                    tournament.estado.equals("open", ignoreCase = true)

            "Completed" -> tournament.estado.equals("terminado", ignoreCase = true) ||
                    tournament.estado.equals("completed", ignoreCase = true) ||
                    tournament.estado.equals("archived", ignoreCase = true)

            else -> true
        }

        matchesSearch && matchesChip
    }

    val selectedTournamentToDelete = tournamentToDelete

    if (selectedTournamentToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                tournamentToDelete = null
            },
            title = {
                Text(text = stringResource(R.string.admin_tournaments_delete_title))
            },
            text = {
                Text(
                    text = stringResource(
                        R.string.admin_tournaments_delete_message,
                        selectedTournamentToDelete.nome
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            repository.apagarTorneio(selectedTournamentToDelete.id)
                                .onSuccess {
                                    actionMessage = tournamentDeletedSuccessText
                                    actionMessageIsError = false
                                    tournamentToDelete = null
                                    carregarTorneios()
                                }
                                .onFailure {
                                    actionMessage = "$deleteTournamentErrorText: ${it.message}"
                                    actionMessageIsError = true
                                    tournamentToDelete = null
                                }
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.admin_tournaments_delete_button),
                        color = ErrorRed
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        tournamentToDelete = null
                    }
                ) {
                    Text(text = stringResource(R.string.admin_common_cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.admin_tournaments_title),
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
                    text = stringResource(R.string.admin_tournaments_all_title),
                    color = DarkBlue,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 32.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.admin_tournaments_description),
                    color = TextGray,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onArchiveClick,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, InputBg),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = CardBg),
                        modifier = Modifier
                            .weight(1.15f)
                            .height(58.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.admin_tournaments_archive_button),
                            color = DarkBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }

                    Button(
                        onClick = {
                            carregarTorneios()
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier
                            .weight(1f)
                            .height(58.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.desc_refresh),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            item {
                AdminTournamentSearchBox(
                    value = searchText,
                    onValueChange = {
                        searchText = it
                    }
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TournamentChip(
                        text = stringResource(R.string.admin_tournaments_filter_all),
                        selected = selectedChip == "All",
                        modifier = Modifier.weight(1f)
                    ) {
                        selectedChip = "All"
                    }

                    TournamentChip(
                        text = stringResource(R.string.admin_tournaments_filter_live),
                        selected = selectedChip == "Live",
                        modifier = Modifier.weight(1f)
                    ) {
                        selectedChip = "Live"
                    }

                    TournamentChip(
                        text = stringResource(R.string.admin_tournaments_filter_open),
                        selected = selectedChip == "Open",
                        modifier = Modifier.weight(1f)
                    ) {
                        selectedChip = "Open"
                    }

                    TournamentChip(
                        text = stringResource(R.string.admin_tournaments_filter_completed),
                        selected = selectedChip == "Completed",
                        modifier = Modifier.weight(1f)
                    ) {
                        selectedChip = "Completed"
                    }
                }
            }

            if (errorMessage.isNotBlank()) {
                item {
                    AdminMessageCard(
                        text = errorMessage,
                        isError = true
                    )
                }
            }

            if (actionMessage.isNotBlank()) {
                item {
                    AdminMessageCard(
                        text = actionMessage,
                        isError = actionMessageIsError
                    )
                }
            }

            if (isLoading) {
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
            } else if (filteredTournaments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.admin_tournaments_no_found),
                            color = TextGray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(filteredTournaments) { tournament ->
                    AdminTournamentMainCard(
                        tournament = tournament,
                        onDetailsClick = {
                            onTournamentDetailsClick(tournament.id)
                        },
                        onManageRegistrationClick = {
                            onManageRegistrationClick(tournament.id)
                        },
                        onDeleteTournamentClick = {
                            tournamentToDelete = tournament
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun AdminTournamentSearchBox(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = stringResource(R.string.admin_tournaments_search_placeholder),
                color = TextGray,
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Text(
                text = "⌕",
                color = TextGray,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp)),
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
private fun TournamentChip(
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
            .clickable {
                onClick()
            }
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = if (selected) Color.White else TextGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AdminMessageCard(
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
private fun AdminTournamentMainCard(
    tournament: AdminTournament,
    onDetailsClick: () -> Unit,
    onManageRegistrationClick: () -> Unit,
    onDeleteTournamentClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val statusColor = tournamentStatusColor(tournament.estado)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TourneyBadge(
                        text = "• ${statusText(tournament.estado)}",
                        textColor = statusColor,
                        bgColor = statusColor.copy(alpha = 0.1f)
                    )
                    TourneyBadge(
                        text = stringResource(R.string.admin_tournaments_pro_league).uppercase(),
                        textColor = PrimaryBlue,
                        bgColor = PrimaryBlue.copy(alpha = 0.1f)
                    )
                    TourneyBadge(
                        text = tournament.modalidade.uppercase(),
                        textColor = TextGray,
                        bgColor = InputBg
                    )
                }

                Box {
                    Text(
                        text = "⋮",
                        color = DarkBlue,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            menuExpanded = true
                        }
                    )

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = {
                            menuExpanded = false
                        },
                        modifier = Modifier.background(Color.White)
                    ) {
                        DropdownMenuItem(
                            leadingIcon = {
                                Icon(
                                    imageVector = AppIcons.Delete,
                                    contentDescription = null,
                                    tint = ErrorRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            text = {
                                Text(
                                    text = stringResource(R.string.admin_tournaments_delete_title),
                                    color = ErrorRed,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onDeleteTournamentClick()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.admin_tournaments_console),
                color = TealGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = tournament.nome,
                color = DarkBlue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(
                    R.string.admin_tournaments_season_organizer,
                    tournament.season,
                    tournament.organizerName
                ),
                color = TextGray,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.admin_tournaments_teams).uppercase(),
                        color = DarkBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = tournament.teamsCount.toString(),
                        color = DarkBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(R.string.admin_tournaments_capacity).uppercase(),
                        color = DarkBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${tournament.teamsCount}/${tournament.maxTeams}",
                        color = PrimaryBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = {
                    tournamentOccupancyProgress(
                        teamsCount = tournament.teamsCount,
                        maxTeams = tournament.maxTeams
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = tournamentOccupancyColor(
                    teamsCount = tournament.teamsCount,
                    maxTeams = tournament.maxTeams
                ),
                trackColor = InputBg
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onDetailsClick,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, PrimaryBlue),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                ) {
                    Text(
                        text = stringResource(R.string.admin_tournaments_details),
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }

                Button(
                    onClick = onManageRegistrationClick,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    modifier = Modifier
                        .weight(1.25f)
                        .height(40.dp)
                ) {
                    Text(
                        text = stringResource(R.string.admin_tournaments_manage_registration),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TourneyBadge(
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
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun statusText(status: String): String {
    val normalized = status.lowercase()

    return when {
        normalized.contains("aberto") || normalized.contains("open") ->
            stringResource(R.string.admin_status_open).uppercase()

        normalized.contains("decorrer") || normalized.contains("live") ->
            stringResource(R.string.admin_status_live).uppercase()

        normalized.contains("terminado") || normalized.contains("completed") || normalized.contains("archived") ->
            stringResource(R.string.admin_status_completed).uppercase()

        normalized.contains("cancelado") ->
            stringResource(R.string.admin_status_cancelled).uppercase()

        normalized.contains("rascunho") ->
            stringResource(R.string.admin_status_draft).uppercase()

        else -> status.uppercase()
    }
}

private fun tournamentStatusColor(status: String): Color {
    val normalized = status.lowercase()

    return when {
        normalized.contains("aberto") || normalized.contains("open") -> TealGreen
        normalized.contains("decorrer") || normalized.contains("live") -> ErrorRed
        normalized.contains("terminado") || normalized.contains("completed") || normalized.contains("archived") -> TextGray
        normalized.contains("cancelado") -> ErrorRed
        normalized.contains("rascunho") -> WarningYellow
        else -> PrimaryBlue
    }
}

private fun tournamentOccupancyProgress(
    teamsCount: Int,
    maxTeams: Int
): Float {
    if (maxTeams <= 0) {
        return 0f
    }

    return (teamsCount.toFloat() / maxTeams.toFloat())
        .coerceIn(0f, 1f)
}

private fun tournamentOccupancyColor(
    teamsCount: Int,
    maxTeams: Int
): Color {
    val progress = tournamentOccupancyProgress(
        teamsCount = teamsCount,
        maxTeams = maxTeams
    )

    return when {
        progress < 0.5f -> TealGreen
        progress < 0.8f -> WarningYellow
        else -> ErrorRed
    }
}

@Preview(showBackground = true)
@Composable
fun AdminTournamentsScreenPreview() {
    MaterialTheme {
        AdminTournamentsScreen()
    }
}
