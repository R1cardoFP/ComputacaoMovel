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
import androidx.compose.ui.res.stringResource
import com.example.trabalhocm.R
import com.example.trabalhocm.data.model.AdminCasualMatchDetails
import com.example.trabalhocm.data.model.AdminCasualMatchPlayer
import com.example.trabalhocm.data.repository.AdminCasualMatchDetailsRepository
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AdminCasualMatchDetailsScreen(
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
    val repository = remember { AdminCasualMatchDetailsRepository() }
    val scope = rememberCoroutineScope()

    var match by remember { mutableStateOf<AdminCasualMatchDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var actionMessage by remember { mutableStateOf("") }
    var actionMessageIsError by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableStateOf(0) }

    val errorLoadingDetailsText = stringResource(R.string.admin_casual_match_details_error_loading)
    val cancelSuccessText = stringResource(R.string.admin_casual_match_details_cancel_success)
    val cancelErrorText = stringResource(R.string.admin_casual_match_details_cancel_error)

    LaunchedEffect(matchId, refreshKey) {
        isLoading = true
        errorMessage = ""

        repository.obterDetalhesPeladinha(matchId)
            .onSuccess {
                match = it
            }
            .onFailure {
                errorMessage = "$errorLoadingDetailsText: ${it.message}"
            }

        isLoading = false
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            AdminCasualMatchDetailsTopBar(
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick
            )
        },
        bottomBar = {
            AdminCasualMatchDetailsBottomBar(
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
                AdminCasualMatchDetailsContent(
                    match = match!!,
                    innerPadding = innerPadding,
                    actionMessage = actionMessage,
                    actionMessageIsError = actionMessageIsError,
                    onEditMatchClick = onEditMatchClick,
                    onCancelMatchClick = { id ->
                        scope.launch {
                            repository.cancelarPeladinha(id)
                                .onSuccess {
                                    actionMessage = cancelSuccessText
                                    actionMessageIsError = false
                                    refreshKey++
                                    onMatchCancelled()
                                }
                                .onFailure {
                                    actionMessage = "$cancelErrorText: ${it.message}"
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
private fun AdminCasualMatchDetailsContent(
    match: AdminCasualMatchDetails,
    innerPadding: PaddingValues,
    actionMessage: String,
    actionMessageIsError: Boolean,
    onEditMatchClick: (String) -> Unit,
    onCancelMatchClick: (String) -> Unit
) {
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
                text = stringResource(R.string.admin_casual_match_details_console).uppercase(),
                color = BrandGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = match.title,
                color = BrandBlue,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = match.description,
                color = TextGray,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }

        item {
            ScheduleCard(match = match)
        }

        item {
            HostCard(match = match)
        }

        item {
            JoinedPlayersCard(match = match)
        }

        if (actionMessage.isNotBlank()) {
            item {
                Text(
                    text = actionMessage,
                    color = if (actionMessageIsError) ErrorRed else BrandGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            AdminMatchActionsCard(
                match = match,
                onEditMatchClick = onEditMatchClick,
                onCancelMatchClick = onCancelMatchClick
            )
        }
    }
}

@Composable
private fun ScheduleCard(match: AdminCasualMatchDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.admin_casual_match_details_schedule),
                color = BrandBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            DetailsRow(stringResource(R.string.admin_casual_match_details_date), formatDate(match.date))
            DetailsRow(stringResource(R.string.admin_casual_match_details_time), formatTime(match.time))
            DetailsRow(stringResource(R.string.admin_casual_match_details_location), match.local)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.admin_casual_match_details_status),
                    color = TextGray,
                    fontSize = 12.sp
                )

                StatusBadge(match.estado)
            }
        }
    }
}

@Composable
private fun HostCard(match: AdminCasualMatchDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.admin_casual_match_details_host),
                color = BrandBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = match.hostInitials,
                        color = BrandWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = match.hostName,
                        color = BrandBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = stringResource(R.string.admin_casual_match_details_hosted_matches, match.hostedMatchesCount),
                        color = TextGray,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun JoinedPlayersCard(match: AdminCasualMatchDetails) {
    var showAllPlayers by remember { mutableStateOf(false) }

    val visiblePlayers = if (showAllPlayers) {
        match.players
    } else {
        match.players.take(5)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.admin_casual_match_details_joined_players),
                    color = BrandBlue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFFE0E7FF),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${match.acceptedPlayers}/${match.maxPlayers}",
                        color = PrimaryBlue,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (visiblePlayers.isEmpty()) {
                Text(
                    text = stringResource(R.string.admin_casual_match_details_no_players),
                    color = TextGray,
                    fontSize = 12.sp
                )
            } else {
                visiblePlayers.forEach { player ->
                    JoinedPlayerRow(player = player)
                    Spacer(modifier = Modifier.height(9.dp))
                }
            }

            if (match.players.size > 5) {
                Button(
                    onClick = {
                        showAllPlayers = !showAllPlayers
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp),
                    shape = RoundedCornerShape(5.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandWhite,
                        contentColor = BrandBlue
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = if (showAllPlayers) {
                            stringResource(R.string.admin_casual_match_details_show_less_players).uppercase()
                        } else {
                            stringResource(R.string.admin_casual_match_details_load_more_players).uppercase()
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun JoinedPlayerRow(player: AdminCasualMatchPlayer) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(BrandGreen),
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

        Column {
            Text(
                text = player.nome,
                color = BrandBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = player.email,
                color = TextGray,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AdminMatchActionsCard(
    match: AdminCasualMatchDetails,
    onEditMatchClick: (String) -> Unit,
    onCancelMatchClick: (String) -> Unit
) {
    val isCanceled = match.estado.lowercase() == "cancelada"

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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = AppIcons.Security,
                    contentDescription = stringResource(R.string.admin_casual_match_details_admin_actions),
                    tint = BrandGreen,
                    modifier = Modifier.size(19.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.admin_casual_match_details_admin_actions),
                    color = BrandGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(13.dp))

            Button(
                onClick = {
                    onEditMatchClick(match.id)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandWhite,
                    contentColor = BrandBlue
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = stringResource(R.string.admin_casual_match_details_edit_match).uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(9.dp))

            Button(
                onClick = {
                    onCancelMatchClick(match.id)
                },
                enabled = !isCanceled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCanceled) TextGray else ErrorRed,
                    contentColor = BrandWhite,
                    disabledContainerColor = TextGray,
                    disabledContentColor = BrandWhite
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = if (isCanceled) {
                        stringResource(R.string.admin_casual_match_details_match_canceled).uppercase()
                    } else {
                        stringResource(R.string.admin_casual_match_details_cancel_match).uppercase()
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DetailsRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = TextGray,
            fontSize = 12.sp
        )

        Text(
            text = value,
            color = BrandBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StatusBadge(status: String) {
    val normalized = status.lowercase()

    val background = when (normalized) {
        "aberta" -> Color(0xFFEAF8F5)
        "fechada" -> Color(0xFFFEF3C7)
        "cancelada" -> Color(0xFFFEE2E2)
        else -> Color(0xFFE5E7EB)
    }

    val color = when (normalized) {
        "aberta" -> BrandGreen
        "fechada" -> Color(0xFFEAB308)
        "cancelada" -> ErrorRed
        else -> TextGray
    }

    val text = when (normalized) {
        "aberta" -> stringResource(R.string.admin_casual_match_details_status_open).uppercase()
        "fechada" -> stringResource(R.string.admin_casual_match_details_status_closed).uppercase()
        "cancelada" -> stringResource(R.string.admin_casual_match_details_status_canceled).uppercase()
        else -> status.uppercase()
    }

    Box(
        modifier = Modifier
            .background(
                color = background,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatDate(value: String): String {
    return try {
        val date = LocalDate.parse(value.take(10))
        date.format(DateTimeFormatter.ofPattern("dd MMM yyyy"))
    } catch (e: Exception) {
        value
    }
}

private fun formatTime(value: String): String {
    return value.take(5)
}

@Composable
private fun AdminCasualMatchDetailsTopBar(
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
                text = stringResource(R.string.admin_casual_match_details_title),
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
private fun AdminCasualMatchDetailsBottomBar(
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
        BottomMatchDetailsItem(AppIcons.Home, stringResource(R.string.admin_nav_home).uppercase(), selected == "home", onHomeClick)
        BottomMatchDetailsItem(AppIcons.Tournaments, stringResource(R.string.admin_nav_tournaments).uppercase(), selected == "tournaments", onTournamentsClick)
        BottomMatchDetailsItem(AppIcons.Games, stringResource(R.string.admin_nav_matches).uppercase(), selected == "matches", onMatchesClick)
        BottomMatchDetailsItem(AppIcons.Teams, stringResource(R.string.admin_nav_teams).uppercase(), selected == "teams", onTeamsClick)
        BottomMatchDetailsItem(AppIcons.Profile, stringResource(R.string.admin_nav_profile).uppercase(), selected == "profile", onProfileClick)
    }
}

@Composable
private fun BottomMatchDetailsItem(
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