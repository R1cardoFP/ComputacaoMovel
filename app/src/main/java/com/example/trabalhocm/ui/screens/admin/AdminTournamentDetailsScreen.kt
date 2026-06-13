package com.example.trabalhocm.ui.screens.admin

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.model.AdminTournamentDetails
import com.example.trabalhocm.data.model.AdminTournamentStanding
import com.example.trabalhocm.data.repository.AdminTournamentRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.AppIcons
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.CardBg
import com.example.trabalhocm.ui.theme.DarkBlue
import com.example.trabalhocm.ui.theme.ErrorRed
import com.example.trabalhocm.ui.theme.InputBg
import com.example.trabalhocm.ui.theme.PrimaryBlue
import com.example.trabalhocm.ui.theme.TealGreen
import com.example.trabalhocm.ui.theme.TextGray
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
            shape = RoundedCornerShape(24.dp),
            containerColor = CardBg,
            title = {
                Text(
                    text = stringResource(R.string.admin_tournament_details_delete_title),
                    color = DarkBlue,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.admin_tournament_details_delete_message),
                    color = TextGray,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
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
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = AppIcons.Back,
                            contentDescription = stringResource(R.string.admin_common_back),
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.admin_tournament_details_title),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = AppIcons.Notifications,
                            contentDescription = stringResource(R.string.admin_common_notifications),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        bottomBar = {
            MatchLeagueBottomBar(
                selectedTab = "TOURNAMENTS",
                onHomeClick = onHomeClick,
                onTournamentsClick = onTournamentsClick,
                onMatchesClick = onMatchesClick,
                onTeamsClick = onTeamsClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = BgLight
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TealGreen)
                }
            }

            errorMessage.isNotBlank() -> {
                AdminTournamentDetailsErrorState(
                    innerPadding = innerPadding,
                    message = errorMessage
                )
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
            start = 24.dp,
            end = 24.dp,
            top = 20.dp,
            bottom = 28.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            TournamentHeroCard(details = details)
        }

        if (deleteMessage.isNotBlank()) {
            item {
                TournamentMessageCard(
                    message = deleteMessage,
                    isError = true
                )
            }
        }

        item {
            TournamentDescriptionCard(details = details)
        }

        item {
            TournamentScheduleCard(details = details)
        }

        item {
            TournamentLocationCard(location = details.local)
        }

        item {
            TournamentStandingsCard(standings = details.classificacao)
        }

        item {
            TournamentAdminActionsCard(
                tournamentId = details.id,
                onManageRegistrationClick = onManageRegistrationClick,
                onEditTournamentClick = onEditTournamentClick,
                onDeleteTournamentClick = onDeleteTournamentClick
            )
        }
    }
}

