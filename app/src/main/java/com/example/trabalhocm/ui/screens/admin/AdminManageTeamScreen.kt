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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.model.AdminManageTeam
import com.example.trabalhocm.data.model.AdminManageTeamPlayer
import com.example.trabalhocm.data.repository.AdminManageTeamRepository
import com.example.trabalhocm.ui.theme.AppIcons
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import com.example.trabalhocm.ui.theme.CardBg
import com.example.trabalhocm.ui.theme.ErrorRed
import com.example.trabalhocm.ui.theme.PrimaryBlue
import com.example.trabalhocm.ui.theme.TextGray
import kotlinx.coroutines.launch

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
    var refreshKey by remember { mutableStateOf(0) }

    LaunchedEffect(teamId, refreshKey) {
        isLoading = true
        errorMessage = ""

        repository.obterEquipa(teamId)
            .onSuccess {
                team = it
            }
            .onFailure {
                errorMessage = "Error loading team: ${it.message}"
            }

        isLoading = false
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            AdminManageTeamTopBar(
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick
            )
        },
        bottomBar = {
            AdminManageTeamBottomBar(
                selected = "teams",
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

            team != null -> {
                val currentTeam = team!!

                AdminManageTeamContent(
                    team = currentTeam,
                    searchText = searchText,
                    onSearchChange = { searchText = it },
                    innerPadding = innerPadding,
                    actionMessage = actionMessage,
                    onInvitePlayerClick = onInvitePlayerClick,
                    onPlayerClick = onPlayerClick,
                    onRemovePlayerClick = { playerId ->
                        scope.launch {
                            repository.removerJogadorDaEquipa(currentTeam.id, playerId)
                                .onSuccess {
                                    actionMessage = "Player removed from team successfully."
                                    refreshKey++
                                }
                                .onFailure {
                                    actionMessage = "Error removing player: ${it.message}"
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
            start = 18.dp,
            end = 18.dp,
            top = 16.dp,
            bottom = 28.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "ADMIN TOOL",
                color = BrandGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Manage Team",
                color = BrandBlue,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Manage roster, invite players and oversee team composition.",
                color = TextGray,
                fontSize = 12.sp
            )
        }

        item {
            TeamManageHeroCard(team = team)
        }

        item {
            Button(
                onClick = {
                    onInvitePlayerClick(team.id)
                },
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
                    imageVector = AppIcons.Add,
                    contentDescription = "Invite player",
                    tint = BrandWhite,
                    modifier = Modifier.size(17.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "INVITE PLAYER",
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
                        text = "Search roster...",
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
            Text(
                text = "ROSTER (${filteredPlayers.size})",
                color = TextGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        if (actionMessage.isNotBlank()) {
            item {
                Text(
                    text = actionMessage,
                    color = if (actionMessage.startsWith("Error")) ErrorRed else BrandGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (filteredPlayers.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(9.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "No players found.",
                        color = TextGray,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(18.dp)
                    )
                }
            }
        } else {
            items(filteredPlayers) { player ->
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
private fun TeamManageHeroCard(team: AdminManageTeam) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(45.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEAB308)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = team.sigla.take(3).uppercase(),
                    color = BrandWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = team.nome,
                    color = BrandWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = team.modalidade,
                    color = Color(0xFFB9C4D8),
                    fontSize = 11.sp
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = team.playersCount.toString(),
                    color = BrandWhite,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "PLAYERS",
                    color = Color(0xFFB9C4D8),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
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
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 13.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(43.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.initials,
                    color = BrandWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = player.nome,
                    color = BrandBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = player.email,
                    color = TextGray,
                    fontSize = 11.sp,
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
                        contentDescription = "Player options",
                        tint = BrandBlue,
                        modifier = Modifier.size(20.dp)
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
                                text = "Remove from team",
                                color = ErrorRed,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = AppIcons.Delete,
                                contentDescription = "Remove",
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

@Composable
private fun AdminManageTeamTopBar(
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
                text = "Manage Team",
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
private fun AdminManageTeamBottomBar(
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
        BottomManageTeamItem(AppIcons.Home, "HOME", selected == "home", onHomeClick)
        BottomManageTeamItem(AppIcons.Tournaments, "TOURNAMENTS", selected == "tournaments", onTournamentsClick)
        BottomManageTeamItem(AppIcons.Games, "MATCHES", selected == "matches", onMatchesClick)
        BottomManageTeamItem(AppIcons.Teams, "TEAMS", selected == "teams", onTeamsClick)
        BottomManageTeamItem(AppIcons.Profile, "PROFILE", selected == "profile", onProfileClick)
    }
}

@Composable
private fun BottomManageTeamItem(
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