package com.example.trabalhocm.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.model.AdminStats
import com.example.trabalhocm.data.repository.AdminRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.AppIcons

private val DarkBlue = Color(0xFF152238)
private val EmeraldGreen = Color(0xFF0E8A6F)
private val BgLight = Color(0xFFF7F7F9)
private val TextGray = Color(0xFF6B7280)
private val PrimaryBlue = Color(0xFF2B5BFE)
private val WarningYellow = Color(0xFFE2A600)

@Composable
fun AdminHomeScreen(
    onManageUsersClick: () -> Unit = {},
    onManageTeamsClick: () -> Unit = {},
    onManageTournamentsClick: () -> Unit = {},
    onReviewRequestsClick: () -> Unit = {},
    onOfflineResultsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val adminRepository = remember { AdminRepository() }

    var adminStats by remember { mutableStateOf(AdminStats()) }
    var isLoadingStats by remember { mutableStateOf(true) }

    var organizerRequestsPending by remember { mutableStateOf(0) }
    var isLoadingOrganizerRequests by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        adminRepository.carregarEstatisticasAdmin()
            .onSuccess { stats ->
                adminStats = stats
            }

        isLoadingStats = false

        adminRepository.carregarPedidosOrganizadorPendentes()
            .onSuccess { total ->
                organizerRequestsPending = total
            }

        isLoadingOrganizerRequests = false
    }

    val usersText = if (isLoadingStats) "..." else adminStats.totalUsers.toString()
    val teamsText = if (isLoadingStats) "..." else adminStats.totalTeams.toString()
    val tournamentsText = if (isLoadingStats) "..." else adminStats.totalTournaments.toString()

    val organizerRequestsText = if (isLoadingOrganizerRequests) {
        "..."
    } else {
        organizerRequestsPending.toString()
    }

    AdminHomeContent(
        usersText = usersText,
        teamsText = teamsText,
        tournamentsText = tournamentsText,
        organizerRequestsText = organizerRequestsText,
        onManageUsersClick = onManageUsersClick,
        onManageTeamsClick = onManageTeamsClick,
        onManageTournamentsClick = onManageTournamentsClick,
        onReviewRequestsClick = onReviewRequestsClick,
        onOfflineResultsClick = onOfflineResultsClick,
        onHomeClick = onHomeClick,
        onNotificationsClick = onNotificationsClick,
        onTournamentsClick = onTournamentsClick,
        onMatchesClick = onMatchesClick,
        onTeamsClick = onTeamsClick,
        onProfileClick = onProfileClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminHomeContent(
    usersText: String,
    teamsText: String,
    tournamentsText: String,
    organizerRequestsText: String,
    onManageUsersClick: () -> Unit = {},
    onManageTeamsClick: () -> Unit = {},
    onManageTournamentsClick: () -> Unit = {},
    onReviewRequestsClick: () -> Unit = {},
    onOfflineResultsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
){
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.admin_home_top_title),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue
                )
            )
        },
        bottomBar = {
            MatchLeagueBottomBar(
                selectedTab = "HOME",
                onHomeClick = onHomeClick,
                onTournamentsClick = onTournamentsClick,
                onMatchesClick = onMatchesClick,
                onTeamsClick = onTeamsClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = BgLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            AdminDashboardHeroCard(
                users = usersText,
                teams = teamsText,
                tournaments = tournamentsText,
                pendingRequests = organizerRequestsText,
                onReviewRequestsClick = onReviewRequestsClick
            )

            AdminQuickActionsSection(
                onManageUsersClick = onManageUsersClick,
                onManageTeamsClick = onManageTeamsClick,
                onManageTournamentsClick = onManageTournamentsClick,
                onReviewRequestsClick = onReviewRequestsClick,
                onOfflineResultsClick = onOfflineResultsClick
            )

            AdminManagementSection(
                users = usersText,
                teams = teamsText,
                tournaments = tournamentsText,
                pendingRequests = organizerRequestsText,
                onManageUsersClick = onManageUsersClick,
                onManageTeamsClick = onManageTeamsClick,
                onManageTournamentsClick = onManageTournamentsClick,
                onReviewRequestsClick = onReviewRequestsClick
            )
        }
    }
}

@Composable
private fun AdminDashboardHeroCard(
    users: String,
    teams: String,
    tournaments: String,
    pendingRequests: String,
    onReviewRequestsClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkBlue
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color.White.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        1.dp,
                        EmeraldGreen.copy(alpha = 0.5f)
                    )
                ) {
                    Text(
                        text = stringResource(R.string.admin_home_console).uppercase(),
                        color = EmeraldGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onReviewRequestsClick() }
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(WarningYellow)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "$pendingRequests ${stringResource(R.string.admin_home_pending).uppercase()}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = stringResource(R.string.admin_home_management_dashboard),
                color = Color.White,
                fontSize = 28.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.admin_home_management_description),
                color = Color(0xFFB9C4D8),
                fontSize = 13.sp,
                lineHeight = 19.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                AdminHeroStat(
                    number = users,
                    label = stringResource(R.string.admin_home_users_label).uppercase()
                )

                AdminHeroStat(
                    number = teams,
                    label = stringResource(R.string.admin_home_teams_label).uppercase()
                )

                AdminHeroStat(
                    number = tournaments,
                    label = stringResource(R.string.admin_home_tournaments_label).uppercase()
                )
            }
        }
    }
}

