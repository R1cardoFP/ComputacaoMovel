package com.example.trabalhocm.ui.screens.player

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.trabalhocm.R
import com.example.trabalhocm.data.remote.SupabaseClient
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
            val user = SupabaseClient.client.from("utilizador").select { filter { eq("id", playerId) } }.decodeSingleOrNull<UtilizadorSimplesDTO>()
            if (user != null) {
                nome = user.nome
                if (!user.fotoUrl.isNullOrBlank()) fotoUrl = Uri.parse("${user.fotoUrl}?v=${System.currentTimeMillis()}")
            }

            val stats = SupabaseClient.client.from("estatistica_jogador").select { filter { eq("id_utilizador", playerId); eq("id_modalidade", 1) } }.decodeSingleOrNull<EstatisticaSimplesDTO>()
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
            val equipaAtiva = listaFinal.firstOrNull { it.first.dataSaida == null && it.first.estadoConvite == "aceite" }
            if (equipaAtiva != null) equipaAtual = equipaAtiva.second
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isLoading = false
    }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF4F5FA)).statusBarsPadding().navigationBarsPadding()
    ) {
        TeamPlayerDetailsTopBar(onBackClick = onBackClick)

        if (isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandGreen)
            }
        } else {
            Column(
                modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 22.dp, vertical = 18.dp)
            ) {
                TeamPlayerProfileCard(nome = nome, fotoUrl = fotoUrl, equipaAtual = equipaAtual)
                Spacer(modifier = Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("▥", color = Color(0xFF0757C8), fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.player_teamplayer_season_stats), color = BrandBlue, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    PlayerSeasonStatCard(
                        modifier = Modifier.weight(1f), label = stringResource(R.string.player_teamplayer_goals), value = golos.toString(), subtitle = stringResource(R.string.player_common_total), icon = "⊙", progress = (golos / 50f).coerceIn(0f, 1f)
                    )
                    PlayerSeasonStatCard(
                        modifier = Modifier.weight(1f), label = stringResource(R.string.player_teamplayer_assists), value = assistencias.toString(), subtitle = stringResource(R.string.player_common_total), icon = "☆", progress = (assistencias / 30f).coerceIn(0f, 1f)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("◷", color = Color(0xFF0757C8), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.player_teamplayer_team_history), color = BrandBlue, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(14.dp))
                TeamPlayerHistoryCard(historico = historico)
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
        MatchLeagueBottomBar(
            selectedTab = "TEAMS", onHomeClick = onHomeClick, onTournamentsClick = onTournamentsClick,
            onMatchesClick = onMatchesClick, onTeamsClick = onTeamsClick, onProfileClick = onProfileClick
        )
    }
}

@Composable
fun TeamPlayerDetailsTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(72.dp).background(BrandBlue).padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("←", color = BrandWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onBackClick() })
        Spacer(modifier = Modifier.width(14.dp))
        Text(stringResource(R.string.player_teamplayer_title), color = BrandWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
        Spacer(modifier = Modifier.weight(1f))
        Text("♧", color = BrandWhite, fontSize = 27.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TeamPlayerProfileCard(nome: String, fotoUrl: Uri?, equipaAtual: String) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 22.dp), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(86.dp).clip(CircleShape).background(Color(0xFFF0F2FA)), contentAlignment = Alignment.Center
            ) {
                if (fotoUrl != null) {
                    AsyncImage(model = fotoUrl, contentDescription = stringResource(R.string.player_common_photo), contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                } else {
                    Text(text = nome.take(1).uppercase(), color = BrandBlue, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(text = nome, color = BrandBlue, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier.clip(RoundedCornerShape(18.dp)).background(Color(0xFFEFF1F6)).padding(horizontal = 10.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🛡 ${equipaAtual.uppercase()}", color = Color(0xFF6D7486), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
            }
        }
    }
}

@Composable
fun PlayerSeasonStatCard(modifier: Modifier = Modifier, label: String, value: String, subtitle: String, icon: String, progress: Float) {
    Card(
        modifier = modifier.height(128.dp), shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(label, color = Color(0xFF6D7486), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp, modifier = Modifier.weight(1f))
                Box(modifier = Modifier.size(24.dp).clip(RoundedCornerShape(5.dp)).background(BrandGreen.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                    Text(icon, color = BrandGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(value, color = Color(0xFF0757C8), fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color(0xFF7D8497), fontSize = 10.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progress }, modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(10.dp)),
                color = Color(0xFF0757C8), trackColor = Color(0xFFE8EAF2)
            )
        }
    }
}

@Composable
fun TeamPlayerHistoryCard(historico: List<Pair<MembroEquipaSimplesDTO, String>>) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            if (historico.isEmpty()) {
                Text(stringResource(R.string.player_teamplayer_no_history), color = Color(0xFF7D8497), fontSize = 12.sp, modifier = Modifier.padding(vertical = 10.dp))
            } else {
                historico.forEach { data ->
                    val membro = data.first
                    val nomeEquipa = data.second
                    val isAtivo = membro.dataSaida == null && membro.estadoConvite == "aceite"
                    val startAno = membro.dataEntrada?.take(4) ?: "..."
                    val endAno = membro.dataSaida?.take(4) ?: stringResource(R.string.player_common_now)

                    Row(
                        modifier = Modifier.fillMaxWidth().height(58.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(modifier = Modifier.weight(1.4f), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(28.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFFEAF0FB)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(nomeEquipa.take(2).uppercase(), color = BrandBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(nomeEquipa, color = BrandBlue, fontSize = 11.sp, lineHeight = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
                            if (isAtivo) {
                                Box(modifier = Modifier.clip(RoundedCornerShape(18.dp)).background(BrandGreen.copy(alpha = 0.12f)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                    Text(stringResource(R.string.player_common_active), color = BrandGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text("$startAno – $endAno", color = Color(0xFF6D7486), fontSize = 9.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}

@Serializable
data class UtilizadorSimplesDTO(val nome: String, @SerialName("foto_url") val fotoUrl: String? = null)

@Serializable
data class EquipaSimplesDTO(val nome: String)

@Serializable
data class EstatisticaSimplesDTO(val pontuacao: Int, val vitorias: Int, @SerialName("id_modalidade") val idModalidade: Int)

@Serializable
data class MembroEquipaSimplesDTO(
    @SerialName("id_equipa") val idEquipa: Long? = null,
    @SerialName("estado_convite") val estadoConvite: String? = null,
    @SerialName("data_entrada") val dataEntrada: String? = null,
    @SerialName("data_saida") val dataSaida: String? = null
)