package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar

// Import das tuas cores centralizadas
import com.example.trabalhocm.ui.theme.*

// Cor extra para o status "Full"
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

    // Estado para controlar se a gaveta dos filtros está aberta
    var showFiltersSheet by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Matches", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Color.White)
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

            // HEADER
            Text("Match Center", color = DarkBlue, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                "Track live games, follow your team and join casual pickup matches.",
                color = TextGray,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // BUTTONS ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCalendarClick,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, InputBg),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = CardBg),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Icon(Icons.Outlined.DateRange, contentDescription = null, tint = DarkBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CALENDAR", color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onHistoryClick,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, InputBg),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = CardBg),
                    modifier = Modifier.weight(1f).height(48.dp)
                ) {
                    Icon(Icons.Outlined.Refresh, contentDescription = null, tint = DarkBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("HISTORY", color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            // SEARCH & FILTERS
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search tournaments...", color = TextGray, fontSize = 14.sp) },
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

                    // BOTÃO QUE ABRE OS FILTROS!
                    OutlinedButton(
                        onClick = { showFiltersSheet = true },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, InputBg),
                        modifier = Modifier.fillMaxWidth().height(40.dp)
                    ) {
                        Icon(Icons.Default.Menu, contentDescription = null, tint = DarkBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("FILTERS", color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            // LIVE MATCH CARD
            LiveMatchCenterCard(onViewDetailsClick = onLiveMatchClick)

            // PICKUP MATCHES
            PickupMatchCard(
                status = "OPEN",
                statusColor = TealGreen,
                sport = "VOLLEYBALL",
                title = "Beach Volley Mix",
                timeLocation = "Tonight 19:30 • Riverside Courts",
                playersJoined = 8,
                maxPlayers = 10,
                onViewDetailsClick = onCasualMatchClick,
                onActionClick = { },
                actionText = "JOIN MATCH",
                actionEnabled = true,
                isInviteOnly = false
            )

            PickupMatchCard(
                status = "FULL",
                statusColor = WarningYellow,
                sport = "FOOTBALL",
                title = "Friday Night Football",
                timeLocation = "Tomorrow 20:00 • Sports Pavilion",
                playersJoined = 22,
                maxPlayers = 22,
                onViewDetailsClick = { },
                onActionClick = { },
                actionText = "JOIN WAITING LIST",
                actionEnabled = false,
                isInviteOnly = false
            )

            PickupMatchCard(
                status = "INVITE ONLY",
                statusColor = PrimaryBlue,
                sport = "BASKETBALL",
                title = "Elite 5v5 Pickup",
                timeLocation = "Saturday 10:00 • Court 3",
                playersJoined = 10,
                maxPlayers = 10,
                onViewDetailsClick = { },
                onActionClick = { },
                actionText = "INVITE ONLY — LOCKED",
                actionEnabled = false,
                isInviteOnly = true
            )

            // HOST A MATCH CARD
            HostMatchCard(onCreateClick = onCreateCasualMatchClick)

            Spacer(modifier = Modifier.height(32.dp))
        }

        // BÓIA DE SALVAÇÃO: O BOTTOM SHEET DOS FILTROS!
        if (showFiltersSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFiltersSheet = false },
                containerColor = BgLight,
                dragHandle = null, // Retiramos o drag handle padrão para fazer o cabeçalho azul do Figma
                modifier = Modifier.fillMaxHeight(0.9f) // Ocupa 90% do ecrã
            ) {
                FiltersBottomSheetContent(onCloseClick = { showFiltersSheet = false })
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENTES DOS FILTROS (BOTTOM SHEET)
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FiltersBottomSheetContent(onCloseClick: () -> Unit) {
    var feeRange by remember { mutableStateOf(10f..70f) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Cabeçalho Azul Escuro
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
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.clickable { onCloseClick() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Filters", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                "RESET",
                color = TealGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { /* Reset states */ }
            )
        }

        // Conteúdo Scrollable
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Sport Category
            Column {
                FilterSectionLabel("Sport Category")
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChipCustom("⚽ Football", isSelected = true)
                    FilterChipCustom("🏐 Volleyball", isSelected = false)
                    FilterChipCustom("🏀 Basketball", isSelected = false)
                }
            }

            // Competition Format
            Column {
                FilterSectionLabel("Competition Format")
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChipCustom("League", isSelected = true)
                    FilterChipCustom("Knockout", isSelected = true)
                    FilterChipCustom("Group Stage", isSelected = false)
                }
            }

            // Status
            Column {
                FilterSectionLabel("Status")
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChipCustom("Upcoming", isSelected = false)

                    // O Live tem cores especiais no Figma!
                    Surface(
                        color = Color(0xFFD1FAE5), // Verde clarinho
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, TealGreen)
                    ) {
                        Text("Live", color = TealGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                    }

                    FilterChipCustom("Registration Open", isSelected = true)
                    FilterChipCustom("Completed", isSelected = false)
                }
            }

            // Region
            Column {
                FilterSectionLabel("Region")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = "",
                    onValueChange = { },
                    placeholder = { Text("City or region...", color = TextGray) },
                    leadingIcon = { Icon(Icons.Outlined.Place, contentDescription = null, tint = TextGray) },
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CardBg, unfocusedContainerColor = CardBg,
                        focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChipCustom("📍 Lisbon", isSelected = true)
                    FilterChipCustom("Porto", isSelected = false)
                    FilterChipCustom("Coimbra", isSelected = false)
                    FilterChipCustom("Braga", isSelected = false)
                }
            }

            // Date Range
            Column {
                FilterSectionLabel("Date Range")
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("FROM", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        TextField(
                            value = "01/06/2026", onValueChange = {},
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = CardBg, unfocusedContainerColor = CardBg,
                                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("TO", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        TextField(
                            value = "30/09/2026", onValueChange = {},
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = CardBg, unfocusedContainerColor = CardBg,
                                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    }
                }
            }

            // Entry Fee Range
            Column {
                FilterSectionLabel("Entry Fee Range")
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
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("€10", color = TextGray, fontSize = 12.sp)
                    Text("€${feeRange.start.toInt()} — €${feeRange.endInclusive.toInt()}", color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("€100+", color = TextGray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botão Aplicar
            Button(
                onClick = onCloseClick,
                colors = ButtonDefaults.buttonColors(containerColor = TealGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("APPLY FILTERS (24)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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
        modifier = Modifier.clickable { /* Lógica de selecionar/deselecionar */ }
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

// -------------------------------------------------------------
// COMPONENTES DOS CARTÕES (MANTER OS MESMOS)
// -------------------------------------------------------------
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
                    drawLine(color = TealGreen, start = Offset(0f, 0f), end = Offset(0f, size.height), strokeWidth = 16f)
                }
                .padding(16.dp)
                .padding(start = 8.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TagBadge("• LIVE NOW", TealGreen, TealGreen.copy(alpha = 0.1f))
                TagBadge("CASUAL", TextGray, InputBg)
                TagBadge("FOOTBALL", TextGray, InputBg)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                        Text("SLB", color = ErrorRed, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Benfica", color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("2 - 1", color = DarkBlue, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Text("75'", color = ErrorRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.size(48.dp).clip(CircleShape).background(Color.White), contentAlignment = Alignment.Center) {
                        Text("FCP", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Porto", color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Place, contentDescription = null, tint = TextGray, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Estádio da Luz - Atlantic Cup 2026", color = TextGray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onViewDetailsClick,
                    shape = RoundedCornerShape(8.dp), border = BorderStroke(1.dp, PrimaryBlue),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) { Text("VIEW DETAILS", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                Button(
                    onClick = onViewDetailsClick,
                    colors = ButtonDefaults.buttonColors(containerColor = TealGreen), shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) { Text("WATCH LIVE", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
            }
        }
    }
}

@Composable
fun PickupMatchCard(
    status: String, statusColor: Color, sport: String, title: String, timeLocation: String,
    playersJoined: Int, maxPlayers: Int, onViewDetailsClick: () -> Unit, onActionClick: () -> Unit,
    actionText: String, actionEnabled: Boolean, isInviteOnly: Boolean
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TagBadge("• $status", statusColor, statusColor.copy(alpha = 0.1f))
                TagBadge("CASUAL", TextGray, InputBg)
                TagBadge(sport, TextGray, InputBg)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(title, color = DarkBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Info, contentDescription = null, tint = TextGray, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(timeLocation, color = TextGray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (!isInviteOnly) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("PLAYERS JOINED", color = DarkBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Text("$playersJoined/$maxPlayers", color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { playersJoined.toFloat() / maxPlayers.toFloat() },
                    modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = statusColor, trackColor = InputBg,
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onViewDetailsClick, shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, PrimaryBlue), modifier = Modifier.weight(1f).height(40.dp)
                ) { Text("VIEW DETAILS", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp) }

                Button(
                    onClick = onActionClick, enabled = actionEnabled,
                    colors = ButtonDefaults.buttonColors(containerColor = TealGreen, disabledContainerColor = Color(0xFFCBD5E1)),
                    shape = RoundedCornerShape(8.dp), modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    if (isInviteOnly) {
                        Icon(Icons.Outlined.Lock, contentDescription = null, tint = TextGray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(actionText, color = TextGray, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    } else {
                        Text(actionText, color = if (actionEnabled) Color.White else TextGray, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun HostMatchCard(onCreateClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .drawBehind {
                val stroke = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f))
                drawRoundRect(color = Color(0xFFCBD5E1), style = stroke, cornerRadius = CornerRadius(12.dp.toPx()))
            }
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(shape = CircleShape, border = BorderStroke(2.dp, DarkBlue), color = Color.Transparent, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Add, contentDescription = null, tint = DarkBlue, modifier = Modifier.padding(4.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Host a Match?", color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Can't find what you're looking for? Create your own casual match.", color = TextGray, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onCreateClick, colors = ButtonDefaults.buttonColors(containerColor = DarkBlue), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth(0.9f).height(48.dp)) {
                Text("CREATE CASUAL MATCH", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
            }
        }
    }
}

@Composable
fun TagBadge(text: String, textColor: Color, bgColor: Color) {
    Surface(color = bgColor, shape = RoundedCornerShape(12.dp)) {
        Text(text, color = textColor, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun OrganizerMatchCenterScreenPreview() {
    MaterialTheme {
        OrganizerMatchCenterScreen()
    }
}