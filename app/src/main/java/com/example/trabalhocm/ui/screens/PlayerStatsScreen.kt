package com.example.trabalhocm.ui.screens

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BrandBlue = Color(0xFF0B1F3A)
private val BrandGreen = Color(0xFF008D7D)
private val BrandWhite = Color(0xFFFFFFFF)
private val BgGray = Color(0xFFF4F5FA)
private val TextGray = Color(0xFF7D8497)
private val TextDark = Color(0xFF303646)
private val InputBg = Color(0xFFF1F2FB)

@Composable
fun PlayerStatsScreen(
    playerName: String = "A carregar...",
    playerRank: String = "#14 GLOBAL",
    playerRole: String = "MIDFIELDER",
    playerTeam: String = "FC Mancos",
    playerCountry: String = "Portugal",
    footballGoals: Int = 0,
    footballAssists: Int = 0,
    basketballPoints: Int = 0,
    basketballWinRate: Int = 0,
    volleyballSpikes: Int = 0,
    volleyballWinRate: Int = 0,
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGray)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(BrandBlue)
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                color = BrandWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Profile",
                color = BrandWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "🔔",
                color = BrandWhite,
                fontSize = 20.sp
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            StatsHeaderCard(
                name = playerName,
                rank = playerRank,
                role = playerRole,
                team = playerTeam,
                country = playerCountry
            )

            Spacer(modifier = Modifier.height(24.dp))

            SportStatCard(
                sportName = "Football",
                sportIcon = "⚽",
                roleTag = "FORWARD",
                stat1Label = "GOALS",
                stat1Value = footballGoals.toString(),
                stat1Color = Color(0xFF3566C9),
                stat2Label = "ASSISTS",
                stat2Value = footballAssists.toString(),
                stat2Color = TextDark
            )

            Spacer(modifier = Modifier.height(16.dp))

            SportStatCard(
                sportName = "Basketball",
                sportIcon = "🏀",
                roleTag = "",
                stat1Label = "POINTS",
                stat1Value = basketballPoints.toString(),
                stat1Color = Color(0xFFC95E35),
                stat2Label = "WIN %",
                stat2Value = "$basketballWinRate%",
                stat2Color = BrandGreen
            )

            Spacer(modifier = Modifier.height(16.dp))

            SportStatCard(
                sportName = "Volleyball",
                sportIcon = "🏐",
                roleTag = "",
                stat1Label = "SPIKES",
                stat1Value = volleyballSpikes.toString(),
                stat1Color = TextDark,
                stat2Label = "WIN %",
                stat2Value = "$volleyballWinRate%",
                stat2Color = BrandGreen
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "MATCH HISTORY",
                    color = TextDark,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "VIEW ALL",
                    color = Color(0xFF3566C9),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "LAST 5 MATCHES",
                color = TextGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            MatchHistoryRow(team1 = "Sporting", team2 = "vianense", score = "3 - 1", subtitle = "Tournament Finals • Oct 14, 2023", resultIcon = "⚽ ⚽")
            MatchHistoryRow(team1 = "Benfica", team2 = "Porto", score = "0 - 0", subtitle = "League Match • Oct 10, 2023", resultIcon = "FULL TIME")
            MatchHistoryRow(team1 = "Vianense", team2 = "Benfica", score = "1 - 4", subtitle = "Quarter Finals • Oct 03, 2023", resultIcon = "⚽ ⚡")
            MatchHistoryRow(team1 = "Porto", team2 = "Sporting", score = "2 - 0", subtitle = "Exhibition • Sep 28, 2023", resultIcon = "⚽")

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun StatsHeaderCard(
    name: String,
    rank: String,
    role: String,
    team: String,
    country: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, BrandGreen, RoundedCornerShape(12.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 40.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(BrandGreen)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("RANK $rank", color = BrandWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF2B3F60))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(role, color = BrandWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = name,
                color = BrandWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👥", color = TextGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(team, color = BrandWhite, fontSize = 12.sp)
                }
                Text("|", color = TextGray, fontSize = 12.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("⚑", color = TextGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(country, color = BrandWhite, fontSize = 12.sp)
                }
            }
        }
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(InputBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(sportIcon, fontSize = 16.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = sportName,
                        color = TextDark,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (roleTag.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFE2E6F2))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(roleTag, color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(BrandWhite)
                        .border(1.dp, InputBg, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(stat1Label, color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(stat1Value, color = stat1Color, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(BrandWhite)
                        .border(1.dp, InputBg, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(stat2Label, color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(stat2Value, color = stat2Color, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    }
                }
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.dp, Color.LightGray, CircleShape),
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
            Text(
                text = resultIcon,
                color = BrandGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}