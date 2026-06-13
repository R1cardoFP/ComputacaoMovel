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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.trabalhocm.R
import com.example.trabalhocm.data.model.AdminInviteTeam
import com.example.trabalhocm.data.model.AdminInviteTeamsData
import com.example.trabalhocm.data.repository.AdminInviteTeamRepository
import com.example.trabalhocm.ui.theme.AppIcons
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import com.example.trabalhocm.ui.theme.CardBg
import com.example.trabalhocm.ui.theme.ErrorRed
import com.example.trabalhocm.ui.theme.InputBg
import com.example.trabalhocm.ui.theme.LightBlueBadge
import com.example.trabalhocm.ui.theme.PrimaryBlue
import com.example.trabalhocm.ui.theme.TextGray
import kotlinx.coroutines.launch

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
        containerColor = BgLight,
        topBar = {
            InviteTeamsTopBar(
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick
            )
        },
        bottomBar = {
            InviteTeamsBottomBar(
                selected = "tournaments",
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
                        start = 18.dp,
                        end = 18.dp,
                        top = 18.dp,
                        bottom = 28.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Column {
                            Text(
                                text = stringResource(R.string.admin_invite_teams_console).uppercase(),
                                color = BrandGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = stringResource(R.string.admin_invite_teams_title),
                                color = BrandBlue,
                                fontSize = 27.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = stringResource(R.string.admin_invite_teams_description, screenData.tournamentName),
                                color = TextGray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    item {
                        InviteTeamsHero(data = screenData)
                    }

                    item {
                        SectionTitleInvite(stringResource(R.string.admin_invite_teams_search_teams).uppercase())
                    }

                    item {
                        InviteTeamsSearchBox(
                            value = searchText,
                            onValueChange = {
                                searchText = it
                            }
                        )
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
                        SectionTitleInvite(stringResource(R.string.admin_invite_teams_suggested_teams).uppercase())
                    }

                    if (filteredTeams.isEmpty()) {
                        item {
                            EmptyInviteCard(stringResource(R.string.admin_invite_teams_no_teams_available))
                        }
                    }

                    items(filteredTeams) { team ->
                        InviteTeamCard(
                            team = team,
                            onInviteClick = {
                                scope.launch {
                                    repository.convidarEquipa(
                                        tournamentId = tournamentId,
                                        teamId = team.teamId
                                    )
                                        .onSuccess {
                                            actionMessage = context.getString(R.string.admin_invite_teams_invitation_sent_to, team.teamName)
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

                    item {
                        SectionTitleInvite(stringResource(R.string.admin_invite_teams_sent_invitations_count, screenData.sentInvitations.size).uppercase())
                    }

                    if (screenData.sentInvitations.isEmpty()) {
                        item {
                            EmptyInviteCard(stringResource(R.string.admin_invite_teams_no_invitations_sent))
                        }
                    }

                    items(screenData.sentInvitations) { team ->
                        SentInvitationCard(team = team)
                    }
                }
            }
        }
    }
}

@Composable
private fun InviteTeamsTopBar(
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
                text = stringResource(R.string.admin_invite_teams_title),
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
private fun InviteTeamsHero(data: AdminInviteTeamsData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = stringResource(R.string.admin_invite_teams_slots_remaining).uppercase(),
                    color = Color(0xFFB9C4D8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = data.slotsRemaining.toString(),
                        color = BrandWhite,
                        fontSize = 31.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(5.dp))

                    Text(
                        text = "/${data.maxTeams}",
                        color = Color(0xFFB9C4D8),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(BrandWhite)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.admin_invite_teams_invite_only).uppercase(),
                    color = BrandGreen,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SectionTitleInvite(text: String) {
    Text(
        text = text,
        color = TextGray,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp
    )
}

@Composable
private fun InviteTeamsSearchBox(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = stringResource(R.string.admin_invite_teams_search_placeholder),
                color = TextGray,
                fontSize = 12.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = AppIcons.Search,
                contentDescription = stringResource(R.string.admin_invite_teams_search_content_description),
                tint = TextGray,
                modifier = Modifier.size(18.dp)
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(9.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = BrandWhite,
            unfocusedContainerColor = BrandWhite,
            focusedBorderColor = InputBg,
            unfocusedBorderColor = InputBg,
            focusedTextColor = BrandBlue,
            unfocusedTextColor = BrandBlue,
            cursorColor = BrandGreen
        )
    )
}

@Composable
private fun InviteTeamCard(
    team: AdminInviteTeam,
    onInviteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamInviteAvatar(team.teamName)

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = team.teamName,
                        color = BrandBlue,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = stringResource(R.string.admin_invite_teams_captain_division, team.captainName, team.division),
                        color = TextGray,
                        fontSize = 10.sp
                    )
                }
            }

            Button(
                onClick = onInviteClick,
                enabled = !team.isInvited,
                modifier = Modifier
                    .height(36.dp)
                    .width(82.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (team.isInvited) InputBg else BrandGreen,
                    contentColor = if (team.isInvited) TextGray else BrandWhite,
                    disabledContainerColor = InputBg,
                    disabledContentColor = TextGray
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = if (team.isInvited) stringResource(R.string.admin_invite_teams_invited).uppercase() else stringResource(R.string.admin_invite_teams_invite).uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SentInvitationCard(team: AdminInviteTeam) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = LightBlueBadge),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(13.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = AppIcons.Info,
                contentDescription = stringResource(R.string.admin_invite_teams_sent_invitation_content_description),
                tint = PrimaryBlue,
                modifier = Modifier.size(19.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = stringResource(
                    R.string.admin_invite_teams_invited_awaiting_response,
                    team.teamName,
                    team.invitedAgo.ifBlank { stringResource(R.string.admin_invite_teams_recently) }
                ),
                color = PrimaryBlue,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun EmptyInviteCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = text,
            color = TextGray,
            fontSize = 12.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun TeamInviteAvatar(name: String) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(avatarColor(name)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials(name),
            color = BrandWhite,
            fontSize = 13.sp,
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
        0 -> BrandGreen
        1 -> PrimaryBlue
        2 -> TextGray
        3 -> Color(0xFFEAB308)
        else -> ErrorRed
    }
}

@Composable
private fun InviteTeamsBottomBar(
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
        BottomInviteItem(AppIcons.Home, stringResource(R.string.admin_nav_home).uppercase(), selected == "home", onHomeClick)
        BottomInviteItem(AppIcons.Tournaments, stringResource(R.string.admin_nav_tournaments).uppercase(), selected == "tournaments", onTournamentsClick)
        BottomInviteItem(AppIcons.Games, stringResource(R.string.admin_nav_matches).uppercase(), selected == "matches", onMatchesClick)
        BottomInviteItem(AppIcons.Teams, stringResource(R.string.admin_nav_teams).uppercase(), selected == "teams", onTeamsClick)
        BottomInviteItem(AppIcons.Profile, stringResource(R.string.admin_nav_profile).uppercase(), selected == "profile", onProfileClick)
    }
}

@Composable
private fun BottomInviteItem(
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
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminInviteTeamsScreenPreview() {
    AdminInviteTeamsScreen(
        tournamentId = "1"
    )
}

