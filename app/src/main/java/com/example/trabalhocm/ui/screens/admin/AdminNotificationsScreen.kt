package com.example.trabalhocm.ui.screens.admin

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.CardBg
import com.example.trabalhocm.ui.theme.LightBlueBadge
import com.example.trabalhocm.ui.theme.TextGray

private data class AdminNotification(
    val title: String,
    val description: String,
    val time: String,
    val type: String,
    val icon: String,
    val iconColor: Color,
    val iconBackground: Color,
    val actionText: String? = null,
    val unread: Boolean = false
)

@Composable
fun AdminNotificationsScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val notifications = remember {
        listOf(
            AdminNotification(
                title = "New Tournament Created",
                description = "Liga Regional Sul created by M. Silva.",
                time = "22M AGO",
                type = "MODERATION",
                icon = "♜",
                iconColor = Color(0xFFE2A600),
                iconBackground = Color(0xFFFFF7DE),
                actionText = "VIEW TOURNAMENT DETAILS",
                unread = true
            ),
            AdminNotification(
                title = "New User Registered",
                description = "Beatriz Almeida joined the platform. Total users: 143.",
                time = "1H AGO",
                type = "MODERATION",
                icon = "♙",
                iconColor = BrandGreen,
                iconBackground = Color(0xFFEAF8F5),
                actionText = "VIEW USER PROFILE"
            ),
            AdminNotification(
                title = "New Team Created",
                description = "Iron Eagles registered by J. Costa. Captain confirmed.",
                time = "3H AGO",
                type = "MODERATION",
                icon = "♟",
                iconColor = Color(0xFF0057C8),
                iconBackground = Color(0xFFEAF3FF),
                actionText = "VIEW TEAM DETAILS"
            ),
            AdminNotification(
                title = "Account Suspended",
                description = "Maria Santos suspended Tomás Pinto for repeated violations.",
                time = "YESTERDAY",
                type = "MODERATION",
                icon = "⊗",
                iconColor = Color(0xFFDC2626),
                iconBackground = Color(0xFFFEE2E2)
            ),
            AdminNotification(
                title = "System",
                description = "Daily backup completed successfully. 2.4 GB stored.",
                time = "YESTERDAY",
                type = "SYSTEM",
                icon = "☁",
                iconColor = Color(0xFF64748B),
                iconBackground = Color(0xFFEAF3FF)
            ),
            AdminNotification(
                title = "Tournament Payment",
                description = "€150 transferred to organizer of Premier Summer Cup. 12 registrations confirmed.",
                time = "2D AGO",
                type = "MODERATION",
                icon = "▤",
                iconColor = BrandGreen,
                iconBackground = Color(0xFFEAF8F5)
            )
        )
    }

    val visibleNotifications = notifications.filter {
        selectedFilter == "ALL" || it.type == selectedFilter
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            AdminNotificationsTopBar(
                title = "Notifications",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            AdminNotificationsBottomBar(
                selected = "profile",
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
                bottom = 28.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Column {
                    Text(
                        text = "ADMIN CONSOLE",
                        color = BrandGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Notifications",
                        color = BrandBlue,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Platform alerts, moderation queue and system\nevents.",
                        color = TextGray,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            item {
                AdminNotificationFilterRow(
                    selectedFilter = selectedFilter,
                    onFilterClick = {
                        selectedFilter = it
                    }
                )
            }

            items(visibleNotifications.size) { index ->
                AdminNotificationCard(
                    notification = visibleNotifications[index]
                )
            }
        }
    }
}

@Composable
private fun AdminNotificationsTopBar(
    title: String,
    onBackClick: () -> Unit
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
            modifier = Modifier.clickable { onBackClick() }
        ) {
            Text(
                text = "←",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "🔔",
            color = Color.White,
            fontSize = 21.sp
        )
    }
}

@Composable
private fun AdminNotificationFilterRow(
    selectedFilter: String,
    onFilterClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F5F9), RoundedCornerShape(5.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        AdminNotificationFilterChip(
            text = "ALL",
            selected = selectedFilter == "ALL",
            modifier = Modifier.weight(1f),
            onClick = onFilterClick
        )

        AdminNotificationFilterChip(
            text = "MODERATION",
            selected = selectedFilter == "MODERATION",
            modifier = Modifier.weight(1f),
            onClick = onFilterClick
        )

        AdminNotificationFilterChip(
            text = "SYSTEM",
            selected = selectedFilter == "SYSTEM",
            modifier = Modifier.weight(1f),
            onClick = onFilterClick
        )
    }
}

@Composable
private fun AdminNotificationFilterChip(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(if (selected) Color(0xFF0057C8) else Color.Transparent)
            .clickable { onClick(text) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else BrandBlue,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun AdminNotificationCard(
    notification: AdminNotification
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(notification.iconBackground),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = notification.icon,
                    color = notification.iconColor,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = notification.title,
                        color = BrandBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(LightBlueBadge)
                            .padding(horizontal = 9.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = notification.time,
                            color = TextGray,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (notification.unread) {
                        Spacer(modifier = Modifier.width(6.dp))

                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFF0057C8))
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.description,
                    color = TextGray,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )

                if (notification.actionText != null) {
                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(34.dp)
                            .clip(RoundedCornerShape(5.dp))
                            .background(Color.White)
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = notification.actionText,
                            color = BrandBlue,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminNotificationsBottomBar(
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
        AdminNotificationsBottomItem("⌂", "HOME", selected == "home", onHomeClick)
        AdminNotificationsBottomItem("♜", "TOURNAMENTS", selected == "tournaments", onTournamentsClick)
        AdminNotificationsBottomItem("◎", "MATCHES", selected == "matches", onMatchesClick)
        AdminNotificationsBottomItem("♟", "TEAMS", selected == "teams", onTeamsClick)
        AdminNotificationsBottomItem("♙", "PROFILE", selected == "profile", onProfileClick)
    }
}

@Composable
private fun AdminNotificationsBottomItem(
    icon: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            color = if (selected) Color(0xFF0057C8) else Color(0xFF9AA5B5),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = if (selected) Color(0xFF0057C8) else Color(0xFF9AA5B5),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminNotificationsScreenPreview() {
    AdminNotificationsScreen()
}