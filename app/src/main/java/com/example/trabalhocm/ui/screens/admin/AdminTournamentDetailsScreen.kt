package com.example.trabalhocm.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.trabalhocm.data.model.AdminTournamentDetails
import com.example.trabalhocm.data.repository.AdminTournamentRepository
import com.example.trabalhocm.ui.theme.AppIcons
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.CardBg
import com.example.trabalhocm.ui.theme.ErrorRed
import com.example.trabalhocm.ui.theme.TextGray
import kotlinx.coroutines.launch
import androidx.compose.ui.res.stringResource
import com.example.trabalhocm.R

@Composable
fun AdminTournamentDetailsScreen(
    tournamentId: String,
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onManageRegistrationClick: (String) -> Unit = {},
    onEditTournamentClick: (String) -> Unit = {},
    onDeleteTournamentSuccess: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { AdminTournamentRepository() }
    val scope = rememberCoroutineScope()

    var details by remember { mutableStateOf<AdminTournamentDetails?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }
    var deleteMessage by remember { mutableStateOf("") }

    val loadDetailsErrorText = stringResource(R.string.admin_tournament_details_error_loading)
    val deleteTournamentErrorText = stringResource(R.string.admin_tournament_details_delete_error)

    LaunchedEffect(tournamentId) {
        isLoading = true
        errorMessage = ""

        repository.obterDetalhesTorneio(tournamentId)
            .onSuccess {
                details = it
            }
            .onFailure {
                errorMessage = "$loadDetailsErrorText: ${it.message}"
            }

        isLoading = false
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                if (!isDeleting) {
                    showDeleteDialog = false
                }
            },
            title = {
                Text(
                    text = stringResource(R.string.admin_tournament_details_delete_title),
                    color = BrandBlue,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.admin_tournament_details_delete_message),
                    color = TextGray,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !isDeleting,
                    onClick = {
                        scope.launch {
                            isDeleting = true
                            deleteMessage = ""

                            repository.apagarTorneio(tournamentId)
                                .onSuccess {
                                    showDeleteDialog = false
                                    onDeleteTournamentSuccess()
                                }
                                .onFailure {
                                    deleteMessage = "$deleteTournamentErrorText: ${it.message}"
                                    showDeleteDialog = false
                                }

                            isDeleting = false
                        }
                    }
                ) {
                    Text(
                        text = if (isDeleting) {
                            stringResource(R.string.admin_tournament_details_deleting)
                        } else {
                            stringResource(R.string.admin_tournament_details_delete_button)
                        },
                        color = ErrorRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    enabled = !isDeleting,
                    onClick = {
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        text = stringResource(R.string.admin_common_cancel),
                        color = BrandBlue
                    )
                }
            }
        )
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            AdminDetailsTopBar(
                title = stringResource(R.string.admin_tournament_details_title),
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
                        color = ErrorRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            details != null -> {
                AdminTournamentDetailsContent(
                    details = details!!,
                    innerPadding = innerPadding,
                    deleteMessage = deleteMessage,
                    onManageRegistrationClick = onManageRegistrationClick,
                    onEditTournamentClick = onEditTournamentClick,
                    onDeleteTournamentClick = {
                        showDeleteDialog = true
                    }
                )
            }
        }
    }
}

