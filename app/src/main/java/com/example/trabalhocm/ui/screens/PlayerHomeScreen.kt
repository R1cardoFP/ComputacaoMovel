package com.example.trabalhocm.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite

@Composable
fun PlayerHomeScreen(
    onTournamentsClick: () -> Unit = {},
    onCasualMatchesClick: () -> Unit = {},
    onLiveMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        PlayerHomeTopBar()

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 20.dp)
        ) {
            PlayerLiveCard()

            Spacer(modifier = Modifier.height(28.dp))

            SectionTitle(title = "QUICK ACTIONS")

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = "♕",
                    title = "TOURNAMENTS",
                    onClick = onTournamentsClick
                )

                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = "⊕",
                    title = "CASUAL MATCHES",
                    onClick = onCasualMatchesClick
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = "⌁",
                    title = "LIVE MATCHS",
                    onClick = onLiveMatchesClick
                )

                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = "♟",
                    title = "TEAMS",
                    onClick = onTeamsClick
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                SectionTitle(title = "ACTIVE TOURNAMENTS")

                Text(
                    text = "VIEW ALL",
                    color = Color(0xFF4167C8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            ActiveTournamentCard(
                title = "Summer Kickoff '26",
                role = "ORGANIZER",
                progress = 0.65f,
                progressText = "65%",
                accentColor = BrandGreen,
                icon = "♕"
            )

            Spacer(modifier = Modifier.height(14.dp))

            ActiveTournamentCard(
                title = "Pro Elite Series",
                role = "CAPTAIN",
                progress = 0.12f,
                progressText = "12%",
                accentColor = Color(0xFF3566C9),
                icon = "♖"
            )

            Spacer(modifier = Modifier.height(30.dp))

            SectionTitle(title = "UPCOMING FIXTURES")

            Spacer(modifier = Modifier.height(14.dp))

            UpcomingFixturesCard()

            Spacer(modifier = Modifier.height(30.dp))

            SectionTitle(title = "PERFORMANCE INSIGHTS")

            Spacer(modifier = Modifier.height(14.dp))

            PlayerOfWeekCard()

            Spacer(modifier = Modifier.height(14.dp))

            GlobalRankCard()

            Spacer(modifier = Modifier.height(22.dp))
        }
    }
}

@Composable
fun PlayerHomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
            .background(BrandBlue)
            .padding(horizontal = 28.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Home",
            color = BrandWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "♧",
            color = BrandWhite,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PlayerLiveCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(345.dp),
        shape = RoundedCornerShape(8.dp),
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
                            Color(0xFF071A30)
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(BrandGreen.copy(alpha = 0.95f))
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "LIVE\nNOW",
                            color = Color(0xFF9DF4E9),
                            fontSize = 12.sp,
                            lineHeight = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.4.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.width(18.dp))

                    Text(
                        text = "PREMIER LEAGUE • GW\n26",
                        color = Color(0xFFA6AFBD),
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    )
                }

                Spacer(modifier = Modifier.height(36.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TeamScoreBlock(
                        logo = R.drawable.team_sporting,
                        name = "Sporting"
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "1",
                            color = BrandWhite,
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Text(
                            text = "-",
                            color = BrandGreen,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Text(
                            text = "2",
                            color = BrandWhite,
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    TeamScoreBlock(
                        logo = R.drawable.team_vianense,
                        name = "Vianense"
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "83'",
                    color = BrandGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {},
                    modifier = Modifier
                        .width(172.dp)
                        .height(54.dp),
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen,
                        contentColor = BrandWhite
                    )
                ) {
                    Text(
                        text = "WATCH STREAM",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TeamScoreBlock(
    logo: Int,
    name: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(logo),
            contentDescription = name,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = name,
            color = BrandWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color(0xFF7D8497),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 3.sp
    )
}

@Composable
fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(112.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFFF0F2FA)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    color = BrandGreen,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                color = Color(0xFF303646),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ActiveTournamentCard(
    title: String,
    role: String,
    progress: Float,
    progressText: String,
    accentColor: Color,
    icon: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(116.dp),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxSize()
                    .background(accentColor)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 17.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = title,
                            color = BrandBlue,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = role,
                            color = accentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }

                    Text(
                        text = icon,
                        color = Color(0xFFE2E6F2),
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "PROGRESS",
                        color = Color(0xFF6D7486),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = progressText,
                        color = Color(0xFF6D7486),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    color = accentColor,
                    trackColor = Color(0xFFECEEF7)
                )
            }
        }
    }
}

@Composable
fun UpcomingFixturesCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            FixtureRow(
                date = "SEP 22",
                time = "19:45",
                homeLogo = R.drawable.team_sporting,
                awayLogo = R.drawable.team_vianense
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE8EAF2))
            )

            FixtureRow(
                date = "SEP 24",
                time = "21:00",
                homeLogo = R.drawable.team_vianense,
                awayLogo = R.drawable.team_sporting
            )
        }
    }
}

@Composable
fun FixtureRow(
    date: String,
    time: String,
    homeLogo: Int,
    awayLogo: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.width(72.dp)
        ) {
            Text(
                text = date,
                color = BrandBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = time,
                color = Color(0xFF7D8497),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Box(
            modifier = Modifier
                .width(1.dp)
                .height(34.dp)
                .background(Color(0xFFE8EAF2))
        )

        Spacer(modifier = Modifier.width(28.dp))

        Image(
            painter = painterResource(homeLogo),
            contentDescription = null,
            modifier = Modifier.size(34.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.width(20.dp))

        Text(
            text = "VS",
            color = Color(0xFFD2D6E3),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(20.dp))

        Image(
            painter = painterResource(awayLogo),
            contentDescription = null,
            modifier = Modifier.size(34.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color(0xFFF0F2FA)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "♧",
                color = Color(0xFF7D8497),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun PlayerOfWeekCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(262.dp),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BrandBlue)
                .padding(24.dp)
        ) {
            Text(
                text = "PLAYER OF THE WEEK",
                color = BrandGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp,
                modifier = Modifier.align(Alignment.TopCenter)
            )

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.avatar_player),
                    contentDescription = "Cristiano Ronaldo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(88.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF0F2FA))
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "CRISTIANO RONALDO",
                    color = BrandWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Arabias • ATTK",
                    color = BrandGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                PlayerStat(value = "04", label = "GOALS")
                PlayerStat(value = "02", label = "ASSISTS")
                PlayerStat(value = "9.4", label = "RATING")
            }
        }
    }
}

@Composable
fun PlayerStat(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = BrandWhite,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            color = Color(0xFF7D8497),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun GlobalRankCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFF0F2FA)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▮",
                    color = Color(0xFF3566C9),
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column {
                Text(
                    text = "YOUR GLOBAL RANK",
                    color = Color(0xFF7D8497),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.8.sp
                )

                Text(
                    text = "#142",
                    color = BrandBlue,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "↗ 12%",
                color = BrandGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, name = "Player Home Screen")
@Composable
fun PlayerHomeScreenPreview() {
    PlayerHomeScreen()
}