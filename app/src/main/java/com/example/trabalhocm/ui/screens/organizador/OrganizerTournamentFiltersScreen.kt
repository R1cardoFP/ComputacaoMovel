package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val DarkBlue = Color(0xFF0B1F3A)
private val PrimaryBlue = Color(0xFF2563EB)
private val TealGreen = Color(0xFF059669)
private val TextGray = Color(0xFF64748B)
private val BgLight = Color(0xFFF8FAFC)
private val CardBg = Color(0xFFFFFFFF)
private val InputBg = Color(0xFFF1F5F9)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OrganizerTournamentFiltersScreen(
    onCloseClick: () -> Unit = {},
    onApplyClick: () -> Unit = {}
) {
    var fromDate by remember { mutableStateOf("01/06/2026") }
    var toDate by remember { mutableStateOf("30/09/2026") }
    var sliderPosition by remember { mutableStateOf(10f..70f) }
    var regionSearch by remember { mutableStateOf("") }

    var selectedSports by remember { mutableStateOf(setOf("⚽ Football")) }
    var selectedFormats by remember { mutableStateOf(setOf("League", "Knockout")) }
    var selectedStatuses by remember { mutableStateOf(setOf("Registration Open")) }
    var selectedRegions by remember { mutableStateOf(setOf("📍 Lisbon")) }


    fun toggleSelection(currentSet: Set<String>, item: String): Set<String> {
        return if (currentSet.contains(item)) currentSet - item else currentSet + item
    }

    val totalActiveFilters = selectedSports.size + selectedFormats.size + selectedStatuses.size + selectedRegions.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filters", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onCloseClick) { Icon(Icons.Default.Close, contentDescription = null, tint = Color.White) }
                },
                actions = {
                    TextButton(onClick = {
                        selectedSports = emptySet()
                        selectedFormats = emptySet()
                        selectedStatuses = emptySet()
                        selectedRegions = emptySet()
                        sliderPosition = 10f..100f
                        regionSearch = ""
                    }) {
                        Text("RESET", color = TealGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().background(BgLight).padding(24.dp)) {
                Button(
                    onClick = onApplyClick,
                    colors = ButtonDefaults.buttonColors(containerColor = TealGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("APPLY FILTERS ($totalActiveFilters)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        },
        containerColor = BgLight
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            FilterSection("Sport Category") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val op1 = "⚽ Football"
                    val op2 = "🏐 Volleyball"
                    val op3 = "🏀 Basketball"
                    FilterChip(op1, isSelected = selectedSports.contains(op1), onClick = { selectedSports = toggleSelection(selectedSports, op1) })
                    FilterChip(op2, isSelected = selectedSports.contains(op2), onClick = { selectedSports = toggleSelection(selectedSports, op2) })
                    FilterChip(op3, isSelected = selectedSports.contains(op3), onClick = { selectedSports = toggleSelection(selectedSports, op3) })
                }
            }

            FilterSection("Competition Format") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val op1 = "League"
                    val op2 = "Knockout"
                    val op3 = "Group Stage"
                    FilterChip(op1, isSelected = selectedFormats.contains(op1), onClick = { selectedFormats = toggleSelection(selectedFormats, op1) })
                    FilterChip(op2, isSelected = selectedFormats.contains(op2), onClick = { selectedFormats = toggleSelection(selectedFormats, op2) })
                    FilterChip(op3, isSelected = selectedFormats.contains(op3), onClick = { selectedFormats = toggleSelection(selectedFormats, op3) })
                }
            }

            FilterSection("Status") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val op1 = "Upcoming"
                    val op2 = "Live"
                    val op3 = "Registration Open"
                    val op4 = "Completed"
                    FilterChip(op1, isSelected = selectedStatuses.contains(op1), onClick = { selectedStatuses = toggleSelection(selectedStatuses, op1) })
                    FilterChip(op2, isSelected = selectedStatuses.contains(op2), onClick = { selectedStatuses = toggleSelection(selectedStatuses, op2) }, overrideColor = if (selectedStatuses.contains(op2)) null else Color(0xFFD1FAE5), overrideTextColor = if (selectedStatuses.contains(op2)) null else TealGreen)
                    FilterChip(op3, isSelected = selectedStatuses.contains(op3), onClick = { selectedStatuses = toggleSelection(selectedStatuses, op3) })
                    FilterChip(op4, isSelected = selectedStatuses.contains(op4), onClick = { selectedStatuses = toggleSelection(selectedStatuses, op4) })
                }
            }

            FilterSection("Region") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextField(
                        value = regionSearch, onValueChange = { regionSearch = it },
                        placeholder = { Text("City or region...", color = TextGray, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Outlined.Place, contentDescription = null, tint = TextGray, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.colors(focusedContainerColor = CardBg, unfocusedContainerColor = CardBg, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val op1 = "📍 Lisbon"
                        val op2 = "Porto"
                        val op3 = "Coimbra"
                        val op4 = "Braga"
                        FilterChip(op1, isSelected = selectedRegions.contains(op1), onClick = { selectedRegions = toggleSelection(selectedRegions, op1) })
                        FilterChip(op2, isSelected = selectedRegions.contains(op2), onClick = { selectedRegions = toggleSelection(selectedRegions, op2) })
                        FilterChip(op3, isSelected = selectedRegions.contains(op3), onClick = { selectedRegions = toggleSelection(selectedRegions, op3) })
                        FilterChip(op4, isSelected = selectedRegions.contains(op4), onClick = { selectedRegions = toggleSelection(selectedRegions, op4) })
                    }
                }
            }

            FilterSection("Date Range") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("FROM", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = fromDate, onValueChange = { fromDate = it },
                            modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(8.dp)),
                            colors = TextFieldDefaults.colors(focusedContainerColor = CardBg, unfocusedContainerColor = CardBg, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = DarkBlue)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("TO", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = toDate, onValueChange = { toDate = it },
                            modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(8.dp)),
                            colors = TextFieldDefaults.colors(focusedContainerColor = CardBg, unfocusedContainerColor = CardBg, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = DarkBlue)
                        )
                    }
                }
            }

            FilterSection("Entry Fee Range") {
                Column {
                    RangeSlider(
                        value = sliderPosition,
                        onValueChange = { sliderPosition = it },
                        valueRange = 10f..100f,
                        colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = PrimaryBlue, inactiveTrackColor = InputBg)
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("€10", color = TextGray, fontSize = 11.sp)
                        Text("€${sliderPosition.start.toInt()} — €${sliderPosition.endInclusive.toInt()}", color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("€100+", color = TextGray, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun FilterSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(title, color = DarkBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
fun FilterChip(text: String, isSelected: Boolean, onClick: () -> Unit, overrideColor: Color? = null, overrideTextColor: Color? = null) {
    val bgColor = overrideColor ?: if (isSelected) PrimaryBlue else CardBg
    val textColor = overrideTextColor ?: if (isSelected) Color.White else TextGray
    val border = if (!isSelected && overrideColor == null) BorderStroke(1.dp, InputBg) else null

    Surface(
        color = bgColor, shape = RoundedCornerShape(8.dp), border = border,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(text, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp))
    }
}