package com.example.trabalhocm.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.model.AdminPlayerDetails
import com.example.trabalhocm.data.repository.AdminPlayerDetailsRepository
import com.example.trabalhocm.data.repository.AuthRepository
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
fun AdminPlayerDetailsScreen(
    playerId: String,
    teamId: String? = null,
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSuspendUserClick: (String) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { AdminPlayerDetailsRepository() }
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()

    var player by remember { mutableStateOf<AdminPlayerDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var actionMessage by remember { mutableStateOf("") }
    var actionMessageIsError by remember { mutableStateOf(false) }
    var refreshKey by remember { mutableStateOf(0) }

    val errorLoadingPlayerText = stringResource(R.string.admin_player_details_error_loading)
    val resetEmailSentText = stringResource(R.string.admin_player_details_reset_email_sent)
    val resetEmailErrorText = stringResource(R.string.admin_player_details_reset_email_error)
    val accountReactivatedSuccessText = stringResource(R.string.admin_player_details_reactivated_success)
    val accountReactivatedErrorText = stringResource(R.string.admin_player_details_reactivated_error)
    val accountSuspendedSuccessText = stringResource(R.string.admin_player_details_suspended_success)
    val accountSuspendedErrorText = stringResource(R.string.admin_player_details_suspended_error)

    LaunchedEffect(playerId, teamId, refreshKey) {
        isLoading = true
        errorMessage = ""

        repository.obterDetalhesJogador(playerId, teamId)
            .onSuccess {
                player = it
            }
            .onFailure {
                errorMessage = "$errorLoadingPlayerText: ${it.message}"
            }

        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.admin_player_details_top_title),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = AppIcons.Back,
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
                AdminPlayerDetailsErrorState(
                    innerPadding = innerPadding,
                    message = errorMessage
                )
            }

            player != null -> {
                AdminPlayerDetailsContent(
                    player = player!!,
                    innerPadding = innerPadding,
                    actionMessage = actionMessage,
                    actionMessageIsError = actionMessageIsError,
                    onResetPasswordClick = { email ->
                        scope.launch {
                            authRepository.recuperarPassword(email)
                                .onSuccess {
                                    actionMessage = "$resetEmailSentText $email."
                                    actionMessageIsError = false
                                }
                                .onFailure {
                                    actionMessage = "$resetEmailErrorText: ${it.message}"
                                    actionMessageIsError = true
                                }
                        }
                    },
                    onSuspendUserClick = { id ->
                        scope.launch {
                            val currentPlayer = player

                            if (currentPlayer?.suspended == true) {
                                repository.reativarUtilizador(id)
                                    .onSuccess {
                                        actionMessage = accountReactivatedSuccessText
                                        actionMessageIsError = false
                                        refreshKey++
                                    }
                                    .onFailure {
                                        actionMessage = "$accountReactivatedErrorText: ${it.message}"
                                        actionMessageIsError = true
                                    }
                            } else {
                                repository.suspenderUtilizador(id)
                                    .onSuccess {
                                        actionMessage = accountSuspendedSuccessText
                                        actionMessageIsError = false
                                        refreshKey++
                                    }
                                    .onFailure {
                                        actionMessage = "$accountSuspendedErrorText: ${it.message}"
                                        actionMessageIsError = true
                                    }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AdminPlayerDetailsContent(
    player: AdminPlayerDetails,
    innerPadding: PaddingValues,
    actionMessage: String,
    actionMessageIsError: Boolean,
    onResetPasswordClick: (String) -> Unit,
    onSuspendUserClick: (String) -> Unit
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
            PlayerProfileHeroCard(player = player)
        }

        item {
            AccountInformationCard(player = player)
        }

        item {
            PlayerSeasonStatsCard(player = player)
        }

        if (actionMessage.isNotBlank()) {
            item {
                AdminPlayerDetailsMessageCard(
                    message = actionMessage,
                    isError = actionMessageIsError
                )
            }
        }

        item {
            AdminActionsCard(
                player = player,
                onResetPasswordClick = onResetPasswordClick,
                onSuspendUserClick = onSuspendUserClick
            )
        }
    }
}

@Composable
private fun PlayerProfileHeroCard(player: AdminPlayerDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                StatusBadge(
                    text = if (player.suspended) {
                        stringResource(R.string.admin_player_details_suspended).uppercase()
                    } else {
                        stringResource(R.string.admin_player_details_active).uppercase()
                    },
                    background = if (player.suspended) Color(0xFFFEF3C7) else Color(0xFFEAF8F5),
                    textColor = if (player.suspended) Color(0xFFB45309) else TealGreen
                )
            }

            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.initials,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = player.nome,
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(
                    text = player.position.uppercase(),
                    background = Color.White.copy(alpha = 0.14f),
                    textColor = Color.White
                )

                StatusBadge(
                    text = player.equipa.uppercase(),
                    background = Color.White.copy(alpha = 0.14f),
                    textColor = Color.White
                )
            }

            if (player.equipas.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = stringResource(R.string.admin_player_details_current_teams).uppercase(),
                    color = Color.White.copy(alpha = 0.65f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = player.equipas.joinToString(" · "),
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlayerHeroMetric(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.admin_player_details_age),
                    value = player.age
                )

                PlayerHeroMetric(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.admin_player_details_height),
                    value = player.height
                )

                PlayerHeroMetric(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.admin_player_details_number),
                    value = player.number
                )
            }
        }
    }
}

