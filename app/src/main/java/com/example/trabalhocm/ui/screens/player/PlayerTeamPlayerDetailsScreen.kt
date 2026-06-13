package com.example.trabalhocm.ui.screens.player

import android.net.Uri
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.trabalhocm.R
import com.example.trabalhocm.data.remote.SupabaseClient
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private val TeamPlayerBg = Color(0xFFF6F7FB)
private val TeamPlayerCardBg = Color.White
private val TeamPlayerDark = Color(0xFF0B1F3A)
private val TeamPlayerBlue = Color(0xFF0757C8)
private val TeamPlayerGreen = Color(0xFF008D7D)
private val TeamPlayerTextGray = Color(0xFF657089)
private val TeamPlayerMuted = Color(0xFF8A92A6)
private val TeamPlayerInputBg = Color(0xFFF0F3F8)
private val TeamPlayerBorder = Color(0xFFE7EAF2)

@Composable
fun PlayerTeamPlayerDetailsScreen(
    playerId: String = "",
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val loadingText = stringResource(R.string.player_common_loading)
    val noTeamText = stringResource(R.string.player_teamplayer_no_team)

    var isLoading by remember { mutableStateOf(true) }
    var nome by remember { mutableStateOf(loadingText) }
    var fotoUrl by remember { mutableStateOf<Uri?>(null) }
    var equipaAtual by remember { mutableStateOf(noTeamText) }
    var golos by remember { mutableIntStateOf(0) }
    var assistencias by remember { mutableIntStateOf(0) }
    var historico by remember { mutableStateOf<List<Pair<MembroEquipaSimplesDTO, String>>>(emptyList()) }

    LaunchedEffect(playerId) {
        if (playerId.isBlank()) return@LaunchedEffect
        isLoading = true
        try {
            val user = SupabaseClient.client.from("utilizador").select {
                filter { eq("id", playerId) }
            }.decodeSingleOrNull<UtilizadorSimplesDTO>()

            if (user != null) {
                nome = user.nome
                if (!user.fotoUrl.isNullOrBlank()) {
                    fotoUrl = Uri.parse("${user.fotoUrl}?v=${System.currentTimeMillis()}")
                }
            }

            val stats = SupabaseClient.client.from("estatistica_jogador").select {
                filter {
                    eq("id_utilizador", playerId)
                    eq("id_modalidade", 1)
                }
            }.decodeSingleOrNull<EstatisticaSimplesDTO>()

            if (stats != null) {
                golos = stats.pontuacao
                assistencias = stats.vitorias
            }

            val rows = SupabaseClient.client.from("membro_equipa").select {
                filter { eq("id_utilizador", playerId) }
            }.decodeList<MembroEquipaSimplesDTO>()

            val listaFinal = mutableListOf<Pair<MembroEquipaSimplesDTO, String>>()
            for (row in rows) {
                if (row.idEquipa != null) {
                    val eqData = SupabaseClient.client.from("equipa").select {
                        filter { eq("id", row.idEquipa) }
                    }.decodeSingleOrNull<EquipaSimplesDTO>()

                    if (eqData != null) {
                        listaFinal.add(Pair(row, eqData.nome))
                    }
                }
            }

            historico = listaFinal.sortedByDescending { it.first.dataEntrada }
            val equipaAtiva = listaFinal.firstOrNull {
                it.first.dataSaida == null && it.first.estadoConvite == "aceite"
            }
            if (equipaAtiva != null) equipaAtual = equipaAtiva.second
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TeamPlayerBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        TeamPlayerDetailsTopBar(onBackClick = onBackClick)

        if (isLoading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TeamPlayerGreen)
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                TeamPlayerProfileCard(
                    nome = nome,
                    fotoUrl = fotoUrl,
                    equipaAtual = equipaAtual,
                    golos = golos,
                    assistencias = assistencias,
                    totalEquipas = historico.size
                )

                Spacer(modifier = Modifier.height(18.dp))

                TeamPlayerSectionTitle(
                    icon = "◎",
                    title = stringResource(R.string.player_teamplayer_season_stats)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PlayerSeasonStatCard(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.player_teamplayer_goals),
                        value = golos.toString(),
                        subtitle = stringResource(R.string.player_common_total),
                        icon = "⚽",
                        progress = (golos / 50f).coerceIn(0f, 1f)
                    )

                    PlayerSeasonStatCard(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.player_teamplayer_assists),
                        value = assistencias.toString(),
                        subtitle = stringResource(R.string.player_common_total),
                        icon = "✦",
                        progress = (assistencias / 30f).coerceIn(0f, 1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                TeamPlayerSectionTitle(
                    icon = "◷",
                    title = stringResource(R.string.player_teamplayer_team_history)
                )

                Spacer(modifier = Modifier.height(12.dp))

                TeamPlayerHistoryCard(historico = historico)

                Spacer(modifier = Modifier.height(20.dp))
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
fun TeamPlayerDetailsTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(TeamPlayerDark)
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
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.player_teamplayer_title),
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.player_teamplayer_team_history),
                color = Color.White.copy(alpha = 0.72f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "♧",
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TeamPlayerProfileCard(
    nome: String,
    fotoUrl: Uri?,
    equipaAtual: String,
    golos: Int,
    assistencias: Int,
    totalEquipas: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = TeamPlayerDark),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF172033),
                            TeamPlayerBlue
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(98.dp)
                        .clip(CircleShape)
                        .border(3.dp, TeamPlayerGreen, CircleShape)
                        .background(Color.White.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (fotoUrl != null) {
                        AsyncImage(
                            model = fotoUrl,
                            contentDescription = stringResource(R.string.player_common_photo),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = nome.take(1).uppercase(),
                            color = Color.White,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = nome,
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(10.dp))

                TeamPlayerHeroBadge(
                    text = "🛡 ${equipaAtual.uppercase()}"
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TeamPlayerHeroStat(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.player_teamplayer_goals),
                        value = golos.toString()
                    )

                    TeamPlayerHeroStat(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.player_teamplayer_assists),
                        value = assistencias.toString()
                    )

                    TeamPlayerHeroStat(
                        modifier = Modifier.weight(1f),
                        label = stringResource(R.string.player_teamplayer_team_history),
                        value = totalEquipas.toString()
                    )
                }
            }
        }
    }
}

