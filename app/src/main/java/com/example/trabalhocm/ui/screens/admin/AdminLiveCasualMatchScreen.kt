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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.trabalhocm.data.model.AdminLiveCasualMatch
import com.example.trabalhocm.data.model.AdminLiveCasualPlayer
import com.example.trabalhocm.data.model.AdminLiveCasualPoint
import com.example.trabalhocm.data.repository.AdminLiveCasualMatchRepository
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

    var pendingPoints by remember { mutableStateOf<List<PendingPoint>>(emptyList()) }

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
                errorMessage = "Error loading live match: ${it.message}"
            }

        isLoading = false
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            AdminLiveCasualMatchTopBar(
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick
            )
        },
        bottomBar = {
            AdminLiveCasualMatchBottomBar(
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

            match != null -> {
                val currentMatch = match!!

                AdminLiveCasualMatchContent(
                    match = currentMatch,
                    pendingPoints = pendingPoints,
                    selectedSide = selectedSide,
                    selectedPlayer = selectedPlayer,
                    isSaving = isSaving,
                    actionMessage = actionMessage,
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
                            actionMessage = "Please select a player first."
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

                        actionMessage = "Point added locally. Confirm changes to save."
                    },
                    onConfirmChangesClick = {
                        if (pendingPoints.isEmpty()) {
                            actionMessage = "There are no changes to confirm."
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
                                        errorText = it.message ?: "Unknown error"
                                    }
                            }

                            if (hasError) {
                                actionMessage = "Error confirming changes: $errorText"
                            } else {
                                pendingPoints = emptyList()
                                actionMessage = "Changes confirmed successfully."
                                refreshKey++
                            }

                            isSaving = false
                        }
                    },
                    onDiscardChangesClick = {
                        if (pendingPoints.isEmpty()) {
                            actionMessage = "There are no changes to discard."
                        } else {
                            pendingPoints = emptyList()
                            actionMessage = "Changes discarded."
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
            start = 18.dp,
            end = 18.dp,
            top = 16.dp,
            bottom = 28.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "ADMIN VIEW",
                color = BrandGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        item {
            LiveScoreCard(
                match = match,
                homeScore = homeScoreWithPending,
                awayScore = awayScoreWithPending
            )
        }

        item {
            Text(
                text = "Points",
                color = BrandBlue,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (match.points.isEmpty() && pendingPoints.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(9.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "No points registered yet.",
                        color = TextGray,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
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
                Text(
                    text = actionMessage,
                    color = if (actionMessage.startsWith("Error") || actionMessage.startsWith("Please")) {
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
            LiveConfirmActionsCard(
                hasPendingChanges = pendingPoints.isNotEmpty(),
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
    awayScore: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = ErrorRed,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "LIVE",
                    color = BrandWhite,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamScoreColumn(
                    name = match.homeTeamName,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "$homeScore : $awayScore",
                    color = BrandWhite,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )

                TeamScoreColumn(
                    name = match.awayTeamName,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = match.title,
                color = Color(0xFFB9C4D8),
                fontSize = 11.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
                .size(42.dp)
                .clip(CircleShape)
                .background(PrimaryBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initials(name),
                color = BrandWhite,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = name.uppercase(),
            color = BrandWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun PointRow(point: AdminLiveCasualPoint) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(BrandGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = point.scorerInitials,
                    color = BrandWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = "POINT!",
                    color = BrandBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${point.scorerName} · ${point.teamName}",
                    color = TextGray,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun PendingPointRow(point: PendingPoint) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF8F5)),
        border = BorderStroke(1.dp, BrandGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(BrandGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = point.playerInitials,
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
                    text = "POINT!",
                    color = BrandBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${point.playerName} · ${point.teamName}",
                    color = TextGray,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = "PENDING",
                color = BrandGreen,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
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
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Text(
                text = "Register Point",
                color = BrandBlue,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "TEAM",
                color = TextGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(7.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TeamSideChip(
                    text = match.homeTeamName,
                    selected = selectedSide == "casa",
                    onClick = {
                        onSideSelected("casa")
                    }
                )

                TeamSideChip(
                    text = match.awayTeamName,
                    selected = selectedSide == "fora",
                    onClick = {
                        onSideSelected("fora")
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "PLAYER",
                color = TextGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(7.dp))

            if (playersFromSelectedSide.isEmpty()) {
                Text(
                    text = "No players assigned to this team.",
                    color = TextGray,
                    fontSize = 12.sp
                )
            } else {
                playersFromSelectedSide.forEach { player ->
                    PlayerPointOption(
                        player = player,
                        selected = selectedPlayer?.id == player.id,
                        onClick = {
                            onPlayerSelected(player)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onAddPointClick,
                enabled = !isSaving && !match.isCanceled && playersFromSelectedSide.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreen,
                    contentColor = BrandWhite,
                    disabledContainerColor = TextGray,
                    disabledContentColor = BrandWhite
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = if (isSaving) "ADDING..." else "ADD POINT",
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
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
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
                color = if (selected) Color(0xFFEAF8F5) else Color(0xFFF8FAFC),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                onClick()
            }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (selected) BrandGreen else PrimaryBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = player.initials,
                color = BrandWhite,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = player.nome,
                color = BrandBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = player.email,
                color = TextGray,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (selected) {
            Text(
                text = "SELECTED",
                color = BrandGreen,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun LiveConfirmActionsCard(
    hasPendingChanges: Boolean,
    isSaving: Boolean,
    onConfirmChangesClick: () -> Unit,
    onDiscardChangesClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF8F5)),
        border = BorderStroke(1.dp, BrandGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Text(
                text = "Admin Actions",
                color = BrandGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(13.dp))

            Button(
                onClick = onDiscardChangesClick,
                enabled = hasPendingChanges && !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(5.dp),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandWhite,
                    contentColor = BrandBlue,
                    disabledContainerColor = Color(0xFFE5E7EB),
                    disabledContentColor = TextGray
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    imageVector = AppIcons.Cancel,
                    contentDescription = "Discard changes",
                    tint = if (hasPendingChanges && !isSaving) BrandBlue else TextGray,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "DISCARD CHANGES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(9.dp))

            Button(
                onClick = onConfirmChangesClick,
                enabled = hasPendingChanges && !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = BrandWhite,
                    disabledContainerColor = TextGray,
                    disabledContentColor = BrandWhite
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    imageVector = AppIcons.Confirm,
                    contentDescription = "Confirm changes",
                    tint = BrandWhite,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (isSaving) "SAVING..." else "CONFIRM CHANGES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AdminLiveCasualMatchTopBar(
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
                contentDescription = "Back",
                tint = BrandWhite,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "LIVE MATCH",
                color = BrandWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Icon(
            imageVector = AppIcons.Notifications,
            contentDescription = "Notifications",
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
private fun AdminLiveCasualMatchBottomBar(
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
        BottomLiveMatchItem(AppIcons.Home, "HOME", selected == "home", onHomeClick)
        BottomLiveMatchItem(AppIcons.Tournaments, "TOURNAMENTS", selected == "tournaments", onTournamentsClick)
        BottomLiveMatchItem(AppIcons.Games, "MATCHES", selected == "matches", onMatchesClick)
        BottomLiveMatchItem(AppIcons.Teams, "TEAMS", selected == "teams", onTeamsClick)
        BottomLiveMatchItem(AppIcons.Profile, "PROFILE", selected == "profile", onProfileClick)
    }
}

@Composable
private fun BottomLiveMatchItem(
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