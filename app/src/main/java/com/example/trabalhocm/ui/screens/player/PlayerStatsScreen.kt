package com.example.trabalhocm.ui.screens.player

import android.net.Uri
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.trabalhocm.R

private val BrandBlue = Color(0xFF0B1F3A)
private val BrandGreen = Color(0xFF008D7D)
private val BrandWhite = Color(0xFFFFFFFF)
private val BgGray = Color(0xFFF4F5FA)
private val TextGray = Color(0xFF7D8497)
private val TextDark = Color(0xFF303646)
private val InputBg = Color(0xFFF1F2FB)
private val SoftGreen = Color(0xFFE7F7F4)
private val SoftBlue = Color(0xFFEAF0FF)

@Composable
fun PlayerStatsScreen(
    playerName: String = stringResource(R.string.player_common_loading),
    playerUsername: String = "",
    playerPhotoUri: Uri? = null,
    footballGoals: Int = 0,
    footballAssists: Int = 0,
    basketballPoints: Int = 0,
    basketballWinRate: Int = 0,
    volleyballSpikes: Int = 0,
    volleyballWinRate: Int = 0,
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGray)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        PlayerStatsTopBar(
            onBackClick = onBackClick,
            onNotificationsClick = onNotificationsClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            StatsHeaderCard(
                name = playerName,
                username = playerUsername,
                photoUri = playerPhotoUri,
                footballGoals = footballGoals,
                basketballPoints = basketballPoints,
                volleyballPoints = volleyballSpikes
            )

            Spacer(modifier = Modifier.height(20.dp))

            SportStatCard(
                sportName = stringResource(R.string.player_sport_football),
                sportIcon = "⚽",
                roleTag = "",
                stat1Label = stringResource(R.string.player_common_goals),
                stat1Value = footballGoals.toString(),
                stat1Color = Color(0xFF3566C9),
                stat2Label = stringResource(R.string.player_common_wins),
                stat2Value = footballAssists.toString(),
                stat2Color = TextDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            SportStatCard(
                sportName = stringResource(R.string.player_sport_basketball),
                sportIcon = "🏀",
                roleTag = "",
                stat1Label = stringResource(R.string.player_common_points),
                stat1Value = basketballPoints.toString(),
                stat1Color = Color(0xFFC95E35),
                stat2Label = stringResource(R.string.player_common_winrate),
                stat2Value = "$basketballWinRate%",
                stat2Color = BrandGreen
            )

            Spacer(modifier = Modifier.height(16.dp))

            SportStatCard(
                sportName = stringResource(R.string.player_sport_volleyball),
                sportIcon = "🏐",
                roleTag = "",
                stat1Label = stringResource(R.string.player_common_points),
                stat1Value = volleyballSpikes.toString(),
                stat1Color = TextDark,
                stat2Label = stringResource(R.string.player_common_winrate),
                stat2Value = "$volleyballWinRate%",
                stat2Color = BrandGreen
            )

            Spacer(modifier = Modifier.height(20.dp))

            MatchHistorySection()

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PlayerStatsTopBar(
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(BrandBlue)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f))
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "‹",
                color = BrandWhite,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.player_common_profile),
                color = BrandWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.player_stats_match_history),
                color = BrandWhite.copy(alpha = 0.72f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f))
                .clickable { onNotificationsClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = stringResource(R.string.player_common_notifications),
                tint = BrandWhite
            )
        }
    }
}

@Composable
fun StatsHeaderCard(
    name: String,
    username: String,
    photoUri: Uri?,
    footballGoals: Int = 0,
    basketballPoints: Int = 0,
    volleyballPoints: Int = 0
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .border(3.dp, BrandGreen, CircleShape)
                    .background(Color.White.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                if (photoUri != null) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = stringResource(R.string.player_common_photo),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text("👤", fontSize = 40.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = name,
                color = BrandWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            if (username.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "@$username",
                    color = BrandGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HeaderMiniStat(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.player_sport_football),
                    value = footballGoals.toString()
                )
                HeaderMiniStat(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.player_sport_basketball),
                    value = basketballPoints.toString()
                )
                HeaderMiniStat(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.player_sport_volleyball),
                    value = volleyballPoints.toString()
                )
            }
        }
    }
}

@Composable
private fun HeaderMiniStat(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = BrandWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = BrandWhite.copy(alpha = 0.70f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SportStatCard(
    sportName: String,
    sportIcon: String,
    roleTag: String,
    stat1Label: String,
    stat1Value: String,
    stat1Color: Color,
    stat2Label: String,
    stat2Value: String,
    stat2Color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(SoftBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(sportIcon, fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = sportName,
                            color = TextDark,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.player_stats_last5),
                            color = TextGray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                if (roleTag.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(InputBg)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = roleTag,
                            color = TextGray,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatValueBox(
                    modifier = Modifier.weight(1f),
                    label = stat1Label,
                    value = stat1Value,
                    valueColor = stat1Color,
                    background = SoftBlue
                )
                StatValueBox(
                    modifier = Modifier.weight(1f),
                    label = stat2Label,
                    value = stat2Value,
                    valueColor = stat2Color,
                    background = SoftGreen
                )
            }
        }
    }
}

@Composable
private fun StatValueBox(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    valueColor: Color,
    background: Color
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .padding(16.dp)
    ) {
        Text(
            text = label,
            color = TextGray,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            color = valueColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MatchHistorySection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.player_stats_match_history),
                        color = TextDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.player_stats_last5),
                        color = TextGray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = stringResource(R.string.player_common_view_all),
                    color = BrandGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(InputBg)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.player_stats_no_matches),
                    color = TextGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun MatchHistoryRow(
    team1: String,
    team2: String,
    score: String,
    subtitle: String,
    resultIcon: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(InputBg)
                    .border(1.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("⚽", fontSize = 20.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$team1 vs $team2",
                    color = TextDark,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = TextGray,
                    fontSize = 11.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = score,
                    color = TextDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = resultIcon,
                    color = BrandGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
