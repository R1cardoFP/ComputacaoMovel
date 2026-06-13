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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.model.AdminStats
import com.example.trabalhocm.data.repository.AdminRepository
import com.example.trabalhocm.ui.theme.AppIcons
import androidx.compose.ui.res.stringResource
import com.example.trabalhocm.R

private val AdminBlue = Color(0xFF0B1F3A)
private val AdminGreen = Color(0xFF008D7D)
private val AdminBackground = Color(0xFFF4F5FA)
private val TextMuted = Color(0xFF6F7A8A)
private val CardWhite = Color.White
private val ButtonBorderGray = Color(0xFFD8DEE9)

@Composable
fun AdminHomeScreen(
    onManageUsersClick: () -> Unit = {},
    onManageTeamsClick: () -> Unit = {},
    onManageTournamentsClick: () -> Unit = {},
    onReviewRequestsClick: () -> Unit = {},
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
        onHomeClick = onHomeClick,
        onNotificationsClick = onNotificationsClick,
        onTournamentsClick = onTournamentsClick,
        onMatchesClick = onMatchesClick,
        onTeamsClick = onTeamsClick,
        onProfileClick = onProfileClick
    )
}

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
    onHomeClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = AdminBackground,
        topBar = {
            AdminTopBar(
                title = stringResource(R.string.admin_home_top_title),
                onNotificationsClick = onNotificationsClick
            )
        },
        bottomBar = {
            AdminBottomBar(
                selected = "home",
                onHomeClick = onHomeClick,
                onTournamentsClick = onTournamentsClick,
                onMatchesClick = onMatchesClick,
                onTeamsClick = onTeamsClick,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 18.dp,
                bottom = 30.dp
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            item {
                Column {
                    Text(
                        text = stringResource(R.string.admin_home_console).uppercase(),
                        color = AdminGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.admin_home_management_dashboard),
                        color = AdminBlue,
                        fontSize = 26.sp,
                        lineHeight = 30.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.admin_home_management_description),
                        color = TextMuted,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }
            }

            item {
                QuickOverviewCard(
                    users = usersText,
                    teams = teamsText,
                    tournaments = tournamentsText
                )
            }

            item {
                AdminManagementCard(
                    icon = AppIcons.Profile,
                    title = stringResource(R.string.admin_home_user_management),
                    description = stringResource(R.string.admin_home_user_management_desc),
                    badge = usersText,
                    iconColor = Color(0xFF0057C8),
                    iconBackground = Color(0xFFF0F5FF),
                    buttonText = stringResource(R.string.admin_home_manage_users).uppercase(),
                    buttonColor = Color.White,
                    buttonTextColor = AdminBlue,
                    onClick = onManageUsersClick
                )
            }

            item {
                AdminManagementCard(
                    icon = AppIcons.Teams,
                    title = stringResource(R.string.admin_home_teams_management),
                    description = stringResource(R.string.admin_home_teams_management_desc),
                    badge = teamsText,
                    iconColor = AdminGreen,
                    iconBackground = Color(0xFFEAF8F5),
                    buttonText = stringResource(R.string.admin_home_manage_teams).uppercase(),
                    buttonColor = AdminGreen,
                    buttonTextColor = Color.White,
                    onClick = onManageTeamsClick
                )
            }

            item {
                AdminManagementCard(
                    icon = AppIcons.Tournaments,
                    title = stringResource(R.string.admin_home_tournaments_management),
                    description = stringResource(R.string.admin_home_tournaments_management_desc),
                    badge = tournamentsText,
                    iconColor = Color(0xFF0057C8),
                    iconBackground = Color(0xFFF0F5FF),
                    buttonText = stringResource(R.string.admin_home_manage_tournaments).uppercase(),
                    buttonColor = Color(0xFF0057C8),
                    buttonTextColor = Color.White,
                    onClick = onManageTournamentsClick
                )
            }

            item {
                AdminManagementCard(
                    icon = AppIcons.Notifications,
                    title = stringResource(R.string.admin_home_organizer_requests),
                    description = stringResource(R.string.admin_home_organizer_requests_desc),
                    badge = "$organizerRequestsText ${stringResource(R.string.admin_home_pending).uppercase()}",
                    iconColor = Color(0xFFE2A600),
                    iconBackground = Color(0xFFFFF7DE),
                    buttonText = stringResource(R.string.admin_home_review_requests).uppercase(),
                    buttonColor = Color.White,
                    buttonTextColor = AdminBlue,
                    badgeTextColor = AdminGreen,
                    onClick = onReviewRequestsClick
                )
            }
        }
    }
}

@Composable
private fun AdminTopBar(
    title: String,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AdminBlue)
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Icon(
            imageVector = AppIcons.Notifications,
            contentDescription = stringResource(R.string.admin_common_notifications),
            tint = Color.White,
            modifier = Modifier
                .size(23.dp)
                .clickable {
                    onNotificationsClick()
                }
        )
    }
}

@Composable
private fun QuickOverviewCard(
    users: String,
    teams: String,
    tournaments: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = AdminBlue),
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
                        text = stringResource(R.string.admin_home_quick_overview),
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = stringResource(R.string.admin_home_global_actions),
                        color = Color(0xFFB9C4D8),
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF263E61))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = stringResource(R.string.admin_home_live).uppercase(),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(28.dp)
            ) {
                OverviewNumber(number = users, label = stringResource(R.string.admin_home_users_label).uppercase())
                OverviewNumber(number = teams, label = stringResource(R.string.admin_home_teams_label).uppercase())
                OverviewNumber(number = tournaments, label = stringResource(R.string.admin_home_tournaments_label).uppercase())
            }
        }
    }
}

@Composable
private fun OverviewNumber(number: String, label: String) {
    Column {
        Text(
            text = number,
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            color = Color(0xFFB9C4D8),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun AdminManagementCard(
    icon: ImageVector,
    title: String,
    description: String,
    badge: String,
    iconColor: Color,
    iconBackground: Color,
    buttonText: String,
    buttonColor: Color,
    buttonTextColor: Color,
    badgeTextColor: Color = TextMuted,
    onClick: () -> Unit
) {
    val hasLightButton = buttonColor == Color.White

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(iconBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(21.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        color = AdminBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = description,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFEAF2F5))
                        .padding(horizontal = 9.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badge,
                        color = badgeTextColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp),
                shape = RoundedCornerShape(5.dp),
                border = if (hasLightButton) {
                    BorderStroke(1.dp, ButtonBorderGray)
                } else {
                    null
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    contentColor = buttonTextColor
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = buttonText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun AdminBottomBar(
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
            .background(Color.White)
            .navigationBarsPadding()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomItem(AppIcons.Home, stringResource(R.string.admin_nav_home).uppercase(), selected == "home", onHomeClick)
        BottomItem(AppIcons.Tournaments, stringResource(R.string.admin_nav_tournaments).uppercase(), selected == "tournaments", onTournamentsClick)
        BottomItem(AppIcons.Games, stringResource(R.string.admin_nav_matches).uppercase(), selected == "matches", onMatchesClick)
        BottomItem(AppIcons.Teams, stringResource(R.string.admin_nav_teams).uppercase(), selected == "teams", onTeamsClick)
        BottomItem(AppIcons.Profile, stringResource(R.string.admin_nav_profile).uppercase(), selected == "profile", onProfileClick)
    }
}

@Composable
private fun BottomItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) Color(0xFF0057C8) else Color(0xFF9AA5B5)

    Column(
        modifier = Modifier.clickable { onClick() },
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