package com.example.trabalhocm.ui.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite

@Composable
fun PlayerTournamentHistoryScreen(
    onBackClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        TournamentHistoryTopBar(
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 24.dp)
        ) {
            Text(
                text = "YOUR RECORDS",
                color = Color(0xFF0757C8),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "My Tournament History",
                color = BrandBlue,
                fontSize = 28.sp,
                lineHeight = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Tournaments you have organized or participated in.",
                color = Color(0xFF6D7486),
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(28.dp))

            TournamentHistoryCard(
                accentColor = BrandGreen,
                chips = listOf(
                    HistoryChip("COMPLETED", BrandGreen),
                    HistoryChip("CHAMPION", Color(0xFF0757C8))
                ),
                title = "Premier Summer Cup 2026",
                subtitle = "Football · League System · Season 25/26",
                role = "Organizer",
                finalPosition = "1st",
                played = "14",
                goalsLabel = "GOALS",
                goals = "28",
                footer = "Champion: FC Mancos · Prize €125 000"
            )

            Spacer(modifier = Modifier.height(14.dp))

            TournamentHistoryCard(
                accentColor = Color(0xFF0757C8),
                chips = listOf(
                    HistoryChip("ARCHIVED", Color(0xFF7D8497)),
                    HistoryChip("RUNNER-UP", Color(0xFF0757C8))
                ),
                title = "Liga Regional Sul 2025",
                subtitle = "Basketball · Knockout · Season 24/25",
                role = "Player",
                finalPosition = "2nd",
                played = "9",
                goalsLabel = "PTS/GAME",
                goals = "14.2",
                footer = null
            )

            Spacer(modifier = Modifier.height(14.dp))

            TournamentHistoryCard(
                accentColor = Color(0xFFB7BDC9),
                chips = listOf(
                    HistoryChip("ARCHIVED", Color(0xFF7D8497))
                ),
                title = "Copa Inverno 2024",
                subtitle = "Football · Group + Knockout · Season 23/24",
                role = "Player",
                finalPosition = "QF",
                played = "5",
                goalsLabel = "GOALS",
                goals = "3",
                footer = null
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TournamentHistoryTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(78.dp)
            .background(BrandBlue)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "←",
            color = BrandWhite,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                onBackClick()
            }
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = "Tournament History",
            color = BrandWhite,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "♧",
            color = BrandWhite,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

data class HistoryChip(
    val text: String,
    val color: Color
)

@Composable
fun TournamentHistoryCard(
    accentColor: Color,
    chips: List<HistoryChip>,
    title: String,
    subtitle: String,
    role: String,
    finalPosition: String,
    played: String,
    goalsLabel: String,
    goals: String,
    footer: String?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (footer == null) 142.dp else 166.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(accentColor)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 15.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    chips.forEach { chip ->
                        HistoryBadge(
                            text = chip.text,
                            color = chip.color
                        )

                        Spacer(modifier = Modifier.width(6.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = title,
                    color = BrandBlue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = subtitle,
                    color = Color(0xFF6D7486),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    HistoryStat(
                        label = "MY ROLE",
                        value = role
                    )

                    HistoryStat(
                        label = "FINAL POS.",
                        value = finalPosition
                    )

                    HistoryStat(
                        label = "PLAYED",
                        value = played
                    )

                    HistoryStat(
                        label = goalsLabel,
                        value = goals,
                        valueColor = Color(0xFF0757C8)
                    )
                }

                if (!footer.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = footer,
                        color = Color(0xFF6D7486),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun HistoryBadge(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun HistoryStat(
    label: String,
    value: String,
    valueColor: Color = BrandBlue
) {
    Column {
        Text(
            text = label,
            color = Color(0xFF7D8497),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = value,
            color = valueColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, name = "Player Tournament History Screen")
@Composable
fun PlayerTournamentHistoryScreenPreview() {
    PlayerTournamentHistoryScreen()
}