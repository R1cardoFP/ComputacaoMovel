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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.model.AdminTeam
import com.example.trabalhocm.data.repository.AdminTeamRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.AppIcons
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.CardBg
import com.example.trabalhocm.ui.theme.DarkBlue
import com.example.trabalhocm.ui.theme.ErrorRed
import com.example.trabalhocm.ui.theme.InputBg
import com.example.trabalhocm.ui.theme.LightBlueBadge
import com.example.trabalhocm.ui.theme.PrimaryBlue
import com.example.trabalhocm.ui.theme.TealGreen
import com.example.trabalhocm.ui.theme.TextGray
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTeamsScreen(
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onViewDetailsClick: (String) -> Unit = {},
    onManageTeamClick: (String) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { AdminTeamRepository() }
    val scope = rememberCoroutineScope()

    var teams by remember { mutableStateOf<List<AdminTeam>>(emptyList()) }
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All Teams") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var actionMessage by remember { mutableStateOf("") }
    var actionMessageIsError by remember { mutableStateOf(false) }
    var teamToDelete by remember { mutableStateOf<AdminTeam?>(null) }

    val errorLoadingTeamsText = stringResource(R.string.admin_teams_error_loading)
    val teamDeletedSuccessText = stringResource(R.string.admin_teams_delete_success)
    val deleteTeamErrorText = stringResource(R.string.admin_teams_delete_error)

    fun carregarEquipas() {
        scope.launch {
            isLoading = true
            errorMessage = ""

            repository.listarEquipasAdmin()
                .onSuccess {
                    teams = it
                }
                .onFailure {
                    errorMessage = "$errorLoadingTeamsText: ${it.message}"
                }

            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        carregarEquipas()
    }

    val filteredTeams = teams.filter { team ->
        val matchesSearch =
            team.nome.contains(searchText, ignoreCase = true) ||
                    team.modalidade.contains(searchText, ignoreCase = true) ||
                    team.divisao.contains(searchText, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Futebol" -> team.modalidade.contains("futebol", ignoreCase = true) ||
                    team.modalidade.contains("football", ignoreCase = true)

            "Basquetebol" -> team.modalidade.contains("basquetebol", ignoreCase = true) ||
                    team.modalidade.contains("basket", ignoreCase = true)

            "Voleibol" -> team.modalidade.contains("voleibol", ignoreCase = true) ||
                    team.modalidade.contains("volley", ignoreCase = true)

            else -> true
        }

        matchesSearch && matchesFilter
    }

    val selectedTeamToDelete = teamToDelete

    if (selectedTeamToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                teamToDelete = null
            },
            title = {
                Text(
                    text = stringResource(R.string.admin_teams_delete_title),
                    color = DarkBlue,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(
                        R.string.admin_teams_delete_message,
                        selectedTeamToDelete.nome
                    ),
                    color = TextGray,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            repository.apagarEquipa(selectedTeamToDelete.id)
                                .onSuccess {
                                    actionMessage = teamDeletedSuccessText
                                    actionMessageIsError = false
                                    teamToDelete = null
                                    carregarEquipas()
                                }
                                .onFailure {
                                    actionMessage = "$deleteTeamErrorText: ${it.message}"
                                    actionMessageIsError = true
                                    teamToDelete = null
                                }
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.admin_teams_delete_button),
                        color = ErrorRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        teamToDelete = null
                    }
                ) {
                    Text(
                        text = stringResource(R.string.admin_common_cancel),
                        color = PrimaryBlue
                    )
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.admin_teams_title),
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
                selectedTab = "TEAMS",
                onHomeClick = onHomeClick,
                onTournamentsClick = onTournamentsClick,
                onMatchesClick = onMatchesClick,
                onTeamsClick = onTeamsClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = BgLight
    ) { innerPadding ->
        AdminTeamsContent(
            teams = filteredTeams,
            totalTeams = teams.size,
            selectedFilter = selectedFilter,
            searchText = searchText,
            isLoading = isLoading,
            errorMessage = errorMessage,
            actionMessage = actionMessage,
            actionMessageIsError = actionMessageIsError,
            innerPadding = innerPadding,
            onSearchChange = { searchText = it },
            onFilterChange = { selectedFilter = it },
            onViewDetailsClick = onViewDetailsClick,
            onManageTeamClick = onManageTeamClick,
            onDeleteTeamClick = { team -> teamToDelete = team }
        )
    }
}

