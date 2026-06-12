package com.example.trabalhocm.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.model.AdminPlayerDetails
import com.example.trabalhocm.data.repository.AdminPlayerDetailsRepository
import com.example.trabalhocm.ui.theme.AppIcons
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import com.example.trabalhocm.ui.theme.CardBg
import com.example.trabalhocm.ui.theme.ErrorRed
import com.example.trabalhocm.ui.theme.LightBlueBadge
import com.example.trabalhocm.ui.theme.PrimaryBlue
import com.example.trabalhocm.ui.theme.TextGray
import kotlinx.coroutines.launch
import com.example.trabalhocm.data.repository.AuthRepository

@Composable
fun AdminPlayerDetailsScreen(
    playerId: String,
    teamId: String? = null,
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSuspendUserClick: (String) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { AdminPlayerDetailsRepository() }
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()

    var player by remember { mutableStateOf<AdminPlayerDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var actionMessage by remember { mutableStateOf("") }
    var refreshKey by remember { mutableStateOf(0) }

    LaunchedEffect(playerId, teamId, refreshKey) {
        isLoading = true
        errorMessage = ""

        repository.obterDetalhesJogador(playerId, teamId)
            .onSuccess {
                player = it
            }
            .onFailure {
                errorMessage = "Error loading player details: ${it.message}"
            }

        isLoading = false
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            AdminPlayerDetailsTopBar(
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick
            )
        },
        bottomBar = {
            AdminPlayerDetailsBottomBar(
                selected = "teams",
                onHomeClick = onHomeClick,
                onTournamentsClick = onTournamentsClick,
                onMatchesClick = onMatchesClick,
                onTeamsClick = onTeamsClick,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandGreen)
                }
            }

            errorMessage.isNotBlank() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage,
                        color = ErrorRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            player != null -> {
                AdminPlayerDetailsContent(
                    player = player!!,
                    innerPadding = innerPadding,
                    actionMessage = actionMessage,
                    onResetPasswordClick = { email ->
                        scope.launch {
                            authRepository.recuperarPassword(email)
                                .onSuccess {
                                    actionMessage = "Reset email sent to $email."
                                }
                                .onFailure {
                                    actionMessage = "Error sending reset email: ${it.message}"
                                }
                        }
                    },
                    onSuspendUserClick = { id ->
                        scope.launch {
                            val currentPlayer = player

                            if (currentPlayer?.suspended == true) {
                                repository.reativarUtilizador(id)
                                    .onSuccess {
                                        actionMessage = "Account reactivated successfully."
                                        refreshKey++
                                    }
                                    .onFailure {
                                        actionMessage = "Error reactivating account: ${it.message}"
                                    }
                            } else {
                                repository.suspenderUtilizador(id)
                                    .onSuccess {
                                        actionMessage = "Account suspended successfully."
                                        refreshKey++
                                    }
                                    .onFailure {
                                        actionMessage = "Error suspending account: ${it.message}"
                                    }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AdminPlayerDetailsContent(
    player: AdminPlayerDetails,
    innerPadding: PaddingValues,
    actionMessage: String,
    onResetPasswordClick: (String) -> Unit,
    onSuspendUserClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(
            start = 18.dp,
            end = 18.dp,
            top = 18.dp,
            bottom = 28.dp
        ),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        item {
            PlayerProfileHeader(player = player)
        }

        item {
            AccountInformationCard(player = player)
        }

        item {
            Text(
                text = "Season Stats",
                color = BrandBlue,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PlayerStatCard(
                    modifier = Modifier.weight(1f),
                    title = "GOALS",
                    value = player.goals.toString()
                )

                PlayerStatCard(
                    modifier = Modifier.weight(1f),
                    title = "ASSISTS",
                    value = player.assists.toString()
                )
            }
        }

        if (actionMessage.isNotBlank()) {
            item {
                Text(
                    text = actionMessage,
                    color = if (actionMessage.startsWith("Error")) ErrorRed else BrandGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            AdminActionsCard(
                player = player,
                onResetPasswordClick = onResetPasswordClick,
                onSuspendUserClick = onSuspendUserClick
            )
        }
    }
}

@Composable
private fun PlayerProfileHeader(player: AdminPlayerDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE95A5A)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = player.initials,
                    color = BrandWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = player.nome,
                color = BrandBlue,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SmallPlayerBadge(
                    text = player.position.uppercase(),
                    background = LightBlueBadge,
                    textColor = PrimaryBlue
                )

                SmallPlayerBadge(
                    text = player.equipa.uppercase(),
                    background = Color(0xFFE5E7EB),
                    textColor = TextGray
                )
            }

            if (player.equipas.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CURRENT TEAMS",
                        color = TextGray,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = player.equipas.joinToString(" · "),
                        color = BrandBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                PlayerMainInfo(
                    label = "AGE",
                    value = player.age
                )

                PlayerMainInfo(
                    label = "HEIGHT",
                    value = player.height
                )

                PlayerMainInfo(
                    label = "NUMBER",
                    value = player.number
                )
            }
        }
    }
}

@Composable
private fun PlayerMainInfo(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = TextGray,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = value,
            color = BrandBlue,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SmallPlayerBadge(
    text: String,
    background: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .padding(horizontal = 9.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.4.sp
        )
    }
}

@Composable
private fun AccountInformationCard(player: AdminPlayerDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(196.dp)
                    .background(
                        if (player.suspended) Color(0xFFEAB308) else BrandGreen
                    )
                    .align(Alignment.CenterStart)
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Account Information",
                        color = BrandBlue,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    SmallPlayerBadge(
                        text = if (player.suspended) "SUSPENDED" else "ACTIVE",
                        background = if (player.suspended) Color(0xFFFEF3C7) else Color(0xFFEAF8F5),
                        textColor = if (player.suspended) Color(0xFFEAB308) else BrandGreen
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                AccountInfoRow("Email", player.email)
                AccountInfoRow("User ID", "#${player.id.take(8).uppercase()}")
                AccountInfoRow("Member since", player.memberSince)
                AccountInfoRow("Last active", player.lastActive)
                AccountInfoRow(
                    label = "Account status",
                    value = if (player.suspended) "SUSPENDED" else player.accountStatus.uppercase(),
                    valueBadge = true,
                    badgePositive = !player.suspended
                )
                AccountInfoRow(
                    label = "2FA Enabled",
                    value = if (player.twoFactorEnabled) "YES" else "NO",
                    valueBadge = true,
                    badgePositive = player.twoFactorEnabled
                )
            }
        }
    }
}

@Composable
private fun AccountInfoRow(
    label: String,
    value: String,
    valueBadge: Boolean = false,
    badgePositive: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextGray,
            fontSize = 12.sp
        )

        if (valueBadge) {
            SmallPlayerBadge(
                text = value,
                background = if (badgePositive) Color(0xFFEAF8F5) else Color(0xFFFEF3C7),
                textColor = if (badgePositive) BrandGreen else Color(0xFFEAB308)
            )
        } else {
            Text(
                text = value,
                color = BrandBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun PlayerStatCard(
    modifier: Modifier,
    title: String,
    value: String
) {
    Card(
        modifier = modifier.height(94.dp),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                color = TextGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Text(
                text = value,
                color = PrimaryBlue,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AdminActionsCard(
    player: AdminPlayerDetails,
    onResetPasswordClick: (String) -> Unit,
    onSuspendUserClick: (String) -> Unit
) {
    val suspendColor = if (player.suspended) BrandGreen else Color(0xFFEAB308)
    val suspendText = if (player.suspended) "REACTIVATE ACCOUNT" else "SUSPEND ACCOUNT"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF8F5)),
        border = BorderStroke(1.dp, BrandGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = AppIcons.Security,
                    contentDescription = "Admin actions",
                    tint = BrandGreen,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Admin Actions",
                    color = BrandGreen,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(13.dp))

            Button(
                onClick = {
                    onResetPasswordClick(player.email)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandWhite,
                    contentColor = BrandBlue
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "SEND RESET EMAIL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(9.dp))

            Button(
                onClick = {
                    onSuspendUserClick(player.id)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(5.dp),
                border = BorderStroke(1.dp, suspendColor),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandWhite,
                    contentColor = suspendColor
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = suspendText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AdminPlayerDetailsTopBar(
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandBlue)
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                onBackClick()
            }
        ) {
            Icon(
                imageVector = AppIcons.Back,
                contentDescription = "Voltar",
                tint = BrandWhite,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Player Profile",
                color = BrandWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Icon(
            imageVector = AppIcons.Notifications,
            contentDescription = "Notificações",
            tint = BrandWhite,
            modifier = Modifier
                .size(23.dp)
                .clickable {
                    onNotificationsClick()
                }
        )
    }
}

@Composable
private fun AdminPlayerDetailsBottomBar(
    selected: String,
    onHomeClick: () -> Unit,
    onTournamentsClick: () -> Unit,
    onMatchesClick: () -> Unit,
    onTeamsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandWhite)
            .navigationBarsPadding()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomPlayerDetailsItem(AppIcons.Home, "HOME", selected == "home", onHomeClick)
        BottomPlayerDetailsItem(AppIcons.Tournaments, "TOURNAMENTS", selected == "tournaments", onTournamentsClick)
        BottomPlayerDetailsItem(AppIcons.Games, "MATCHES", selected == "matches", onMatchesClick)
        BottomPlayerDetailsItem(AppIcons.Teams, "TEAMS", selected == "teams", onTeamsClick)
        BottomPlayerDetailsItem(AppIcons.Profile, "PROFILE", selected == "profile", onProfileClick)
    }
}

@Composable
private fun BottomPlayerDetailsItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) PrimaryBlue else TextGray

    Column(
        modifier = Modifier.clickable {
            onClick()
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = color,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminPlayerDetailsScreenPreview() {
    AdminPlayerDetailsScreen(
        playerId = "9ffdf3d8-96b5-46ea-b36f-181b490602f6"
    )
}