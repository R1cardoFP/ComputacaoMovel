package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trabalhocm.R
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.*

private val WarningYellow = Color(0xFFF59E0B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerTournamentsScreen(
    viewModel: OrganizerTournamentsViewModel = viewModel(),
    onHistoryClick: () -> Unit = {},
    onFiltersClick: () -> Unit = {},
    onCreateNewClick: () -> Unit = {},
    onDetailsClick: (Long) -> Unit = { _ -> },
    onInviteTeamsClick: (Long) -> Unit = { _ -> },
    onManageRegistrationClick: (Long) -> Unit = { _ -> },
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onEditClick: (Long) -> Unit = { _ -> }
) {
    var searchQuery by remember { mutableStateOf("") }

    // Recarrega sempre que o ecrã volta a entrar em composição (ex.: regresso do editar)
    LaunchedEffect(Unit) {
        viewModel.carregarTorneios()
    }

    val torneios = viewModel.torneios
    val isLoading = viewModel.isLoading

    val sportFootball = stringResource(R.string.sport_football)
    val sportBasketball = stringResource(R.string.sport_basketball)
    val sportVolleyball = stringResource(R.string.sport_volleyball)
    val sportDefault = stringResource(R.string.sport_default)

    val tbdText = stringResource(R.string.val_tbd)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_list), color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.carregarTorneios() }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = stringResource(R.string.desc_refresh), tint = Color.White)
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
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(stringResource(R.string.title_tournament_management), color = DarkBlue, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 32.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(R.string.desc_tournament_management), color = TextGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(20.dp))
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onHistoryClick,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, InputBg),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = CardBg),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text(stringResource(R.string.btn_history), color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onCreateNewClick,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        modifier = Modifier.weight(1f).height(48.dp)
                    ) {
                        Text(stringResource(R.string.btn_create_new), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(stringResource(R.string.placeholder_search_tournaments), color = TextGray, fontSize = 14.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CardBg, unfocusedContainerColor = CardBg,
                        focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TealGreen)
                    }
                }
            } else {
                val torneiosFiltrados = torneios.filter { it.nome.contains(searchQuery, ignoreCase = true) }

                if (torneiosFiltrados.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text(stringResource(R.string.msg_no_tournaments_found), color = TextGray)
                        }
                    }
                } else {
                    items(torneiosFiltrados) { torneio ->

                        val isOrganizer = torneio.idOrganizador == viewModel.currentUserId
                        val sportName = when (torneio.idModalidade) {
                            1L -> sportFootball
                            2L -> sportBasketball
                            3L -> sportVolleyball
                            else -> sportDefault
                        }
                        val statusColor = if (torneio.estado == "aberto") TealGreen else WarningYellow

                        if (isOrganizer) {
                            OrganizerTournamentCard(
                                status = "• ${torneio.estado.uppercase()}",
                                statusColor = statusColor,
                                tags = listOf(torneio.formato.uppercase(), sportName),
                                title = torneio.nome,
                                dates = "${torneio.dataInicio} — ${torneio.dataFim ?: tbdText}",
                                teams = 0,
                                gamesToday = 0,
                                onDetailsClick = { onDetailsClick(torneio.id) },
                                onInviteTeamsClick = { onInviteTeamsClick(torneio.id) },
                                onManageRegistrationClick = { onManageRegistrationClick(torneio.id) },
                                onEditClick = { onEditClick(torneio.id) }
                            )
                        } else {
                            RegularTournamentCard(
                                status = "• ${torneio.estado.uppercase()}",
                                statusColor = statusColor,
                                tags = listOf(torneio.formato.uppercase(), sportName),
                                title = torneio.nome,
                                dates = stringResource(R.string.format_start_date, torneio.dataInicio),
                                registered = 0,
                                capacity = 32,
                                actionText = if (torneio.estado == "aberto") stringResource(R.string.btn_register_now) else stringResource(R.string.btn_view_details),
                                actionColor = if (torneio.estado == "aberto") TealGreen else InputBg,
                                isActionEnabled = torneio.estado == "aberto",
                                onDetailsClick = { onDetailsClick(torneio.id) }
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun OrganizerTournamentCard(
    status: String, statusColor: Color, tags: List<String>, title: String, dates: String,
    teams: Int, gamesToday: Int, onDetailsClick: () -> Unit,
    onInviteTeamsClick: () -> Unit = {},
    onManageRegistrationClick: () -> Unit = {},
    onEditClick: () -> Unit = {}
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TourneyBadge(status, statusColor, statusColor.copy(alpha = 0.1f))
                    tags.forEach { TourneyBadge(it, PrimaryBlue, PrimaryBlue.copy(alpha = 0.1f)) }
                }
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = stringResource(R.string.desc_edit),
                    tint = TealGreen,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onEditClick() }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(stringResource(R.string.label_you_organize), color = TealGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, color = DarkBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("📅 $dates", color = TextGray, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                Column {
                    Text(stringResource(R.string.label_teams_caps), color = DarkBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("$teams", color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
                Column {
                    Text(stringResource(R.string.label_games_today), color = DarkBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("$gamesToday", color = PrimaryBlue, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDetailsClick, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, PrimaryBlue), modifier = Modifier.weight(1f).height(40.dp)) {
                    Text(stringResource(R.string.btn_details), color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
                OutlinedButton(onClick = onInviteTeamsClick, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, PrimaryBlue), modifier = Modifier.weight(1f).height(40.dp)) {
                    Text(stringResource(R.string.btn_invite_teams_caps), color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onManageRegistrationClick, shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue), modifier = Modifier.fillMaxWidth().height(40.dp)) {
                Text(stringResource(R.string.btn_manage_registration), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            }
        }
    }
}

@Composable
private fun RegularTournamentCard(
    status: String, statusColor: Color, tags: List<String>, title: String, dates: String,
    registered: Int?, capacity: Int?, actionText: String, actionColor: Color, isActionEnabled: Boolean = true,
    onDetailsClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TourneyBadge(status, statusColor, statusColor.copy(alpha = 0.1f))
                tags.forEach { TourneyBadge(it, TextGray, InputBg) }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, color = DarkBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("📅 $dates", color = TextGray, fontSize = 12.sp)

            if (registered != null && capacity != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringResource(R.string.label_registered), color = DarkBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text("$registered/$capacity", color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { registered.toFloat() / capacity.toFloat() },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = statusColor, trackColor = InputBg,
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onDetailsClick, shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, PrimaryBlue), modifier = Modifier.weight(1f).height(40.dp)) {
                    Text(stringResource(R.string.btn_details), color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
                Button(
                    onClick = { }, enabled = isActionEnabled, shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = actionColor, disabledContainerColor = actionColor),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text(actionText, color = if (isActionEnabled) Color.White else TextGray, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
fun TourneyBadge(text: String, textColor: Color, bgColor: Color) {
    Surface(color = bgColor, shape = RoundedCornerShape(12.dp)) {
        Text(text, color = textColor, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun OrganizerTournamentsScreenPreview() {
    MaterialTheme {
        OrganizerTournamentsScreen()
    }
}