package com.example.trabalhocm.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite

@Composable
fun PlayerTournamentDetailsScreen(
    onBackClick: () -> Unit = {},
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
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            TournamentDetailsHeader(
                onBackClick = onBackClick
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp, vertical = 18.dp)
            ) {
                TournamentDetailsAboutCard()

                Spacer(modifier = Modifier.height(14.dp))

                TournamentDetailsScheduleCard()

                Spacer(modifier = Modifier.height(14.dp))

                TournamentDetailsLocationCard()

                Spacer(modifier = Modifier.height(14.dp))

                TournamentDetailsStandingsCard()

                Spacer(modifier = Modifier.height(22.dp))
            }
        }

        TournamentDetailsBottomBar(
            onHomeClick = onHomeClick,
            onTournamentsClick = onTournamentsClick,
            onMatchesClick = onMatchesClick,
            onTeamsClick = onTeamsClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun TournamentDetailsHeader(
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(270.dp)
            .background(
                Brush.verticalGradient(
                    listOf(
                        BrandBlue,
                        Color(0xFF071A30)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
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

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Details",
                    color = BrandWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "♧",
                    color = BrandWhite,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DetailHeaderBadge(
                        text = "IN PROGRESS",
                        color = BrandGreen
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "● FOOTBALL",
                        color = BrandGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Premier Summer\nCup 2026",
                    color = BrandWhite,
                    fontSize = 26.sp,
                    lineHeight = 30.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(26.dp)
                ) {
                    DetailHeaderInfo(
                        label = "SEASON",
                        value = "25/26"
                    )

                    DetailHeaderInfo(
                        label = "PRIZE POOL",
                        value = "125,000 €"
                    )

                    DetailHeaderInfo(
                        label = "TEAMS",
                        value = "24"
                    )
                }
            }
        }
    }
}

@Composable
fun DetailHeaderBadge(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(BrandWhite)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "● $text",
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun DetailHeaderInfo(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            color = Color(0xFF9EA8BA),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.4.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            color = BrandWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TournamentDetailsAboutCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Text(
                text = "About",
                color = BrandBlue,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "A flagship summer competition for Premier Tier\nclubs across the western region. League format\nwith playoff finals.",
                color = Color(0xFF6D7486),
                fontSize = 13.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun TournamentDetailsScheduleCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Text(
                text = "Schedule",
                color = BrandBlue,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            ScheduleRow("Start Date", "15 Jun 2026")
            ScheduleRow("End Date", "30 Aug 2026")
            ScheduleRow("Registration Closes", "01 Jun 2026")
            ScheduleRow("Format", "League System")
        }
    }
}

@Composable
fun ScheduleRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color(0xFF6D7486),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = value,
            color = BrandBlue,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TournamentDetailsLocationCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Text(
                text = "Location",
                color = BrandBlue,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⊙",
                    color = Color(0xFF0757C8),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = "Estádio Cidade de Barcelos",
                        color = BrandBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Barcelos, Portugal",
                        color = Color(0xFF7D8497),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun TournamentDetailsStandingsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Text(
                text = "Standings",
                color = BrandBlue,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "POS",
                    color = Color(0xFF7D8497),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(36.dp)
                )

                Text(
                    text = "TEAM",
                    color = Color(0xFF7D8497),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "P",
                    color = Color(0xFF7D8497),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(32.dp)
                )

                Text(
                    text = "PTS",
                    color = Color(0xFF7D8497),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            StandingTeamRow(
                position = "01",
                team = "Porto",
                played = "12",
                points = "31",
                accent = Color(0xFF0757C8),
                logoType = "porto"
            )

            StandingTeamRow(
                position = "02",
                team = "Sporting",
                played = "12",
                points = "29",
                accent = BrandGreen,
                logoType = "sporting"
            )

            StandingTeamRow(
                position = "03",
                team = "Benfica",
                played = "12",
                points = "27",
                accent = Color(0xFFE53935),
                logoType = "benfica"
            )

            StandingTeamRow(
                position = "04",
                team = "Vianense",
                played = "12",
                points = "23",
                accent = Color(0xFFD19A00),
                logoType = "vianense"
            )
        }
    }
}

@Composable
fun StandingTeamRow(
    position: String,
    team: String,
    played: String,
    points: String,
    accent: Color,
    logoType: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = position,
            color = if (position == "01") BrandGreen else BrandBlue,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(36.dp)
        )

        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StandingLogo(
                logoType = logoType,
                accent = accent
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = team,
                color = BrandBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = played,
            color = Color(0xFF6D7486),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(32.dp)
        )

        Text(
            text = points,
            color = Color(0xFF0757C8),
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(38.dp)
        )
    }
}

@Composable
fun StandingLogo(
    logoType: String,
    accent: Color
) {
    if (logoType == "sporting") {
        Image(
            painter = painterResource(R.drawable.team_sporting),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            contentScale = ContentScale.Fit
        )
    } else if (logoType == "vianense") {
        Image(
            painter = painterResource(R.drawable.team_vianense),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            contentScale = ContentScale.Fit
        )
    } else {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(accent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (logoType == "porto") "P" else "B",
                color = accent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TournamentDetailsBottomBar(
    onHomeClick: () -> Unit,
    onTournamentsClick: () -> Unit,
    onMatchesClick: () -> Unit,
    onTeamsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
            .background(BrandWhite)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        TournamentDetailsBottomItem("⌂", "HOME", false, onHomeClick)
        TournamentDetailsBottomItem("♕", "TOURNAMENTS", true, onTournamentsClick)
        TournamentDetailsBottomItem("◎", "MATCHES", false, onMatchesClick)
        TournamentDetailsBottomItem("♟", "TEAMS", false, onTeamsClick)
        TournamentDetailsBottomItem("♙", "PROFILE", false, onProfileClick)
    }
}

@Composable
fun TournamentDetailsBottomItem(
    icon: String,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) Color(0xFF0757C8) else Color(0xFF9EA4B3)

    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            color = color,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = title,
            color = color,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, name = "Player Tournament Details Screen")
@Composable
fun PlayerTournamentDetailsScreenPreview() {
    PlayerTournamentDetailsScreen()
}