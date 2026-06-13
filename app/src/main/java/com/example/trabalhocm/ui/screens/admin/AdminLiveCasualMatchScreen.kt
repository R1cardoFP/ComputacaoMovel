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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.trabalhocm.data.model.AdminLiveCasualMatch
import com.example.trabalhocm.data.model.AdminLiveCasualPlayer
import com.example.trabalhocm.data.model.AdminLiveCasualPoint
import com.example.trabalhocm.data.repository.AdminLiveCasualMatchRepository
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
fun AdminLiveCasualMatchScreen(
    matchId: String,
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onEditMatchClick: (String) -> Unit = {},
    onMatchCancelled: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { AdminLiveCasualMatchRepository() }
    val scope = rememberCoroutineScope()

    var match by remember { mutableStateOf<AdminLiveCasualMatch?>(null) }
    var selectedSide by remember { mutableStateOf("casa") }
    var selectedPlayer by remember { mutableStateOf<AdminLiveCasualPlayer?>(null) }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var actionMessage by remember { mutableStateOf("") }
    var refreshKey by remember { mutableStateOf(0) }
    var actionMessageIsError by remember { mutableStateOf(false) }

    var pendingPoints by remember { mutableStateOf<List<PendingPoint>>(emptyList()) }

    val errorLoadingLiveMatchText = stringResource(R.string.admin_live_match_error_loading)
    val selectPlayerFirstText = stringResource(R.string.admin_live_match_select_player_first)
    val pointAddedLocallyText = stringResource(R.string.admin_live_match_point_added_locally)
    val noChangesToConfirmText = stringResource(R.string.admin_live_match_no_changes_confirm)
    val unknownErrorText = stringResource(R.string.admin_live_match_unknown_error)
    val errorConfirmingChangesText = stringResource(R.string.admin_live_match_error_confirming_changes)
    val changesConfirmedText = stringResource(R.string.admin_live_match_changes_confirmed)
    val noChangesToDiscardText = stringResource(R.string.admin_live_match_no_changes_discard)
    val changesDiscardedText = stringResource(R.string.admin_live_match_changes_discarded)

    LaunchedEffect(matchId, refreshKey) {
        isLoading = true
        errorMessage = ""

        repository.obterLiveMatch(matchId)
            .onSuccess { loadedMatch ->
                match = loadedMatch

                val currentSelectedStillExists = selectedPlayer != null &&
                        loadedMatch.players.any { it.id == selectedPlayer?.id && it.teamSide == selectedSide }

                if (!currentSelectedStillExists) {
                    selectedPlayer = loadedMatch.players.firstOrNull { it.teamSide == selectedSide }
                        ?: loadedMatch.players.firstOrNull()
                }
            }
            .onFailure {
                errorMessage = "$errorLoadingLiveMatchText: ${it.message}"
            }

        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.admin_live_match_top_title),
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AdminLiveErrorCard(errorMessage = errorMessage)
                }
            }

            match != null -> {
                val currentMatch = match!!

                AdminLiveCasualMatchContent(
                    match = currentMatch,
                    pendingPoints = pendingPoints,
                    selectedSide = selectedSide,
                    selectedPlayer = selectedPlayer,
                    isSaving = isSaving,
                    actionMessage = actionMessage,
                    actionMessageIsError = actionMessageIsError,
                    innerPadding = innerPadding,
                    onSideSelected = { side ->
                        selectedSide = side
                        selectedPlayer = match?.players?.firstOrNull { player ->
                            player.teamSide == side
                        }
                    },
                    onPlayerSelected = {
                        selectedPlayer = it
                    },
                    onAddPointClick = {
                        val player = selectedPlayer

                        if (player == null) {
                            actionMessage = selectPlayerFirstText
                            actionMessageIsError = true
                            return@AdminLiveCasualMatchContent
                        }

                        pendingPoints = pendingPoints + PendingPoint(
                            playerId = player.id,
                            playerName = player.nome,
                            playerInitials = player.initials,
                            teamSide = selectedSide,
                            teamName = if (selectedSide == "casa") {
                                currentMatch.homeTeamName
                            } else {
                                currentMatch.awayTeamName
                            }
                        )

                        actionMessage = pointAddedLocallyText
                        actionMessageIsError = false
                    },
                    onConfirmChangesClick = {
                        if (pendingPoints.isEmpty()) {
                            actionMessage = noChangesToConfirmText
                            actionMessageIsError = true
                            return@AdminLiveCasualMatchContent
                        }

                        scope.launch {
                            isSaving = true
                            actionMessage = ""

                            var hasError = false
                            var errorText = ""

                            pendingPoints.forEach { point ->
                                repository.adicionarPonto(
                                    matchId = matchId,
                                    playerId = point.playerId,
                                    teamSide = point.teamSide,
                                    minute = 0
                                )
                                    .onFailure {
                                        hasError = true
                                        errorText = it.message ?: unknownErrorText
                                    }
                            }

                            if (hasError) {
                                actionMessage = "$errorConfirmingChangesText: $errorText"
                                actionMessageIsError = true
                            } else {
                                pendingPoints = emptyList()
                                actionMessage = changesConfirmedText
                                actionMessageIsError = false
                                refreshKey++
                            }

                            isSaving = false
                        }
                    },
                    onDiscardChangesClick = {
                        if (pendingPoints.isEmpty()) {
                            actionMessage = noChangesToDiscardText
                            actionMessageIsError = false
                        } else {
                            pendingPoints = emptyList()
                            actionMessage = changesDiscardedText
                            actionMessageIsError = false
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AdminLiveCasualMatchContent(
    match: AdminLiveCasualMatch,
    pendingPoints: List<PendingPoint>,
    selectedSide: String,
    selectedPlayer: AdminLiveCasualPlayer?,
    isSaving: Boolean,
    actionMessage: String,
    actionMessageIsError: Boolean,
    innerPadding: PaddingValues,
    onSideSelected: (String) -> Unit,
    onPlayerSelected: (AdminLiveCasualPlayer) -> Unit,
    onAddPointClick: () -> Unit,
    onConfirmChangesClick: () -> Unit,
    onDiscardChangesClick: () -> Unit
) {
    val homeScoreWithPending = match.homeScore + pendingPoints.count { it.teamSide == "casa" }
    val awayScoreWithPending = match.awayScore + pendingPoints.count { it.teamSide == "fora" }

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
            LiveScoreCard(
                match = match,
                homeScore = homeScoreWithPending,
                awayScore = awayScoreWithPending,
                pendingCount = pendingPoints.size
            )
        }

        item {
            AdminLiveSectionHeader(
                title = stringResource(R.string.admin_live_match_points),
                subtitle = if (pendingPoints.isEmpty()) {
                    stringResource(R.string.admin_live_match_admin_view)
                } else {
                    "${pendingPoints.size} ${stringResource(R.string.admin_live_match_pending).lowercase()}"
                }
            )
        }

        if (match.points.isEmpty() && pendingPoints.isEmpty()) {
            item {
                EmptyLivePointsCard()
            }
        }

        items(match.points) { point ->
            PointRow(point = point)
        }

        items(pendingPoints) { point ->
            PendingPointRow(point = point)
        }

        item {
            AddPointCard(
                match = match,
                selectedSide = selectedSide,
                selectedPlayer = selectedPlayer,
                isSaving = isSaving,
                onSideSelected = onSideSelected,
                onPlayerSelected = onPlayerSelected,
                onAddPointClick = onAddPointClick
            )
        }

        if (actionMessage.isNotBlank()) {
            item {
                AdminLiveActionMessageCard(
                    message = actionMessage,
                    isError = actionMessageIsError
                )
            }
        }

        item {
            LiveConfirmActionsCard(
                hasPendingChanges = pendingPoints.isNotEmpty(),
                pendingCount = pendingPoints.size,
                isSaving = isSaving,
                onConfirmChangesClick = onConfirmChangesClick,
                onDiscardChangesClick = onDiscardChangesClick
            )
        }
    }
}

@Composable
private fun LiveScoreCard(
    match: AdminLiveCasualMatch,
    homeScore: Int,
    awayScore: Int,
    pendingCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LiveBadge()

                if (pendingCount > 0) {
                    Surface(
                        color = Color.White.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        Text(
                            text = "+$pendingCount ${stringResource(R.string.admin_live_match_pending).uppercase()}",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamScoreColumn(
                    name = match.homeTeamName,
                    modifier = Modifier.weight(1f)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 14.dp)
                ) {
                    Text(
                        text = "$homeScore : $awayScore",
                        color = Color.White,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = stringResource(R.string.admin_live_match_live).uppercase(),
                        color = Color.White.copy(alpha = 0.68f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }

                TeamScoreColumn(
                    name = match.awayTeamName,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            HorizontalDivider(color = Color.White.copy(alpha = 0.12f))

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = match.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun LiveBadge() {
    Surface(
        color = ErrorRed,
        shape = RoundedCornerShape(50.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = stringResource(R.string.admin_live_match_live).uppercase(),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun TeamScoreColumn(
    name: String,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials(name),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = name,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AdminLiveSectionHeader(
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = DarkBlue,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Surface(
            color = InputBg,
            shape = RoundedCornerShape(50.dp)
        ) {
            Text(
                text = subtitle.uppercase(),
                color = TextGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
    }
}

@Composable
private fun EmptyLivePointsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(InputBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AppIcons.Games,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.admin_live_match_no_points),
                color = TextGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun PointRow(point: AdminLiveCasualPoint) {
    LivePointCard(
        initials = point.scorerInitials,
        title = stringResource(R.string.admin_live_match_point).uppercase(),
        subtitle = "${point.scorerName} · ${point.teamName}",
        badgeText = null,
        containerColor = CardBg,
        avatarColor = TealGreen,
        borderColor = null
    )
}

@Composable
private fun PendingPointRow(point: PendingPoint) {
    LivePointCard(
        initials = point.playerInitials,
        title = stringResource(R.string.admin_live_match_point).uppercase(),
        subtitle = "${point.playerName} · ${point.teamName}",
        badgeText = stringResource(R.string.admin_live_match_pending).uppercase(),
        containerColor = Color(0xFFEAF8F5),
        avatarColor = TealGreen,
        borderColor = TealGreen
    )
}

@Composable
private fun LivePointCard(
    initials: String,
    title: String,
    subtitle: String,
    badgeText: String?,
    containerColor: Color,
    avatarColor: Color,
    borderColor: Color?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = borderColor?.let { BorderStroke(1.dp, it.copy(alpha = 0.55f)) },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = DarkBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = subtitle,
                    color = TextGray,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (badgeText != null) {
                Surface(
                    color = TealGreen.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = TealGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddPointCard(
    match: AdminLiveCasualMatch,
    selectedSide: String,
    selectedPlayer: AdminLiveCasualPlayer?,
    isSaving: Boolean,
    onSideSelected: (String) -> Unit,
    onPlayerSelected: (AdminLiveCasualPlayer) -> Unit,
    onAddPointClick: () -> Unit
) {
    val playersFromSelectedSide = match.players.filter { player ->
        player.teamSide == selectedSide
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            AdminLiveCardTitle(
                title = stringResource(R.string.admin_live_match_register_point),
                icon = AppIcons.Confirm
            )

            Spacer(modifier = Modifier.height(16.dp))

            AdminLiveFieldLabel(text = stringResource(R.string.admin_live_match_team))

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TeamSideChip(
                    text = match.homeTeamName,
                    selected = selectedSide == "casa",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onSideSelected("casa")
                    }
                )

                TeamSideChip(
                    text = match.awayTeamName,
                    selected = selectedSide == "fora",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onSideSelected("fora")
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            AdminLiveFieldLabel(text = stringResource(R.string.admin_live_match_player))

            Spacer(modifier = Modifier.height(8.dp))

            if (playersFromSelectedSide.isEmpty()) {
                Surface(
                    color = InputBg,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.admin_live_match_no_players_team),
                        color = TextGray,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            } else {
                playersFromSelectedSide.forEachIndexed { index, player ->
                    PlayerPointOption(
                        player = player,
                        selected = selectedPlayer?.id == player.id,
                        onClick = {
                            onPlayerSelected(player)
                        }
                    )

                    if (index != playersFromSelectedSide.lastIndex) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onAddPointClick,
                enabled = !isSaving && !match.isCanceled && playersFromSelectedSide.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealGreen,
                    contentColor = Color.White,
                    disabledContainerColor = TextGray.copy(alpha = 0.28f),
                    disabledContentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = if (isSaving) {
                        stringResource(R.string.admin_live_match_adding).uppercase()
                    } else {
                        stringResource(R.string.admin_live_match_add_point).uppercase()
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TeamSideChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(46.dp)
            .clickable { onClick() },
        color = if (selected) PrimaryBlue else InputBg,
        shape = RoundedCornerShape(14.dp),
        border = if (selected) null else BorderStroke(1.dp, Color(0xFFE5EAF2))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
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
private fun PlayerPointOption(
    player: AdminLiveCasualPlayer,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (selected) Color(0xFFEAF8F5) else InputBg,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 13.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (selected) TealGreen else PrimaryBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = player.initials,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = player.nome,
                color = DarkBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = player.email,
                color = TextGray,
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (selected) {
            Surface(
                color = TealGreen.copy(alpha = 0.12f),
                shape = RoundedCornerShape(50.dp)
            ) {
                Text(
                    text = stringResource(R.string.admin_live_match_selected).uppercase(),
                    color = TealGreen,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun LiveConfirmActionsCard(
    hasPendingChanges: Boolean,
    pendingCount: Int,
    isSaving: Boolean,
    onConfirmChangesClick: () -> Unit,
    onDiscardChangesClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF8F5)),
        border = BorderStroke(1.dp, TealGreen.copy(alpha = 0.45f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AdminLiveCardTitle(
                    title = stringResource(R.string.admin_live_match_admin_actions),
                    icon = AppIcons.Confirm,
                    iconColor = TealGreen
                )

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(50.dp)
                ) {
                    Text(
                        text = "$pendingCount ${stringResource(R.string.admin_live_match_pending).uppercase()}",
                        color = TealGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onDiscardChangesClick,
                enabled = hasPendingChanges && !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color(0xFFD7E2EA)),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.White,
                    contentColor = DarkBlue,
                    disabledContainerColor = Color.White.copy(alpha = 0.55f),
                    disabledContentColor = TextGray
                )
            ) {
                Icon(
                    imageVector = AppIcons.Cancel,
                    contentDescription = stringResource(R.string.admin_live_match_discard_changes),
                    tint = if (hasPendingChanges && !isSaving) DarkBlue else TextGray,
                    modifier = Modifier.size(17.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.admin_live_match_discard_changes).uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onConfirmChangesClick,
                enabled = hasPendingChanges && !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White,
                    disabledContainerColor = TextGray.copy(alpha = 0.28f),
                    disabledContentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    imageVector = AppIcons.Confirm,
                    contentDescription = stringResource(R.string.admin_live_match_confirm_changes),
                    tint = Color.White,
                    modifier = Modifier.size(17.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (isSaving) {
                        stringResource(R.string.admin_live_match_saving).uppercase()
                    } else {
                        stringResource(R.string.admin_live_match_confirm_changes).uppercase()
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AdminLiveActionMessageCard(
    message: String,
    isError: Boolean
) {
    val color = if (isError) ErrorRed else TealGreen
    val background = if (isError) Color(0xFFFFEEF0) else Color(0xFFEAF8F5)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = background,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.32f))
    ) {
        Text(
            text = message,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        )
    }
}

@Composable
private fun AdminLiveErrorCard(errorMessage: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFEEF0)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AppIcons.Cancel,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = errorMessage,
                color = ErrorRed,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AdminLiveCardTitle(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color = PrimaryBlue
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = title,
            color = DarkBlue,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AdminLiveFieldLabel(text: String) {
    Text(
        text = text.uppercase(),
        color = TextGray,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
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

private data class PendingPoint(
    val playerId: String,
    val playerName: String,
    val playerInitials: String,
    val teamSide: String,
    val teamName: String
)
