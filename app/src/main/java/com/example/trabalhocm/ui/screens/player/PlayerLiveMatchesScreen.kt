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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.repository.LiveMatchInfo
import com.example.trabalhocm.data.repository.LiveMatchRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite

private val PlayerLiveBg = Color(0xFFF4F6FA)
private val PlayerLiveCard = Color.White
private val PlayerLiveTextGray = Color(0xFF596579)
private val PlayerLiveInputBg = Color(0xFFF1F4F8)
private val PlayerLiveDanger = Color(0xFFE53935)

@Composable
fun PlayerLiveMatchesScreen(
    onBackClick: () -> Unit = {},
    onWatchLiveClick: (Long) -> Unit = {},
    onDetailsClick: (Long) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { LiveMatchRepository() }

    var liveMatches by remember { mutableStateOf<List<LiveMatchInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = ""

        repository.listarJogosEmDireto()
            .onSuccess { jogos ->
                liveMatches = jogos
            }
            .onFailure { erro ->
                errorMessage = erro.message ?: "Erro ao carregar jogos em direto."
            }

        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PlayerLiveBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        PlayerLiveMatchesTopBar(
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            PlayerLiveMatchesHeroCard(
                totalLiveMatches = liveMatches.size
            )

            Spacer(modifier = Modifier.height(18.dp))

            when {
                isLoading -> {
                    PlayerLiveMatchesLoadingCard()
                }

                errorMessage.isNotBlank() -> {
                    PlayerLiveMatchesMessageCard(
                        title = "Erro ao carregar",
                        text = errorMessage,
                        color = PlayerLiveDanger
                    )
                }

                liveMatches.isEmpty() -> {
                    PlayerLiveMatchesMessageCard(
                        title = "Sem jogos em direto",
                        text = "Não existem jogos em direto neste momento.",
                        color = PlayerLiveTextGray
                    )
                }

                else -> {
                    Text(
                        text = "Jogos disponíveis",
                        color = BrandBlue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    liveMatches.forEach { jogo ->
                        PlayerLiveMatchCard(
                            jogo = jogo,
                            onWatchLiveClick = { onWatchLiveClick(jogo.idJogo) },
                            onDetailsClick = { onDetailsClick(jogo.idJogo) }
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        MatchLeagueBottomBar(
            selectedTab = "HOME",
            onHomeClick = onHomeClick,
            onTournamentsClick = onTournamentsClick,
            onMatchesClick = onMatchesClick,
            onTeamsClick = onTeamsClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun PlayerLiveMatchesTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(BrandBlue)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f))
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "‹",
                color = BrandWhite,
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 3.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "Live Matches",
                color = BrandWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            )

            Text(
                text = "Acompanha os jogos em direto",
                color = BrandWhite.copy(alpha = 0.78f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "●",
                color = BrandGreen,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PlayerLiveMatchesHeroCard(
    totalLiveMatches: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlayerLiveMatchesBadge(
                    text = "LIVE CENTER",
                    backgroundColor = BrandWhite.copy(alpha = 0.14f),
                    textColor = BrandWhite
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(BrandGreen)
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$totalLiveMatches LIVE",
                        color = BrandWhite,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Jogos em direto",
                color = BrandWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Vê todos os jogos que estão a decorrer na plataforma e acompanha o marcador em tempo real.",
                color = BrandWhite.copy(alpha = 0.82f),
                fontSize = 13.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PlayerLiveMatchCard(
    jogo: LiveMatchInfo,
    onWatchLiveClick: () -> Unit,
    onDetailsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = PlayerLiveCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PlayerLiveMatchesBadge(
                    text = "● LIVE NOW",
                    backgroundColor = BrandGreen.copy(alpha = 0.12f),
                    textColor = BrandGreen
                )

                PlayerLiveMatchesBadge(
                    text = "LIVE STREAM",
                    backgroundColor = Color(0xFFEAF0FB),
                    textColor = BrandBlue
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = jogo.torneioNome.uppercase(),
                color = PlayerLiveTextGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlayerLiveMatchesTeamBlock(
                    teamName = jogo.equipaCasa,
                    modifier = Modifier.weight(1f)
                )

                PlayerLiveMatchesScoreBlock(
                    homeScore = jogo.pontosCasa,
                    awayScore = jogo.pontosFora,
                    minute = jogo.minuto
                )

                PlayerLiveMatchesTeamBlock(
                    teamName = jogo.equipaFora,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PlayerLiveInputBg)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "⌖ ${jogo.local}",
                    color = PlayerLiveTextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onDetailsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PlayerLiveInputBg,
                        contentColor = BrandBlue
                    )
                ) {
                    Text(
                        text = "VER DETALHES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Button(
                    onClick = onWatchLiveClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen,
                        contentColor = BrandWhite
                    )
                ) {
                    Text(
                        text = "VER LIVE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerLiveMatchesScoreBlock(
    homeScore: Int,
    awayScore: Int,
    minute: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(BrandBlue)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$homeScore - $awayScore",
                color = BrandWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        PlayerLiveMatchesBadge(
            text = "$minute'",
            backgroundColor = PlayerLiveDanger.copy(alpha = 0.12f),
            textColor = PlayerLiveDanger
        )
    }
}

@Composable
fun PlayerLiveMatchesTeamBlock(
    teamName: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PlayerLiveMatchesTeamLogo(
            nome = teamName,
            size = 54.dp
        )

        Spacer(modifier = Modifier.height(9.dp))

        Text(
            text = teamName,
            color = BrandBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 14.sp
        )
    }
}

@Composable
fun PlayerLiveMatchesBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.7.sp
        )
    }
}

@Composable
fun PlayerLiveMatchesLoadingCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = PlayerLiveCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = BrandGreen)
        }
    }
}

@Composable
fun PlayerLiveMatchesMessageCard(
    title: String,
    text: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = PlayerLiveCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "!",
                    color = color,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                color = BrandBlue,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = text,
                color = PlayerLiveTextGray,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

fun playerLiveMatchesLogoForTeam(nome: String): Int? {
    val nomeLower = nome.lowercase()

    return when {
        nomeLower.contains("sporting") -> R.drawable.team_sporting
        nomeLower.contains("vianense") -> R.drawable.team_vianense
        else -> null
    }
}

@Composable
fun PlayerLiveMatchesTeamLogo(
    nome: String,
    size: Dp
) {
    val logo = playerLiveMatchesLogoForTeam(nome)

    if (logo != null) {
        Image(
            painter = painterResource(logo),
            contentDescription = nome,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(size)
                .clip(RoundedCornerShape(14.dp))
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
                text = playerLiveMatchesTeamInitials(nome),
                color = BrandBlue,
                fontSize = if (size.value >= 45f) 13.sp else 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun playerLiveMatchesTeamInitials(nome: String): String {
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

@Preview(showBackground = true)
@Composable
fun PlayerLiveMatchesScreenPreview() {
    PlayerLiveMatchesScreen()
}
