package com.example.trabalhocm.ui.screens

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite

@Composable
fun PlayerTournamentManagementScreen(
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onDetailsClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onAskOrganizerClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onFiltersClick: () -> Unit = {}
) {
    var search by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        PlayerTournamentTopBar()

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Tournament\nManagement",
                color = BrandBlue,
                fontSize = 30.sp,
                lineHeight = 33.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Visualize and manage all your active and upcoming\nleagues.",
                color = Color(0xFF6D7486),
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TournamentMainActionButton(
                    modifier = Modifier.weight(1f),
                    text = "HISTORY",
                    icon = "◷",
                    backgroundColor = Color(0xFFF0F2FA),
                    textColor = BrandBlue,
                    onClick = onHistoryClick
                )

                TournamentMainActionButton(
                    modifier = Modifier.weight(1f),
                    text = "ASK TO BE\nORGANIZER",
                    icon = "⊕",
                    backgroundColor = Color(0xFF0757C8),
                    textColor = BrandWhite,
                    onClick = onAskOrganizerClick
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            TournamentSearchAndFilters(
                search = search,
                onSearchChange = { search = it },
                onFiltersClick = onFiltersClick
            )

            Spacer(modifier = Modifier.height(22.dp))

            PlayerTournamentCard(
                status = "LIVE",
                statusColor = Color(0xFFE53935),
                tags = listOf("PRO LEAGUE", "FOOTBALL"),
                infoText = "YOU Joined",
                title = "Premier Summer Cup 2026",
                date = "15 Jun – 30 Aug",
                teamsText = "24",
                gamesText = "6",
                progress = null,
                progressColor = BrandGreen,
                primaryButtonText = null,
                secondaryButtonText = "DETAILS",
                disabledButton = false,
                onDetailsClick = onDetailsClick,
                onPrimaryClick = onRegisterClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            PlayerTournamentCard(
                status = "OPEN",
                statusColor = BrandGreen,
                tags = listOf("AMATEUR", "BASKETBALL"),
                infoText = null,
                title = "Liga Regional Sul",
                date = "Start: 10 Sep",
                teamsText = null,
                gamesText = null,
                registeredText = "12/16",
                progress = 0.75f,
                progressColor = BrandGreen,
                primaryButtonText = "REGISTER NOW",
                secondaryButtonText = "DETAILS",
                disabledButton = false,
                onDetailsClick = onDetailsClick,
                onPrimaryClick = onRegisterClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            PlayerTournamentCard(
                status = "SOLD OUT",
                statusColor = Color(0xFFD19A00),
                tags = listOf("PRO LEAGUE", "FOOTBALL"),
                infoText = null,
                title = "Atlantic Cup 2026",
                date = "Start: 1 Oct",
                teamsText = null,
                gamesText = null,
                registeredText = "32/32",
                progress = 1f,
                progressColor = Color(0xFFD19A00),
                primaryButtonText = "ALL SLOTS FILLED",
                secondaryButtonText = "DETAILS",
                disabledButton = true,
                onDetailsClick = onDetailsClick,
                onPrimaryClick = onRegisterClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            PlayerTournamentCard(
                status = "INVITE ONLY",
                statusColor = Color(0xFF3566C9),
                tags = listOf("ELITE", "VOLLEYBALL"),
                infoText = null,
                title = "Elite Invitational 2026",
                date = "Start: 12 Nov",
                teamsText = null,
                gamesText = null,
                registeredText = null,
                progress = null,
                progressColor = Color(0xFF3566C9),
                primaryButtonText = "INVITE ONLY — LOCKED",
                secondaryButtonText = "DETAILS",
                disabledButton = true,
                onDetailsClick = onDetailsClick,
                onPrimaryClick = onRegisterClick
            )

            Spacer(modifier = Modifier.height(22.dp))
        }

        PlayerTournamentBottomBar(
            onHomeClick = onHomeClick,
            onTournamentsClick = onTournamentsClick,
            onMatchesClick = onMatchesClick,
            onTeamsClick = onTeamsClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun PlayerTournamentTopBar() {
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
            text = "List",
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
fun TournamentMainActionButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(58.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text,
                color = textColor,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun TournamentSearchAndFilters(
    search: String,
    onSearchChange: (String) -> Unit,
    onFiltersClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            OutlinedTextField(
                value = search,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Search tournaments...",
                        color = Color(0xFF9EA4B3),
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Text(
                        text = "⌕",
                        color = Color(0xFF8D94A3),
                        fontSize = 18.sp
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                shape = RoundedCornerShape(5.dp),
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

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterBox(
                    modifier = Modifier.weight(1f),
                    text = "Status: All"
                )

                FilterBox(
                    modifier = Modifier.weight(1f),
                    text = "Region: All"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onFiltersClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(
                    text = "≡  FILTERS",
                    color = BrandBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FilterBox(
    modifier: Modifier = Modifier,
    text: String
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(Color(0xFFF0F2FA))
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            color = BrandBlue,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PlayerTournamentCard(
    status: String,
    statusColor: Color,
    tags: List<String>,
    infoText: String?,
    title: String,
    date: String,
    teamsText: String?,
    gamesText: String?,
    registeredText: String? = null,
    progress: Float?,
    progressColor: Color,
    primaryButtonText: String?,
    secondaryButtonText: String,
    disabledButton: Boolean,
    onDetailsClick: () -> Unit,
    onPrimaryClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TournamentBadge(
                    text = status,
                    color = statusColor,
                    strong = true
                )

                Spacer(modifier = Modifier.width(6.dp))

                tags.forEach { tag ->
                    TournamentBadge(
                        text = tag,
                        color = Color(0xFF7D8497),
                        strong = false
                    )

                    Spacer(modifier = Modifier.width(6.dp))
                }
            }

            if (!infoText.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = infoText,
                    color = BrandGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = BrandBlue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = "▣  $date",
                color = Color(0xFF7D8497),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(13.dp))

            if (teamsText != null && gamesText != null) {
                Row {
                    Column {
                        Text(
                            text = "TEAMS",
                            color = Color(0xFF7D8497),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = teamsText,
                            color = BrandBlue,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    Column {
                        Text(
                            text = "GAMES TODAY",
                            color = Color(0xFF7D8497),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = gamesText,
                            color = Color(0xFF0757C8),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (progress != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "REGISTERED",
                        color = Color(0xFF7D8497),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (!registeredText.isNullOrBlank()) {
                        Text(
                            text = registeredText,
                            color = BrandBlue,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    color = progressColor,
                    trackColor = Color(0xFFECEEF7)
                )

                Spacer(modifier = Modifier.height(18.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDetailsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text(
                        text = "⊙  $secondaryButtonText",
                        color = Color(0xFF0757C8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (!primaryButtonText.isNullOrBlank()) {
                    Button(
                        onClick = onPrimaryClick,
                        modifier = Modifier
                            .weight(1.2f)
                            .height(42.dp),
                        enabled = !disabledButton,
                        shape = RoundedCornerShape(3.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (disabledButton) {
                                Color(0xFFDDE1EA)
                            } else {
                                BrandGreen
                            },
                            disabledContainerColor = Color(0xFFDDE1EA),
                            contentColor = BrandWhite,
                            disabledContentColor = Color(0xFF7D8497)
                        )
                    ) {
                        Text(
                            text = if (disabledButton) primaryButtonText else "✓  $primaryButtonText",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TournamentBadge(
    text: String,
    color: Color,
    strong: Boolean
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (strong) color.copy(alpha = 0.12f)
                else Color(0xFFF0F2FA)
            )
            .padding(horizontal = 9.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "● $text",
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PlayerTournamentBottomBar(
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
        PlayerTournamentBottomItem("⌂", "HOME", false, onHomeClick)
        PlayerTournamentBottomItem("♕", "TOURNAMENTS", true, onTournamentsClick)
        PlayerTournamentBottomItem("◎", "MATCHES", false, onMatchesClick)
        PlayerTournamentBottomItem("♟", "TEAMS", false, onTeamsClick)
        PlayerTournamentBottomItem("♙", "PROFILE", false, onProfileClick)
    }
}

@Composable
fun PlayerTournamentBottomItem(
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

@Preview(showBackground = true, name = "Player Tournament Management Screen")
@Composable
fun PlayerTournamentManagementScreenPreview() {
    PlayerTournamentManagementScreen()
}