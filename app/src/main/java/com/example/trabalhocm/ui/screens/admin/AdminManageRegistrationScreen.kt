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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.model.AdminManageRegistrationData
import com.example.trabalhocm.data.model.AdminRegistrationTeam
import com.example.trabalhocm.data.repository.AdminRegistrationRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.AppIcons
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import com.example.trabalhocm.ui.theme.CardBg
import com.example.trabalhocm.ui.theme.DarkBlue
import com.example.trabalhocm.ui.theme.ErrorRed
import com.example.trabalhocm.ui.theme.InputBg
import com.example.trabalhocm.ui.theme.LightBlueBadge
import com.example.trabalhocm.ui.theme.LightRedBadge
import com.example.trabalhocm.ui.theme.PrimaryBlue
import com.example.trabalhocm.ui.theme.TealGreen
import com.example.trabalhocm.ui.theme.TextGray
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.admin_manage_registration_title),
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
                AdminManageRegistrationErrorState(
                    innerPadding = innerPadding,
                    message = errorMessage
                )
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
                        start = 24.dp,
                        end = 24.dp,
                        top = 20.dp,
                        bottom = 32.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        AdminManageRegistrationHeroCard(
                            data = screenData,
                            pendingCount = screenData.pendingTeams.size,
                            approvedCount = screenData.approvedTeams.size,
                            rejectedCount = screenData.rejectedTeams.size
                        )
                    }

                    item {
                        Button(
                            onClick = { onInviteTeamClick(tournamentId) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                                contentColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Icon(
                                imageVector = AppIcons.Add,
                                contentDescription = stringResource(R.string.admin_manage_registration_invite_team_content_description),
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = stringResource(R.string.admin_manage_registration_invite_team),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    item {
                        AdminManageRegistrationSearchBox(
                            value = searchText,
                            onValueChange = { searchText = it }
                        )
                    }

                    item {
                        AdminRegistrationTabs(
                            selectedTab = selectedTab,
                            allCount = screenData.pendingTeams.size + screenData.approvedTeams.size + screenData.rejectedTeams.size,
                            pendingCount = screenData.pendingTeams.size,
                            approvedCount = screenData.approvedTeams.size,
                            onTabSelected = { selectedTab = it }
                        )
                    }

                    if (actionMessage.isNotBlank()) {
                        item {
                            AdminManageRegistrationMessage(
                                message = actionMessage,
                                isError = actionMessageIsError
                            )
                        }
                    }

                    if (selectedTab == "all" || selectedTab == "pending") {
                        item {
                            AdminSectionHeader(
                                title = stringResource(R.string.admin_manage_registration_pending_approval, pendingFiltered.size),
                                subtitle = stringResource(R.string.admin_manage_registration_description)
                            )
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
                                onDetailsClick = { onTeamDetailsClick(team.teamId) }
                            )
                        }
                    }

                    if (selectedTab == "all" || selectedTab == "approved") {
                        item {
                            AdminSectionHeader(
                                title = stringResource(R.string.admin_manage_registration_approved_teams, approvedFiltered.size),
                                subtitle = stringResource(R.string.admin_manage_registration_teams_registered)
                            )
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
                            AdminSectionHeader(
                                title = stringResource(R.string.admin_manage_registration_rejected_teams, rejectedFiltered.size),
                                subtitle = stringResource(R.string.admin_manage_registration_status_rejected)
                            )
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
private fun AdminManageRegistrationErrorState(
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
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(LightRedBadge),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "!",
                        color = ErrorRed,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = message,
                    color = ErrorRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AdminManageRegistrationHeroCard(
    data: AdminManageRegistrationData,
    pendingCount: Int,
    approvedCount: Int,
    rejectedCount: Int
) {
    val progressValue = if (data.maxTeams > 0) {
        (data.registeredTeams.toFloat() / data.maxTeams.toFloat()).coerceIn(0f, 1f)
    } else {
        0f
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
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
                        text = stringResource(R.string.admin_manage_registration_console).uppercase(),
                        color = TealGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = data.tournamentName,
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.admin_manage_registration_description),
                        color = Color(0xFFC8D2E3),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                SmallStatusBadge(data.status)
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.admin_manage_registration_teams_registered).uppercase(),
                            color = Color(0xFFC8D2E3),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Row(
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = data.registeredTeams.toString(),
                                color = Color.White,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.ExtraBold
                            )

                            Text(
                                text = "/${data.maxTeams}",
                                color = Color(0xFFC8D2E3),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }

                    Text(
                        text = data.registrationClosesText,
                        color = Color(0xFFFFC857),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                LinearProgressIndicator(
                    progress = { progressValue },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(50.dp)),
                    color = TealGreen,
                    trackColor = Color(0xFF31445F)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AdminRegistrationStatPill(
                    label = stringResource(R.string.admin_manage_registration_tab_pending),
                    value = pendingCount.toString(),
                    modifier = Modifier.weight(1f)
                )

                AdminRegistrationStatPill(
                    label = stringResource(R.string.admin_manage_registration_tab_approved),
                    value = approvedCount.toString(),
                    modifier = Modifier.weight(1f)
                )

                AdminRegistrationStatPill(
                    label = stringResource(R.string.admin_manage_registration_status_rejected),
                    value = rejectedCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun AdminRegistrationStatPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.10f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label.uppercase(),
                color = Color(0xFFC8D2E3),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
            .clip(RoundedCornerShape(50.dp))
            .background(TealGreen)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AdminManageRegistrationSearchBox(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = stringResource(R.string.admin_manage_registration_search_placeholder),
                color = TextGray,
                fontSize = 13.sp
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
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = InputBg,
            unfocusedContainerColor = InputBg,
            disabledContainerColor = InputBg,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = DarkBlue,
            unfocusedTextColor = DarkBlue,
            cursorColor = PrimaryBlue
        )
    )
}

@Composable
private fun AdminRegistrationTabs(
    selectedTab: String,
    allCount: Int,
    pendingCount: Int,
    approvedCount: Int,
    onTabSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RegistrationTabChip(
            text = stringResource(R.string.admin_manage_registration_tab_all),
            count = allCount,
            selected = selectedTab == "all",
            modifier = Modifier.weight(1f),
            onClick = { onTabSelected("all") }
        )

        RegistrationTabChip(
            text = stringResource(R.string.admin_manage_registration_tab_pending),
            count = pendingCount,
            selected = selectedTab == "pending",
            modifier = Modifier.weight(1f),
            onClick = { onTabSelected("pending") }
        )

        RegistrationTabChip(
            text = stringResource(R.string.admin_manage_registration_tab_approved),
            count = approvedCount,
            selected = selectedTab == "approved",
            modifier = Modifier.weight(1f),
            onClick = { onTabSelected("approved") }
        )
    }
}

@Composable
private fun RegistrationTabChip(
    text: String,
    count: Int,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(42.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = if (selected) PrimaryBlue else Color.White,
        border = if (selected) null else BorderStroke(1.dp, InputBg)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                color = if (selected) Color.White else DarkBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.width(5.dp))

            Text(
                text = count.toString(),
                color = if (selected) Color.White else PrimaryBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun AdminSectionHeader(
    title: String,
    subtitle: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            color = DarkBlue,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = subtitle,
            color = TextGray,
            fontSize = 12.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AdminManageRegistrationMessage(
    message: String,
    isError: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) LightRedBadge else Color(0xFFEAF8F5)
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
private fun EmptyRegistrationCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(InputBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "—",
                    color = TextGray,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = text,
                color = TextGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
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
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TeamAvatar(name = team.teamName, background = LightRedBadge, textColor = ErrorRed)

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = team.teamName,
                            color = DarkBlue,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = stringResource(R.string.admin_manage_registration_captain_division, team.captainName, team.division),
                            color = TextGray,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                MiniBadge(
                    text = stringResource(R.string.admin_manage_registration_status_pending),
                    background = Color(0xFFFFF7D6),
                    textColor = Color(0xFFB45309)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TeamInfoBox(
                    label = stringResource(R.string.admin_manage_registration_players),
                    value = "${team.playersCount}/${team.maxPlayers}",
                    modifier = Modifier.weight(1f)
                )

                TeamInfoBox(
                    label = stringResource(R.string.admin_manage_registration_win_rate),
                    value = team.winRate,
                    modifier = Modifier.weight(1f)
                )

                TeamInfoBox(
                    label = stringResource(R.string.admin_manage_registration_applied),
                    value = team.appliedAgo,
                    modifier = Modifier.weight(1f)
                )
            }

            PaymentStatusRow(status = team.paymentStatus)

            HorizontalDivider(color = InputBg)

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onRejectClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LightRedBadge,
                        contentColor = ErrorRed
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.admin_manage_registration_reject),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onApproveClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealGreen,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = stringResource(R.string.admin_manage_registration_approve),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Button(
                onClick = onDetailsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, PrimaryBlue),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = PrimaryBlue
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = stringResource(R.string.admin_manage_registration_details),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamAvatar(name = team.teamName, background = Color(0xFFEAF8F5), textColor = TealGreen)

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = team.teamName,
                        color = DarkBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(7.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MiniBadge(
                            text = stringResource(R.string.admin_manage_registration_status_confirmed),
                            background = Color(0xFFEAF8F5),
                            textColor = TealGreen
                        )

                        MiniBadge(
                            text = paymentLabel(team.paymentStatus),
                            background = LightBlueBadge,
                            textColor = PrimaryBlue
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(LightRedBadge)
                    .clickable { onRemoveClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AppIcons.Delete,
                    contentDescription = stringResource(R.string.admin_manage_registration_remove_team_content_description),
                    tint = ErrorRed,
                    modifier = Modifier.size(19.dp)
                )
            }
        }
    }
}

@Composable
private fun RejectedTeamCard(team: AdminRegistrationTeam) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamAvatar(name = team.teamName, background = LightRedBadge, textColor = ErrorRed)

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = team.teamName,
                    color = DarkBlue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(7.dp))

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
private fun TeamAvatar(
    name: String,
    background: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.trim().take(1).uppercase().ifBlank { "?" },
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun TeamInfoBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = InputBg
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = DarkBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label.uppercase(),
                color = TextGray,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun PaymentStatusRow(status: String) {
    val paid = status.equals("pago", ignoreCase = true)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = if (paid) Color(0xFFEAF8F5) else LightRedBadge
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.admin_manage_registration_payment),
                color = if (paid) TealGreen else ErrorRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = if (paid) "✓ ${stringResource(R.string.admin_manage_registration_paid)}" else "× ${stringResource(R.string.admin_manage_registration_unpaid)}",
                color = if (paid) TealGreen else ErrorRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
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
            .clip(RoundedCornerShape(50.dp))
            .background(background)
            .padding(horizontal = 9.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = textColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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

@Preview(showBackground = true)
@Composable
fun AdminManageRegistrationScreenPreview() {
    AdminManageRegistrationScreen(
        tournamentId = "1"
    )
}
