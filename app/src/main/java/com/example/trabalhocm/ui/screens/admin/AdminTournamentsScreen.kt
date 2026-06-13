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
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.model.AdminTournament
import com.example.trabalhocm.data.repository.AdminTournamentRepository
import com.example.trabalhocm.ui.theme.AppIcons
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.CardBg
import com.example.trabalhocm.ui.theme.LightBlueBadge
import com.example.trabalhocm.ui.theme.TextGray
import androidx.compose.material3.LinearProgressIndicator
import com.example.trabalhocm.ui.theme.WarningYellow
import com.example.trabalhocm.ui.theme.ErrorRed
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import com.example.trabalhocm.R

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

    val errorLoadingTournamentsText = stringResource(R.string.admin_tournaments_error_loading)
    val tournamentDeletedSuccessText = stringResource(R.string.admin_tournaments_delete_success)
    val deleteTournamentErrorText = stringResource(R.string.admin_tournaments_delete_error)

    var actionMessageIsError by remember { mutableStateOf(false) }

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
                        color = Color(0xFFDC2626)
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
        containerColor = BgLight,
        topBar = {
            AdminTournamentsTopBar(
                title = stringResource(R.string.admin_tournaments_title),
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick
            )
        },
        bottomBar = {
            AdminTournamentsBottomBar(
                selected = "tournaments",
                onHomeClick = onHomeClick,
                onTournamentsClick = onTournamentsClick,
                onMatchesClick = onMatchesClick,
                onTeamsClick = onTeamsClick,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BrandGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 18.dp,
                    bottom = 28.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Column {
                        Text(
                            text = stringResource(R.string.admin_tournaments_console).uppercase(),
                            color = BrandGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = stringResource(R.string.admin_tournaments_all_title),
                            color = BrandBlue,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = stringResource(R.string.admin_tournaments_description),
                            color = TextGray,
                            fontSize = 13.sp
                        )
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TournamentChip(stringResource(R.string.admin_tournaments_filter_all), selectedChip == "All") {
                            selectedChip = "All"
                        }

                        TournamentChip(stringResource(R.string.admin_tournaments_filter_live), selectedChip == "Live") {
                            selectedChip = "Live"
                        }

                        TournamentChip(stringResource(R.string.admin_tournaments_filter_open), selectedChip == "Open") {
                            selectedChip = "Open"
                        }

                        TournamentChip(stringResource(R.string.admin_tournaments_filter_completed), selectedChip == "Completed") {
                            selectedChip = "Completed"
                        }
                    }
                }

                if (errorMessage.isNotBlank()) {
                    item {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (actionMessage.isNotBlank()) {
                    item {
                        Text(
                            text = actionMessage,
                            color = if (actionMessageIsError) {
                                Color(0xFFDC2626)
                            } else {
                                BrandGreen
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (filteredTournaments.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBg),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.admin_tournaments_no_found),
                                color = TextGray,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(18.dp)
                            )
                        }
                    }
                }

                items(filteredTournaments.size) { index ->
                    AdminTournamentMainCard(
                        tournament = filteredTournaments[index],
                        onDetailsClick = {
                            onTournamentDetailsClick(filteredTournaments[index].id)
                        },
                        onManageRegistrationClick = {
                            onManageRegistrationClick(filteredTournaments[index].id)
                        },
                        onDeleteTournamentClick = {
                            tournamentToDelete = filteredTournaments[index]
                        }
                    )
                }

                item {
                    Button(
                        onClick = onArchiveClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0057C8),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.admin_tournaments_archive_button).uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.7.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminTournamentsTopBar(
    title: String,
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
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Icon(
            imageVector = AppIcons.Notifications,
            contentDescription = stringResource(R.string.admin_common_notifications),
            tint = Color.White,
            modifier = Modifier
                .size(23.dp)
                .clickable {
                    onNotificationsClick()
                }
        )
    }
}

@Composable
private fun AdminTournamentSearchBox(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = stringResource(R.string.admin_tournaments_search_placeholder),
                color = TextGray,
                fontSize = 13.sp
            )
        },
        leadingIcon = {
            Text(
                text = "⌕",
                color = TextGray,
                fontSize = 18.sp
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = BrandBlue,
            unfocusedTextColor = BrandBlue,
            cursorColor = BrandGreen
        )
    )
}