@Composable
private fun AdminTournamentDetailsContent(
    details: AdminTournamentDetails,
    innerPadding: PaddingValues,
    deleteMessage: String,
    onManageRegistrationClick: (String) -> Unit,
    onEditTournamentClick: (String) -> Unit,
    onDeleteTournamentClick: () -> Unit
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
            DetailsWhiteCard(title = stringResource(R.string.admin_tournament_details_about)) {
                Text(
                    text = details.descricao,
                    color = TextGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }

        item {
            DetailsWhiteCard(title = stringResource(R.string.admin_tournament_details_schedule)) {
                DetailInfoRow(stringResource(R.string.admin_tournament_details_start_date), details.dataInicio)
                DetailInfoRow(stringResource(R.string.admin_tournament_details_end_date), details.dataFim)
                DetailInfoRow(stringResource(R.string.admin_tournament_details_registration_closes), details.inscricoesFecham)
                DetailInfoRow(stringResource(R.string.admin_tournament_details_format), details.formato)
                DetailInfoRow(stringResource(R.string.admin_tournament_details_organizer), details.organizerName)
            }
        }

        item {
            DetailsWhiteCard(title = stringResource(R.string.admin_tournament_details_location)) {
                Row(
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = AppIcons.Location,
                        contentDescription = stringResource(R.string.admin_tournament_details_location),
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
            DetailsWhiteCard(title = stringResource(R.string.admin_tournament_details_standings)) {
                Text(
                    text = stringResource(R.string.admin_tournament_details_no_standings),
                    color = TextGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }

        if (deleteMessage.isNotBlank()) {
            item {
                Text(
                    text = deleteMessage,
                    color = ErrorRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
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
                    text = stringResource(R.string.admin_tournament_details_admin_view).uppercase(),
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
                HeroStat(stringResource(R.string.admin_tournament_details_season).uppercase(), details.season)
                HeroStat(stringResource(R.string.admin_tournament_details_prize_pool).uppercase(), details.premio)
                HeroStat(stringResource(R.string.admin_tournament_details_teams).uppercase(), details.teamsCount.toString())
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
    onDeleteTournamentClick: () -> Unit
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
                text = stringResource(R.string.admin_tournament_details_admin_controls),
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.admin_tournament_details_admin_controls_desc),
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
                    text = stringResource(R.string.admin_tournament_details_manage_registration).uppercase(),
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
                    text = stringResource(R.string.admin_tournament_details_edit_tournament).uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    onDeleteTournamentClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorRed,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = AppIcons.Delete,
                    contentDescription = stringResource(R.string.admin_tournament_details_delete_title),
                    tint = Color.White,
                    modifier = Modifier.size(17.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.admin_tournament_details_delete_title).uppercase(),
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
        normalized.contains("decorrer") || normalized.contains("live") -> ErrorRed
        normalized.contains("terminado") || normalized.contains("archived") -> TextGray
        normalized.contains("cancelado") -> ErrorRed
        else -> BrandGreen
    }

    val text = when {
        normalized.contains("aberto") ||
                normalized.contains("open") -> stringResource(R.string.admin_status_open).uppercase()

        normalized.contains("decorrer") ||
                normalized.contains("live") ||
                normalized.contains("progress") -> stringResource(R.string.admin_tournament_details_status_in_progress).uppercase()

        normalized.contains("terminado") ||
                normalized.contains("completed") ||
                normalized.contains("archived") -> stringResource(R.string.admin_status_completed).uppercase()

        normalized.contains("cancelado") ||
                normalized.contains("cancel") -> stringResource(R.string.admin_status_cancelled).uppercase()

        normalized.contains("rascunho") ||
                normalized.contains("draft") -> stringResource(R.string.admin_status_draft).uppercase()

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
                contentDescription = stringResource(R.string.admin_common_back),
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
            contentDescription = stringResource(R.string.admin_common_notifications),
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
        BottomItemDetails(AppIcons.Home, stringResource(R.string.admin_nav_home).uppercase(), selected == "home", onHomeClick)
        BottomItemDetails(AppIcons.Tournaments, stringResource(R.string.admin_nav_tournaments).uppercase(), selected == "tournaments", onTournamentsClick)
        BottomItemDetails(AppIcons.Games, stringResource(R.string.admin_nav_matches).uppercase(), selected == "matches", onMatchesClick)
        BottomItemDetails(AppIcons.Teams, stringResource(R.string.admin_nav_teams).uppercase(), selected == "teams", onTeamsClick)
        BottomItemDetails(AppIcons.Profile, stringResource(R.string.admin_nav_profile).uppercase(), selected == "profile", onProfileClick)
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