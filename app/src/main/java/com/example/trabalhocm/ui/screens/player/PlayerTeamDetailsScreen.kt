package com.example.trabalhocm.ui.screens.player

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.repository.EquipaDetalhesInfo
import com.example.trabalhocm.data.repository.EquipaRepository
import com.example.trabalhocm.data.repository.MembroEquipaDetalhesInfo
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import java.util.Locale

private val TeamDetailsBg = Color(0xFFF6F7FB)
private val TeamDetailsCardBg = Color.White
private val TeamDetailsInputBg = Color(0xFFF0F3F8)
private val TeamDetailsTextGray = Color(0xFF657089)
private val TeamDetailsMuted = Color(0xFF8A92A6)
private val TeamDetailsDarkCard = Color(0xFF111827)
private val TeamDetailsBlue = Color(0xFF0757C8)
private val TeamDetailsRed = Color(0xFFE53935)
private val TeamDetailsBorder = Color(0xFFE7EAF2)

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
            .background(TeamDetailsBg)
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
                    CircularProgressIndicator(color = BrandGreen)
                }
            }

            errorMessage.isNotBlank() -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    TeamDetailsErrorCard(text = errorMessage)
                }
            }

            detalhes != null -> {
                val info = detalhes!!
                val isPublic = info.equipaInfo.tipoEntrada.lowercase() == "publica"

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    TeamDetailsHeroCard(
                        info = info,
                        isPublic = isPublic
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TeamWinRateCard(winRate = info.winRate)

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TeamSmallMetricCard(
                            modifier = Modifier.weight(1f),
                            icon = "◎",
                            title = stringResource(R.string.player_teamdetails_total_goals),
                            value = info.totalGolos.toString(),
                            subtitle = stringResource(R.string.player_teamdetails_team_total)
                        )

                        TeamSmallMetricCard(
                            modifier = Modifier.weight(1f),
                            icon = "▦",
                            title = stringResource(R.string.player_teamdetails_matches_played),
                            value = info.jogosDisputados.toString(),
                            subtitle = stringResource(
                                R.string.player_teamdetails_wdl_format,
                                info.equipaInfo.vitorias,
                                info.empates,
                                info.equipaInfo.derrotas
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    TeamRosterSectionCard(
                        membros = info.membros,
                        onViewPlayerProfileClick = onViewPlayerProfileClick
                    )

                    Spacer(modifier = Modifier.height(20.dp))
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
            .height(66.dp)
            .background(TeamDetailsDarkCard)
            .padding(horizontal = 22.dp),
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
            text = stringResource(R.string.player_teamdetails_title),
            color = BrandWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(start = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "♧",
            color = BrandWhite,
            fontSize = 25.sp,
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = TeamDetailsDarkCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF172033),
                            BrandBlue
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TeamDetailsLogoBox(
                        initials = equipa.iniciais,
                        color = teamDetailsColorFromName(equipa.equipa.nome)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TeamDetailsBadge(
                                text = equipa.divisao.uppercase(),
                                backgroundColor = Color.White.copy(alpha = 0.14f),
                                contentColor = Color.White
                            )

                            TeamDetailsBadge(
                                text = if (isPublic) {
                                    "${stringResource(R.string.player_common_public).uppercase()} 🔓"
                                } else {
                                    "${stringResource(R.string.player_common_private).uppercase()} 🔒"
                                },
                                backgroundColor = if (isPublic) {
                                    BrandGreen.copy(alpha = 0.18f)
                                } else {
                                    TeamDetailsRed.copy(alpha = 0.18f)
                                },
                                contentColor = if (isPublic) BrandGreen else Color(0xFFFFB4B4)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = equipa.equipa.nome,
                            color = BrandWhite,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 30.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "${equipa.modalidadeNome} • ${equipa.cidade}",
                            color = Color.White.copy(alpha = 0.72f),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TeamHeroMiniStat(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.player_teamdetails_win_rate),
                        value = "${String.format(Locale.US, "%.1f", info.winRate)} %"
                    )

                    TeamHeroMiniStat(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.player_teamdetails_matches_played),
                        value = info.jogosDisputados.toString()
                    )
                }
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
            .size(82.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(BrandWhite),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials.take(3).uppercase(),
            color = color,
            fontSize = 23.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TeamDetailsBadge(
    text: String,
    backgroundColor: Color,
    contentColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            color = contentColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.4.sp
        )
    }
}

@Composable
fun TeamHeroMiniStat(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(
            text = label.uppercase(),
            color = Color.White.copy(alpha = 0.68f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.7.sp
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = value,
            color = BrandWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TeamWinRateCard(
    winRate: Double
) {
    val progress = (winRate / 100.0).toFloat().coerceIn(0f, 1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = TeamDetailsCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.player_teamdetails_win_rate).uppercase(),
                    color = TeamDetailsMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${String.format(Locale.US, "%.1f", winRate)} %",
                    color = TeamDetailsBlue,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp)
                        .clip(RoundedCornerShape(50.dp)),
                    color = if (winRate >= 50.0) BrandGreen else TeamDetailsRed,
                    trackColor = TeamDetailsInputBg
                )
            }

            Spacer(modifier = Modifier.width(18.dp))

            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(if (winRate >= 50.0) Color(0xFFEAF8F5) else Color(0xFFFFECEC)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (winRate >= 50.0) "↗" else "↘",
                    color = if (winRate >= 50.0) BrandGreen else TeamDetailsRed,
                    fontSize = 24.sp,
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
        modifier = modifier.height(116.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = TeamDetailsCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$icon ${title.uppercase()}",
                color = TeamDetailsMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            )

            Text(
                text = value,
                color = BrandBlue,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                color = BrandGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TeamRosterSectionCard(
    membros: List<MembroEquipaDetalhesInfo>,
    onViewPlayerProfileClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = TeamDetailsCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.player_teamdetails_active_roster),
                        color = TeamDetailsDarkCard,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = membros.size.toString(),
                        color = TeamDetailsTextGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEAF1FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = membros.size.toString(),
                        color = TeamDetailsBlue,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (membros.isEmpty()) {
                TeamDetailsEmptyCard(
                    text = stringResource(R.string.player_teamdetails_no_players)
                )
            } else {
                membros.forEachIndexed { index, membro ->
                    TeamPlayerRosterCard(
                        membro = membro,
                        onViewProfileClick = {
                            onViewPlayerProfileClick(membro.utilizador.id)
                        }
                    )

                    if (index != membros.lastIndex) {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
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
        stringResource(R.string.player_role_captain)
    } else {
        formatarPapelMembroEquipa(membro.papel)
    }
    val posicao = formatarPosicaoMembroEquipa(membro.posicao)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = TeamDetailsInputBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                if (!membro.utilizador.fotoUrl.isNullOrEmpty()) {
                    coil.compose.AsyncImage(
                        model = membro.utilizador.fotoUrl,
                        contentDescription = stringResource(R.string.player_common_photo),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(BrandWhite),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = teamDetailsInitials(nome),
                            color = BrandBlue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (membro.isCaptain) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(17.dp)
                            .clip(CircleShape)
                            .background(TeamDetailsRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "★",
                            color = BrandWhite,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(13.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = nome,
                        color = TeamDetailsDarkCard,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (membro.isCaptain) {
                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(Color(0xFFFFE4E4))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.player_common_captain).uppercase(),
                                color = TeamDetailsRed,
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.4.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$role · $posicao",
                    color = TeamDetailsTextGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            OutlinedButton(
                onClick = onViewProfileClick,
                modifier = Modifier
                    .width(82.dp)
                    .height(38.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, TeamDetailsBorder)
            ) {
                Text(
                    text = stringResource(R.string.player_common_view),
                    color = TeamDetailsDarkCard,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
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
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = TeamDetailsCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFECEC)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "!",
                    color = TeamDetailsRed,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.player_common_error),
                color = TeamDetailsDarkCard,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = text,
                color = TeamDetailsTextGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TeamDetailsEmptyCard(
    text: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(TeamDetailsInputBg)
            .border(1.dp, TeamDetailsBorder, RoundedCornerShape(18.dp))
            .padding(18.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = TeamDetailsTextGray,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
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

@Composable
fun formatarPapelMembroEquipa(papel: String): String {
    val papelLimpo = papel
        .replace("'", "")
        .replace("\"", "")
        .trim()
        .lowercase()

    return when (papelLimpo) {
        "capitao", "captain" -> stringResource(R.string.player_role_captain)
        "jogador", "player" -> stringResource(R.string.player_role_player)
        "treinador", "coach" -> stringResource(R.string.player_role_coach)
        else -> papelLimpo
            .replace("_", " ")
            .replaceFirstChar {
                if (it.isLowerCase()) it.titlecase() else it.toString()
            }
    }
}

@Composable
fun formatarPosicaoMembroEquipa(posicao: String): String {
    val posicaoLimpa = posicao
        .replace("'", "")
        .replace("\"", "")
        .trim()
        .lowercase()

    return when (posicaoLimpa) {
        "forward", "foward", "avancado", "avançado" -> stringResource(R.string.player_pos_forward)
        "midfielder", "medio", "médio" -> stringResource(R.string.player_pos_midfielder)
        "defender", "defesa" -> stringResource(R.string.player_pos_defender)
        "goalkeeper", "guarda-redes", "guarda redes" -> stringResource(R.string.player_pos_goalkeeper)
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
