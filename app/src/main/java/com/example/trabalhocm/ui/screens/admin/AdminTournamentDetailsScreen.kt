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
import com.example.trabalhocm.data.model.AdminTournamentDetails
import com.example.trabalhocm.data.repository.AdminTournamentRepository
import com.example.trabalhocm.ui.theme.AppIcons
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.CardBg
import com.example.trabalhocm.ui.theme.LightBlueBadge
import com.example.trabalhocm.ui.theme.TextGray
import androidx.compose.foundation.layout.ColumnScope

@Composable
fun AdminTournamentDetailsScreen(
    tournamentId: String,
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onManageRegistrationClick: (String) -> Unit = {},
    onEditTournamentClick: (String) -> Unit = {},
    onDeleteTournamentClick: (String) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { AdminTournamentRepository() }

    var details by remember { mutableStateOf<AdminTournamentDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(tournamentId) {
        repository.obterDetalhesTorneio(tournamentId)
            .onSuccess {
                details = it
            }
            .onFailure {
                errorMessage = "Erro ao carregar detalhes: ${it.message}"
            }

        isLoading = false
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            AdminDetailsTopBar(
                title = "Details",
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick
            )
        },
        bottomBar = {
            AdminDetailsBottomBar(
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
                        color = Color(0xFFDC2626),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            details != null -> {
                AdminTournamentDetailsContent(
                    details = details!!,
                    innerPadding = innerPadding,
                    onManageRegistrationClick = onManageRegistrationClick,
                    onEditTournamentClick = onEditTournamentClick,
                    onDeleteTournamentClick = onDeleteTournamentClick
                )
            }
        }
    }
}

@Composable
private fun AdminTournamentDetailsContent(
    details: AdminTournamentDetails,
    innerPadding: PaddingValues,
    onManageRegistrationClick: (String) -> Unit,
    onEditTournamentClick: (String) -> Unit,
    onDeleteTournamentClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(
            start = 18.dp,
            end = 18.dp,
            top = 0.dp,
            bottom = 28.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            HeroDetailsCard(details = details)
        }

        item {
            DetailsWhiteCard(title = "About") {
                Text(
                    text = details.descricao,
                    color = TextGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }

        item {
            DetailsWhiteCard(title = "Schedule") {
                DetailInfoRow("Start Date", details.dataInicio)
                DetailInfoRow("End Date", details.dataFim)
                DetailInfoRow("Registration Closes", details.inscricoesFecham)
                DetailInfoRow("Format", details.formato)
                DetailInfoRow("Organizer", details.organizerName)
            }
        }

        item {
            DetailsWhiteCard(title = "Location") {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = AppIcons.Location,
                        contentDescription = "Localização",
                        tint = Color(0xFF0057C8),
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Text(
                            text = details.local,
                            color = BrandBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "Portugal",
                            color = TextGray,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        item {
            DetailsWhiteCard(title = "Standings") {
                Text(
                    text = "Ainda não existem dados de classificação associados a este torneio.",
                    color = TextGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }

        item {
            AdminControlsCard(
                tournamentId = details.id,
                onManageRegistrationClick = onManageRegistrationClick,
                onEditTournamentClick = onEditTournamentClick,
                onDeleteTournamentClick = onDeleteTournamentClick
            )
        }
    }
}

@Composable
private fun HeroDetailsCard(details: AdminTournamentDetails) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandBlue, RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
            .padding(horizontal = 18.dp, vertical = 20.dp)
    ) {
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusBadgeDetails(details.estado)

                SmallBadgeDetails(
                    text = details.modalidade,
                    background = Color(0xFFEAF8F5),
                    textColor = BrandGreen
                )

                SmallBadgeDetails(
                    text = "ADMIN VIEW",
                    background = BrandGreen,
                    textColor = Color.White
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = details.nome,
                color = Color.White,
                fontSize = 25.sp,
                lineHeight = 27.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                HeroStat("SEASON", details.season)
                HeroStat("PRIZE POOL", details.premio)
                HeroStat("TEAMS", details.teamsCount.toString())
            }
        }
    }
}

@Composable
private fun HeroStat(label: String, value: String) {
    Column {
        Text(
            text = label,
            color = Color(0xFFB9C4D8),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DetailsWhiteCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                color = BrandBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            content()
        }
    }
}

@Composable
private fun DetailInfoRow(
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
            color = TextGray,
            fontSize = 12.sp
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
private fun AdminControlsCard(
    tournamentId: String,
    onManageRegistrationClick: (String) -> Unit,
    onEditTournamentClick: (String) -> Unit,
    onDeleteTournamentClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Admin Controls",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Full oversight of this tournament",
                color = Color(0xFFB9C4D8),
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    onManageRegistrationClick(tournamentId)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreen,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "MANAGE REGISTRATION",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onEditTournamentClick(tournamentId)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreen,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "EDIT TOURNAMENT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onDeleteTournamentClick(tournamentId)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDC2626),
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = AppIcons.Delete,
                    contentDescription = "Apagar torneio",
                    tint = Color.White,
                    modifier = Modifier.size(17.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "DELETE TOURNAMENT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun StatusBadgeDetails(status: String) {
    val normalized = status.lowercase()

    val background = when {
        normalized.contains("aberto") || normalized.contains("open") -> Color(0xFFEAF8F5)
        normalized.contains("decorrer") || normalized.contains("live") -> Color(0xFFFEE2E2)
        normalized.contains("terminado") || normalized.contains("archived") -> Color(0xFFEAF2F5)
        normalized.contains("cancelado") -> Color(0xFFFEE2E2)
        else -> Color(0xFFEAF8F5)
    }

    val textColor = when {
        normalized.contains("aberto") || normalized.contains("open") -> BrandGreen
        normalized.contains("decorrer") || normalized.contains("live") -> Color(0xFFDC2626)
        normalized.contains("terminado") || normalized.contains("archived") -> TextGray
        normalized.contains("cancelado") -> Color(0xFFDC2626)
        else -> BrandGreen
    }

    val text = when {
        normalized.contains("aberto") ||
                normalized.contains("open") -> "OPEN"

        normalized.contains("decorrer") ||
                normalized.contains("live") ||
                normalized.contains("progress") -> "IN PROGRESS"

        normalized.contains("terminado") ||
                normalized.contains("completed") ||
                normalized.contains("archived") -> "COMPLETED"

        normalized.contains("cancelado") ||
                normalized.contains("cancel") -> "CANCELLED"

        normalized.contains("rascunho") ||
                normalized.contains("draft") -> "DRAFT"

        else -> status.uppercase()
    }

    SmallBadgeDetails(
        text = text,
        background = background,
        textColor = textColor
    )
}

@Composable
private fun SmallBadgeDetails(
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
private fun AdminDetailsTopBar(
    title: String,
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
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Icon(
            imageVector = AppIcons.Notifications,
            contentDescription = "Notificações",
            tint = Color.White,
            modifier = Modifier
                .size(23.dp)
                .clickable {
                    onNotificationsClick()
                }
        )
    }
}

@Composable
private fun AdminDetailsBottomBar(
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
            .background(Color.White)
            .navigationBarsPadding()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomItemDetails(AppIcons.Home, "HOME", selected == "home", onHomeClick)
        BottomItemDetails(AppIcons.Tournaments, "TOURNAMENTS", selected == "tournaments", onTournamentsClick)
        BottomItemDetails(AppIcons.Games, "MATCHES", selected == "matches", onMatchesClick)
        BottomItemDetails(AppIcons.Teams, "TEAMS", selected == "teams", onTeamsClick)
        BottomItemDetails(AppIcons.Profile, "PROFILE", selected == "profile", onProfileClick)
    }
}

@Composable
private fun BottomItemDetails(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) Color(0xFF0057C8) else Color(0xFF9AA5B5)

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
fun AdminTournamentDetailsScreenPreview() {
    AdminTournamentDetailsScreen(
        tournamentId = "1"
    )
}