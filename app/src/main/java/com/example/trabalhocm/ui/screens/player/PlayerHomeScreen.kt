package com.example.trabalhocm.ui.screens.player

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.repository.PlayerHomeData
import com.example.trabalhocm.data.repository.PlayerHomeFixture
import com.example.trabalhocm.data.repository.PlayerHomeLiveMatch
import com.example.trabalhocm.data.repository.PlayerHomePlayerStats
import com.example.trabalhocm.data.repository.PlayerHomeRepository
import com.example.trabalhocm.data.repository.PlayerHomeTournament
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun PlayerHomeScreen(
    onTournamentsClick: () -> Unit = {},
    onCasualMatchesClick: () -> Unit = {},
    onLiveMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onWatchStreamClick: (Long) -> Unit = {},
    onFixtureDetailsClick: (Long) -> Unit = {}
) {
    val repository = remember { PlayerHomeRepository() }

    var homeData by remember { mutableStateOf<PlayerHomeData?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = ""

        repository.carregarDadosHome()
            .onSuccess { dados ->
                homeData = dados
            }
            .onFailure { erro ->
                errorMessage = erro.message ?: "Erro ao carregar dados da Home."
            }

        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        PlayerHomeTopBar()

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 20.dp)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BrandGreen)
                    }
                }

                errorMessage.isNotBlank() -> {
                    PlayerHomeErrorCard(errorMessage)
                }

                else -> {
                    val dados = homeData

                    PlayerLiveCard(
                        liveMatch = dados?.liveMatch,
                        onWatchStreamClick = onWatchStreamClick
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    SectionTitle(title = "QUICK ACTIONS")

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.EmojiEvents,
                            title = "TOURNAMENTS",
                            onClick = onTournamentsClick
                        )

                        QuickActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.AddCircleOutline,
                            title = "CASUAL MATCHES",
                            onClick = onCasualMatchesClick
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuickActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Default.RssFeed,
                            title = "LIVE MATCHES",
                            onClick = onLiveMatchesClick
                        )

                        QuickActionCard(
                            modifier = Modifier.weight(1f),
                            icon = Icons.Outlined.Groups,
                            title = "TEAMS",
                            onClick = onTeamsClick
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SectionTitle(title = "ACTIVE TOURNAMENTS")

                        Text(
                            text = "VIEW ALL",
                            color = Color(0xFF4167C8),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onTournamentsClick() }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val torneiosAtivos = dados?.activeTournaments.orEmpty()

                    if (torneiosAtivos.isEmpty()) {
                        PlayerHomeEmptyCard("Ainda não existem torneios ativos.")
                    } else {
                        torneiosAtivos.forEachIndexed { index, torneio ->
                            ActiveTournamentCard(
                                torneio = torneio,
                                accentColor = if (index % 2 == 0) BrandGreen else Color(0xFF3566C9),
                                icon = if (index % 2 == 0) "♕" else "♖"
                            )

                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    SectionTitle(title = "UPCOMING FIXTURES")

                    Spacer(modifier = Modifier.height(14.dp))

                    UpcomingFixturesCard(
                        fixtures = dados?.upcomingFixtures.orEmpty(),
                        onFixtureDetailsClick = onFixtureDetailsClick
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    SectionTitle(title = "PERFORMANCE INSIGHTS")

                    Spacer(modifier = Modifier.height(14.dp))

                    PlayerOfWeekCard(
                        playerStats = dados?.playerOfWeek
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    GlobalRankCard(
                        rank = dados?.currentUserRank,
                        variationText = dados?.rankVariationText ?: "↗ 0%"
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
        }

        MatchLeagueBottomBar(
            selectedTab = "HOME",
            onHomeClick = {},
            onTournamentsClick = onTournamentsClick,
            onMatchesClick = onCasualMatchesClick,
            onTeamsClick = onTeamsClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun PlayerHomeTopBar() {
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
            text = "Home",
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
fun PlayerLiveCard(
    liveMatch: PlayerHomeLiveMatch?,
    onWatchStreamClick: (Long) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(345.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF102845),
                            Color(0xFF071A30)
                        )
                    )
                )
                .padding(22.dp)
        ) {
            if (liveMatch == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "NO LIVE MATCH",
                        color = BrandGreen,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "There are no live matches available right now.",
                        color = Color(0xFFA6AFBD),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(BrandGreen.copy(alpha = 0.95f))
                                .padding(horizontal = 18.dp, vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "LIVE\nNOW",
                                color = Color(0xFF9DF4E9),
                                fontSize = 12.sp,
                                lineHeight = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.4.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.width(18.dp))

                        Text(
                            text = "${liveMatch.torneioNome.uppercase()} • GW\n26",
                            color = Color(0xFFA6AFBD),
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(36.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TeamScoreBlock(
                            name = liveMatch.equipaCasa
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = liveMatch.pontosCasa.toString(),
                                color = BrandWhite,
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Text(
                                text = "-",
                                color = BrandGreen,
                                fontSize = 44.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.width(14.dp))

                            Text(
                                text = liveMatch.pontosFora.toString(),
                                color = BrandWhite,
                                fontSize = 56.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        TeamScoreBlock(
                            name = liveMatch.equipaFora
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${liveMatch.minuto}'",
                        color = BrandGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Button(
                        onClick = { onWatchStreamClick(liveMatch.idJogo) },
                        modifier = Modifier
                            .width(172.dp)
                            .height(54.dp),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandGreen,
                            contentColor = BrandWhite
                        )
                    ) {
                        Text(
                            text = "WATCH STREAM",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TeamScoreBlock(
    name: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlayerHomeTeamLogo(
            nome = name,
            size = 70.dp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = name,
            color = BrandWhite,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color(0xFF7D8497),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 3.sp
    )
}

@Composable
fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFEAF4F2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = BrandGreen,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                color = Color(0xFF303646),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
        }
    }
}

@Composable
fun ActiveTournamentCard(
    torneio: PlayerHomeTournament,
    accentColor: Color,
    icon: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(116.dp),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxSize()
                    .background(accentColor)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp, vertical = 17.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column {
                        Text(
                            text = torneio.nome,
                            color = BrandBlue,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = torneio.papel,
                            color = accentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }

                    Text(
                        text = icon,
                        color = Color(0xFFE2E6F2),
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "PROGRESS",
                        color = Color(0xFF6D7486),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = torneio.progressoTexto,
                        color = Color(0xFF6D7486),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { torneio.progresso },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    color = accentColor,
                    trackColor = Color(0xFFECEEF7)
                )
            }
        }
    }
}

@Composable
fun UpcomingFixturesCard(
    fixtures: List<PlayerHomeFixture>,
    onFixtureDetailsClick: (Long) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        if (fixtures.isEmpty()) {
            Text(
                text = "Ainda não existem jogos agendados.",
                color = Color(0xFF6D7486),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(18.dp)
            )
        } else {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                fixtures.forEachIndexed { index, fixture ->
                    FixtureRow(
                        fixture = fixture,
                        onDetailsClick = { onFixtureDetailsClick(fixture.idJogo) }
                    )

                    if (index < fixtures.lastIndex) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFE8EAF2))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FixtureRow(
    fixture: PlayerHomeFixture,
    onDetailsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.width(72.dp)
        ) {
            Text(
                text = formatFixtureDate(fixture.data),
                color = BrandBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = fixture.hora.take(5),
                color = Color(0xFF7D8497),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Box(
            modifier = Modifier
                .width(1.dp)
                .height(34.dp)
                .background(Color(0xFFE8EAF2))
        )

        Spacer(modifier = Modifier.width(28.dp))

        PlayerHomeTeamLogo(
            nome = fixture.equipaCasa,
            size = 34.dp
        )

        Spacer(modifier = Modifier.width(20.dp))

        Text(
            text = "VS",
            color = Color(0xFFD2D6E3),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(20.dp))

        PlayerHomeTeamLogo(
            nome = fixture.equipaFora,
            size = 34.dp
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(Color(0xFFF0F2FA))
                .clickable { onDetailsClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "♧",
                color = Color(0xFF7D8497),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun PlayerOfWeekCard(
    playerStats: PlayerHomePlayerStats?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(310.dp),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BrandBlue)
                .padding(horizontal = 24.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "PLAYER OF THE WEEK",
                color = BrandGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (playerStats == null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Ainda não existem estatísticas suficientes.",
                        color = BrandWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Image(
                    painter = painterResource(R.drawable.avatar_player),
                    contentDescription = playerStats.nome,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(86.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF0F2FA))
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = playerStats.nome.uppercase(),
                    color = BrandWhite,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${playerStats.username} • PLAYER",
                    color = BrandGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    PlayerStat(
                        value = playerStats.vitorias.toString().padStart(2, '0'),
                        label = "WINS"
                    )

                    PlayerStat(
                        value = playerStats.empates.toString().padStart(2, '0'),
                        label = "DRAWS"
                    )

                    PlayerStat(
                        value = String.format(Locale.US, "%.1f", playerStats.rating),
                        label = "RATING"
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerStat(
    value: String,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = BrandWhite,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            color = Color(0xFF7D8497),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
fun GlobalRankCard(
    rank: Int?,
    variationText: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFF0F2FA)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "▮",
                    color = Color(0xFF3566C9),
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Column {
                Text(
                    text = "YOUR GLOBAL RANK",
                    color = Color(0xFF7D8497),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.8.sp
                )

                Text(
                    text = rank?.let { "#$it" } ?: "N/A",
                    color = BrandBlue,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = variationText,
                color = BrandGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PlayerHomeEmptyCard(
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFF6D7486),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(18.dp)
        )
    }
}

@Composable
fun PlayerHomeErrorCard(
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFFD01818),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(18.dp)
        )
    }
}

fun playerHomeLogoForTeam(nome: String): Int? {
    val nomeLower = nome.lowercase()

    return when {
        nomeLower.contains("sporting") -> R.drawable.team_sporting
        nomeLower.contains("vianense") -> R.drawable.team_vianense
        else -> null
    }
}

@Composable
fun PlayerHomeTeamLogo(
    nome: String,
    size: Dp
) {
    val logo = playerHomeLogoForTeam(nome)

    if (logo != null) {
        Image(
            painter = painterResource(logo),
            contentDescription = nome,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(8.dp))
        )
    } else {
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(Color(0xFFEAF0FB)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = playerHomeTeamInitials(nome),
                color = BrandBlue,
                fontSize = if (size.value >= 60f) 16.sp else 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun playerHomeTeamInitials(nome: String): String {
    val palavras = nome
        .split(" ")
        .filter { it.isNotBlank() }

    return when {
        palavras.isEmpty() -> "?"
        palavras.size == 1 -> palavras.first().take(2).uppercase()
        else -> palavras.take(2).joinToString("") {
            it.first().uppercaseChar().toString()
        }
    }
}

fun formatFixtureDate(data: String): String {
    return runCatching {
        LocalDate.parse(data.take(10))
            .format(DateTimeFormatter.ofPattern("MMM dd", Locale.ENGLISH))
            .uppercase(Locale.ENGLISH)
    }.getOrDefault(data.take(5))
}

@Preview(showBackground = true, name = "Player Home Screen")
@Composable
fun PlayerHomeScreenPreview() {
    PlayerHomeScreen()
}