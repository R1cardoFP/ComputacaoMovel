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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.model.AdminInviteTeam
import com.example.trabalhocm.data.model.AdminInviteTeamsData
import com.example.trabalhocm.data.repository.AdminInviteTeamRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.AppIcons
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.BrandWhite
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
fun AdminInviteTeamsScreen(
    tournamentId: String,
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { AdminInviteTeamRepository() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var data by remember { mutableStateOf<AdminInviteTeamsData?>(null) }
    var searchText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var actionMessage by remember { mutableStateOf("") }
    var refreshKey by remember { mutableStateOf(0) }
    var actionMessageIsError by remember { mutableStateOf(false) }

    val errorLoadingTeamsText = stringResource(R.string.admin_invite_teams_error_loading)
    val inviteErrorText = stringResource(R.string.admin_invite_teams_error_inviting)

    LaunchedEffect(tournamentId, refreshKey) {
        isLoading = true
        errorMessage = ""

        repository.carregarEquipasParaConvite(tournamentId)
            .onSuccess {
                data = it
            }
            .onFailure {
                errorMessage = "$errorLoadingTeamsText: ${it.message}"
            }

        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.admin_invite_teams_title),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                selectedTab = "TOURNAMENTS",
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
                AdminInviteTeamsErrorState(
                    innerPadding = innerPadding,
                    message = errorMessage
                )
            }

            data != null -> {
                val screenData = data!!

                val filteredTeams = screenData.teams.filter { team ->
                    team.teamName.contains(searchText, ignoreCase = true) ||
                            team.captainName.contains(searchText, ignoreCase = true) ||
                            team.division.contains(searchText, ignoreCase = true)
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
                        AdminInviteTeamsHeroCard(
                            data = screenData,
                            availableTeamsCount = filteredTeams.size
                        )
                    }

                    item {
                        AdminInviteTeamsSummaryCard(data = screenData)
                    }

                    item {
                        AdminInviteTeamsSearchBox(
                            value = searchText,
                            onValueChange = { searchText = it }
                        )
                    }

                    if (actionMessage.isNotBlank()) {
                        item {
                            AdminInviteTeamsActionMessage(
                                message = actionMessage,
                                isError = actionMessageIsError
                            )
                        }
                    }

                    item {
                        AdminSectionHeader(
                            title = stringResource(R.string.admin_invite_teams_suggested_teams),
                            subtitle = stringResource(R.string.admin_invite_teams_search_teams)
                        )
                    }

                    if (filteredTeams.isEmpty()) {
                        item {
                            AdminInviteTeamsEmptyCard(
                                text = stringResource(R.string.admin_invite_teams_no_teams_available)
                            )
                        }
                    } else {
                        items(filteredTeams) { team ->
                            AdminInviteTeamCard(
                                team = team,
                                onInviteClick = {
                                    scope.launch {
                                        repository.convidarEquipa(
                                            tournamentId = tournamentId,
                                            teamId = team.teamId
                                        )
                                            .onSuccess {
                                                actionMessage = context.getString(
                                                    R.string.admin_invite_teams_invitation_sent_to,
                                                    team.teamName
                                                )
                                                actionMessageIsError = false
                                                refreshKey++
                                            }
                                            .onFailure {
                                                actionMessage = "$inviteErrorText: ${it.message}"
                                                actionMessageIsError = true
                                            }
                                    }
                                }
                            )
                        }
                    }

                    item {
                        AdminSectionHeader(
                            title = stringResource(
                                R.string.admin_invite_teams_sent_invitations_count,
                                screenData.sentInvitations.size
                            ),
                            subtitle = stringResource(R.string.admin_invite_teams_invite_only)
                        )
                    }

                    if (screenData.sentInvitations.isEmpty()) {
                        item {
                            AdminInviteTeamsEmptyCard(
                                text = stringResource(R.string.admin_invite_teams_no_invitations_sent)
                            )
                        }
                    } else {
                        items(screenData.sentInvitations) { team ->
                            AdminSentInvitationTeamCard(team = team)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminInviteTeamsHeroCard(
    data: AdminInviteTeamsData,
    availableTeamsCount: Int
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
                text = stringResource(R.string.admin_invite_teams_console).uppercase(),
                color = TealGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.4.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.admin_invite_teams_title),
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(
                    R.string.admin_invite_teams_description,
                    data.tournamentName
                ),
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 13.sp,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InviteTeamsStatPill(
                    value = data.slotsRemaining.toString(),
                    label = stringResource(R.string.admin_invite_teams_slots_remaining),
                    modifier = Modifier.weight(1f)
                )

                InviteTeamsStatPill(
                    value = availableTeamsCount.toString(),
                    label = stringResource(R.string.admin_invite_teams_suggested_teams),
                    modifier = Modifier.weight(1f)
                )

                InviteTeamsStatPill(
                    value = data.sentInvitations.size.toString(),
                    label = stringResource(
                        R.string.admin_invite_teams_sent_invitations_count,
                        data.sentInvitations.size
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun InviteTeamsStatPill(
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
private fun AdminInviteTeamsSummaryCard(data: AdminInviteTeamsData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(LightBlueBadge),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AppIcons.Tournaments,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = data.tournamentName,
                    color = DarkBlue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.admin_invite_teams_invite_only),
                    color = TextGray,
                    fontSize = 12.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(50),
                color = TealGreen.copy(alpha = 0.12f),
                border = BorderStroke(1.dp, TealGreen.copy(alpha = 0.22f))
            ) {
                Text(
                    text = "${data.slotsRemaining}/${data.maxTeams}",
                    color = TealGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                )
            }
        }
    }
}

@Composable
private fun AdminInviteTeamsSearchBox(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = stringResource(R.string.admin_invite_teams_search_placeholder),
                color = TextGray,
                fontSize = 13.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = AppIcons.Search,
                contentDescription = stringResource(R.string.admin_invite_teams_search_content_description),
                tint = TextGray,
                modifier = Modifier.size(19.dp)
            )
        },
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(18.dp),
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

@Composable
private fun AdminSectionHeader(
    title: String,
    subtitle: String
) {
    Column {
        Text(
            text = title.uppercase(),
            color = DarkBlue,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = subtitle,
            color = TextGray,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun AdminInviteTeamCard(
    team: AdminInviteTeam,
    onInviteClick: () -> Unit
) {
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
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamInviteAvatar(
                    name = team.teamName,
                    size = 50.dp
                )

                Spacer(modifier = Modifier.width(13.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = team.teamName,
                        color = DarkBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(
                            R.string.admin_invite_teams_captain_division,
                            team.captainName,
                            team.division
                        ),
                        color = TextGray,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                InviteStatusBadge(isInvited = team.isInvited)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onInviteClick,
                enabled = !team.isInvited,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealGreen,
                    contentColor = BrandWhite,
                    disabledContainerColor = InputBg,
                    disabledContentColor = TextGray
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = if (team.isInvited) {
                        stringResource(R.string.admin_invite_teams_invited)
                    } else {
                        stringResource(R.string.admin_invite_teams_invite)
                    }.uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun InviteStatusBadge(isInvited: Boolean) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (isInvited) InputBg else TealGreen.copy(alpha = 0.12f),
        border = BorderStroke(
            width = 1.dp,
            color = if (isInvited) TextGray.copy(alpha = 0.20f) else TealGreen.copy(alpha = 0.25f)
        )
    ) {
        Text(
            text = if (isInvited) {
                stringResource(R.string.admin_invite_teams_invited)
            } else {
                stringResource(R.string.admin_invite_teams_invite)
            }.uppercase(),
            color = if (isInvited) TextGray else TealGreen,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun AdminSentInvitationTeamCard(team: AdminInviteTeam) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(LightBlueBadge),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AppIcons.Info,
                    contentDescription = stringResource(R.string.admin_invite_teams_sent_invitation_content_description),
                    tint = PrimaryBlue,
                    modifier = Modifier.size(21.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = team.teamName,
                    color = DarkBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(
                        R.string.admin_invite_teams_invited_awaiting_response,
                        team.teamName,
                        team.invitedAgo.ifBlank { stringResource(R.string.admin_invite_teams_recently) }
                    ),
                    color = TextGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

@Composable
private fun AdminInviteTeamsActionMessage(
    message: String,
    isError: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) ErrorRed.copy(alpha = 0.10f) else TealGreen.copy(alpha = 0.10f)
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
                    .background(if (isError) ErrorRed.copy(alpha = 0.12f) else TealGreen.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AppIcons.Info,
                    contentDescription = null,
                    tint = if (isError) ErrorRed else TealGreen,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = message,
                color = if (isError) ErrorRed else TealGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun AdminInviteTeamsEmptyCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
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
                    imageVector = AppIcons.Info,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = text,
                color = TextGray,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun AdminInviteTeamsErrorState(
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
            shape = RoundedCornerShape(20.dp),
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
                        imageVector = AppIcons.Info,
                        contentDescription = null,
                        tint = ErrorRed,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = message,
                    color = ErrorRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun TeamInviteAvatar(
    name: String,
    size: Dp = 44.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(avatarColor(name)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials(name),
            color = BrandWhite,
            fontSize = if (size > 44.dp) 14.sp else 13.sp,
            fontWeight = FontWeight.Bold
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

private fun avatarColor(name: String): Color {
    return when (name.length % 5) {
        0 -> TealGreen
        1 -> PrimaryBlue
        2 -> TextGray
        3 -> Color(0xFFEAB308)
        else -> ErrorRed
    }
}

@Preview(showBackground = true)
@Composable
fun AdminInviteTeamsScreenPreview() {
    AdminInviteTeamsScreen(
        tournamentId = "1"
    )
}
