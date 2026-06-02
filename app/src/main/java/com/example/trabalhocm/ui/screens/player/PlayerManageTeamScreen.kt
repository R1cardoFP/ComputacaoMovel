package com.example.trabalhocm.ui.screens.player

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.trabalhocm.R
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite

data class ManageTeamPlayerMock(
    val name: String,
    val position: String,
    val number: String,
    val captain: Boolean = false
)

@Composable
fun PlayerManageTeamScreen(
    onBackClick: () -> Unit = {},
    onInvitePlayerClick: () -> Unit = {},
    onViewPlayerProfileClick: () -> Unit = {},
    onMakeCaptainClick: () -> Unit = {},
    onRemoveFromTeamClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var search by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("All") }
    var showOptions by remember { mutableStateOf(false) }

    val players = listOf(
        ManageTeamPlayerMock("Bruno Fernandes", "Midfielder", "#10", captain = true),
        ManageTeamPlayerMock("Cristiano Ronaldo", "Striker", "#9"),
        ManageTeamPlayerMock("Rúben Dias", "Defender", "#4"),
        ManageTeamPlayerMock("Diogo Costa", "Goalkeeper", "#1"),
        ManageTeamPlayerMock("João Cancelo", "Defender", "#20"),
        ManageTeamPlayerMock("Bernardo Silva", "Midfielder", "#8"),
        ManageTeamPlayerMock("João Félix", "Forward", "#11")
    )

    val filteredPlayers = players.filter { player ->
        val matchesRole =
            selectedRole == "All" ||
                    player.position == selectedRole ||
                    selectedRole == "Forward" && player.position == "Striker"

        val matchesSearch =
            search.isBlank() ||
                    player.name.contains(search, ignoreCase = true) ||
                    player.position.contains(search, ignoreCase = true)

        matchesRole && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        ManageTeamTopBar(
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 18.dp)
        ) {
            Text(
                text = "TEAM MANAGEMENT",
                color = Color(0xFF0757C8),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.8.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Manage your roster, invite new players and oversee\nteam composition.",
                color = Color(0xFF6D7486),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            ManageTeamHeaderCard()

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onInvitePlayerClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0757C8),
                    contentColor = BrandWhite
                )
            ) {
                Text(
                    text = "♙+  INVITE PLAYER",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Search roster...",
                        color = Color(0xFF9EA4B3),
                        fontSize = 12.sp
                    )
                },
                leadingIcon = {
                    Text(
                        text = "⌕",
                        color = Color(0xFF8D94A3),
                        fontSize = 16.sp
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

            ManageTeamRoleFilters(
                selectedRole = selectedRole,
                onRoleSelected = { selectedRole = it }
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "ROSTER (${filteredPlayers.size})",
                color = Color(0xFF7D8497),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            filteredPlayers.forEach { player ->
                ManageTeamPlayerRow(
                    player = player,
                    onOptionsClick = {
                        showOptions = true
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
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

    if (showOptions) {
        ManagePlayerOptionsDialog(
            onDismiss = {
                showOptions = false
            },
            onViewProfileClick = {
                showOptions = false
                onViewPlayerProfileClick()
            },
            onMakeCaptainClick = {
                showOptions = false
                onMakeCaptainClick()
            },
            onRemoveFromTeamClick = {
                showOptions = false
                onRemoveFromTeamClick()
            }
        )
    }
}

@Composable
fun ManageTeamTopBar(
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
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                onBackClick()
            }
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = "Teams",
            color = BrandWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic
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
fun ManageTeamHeaderCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(74.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFF4A555C).copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "FC",
                    color = BrandWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "FC Mancos",
                    color = BrandWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "11 players · Premier Tier",
                    color = Color(0xFFB8C2D3),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ManageTeamRoleFilters(
    selectedRole: String,
    onRoleSelected: (String) -> Unit
) {
    val roles = listOf("All", "Forward", "Midfielder", "Defender", "Goalkeeper")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        roles.forEach { role ->
            ManageTeamRoleButton(
                text = role,
                selected = selectedRole == role,
                onClick = {
                    onRoleSelected(role)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ManageTeamRoleButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(30.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(if (selected) Color(0xFF0757C8) else Color(0xFFEAF0FF))
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) BrandWhite else Color(0xFF0757C8),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ManageTeamPlayerRow(
    player: ManageTeamPlayerMock,
    onOptionsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (player.captain) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(68.dp)
                        .background(Color(0xFFB72D2D))
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.avatar_player),
                    contentDescription = player.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF0F2FA))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = player.name,
                            color = BrandBlue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (player.captain) {
                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFFFFE4E4))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "CAPTAIN",
                                    color = Color(0xFFB72D2D),
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "${player.position} · ${player.number}",
                        color = Color(0xFF7D8497),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "⋮",
                    color = Color(0xFF7D8497),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            onOptionsClick()
                        }
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ManagePlayerOptionsDialog(
    onDismiss: () -> Unit,
    onViewProfileClick: () -> Unit,
    onMakeCaptainClick: () -> Unit,
    onRemoveFromTeamClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 34.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = BrandWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                ManagePlayerOptionRow(
                    icon = "♙",
                    text = "View Profile",
                    textColor = BrandBlue,
                    onClick = onViewProfileClick
                )

                ManagePlayerOptionRow(
                    icon = "☆",
                    text = "Make Captain",
                    textColor = BrandBlue,
                    onClick = onMakeCaptainClick
                )

                ManagePlayerOptionRow(
                    icon = "♜",
                    text = "Remove from Team",
                    textColor = Color(0xFFD01818),
                    onClick = onRemoveFromTeamClick
                )
            }
        }
    }
}

@Composable
fun ManagePlayerOptionRow(
    icon: String,
    text: String,
    textColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable {
                onClick()
            }
            .padding(horizontal = 26.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            color = textColor,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = text,
            color = textColor,
            fontSize = 19.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, name = "Player Manage Team Screen")
@Composable
fun PlayerManageTeamScreenPreview() {
    PlayerManageTeamScreen()
}