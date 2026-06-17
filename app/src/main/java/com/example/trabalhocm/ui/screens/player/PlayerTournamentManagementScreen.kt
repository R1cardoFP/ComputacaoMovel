package com.example.trabalhocm.ui.screens.player

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val TournamentManagementBg = Color(0xFFF4F6FA)
private val TournamentManagementCard = Color.White
private val TournamentManagementInputBg = Color(0xFFF1F4F8)
private val TournamentManagementTextGray = Color(0xFF596579)
private val TournamentManagementTextLight = Color(0xFF8A94A6)
private val TournamentManagementAccentBlue = Color(0xFF0757C8)
private val TournamentManagementDanger = Color(0xFFE53935)
private val TournamentManagementDisabled = Color(0xFFDDE1EA)

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

    val torneiosFiltrados = listaTorneios.filter { torneio ->
        val nomeOk = torneio.nome.contains(search, ignoreCase = true)
        val modalidadeOk = torneioCorrespondeModalidade(selectedSport, torneio.idModalidade)
        val formatoOk = torneioCorrespondeFormato(selectedFormat, torneio.formato)
        val estadoOk = torneioCorrespondeEstado(selectedStatus, torneio.estado)
        val regiaoOk = torneioCorrespondeRegiao(selectedRegion, cityOrRegion, torneio.local)
        val dataOk = torneioCorrespondeData(torneio.dataInicio, fromDate, toDate)

        nomeOk && modalidadeOk && formatoOk && estadoOk && regiaoOk && dataOk
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TournamentManagementBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        PlayerTournamentTopBar(
            onNotificationsClick = onNotificationsClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            PlayerTournamentManagementHeroCard(
                totalTournaments = listaTorneios.size,
                visibleTournaments = torneiosFiltrados.size,
                registeredCount = equipasInscritasMap.count { it.value },
                isCaptain = idMinhaEquipa != null
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TournamentMainActionButton(
                    modifier = Modifier.weight(1f),
                    text = "HISTORY",
                    icon = "◷",
                    backgroundColor = TournamentManagementCard,
                    textColor = BrandBlue,
                    subText = "Past tournaments",
                    onClick = onHistoryClick
                )

                TournamentMainActionButton(
                    modifier = Modifier.weight(1f),
                    text = "ASK TO BE ORGANIZER",
                    icon = "+",
                    backgroundColor = BrandBlue,
                    textColor = BrandWhite,
                    subText = "Create your events",
                    onClick = onAskOrganizerClick
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TournamentSearchAndFilters(
                search = search,
                selectedSport = selectedSport,
                selectedStatus = selectedStatus,
                selectedFormat = selectedFormat,
                selectedRegion = selectedRegion,
                onSearchChange = { search = it },
                onFiltersClick = onFiltersClick
            )

            Spacer(modifier = Modifier.height(18.dp))

            when {
                isLoading -> {
                    TournamentManagementLoadingCard()
                }

                torneiosFiltrados.isEmpty() -> {
                    TournamentManagementMessageCard(
                        title = "No tournaments found",
                        text = "Try changing the search or opening the filters to broaden the results.",
                        color = TournamentManagementTextGray
                    )
                }

                else -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Available tournaments",
                                color = BrandBlue,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = "${torneiosFiltrados.size} result${if (torneiosFiltrados.size == 1) "" else "s"} found",
                                color = TournamentManagementTextLight,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    torneiosFiltrados.forEach { torneio ->

                        val isAlreadyRegistered = equipasInscritasMap[torneio.id] == true
                        // Correção da leitura do estado
                        val estadoAberto = torneio.estado?.trim()?.equals("aberto", ignoreCase = true) == true

                        val tagEstado = if (estadoAberto) "OPEN" else (torneio.estado?.uppercase() ?: "LIVE")
                        val corEstado = if (estadoAberto) BrandGreen else TournamentManagementDanger

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
                            infoText = if (isAlreadyRegistered) "YOUR TEAM IS REGISTERED" else null,
                            title = torneio.nome,
                            date = "Start: ${torneio.dataInicio ?: "TBD"}",
                            location = torneio.local,
                            prize = torneio.premio,
                            primaryButtonText = btnTexto,
                            secondaryButtonText = "DETAILS",
                            disabledButton = desativarBotao,
                            onDetailsClick = { onDetailsClick(torneio.id) },
                            onPrimaryClick = {
                                if (!desativarBotao) onRegisterClick(torneio.id)
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
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
fun PlayerTournamentTopBar(
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(BrandBlue)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Tournaments",
                color = BrandWhite,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic
            )

            Text(
                text = "Discover and manage competitions",
                color = BrandWhite.copy(alpha = 0.78f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f))
                .clickable { onNotificationsClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = BrandWhite,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun PlayerTournamentManagementHeroCard(
    totalTournaments: Int,
    visibleTournaments: Int,
    registeredCount: Int,
    isCaptain: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TOURNAMENT HUB",
                        color = BrandGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Tournament\nManagement",
                        color = BrandWhite,
                        fontSize = 28.sp,
                        lineHeight = 31.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏆",
                        fontSize = 25.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isCaptain) {
                    "Browse tournaments, check your registrations and register your team in open competitions."
                } else {
                    "Browse tournaments and ask to become an organizer or captain to unlock registrations."
                },
                color = BrandWhite.copy(alpha = 0.82f),
                fontSize = 13.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TournamentHeroMetric(
                    modifier = Modifier.weight(1f),
                    label = "TOTAL",
                    value = totalTournaments.toString()
                )

                TournamentHeroMetric(
                    modifier = Modifier.weight(1f),
                    label = "VISIBLE",
                    value = visibleTournaments.toString()
                )

                TournamentHeroMetric(
                    modifier = Modifier.weight(1f),
                    label = "REGISTERED",
                    value = registeredCount.toString()
                )
            }
        }
    }
}

@Composable
private fun TournamentHeroMetric(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.11f))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = BrandWhite,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            color = BrandWhite.copy(alpha = 0.70f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            maxLines = 1
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
    subText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(86.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(
                        if (backgroundColor == BrandBlue) Color.White.copy(alpha = 0.14f)
                        else TournamentManagementInputBg
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = icon,
                    color = textColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = text,
                    color = textColor,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = subText,
                    color = textColor.copy(alpha = 0.72f),
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun TournamentSearchAndFilters(
    search: String,
    selectedSport: String?,
    selectedStatus: String?,
    selectedFormat: String?,
    selectedRegion: String?,
    onSearchChange: (String) -> Unit,
    onFiltersClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = TournamentManagementCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Search and filters",
                        color = BrandBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Find competitions that match your team",
                        color = TournamentManagementTextLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(TournamentManagementInputBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⌕",
                        color = TournamentManagementAccentBlue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = search,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Search tournaments...",
                        color = TournamentManagementTextLight,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Text(
                        text = "⌕",
                        color = TournamentManagementTextLight,
                        fontSize = 18.sp
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = TournamentManagementInputBg,
                    unfocusedContainerColor = TournamentManagementInputBg,
                    focusedBorderColor = BrandGreen,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = BrandGreen,
                    focusedTextColor = BrandBlue,
                    unfocusedTextColor = BrandBlue
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterBox(
                    modifier = Modifier.weight(1f),
                    label = "Sport",
                    text = selectedSport ?: "All"
                )

                FilterBox(
                    modifier = Modifier.weight(1f),
                    label = "Status",
                    text = selectedStatus ?: "All"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterBox(
                    modifier = Modifier.weight(1f),
                    label = "Format",
                    text = selectedFormat ?: "All"
                )

                FilterBox(
                    modifier = Modifier.weight(1f),
                    label = "Region",
                    text = selectedRegion ?: "All"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onFiltersClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, TournamentManagementInputBg),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = TournamentManagementCard
                )
            ) {
                Text(
                    text = "≡  FILTERS",
                    color = BrandBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.7.sp
                )
            }
        }
    }
}

@Composable
fun FilterBox(
    modifier: Modifier = Modifier,
    label: String,
    text: String
) {
    Column(
        modifier = modifier
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(TournamentManagementInputBg)
            .padding(horizontal = 14.dp, vertical = 9.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label.uppercase(),
            color = TournamentManagementTextLight,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            maxLines = 1
        )

        Text(
            text = text,
            color = BrandBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
    location: String?,
    prize: Double?,
    primaryButtonText: String?,
    secondaryButtonText: String,
    disabledButton: Boolean,
    onDetailsClick: () -> Unit,
    onPrimaryClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = TournamentManagementCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(statusColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title.take(1).uppercase(),
                        color = statusColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TournamentBadge(
                            text = status,
                            color = statusColor,
                            strong = true
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        tags.take(1).forEach { tag ->
                            TournamentBadge(
                                text = tag,
                                color = TournamentManagementTextGray,
                                strong = false
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = title,
                        color = BrandBlue,
                        fontSize = 18.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (!infoText.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(BrandGreen.copy(alpha = 0.11f))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = infoText,
                        color = BrandGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.7.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(TournamentManagementInputBg)
                    .padding(14.dp)
            ) {
                TournamentInfoRow(
                    label = "DATE",
                    value = date,
                    icon = "▣"
                )

                if (!location.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))

                    TournamentInfoRow(
                        label = "LOCATION",
                        value = location,
                        icon = "⌖"
                    )
                }

                if (prize != null && prize > 0.0) {
                    Spacer(modifier = Modifier.height(10.dp))

                    TournamentInfoRow(
                        label = "PRIZE",
                        value = "€$prize",
                        icon = "★"
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    tags.drop(1).forEach { tag ->
                        TournamentSmallBadge(
                            text = tag,
                            color = Color.White,
                            textColor = TournamentManagementTextGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDetailsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, TournamentManagementInputBg),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = TournamentManagementCard
                    )
                ) {
                    Text(
                        text = "⊙  $secondaryButtonText",
                        color = TournamentManagementAccentBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (!primaryButtonText.isNullOrBlank()) {
                    Button(
                        onClick = onPrimaryClick,
                        modifier = Modifier
                            .weight(1.25f)
                            .height(48.dp),
                        enabled = !disabledButton,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandGreen,
                            disabledContainerColor = TournamentManagementDisabled,
                            contentColor = BrandWhite,
                            disabledContentColor = TournamentManagementTextGray
                        )
                    ) {
                        Text(
                            text = if (disabledButton) primaryButtonText else "✓  $primaryButtonText",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            lineHeight = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TournamentInfoRow(
    label: String,
    value: String,
    icon: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                color = TournamentManagementAccentBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = label,
                color = TournamentManagementTextLight,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Text(
                text = value,
                color = BrandBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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
                else TournamentManagementInputBg
            )
            .padding(horizontal = 9.dp, vertical = 5.dp),
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

@Composable
private fun TournamentSmallBadge(
    text: String,
    color: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .padding(horizontal = 9.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TournamentManagementLoadingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = TournamentManagementCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 42.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = BrandGreen)
        }
    }
}

@Composable
private fun TournamentManagementMessageCard(
    title: String,
    text: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = TournamentManagementCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(TournamentManagementInputBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⌕",
                    color = TournamentManagementTextLight,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                color = BrandBlue,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = text,
                color = color,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

// --- FUNÇÕES DE FILTRAGEM CORRIGIDAS ---
fun torneioCorrespondeModalidade(filtro: String?, idModalidade: Int?): Boolean {
    if (filtro.isNullOrBlank() || filtro.equals("All", ignoreCase = true)) return true
    return when (filtro) {
        "Football" -> idModalidade == 1
        "Basketball" -> idModalidade == 2
        "Volleyball" -> idModalidade == 3
        else -> true
    }
}

fun torneioCorrespondeFormato(filtro: String?, formato: String?): Boolean {
    if (filtro.isNullOrBlank() || filtro.equals("All", ignoreCase = true)) return true
    val f = formato?.lowercase() ?: ""
    return when (filtro) {
        "League" -> f.contains("liga") || f.contains("league")
        "Knockout" -> f.contains("elimin") || f.contains("knock") || f.contains("mata")
        "Group Stage" -> f.contains("grupo") || f.contains("group")
        else -> true
    }
}

fun torneioCorrespondeEstado(filtro: String?, estado: String?): Boolean {
    if (filtro.isNullOrBlank() || filtro.equals("All", ignoreCase = true)) return true
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
    val regiaoOk = selectedRegion.isNullOrBlank() || selectedRegion.equals("All", ignoreCase = true) || localNormalizado.contains(selectedRegion.lowercase())
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