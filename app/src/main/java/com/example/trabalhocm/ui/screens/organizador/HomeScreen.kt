package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trabalhocm.R
import com.example.trabalhocm.data.repository.OrganizerHomeFixture
import com.example.trabalhocm.data.repository.OrganizerHomeLiveMatch
import com.example.trabalhocm.data.repository.OrganizerHomePlayerStats
import com.example.trabalhocm.data.repository.OrganizerHomeTournament
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import kotlin.math.roundToInt

private val DarkBlue = Color(0xFF152238)
private val EmeraldGreen = Color(0xFF0E8A6F)
private val BgLight = Color(0xFFF7F7F9)
private val TextGray = Color(0xFF6B7280)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: OrganizerHomeViewModel = viewModel(),
    onVerTorneios: () -> Unit = {},
    onCreateTournamentClick: () -> Unit = {},
    onCreateCasualMatchClick: () -> Unit = {},
    onLiveMatchesClick: () -> Unit = {},
    onCreateTeamClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.title_home),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(
                        onClick = onNotificationsClick
                    ) {
                        Icon(
                            Icons.Outlined.Notifications,
                            contentDescription = stringResource(R.string.desc_notifications),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue
                )
            )
        },
        bottomBar = {
            MatchLeagueBottomBar(
                selectedTab = "HOME",
                onHomeClick = onHomeClick,
                onTournamentsClick = onTournamentsClick,
                onMatchesClick = onMatchesClick,
                onTeamsClick = onTeamsClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = BgLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (viewModel.errorMessage.isNotBlank()) {
                DashboardErrorCard(
                    message = viewModel.errorMessage
                )
            }

            LiveMatchSection(
                liveMatch = viewModel.liveMatch,
                isLoading = viewModel.isLoading
            )

            QuickActionsSection(
                onCreateTournamentClick = onCreateTournamentClick,
                onCreateCasualMatchClick = onCreateCasualMatchClick,
                onLiveMatchesClick = onLiveMatchesClick,
                onCreateTeamClick = onCreateTeamClick
            )

            ActiveTournamentsSection(
                tournaments = viewModel.activeTournaments,
                isLoading = viewModel.isLoading,
                onViewAllClick = onTournamentsClick
            )

            UpcomingFixturesSection(
                fixtures = viewModel.upcomingFixtures,
                isLoading = viewModel.isLoading
            )

            PerformanceInsightsSection(
                playerOfWeek = viewModel.playerOfWeek,
                isLoading = viewModel.isLoading
            )
        }
    }
}

@Composable
fun DashboardErrorCard(
    message: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "${stringResource(R.string.label_error)}: $message",
            color = Color(0xFFD01818),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Composable
fun ActiveTournamentsSection(
    tournaments: List<OrganizerHomeTournament>,
    isLoading: Boolean,
    onViewAllClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SectionTitle(stringResource(R.string.section_active_tournaments))

            Text(
                text = stringResource(R.string.btn_view_all),
                color = Color(0xFF2B5BFE),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    onViewAllClick()
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = EmeraldGreen
                    )
                }
            }

            tournaments.isEmpty() -> {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.msg_no_active_tournaments),
                        color = TextGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            else -> {
                tournaments.forEach { torneio ->
                    val progress = (torneio.progresso * 100).roundToInt()
                    val color = when (torneio.estado.lowercase()) {
                        "aberto" -> EmeraldGreen
                        "em_decorrer", "ativo" -> Color(0xFF2B5BFE)
                        else -> EmeraldGreen
                    }

                    TournamentCard(
                        title = torneio.nome,
                        role = stringResource(R.string.role_organizer_caps),
                        progress = progress,
                        color = color
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun QuickActionsSection(
    onCreateTournamentClick: () -> Unit,
    onCreateCasualMatchClick: () -> Unit,
    onLiveMatchesClick: () -> Unit,
    onCreateTeamClick: () -> Unit
) {
    Column {
        SectionTitle(stringResource(R.string.section_quick_actions))

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Star,
                title = stringResource(R.string.action_create_tournament),
                tint = EmeraldGreen,
                onClick = onCreateTournamentClick
            )

            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Add,
                title = stringResource(R.string.action_create_casual_match),
                tint = EmeraldGreen,
                onClick = onCreateCasualMatchClick
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Share,
                title = stringResource(R.string.action_live_matches),
                tint = Color(0xFF6366F1),
                onClick = onLiveMatchesClick
            )

            QuickActionCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Person,
                title = stringResource(R.string.action_create_team),
                tint = EmeraldGreen,
                onClick = onCreateTeamClick
            )
        }
    }
}

@Composable
fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    tint: Color,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable {
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                color = tint.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LiveMatchSection(
    liveMatch: OrganizerHomeLiveMatch?,
    isLoading: Boolean
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = DarkBlue
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = EmeraldGreen
                    )
                }
            }

            liveMatch == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(
                            1.dp,
                            EmeraldGreen.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.badge_no_live_match),
                            color = EmeraldGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.msg_no_live_matches_desc),
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.msg_live_matches_appear_here),
                        color = Color.Gray,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                1.dp,
                                EmeraldGreen.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldGreen)
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                Text(
                                    text = stringResource(R.string.badge_live_now),
                                    color = EmeraldGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Text(
                            text = liveMatch.torneioNome.uppercase(),
                            color = Color.Gray,
                            fontSize = 10.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TeamIconCircle(
                            text = getTeamInitials(liveMatch.equipaCasa),
                            color = EmeraldGreen
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "${liveMatch.pontosCasa} - ${liveMatch.pontosFora}",
                                color = Color.White,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "${liveMatch.minuto}'",
                                color = EmeraldGreen,
                                fontSize = 14.sp
                            )
                        }

                        TeamIconCircle(
                            text = getTeamInitials(liveMatch.equipaFora),
                            color = Color.Yellow
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(
                            text = liveMatch.equipaCasa,
                            color = Color.White,
                            fontSize = 13.sp
                        )

                        Text(
                            text = liveMatch.equipaFora,
                            color = Color.White,
                            fontSize = 13.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {},
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EmeraldGreen
                        ),
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        Text(stringResource(R.string.btn_watch_stream))
                    }
                }
            }
        }
    }
}

