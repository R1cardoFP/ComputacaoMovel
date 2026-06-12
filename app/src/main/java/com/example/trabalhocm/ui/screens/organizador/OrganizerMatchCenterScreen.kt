package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.repository.PeladinhaComInfo
import com.example.trabalhocm.data.repository.PeladinhaRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.*
import kotlinx.coroutines.launch

private val WarningYellow = Color(0xFFF59E0B)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OrganizerMatchCenterScreen(
    onLiveMatchClick: () -> Unit = {},
    onCasualMatchClick: () -> Unit = {},
    onCalendarClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onCreateCasualMatchClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var showFiltersSheet by remember { mutableStateOf(false) }

    val repository = remember { PeladinhaRepository() }
    val scope = rememberCoroutineScope()

    var peladinhas by remember { mutableStateOf<List<PeladinhaComInfo>>(emptyList()) }
    var isLoadingPeladinhas by remember { mutableStateOf(true) }
    var errorPeladinhas by remember { mutableStateOf("") }

    val errorLoadCasualMatches = stringResource(R.string.error_load_casual_matches)

    fun carregarPeladinhas() {
        scope.launch {
            isLoadingPeladinhas = true
            errorPeladinhas = ""

            repository.listarPeladinhasComInfo()
                .onSuccess { lista ->
                    peladinhas = lista.sortedByDescending { it.peladinha.id }
                }
                .onFailure { erro ->
                    errorPeladinhas = erro.message ?: errorLoadCasualMatches
                }

            isLoadingPeladinhas = false
        }
    }

    LaunchedEffect(Unit) {
        carregarPeladinhas()
    }

    val peladinhasFiltradas = peladinhas.filter { info ->
        val query = searchQuery.trim()

        query.isBlank() ||
                info.modalidadeNome.contains(query, ignoreCase = true) ||
                info.peladinha.local.orEmpty().contains(query, ignoreCase = true) ||
                info.peladinha.descricao.orEmpty().contains(query, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_matches), color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { carregarPeladinhas() }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = stringResource(R.string.desc_refresh), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        bottomBar = {
            MatchLeagueBottomBar(
                selectedTab = "MATCHES",
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
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(stringResource(R.string.title_match_center), color = DarkBlue, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                stringResource(R.string.desc_match_center),
                color = TextGray,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCalendarClick,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, InputBg),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = CardBg),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(
                        Icons.Outlined.DateRange,
                        contentDescription = null,
                        tint = DarkBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.btn_calendar), color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onHistoryClick,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, InputBg),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = CardBg),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                ) {
                    Icon(
                        Icons.Outlined.Refresh,
                        contentDescription = null,
                        tint = DarkBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.btn_history), color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = TextGray,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(stringResource(R.string.placeholder_search_matches), color = TextGray, fontSize = 14.sp)
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    HorizontalDivider(color = InputBg)

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = { showFiltersSheet = true },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, InputBg),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = null,
                            tint = DarkBlue,
                            modifier = Modifier.size(16.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(stringResource(R.string.btn_filters), color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            LiveMatchCenterCard(onViewDetailsClick = onLiveMatchClick)

            PickupMatchesFromDatabaseSection(
                peladinhas = peladinhasFiltradas,
                isLoading = isLoadingPeladinhas,
                errorMessage = errorPeladinhas,
                onCasualMatchClick = onCasualMatchClick
            )

            HostMatchCard(onCreateClick = onCreateCasualMatchClick)

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (showFiltersSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFiltersSheet = false },
                containerColor = BgLight,
                dragHandle = null,
                modifier = Modifier.fillMaxHeight(0.9f)
            ) {
                FiltersBottomSheetContent(onCloseClick = { showFiltersSheet = false })
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltersBottomSheetContent(onCloseClick: () -> Unit) {
    var feeRange by remember { mutableStateOf(10f..70f) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkBlue)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = stringResource(R.string.desc_close),
                    tint = Color.White,
                    modifier = Modifier.clickable { onCloseClick() }
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(stringResource(R.string.title_filters_sheet), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            Text(
                stringResource(R.string.btn_reset),
                color = TealGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column {
                FilterSectionLabel(stringResource(R.string.label_sport_category_filters))

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChipCustom(stringResource(R.string.filter_football), isSelected = true)
                    FilterChipCustom(stringResource(R.string.filter_volleyball), isSelected = false)
                    FilterChipCustom(stringResource(R.string.filter_basketball), isSelected = false)
                }
            }

            Column {
                FilterSectionLabel(stringResource(R.string.label_competition_format_filters))

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChipCustom(stringResource(R.string.filter_league), isSelected = true)
                    FilterChipCustom(stringResource(R.string.filter_knockout), isSelected = true)
                    FilterChipCustom(stringResource(R.string.filter_group_stage), isSelected = false)
                }
            }

            Column {
                FilterSectionLabel(stringResource(R.string.label_status_filters))

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChipCustom(stringResource(R.string.filter_upcoming), isSelected = false)

                    Surface(
                        color = Color(0xFFD1FAE5),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, TealGreen)
                    ) {
                        Text(
                            stringResource(R.string.filter_live),
                            color = TealGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    FilterChipCustom(stringResource(R.string.filter_registration_open), isSelected = true)
                    FilterChipCustom(stringResource(R.string.filter_completed), isSelected = false)
                }
            }

            Column {
                FilterSectionLabel(stringResource(R.string.label_region_filters))

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = "",
                    onValueChange = { },
                    placeholder = { Text(stringResource(R.string.placeholder_city_region), color = TextGray) },
                    leadingIcon = {
                        Icon(
                            Icons.Outlined.Place,
                            contentDescription = null,
                            tint = TextGray
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CardBg,
                        unfocusedContainerColor = CardBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChipCustom(stringResource(R.string.filter_lisbon), isSelected = true)
                    FilterChipCustom(stringResource(R.string.filter_porto), isSelected = false)
                    FilterChipCustom(stringResource(R.string.filter_coimbra), isSelected = false)
                    FilterChipCustom(stringResource(R.string.filter_braga), isSelected = false)
                }
            }

            Column {
                FilterSectionLabel(stringResource(R.string.label_date_range_filters))

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.label_from), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(4.dp))

                        TextField(
                            value = "01/06/2026",
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = CardBg,
                                unfocusedContainerColor = CardBg,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.label_to), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(4.dp))

                        TextField(
                            value = "30/09/2026",
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = CardBg,
                                unfocusedContainerColor = CardBg,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }

            Column {
                FilterSectionLabel(stringResource(R.string.label_entry_fee_range))

                Spacer(modifier = Modifier.height(8.dp))

                RangeSlider(
                    value = feeRange,
                    onValueChange = { feeRange = it },
                    valueRange = 10f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = PrimaryBlue,
                        inactiveTrackColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("€10", color = TextGray, fontSize = 12.sp)
                    Text(
                        "€${feeRange.start.toInt()} — €${feeRange.endInclusive.toInt()}",
                        color = DarkBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("€100+", color = TextGray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onCloseClick,
                colors = ButtonDefaults.buttonColors(containerColor = TealGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(stringResource(R.string.btn_apply_filters), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun FilterChipCustom(text: String, isSelected: Boolean) {
    val bgColor = if (isSelected) PrimaryBlue else CardBg
    val textColor = if (isSelected) Color.White else TextGray
    val borderColor = if (isSelected) PrimaryBlue else Color(0xFFE2E8F0)

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier.clickable { }
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun FilterSectionLabel(text: String) {
    Text(
        text = text,
        color = DarkBlue,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun LiveMatchCenterCard(onViewDetailsClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawLine(
                        color = TealGreen,
                        start = Offset(0f, 0f),
                        end = Offset(0f, size.height),
                        strokeWidth = 16f
                    )
                }
                .padding(16.dp)
                .padding(start = 8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TagBadge(stringResource(R.string.badge_live_now_bullet), TealGreen, TealGreen.copy(alpha = 0.1f))
                TagBadge(stringResource(R.string.badge_casual), TextGray, InputBg)
                TagBadge(stringResource(R.string.badge_football), TextGray, InputBg)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.team_slb), color = ErrorRed, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(stringResource(R.string.team_benfica), color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("2 - 1", color = DarkBlue, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Text("75'", color = ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(stringResource(R.string.team_fcp), color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(stringResource(R.string.team_porto), color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.Place,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(stringResource(R.string.mock_stadium_atlantic_cup), color = TextGray, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onViewDetailsClick,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, PrimaryBlue),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                ) {
                    Text(stringResource(R.string.btn_view_details), color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = onViewDetailsClick,
                    colors = ButtonDefaults.buttonColors(containerColor = TealGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                ) {
                    Text(stringResource(R.string.btn_watch_live), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun PickupMatchesFromDatabaseSection(
    peladinhas: List<PeladinhaComInfo>,
    isLoading: Boolean,
    errorMessage: String,
    onCasualMatchClick: () -> Unit
) {
    val suffixCasualMatch = stringResource(R.string.suffix_casual_match)
    val dateTbd = stringResource(R.string.date_tbd)
    val locTbd = stringResource(R.string.loc_tbd)

    when {
        isLoading -> {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TealGreen)
                }
            }
        }

        errorMessage.isNotBlank() -> {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMessage,
                    color = ErrorRed,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        peladinhas.isEmpty() -> {
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.msg_no_casual_matches),
                    color = TextGray,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        else -> {
            peladinhas.forEach { info ->
                val status = obterStatusPeladinha(info)
                val statusColor = obterCorStatusPeladinha(status)
                val inviteOnly = info.peladinha.descricao
                    .orEmpty()
                    .contains("Invite Only", ignoreCase = true)

                val maxJogadores = info.peladinha.maxJogadores.coerceAtLeast(1)
                val actionEnabled = !inviteOnly && status != "FULL"

                val btnInviteOnlyLocked = stringResource(R.string.btn_invite_only_locked)
                val btnFull = stringResource(R.string.btn_full)
                val btnViewDetails = stringResource(R.string.btn_view_details)

                val statusTranslated = when (status) {
                    "OPEN" -> stringResource(R.string.status_open)
                    "LIVE NOW" -> stringResource(R.string.status_live_now)
                    "FULL" -> stringResource(R.string.status_full)
                    else -> status.uppercase()
                }

                PickupMatchCard(
                    status = statusTranslated,
                    statusColor = statusColor,
                    sport = info.modalidadeNome.uppercase(),
                    title = obterTituloPeladinha(info, suffixCasualMatch),
                    timeLocation = obterDataHoraLocalPeladinha(info, dateTbd, locTbd),
                    playersJoined = info.jogadoresInscritos,
                    maxPlayers = maxJogadores,
                    onViewDetailsClick = onCasualMatchClick,
                    onActionClick = onCasualMatchClick,
                    actionText = when {
                        inviteOnly -> btnInviteOnlyLocked
                        status == "FULL" -> btnFull
                        else -> btnViewDetails
                    },
                    actionEnabled = actionEnabled,
                    isInviteOnly = inviteOnly
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun PickupMatchCard(
    status: String,
    statusColor: Color,
    sport: String,
    title: String,
    timeLocation: String,
    playersJoined: Int,
    maxPlayers: Int,
    onViewDetailsClick: () -> Unit,
    onActionClick: () -> Unit,
    actionText: String,
    actionEnabled: Boolean,
    isInviteOnly: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TagBadge("• $status", statusColor, statusColor.copy(alpha = 0.1f))
                TagBadge(stringResource(R.string.badge_casual), TextGray, InputBg)
                TagBadge(sport, TextGray, InputBg)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(title, color = DarkBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.Info,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(timeLocation, color = TextGray, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (!isInviteOnly) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(R.string.label_players_joined),
                        color = DarkBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Text(
                        "$playersJoined/$maxPlayers",
                        color = DarkBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { playersJoined.toFloat() / maxPlayers.toFloat() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = statusColor,
                    trackColor = InputBg,
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onViewDetailsClick,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, PrimaryBlue),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                ) {
                    Text(stringResource(R.string.btn_view_details), color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Button(
                    onClick = onActionClick,
                    enabled = actionEnabled,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealGreen,
                        disabledContainerColor = Color(0xFFCBD5E1)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                ) {
                    if (isInviteOnly) {
                        Icon(
                            Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = TextGray,
                            modifier = Modifier.size(14.dp)
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(actionText, color = TextGray, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    } else {
                        Text(
                            actionText,
                            color = if (actionEnabled) Color.White else TextGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HostMatchCard(onCreateClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                val stroke = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                )
                drawRoundRect(
                    color = Color(0xFFCBD5E1),
                    style = stroke,
                    cornerRadius = CornerRadius(12.dp.toPx())
                )
            }
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = CircleShape,
                border = BorderStroke(2.dp, DarkBlue),
                color = Color.Transparent,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = DarkBlue,
                    modifier = Modifier.padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(stringResource(R.string.title_host_match), color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                stringResource(R.string.desc_host_match),
                color = TextGray,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCreateClick,
                colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(48.dp)
            ) {
                Text(
                    stringResource(R.string.btn_create_casual_match),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun TagBadge(text: String, textColor: Color, bgColor: Color) {
    Surface(color = bgColor, shape = RoundedCornerShape(12.dp)) {
        Text(
            text = text,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

fun obterStatusPeladinha(info: PeladinhaComInfo): String {
    val estado = info.peladinha.estado.lowercase()
    val maxJogadores = info.peladinha.maxJogadores

    if (maxJogadores > 0 && info.jogadoresInscritos >= maxJogadores) {
        return "FULL"
    }

    return when (estado) {
        "aberta" -> "OPEN"
        "em_direto", "live" -> "LIVE NOW"
        "fechada" -> "FULL"
        else -> estado.uppercase()
    }
}

fun obterCorStatusPeladinha(status: String): Color {
    return when (status) {
        "OPEN" -> TealGreen
        "LIVE NOW" -> ErrorRed
        "FULL" -> WarningYellow
        else -> PrimaryBlue
    }
}

fun obterTituloPeladinha(info: PeladinhaComInfo, defaultSuffix: String): String {
    val descricao = info.peladinha.descricao.orEmpty().trim()

    return if (descricao.isNotBlank() && !descricao.startsWith("Nível:", ignoreCase = true)) {
        descricao
    } else {
        "${info.modalidadeNome} $defaultSuffix"
    }
}

fun obterDataHoraLocalPeladinha(info: PeladinhaComInfo, dateTbd: String, locTbd: String): String {
    val data = formatarDataPeladinha(info.peladinha.data, dateTbd)
    val hora = formatarHoraPeladinha(info.peladinha.hora)
    val local = info.peladinha.local ?: locTbd

    return "$data $hora • $local"
}

fun formatarDataPeladinha(data: String?, dateTbd: String): String {
    if (data.isNullOrBlank()) {
        return dateTbd
    }

    val partes = data.take(10).split("-")

    return if (partes.size == 3) {
        "${partes[2]}/${partes[1]}/${partes[0]}"
    } else {
        data
    }
}

fun formatarHoraPeladinha(hora: String?): String {
    if (hora.isNullOrBlank()) {
        return "--:--"
    }

    return hora.take(5)
}

@Preview(showBackground = true)
@Composable
fun OrganizerMatchCenterScreenPreview() {
    MaterialTheme {
        OrganizerMatchCenterScreen()
    }
}