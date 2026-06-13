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
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.trabalhocm.data.model.AdminCasualMatchDetails
import com.example.trabalhocm.data.model.AdminCasualMatchPlayer
import com.example.trabalhocm.data.repository.AdminCasualMatchDetailsRepository
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.admin_casual_match_details_title),
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
                                    .background(ErrorRed.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = AppIcons.Cancel,
                                    contentDescription = null,
                                    tint = ErrorRed,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = errorMessage,
                                color = ErrorRed,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 18.sp
                            )
                        }
                    }
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
            start = 24.dp,
            end = 24.dp,
            top = 20.dp,
            bottom = 28.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            MatchHeroCard(match = match)
        }

        item {
            MatchQuickInfoCard(match = match)
        }

        item {
            HostCard(match = match)
        }

        item {
            JoinedPlayersCard(match = match)
        }

        if (actionMessage.isNotBlank()) {
            item {
                ActionMessageCard(
                    message = actionMessage,
                    isError = actionMessageIsError
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
private fun MatchHeroCard(match: AdminCasualMatchDetails) {
    val progress = if (match.maxPlayers > 0) {
        (match.acceptedPlayers.toFloat() / match.maxPlayers.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.admin_casual_match_details_console).uppercase(),
                        color = TealGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.6.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = match.title,
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 30.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                StatusBadge(status = match.estado)
            }

            if (match.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = match.description,
                    color = Color.White.copy(alpha = 0.78f),
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HeroMiniStat(
                    label = stringResource(R.string.admin_casual_match_details_date),
                    value = formatDate(match.date),
                    modifier = Modifier.weight(1f)
                )

                HeroMiniStat(
                    label = stringResource(R.string.admin_casual_match_details_time),
                    value = formatTime(match.time),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.admin_casual_match_details_joined_players),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "${match.acceptedPlayers}/${match.maxPlayers}",
                    color = TealGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(50.dp)),
                color = TealGreen,
                trackColor = Color.White.copy(alpha = 0.18f)
            )
        }
    }
}

@Composable
private fun HeroMiniStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.1f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                text = label.uppercase(),
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MatchQuickInfoCard(match: AdminCasualMatchDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            SectionHeader(
                icon = AppIcons.Calendar,
                title = stringResource(R.string.admin_casual_match_details_schedule)
            )

            Spacer(modifier = Modifier.height(14.dp))

            DetailsRow(
                label = stringResource(R.string.admin_casual_match_details_date),
                value = formatDate(match.date)
            )
            DetailsRow(
                label = stringResource(R.string.admin_casual_match_details_time),
                value = formatTime(match.time)
            )
            DetailsRow(
                label = stringResource(R.string.admin_casual_match_details_location),
                value = match.local
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = InputBg
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.admin_casual_match_details_status),
                    color = TextGray,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                StatusBadge(status = match.estado, darkBackground = false)
            }
        }
    }
}

@Composable
private fun HostCard(match: AdminCasualMatchDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            SectionHeader(
                icon = AppIcons.Profile,
                title = stringResource(R.string.admin_casual_match_details_host)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(InputBg, RoundedCornerShape(20.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = match.hostInitials,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = match.hostName,
                        color = DarkBlue,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = stringResource(R.string.admin_casual_match_details_hosted_matches, match.hostedMatchesCount),
                        color = TextGray,
                        fontSize = 12.sp
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionHeader(
                    icon = AppIcons.Teams,
                    title = stringResource(R.string.admin_casual_match_details_joined_players)
                )

                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = PrimaryBlue.copy(alpha = 0.1f)
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        text = "${match.acceptedPlayers}/${match.maxPlayers}",
                        color = PrimaryBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (visiblePlayers.isEmpty()) {
                EmptyPlayersBox()
            } else {
                visiblePlayers.forEachIndexed { index, player ->
                    JoinedPlayerRow(player = player)
                    if (index != visiblePlayers.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = InputBg
                        )
                    }
                }
            }

            if (match.players.size > 5) {
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedButton(
                    onClick = {
                        showAllPlayers = !showAllPlayers
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, InputBg),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = DarkBlue
                    )
                ) {
                    Text(
                        text = if (showAllPlayers) {
                            stringResource(R.string.admin_casual_match_details_show_less_players).uppercase()
                        } else {
                            stringResource(R.string.admin_casual_match_details_load_more_players).uppercase()
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyPlayersBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(InputBg, RoundedCornerShape(18.dp))
            .padding(18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.admin_casual_match_details_no_players),
            color = TextGray,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun JoinedPlayerRow(player: AdminCasualMatchPlayer) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(TealGreen),
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

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = player.email,
                color = TextGray,
                fontSize = 11.sp,
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = TealGreen.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, TealGreen.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(TealGreen.copy(alpha = 0.16f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.Security,
                        contentDescription = null,
                        tint = TealGreen,
                        modifier = Modifier.size(21.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = stringResource(R.string.admin_casual_match_details_admin_actions),
                        color = DarkBlue,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = stringResource(R.string.admin_casual_match_details_console),
                        color = TextGray,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onEditMatchClick(match.id)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkBlue,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    imageVector = AppIcons.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.admin_casual_match_details_edit_match).uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    onCancelMatchClick(match.id)
                },
                enabled = !isCanceled,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCanceled) TextGray else ErrorRed,
                    contentColor = Color.White,
                    disabledContainerColor = TextGray.copy(alpha = 0.55f),
                    disabledContentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    imageVector = AppIcons.Cancel,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

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
private fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(PrimaryBlue.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBlue,
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
private fun DetailsRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextGray,
            fontSize = 13.sp,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = value,
            color = DarkBlue,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ActionMessageCard(
    message: String,
    isError: Boolean
) {
    val color = if (isError) ErrorRed else TealGreen
    val background = if (isError) ErrorRed.copy(alpha = 0.1f) else TealGreen.copy(alpha = 0.1f)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = background,
        border = BorderStroke(1.dp, color.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isError) AppIcons.Cancel else AppIcons.Confirm,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
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
}

@Composable
private fun StatusBadge(
    status: String,
    darkBackground: Boolean = true
) {
    val normalized = status.lowercase()

    val color = when (normalized) {
        "aberta" -> TealGreen
        "fechada" -> WarningYellow
        "cancelada" -> ErrorRed
        else -> TextGray
    }

    val background = if (darkBackground) {
        Color.White.copy(alpha = 0.12f)
    } else {
        when (normalized) {
            "aberta" -> TealGreen.copy(alpha = 0.1f)
            "fechada" -> WarningYellow.copy(alpha = 0.14f)
            "cancelada" -> ErrorRed.copy(alpha = 0.12f)
            else -> InputBg
        }
    }

    val text = when (normalized) {
        "aberta" -> stringResource(R.string.admin_casual_match_details_status_open).uppercase()
        "fechada" -> stringResource(R.string.admin_casual_match_details_status_closed).uppercase()
        "cancelada" -> stringResource(R.string.admin_casual_match_details_status_canceled).uppercase()
        else -> status.uppercase()
    }

    Surface(
        shape = RoundedCornerShape(50.dp),
        color = background
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            text = text,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
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