@Composable
private fun AdminTeamsContent(
    teams: List<AdminTeam>,
    totalTeams: Int,
    selectedFilter: String,
    searchText: String,
    isLoading: Boolean,
    errorMessage: String,
    actionMessage: String,
    actionMessageIsError: Boolean,
    innerPadding: PaddingValues,
    onSearchChange: (String) -> Unit,
    onFilterChange: (String) -> Unit,
    onViewDetailsClick: (String) -> Unit,
    onManageTeamClick: (String) -> Unit,
    onDeleteTeamClick: (AdminTeam) -> Unit
) {
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = TealGreen)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(
            start = 24.dp,
            end = 24.dp,
            top = 24.dp,
            bottom = 28.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AdminTeamsHeader(totalTeams = totalTeams)
        }

        item {
            AdminTeamsSearchBox(
                value = searchText,
                onValueChange = onSearchChange
            )
        }

        item {
            AdminTeamsFilters(
                selectedFilter = selectedFilter,
                onFilterChange = onFilterChange
            )
        }

        if (errorMessage.isNotBlank()) {
            item {
                AdminTeamsMessage(
                    message = errorMessage,
                    isError = true
                )
            }
        }

        if (actionMessage.isNotBlank()) {
            item {
                AdminTeamsMessage(
                    message = actionMessage,
                    isError = actionMessageIsError
                )
            }
        }

        if (teams.isEmpty()) {
            item {
                AdminTeamsEmptyCard()
            }
        }

        items(teams.size) { index ->
            val team = teams[index]

            AdminTeamCard(
                team = team,
                index = index,
                onViewDetailsClick = {
                    onViewDetailsClick(team.id)
                },
                onManageTeamClick = {
                    onManageTeamClick(team.id)
                },
                onDeleteTeamClick = {
                    onDeleteTeamClick(team)
                }
            )
        }
    }
}

@Composable
private fun AdminTeamsHeader(totalTeams: Int) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.admin_teams_console).uppercase(),
                color = PrimaryBlue,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.admin_teams_all_title),
                color = DarkBlue,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.admin_teams_description),
                color = TextGray,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = DarkBlue),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.admin_nav_teams).uppercase(),
                        color = TealGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = totalTeams.toString(),
                        color = Color.White,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = stringResource(R.string.admin_teams_all_title),
                        color = Color.White.copy(alpha = 0.78f),
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.Teams,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminTeamsSearchBox(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.admin_teams_search_content_description).uppercase(),
            color = TextGray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = stringResource(R.string.admin_teams_search_placeholder),
                    color = TextGray,
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = AppIcons.Search,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(19.dp)
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
                focusedContainerColor = InputBg,
                unfocusedContainerColor = InputBg,
                disabledContainerColor = InputBg,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = DarkBlue,
                unfocusedTextColor = DarkBlue,
                cursorColor = TealGreen
            )
        )
    }
}

@Composable
private fun AdminTeamsFilters(
    selectedFilter: String,
    onFilterChange: (String) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.admin_teams_filter_all).uppercase(),
            color = TextGray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdminTeamFilterChip(
                text = stringResource(R.string.admin_teams_filter_all),
                selected = selectedFilter == "All Teams",
                modifier = Modifier.weight(1f)
            ) {
                onFilterChange("All Teams")
            }

            AdminTeamFilterChip(
                text = stringResource(R.string.admin_teams_filter_football),
                selected = selectedFilter == "Futebol",
                modifier = Modifier.weight(1f)
            ) {
                onFilterChange("Futebol")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AdminTeamFilterChip(
                text = stringResource(R.string.admin_teams_filter_basketball),
                selected = selectedFilter == "Basquetebol",
                modifier = Modifier.weight(1f)
            ) {
                onFilterChange("Basquetebol")
            }

            AdminTeamFilterChip(
                text = stringResource(R.string.admin_teams_filter_volleyball),
                selected = selectedFilter == "Voleibol",
                modifier = Modifier.weight(1f)
            ) {
                onFilterChange("Voleibol")
            }
        }
    }
}

