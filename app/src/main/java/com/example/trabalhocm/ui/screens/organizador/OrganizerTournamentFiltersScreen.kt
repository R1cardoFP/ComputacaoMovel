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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R

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
    val filterFootballStr = stringResource(R.string.filter_football)
    val filterVolleyballStr = stringResource(R.string.filter_volleyball)
    val filterBasketballStr = stringResource(R.string.filter_basketball)

    val filterLeagueStr = stringResource(R.string.filter_league)
    val filterKnockoutStr = stringResource(R.string.filter_knockout)
    val filterGroupStageStr = stringResource(R.string.filter_group_stage)

    val filterUpcomingStr = stringResource(R.string.filter_upcoming)
    val filterLiveStr = stringResource(R.string.filter_live)
    val filterRegistrationOpenStr = stringResource(R.string.filter_registration_open)
    val filterCompletedStr = stringResource(R.string.filter_completed)

    val filterLisbonStr = stringResource(R.string.filter_lisbon)
    val filterPortoStr = stringResource(R.string.filter_porto)
    val filterCoimbraStr = stringResource(R.string.filter_coimbra)
    val filterBragaStr = stringResource(R.string.filter_braga)

    var fromDate by remember { mutableStateOf("01/06/2026") }
    var toDate by remember { mutableStateOf("30/09/2026") }
    var sliderPosition by remember { mutableStateOf(10f..70f) }
    var regionSearch by remember { mutableStateOf("") }

    var selectedSports by remember { mutableStateOf(setOf(filterFootballStr)) }
    var selectedFormats by remember { mutableStateOf(setOf(filterLeagueStr, filterKnockoutStr)) }
    var selectedStatuses by remember { mutableStateOf(setOf(filterRegistrationOpenStr)) }
    var selectedRegions by remember { mutableStateOf(setOf(filterLisbonStr)) }

    fun toggleSelection(currentSet: Set<String>, item: String): Set<String> {
        return if (currentSet.contains(item)) currentSet - item else currentSet + item
    }

    val totalActiveFilters = selectedSports.size + selectedFormats.size + selectedStatuses.size + selectedRegions.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_filters_sheet), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
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
                        Text(stringResource(R.string.btn_reset_caps), color = TealGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
                    Text(stringResource(R.string.btn_apply_filters_count, totalActiveFilters), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        },
        containerColor = BgLight
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).verticalScroll(rememberScrollState()).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            FilterSection(stringResource(R.string.label_sport_category_filters)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(filterFootballStr, isSelected = selectedSports.contains(filterFootballStr), onClick = { selectedSports = toggleSelection(selectedSports, filterFootballStr) })
                    FilterChip(filterVolleyballStr, isSelected = selectedSports.contains(filterVolleyballStr), onClick = { selectedSports = toggleSelection(selectedSports, filterVolleyballStr) })
                    FilterChip(filterBasketballStr, isSelected = selectedSports.contains(filterBasketballStr), onClick = { selectedSports = toggleSelection(selectedSports, filterBasketballStr) })
                }
            }

            FilterSection(stringResource(R.string.label_competition_format_filters)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(filterLeagueStr, isSelected = selectedFormats.contains(filterLeagueStr), onClick = { selectedFormats = toggleSelection(selectedFormats, filterLeagueStr) })
                    FilterChip(filterKnockoutStr, isSelected = selectedFormats.contains(filterKnockoutStr), onClick = { selectedFormats = toggleSelection(selectedFormats, filterKnockoutStr) })
                    FilterChip(filterGroupStageStr, isSelected = selectedFormats.contains(filterGroupStageStr), onClick = { selectedFormats = toggleSelection(selectedFormats, filterGroupStageStr) })
                }
            }

            FilterSection(stringResource(R.string.label_status_filters)) {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(filterUpcomingStr, isSelected = selectedStatuses.contains(filterUpcomingStr), onClick = { selectedStatuses = toggleSelection(selectedStatuses, filterUpcomingStr) })
                    FilterChip(filterLiveStr, isSelected = selectedStatuses.contains(filterLiveStr), onClick = { selectedStatuses = toggleSelection(selectedStatuses, filterLiveStr) }, overrideColor = if (selectedStatuses.contains(filterLiveStr)) null else Color(0xFFD1FAE5), overrideTextColor = if (selectedStatuses.contains(filterLiveStr)) null else TealGreen)
                    FilterChip(filterRegistrationOpenStr, isSelected = selectedStatuses.contains(filterRegistrationOpenStr), onClick = { selectedStatuses = toggleSelection(selectedStatuses, filterRegistrationOpenStr) })
                    FilterChip(filterCompletedStr, isSelected = selectedStatuses.contains(filterCompletedStr), onClick = { selectedStatuses = toggleSelection(selectedStatuses, filterCompletedStr) })
                }
            }

            FilterSection(stringResource(R.string.label_region_filters)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TextField(
                        value = regionSearch, onValueChange = { regionSearch = it },
                        placeholder = { Text(stringResource(R.string.placeholder_city_region), color = TextGray, fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Outlined.Place, contentDescription = null, tint = TextGray, modifier = Modifier.size(18.dp)) },
                        modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.colors(focusedContainerColor = CardBg, unfocusedContainerColor = CardBg, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(filterLisbonStr, isSelected = selectedRegions.contains(filterLisbonStr), onClick = { selectedRegions = toggleSelection(selectedRegions, filterLisbonStr) })
                        FilterChip(filterPortoStr, isSelected = selectedRegions.contains(filterPortoStr), onClick = { selectedRegions = toggleSelection(selectedRegions, filterPortoStr) })
                        FilterChip(filterCoimbraStr, isSelected = selectedRegions.contains(filterCoimbraStr), onClick = { selectedRegions = toggleSelection(selectedRegions, filterCoimbraStr) })
                        FilterChip(filterBragaStr, isSelected = selectedRegions.contains(filterBragaStr), onClick = { selectedRegions = toggleSelection(selectedRegions, filterBragaStr) })
                    }
                }
            }

            FilterSection(stringResource(R.string.label_date_range_filters)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.label_from), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = fromDate, onValueChange = { fromDate = it },
                            modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(8.dp)),
                            colors = TextFieldDefaults.colors(focusedContainerColor = CardBg, unfocusedContainerColor = CardBg, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = DarkBlue)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.label_to), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
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

            FilterSection(stringResource(R.string.label_entry_fee_range)) {
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