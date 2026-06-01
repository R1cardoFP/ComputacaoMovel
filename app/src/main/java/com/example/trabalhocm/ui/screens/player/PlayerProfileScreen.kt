package com.example.trabalhocm.ui.screens.player

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.ui.screens.MatchPointBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite

@Composable
fun PlayerProfileScreen(
    onBackClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        PlayerProfileTopBar(
            onBackClick = onBackClick,
            onNotificationsClick = onNotificationsClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 18.dp)
        ) {
            PlayerProfileHeroCard()

            Spacer(modifier = Modifier.height(14.dp))

            ProfileActionButtons(
                onSettingsClick = onSettingsClick,
                onNotificationsClick = onNotificationsClick
            )

            Spacer(modifier = Modifier.height(18.dp))

            SportStatsCard(
                sport = "Football",
                tag = "FORWARD",
                icon = "⚽",
                statOneLabel = "GOALS",
                statOneValue = "24",
                statTwoLabel = "ASSISTS",
                statTwoValue = "18",
                progress = null
            )

            Spacer(modifier = Modifier.height(14.dp))

            SportStatsCard(
                sport = "Basketball",
                tag = "",
                icon = "🏀",
                statOneLabel = "POINTS",
                statOneValue = "412",
                statTwoLabel = "WIN %",
                statTwoValue = "65%",
                progress = 0.65f
            )

            Spacer(modifier = Modifier.height(14.dp))

            SportStatsCard(
                sport = "Volleyball",
                tag = "",
                icon = "🏐",
                statOneLabel = "SPIKES",
                statOneValue = "84",
                statTwoLabel = "WIN %",
                statTwoValue = "82%",
                progress = 0.82f
            )

            Spacer(modifier = Modifier.height(18.dp))

            MatchHistoryCard()

            Spacer(modifier = Modifier.height(20.dp))
        }

        MatchPointBottomBar(
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
fun PlayerProfileTopBar(
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit
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
            text = "Profile",
            color = BrandWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "♧",
            color = BrandWhite,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                onNotificationsClick()
            }
        )
    }
}

@Composable
fun PlayerProfileHeroCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(245.dp),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF102845),
                            BrandBlue
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.avatar_player),
                    contentDescription = "Cristiano Ronaldo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(92.dp)
                        .clip(RoundedCornerShape(7.dp))
                        .background(Color(0xFFF0F2FA))
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProfileBadge(
                        text = "RANK #1 GLOBAL",
                        backgroundColor = BrandGreen.copy(alpha = 0.20f),
                        textColor = BrandGreen
                    )

                    ProfileBadge(
                        text = "MIDFIELDER",
                        backgroundColor = Color(0xFF2B3F60),
                        textColor = Color(0xFFB6C0D0)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Cristiano Ronaldo",
                    color = BrandWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "●",
                        color = BrandGreen,
                        fontSize = 10.sp
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "FC Mancos",
                        color = Color(0xFF9EA8BA),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.width(18.dp))

                    Text(
                        text = "⚑",
                        color = BrandGreen,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = "Portugal",
                        color = Color(0xFF9EA8BA),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileActionButtons(
    onSettingsClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Card(
            modifier = Modifier
                .weight(1f)
                .height(54.dp)
                .clickable {
                    onSettingsClick()
                },
            shape = RoundedCornerShape(7.dp),
            colors = CardDefaults.cardColors(containerColor = BrandWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "⚙",
                    color = BrandGreen,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "DEFINIÇÕES",
                    color = BrandBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        Card(
            modifier = Modifier
                .weight(1f)
                .height(54.dp)
                .clickable {
                    onNotificationsClick()
                },
            shape = RoundedCornerShape(7.dp),
            colors = CardDefaults.cardColors(containerColor = BrandWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🔔",
                    color = BrandGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "NOTIFICAÇÕES",
                    color = BrandBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun ProfileBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .padding(horizontal = 11.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
fun SportStatsCard(
    sport: String,
    tag: String,
    icon: String,
    statOneLabel: String,
    statOneValue: String,
    statTwoLabel: String,
    statTwoValue: String,
    progress: Float?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFF0F2FA)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = sport,
                    color = BrandBlue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                if (tag.isNotBlank()) {
                    ProfileBadge(
                        text = tag,
                        backgroundColor = Color(0xFFF0F2FA),
                        textColor = Color(0xFF7D8497)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatBox(
                    modifier = Modifier.weight(1f),
                    label = statOneLabel,
                    value = statOneValue
                )

                StatBox(
                    modifier = Modifier.weight(1f),
                    label = statTwoLabel,
                    value = statTwoValue,
                    progress = progress
                )
            }
        }
    }
}

@Composable
fun StatBox(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    progress: Float? = null
) {
    Box(
        modifier = modifier
            .height(70.dp)
            .background(Color(0xFFF9FAFD))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Column {
            Text(
                text = label,
                color = Color(0xFF7D8497),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = value,
                color = Color(0xFF0757C8),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            if (progress != null) {
                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = BrandGreen,
                    trackColor = Color(0xFFECEEF7)
                )
            }
        }
    }
}

@Composable
fun MatchHistoryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "MATCH HISTORY",
                        color = BrandBlue,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "LAST 5 MATCHES",
                        color = Color(0xFF9EA4B3),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "VIEW ALL",
                    color = Color(0xFF4167C8),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            MatchHistoryRow(
                home = "Sporting",
                away = "Vianense",
                result = "3-1",
                date = "Tournament Finals · Oct 4th, 2023",
                won = true
            )

            MatchHistoryRow(
                home = "Benfica",
                away = "Porto",
                result = "0-0",
                date = "League Match · Oct 1st, 2023",
                won = false
            )

            MatchHistoryRow(
                home = "Vianense",
                away = "Benfica",
                result = "1-4",
                date = "Quarter Finals · Oct 3rd, 2023",
                won = false
            )

            MatchHistoryRow(
                home = "Porto",
                away = "Sporting",
                result = "2-0",
                date = "Round League · Sep 28, 2023",
                won = true
            )
        }
    }
}

@Composable
fun MatchHistoryRow(
    home: String,
    away: String,
    result: String,
    date: String,
    won: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F2FA)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⚽",
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "$home vs\n$away",
                color = BrandBlue,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = date,
                color = Color(0xFF9EA4B3),
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = result,
                color = BrandBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = if (won) "WIN" else "FINAL",
                color = if (won) BrandGreen else Color(0xFF9EA4B3),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, name = "Player Profile Screen")
@Composable
fun PlayerProfileScreenPreview() {
    PlayerProfileScreen()
}