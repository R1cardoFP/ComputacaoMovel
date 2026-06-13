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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.res.stringResource
import com.example.trabalhocm.R
import com.example.trabalhocm.data.model.AdminManageRegistrationData
import com.example.trabalhocm.data.model.AdminRegistrationTeam
import com.example.trabalhocm.data.repository.AdminRegistrationRepository
import com.example.trabalhocm.ui.theme.AppIcons
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import com.example.trabalhocm.ui.theme.CardBg
import com.example.trabalhocm.ui.theme.ErrorRed
import com.example.trabalhocm.ui.theme.InputBg
import com.example.trabalhocm.ui.theme.LightBlueBadge
import com.example.trabalhocm.ui.theme.LightRedBadge
import com.example.trabalhocm.ui.theme.PrimaryBlue
import com.example.trabalhocm.ui.theme.TextGray
import kotlinx.coroutines.launch

@Composable
fun AdminManageRegistrationScreen(
    tournamentId: String,
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onInviteTeamClick: (String) -> Unit = {},
    onTeamDetailsClick: (String) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { AdminRegistrationRepository() }
    val scope = rememberCoroutineScope()

    var data by remember { mutableStateOf<AdminManageRegistrationData?>(null) }
    var searchText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("pending") }
    var isLoading by remember { mutableStateOf(true) }
    var actionMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var refreshKey by remember { mutableStateOf(0) }
    var actionMessageIsError by remember { mutableStateOf(false) }

    val errorLoadingRegistrationsText = stringResource(R.string.admin_manage_registration_error_loading)
    val rejectedSuccessText = stringResource(R.string.admin_manage_registration_rejected_success)
    val approvedSuccessText = stringResource(R.string.admin_manage_registration_approved_success)
    val removedSuccessText = stringResource(R.string.admin_manage_registration_removed_success)
    val rejectErrorText = stringResource(R.string.admin_manage_registration_reject_error)
    val approveErrorText = stringResource(R.string.admin_manage_registration_approve_error)
    val removeErrorText = stringResource(R.string.admin_manage_registration_remove_error)

    LaunchedEffect(tournamentId, refreshKey) {
        isLoading = true
        errorMessage = ""

        repository.carregarGestaoInscricoes(tournamentId)
            .onSuccess {
                data = it
            }
            .onFailure {
                errorMessage = "$errorLoadingRegistrationsText: ${it.message}"
            }

        isLoading = false
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            ManageRegistrationTopBar(
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick
            )
        },
        bottomBar = {
            ManageRegistrationBottomBar(
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

                val pendingFiltered = screenData.pendingTeams.filter {
                    it.teamName.contains(searchText, ignoreCase = true) ||
                            it.captainName.contains(searchText, ignoreCase = true)
                }

                val approvedFiltered = screenData.approvedTeams.filter {
                    it.teamName.contains(searchText, ignoreCase = true) ||
                            it.captainName.contains(searchText, ignoreCase = true)
                }

                val rejectedFiltered = screenData.rejectedTeams.filter {
                    it.teamName.contains(searchText, ignoreCase = true) ||
                            it.captainName.contains(searchText, ignoreCase = true)
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
                                text = stringResource(R.string.admin_manage_registration_console).uppercase(),
                                color = BrandGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = stringResource(R.string.admin_manage_registration_title),
                                color = BrandBlue,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            Text(
                                text = stringResource(R.string.admin_manage_registration_description),
                                color = TextGray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    item {
                        TournamentRegistrationHero(data = screenData)
                    }

                    item {
                        Button(
                            onClick = {
                                onInviteTeamClick(tournamentId)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(43.dp),
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                                contentColor = BrandWhite
                            )
                        ) {
                            Icon(
                                imageVector = AppIcons.Add,
                                contentDescription = stringResource(R.string.admin_manage_registration_invite_team_content_description),
                                tint = BrandWhite,
                                modifier = Modifier.size(17.dp)
                            )

                            Spacer(modifier = Modifier.width(7.dp))

                            Text(
                                text = stringResource(R.string.admin_manage_registration_invite_team).uppercase(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    item {
                        RegistrationSearchBox(
                            value = searchText,
                            onValueChange = {
                                searchText = it
                            }
                        )
                    }

                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RegistrationTabChip(
                                text = stringResource(R.string.admin_manage_registration_tab_all),
                                count = screenData.pendingTeams.size +
                                        screenData.approvedTeams.size +
                                        screenData.rejectedTeams.size,
                                selected = selectedTab == "all"
                            ) {
                                selectedTab = "all"
                            }

                            RegistrationTabChip(
                                text = stringResource(R.string.admin_manage_registration_tab_pending),
                                count = screenData.pendingTeams.size,
                                selected = selectedTab == "pending"
                            ) {
                                selectedTab = "pending"
                            }

                            RegistrationTabChip(
                                text = stringResource(R.string.admin_manage_registration_tab_approved),
                                count = screenData.approvedTeams.size,
                                selected = selectedTab == "approved"
                            ) {
                                selectedTab = "approved"
                            }
                        }
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

                    if (selectedTab == "all" || selectedTab == "pending") {
                        item {
                            SectionTitle(stringResource(R.string.admin_manage_registration_pending_approval, pendingFiltered.size).uppercase())
                        }

                        if (pendingFiltered.isEmpty()) {
                            item {
                                EmptyRegistrationCard(stringResource(R.string.admin_manage_registration_empty_pending))
                            }
                        }

                        items(pendingFiltered) { team ->
                            PendingTeamCard(
                                team = team,
                                onRejectClick = {
                                    scope.launch {
                                        repository.rejeitarInscricao(team.registrationId)
                                            .onSuccess {
                                                actionMessage = rejectedSuccessText
                                                actionMessageIsError = false
                                                refreshKey++
                                            }
                                            .onFailure {
                                                actionMessage = "$rejectErrorText: ${it.message}"
                                                actionMessageIsError = true
                                            }
                                    }
                                },
                                onApproveClick = {
                                    scope.launch {
                                        repository.aprovarInscricao(team.registrationId)
                                            .onSuccess {
                                                actionMessage = approvedSuccessText
                                                actionMessageIsError = false
                                                refreshKey++
                                            }
                                            .onFailure {
                                                actionMessage = "$approveErrorText: ${it.message}"
                                                actionMessageIsError = true
                                            }
                                    }
                                },
                                onDetailsClick = {
                                    onTeamDetailsClick(team.teamId)
                                }
                            )
                        }
                    }

                    if (selectedTab == "all" || selectedTab == "approved") {
                        item {
                            SectionTitle(stringResource(R.string.admin_manage_registration_approved_teams, approvedFiltered.size).uppercase())
                        }

                        if (approvedFiltered.isEmpty()) {
                            item {
                                EmptyRegistrationCard(stringResource(R.string.admin_manage_registration_empty_approved))
                            }
                        }

                        items(approvedFiltered) { team ->
                            ApprovedTeamCard(
                                team = team,
                                onRemoveClick = {
                                    scope.launch {
                                        repository.removerInscricao(team.registrationId)
                                            .onSuccess {
                                                actionMessage = removedSuccessText
                                                actionMessageIsError = false
                                                refreshKey++
                                            }
                                            .onFailure {
                                                actionMessage = "$removeErrorText: ${it.message}"
                                                actionMessageIsError = true
                                            }
                                    }
                                }
                            )
                        }
                    }

                    if (selectedTab == "all") {
                        item {
                            SectionTitle(stringResource(R.string.admin_manage_registration_rejected_teams, rejectedFiltered.size).uppercase())
                        }

                        if (rejectedFiltered.isEmpty()) {
                            item {
                                EmptyRegistrationCard(stringResource(R.string.admin_manage_registration_empty_rejected))
                            }
                        }

                        items(rejectedFiltered) { team ->
                            RejectedTeamCard(team = team)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ManageRegistrationTopBar(
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
                text = stringResource(R.string.admin_manage_registration_title),
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
private fun TournamentRegistrationHero(data: AdminManageRegistrationData) {
    val progressValue = if (data.maxTeams > 0) {
        data.registeredTeams.toFloat() / data.maxTeams.toFloat()
    } else {
        0f
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = data.tournamentName,
                        color = BrandWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = stringResource(R.string.admin_manage_registration_teams_registered).uppercase(),
                        color = Color(0xFFB9C4D8),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = data.registeredTeams.toString(),
                            color = BrandWhite,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "/${data.maxTeams}",
                            color = Color(0xFFB9C4D8),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                SmallStatusBadge(data.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progressValue },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = BrandGreen,
                trackColor = Color(0xFF31445F)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = data.registrationClosesText,
                color = Color(0xFFFFC857),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SmallStatusBadge(status: String) {
    val text = when {
        status.contains("aberto", ignoreCase = true) || status.contains("open", ignoreCase = true) ->
            stringResource(R.string.admin_status_open).uppercase()

        status.contains("decorrer", ignoreCase = true) || status.contains("live", ignoreCase = true) ->
            stringResource(R.string.admin_status_live).uppercase()

        status.contains("terminado", ignoreCase = true) || status.contains("completed", ignoreCase = true) || status.contains("archived", ignoreCase = true) ->
            stringResource(R.string.admin_status_completed).uppercase()

        status.contains("cancelado", ignoreCase = true) || status.contains("canceled", ignoreCase = true) ->
            stringResource(R.string.admin_status_cancelled).uppercase()

        else -> status.uppercase()
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(BrandGreen)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = BrandWhite,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RegistrationSearchBox(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = stringResource(R.string.admin_manage_registration_search_placeholder),
                color = TextGray,
                fontSize = 12.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = AppIcons.Search,
                contentDescription = stringResource(R.string.admin_manage_registration_search_content_description),
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
private fun RegistrationTabChip(
    text: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (selected) PrimaryBlue else InputBg)
            .clickable {
                onClick()
            }
            .padding(horizontal = 11.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$text  $count",
            color = if (selected) BrandWhite else PrimaryBlue,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = TextGray,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp
    )
}

@Composable
private fun EmptyRegistrationCard(text: String) {
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
private fun PendingTeamCard(
    team: AdminRegistrationTeam,
    onRejectClick: () -> Unit,
    onApproveClick: () -> Unit,
    onDetailsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(165.dp)
                    .background(ErrorRed)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(13.dp)
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
                            text = team.teamName,
                            color = BrandBlue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = stringResource(R.string.admin_manage_registration_captain_division, team.captainName, team.division),
                            color = TextGray,
                            fontSize = 10.sp
                        )
                    }

                    MiniBadge(
                        text = stringResource(R.string.admin_manage_registration_status_pending),
                        background = Color(0xFFFFF7D6),
                        textColor = Color(0xFFB45309)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SmallInfo(
                        label = stringResource(R.string.admin_manage_registration_players).uppercase(),
                        value = "${team.playersCount}/${team.maxPlayers}"
                    )

                    SmallInfo(
                        label = stringResource(R.string.admin_manage_registration_win_rate).uppercase(),
                        value = team.winRate
                    )
                }

                Spacer(modifier = Modifier.height(7.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SmallInfo(
                        label = stringResource(R.string.admin_manage_registration_applied).uppercase(),
                        value = team.appliedAgo
                    )

                    PaymentText(status = team.paymentStatus)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onRejectClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightRedBadge,
                            contentColor = ErrorRed
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "×  ${stringResource(R.string.admin_manage_registration_reject).uppercase()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onApproveClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandGreen,
                            contentColor = BrandWhite
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "✓  ${stringResource(R.string.admin_manage_registration_approve).uppercase()}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDetailsClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, PrimaryBlue),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandWhite,
                        contentColor = PrimaryBlue
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.admin_manage_registration_details).uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ApprovedTeamCard(
    team: AdminRegistrationTeam,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(76.dp)
                    .background(BrandGreen)
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = team.teamName,
                        color = BrandBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MiniBadge(
                            text = stringResource(R.string.admin_manage_registration_status_confirmed),
                            background = Color(0xFFEAF8F5),
                            textColor = BrandGreen
                        )

                        MiniBadge(
                            text = paymentLabel(team.paymentStatus),
                            background = LightBlueBadge,
                            textColor = PrimaryBlue
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(LightRedBadge)
                        .clickable {
                            onRemoveClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.Delete,
                        contentDescription = stringResource(R.string.admin_manage_registration_remove_team_content_description),
                        tint = ErrorRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RejectedTeamCard(team: AdminRegistrationTeam) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(70.dp)
                    .background(ErrorRed)
            )

            Column(
                modifier = Modifier.padding(13.dp)
            ) {
                Text(
                    text = team.teamName,
                    color = BrandBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                MiniBadge(
                    text = stringResource(R.string.admin_manage_registration_status_rejected),
                    background = LightRedBadge,
                    textColor = ErrorRed
                )
            }
        }
    }
}

@Composable
private fun SmallInfo(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            color = TextGray,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
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
private fun PaymentText(status: String) {
    val paid = status.equals("pago", ignoreCase = true)

    Column {
        Text(
            text = stringResource(R.string.admin_manage_registration_payment).uppercase(),
            color = TextGray,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = if (paid) "✓ ${stringResource(R.string.admin_manage_registration_paid)}" else "× ${stringResource(R.string.admin_manage_registration_unpaid)}",
            color = if (paid) BrandGreen else ErrorRed,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MiniBadge(
    text: String,
    background: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = textColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun paymentLabel(status: String): String {
    return when {
        status.equals("pago", ignoreCase = true) -> stringResource(R.string.admin_manage_registration_paid).uppercase()
        status.equals("nao_pago", ignoreCase = true) -> stringResource(R.string.admin_manage_registration_unpaid).uppercase()
        else -> stringResource(R.string.admin_manage_registration_status_pending).uppercase()
    }
}

@Composable
private fun ManageRegistrationBottomBar(
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
        BottomManageItem(AppIcons.Home, stringResource(R.string.admin_nav_home).uppercase(), selected == "home", onHomeClick)
        BottomManageItem(AppIcons.Tournaments, stringResource(R.string.admin_nav_tournaments).uppercase(), selected == "tournaments", onTournamentsClick)
        BottomManageItem(AppIcons.Games, stringResource(R.string.admin_nav_matches).uppercase(), selected == "matches", onMatchesClick)
        BottomManageItem(AppIcons.Teams, stringResource(R.string.admin_nav_teams).uppercase(), selected == "teams", onTeamsClick)
        BottomManageItem(AppIcons.Profile, stringResource(R.string.admin_nav_profile).uppercase(), selected == "profile", onProfileClick)
    }
}

@Composable
private fun BottomManageItem(
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
fun AdminManageRegistrationScreenPreview() {
    AdminManageRegistrationScreen(
        tournamentId = "1"
    )
}