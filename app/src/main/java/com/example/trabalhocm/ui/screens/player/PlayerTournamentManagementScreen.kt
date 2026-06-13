package com.example.trabalhocm.ui.screens.player

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.remote.SupabaseClient
import com.example.trabalhocm.data.repository.AuthRepository
import com.example.trabalhocm.data.repository.Torneio
import com.example.trabalhocm.data.repository.TorneioRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun PlayerTournamentManagementScreen(
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onDetailsClick: (Long) -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onRegisterClick: (Long) -> Unit = {},
    onAskOrganizerClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onFiltersClick: () -> Unit = {},
) {
    var search by remember { mutableStateOf("") }

    // --- ESTADOS PARA A BASE DE DADOS ---
    val authRepository = remember { AuthRepository() }
    val torneioRepository = remember { TorneioRepository() }

    var listaTorneios by remember { mutableStateOf<List<Torneio>>(emptyList()) }
    var equipasInscritasMap by remember { mutableStateOf<Map<Long, Boolean>>(emptyMap()) }
    var idMinhaEquipa by remember { mutableStateOf<Long?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // 1. Descobre se o user logado é capitão de alguma equipa
        val currentUserId = SupabaseClient.client.auth.currentUserOrNull()?.id
        if (currentUserId != null) {
            try {
                val memberRows = SupabaseClient.client.from("membro_equipa").select {
                    filter { eq("id_utilizador", currentUserId); eq("estado_convite", "aceite") }
                }.decodeList<MembroEquipaSimplesRegDTO>()

                val userMembership = memberRows.firstOrNull()
                if (userMembership?.papel?.lowercase() == "capitao") {
                    idMinhaEquipa = userMembership.idEquipa
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 2. Carrega todos os torneios
        authRepository.obterTorneios().onSuccess { torneios ->
            listaTorneios = torneios

            // 3. Verifica em quais torneios a equipa já está inscrita
            if (idMinhaEquipa != null) {
                val mapInscricoes = mutableMapOf<Long, Boolean>()
                torneios.forEach { torneio ->
                    torneioRepository.verificarEquipaInscrita(torneio.id, idMinhaEquipa!!).onSuccess { isRegistered ->
                        mapInscricoes[torneio.id] = isRegistered
                    }
                }
                equipasInscritasMap = mapInscricoes
            }

            isLoading = false
        }.onFailure {
            isLoading = false
        }
    }

    val updateTrigger = PlayerTournamentFiltersState.updateTrigger
    val selectedSport = PlayerTournamentFiltersState.selectedSport
    val selectedFormat = PlayerTournamentFiltersState.selectedFormat
    val selectedStatus = PlayerTournamentFiltersState.selectedStatus
    val selectedRegion = PlayerTournamentFiltersState.selectedRegion
    val cityOrRegion = PlayerTournamentFiltersState.cityOrRegion
    val fromDate = PlayerTournamentFiltersState.fromDate
    val toDate = PlayerTournamentFiltersState.toDate

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        PlayerTournamentTopBar()

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 20.dp)
        ) {
            Text(
                text = "Tournament\nManagement",
                color = BrandBlue,
                fontSize = 30.sp,
                lineHeight = 33.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Visualize and manage all your active and upcoming\nleagues.",
                color = Color(0xFF6D7486),
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TournamentMainActionButton(
                    modifier = Modifier.weight(1f),
                    text = "HISTORY",
                    icon = "◷",
                    backgroundColor = Color(0xFFF0F2FA),
                    textColor = BrandBlue,
                    onClick = onHistoryClick
                )

                TournamentMainActionButton(
                    modifier = Modifier.weight(1f),
                    text = "ASK TO BE\nORGANIZER",
                    icon = "⊕",
                    backgroundColor = Color(0xFF0757C8),
                    textColor = BrandWhite,
                    onClick = onAskOrganizerClick
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            TournamentSearchAndFilters(
                search = search,
                selectedSport = selectedSport,
                selectedStatus = selectedStatus,
                onSearchChange = { search = it },
                onFiltersClick = onFiltersClick
            )

            Spacer(modifier = Modifier.height(22.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandGreen)
                }
            } else {
                val torneiosFiltrados = listaTorneios.filter { torneio ->
                    val nomeOk = torneio.nome.contains(search, ignoreCase = true)
                    val modalidadeOk = torneioCorrespondeModalidade(selectedSport, torneio.idModalidade)
                    val formatoOk = torneioCorrespondeFormato(selectedFormat, torneio.formato)
                    val estadoOk = torneioCorrespondeEstado(selectedStatus, torneio.estado)
                    val regiaoOk = torneioCorrespondeRegiao(selectedRegion, cityOrRegion, torneio.local)
                    val dataOk = torneioCorrespondeData(torneio.dataInicio, fromDate, toDate)

                    nomeOk && modalidadeOk && formatoOk && estadoOk && regiaoOk && dataOk
                }

                if (torneiosFiltrados.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("No tournaments found.", color = Color.Gray)
                    }
                } else {
                    torneiosFiltrados.forEach { torneio ->

                        val isAlreadyRegistered = equipasInscritasMap[torneio.id] == true
                        val estadoAberto = torneio.estado?.lowercase() == "aberto"

                        val tagEstado = if (estadoAberto) "OPEN" else (torneio.estado?.uppercase() ?: "LIVE")
                        val corEstado = if (estadoAberto) BrandGreen else Color(0xFFE53935)

                        // Lógica do Botão Principal:
                        // Se já está inscrito, mostra "REGISTERED". Se não, depende de estar "aberto"
                        val btnTexto = when {
                            isAlreadyRegistered -> "ALREADY REGISTERED"
                            estadoAberto -> "REGISTER NOW"
                            else -> "CLOSED"
                        }

                        val desativarBotao = !estadoAberto || isAlreadyRegistered

                        val modalidadeNome = when (torneio.idModalidade) {
                            1 -> "FOOTBALL"
                            2 -> "BASKETBALL"
                            3 -> "VOLLEYBALL"
                            else -> "SPORT"
                        }

                        PlayerTournamentCard(
                            status = tagEstado,
                            statusColor = corEstado,
                            tags = listOf(torneio.formato?.uppercase() ?: "LEAGUE", modalidadeNome),
                            infoText = null,
                            title = torneio.nome,
                            date = "Start: ${torneio.dataInicio ?: "TBD"}",
                            teamsText = null,
                            gamesText = null,
                            registeredText = null,
                            progress = null,
                            progressColor = BrandGreen,
                            primaryButtonText = btnTexto,
                            secondaryButtonText = "DETAILS",
                            disabledButton = desativarBotao,
                            onDetailsClick = { onDetailsClick(torneio.id) },
                            onPrimaryClick = {
                                if (!desativarBotao) onRegisterClick(torneio.id)
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(22.dp))
        }

        MatchLeagueBottomBar(
            selectedTab = "TOURNAMENTS",
            onHomeClick = onHomeClick,
            onTournamentsClick = onTournamentsClick,
            onMatchesClick = onMatchesClick,
            onTeamsClick = onTeamsClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun PlayerTournamentTopBar() {
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
            text = "List",
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
fun TournamentMainActionButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(58.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text,
                color = textColor,
                fontSize = 12.sp,
                lineHeight = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun TournamentSearchAndFilters(
    search: String,
    selectedSport: String?,
    selectedStatus: String?,
    onSearchChange: (String) -> Unit,
    onFiltersClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            OutlinedTextField(
                value = search,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Search tournaments...",
                        color = Color(0xFF9EA4B3),
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Text(
                        text = "⌕",
                        color = Color(0xFF8D94A3),
                        fontSize = 18.sp
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                shape = RoundedCornerShape(5.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BrandWhite,
                    unfocusedContainerColor = BrandWhite,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = BrandGreen,
                    focusedTextColor = BrandBlue,
                    unfocusedTextColor = BrandBlue
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterBox(
                    modifier = Modifier.weight(1f),
                    text = "Sport: ${selectedSport ?: "All"}"
                )

                FilterBox(
                    modifier = Modifier.weight(1f),
                    text = "Status: ${selectedStatus ?: "All"}"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onFiltersClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(
                    text = "≡  FILTERS",
                    color = BrandBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FilterBox(
    modifier: Modifier = Modifier,
    text: String
) {
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(Color(0xFFF0F2FA))
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            color = BrandBlue,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun PlayerTournamentCard(
    status: String,
    statusColor: Color,
    tags: List<String>,
    infoText: String?,
    title: String,
    date: String,
    teamsText: String?,
    gamesText: String?,
    registeredText: String? = null,
    progress: Float?,
    progressColor: Color,
    primaryButtonText: String?,
    secondaryButtonText: String,
    disabledButton: Boolean,
    onDetailsClick: () -> Unit,
    onPrimaryClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TournamentBadge(
                    text = status,
                    color = statusColor,
                    strong = true
                )

                Spacer(modifier = Modifier.width(6.dp))

                tags.forEach { tag ->
                    TournamentBadge(
                        text = tag,
                        color = Color(0xFF7D8497),
                        strong = false
                    )

                    Spacer(modifier = Modifier.width(6.dp))
                }
            }

            if (!infoText.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = infoText,
                    color = BrandGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = BrandBlue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = "▣  $date",
                color = Color(0xFF7D8497),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(13.dp))

            if (teamsText != null && gamesText != null) {
                Row {
                    Column {
                        Text(
                            text = "TEAMS",
                            color = Color(0xFF7D8497),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = teamsText,
                            color = BrandBlue,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(24.dp))

                    Column {
                        Text(
                            text = "GAMES TODAY",
                            color = Color(0xFF7D8497),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = gamesText,
                            color = Color(0xFF0757C8),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            if (progress != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "REGISTERED",
                        color = Color(0xFF7D8497),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (!registeredText.isNullOrBlank()) {
                        Text(
                            text = registeredText,
                            color = BrandBlue,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                /*LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    color = progressColor,
                    trackColor = Color(0xFFECEEF7)
                )*/

                Spacer(modifier = Modifier.height(18.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDetailsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(3.dp)
                ) {
                    Text(
                        text = "⊙  $secondaryButtonText",
                        color = Color(0xFF0757C8),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (!primaryButtonText.isNullOrBlank()) {
                    Button(
                        onClick = onPrimaryClick,
                        modifier = Modifier
                            .weight(1.2f)
                            .height(42.dp),
                        enabled = !disabledButton,
                        shape = RoundedCornerShape(3.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (disabledButton) {
                                Color(0xFFDDE1EA)
                            } else {
                                BrandGreen
                            },
                            disabledContainerColor = Color(0xFFDDE1EA),
                            contentColor = BrandWhite,
                            disabledContentColor = Color(0xFF7D8497)
                        )
                    ) {
                        Text(
                            text = if (disabledButton) primaryButtonText else "✓  $primaryButtonText",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TournamentBadge(
    text: String,
    color: Color,
    strong: Boolean
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (strong) color.copy(alpha = 0.12f)
                else Color(0xFFF0F2FA)
            )
            .padding(horizontal = 9.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "● $text",
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

// --- FUNÇÕES DE CORRESPONDÊNCIA SEGURA PARA FILTRAGEM ---
fun torneioCorrespondeModalidade(filtro: String?, idModalidade: Int?): Boolean {
    if (filtro.isNullOrBlank()) return true
    return when (filtro) {
        "Football" -> idModalidade == 1
        "Basketball" -> idModalidade == 2
        "Volleyball" -> idModalidade == 3
        else -> true
    }
}

fun torneioCorrespondeFormato(filtro: String?, formato: String?): Boolean {
    if (filtro.isNullOrBlank()) return true
    val f = formato?.lowercase() ?: ""
    return when (filtro) {
        "League" -> f.contains("liga") || f.contains("league")
        "Knockout" -> f.contains("elimin") || f.contains("knock") || f.contains("mata")
        "Group Stage" -> f.contains("grupo") || f.contains("group")
        else -> true
    }
}

fun torneioCorrespondeEstado(filtro: String?, estado: String?): Boolean {
    if (filtro.isNullOrBlank()) return true
    val e = estado?.lowercase() ?: ""
    return when (filtro) {
        "Upcoming" -> e.contains("breve") || e.contains("pendente") || e.contains("upcoming")
        "Live" -> e.contains("live") || e.contains("curso") || e.contains("decorrer")
        "Registration Open" -> e.contains("aberto") || e.contains("open") || e.contains("inscri")
        "Completed" -> e.contains("terminado") || e.contains("concluido") || e.contains("completed") || e.contains("fechado")
        else -> true
    }
}

fun torneioCorrespondeRegiao(selectedRegion: String?, cityOrRegion: String, local: String?): Boolean {
    val localNormalizado = local.orEmpty().lowercase()
    val regiaoOk = selectedRegion.isNullOrBlank() || localNormalizado.contains(selectedRegion.lowercase())
    val pesquisaRegiaoOk = cityOrRegion.isBlank() || localNormalizado.contains(cityOrRegion.lowercase())
    return regiaoOk && pesquisaRegiaoOk
}

fun torneioCorrespondeData(dataTorneio: String?, fromDate: String, toDate: String): Boolean {
    val dataDaBaseDeDados = dataTorneio?.take(10) ?: return true
    val formatterFiltro = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val dataDoTorneioParsed = runCatching {
        LocalDate.parse(dataDaBaseDeDados)
    }.getOrNull() ?: return true

    val dataInicio = runCatching { LocalDate.parse(fromDate, formatterFiltro) }.getOrNull()
    val dataFim = runCatching { LocalDate.parse(toDate, formatterFiltro) }.getOrNull()

    val depoisDoInicio = dataInicio == null || !dataDoTorneioParsed.isBefore(dataInicio)
    val antesDoFim = dataFim == null || !dataDoTorneioParsed.isAfter(dataFim)

    return depoisDoInicio && antesDoFim
}

@Preview(showBackground = true, name = "Player Tournament Management Screen")
@Composable
fun PlayerTournamentManagementScreenPreview() {
    PlayerTournamentManagementScreen()
}