@Composable
fun TeamIconCircle(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.18f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TournamentCard(
    title: String,
    role: String,
    progress: Int,
    color: Color
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(color)
            )

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = title,
                            fontSize = 18.sp,
                            color = DarkBlue
                        )

                        Text(
                            text = role,
                            fontSize = 10.sp,
                            color = color,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = color.copy(alpha = 0.3f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(R.string.label_progress),
                        fontSize = 10.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "$progress%",
                        fontSize = 10.sp,
                        color = TextGray,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = {
                        progress / 100f
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp)),
                    color = color,
                    trackColor = color.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
fun UpcomingFixturesSection(
    fixtures: List<OrganizerHomeFixture>,
    isLoading: Boolean
) {
    Column {
        SectionTitle(stringResource(R.string.section_upcoming_fixtures))

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = EmeraldGreen
                        )
                    }
                }

                fixtures.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.msg_no_upcoming_fixtures),
                        color = TextGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                else -> {
                    Column {
                        fixtures.forEachIndexed { index, fixture ->
                            FixtureItem(
                                fixture = fixture
                            )

                            if (index < fixtures.lastIndex) {
                                HorizontalDivider(
                                    color = BgLight,
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FixtureItem(
    fixture: OrganizerHomeFixture
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = formatDateForHome(fixture.data),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Text(
                text = formatTimeForHome(fixture.hora),
                color = TextGray,
                fontSize = 12.sp
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TeamFixtureIcon(
                text = getTeamInitials(fixture.equipaCasa),
                color = EmeraldGreen
            )

            Text(
                text = "VS",
                color = Color(0xFFC3C6CF),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )

            TeamFixtureIcon(
                text = getTeamInitials(fixture.equipaFora),
                color = Color(0xFF2B5BFE)
            )
        }

        Surface(
            color = BgLight,
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                Icons.Outlined.Notifications,
                contentDescription = null,
                tint = DarkBlue,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
fun TeamFixtureIcon(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PerformanceInsightsSection(
    playerOfWeek: OrganizerHomePlayerStats?,
    isLoading: Boolean
) {
    Column {
        SectionTitle(stringResource(R.string.section_performance_insights))

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = DarkBlue
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(270.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = EmeraldGreen
                        )
                    }
                }

                playerOfWeek == null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.badge_player_of_week),
                            color = EmeraldGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(72.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(R.string.msg_no_player_stats),
                            color = Color.White,
                            fontSize = 18.sp
                        )

                        Text(
                            text = stringResource(R.string.msg_stats_appear_later),
                            color = EmeraldGreen,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.badge_player_of_week),
                            color = EmeraldGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(
                                3.dp,
                                EmeraldGreen.copy(alpha = 0.3f)
                            ),
                            color = Color.Gray,
                            modifier = Modifier.size(100.dp)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = getPlayerInitials(playerOfWeek.nome),
                                    color = Color.White,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = playerOfWeek.nome.uppercase(),
                            color = Color.White,
                            fontSize = 20.sp
                        )

                        Text(
                            text = "@${playerOfWeek.username}",
                            color = EmeraldGreen,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        HorizontalDivider(
                            color = Color.White.copy(alpha = 0.1f)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatColumn(
                                value = playerOfWeek.vitorias.toString().padStart(2, '0'),
                                label = stringResource(R.string.stat_wins_caps)
                            )

                            StatColumn(
                                value = playerOfWeek.pontuacao.toString().padStart(2, '0'),
                                label = stringResource(R.string.stat_points_caps)
                            )

                            StatColumn(
                                value = String.format("%.1f", playerOfWeek.rating),
                                label = stringResource(R.string.stat_rating_caps)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatColumn(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            color = TextGray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun SectionTitle(
    title: String
) {
    Text(
        text = title,
        color = Color(0xFF6B7280),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
}

fun getTeamInitials(
    name: String
): String {
    val words = name
        .split(" ")
        .filter { it.isNotBlank() }

    return when {
        words.isEmpty() -> "?"
        words.size == 1 -> words.first().take(2).uppercase()
        else -> words.take(2).joinToString("") {
            it.first().uppercaseChar().toString()
        }
    }
}

fun getPlayerInitials(
    name: String
): String {
    val words = name
        .split(" ")
        .filter { it.isNotBlank() }

    return when {
        words.isEmpty() -> "?"
        words.size == 1 -> words.first().take(2).uppercase()
        else -> words.take(2).joinToString("") {
            it.first().uppercaseChar().toString()
        }
    }
}

fun formatDateForHome(
    value: String
): String {
    if (value.isBlank()) {
        return "--"
    }

    val parts = value.take(10).split("-")

    if (parts.size != 3) {
        return value
    }

    return "${parts[2]}/${parts[1]}"
}

fun formatTimeForHome(
    value: String
): String {
    if (value.isBlank()) {
        return "--:--"
    }

    return value.take(5)
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}