@Composable
private fun TournamentHeroCard(details: AdminTournamentDetails) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(62.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.Tournaments,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.admin_tournament_details_admin_view).uppercase(),
                        color = TealGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = details.nome,
                        color = Color.White,
                        fontSize = 24.sp,
                        lineHeight = 29.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TournamentStatusBadge(status = details.estado)
                SmallTournamentBadge(
                    text = details.modalidade,
                    background = Color.White.copy(alpha = 0.12f),
                    textColor = Color.White
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HeroMetricCard(
                    label = stringResource(R.string.admin_tournament_details_season),
                    value = details.season,
                    modifier = Modifier.weight(1f)
                )

                HeroMetricCard(
                    label = stringResource(R.string.admin_tournament_details_prize_pool),
                    value = details.premio,
                    modifier = Modifier.weight(1f)
                )

                HeroMetricCard(
                    label = stringResource(R.string.admin_tournament_details_teams),
                    value = details.teamsCount.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HeroMetricCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .padding(horizontal = 10.dp, vertical = 12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = label.uppercase(),
                color = Color.White.copy(alpha = 0.70f),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TournamentDescriptionCard(details: AdminTournamentDetails) {
    DetailsSectionCard(
        title = stringResource(R.string.admin_tournament_details_about),
        icon = AppIcons.Info,
        iconColor = PrimaryBlue,
        iconBackground = Color(0xFFEAF2FF)
    ) {
        Text(
            text = details.descricao,
            color = TextGray,
            fontSize = 13.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
private fun TournamentScheduleCard(details: AdminTournamentDetails) {
    DetailsSectionCard(
        title = stringResource(R.string.admin_tournament_details_schedule),
        icon = AppIcons.Calendar,
        iconColor = TealGreen,
        iconBackground = Color(0xFFEAF8F5)
    ) {
        DetailInfoRow(stringResource(R.string.admin_tournament_details_start_date), details.dataInicio)
        DetailInfoRow(stringResource(R.string.admin_tournament_details_end_date), details.dataFim)
        DetailInfoRow(stringResource(R.string.admin_tournament_details_registration_closes), details.inscricoesFecham)
        DetailInfoRow(stringResource(R.string.admin_tournament_details_format), details.formato)
        DetailInfoRow(stringResource(R.string.admin_tournament_details_organizer), details.organizerName)
    }
}

@Composable
private fun TournamentLocationCard(location: String) {
    DetailsSectionCard(
        title = stringResource(R.string.admin_tournament_details_location),
        icon = AppIcons.Location,
        iconColor = PrimaryBlue,
        iconBackground = Color(0xFFEAF2FF)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = location,
                    color = DarkBlue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Portugal",
                    color = TextGray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun TournamentStandingsCard(standings: List<AdminTournamentStanding>) {
    DetailsSectionCard(
        title = stringResource(R.string.admin_tournament_details_standings),
        icon = AppIcons.Teams,
        iconColor = TealGreen,
        iconBackground = Color(0xFFEAF8F5)
    ) {
        if (standings.isEmpty()) {
            EmptyStandingsCard()
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                standings.forEach { standing ->
                    StandingTeamRow(standing = standing)
                }
            }
        }
    }
}

@Composable
private fun StandingTeamRow(standing: AdminTournamentStanding) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(InputBg)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (standing.posicao == 1) TealGreen else Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = standing.posicao.takeIf { it > 0 }?.toString() ?: "-",
                color = if (standing.posicao == 1) Color.White else PrimaryBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = standing.equipa,
                color = DarkBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(5.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StandingMiniStat(stringResource(R.string.admin_tournament_details_played_short), standing.jogos)
                StandingMiniStat(stringResource(R.string.admin_tournament_details_wins_short), standing.vitorias)
                StandingMiniStat(stringResource(R.string.admin_tournament_details_draws_short), standing.empates)
                StandingMiniStat(stringResource(R.string.admin_tournament_details_losses_short), standing.derrotas)
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = standing.pontos.toString(),
                color = TealGreen,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.admin_tournament_details_points_short).uppercase(),
                color = TextGray,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun StandingMiniStat(
    label: String,
    value: Int
) {
    Text(
        text = "${label.uppercase()}: $value",
        color = TextGray,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun EmptyStandingsCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(InputBg)
            .padding(18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.admin_tournament_details_no_standings),
            color = TextGray,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TournamentAdminActionsCard(
    tournamentId: String,
    onManageRegistrationClick: (String) -> Unit,
    onEditTournamentClick: (String) -> Unit,
    onDeleteTournamentClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFEAF8F5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.Settings,
                        contentDescription = null,
                        tint = TealGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.admin_tournament_details_admin_controls),
                        color = DarkBlue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = stringResource(R.string.admin_tournament_details_admin_controls_desc),
                        color = TextGray,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    onManageRegistrationClick(tournamentId)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealGreen,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    imageVector = AppIcons.Teams,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.admin_tournament_details_manage_registration),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    onEditTournamentClick(tournamentId)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue)
            ) {
                Icon(
                    imageVector = AppIcons.Edit,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.admin_tournament_details_edit_tournament),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    onDeleteTournamentClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ErrorRed,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    imageVector = AppIcons.Delete,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = stringResource(R.string.admin_tournament_details_delete_title),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DetailsSectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    iconBackground: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(iconBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(21.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    color = DarkBlue,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}

@Composable
private fun DetailInfoRow(
    label: String,
    value: String
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = TextGray,
                fontSize = 13.sp,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = value,
                color = DarkBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        HorizontalDivider(color = InputBg)
    }
}

@Composable
private fun TournamentStatusBadge(status: String) {
    val normalized = status.lowercase()

    val background = when {
        normalized.contains("aberto") || normalized.contains("open") -> Color(0xFFEAF8F5)
        normalized.contains("decorrer") || normalized.contains("live") || normalized.contains("progress") -> Color(0xFFFFEBEE)
        normalized.contains("terminado") || normalized.contains("completed") || normalized.contains("archived") -> Color(0xFFEAF2FF)
        normalized.contains("cancelado") || normalized.contains("cancel") -> Color(0xFFFFEBEE)
        else -> Color(0xFFEAF8F5)
    }

    val textColor = when {
        normalized.contains("aberto") || normalized.contains("open") -> TealGreen
        normalized.contains("decorrer") || normalized.contains("live") || normalized.contains("progress") -> ErrorRed
        normalized.contains("terminado") || normalized.contains("completed") || normalized.contains("archived") -> PrimaryBlue
        normalized.contains("cancelado") || normalized.contains("cancel") -> ErrorRed
        else -> TealGreen
    }

    val text = when {
        normalized.contains("aberto") || normalized.contains("open") -> stringResource(R.string.admin_status_open).uppercase()
        normalized.contains("decorrer") || normalized.contains("live") || normalized.contains("progress") -> stringResource(R.string.admin_tournament_details_status_in_progress).uppercase()
        normalized.contains("terminado") || normalized.contains("completed") || normalized.contains("archived") -> stringResource(R.string.admin_status_completed).uppercase()
        normalized.contains("cancelado") || normalized.contains("cancel") -> stringResource(R.string.admin_status_cancelled).uppercase()
        normalized.contains("rascunho") || normalized.contains("draft") -> stringResource(R.string.admin_status_draft).uppercase()
        else -> status.uppercase()
    }

    SmallTournamentBadge(
        text = text,
        background = background,
        textColor = textColor
    )
}

@Composable
private fun SmallTournamentBadge(
    text: String,
    background: Color,
    textColor: Color
) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = background
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TournamentMessageCard(
    message: String,
    isError: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) Color(0xFFFFEBEE) else Color(0xFFEAF8F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = message,
            color = if (isError) ErrorRed else TealGreen,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(14.dp)
        )
    }
}

@Composable
private fun AdminTournamentDetailsErrorState(
    innerPadding: PaddingValues,
    message: String
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = AppIcons.Info,
                    contentDescription = null,
                    tint = ErrorRed,
                    modifier = Modifier.size(34.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = stringResource(R.string.admin_tournament_details_error_loading),
                    color = ErrorRed,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = message,
                    color = ErrorRed,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminTournamentDetailsScreenPreview() {
    AdminTournamentDetailsScreen(
        tournamentId = "1"
    )
}
