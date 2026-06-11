package com.example.trabalhocm.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.repository.EquipaDetalhesInfo
import com.example.trabalhocm.data.repository.EquipaRepository
import com.example.trabalhocm.data.repository.MembroEquipaDetalhesInfo
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import java.util.Locale

@Composable
fun PlayerTeamDetailsScreen(
    idEquipa: Long = 0L,
    onBackClick: () -> Unit = {},
    onInvitePlayerClick: () -> Unit = {},
    onViewPlayerProfileClick: (String) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { EquipaRepository() }

    var detalhes by remember { mutableStateOf<EquipaDetalhesInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(idEquipa) {
        isLoading = true
        errorMessage = ""

        repository.obterDetalhesEquipa(idEquipa)
            .onSuccess {
                detalhes = it
            }
            .onFailure {
                errorMessage = it.message ?: "Erro ao carregar detalhes da equipa."
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
        TeamDetailsTopBar(
            onBackClick = onBackClick
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = BrandGreen
                    )
                }
            }

            errorMessage.isNotBlank() -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(22.dp)
                ) {
                    TeamDetailsErrorCard(
                        text = errorMessage
                    )
                }
            }

            detalhes != null -> {
                val info = detalhes!!
                val isPublic = info.equipaInfo.tipoEntrada.lowercase() == "publica"

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    TeamDetailsHeroCard(
                        info = info,
                        isPublic = isPublic
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp, vertical = 18.dp)
                    ) {
                        TeamWinRateCard(
                            winRate = info.winRate
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            TeamSmallMetricCard(
                                modifier = Modifier.weight(1f),
                                icon = "◎",
                                title = "Total Goals",
                                value = info.totalGolos.toString(),
                                subtitle = "Total da equipa"
                            )

                            TeamSmallMetricCard(
                                modifier = Modifier.weight(1f),
                                icon = "▦",
                                title = "Matches Played",
                                value = info.jogosDisputados.toString(),
                                subtitle = "${info.equipaInfo.vitorias}W - ${info.empates}D - ${info.equipaInfo.derrotas}L"
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Active Roster",
                            color = Color(0xFF20242D),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        if (info.membros.isEmpty()) {
                            TeamDetailsEmptyCard(
                                text = "Esta equipa ainda não tem jogadores associados."
                            )
                        } else {
                            info.membros.forEach { membro ->
                                TeamPlayerRosterCard(
                                    membro = membro,
                                    onViewProfileClick = {
                                        onViewPlayerProfileClick(membro.utilizador.id)
                                    }
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }

        MatchLeagueBottomBar(
            selectedTab = "TEAMS",
            onHomeClick = onHomeClick,
            onTournamentsClick = onTournamentsClick,
            onMatchesClick = onMatchesClick,
            onTeamsClick = onTeamsClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun TeamDetailsTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(BrandBlue)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "←",
            color = BrandWhite,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                onBackClick()
            }
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = "Team Details",
            color = BrandWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.4.sp
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
fun TeamDetailsHeroCard(
    info: EquipaDetalhesInfo,
    isPublic: Boolean = false
) {
    val equipa = info.equipaInfo

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF073061),
                        BrandBlue
                    )
                )
            )
            .padding(horizontal = 22.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TeamDetailsLogoBox(
                initials = equipa.iniciais,
                color = teamDetailsColorFromName(equipa.equipa.nome)
            )

            Spacer(modifier = Modifier.width(18.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFEAF0FB))
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = equipa.divisao,
                            color = Color(0xFF0757C8),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if(isPublic) BrandGreen.copy(alpha = 0.2f) else Color(0xFFE53935).copy(alpha = 0.2f))
                            .padding(horizontal = 9.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if(isPublic) "PUBLIC 🔓" else "PRIVATE 🔒",
                            color = if(isPublic) BrandGreen else Color(0xFFFF8A80),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = equipa.equipa.nome,
                    color = BrandWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "${equipa.modalidadeNome} • ${equipa.cidade}",
                    color = Color(0xFFB8C2D3),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun TeamDetailsLogoBox(
    initials: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .size(90.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(BrandWhite),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials.take(3).uppercase(),
            color = color,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TeamWinRateCard(
    winRate: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Season Win Rate",
                    color = Color(0xFF7D8497),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${String.format(Locale.US, "%.1f", winRate)} %",
                    color = Color(0xFF0757C8),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color(0xFFE0E3EA), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (winRate >= 50.0) "↗" else "↘",
                    color = if (winRate >= 50.0) BrandGreen else Color(0xFFE53935),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TeamSmallMetricCard(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    value: String,
    subtitle: String
) {
    Card(
        modifier = modifier.height(96.dp),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Text(
                text = "$icon $title",
                color = Color(0xFF6D7486),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = value,
                color = BrandBlue,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = BrandGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TeamPlayerRosterCard(
    membro: MembroEquipaDetalhesInfo,
    onViewProfileClick: () -> Unit
) {
    val nome = membro.utilizador.nome
    val role = if (membro.isCaptain) {
        "Captain"
    } else {
        formatarPapelMembroEquipa(membro.papel)
    }
    val posicao = formatarPosicaoMembroEquipa(membro.posicao)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (membro.isCaptain) {
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(74.dp)
                        .background(Color(0xFFB72D2D))
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!membro.utilizador.fotoUrl.isNullOrEmpty()) {
                    coil.compose.AsyncImage(
                        model = membro.utilizador.fotoUrl,
                        contentDescription = "Foto do Jogador",
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF0F2FA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = teamDetailsInitials(nome),
                            color = BrandBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = nome,
                            color = BrandBlue,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )

                        if (membro.isCaptain) {
                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFFFFE4E4))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "CAPTAIN",
                                    color = Color(0xFFB72D2D),
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Text(
                        text = "$role · $posicao",
                        color = Color(0xFF7D8497),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                OutlinedButton(
                    onClick = onViewProfileClick,
                    modifier = Modifier
                        .width(82.dp)
                        .height(36.dp),
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(
                        text = "View",
                        color = Color(0xFF062B67),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun TeamDetailsErrorCard(
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = "Erro: $text",
            color = Color(0xFFD01818),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(18.dp)
        )
    }
}

@Composable
fun TeamDetailsEmptyCard(
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

fun teamDetailsColorFromName(nome: String): Color {
    val nomeNormalizado = nome.lowercase()

    return when {
        nomeNormalizado.contains("benfica") -> Color(0xFFE53935)
        nomeNormalizado.contains("porto") -> Color(0xFF0757C8)
        nomeNormalizado.contains("sporting") -> BrandGreen
        nomeNormalizado.contains("vianense") -> Color(0xFFD19A00)
        nomeNormalizado.contains("mancos") -> Color(0xFF4A555C)
        else -> Color(0xFF49617F)
    }
}

fun teamDetailsInitials(nome: String): String {
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

fun formatarPapelMembroEquipa(papel: String): String {
    val papelLimpo = papel
        .replace("'", "")
        .replace("\"", "")
        .trim()
        .lowercase()

    return when (papelLimpo) {
        "capitao", "captain" -> "Captain"
        "jogador", "player" -> "Player"
        "treinador", "coach" -> "Coach"
        else -> papelLimpo
            .replace("_", " ")
            .replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            }
    }
}

fun formatarPosicaoMembroEquipa(posicao: String): String {
    val posicaoLimpa = posicao
        .replace("'", "")
        .replace("\"", "")
        .trim()
        .lowercase()

    return when (posicaoLimpa) {
        "forward", "foward", "avancado", "avançado" -> "Forward"
        "midfielder", "medio", "médio" -> "Midfielder"
        "defender", "defesa" -> "Defender"
        "goalkeeper", "guarda-redes", "guarda redes" -> "Goalkeeper"
        else -> posicaoLimpa
            .replace("_", " ")
            .replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            }
    }
}

@Preview(showBackground = true, name = "Player Team Details")
@Composable
fun PlayerTeamDetailsPreview() {
    PlayerTeamDetailsScreen(
        idEquipa = 1L
    )
}