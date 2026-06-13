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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.model.AdminInvitePlayerTeam
import com.example.trabalhocm.data.model.AdminInvitePlayerUser
import com.example.trabalhocm.data.repository.AdminInvitePlayerRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.AppIcons
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.BrandWhite
import com.example.trabalhocm.ui.theme.DarkBlue
import com.example.trabalhocm.ui.theme.ErrorRed
import com.example.trabalhocm.ui.theme.InputBg
import com.example.trabalhocm.ui.theme.PrimaryBlue
import com.example.trabalhocm.ui.theme.TealGreen
import com.example.trabalhocm.ui.theme.TextGray
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.admin_invite_player_manage_team_title),
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
                AdminInvitePlayerErrorState(
                    innerPadding = innerPadding,
                    message = errorMessage
                )
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
            start = 24.dp,
            end = 24.dp,
            top = 20.dp,
            bottom = 32.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            AdminInvitePlayerHeroCard(
                team = team,
                availableCount = availablePlayers.size,
                invitedCount = invitedPlayers.size
            )
        }

        item {
            TargetTeamCard(team = team)
        }

        item {
            AdminInvitePlayerSearchBox(
                value = searchText,
                onValueChange = onSearchChange
            )
        }

        item {
            SectionHeader(
                title = stringResource(
                    R.string.admin_invite_player_available_players_count,
                    filteredAvailablePlayers.size
                ),
                subtitle = stringResource(R.string.admin_invite_player_search_players)
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
                SectionHeader(
                    title = stringResource(
                        R.string.admin_invite_player_pending_invites_count,
                        filteredInvitedPlayers.size
                    ),
                    subtitle = stringResource(R.string.admin_invite_player_invitation_details)
                )
            }

            items(filteredInvitedPlayers) { player ->
                InvitedPlayerCard(player = player)
            }
        }

        item {
            InvitationMessageCard(
                message = message,
                actionMessage = actionMessage,
                actionMessageIsError = actionMessageIsError,
                isSending = isSending,
                onMessageChange = onMessageChange,
                onSendInviteClick = onSendInviteClick
            )
        }
    }
}

@Composable
private fun AdminInvitePlayerHeroCard(
    team: AdminInvitePlayerTeam,
    availableCount: Int,
    invitedCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.admin_invite_player_console).uppercase(),
                color = TealGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.4.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.admin_invite_player_title),
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.admin_invite_player_description),
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 13.sp,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HeroStatPill(
                    value = availableCount.toString(),
                    label = stringResource(R.string.admin_invite_player_available_players_count, availableCount),
                    modifier = Modifier.weight(1f)
                )

                HeroStatPill(
                    value = invitedCount.toString(),
                    label = stringResource(R.string.admin_invite_player_pending_invites_count, invitedCount),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.10f))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamInitialsAvatar(
                    initials = team.sigla.take(2).uppercase(),
                    backgroundColor = TealGreen,
                    size = 46.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = team.nome,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = team.modalidade,
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroStatPill(
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 11.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = value,
            color = Color.White,
            fontSize = 19.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label.uppercase(),
            color = Color.White.copy(alpha = 0.70f),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TargetTeamCard(team: AdminInvitePlayerTeam) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(
                title = stringResource(R.string.admin_invite_player_target_team),
                subtitle = team.modalidade
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(InputBg)
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamInitialsAvatar(
                    initials = team.sigla.take(2).uppercase(),
                    backgroundColor = PrimaryBlue,
                    size = 48.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = team.nome,
                        color = DarkBlue,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    TeamSmallBadge(text = team.modalidade.uppercase())
                }
            }
        }
    }
}

