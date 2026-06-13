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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.model.AdminInvitePlayerTeam
import com.example.trabalhocm.data.model.AdminInvitePlayerUser
import com.example.trabalhocm.data.repository.AdminInvitePlayerRepository
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
import com.example.trabalhocm.R

@Composable
fun AdminInvitePlayerScreen(
    teamId: String,
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onInviteSent: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { AdminInvitePlayerRepository() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val errorLoadingPlayersText = stringResource(R.string.admin_invite_player_error_loading)
    val selectPlayerFirstText = stringResource(R.string.admin_invite_player_select_player_first)
    val errorSendingInviteText = stringResource(R.string.admin_invite_player_error_sending_invite)

    var team by remember { mutableStateOf<AdminInvitePlayerTeam?>(null) }
    var availablePlayers by remember { mutableStateOf<List<AdminInvitePlayerUser>>(emptyList()) }
    var invitedPlayers by remember { mutableStateOf<List<AdminInvitePlayerUser>>(emptyList()) }
    var selectedPlayer by remember { mutableStateOf<AdminInvitePlayerUser?>(null) }
    var searchText by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var actionMessage by remember { mutableStateOf("") }
    var actionMessageIsError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var isSending by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var refreshKey by remember { mutableStateOf(0) }

    LaunchedEffect(teamId, refreshKey) {
        isLoading = true
        errorMessage = ""

        repository.carregarDados(teamId)
            .onSuccess { data ->
                team = data.team
                availablePlayers = data.availablePlayers
                invitedPlayers = data.invitedPlayers

                if (message.isBlank()) {
                    message = context.getString(
                        R.string.admin_invite_player_default_message,
                        data.team.nome
                    )
                }
            }
            .onFailure {
                errorMessage = "$errorLoadingPlayersText: ${it.message}"
            }

        isLoading = false
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            AdminInvitePlayerTopBar(
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick
            )
        },
        bottomBar = {
            AdminInvitePlayerBottomBar(
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
                AdminInvitePlayerContent(
                    team = team!!,
                    availablePlayers = availablePlayers,
                    invitedPlayers = invitedPlayers,
                    selectedPlayer = selectedPlayer,
                    searchText = searchText,
                    message = message,
                    actionMessage = actionMessage,
                    actionMessageIsError = actionMessageIsError,
                    isSending = isSending,
                    innerPadding = innerPadding,
                    onSearchChange = { searchText = it },
                    onMessageChange = { message = it },
                    onSelectPlayer = { selectedPlayer = it },
                    onSendInviteClick = {
                        val player = selectedPlayer

                        if (player == null) {
                            actionMessage = selectPlayerFirstText
                            actionMessageIsError = true
                            return@AdminInvitePlayerContent
                        }

                        scope.launch {
                            isSending = true
                            actionMessage = ""
                            actionMessageIsError = false

                            repository.enviarConvite(
                                teamId = teamId,
                                playerId = player.id,
                                mensagem = message
                            )
                                .onSuccess {
                                    actionMessage = context.getString(
                                        R.string.admin_invite_player_invite_sent_to,
                                        player.nome
                                    )
                                    actionMessageIsError = false
                                    selectedPlayer = null
                                    refreshKey++
                                }
                                .onFailure {
                                    actionMessage = "$errorSendingInviteText: ${it.message}"
                                    actionMessageIsError = true
                                }

                            isSending = false
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AdminInvitePlayerContent(
    team: AdminInvitePlayerTeam,
    availablePlayers: List<AdminInvitePlayerUser>,
    invitedPlayers: List<AdminInvitePlayerUser>,
    selectedPlayer: AdminInvitePlayerUser?,
    searchText: String,
    message: String,
    actionMessage: String,
    actionMessageIsError: Boolean,
    isSending: Boolean,
    innerPadding: PaddingValues,
    onSearchChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onSelectPlayer: (AdminInvitePlayerUser) -> Unit,
    onSendInviteClick: () -> Unit
) {
    val query = searchText.trim()

    val filteredAvailablePlayers = availablePlayers.filter { player ->
        query.isBlank() ||
                player.nome.contains(query, ignoreCase = true) ||
                player.email.contains(query, ignoreCase = true) ||
                player.id.contains(query, ignoreCase = true)
    }

    val filteredInvitedPlayers = invitedPlayers.filter { player ->
        query.isBlank() ||
                player.nome.contains(query, ignoreCase = true) ||
                player.email.contains(query, ignoreCase = true) ||
                player.id.contains(query, ignoreCase = true)
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
                text = stringResource(R.string.admin_invite_player_console).uppercase(),
                color = BrandGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.admin_invite_player_title),
                color = BrandBlue,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.admin_invite_player_description),
                color = TextGray,
                fontSize = 12.sp
            )
        }

        item {
            TargetTeamCard(team = team)
        }

        item {
            Text(
                text = stringResource(R.string.admin_invite_player_search_players).uppercase(),
                color = TextGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

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
                        contentDescription = stringResource(R.string.admin_invite_player_search_content_description),
                        tint = TextGray,
                        modifier = Modifier.size(18.dp)
                    )
                },
                placeholder = {
                    Text(
                        text = stringResource(R.string.admin_invite_player_search_placeholder),
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
                text = stringResource(
                    R.string.admin_invite_player_available_players_count,
                    filteredAvailablePlayers.size
                ).uppercase(),
                color = TextGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        if (filteredAvailablePlayers.isEmpty()) {
            item {
                EmptyPlayersCard(
                    text = stringResource(R.string.admin_invite_player_no_available_players)
                )
            }
        } else {
            items(filteredAvailablePlayers) { player ->
                InvitePlayerCard(
                    player = player,
                    selected = selectedPlayer?.id == player.id,
                    onClick = {
                        onSelectPlayer(player)
                    }
                )
            }
        }

        if (filteredInvitedPlayers.isNotEmpty()) {
            item {
                Text(
                    text = stringResource(
                        R.string.admin_invite_player_pending_invites_count,
                        filteredInvitedPlayers.size
                    ).uppercase(),
                    color = TextGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            items(filteredInvitedPlayers) { player ->
                InvitedPlayerCard(player = player)
            }
        }

        item {
            Text(
                text = stringResource(R.string.admin_invite_player_invitation_details).uppercase(),
                color = BrandBlue,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = stringResource(R.string.admin_invite_player_invitation_desc),
                color = TextGray,
                fontSize = 12.sp
            )
        }

        item {
            Text(
                text = stringResource(R.string.admin_invite_player_personal_message).uppercase(),
                color = TextGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = message,
                onValueChange = onMessageChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp),
                shape = RoundedCornerShape(9.dp),
                maxLines = 4,
                placeholder = {
                    Text(
                        text = stringResource(R.string.admin_invite_player_message_placeholder),
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

        if (actionMessage.isNotBlank()) {
            item {
                Text(
                    text = actionMessage,
                    color = if (actionMessageIsError) {
                        ErrorRed
                    } else {
                        BrandGreen
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Button(
                onClick = onSendInviteClick,
                enabled = !isSending,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(7.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreen,
                    contentColor = BrandWhite,
                    disabledContainerColor = TextGray,
                    disabledContentColor = BrandWhite
                )
            ) {
                Icon(
                    imageVector = AppIcons.Confirm,
                    contentDescription = stringResource(R.string.admin_invite_player_send_invite_content_description),
                    tint = BrandWhite,
                    modifier = Modifier.size(17.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (isSending) stringResource(R.string.admin_invite_player_sending).uppercase() else stringResource(R.string.admin_invite_player_send_invite).uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TargetTeamCard(team: AdminInvitePlayerTeam) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Text(
                text = stringResource(R.string.admin_invite_player_target_team).uppercase(),
                color = TextGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFEFF3F8),
                        shape = RoundedCornerShape(7.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = team.sigla.take(2).uppercase(),
                        color = BrandWhite,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = team.nome,
                        color = BrandBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = team.modalidade,
                        color = TextGray,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun InvitePlayerCard(
    player: AdminInvitePlayerUser,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) BrandGreen else Color.Transparent
    val backgroundColor = if (selected) Color(0xFFEAF8F5) else CardBg

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        PlayerRow(
            player = player,
            circleColor = if (selected) BrandGreen else PrimaryBlue,
            trailingText = if (selected) stringResource(R.string.admin_invite_player_selected).uppercase() else null,
            trailingColor = BrandGreen
        )
    }
}

@Composable
private fun InvitedPlayerCard(
    player: AdminInvitePlayerUser
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF8F5)),
        border = BorderStroke(1.dp, BrandGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        PlayerRow(
            player = player,
            circleColor = BrandGreen,
            trailingText = stringResource(R.string.admin_invite_player_invited).uppercase(),
            trailingColor = BrandGreen
        )
    }
}

@Composable
private fun PlayerRow(
    player: AdminInvitePlayerUser,
    circleColor: Color,
    trailingText: String?,
    trailingColor: Color
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
                .background(circleColor),
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

        if (!trailingText.isNullOrBlank()) {
            Text(
                text = trailingText,
                color = trailingColor,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun EmptyPlayersCard(
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = text,
            color = TextGray,
            fontSize = 13.sp,
            modifier = Modifier.padding(18.dp)
        )
    }
}

@Composable
private fun AdminInvitePlayerTopBar(
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
                text = stringResource(R.string.admin_invite_player_manage_team_title),
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
private fun AdminInvitePlayerBottomBar(
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
        BottomInvitePlayerItem(AppIcons.Home, stringResource(R.string.admin_nav_home).uppercase(), selected == "home", onHomeClick)
        BottomInvitePlayerItem(AppIcons.Tournaments, stringResource(R.string.admin_nav_tournaments).uppercase(), selected == "tournaments", onTournamentsClick)
        BottomInvitePlayerItem(AppIcons.Games, stringResource(R.string.admin_nav_matches).uppercase(), selected == "matches", onMatchesClick)
        BottomInvitePlayerItem(AppIcons.Teams, stringResource(R.string.admin_nav_teams).uppercase(), selected == "teams", onTeamsClick)
        BottomInvitePlayerItem(AppIcons.Profile, stringResource(R.string.admin_nav_profile).uppercase(), selected == "profile", onProfileClick)
    }
}

@Composable
private fun BottomInvitePlayerItem(
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