@Composable
private fun TournamentChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (selected) Color(0xFF0057C8) else Color(0xFFE8EEF9))
            .clickable {
                onClick()
            }
            .padding(horizontal = 13.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color(0xFF0057C8),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
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
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    StatusBadge(status = tournament.estado)

                    SmallBadge(
                        text = stringResource(R.string.admin_tournaments_pro_league).uppercase(),
                        background = Color(0xFFEAF2F5),
                        textColor = TextGray
                    )

                    SmallBadge(
                        text = tournament.modalidade,
                        background = LightBlueBadge,
                        textColor = TextGray
                    )
                }

                Box {
                    Text(
                        text = "⋮",
                        color = BrandBlue,
                        fontSize = 20.sp,
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
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            text = {
                                Text(
                                    text = stringResource(R.string.admin_tournaments_delete_title),
                                    color = Color(0xFFDC2626),
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

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = tournament.nome,
                color = BrandBlue,
                fontSize = 15.sp,
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
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.admin_tournaments_teams).uppercase(),
                        color = TextGray,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.7.sp
                    )

                    Text(
                        text = tournament.teamsCount.toString(),
                        color = BrandBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${tournament.teamsCount}/${tournament.maxTeams}",
                    color = BrandBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(7.dp))

            LinearProgressIndicator(
                progress = {
                    tournamentOccupancyProgress(
                        teamsCount = tournament.teamsCount,
                        maxTeams = tournament.maxTeams
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(20.dp)),
                color = tournamentOccupancyColor(
                    teamsCount = tournament.teamsCount,
                    maxTeams = tournament.maxTeams
                ),
                trackColor = Color(0xFFE5E7EB)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onDetailsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp),
                    shape = RoundedCornerShape(3.dp),
                    border = BorderStroke(1.dp, Color(0xFF0057C8)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF0057C8)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.admin_tournaments_details).uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onManageRegistrationClick,
                    modifier = Modifier
                        .weight(1.4f)
                        .height(38.dp),
                    shape = RoundedCornerShape(3.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0057C8),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.admin_tournaments_manage_registration).uppercase(),
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
    val normalized = status.lowercase()

    val background = when {
        normalized.contains("aberto") || normalized.contains("open") -> Color(0xFFEAF8F5)
        normalized.contains("decorrer") || normalized.contains("live") -> Color(0xFFFEE2E2)
        normalized.contains("terminado") || normalized.contains("completed") || normalized.contains("archived") -> Color(0xFFEAF2F5)
        normalized.contains("cancelado") -> Color(0xFFFEE2E2)
        else -> Color(0xFFEAF3FF)
    }

    val textColor = when {
        normalized.contains("aberto") || normalized.contains("open") -> BrandGreen
        normalized.contains("decorrer") || normalized.contains("live") -> Color(0xFFDC2626)
        normalized.contains("terminado") || normalized.contains("completed") || normalized.contains("archived") -> TextGray
        normalized.contains("cancelado") -> Color(0xFFDC2626)
        else -> Color(0xFF0057C8)
    }

    val text = when {
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

    SmallBadge(
        text = text,
        background = background,
        textColor = textColor
    )
}

@Composable
private fun SmallBadge(
    text: String,
    background: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .padding(horizontal = 9.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.4.sp
        )
    }
}

@Composable
private fun AdminTournamentsBottomBar(
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
            .background(Color.White)
            .navigationBarsPadding()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomItem(AppIcons.Home, stringResource(R.string.admin_nav_home).uppercase(), selected == "home", onHomeClick)
        BottomItem(AppIcons.Tournaments, stringResource(R.string.admin_nav_tournaments).uppercase(), selected == "tournaments", onTournamentsClick)
        BottomItem(AppIcons.Games, stringResource(R.string.admin_nav_matches).uppercase(), selected == "matches", onMatchesClick)
        BottomItem(AppIcons.Teams, stringResource(R.string.admin_nav_teams).uppercase(), selected == "teams", onTeamsClick)
        BottomItem(AppIcons.Profile, stringResource(R.string.admin_nav_profile).uppercase(), selected == "profile", onProfileClick)
    }
}

@Composable
private fun BottomItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) Color(0xFF0057C8) else Color(0xFF9AA5B5)

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
        progress < 0.5f -> BrandGreen
        progress < 0.8f -> WarningYellow
        else -> ErrorRed
    }
}

@Preview(showBackground = true)
@Composable
fun AdminTournamentsScreenPreview() {
    AdminTournamentsScreen()
}