@Composable
private fun AdminInvitePlayerSearchBox(
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.admin_invite_player_search_players).uppercase(),
            color = TextGray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp)),
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = AppIcons.Search,
                    contentDescription = stringResource(R.string.admin_invite_player_search_content_description),
                    tint = TextGray,
                    modifier = Modifier.size(19.dp)
                )
            },
            placeholder = {
                Text(
                    text = stringResource(R.string.admin_invite_player_search_placeholder),
                    color = TextGray,
                    fontSize = 13.sp
                )
            },
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
private fun SectionHeader(
    title: String,
    subtitle: String? = null
) {
    Column {
        Text(
            text = title.uppercase(),
            color = DarkBlue,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.3.sp
        )

        if (!subtitle.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = TextGray,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun InvitePlayerCard(
    player: AdminInvitePlayerUser,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) TealGreen else Color.Transparent
    val backgroundColor = if (selected) Color(0xFFEAF8F5) else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        PlayerRow(
            player = player,
            circleColor = if (selected) TealGreen else PrimaryBlue,
            trailingText = if (selected) stringResource(R.string.admin_invite_player_selected).uppercase() else null,
            trailingColor = TealGreen
        )
    }
}

@Composable
private fun InvitedPlayerCard(
    player: AdminInvitePlayerUser
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF8F5)),
        border = BorderStroke(1.dp, TealGreen.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        PlayerRow(
            player = player,
            circleColor = TealGreen,
            trailingText = stringResource(R.string.admin_invite_player_invited).uppercase(),
            trailingColor = TealGreen
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
            .padding(horizontal = 15.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TeamInitialsAvatar(
            initials = player.initials,
            backgroundColor = circleColor,
            size = 46.dp
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = player.nome,
                color = DarkBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = player.email,
                color = TextGray,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (!trailingText.isNullOrBlank()) {
            StatusBadge(
                text = trailingText,
                color = trailingColor
            )
        }
    }
}

@Composable
private fun InvitationMessageCard(
    message: String,
    actionMessage: String,
    actionMessageIsError: Boolean,
    isSending: Boolean,
    onMessageChange: (String) -> Unit,
    onSendInviteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            SectionHeader(
                title = stringResource(R.string.admin_invite_player_invitation_details),
                subtitle = stringResource(R.string.admin_invite_player_invitation_desc)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.admin_invite_player_personal_message).uppercase(),
                color = TextGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = message,
                onValueChange = onMessageChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
                    .clip(RoundedCornerShape(14.dp)),
                maxLines = 4,
                placeholder = {
                    Text(
                        text = stringResource(R.string.admin_invite_player_message_placeholder),
                        color = TextGray,
                        fontSize = 13.sp
                    )
                },
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

            if (actionMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                ActionMessageCard(
                    message = actionMessage,
                    isError = actionMessageIsError
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSendInviteClick,
                enabled = !isSending,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealGreen,
                    contentColor = Color.White,
                    disabledContainerColor = TextGray.copy(alpha = 0.35f),
                    disabledContentColor = Color.White
                )
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Icon(
                        imageVector = AppIcons.Confirm,
                        contentDescription = stringResource(R.string.admin_invite_player_send_invite_content_description),
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (isSending) {
                        stringResource(R.string.admin_invite_player_sending).uppercase()
                    } else {
                        stringResource(R.string.admin_invite_player_send_invite).uppercase()
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

@Composable
private fun ActionMessageCard(
    message: String,
    isError: Boolean
) {
    val color = if (isError) ErrorRed else TealGreen
    val background = if (isError) ErrorRed.copy(alpha = 0.08f) else TealGreen.copy(alpha = 0.10f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(color)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = message,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 17.sp
        )
    }
}

@Composable
private fun EmptyPlayersCard(
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(InputBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AppIcons.Profile,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = text,
                color = TextGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun AdminInvitePlayerErrorState(
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
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(ErrorRed.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.Notifications,
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(25.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = message,
                    color = ErrorRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 19.sp
                )
            }
        }
    }
}

@Composable
private fun TeamInitialsAvatar(
    initials: String,
    backgroundColor: Color,
    size: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = BrandWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun TeamSmallBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = PrimaryBlue.copy(alpha = 0.10f)
    ) {
        Text(
            text = text,
            color = PrimaryBlue,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun StatusBadge(
    text: String,
    color: Color
) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = color.copy(alpha = 0.12f)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
        )
    }
}