@Composable
private fun AdminTeamFilterChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        color = if (selected) PrimaryBlue else Color.White,
        border = BorderStroke(
            width = 1.dp,
            color = if (selected) PrimaryBlue else Color(0xFFE2E8F0)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = if (selected) Color.White else PrimaryBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AdminTeamsMessage(
    message: String,
    isError: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) ErrorRed.copy(alpha = 0.08f) else TealGreen.copy(alpha = 0.10f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = message,
            color = if (isError) ErrorRed else TealGreen,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(14.dp)
        )
    }
}

@Composable
private fun AdminTeamsEmptyCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = AppIcons.Teams,
                contentDescription = null,
                tint = TextGray,
                modifier = Modifier.size(34.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.admin_teams_no_found),
                color = TextGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun AdminTeamCard(
    team: AdminTeam,
    index: Int,
    onViewDetailsClick: () -> Unit,
    onManageTeamClick: () -> Unit,
    onDeleteTeamClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    TeamAvatar(
                        name = team.nome,
                        index = index
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = team.nome,
                            color = DarkBlue,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SmallTeamBadge(text = team.modalidade.uppercase())

                            if (team.divisao.isNotBlank()) {
                                SmallTeamBadge(text = team.divisao.uppercase())
                            }
                        }
                    }
                }

                Box {
                    IconButton(
                        onClick = {
                            menuExpanded = true
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = AppIcons.MoreVert,
                            contentDescription = stringResource(R.string.admin_teams_options_content_description),
                            tint = TextGray,
                            modifier = Modifier.size(22.dp)
                        )
                    }

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
                                    text = stringResource(R.string.admin_teams_delete_title),
                                    color = ErrorRed,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onDeleteTeamClick()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HorizontalDivider(color = Color(0xFFE2E8F0))

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TeamStat(
                    label = stringResource(R.string.admin_teams_players).uppercase(),
                    value = team.playersCount.toString(),
                    color = PrimaryBlue,
                    modifier = Modifier.weight(1f)
                )

                TeamStat(
                    label = stringResource(R.string.admin_teams_wins).uppercase(),
                    value = team.wins.toString(),
                    color = TealGreen,
                    modifier = Modifier.weight(1f)
                )

                TeamStat(
                    label = stringResource(R.string.admin_teams_losses).uppercase(),
                    value = team.losses.toString(),
                    color = ErrorRed,
                    modifier = Modifier.weight(1f)
                )

                TeamStat(
                    label = stringResource(R.string.admin_teams_streak).uppercase(),
                    value = team.streak.uppercase(),
                    color = if (team.streak.startsWith("L", ignoreCase = true)) {
                        ErrorRed
                    } else {
                        TealGreen
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onViewDetailsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, PrimaryBlue),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = PrimaryBlue
                    )
                ) {
                    Text(
                        text = stringResource(R.string.admin_teams_view_details).uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Button(
                    onClick = onManageTeamClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealGreen,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.admin_teams_manage_team).uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun TeamAvatar(
    name: String,
    index: Int
) {
    val color = when (index % 5) {
        0 -> TealGreen
        1 -> PrimaryBlue
        2 -> Color(0xFFEAB308)
        3 -> DarkBlue
        else -> Color(0xFF7C3AED)
    }

    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials(name),
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold
        )
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

@Composable
private fun SmallTeamBadge(
    text: String
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(LightBlueBadge)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = PrimaryBlue,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TeamStat(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = TextGray,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            color = color,
            fontSize = 19.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminTeamsScreenPreview() {
    AdminTeamsScreen()
}
