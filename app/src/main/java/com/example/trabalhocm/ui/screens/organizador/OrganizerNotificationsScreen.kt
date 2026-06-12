package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.*

enum class NotificationType { MATCH, TEAM, SYSTEM }

data class AppNotification(
    val id: Int,
    val type: NotificationType,
    val title: String,
    val message: String,
    val time: String,
    val isUnread: Boolean = false,
    val hasActions: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerNotificationsScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val tabAll = stringResource(R.string.tab_all)
    val tabMatches = stringResource(R.string.tab_matches)
    val tabSystem = stringResource(R.string.tab_system)

    val tabs = listOf(tabAll, tabMatches, tabSystem)
    var selectedTab by remember { mutableStateOf(tabs[0]) }

    val notifications = listOf(
        AppNotification(1, NotificationType.MATCH, stringResource(R.string.notif_match_reminder_title), stringResource(R.string.notif_match_reminder_msg), "2M AGO", true),
        AppNotification(2, NotificationType.TEAM, stringResource(R.string.notif_team_invite_title), stringResource(R.string.notif_team_invite_msg), "15M AGO", false, true),
        AppNotification(3, NotificationType.MATCH, stringResource(R.string.notif_result_title), stringResource(R.string.notif_result_msg), "2H AGO"),
        AppNotification(4, NotificationType.SYSTEM, stringResource(R.string.notif_system_title), stringResource(R.string.notif_system_msg), stringResource(R.string.time_yesterday))
    )

    val filteredNotifications = notifications.filter { notif ->
        when (selectedTab) {
            tabMatches -> notif.type == NotificationType.MATCH
            tabSystem -> notif.type == NotificationType.SYSTEM
            else -> true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_notifications), color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.desc_back), tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = stringResource(R.string.desc_notifications), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        bottomBar = {
            MatchLeagueBottomBar(
                selectedTab = "PROFILE",
                onHomeClick = onHomeClick,
                onTournamentsClick = onTournamentsClick,
                onMatchesClick = onMatchesClick,
                onTeamsClick = onTeamsClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = BgLight
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(stringResource(R.string.tag_updates_center), color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(R.string.title_notifications_caps), color = DarkBlue, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBg, RoundedCornerShape(8.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tabs.forEach { tab ->
                        val isSelected = selectedTab == tab
                        Surface(
                            color = if (isSelected) PrimaryBlue else Color.Transparent,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = tab }
                        ) {
                            Text(
                                text = tab,
                                color = if (isSelected) Color.White else TextGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(vertical = 10.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(filteredNotifications) { notification ->
                NotificationCard(notification = notification)
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Outlined.Notifications, contentDescription = null, tint = TextGray.copy(alpha = 0.5f), modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(stringResource(R.string.msg_end_of_feed), color = TextGray.copy(alpha = 0.5f), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun NotificationCard(notification: AppNotification) {
    val (iconColor, iconBg, iconVector) = when (notification.type) {
        NotificationType.MATCH -> Triple(PrimaryBlue, PrimaryBlue.copy(alpha = 0.1f), Icons.Outlined.DateRange)
        NotificationType.TEAM -> Triple(TealGreen, TealGreen.copy(alpha = 0.1f), Icons.Outlined.Person)
        NotificationType.SYSTEM -> Triple(TextGray, InputBg, Icons.Outlined.Info)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    if (notification.isUnread) {
                        drawLine(
                            color = PrimaryBlue,
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            strokeWidth = 12f
                        )
                    }
                }
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = iconBg,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(iconVector, contentDescription = null, tint = iconColor, modifier = Modifier.padding(8.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(notification.title, color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (notification.isUnread) {
                            Surface(color = PrimaryBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(4.dp)) {
                                Text(notification.time, color = PrimaryBlue, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PrimaryBlue))
                        } else {
                            Text(notification.time, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(notification.message, color = TextGray, fontSize = 12.sp, lineHeight = 16.sp)

                if (notification.hasActions) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { },
                            colors = ButtonDefaults.buttonColors(containerColor = TealGreen),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(stringResource(R.string.btn_accept), fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }

                        OutlinedButton(
                            onClick = { },
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, InputBg),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text(stringResource(R.string.btn_decline), color = TextGray, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}