@Composable
private fun AdminHeroStat(
    number: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = number,
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = Color(0xFFB9C4D8),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun AdminQuickActionsSection(
    onManageUsersClick: () -> Unit,
    onManageTeamsClick: () -> Unit,
    onManageTournamentsClick: () -> Unit,
    onReviewRequestsClick: () -> Unit,
    onOfflineResultsClick: () -> Unit
){
    Column {
        AdminSectionTitle(stringResource(R.string.admin_home_global_actions))

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdminQuickActionCard(
                modifier = Modifier.weight(1f),
                icon = AppIcons.Profile,
                title = stringResource(R.string.admin_home_manage_users),
                tint = PrimaryBlue,
                onClick = onManageUsersClick
            )

            AdminQuickActionCard(
                modifier = Modifier.weight(1f),
                icon = AppIcons.Teams,
                title = stringResource(R.string.admin_home_manage_teams),
                tint = EmeraldGreen,
                onClick = onManageTeamsClick
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdminQuickActionCard(
                modifier = Modifier.weight(1f),
                icon = AppIcons.Tournaments,
                title = stringResource(R.string.admin_home_manage_tournaments),
                tint = PrimaryBlue,
                onClick = onManageTournamentsClick
            )

            AdminQuickActionCard(
                modifier = Modifier.weight(1f),
                icon = AppIcons.Notifications,
                title = stringResource(R.string.admin_home_review_requests),
                tint = WarningYellow,
                onClick = onReviewRequestsClick
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        AdminQuickActionCard(
            modifier = Modifier.fillMaxWidth(),
            icon = AppIcons.Notifications,
            title = "MODO OFFLINE",
            tint = EmeraldGreen,
            onClick = onOfflineResultsClick
        )
    }
}

@Composable
private fun AdminQuickActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    tint: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                color = tint.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = tint,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                color = DarkBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
private fun AdminManagementSection(
    users: String,
    teams: String,
    tournaments: String,
    pendingRequests: String,
    onManageUsersClick: () -> Unit,
    onManageTeamsClick: () -> Unit,
    onManageTournamentsClick: () -> Unit,
    onReviewRequestsClick: () -> Unit
) {
    Column {
        AdminSectionTitle(stringResource(R.string.admin_home_quick_overview))

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Column {
                AdminManagementRow(
                    icon = AppIcons.Profile,
                    title = stringResource(R.string.admin_home_user_management),
                    description = stringResource(R.string.admin_home_user_management_desc),
                    value = users,
                    valueLabel = stringResource(R.string.admin_home_users_label).uppercase(),
                    tint = PrimaryBlue,
                    onClick = onManageUsersClick
                )

                AdminDivider()

                AdminManagementRow(
                    icon = AppIcons.Teams,
                    title = stringResource(R.string.admin_home_teams_management),
                    description = stringResource(R.string.admin_home_teams_management_desc),
                    value = teams,
                    valueLabel = stringResource(R.string.admin_home_teams_label).uppercase(),
                    tint = EmeraldGreen,
                    onClick = onManageTeamsClick
                )

                AdminDivider()

                AdminManagementRow(
                    icon = AppIcons.Tournaments,
                    title = stringResource(R.string.admin_home_tournaments_management),
                    description = stringResource(R.string.admin_home_tournaments_management_desc),
                    value = tournaments,
                    valueLabel = stringResource(R.string.admin_home_tournaments_label).uppercase(),
                    tint = PrimaryBlue,
                    onClick = onManageTournamentsClick
                )

                AdminDivider()

                AdminManagementRow(
                    icon = AppIcons.Notifications,
                    title = stringResource(R.string.admin_home_organizer_requests),
                    description = stringResource(R.string.admin_home_organizer_requests_desc),
                    value = pendingRequests,
                    valueLabel = stringResource(R.string.admin_home_pending).uppercase(),
                    tint = WarningYellow,
                    onClick = onReviewRequestsClick
                )
            }
        }
    }
}

@Composable
private fun AdminManagementRow(
    icon: ImageVector,
    title: String,
    description: String,
    value: String,
    valueLabel: String,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            color = tint.copy(alpha = 0.1f),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = tint,
                modifier = Modifier.padding(11.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = DarkBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = description,
                color = TextGray,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = value,
                color = tint,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = valueLabel,
                color = TextGray,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
        }
    }
}

@Composable
private fun AdminDivider() {
    HorizontalDivider(
        color = BgLight,
        thickness = 1.dp
    )
}

@Composable
private fun AdminSectionTitle(
    title: String
) {
    Text(
        text = title,
        fontSize = 18.sp,
        color = DarkBlue,
        fontWeight = FontWeight.Bold
    )
}

@Preview(showBackground = true)
@Composable
fun AdminHomeScreenPreview() {
    AdminHomeContent(
        usersText = "142",
        teamsText = "3",
        tournamentsText = "4",
        organizerRequestsText = "4"
    )
}
