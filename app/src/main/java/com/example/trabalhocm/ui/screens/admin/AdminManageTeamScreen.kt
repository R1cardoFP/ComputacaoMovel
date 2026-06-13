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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.trabalhocm.data.model.AdminManageTeam
import com.example.trabalhocm.data.model.AdminManageTeamPlayer
import com.example.trabalhocm.data.repository.AdminManageTeamRepository
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminManageTeamScreen(
    teamId: String,
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onInvitePlayerClick: (String) -> Unit = {},
    onPlayerClick: (String) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { AdminManageTeamRepository() }
    val scope = rememberCoroutineScope()

    var team by remember { mutableStateOf<AdminManageTeam?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var searchText by remember { mutableStateOf("") }
    var actionMessage by remember { mutableStateOf("") }
    var actionMessageIsError by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableStateOf(0) }

    val errorLoadingTeamText = stringResource(R.string.admin_manage_team_error_loading)
    val playerRemovedSuccessText = stringResource(R.string.admin_manage_team_remove_success)
    val removePlayerErrorText = stringResource(R.string.admin_manage_team_remove_error)

    LaunchedEffect(teamId, refreshKey) {
        isLoading = true
        errorMessage = ""

        repository.obterEquipa(teamId)
            .onSuccess {
                team = it
            }
            .onFailure {
                errorMessage = "$errorLoadingTeamText: ${it.message}"
            }

        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.admin_manage_team_title),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.admin_common_back),
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
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TealGreen)
                }
            }

            errorMessage.isNotBlank() -> {
                AdminManageTeamErrorState(
                    innerPadding = innerPadding,
                    message = errorMessage
                )
            }

            team != null -> {
                val currentTeam = team!!

                AdminManageTeamContent(
                    team = currentTeam,
                    searchText = searchText,
                    onSearchChange = { searchText = it },
                    innerPadding = innerPadding,
                    actionMessage = actionMessage,
                    actionMessageIsError = actionMessageIsError,
                    onInvitePlayerClick = onInvitePlayerClick,
                    onPlayerClick = onPlayerClick,
                    onRemovePlayerClick = { playerId ->
                        scope.launch {
                            repository.removerJogadorDaEquipa(currentTeam.id, playerId)
                                .onSuccess {
                                    actionMessage = playerRemovedSuccessText
                                    actionMessageIsError = false
                                    refreshKey++
                                }
                                .onFailure {
                                    actionMessage = "$removePlayerErrorText: ${it.message}"
                                    actionMessageIsError = true
                                }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AdminManageTeamContent(
    team: AdminManageTeam,
    searchText: String,
    onSearchChange: (String) -> Unit,
    innerPadding: PaddingValues,
    actionMessage: String,
    actionMessageIsError: Boolean,
    onInvitePlayerClick: (String) -> Unit,
    onPlayerClick: (String) -> Unit,
    onRemovePlayerClick: (String) -> Unit
) {
    val filteredPlayers = team.players.filter { player ->
        val query = searchText.trim()
        query.isBlank() ||
                player.nome.contains(query, ignoreCase = true) ||
                player.email.contains(query, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(
            start = 24.dp,
            end = 24.dp,
            top = 20.dp,
            bottom = 28.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AdminManageTeamHeader()
        }

        item {
            TeamManageHeroCard(team = team)
        }

        if (actionMessage.isNotBlank()) {
            item {
                AdminManageTeamMessageCard(
                    message = actionMessage,
                    isError = actionMessageIsError
                )
            }
        }

        item {
            Button(
                onClick = {
                    onInvitePlayerClick(team.id)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    imageVector = AppIcons.Add,
                    contentDescription = stringResource(R.string.admin_manage_team_invite_player),
                    tint = Color.White,
                    modifier = Modifier.size(19.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = stringResource(R.string.admin_manage_team_invite_player),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            AdminManageTeamSearchCard(
                searchText = searchText,
                onSearchChange = onSearchChange
            )
        }

        item {
            AdminManageTeamRosterHeader(count = filteredPlayers.size)
        }

        if (filteredPlayers.isEmpty()) {
            item {
                AdminManageTeamEmptyCard()
            }
        } else {
            items(
                items = filteredPlayers,
                key = { it.id }
            ) { player ->
                RosterPlayerCard(
                    player = player,
                    onPlayerClick = onPlayerClick,
                    onRemovePlayerClick = onRemovePlayerClick
                )
            }
        }
    }
}

@Composable
private fun AdminManageTeamHeader() {
    Column {
        Text(
            text = stringResource(R.string.admin_manage_team_console).uppercase(),
            color = TealGreen,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.6.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.admin_manage_team_title),
            color = DarkBlue,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.admin_manage_team_description),
            color = TextGray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun TeamManageHeroCard(team: AdminManageTeam) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(TealGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = team.sigla.take(3).uppercase(),
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = team.nome,
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Surface(
                        color = Color.White.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = team.modalidade,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.admin_manage_team_players).uppercase(),
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = team.playersCount.toString(),
                        color = Color.White,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    color = TealGreen.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = AppIcons.Teams,
                            contentDescription = null,
                            tint = TealGreen,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = team.sigla.uppercase(),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminManageTeamSearchCard(
    searchText: String,
    onSearchChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        TextField(
            value = searchText,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            singleLine = true,
            shape = RoundedCornerShape(18.dp),
            leadingIcon = {
                Icon(
                    imageVector = AppIcons.Search,
                    contentDescription = stringResource(R.string.admin_manage_team_search_content_description),
                    tint = TextGray,
                    modifier = Modifier.size(20.dp)
                )
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.admin_manage_team_search_placeholder),
                    color = TextGray,
                    fontSize = 14.sp
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = InputBg,
                unfocusedContainerColor = InputBg,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = PrimaryBlue
            )
        )
    }
}

@Composable
private fun AdminManageTeamRosterHeader(count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.admin_manage_team_roster_count, count),
            color = DarkBlue,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Surface(
            color = PrimaryBlue.copy(alpha = 0.12f),
            shape = RoundedCornerShape(50)
        ) {
            Text(
                text = count.toString(),
                color = PrimaryBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun RosterPlayerCard(
    player: AdminManageTeamPlayer,
    onPlayerClick: (String) -> Unit,
    onRemovePlayerClick: (String) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onPlayerClick(player.id)
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = player.initials,
                        color = PrimaryBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = player.nome,
                        color = DarkBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = player.email,
                        color = TextGray,
                        fontSize = 13.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box {
                    IconButton(
                        onClick = {
                            menuExpanded = true
                        }
                    ) {
                        Icon(
                            imageVector = AppIcons.MoreVert,
                            contentDescription = stringResource(R.string.admin_manage_team_player_options),
                            tint = TextGray,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = {
                            menuExpanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.admin_manage_team_remove_from_team),
                                    color = ErrorRed,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = AppIcons.Delete,
                                    contentDescription = stringResource(R.string.admin_manage_team_remove_content_description),
                                    tint = ErrorRed,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onRemovePlayerClick(player.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminManageTeamMessageCard(
    message: String,
    isError: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) Color(0xFFFFEBEE) else Color(0xFFEAF7F1)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(if (isError) ErrorRed.copy(alpha = 0.12f) else TealGreen.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isError) AppIcons.Cancel else AppIcons.Confirm,
                    contentDescription = null,
                    tint = if (isError) ErrorRed else TealGreen,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = message,
                color = if (isError) ErrorRed else TealGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun AdminManageTeamEmptyCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(InputBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AppIcons.Search,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.admin_manage_team_no_players_found),
                color = TextGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun AdminManageTeamErrorState(
    innerPadding: PaddingValues,
    message: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFEBEE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.Cancel,
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = message,
                    color = ErrorRed,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
