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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite

data class PlayerTeamMock(
    val name: String,
    val division: String,
    val wins: Int,
    val losses: Int,
    val streak: String,
    val streakGood: Boolean,
    val isUserTeam: Boolean,
    val logoText: String,
    val logoColor: Color
)

@Composable
fun PlayerTeamsScreen(
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onTeamDetailsClick: (Boolean) -> Unit = {},
    onManageTeamClick: () -> Unit = {},
    onCreateTeamClick: () -> Unit = {}
) {
    var selectedDivision by remember { mutableStateOf("All Teams") }
    var search by remember { mutableStateOf("") }

    val teams = listOf(
        PlayerTeamMock(
            name = "FC Mancos",
            division = "Your Team",
            wins = 21,
            losses = 4,
            streak = "W3",
            streakGood = true,
            isUserTeam = true,
            logoText = "FC",
            logoColor = Color(0xFF4A555C)
        ),
        PlayerTeamMock(
            name = "Benfica",
            division = "Division B",
            wins = 24,
            losses = 6,
            streak = "W5",
            streakGood = true,
            isUserTeam = false,
            logoText = "B",
            logoColor = Color(0xFFE53935)
        ),
        PlayerTeamMock(
            name = "Porto",
            division = "Division A",
            wins = 18,
            losses = 12,
            streak = "L2",
            streakGood = false,
            isUserTeam = false,
            logoText = "P",
            logoColor = Color(0xFF0757C8)
        ),
        PlayerTeamMock(
            name = "Vianense",
            division = "Division A",
            wins = 15,
            losses = 15,
            streak = "W1",
            streakGood = true,
            isUserTeam = false,
            logoText = "V",
            logoColor = Color(0xFFD19A00)
        ),
        PlayerTeamMock(
            name = "Sporting",
            division = "Division B",
            wins = 8,
            losses = 22,
            streak = "L5",
            streakGood = false,
            isUserTeam = false,
            logoText = "S",
            logoColor = BrandGreen
        )
    )

    val filteredTeams = teams.filter { team ->
        val matchesDivision = selectedDivision == "All Teams" || team.division == selectedDivision
        val matchesSearch = search.isBlank() || team.name.contains(search, ignoreCase = true)
        matchesDivision && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        PlayerTeamsTopBar()

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 18.dp)
        ) {
            Text(
                text = "PREMIER LEAGUE TEAMS",
                color = Color(0xFF0757C8),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.8.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Browse Teams",
                color = BrandBlue,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Discover all active teams across the league\necosystem.",
                color = Color(0xFF6D7486),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Search for teams...",
                        color = Color(0xFF9EA4B3),
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Text(
                        text = "⌕",
                        color = Color(0xFF8D94A3),
                        fontSize = 17.sp
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                shape = RoundedCornerShape(7.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BrandWhite,
                    unfocusedContainerColor = BrandWhite,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = BrandGreen,
                    focusedTextColor = BrandBlue,
                    unfocusedTextColor = BrandBlue
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            TeamsDivisionTabs(
                selectedDivision = selectedDivision,
                onDivisionSelected = { selectedDivision = it }
            )

            Spacer(modifier = Modifier.height(18.dp))

            filteredTeams.forEach { team ->
                PlayerTeamCard(
                    team = team,
                    onDetailsClick = {
                        onTeamDetailsClick(team.isUserTeam)
                    },
                    onManageTeamClick = onManageTeamClick
                )

                Spacer(modifier = Modifier.height(14.dp))
            }

            Button(
                onClick = onCreateTeamClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0757C8),
                    contentColor = BrandWhite
                )
            ) {
                Text(
                    text = "⊙  CREATE TEAM",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        MatchLeagueBottomBar(
            selectedTab = "TEAMS",
            onHomeClick = onHomeClick,
            onTournamentsClick = onTournamentsClick,
            onMatchesClick = onMatchesClick,
            onTeamsClick = onTeamsClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun PlayerTeamsTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(BrandBlue)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Teams",
            color = BrandWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
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
fun TeamsDivisionTabs(
    selectedDivision: String,
    onDivisionSelected: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        TeamDivisionButton(
            text = "All Teams",
            selected = selectedDivision == "All Teams",
            onClick = { onDivisionSelected("All Teams") }
        )

        TeamDivisionButton(
            text = "Division A",
            selected = selectedDivision == "Division A",
            onClick = { onDivisionSelected("Division A") }
        )

        TeamDivisionButton(
            text = "Division B",
            selected = selectedDivision == "Division B",
            onClick = { onDivisionSelected("Division B") }
        )
    }
}

@Composable
fun TeamDivisionButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(32.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(if (selected) Color(0xFF0757C8) else Color(0xFFEAF0FF))
            .clickable { onClick() }
            .padding(horizontal = 13.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) BrandWhite else Color(0xFF0757C8),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PlayerTeamCard(
    team: PlayerTeamMock,
    onDetailsClick: () -> Unit,
    onManageTeamClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (team.isUserTeam) 1.dp else 0.dp,
                color = if (team.isUserTeam) BrandGreen else Color.Transparent,
                shape = RoundedCornerShape(7.dp)
            ),
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
                TeamLogoBox(
                    text = team.logoText,
                    color = team.logoColor
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = team.name,
                        color = BrandBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = team.division.uppercase(),
                        color = if (team.isUserTeam) BrandGreen else Color(0xFF0757C8),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TeamStat(
                    label = "WINS",
                    value = team.wins.toString(),
                    valueColor = Color(0xFF0757C8)
                )

                TeamStat(
                    label = "LOSSES",
                    value = team.losses.toString(),
                    valueColor = BrandBlue
                )

                TeamStat(
                    label = "STREAK",
                    value = team.streak,
                    valueColor = if (team.streakGood) BrandGreen else Color(0xFFE53935)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDetailsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(2.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF0757C8)
                    )
                ) {
                    Text(
                        text = "VIEW DETAILS  →",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (team.isUserTeam) {
                    Button(
                        onClick = onManageTeamClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandGreen,
                            contentColor = BrandWhite
                        )
                    ) {
                        Text(
                            text = "MANAGE TEAM",
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
fun TeamLogoBox(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TeamStat(
    label: String,
    value: String,
    valueColor: Color
) {
    Column {
        Text(
            text = label,
            color = Color(0xFF7D8497),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = value,
            color = valueColor,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, name = "Player Teams Screen")
@Composable
fun PlayerTeamsScreenPreview() {
    PlayerTeamsScreen()
}