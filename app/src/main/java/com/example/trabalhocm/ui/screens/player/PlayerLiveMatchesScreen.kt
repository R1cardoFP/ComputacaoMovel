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
            .background(Color(0xFFF4F5FA))
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
            Text(
                text = "LIVE CENTER",
                color = Color(0xFF0757C8),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Live Matches",
                color = BrandBlue,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Watch all matches currently live on the platform.",
                color = Color(0xFF51607A),
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))

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
                    PlayerLiveMatchesMessageCard(
                        text = errorMessage,
                        color = Color(0xFFD01818)
                    )
                }

                liveMatches.isEmpty() -> {
                    PlayerLiveMatchesMessageCard(
                        text = "Não existem jogos em direto neste momento.",
                        color = Color(0xFF51607A)
                    )
                }

                else -> {
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
        Text(
            text = "‹",
            color = BrandWhite,
            fontSize = 34.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { onBackClick() }
        )

        Text(
            text = "Live Matches",
            color = BrandWhite,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(start = 6.dp)
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
fun PlayerLiveMatchCard(
    jogo: LiveMatchInfo,
    onWatchLiveClick: () -> Unit,
    onDetailsClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
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
                    textColor = Color(0xFF0757C8)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = jogo.torneioNome,
                color = Color(0xFF6D7486),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PlayerLiveMatchesTeamBlock(
                    teamName = jogo.equipaCasa,
                    modifier = Modifier.weight(1f)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${jogo.pontosCasa} - ${jogo.pontosFora}",
                        color = BrandBlue,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${jogo.minuto}'",
                        color = Color(0xFFE53935),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                PlayerLiveMatchesTeamBlock(
                    teamName = jogo.equipaFora,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "⌖ ${jogo.local}",
                color = Color(0xFF51607A),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onDetailsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF7F8FC),
                        contentColor = BrandBlue
                    )
                ) {
                    Text(
                        text = "VIEW DETAILS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onWatchLiveClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(5.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen,
                        contentColor = BrandWhite
                    )
                ) {
                    Text(
                        text = "WATCH LIVE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
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
            size = 48.dp
        )

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = teamName,
            color = BrandBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
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
            .padding(horizontal = 9.dp, vertical = 5.dp),
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
fun PlayerLiveMatchesMessageCard(
    text: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 13.sp,
            modifier = Modifier.padding(18.dp),
            fontWeight = FontWeight.Medium
        )
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