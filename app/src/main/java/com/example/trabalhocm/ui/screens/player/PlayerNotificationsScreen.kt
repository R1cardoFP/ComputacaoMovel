package com.example.trabalhocm.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
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
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite

@Composable
fun PlayerNotificationsScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("ALL") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        NotificationsTopBar(
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 20.dp)
        ) {
            Text(
                text = "UPDATES CENTER",
                color = Color(0xFF4167C8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "NOTIFICATIONS",
                color = BrandBlue,
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(26.dp))

            NotificationsTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            when (selectedTab) {
                "ALL" -> AllNotificationsContent()
                "MATCHES" -> MatchesNotificationsContent()
                "SYSTEM" -> SystemNotificationsContent()
            }

            Spacer(modifier = Modifier.height(30.dp))

            EndOfFeed()
        }

        MatchLeagueBottomBar(
            selectedTab = "PROFILE",
            onHomeClick = onHomeClick,
            onTournamentsClick = onTournamentsClick,
            onMatchesClick = onMatchesClick,
            onTeamsClick = onTeamsClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun NotificationsTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(BrandBlue)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "←",
            color = BrandWhite,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                onBackClick()
            }
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = "Notifications",
            color = BrandWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.3.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "♧",
            color = BrandWhite,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun NotificationsTabs(
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFFF0F2FA))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        NotificationTabButton(
            text = "ALL",
            selected = selectedTab == "ALL",
            onClick = { onTabSelected("ALL") },
            modifier = Modifier.weight(1f)
        )

        NotificationTabButton(
            text = "MATCHES",
            selected = selectedTab == "MATCHES",
            onClick = { onTabSelected("MATCHES") },
            modifier = Modifier.weight(1f)
        )

        NotificationTabButton(
            text = "SYSTEM",
            selected = selectedTab == "SYSTEM",
            onClick = { onTabSelected("SYSTEM") },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun NotificationTabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(3.dp))
            .background(if (selected) Color(0xFF2949FF) else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) BrandWhite else Color(0xFF7D8497),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun AllNotificationsContent() {
    NotificationCard(
        icon = "⏱",
        iconColor = Color(0xFF2949FF),
        title = "Match Reminder",
        time = "2M AGO",
        description = "Match vs Iron Eagles starts\nin 1 hour.",
        highlighted = true,
        unread = true
    )

    Spacer(modifier = Modifier.height(14.dp))

    TeamInvitationNotificationCard()

    Spacer(modifier = Modifier.height(14.dp))

    NotificationCard(
        icon = "◎",
        iconColor = Color(0xFF7D8497),
        title = "Result Update",
        time = "2H AGO",
        description = "Final Score: Match #42 - Team\nA wins 2-1.",
        highlighted = false,
        unread = false
    )

    Spacer(modifier = Modifier.height(14.dp))

    NotificationCard(
        icon = "☁",
        iconColor = Color(0xFF7D8497),
        title = "System",
        time = "YESTERDAY",
        description = "Offline data synced successfully.",
        highlighted = false,
        unread = false
    )
}

@Composable
fun MatchesNotificationsContent() {
    NotificationCard(
        icon = "⏱",
        iconColor = Color(0xFF2949FF),
        title = "Match Reminder",
        time = "2M AGO",
        description = "Match vs Iron Eagles starts\nin 1 hour.",
        highlighted = true,
        unread = true
    )

    Spacer(modifier = Modifier.height(14.dp))

    NotificationCard(
        icon = "◎",
        iconColor = Color(0xFF7D8497),
        title = "Result Update",
        time = "2H AGO",
        description = "Final Score: Match #42 - Team\nA wins 2-1.",
        highlighted = false,
        unread = false
    )
}

@Composable
fun SystemNotificationsContent() {
    NotificationCard(
        icon = "☁",
        iconColor = Color(0xFF7D8497),
        title = "System",
        time = "YESTERDAY",
        description = "Offline data synced successfully.",
        highlighted = false,
        unread = false
    )
}

@Composable
fun NotificationCard(
    icon: String,
    iconColor: Color,
    title: String,
    time: String,
    description: String,
    highlighted: Boolean,
    unread: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            if (highlighted) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(104.dp)
                        .background(Color(0xFF2949FF))
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFF0F2FA)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
                        color = iconColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            color = BrandBlue,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = time,
                            color = Color(0xFF8D94A3),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (unread) {
                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2949FF))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = description,
                        color = Color(0xFF6D7486),
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun TeamInvitationNotificationCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(BrandGreen.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "♙+",
                    color = BrandGreen,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Team Invitation",
                        color = BrandBlue,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "15M AGO",
                        color = Color(0xFF8D94A3),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "You have been invited to join\nSC BRAGA.",
                    color = Color(0xFF6D7486),
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .width(82.dp)
                            .height(34.dp),
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandGreen,
                            contentColor = BrandWhite
                        )
                    ) {
                        Text(
                            text = "ACCEPT",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier
                            .width(82.dp)
                            .height(34.dp),
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF7D8497)
                        )
                    ) {
                        Text(
                            text = "DECLINE",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EndOfFeed() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 28.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "♧",
            color = Color(0xFF9EA4B3),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "END OF FEED",
            color = Color(0xFF9EA4B3),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 3.sp
        )
    }
}

@Preview(showBackground = true, name = "Player Notifications Screen")
@Composable
fun PlayerNotificationsScreenPreview() {
    PlayerNotificationsScreen()
}