@Composable
fun TeamPlayerHeroBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(Color.White.copy(alpha = 0.14f))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White.copy(alpha = 0.88f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TeamPlayerHeroStat(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label.uppercase(),
            color = Color.White.copy(alpha = 0.68f),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TeamPlayerSectionTitle(
    icon: String,
    title: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFEAF0FF)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                color = TeamPlayerBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = title,
            color = TeamPlayerDark,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PlayerSeasonStatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    subtitle: String,
    icon: String,
    progress: Float
) {
    Card(
        modifier = modifier.height(134.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = TeamPlayerCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = label.uppercase(),
                    color = TeamPlayerMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(9.dp))
                        .background(TeamPlayerGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
                        color = TeamPlayerGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column {
                Text(
                    text = value,
                    color = TeamPlayerBlue,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TeamPlayerTextGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50.dp)),
                color = TeamPlayerBlue,
                trackColor = TeamPlayerInputBg
            )
        }
    }
}

@Composable
fun TeamPlayerHistoryCard(historico: List<Pair<MembroEquipaSimplesDTO, String>>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = TeamPlayerCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            if (historico.isEmpty()) {
                TeamPlayerEmptyHistory()
            } else {
                historico.forEachIndexed { index, data ->
                    val membro = data.first
                    val nomeEquipa = data.second
                    val isAtivo = membro.dataSaida == null && membro.estadoConvite == "aceite"
                    val startAno = membro.dataEntrada?.take(4) ?: "..."
                    val endAno = membro.dataSaida?.take(4) ?: stringResource(R.string.player_common_now)

                    TeamPlayerHistoryRow(
                        nomeEquipa = nomeEquipa,
                        isAtivo = isAtivo,
                        startAno = startAno,
                        endAno = endAno
                    )

                    if (index < historico.lastIndex) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 6.dp),
                            color = TeamPlayerBorder
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TeamPlayerEmptyHistory() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(TeamPlayerInputBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "◷",
                color = TeamPlayerMuted,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.player_teamplayer_no_history),
            color = TeamPlayerTextGray,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun TeamPlayerHistoryRow(
    nomeEquipa: String,
    isAtivo: Boolean,
    startAno: String,
    endAno: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(TeamPlayerColorFromName(nomeEquipa).copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = nomeEquipa.take(2).uppercase(),
                color = TeamPlayerColorFromName(nomeEquipa),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = nomeEquipa,
                color = TeamPlayerDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$startAno – $endAno",
                color = TeamPlayerTextGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        if (isAtivo) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(TeamPlayerGreen.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = stringResource(R.string.player_common_active),
                    color = TeamPlayerGreen,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

private fun TeamPlayerColorFromName(name: String): Color {
    val colors = listOf(
        Color(0xFF0757C8),
        Color(0xFF008D7D),
        Color(0xFFE67E22),
        Color(0xFF8E44AD),
        Color(0xFFD32F2F),
        Color(0xFF2E7D32)
    )
    return colors[(name.hashCode() and Int.MAX_VALUE) % colors.size]
}

@Serializable
data class UtilizadorSimplesDTO(
    val nome: String,
    @SerialName("foto_url") val fotoUrl: String? = null
)

@Serializable
data class EquipaSimplesDTO(val nome: String)

@Serializable
data class EstatisticaSimplesDTO(
    val pontuacao: Int,
    val vitorias: Int,
    @SerialName("id_modalidade") val idModalidade: Int
)

@Serializable
data class MembroEquipaSimplesDTO(
    @SerialName("id_equipa") val idEquipa: Long? = null,
    @SerialName("estado_convite") val estadoConvite: String? = null,
    @SerialName("data_entrada") val dataEntrada: String? = null,
    @SerialName("data_saida") val dataSaida: String? = null
)