@Composable
private fun PlayerHeroMetric(
    modifier: Modifier,
    label: String,
    value: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label.uppercase(),
                color = Color.White.copy(alpha = 0.68f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.7.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
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
private fun AccountInformationCard(player: AdminPlayerDetails) {
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
                Text(
                    text = stringResource(R.string.admin_player_details_account_information),
                    color = DarkBlue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                StatusBadge(
                    text = if (player.suspended) {
                        stringResource(R.string.admin_player_details_suspended).uppercase()
                    } else {
                        player.accountStatus.uppercase()
                    },
                    background = if (player.suspended) Color(0xFFFEF3C7) else Color(0xFFEAF8F5),
                    textColor = if (player.suspended) Color(0xFFB45309) else TealGreen
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            AccountInfoRow(stringResource(R.string.admin_player_details_email), player.email)
            AccountInfoRow(stringResource(R.string.admin_player_details_user_id), "#${player.id.take(8).uppercase()}")
            AccountInfoRow(stringResource(R.string.admin_player_details_member_since), player.memberSince)
            AccountInfoRow(stringResource(R.string.admin_player_details_last_active), player.lastActive)
        }
    }
}

@Composable
private fun AccountInfoRow(
    label: String,
    value: String
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = TextGray,
                fontSize = 13.sp,
                modifier = Modifier.weight(0.42f)
            )

            Text(
                text = value,
                color = DarkBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(0.58f)
            )
        }

        HorizontalDivider(color = InputBg)
    }
}

@Composable
private fun PlayerSeasonStatsCard(player: AdminPlayerDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = stringResource(R.string.admin_player_details_season_stats),
                color = DarkBlue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = InputBg
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.admin_player_details_points).uppercase(),
                            color = TextGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Total acumulado na época",
                            color = TextGray,
                            fontSize = 12.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = player.points.toString(),
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminActionsCard(
    player: AdminPlayerDetails,
    onResetPasswordClick: (String) -> Unit,
    onSuspendUserClick: (String) -> Unit
) {
    val suspendColor = if (player.suspended) TealGreen else Color(0xFFB45309)
    val suspendText = if (player.suspended) {
        stringResource(R.string.admin_player_details_reactivate_account).uppercase()
    } else {
        stringResource(R.string.admin_player_details_suspend_account).uppercase()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF8F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, TealGreen.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.Security,
                        contentDescription = stringResource(R.string.admin_player_details_admin_actions),
                        tint = TealGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = stringResource(R.string.admin_player_details_admin_actions),
                        color = DarkBlue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Gestão de segurança da conta",
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onResetPasswordClick(player.email)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = DarkBlue
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = stringResource(R.string.admin_player_details_send_reset_email).uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    onSuspendUserClick(player.id)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, suspendColor),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = suspendColor
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = suspendText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AdminPlayerDetailsMessageCard(
    message: String,
    isError: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) Color(0xFFFFEBEE) else Color(0xFFEAF8F5)
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
private fun AdminPlayerDetailsErrorState(
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
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Erro ao carregar jogador",
                    color = ErrorRed,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = message,
                    color = ErrorRed,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(
    text: String,
    background: Color,
    textColor: Color
) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = background
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminPlayerDetailsScreenPreview() {
    AdminPlayerDetailsScreen(
        playerId = "9ffdf3d8-96b5-46ea-b36f-181b490602f6"
    )
}
