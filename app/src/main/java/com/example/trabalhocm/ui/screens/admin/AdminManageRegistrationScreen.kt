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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.model.AdminManageRegistrationData
import com.example.trabalhocm.data.model.AdminRegistrationTeam
import com.example.trabalhocm.data.repository.AdminRegistrationRepository
import com.example.trabalhocm.ui.theme.AppIcons
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import com.example.trabalhocm.ui.theme.CardBg
import com.example.trabalhocm.ui.theme.ErrorRed
import com.example.trabalhocm.ui.theme.InputBg
import com.example.trabalhocm.ui.theme.LightBlueBadge
import com.example.trabalhocm.ui.theme.LightRedBadge
import com.example.trabalhocm.ui.theme.PrimaryBlue
import com.example.trabalhocm.ui.theme.TextGray
import kotlinx.coroutines.launch

@Composable
fun AdminManageRegistrationScreen(
    tournamentId: String,
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onInviteTeamClick: (String) -> Unit = {},
    onTeamDetailsClick: (String) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { AdminRegistrationRepository() }
    val scope = rememberCoroutineScope()

    var data by remember { mutableStateOf<AdminManageRegistrationData?>(null) }
    var searchText by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("pending") }
    var isLoading by remember { mutableStateOf(true) }
    var actionMessage by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var refreshKey by remember { mutableStateOf(0) }

    LaunchedEffect(tournamentId, refreshKey) {
        isLoading = true
        errorMessage = ""

        repository.carregarGestaoInscricoes(tournamentId)
            .onSuccess {
                data = it
            }
            .onFailure {
                errorMessage = "Erro ao carregar inscrições: ${it.message}"
            }

        isLoading = false
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            ManageRegistrationTopBar(
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick
            )
        },
        bottomBar = {
            ManageRegistrationBottomBar(
                selected = "tournaments",
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

            data != null -> {
                val screenData = data!!

                val pendingFiltered = screenData.pendingTeams.filter {
                    it.teamName.contains(searchText, ignoreCase = true) ||
                            it.captainName.contains(searchText, ignoreCase = true)
                }

                val approvedFiltered = screenData.approvedTeams.filter {
                    it.teamName.contains(searchText, ignoreCase = true) ||
                            it.captainName.contains(searchText, ignoreCase = true)
                }

                val rejectedFiltered = screenData.rejectedTeams.filter {
                    it.teamName.contains(searchText, ignoreCase = true) ||
                            it.captainName.contains(searchText, ignoreCase = true)
                }

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
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    item {
                        Column {
                            Text(
                                text = "ADMIN TOOL",
                                color = BrandGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Manage Registration",
                                color = BrandBlue,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            Text(
                                text = "Review applications and manage participating teams.",
                                color = TextGray,
                                fontSize = 12.sp
                            )
                        }
                    }

                    item {
                        TournamentRegistrationHero(data = screenData)
                    }

                    item {
                        Button(
                            onClick = {
                                onInviteTeamClick(tournamentId)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(43.dp),
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryBlue,
                                contentColor = BrandWhite
                            )
                        ) {
                            Icon(
                                imageVector = AppIcons.Add,
                                contentDescription = "Convidar equipa",
                                tint = BrandWhite,
                                modifier = Modifier.size(17.dp)
                            )

                            Spacer(modifier = Modifier.width(7.dp))

                            Text(
                                text = "INVITE TEAM",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    item {
                        RegistrationSearchBox(
                            value = searchText,
                            onValueChange = {
                                searchText = it
                            }
                        )
                    }

                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RegistrationTabChip(
                                text = "All",
                                count = screenData.pendingTeams.size +
                                        screenData.approvedTeams.size +
                                        screenData.rejectedTeams.size,
                                selected = selectedTab == "all"
                            ) {
                                selectedTab = "all"
                            }

                            RegistrationTabChip(
                                text = "Pending",
                                count = screenData.pendingTeams.size,
                                selected = selectedTab == "pending"
                            ) {
                                selectedTab = "pending"
                            }

                            RegistrationTabChip(
                                text = "Approved",
                                count = screenData.approvedTeams.size,
                                selected = selectedTab == "approved"
                            ) {
                                selectedTab = "approved"
                            }
                        }
                    }

                    if (actionMessage.isNotBlank()) {
                        item {
                            Text(
                                text = actionMessage,
                                color = if (actionMessage.startsWith("Erro")) ErrorRed else BrandGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (selectedTab == "all" || selectedTab == "pending") {
                        item {
                            SectionTitle("PENDING APPROVAL (${pendingFiltered.size})")
                        }

                        if (pendingFiltered.isEmpty()) {
                            item {
                                EmptyRegistrationCard("Sem pedidos pendentes.")
                            }
                        }

                        items(pendingFiltered) { team ->
                            PendingTeamCard(
                                team = team,
                                onRejectClick = {
                                    scope.launch {
                                        repository.rejeitarInscricao(team.registrationId)
                                            .onSuccess {
                                                actionMessage = "Pedido rejeitado."
                                                refreshKey++
                                            }
                                            .onFailure {
                                                actionMessage = "Erro ao rejeitar: ${it.message}"
                                            }
                                    }
                                },
                                onApproveClick = {
                                    scope.launch {
                                        repository.aprovarInscricao(team.registrationId)
                                            .onSuccess {
                                                actionMessage = "Pedido aprovado."
                                                refreshKey++
                                            }
                                            .onFailure {
                                                actionMessage = "Erro ao aprovar: ${it.message}"
                                            }
                                    }
                                },
                                onDetailsClick = {
                                    onTeamDetailsClick(team.teamId)
                                }
                            )
                        }
                    }

                    if (selectedTab == "all" || selectedTab == "approved") {
                        item {
                            SectionTitle("APPROVED TEAMS (${approvedFiltered.size})")
                        }

                        if (approvedFiltered.isEmpty()) {
                            item {
                                EmptyRegistrationCard("Sem equipas aprovadas.")
                            }
                        }

                        items(approvedFiltered) { team ->
                            ApprovedTeamCard(
                                team = team,
                                onRemoveClick = {
                                    scope.launch {
                                        repository.removerInscricao(team.registrationId)
                                            .onSuccess {
                                                actionMessage = "Equipa removida do torneio."
                                                refreshKey++
                                            }
                                            .onFailure {
                                                actionMessage = "Erro ao remover: ${it.message}"
                                            }
                                    }
                                }
                            )
                        }
                    }

                    if (selectedTab == "all") {
                        item {
                            SectionTitle("REJECTED TEAMS (${rejectedFiltered.size})")
                        }

                        if (rejectedFiltered.isEmpty()) {
                            item {
                                EmptyRegistrationCard("Sem equipas rejeitadas.")
                            }
                        }

                        items(rejectedFiltered) { team ->
                            RejectedTeamCard(team = team)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ManageRegistrationTopBar(
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
                text = "Manage Registration",
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
private fun TournamentRegistrationHero(data: AdminManageRegistrationData) {
    val progressValue = if (data.maxTeams > 0) {
        data.registeredTeams.toFloat() / data.maxTeams.toFloat()
    } else {
        0f
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = data.tournamentName,
                        color = BrandWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "TEAMS REGISTERED",
                        color = Color(0xFFB9C4D8),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = data.registeredTeams.toString(),
                            color = BrandWhite,
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "/${data.maxTeams}",
                            color = Color(0xFFB9C4D8),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                SmallStatusBadge(data.status)
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progressValue },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(10.dp)),
                color = BrandGreen,
                trackColor = Color(0xFF31445F)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = data.registrationClosesText,
                color = Color(0xFFFFC857),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SmallStatusBadge(status: String) {
    val text = when {
        status.contains("aberto", ignoreCase = true) -> "OPEN"
        status.contains("decorrer", ignoreCase = true) -> "LIVE"
        status.contains("terminado", ignoreCase = true) -> "COMPLETED"
        status.contains("cancelado", ignoreCase = true) -> "CANCELLED"
        else -> status.uppercase()
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(BrandGreen)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = BrandWhite,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RegistrationSearchBox(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = "Search teams or captains...",
                color = TextGray,
                fontSize = 12.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = AppIcons.Search,
                contentDescription = "Pesquisar",
                tint = TextGray,
                modifier = Modifier.size(18.dp)
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(9.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = BrandWhite,
            unfocusedContainerColor = BrandWhite,
            focusedBorderColor = InputBg,
            unfocusedBorderColor = InputBg,
            focusedTextColor = BrandBlue,
            unfocusedTextColor = BrandBlue,
            cursorColor = BrandGreen
        )
    )
}

@Composable
private fun RegistrationTabChip(
    text: String,
    count: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (selected) PrimaryBlue else InputBg)
            .clickable {
                onClick()
            }
            .padding(horizontal = 11.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$text  $count",
            color = if (selected) BrandWhite else PrimaryBlue,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = TextGray,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp
    )
}

@Composable
private fun EmptyRegistrationCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Text(
            text = text,
            color = TextGray,
            fontSize = 12.sp,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
private fun PendingTeamCard(
    team: AdminRegistrationTeam,
    onRejectClick: () -> Unit,
    onApproveClick: () -> Unit,
    onDetailsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(165.dp)
                    .background(ErrorRed)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(13.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = team.teamName,
                            color = BrandBlue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Captain: ${team.captainName} · ${team.division}",
                            color = TextGray,
                            fontSize = 10.sp
                        )
                    }

                    MiniBadge(
                        text = "PENDING",
                        background = Color(0xFFFFF7D6),
                        textColor = Color(0xFFB45309)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SmallInfo(
                        label = "PLAYERS",
                        value = "${team.playersCount}/${team.maxPlayers}"
                    )

                    SmallInfo(
                        label = "WIN RATE",
                        value = team.winRate
                    )
                }

                Spacer(modifier = Modifier.height(7.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SmallInfo(
                        label = "APPLIED",
                        value = team.appliedAgo
                    )

                    PaymentText(status = team.paymentStatus)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onRejectClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightRedBadge,
                            contentColor = ErrorRed
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "×  REJECT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onApproveClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandGreen,
                            contentColor = BrandWhite
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "✓  APPROVE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onDetailsClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, PrimaryBlue),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandWhite,
                        contentColor = PrimaryBlue
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "DETAILS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ApprovedTeamCard(
    team: AdminRegistrationTeam,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(76.dp)
                    .background(BrandGreen)
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .padding(13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = team.teamName,
                        color = BrandBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MiniBadge(
                            text = "CONFIRMED",
                            background = Color(0xFFEAF8F5),
                            textColor = BrandGreen
                        )

                        MiniBadge(
                            text = paymentLabel(team.paymentStatus),
                            background = LightBlueBadge,
                            textColor = PrimaryBlue
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(LightRedBadge)
                        .clickable {
                            onRemoveClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.Delete,
                        contentDescription = "Remover equipa",
                        tint = ErrorRed,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RejectedTeamCard(team: AdminRegistrationTeam) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(70.dp)
                    .background(ErrorRed)
            )

            Column(
                modifier = Modifier.padding(13.dp)
            ) {
                Text(
                    text = team.teamName,
                    color = BrandBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                MiniBadge(
                    text = "REJECTED",
                    background = LightRedBadge,
                    textColor = ErrorRed
                )
            }
        }
    }
}

@Composable
private fun SmallInfo(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            color = TextGray,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = value,
            color = BrandBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PaymentText(status: String) {
    val paid = status.equals("pago", ignoreCase = true)

    Column {
        Text(
            text = "PAYMENT",
            color = TextGray,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = if (paid) "✓ Paid" else "× Unpaid",
            color = if (paid) BrandGreen else ErrorRed,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MiniBadge(
    text: String,
    background: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = textColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun paymentLabel(status: String): String {
    return when {
        status.equals("pago", ignoreCase = true) -> "PAID"
        status.equals("nao_pago", ignoreCase = true) -> "UNPAID"
        else -> "PENDING"
    }
}

@Composable
private fun ManageRegistrationBottomBar(
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
        BottomManageItem(AppIcons.Home, "HOME", selected == "home", onHomeClick)
        BottomManageItem(AppIcons.Tournaments, "TOURNAMENTS", selected == "tournaments", onTournamentsClick)
        BottomManageItem(AppIcons.Games, "MATCHES", selected == "matches", onMatchesClick)
        BottomManageItem(AppIcons.Teams, "TEAMS", selected == "teams", onTeamsClick)
        BottomManageItem(AppIcons.Profile, "PROFILE", selected == "profile", onProfileClick)
    }
}

@Composable
private fun BottomManageItem(
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
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminManageRegistrationScreenPreview() {
    AdminManageRegistrationScreen(
        tournamentId = "1"
    )
}