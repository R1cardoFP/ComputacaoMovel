package com.example.trabalhocm.ui.screens.player

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object PlayerTournamentFiltersState {
    var updateTrigger by mutableIntStateOf(0)
    var selectedSport by mutableStateOf<String?>(null)
    var selectedFormat by mutableStateOf<String?>(null)
    var selectedStatus by mutableStateOf<String?>(null)
    var selectedRegion by mutableStateOf<String?>(null)
    var cityOrRegion by mutableStateOf("")
    var fromDate by mutableStateOf("01/01/2024")
    var toDate by mutableStateOf("31/12/2030")

    fun reset() {
        selectedSport = null
        selectedFormat = null
        selectedStatus = null
        selectedRegion = null
        cityOrRegion = ""
        fromDate = "01/01/2024"
        toDate = "31/12/2030"
        updateTrigger++
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlayerTournamentFiltersScreen(
    onCloseClick: () -> Unit = {},
    onApplyClick: () -> Unit = {}
) {
    var sportCategory by remember { mutableStateOf(PlayerTournamentFiltersState.selectedSport) }
    var competitionFormat by remember { mutableStateOf(PlayerTournamentFiltersState.selectedFormat) }
    var status by remember { mutableStateOf(PlayerTournamentFiltersState.selectedStatus) }
    var region by remember { mutableStateOf(PlayerTournamentFiltersState.cityOrRegion) }
    var regionQuick by remember { mutableStateOf(PlayerTournamentFiltersState.selectedRegion) }
    var fromDate by remember { mutableStateOf(PlayerTournamentFiltersState.fromDate) }
    var toDate by remember { mutableStateOf(PlayerTournamentFiltersState.toDate) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F7FB))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        PlayerTournamentFiltersTopBar(
            onCloseClick = onCloseClick,
            onResetClick = {
                PlayerTournamentFiltersState.reset()
                sportCategory = null
                competitionFormat = null
                status = null
                region = ""
                regionQuick = null
                fromDate = "01/01/2024"
                toDate = "31/12/2030"
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 22.dp)
        ) {
            PlayerTournamentFiltersHeroCard(
                selectedSport = sportCategory,
                selectedFormat = competitionFormat,
                selectedStatus = status,
                selectedRegion = regionQuick ?: region.ifBlank { null },
                fromDate = fromDate,
                toDate = toDate
            )

            Spacer(modifier = Modifier.height(18.dp))

            PlayerTournamentFilterCard(
                title = "Sport Category",
                subtitle = "Choose the sport category for the tournaments."
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PlayerTournamentFilterButton(
                        text = "⚽ Football",
                        selected = sportCategory == "Football",
                        onClick = { sportCategory = if (sportCategory == "Football") null else "Football" }
                    )

                    PlayerTournamentFilterButton(
                        text = "🏐 Volleyball",
                        selected = sportCategory == "Volleyball",
                        onClick = { sportCategory = if (sportCategory == "Volleyball") null else "Volleyball" }
                    )

                    PlayerTournamentFilterButton(
                        text = "🏀 Basketball",
                        selected = sportCategory == "Basketball",
                        onClick = { sportCategory = if (sportCategory == "Basketball") null else "Basketball" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PlayerTournamentFilterCard(
                title = "Competition Format",
                subtitle = "Filter tournaments by their competition structure."
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PlayerTournamentFilterButton(
                        text = "League",
                        selected = competitionFormat == "League",
                        onClick = { competitionFormat = if (competitionFormat == "League") null else "League" }
                    )

                    PlayerTournamentFilterButton(
                        text = "Knockout",
                        selected = competitionFormat == "Knockout",
                        onClick = { competitionFormat = if (competitionFormat == "Knockout") null else "Knockout" }
                    )

                    PlayerTournamentFilterButton(
                        text = "Group Stage",
                        selected = competitionFormat == "Group Stage",
                        onClick = { competitionFormat = if (competitionFormat == "Group Stage") null else "Group Stage" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PlayerTournamentFilterCard(
                title = "Status",
                subtitle = "Choose the current state of the tournament."
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PlayerTournamentFilterButton(
                        text = "Upcoming",
                        selected = status == "Upcoming",
                        onClick = { status = if (status == "Upcoming") null else "Upcoming" }
                    )

                    PlayerTournamentFilterButton(
                        text = "Live",
                        selected = status == "Live",
                        onClick = { status = if (status == "Live") null else "Live" },
                        selectedBackground = BrandGreen.copy(alpha = 0.14f),
                        selectedTextColor = BrandGreen,
                        selectedBorderColor = BrandGreen
                    )

                    PlayerTournamentFilterButton(
                        text = "Registration Open",
                        selected = status == "Registration Open",
                        onClick = { status = if (status == "Registration Open") null else "Registration Open" }
                    )

                    PlayerTournamentFilterButton(
                        text = "Completed",
                        selected = status == "Completed",
                        onClick = { status = if (status == "Completed") null else "Completed" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PlayerTournamentFilterCard(
                title = "Region",
                subtitle = "Search by city or choose one of the quick regions."
            ) {
                OutlinedTextField(
                    value = region,
                    onValueChange = { region = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "City or region...",
                            color = Color(0xFF8D94A3),
                            fontSize = 15.sp
                        )
                    },
                    leadingIcon = {
                        Text(
                            text = "⌖",
                            color = Color(0xFF9EA4B3),
                            fontSize = 18.sp
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF4F7FB),
                        unfocusedContainerColor = Color(0xFFF4F7FB),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = BrandGreen,
                        focusedTextColor = BrandBlue,
                        unfocusedTextColor = BrandBlue
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PlayerTournamentFilterButton(
                        text = "📍 Lisbon",
                        selected = regionQuick == "Lisbon",
                        onClick = { regionQuick = if (regionQuick == "Lisbon") null else "Lisbon" }
                    )

                    PlayerTournamentFilterButton(
                        text = "Porto",
                        selected = regionQuick == "Porto",
                        onClick = { regionQuick = if (regionQuick == "Porto") null else "Porto" }
                    )

                    PlayerTournamentFilterButton(
                        text = "Coimbra",
                        selected = regionQuick == "Coimbra",
                        onClick = { regionQuick = if (regionQuick == "Coimbra") null else "Coimbra" }
                    )

                    PlayerTournamentFilterButton(
                        text = "Braga",
                        selected = regionQuick == "Braga",
                        onClick = { regionQuick = if (regionQuick == "Braga") null else "Braga" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PlayerTournamentFilterCard(
                title = "Date Range",
                subtitle = "Define the date interval for the tournaments."
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        PlayerTournamentSmallLabel("FROM")

                        Spacer(modifier = Modifier.height(8.dp))

                        PlayerTournamentDateField(
                            value = fromDate,
                            onValueChange = { fromDate = it }
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        PlayerTournamentSmallLabel("TO")

                        Spacer(modifier = Modifier.height(8.dp))

                        PlayerTournamentDateField(
                            value = toDate,
                            onValueChange = { toDate = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PlayerTournamentFilterCard(
                title = "Entry Fee Range",
                subtitle = "Use this visual reference to compare tournament entry fees."
            ) {
                PlayerTournamentFeeRangeMock()
            }

            Spacer(modifier = Modifier.height(22.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF4F7FB))
                .padding(horizontal = 24.dp)
                .padding(bottom = 18.dp, top = 8.dp)
        ) {
            Button(
                onClick = {
                    PlayerTournamentFiltersState.selectedSport = sportCategory
                    PlayerTournamentFiltersState.selectedFormat = competitionFormat
                    PlayerTournamentFiltersState.selectedStatus = status
                    PlayerTournamentFiltersState.cityOrRegion = region
                    PlayerTournamentFiltersState.selectedRegion = regionQuick
                    PlayerTournamentFiltersState.fromDate = fromDate
                    PlayerTournamentFiltersState.toDate = toDate

                    PlayerTournamentFiltersState.updateTrigger++
                    onApplyClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreen,
                    contentColor = BrandWhite
                )
            ) {
                Text(
                    text = "APPLY FILTERS",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.4.sp
                )
            }
        }
    }
}

@Composable
fun PlayerTournamentFiltersTopBar(
    onCloseClick: () -> Unit,
    onResetClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
            .background(BrandBlue)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(BrandWhite.copy(alpha = 0.12f))
                .clickable { onCloseClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "×",
                color = BrandWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = "Filters",
                color = BrandWhite,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Refine your tournaments",
                color = BrandWhite.copy(alpha = 0.76f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(BrandGreen.copy(alpha = 0.16f))
                .clickable { onResetClick() }
                .padding(horizontal = 14.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "RESET",
                color = BrandGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PlayerTournamentFiltersHeroCard(
    selectedSport: String?,
    selectedFormat: String?,
    selectedStatus: String?,
    selectedRegion: String?,
    fromDate: String,
    toDate: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Tournament Filters",
                color = BrandWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Adjust the options below to find competitions that match your preferences.",
                color = BrandWhite.copy(alpha = 0.78f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlayerTournamentHeroStat(
                    modifier = Modifier.weight(1f),
                    label = "SPORT",
                    value = selectedSport ?: "Any"
                )

                PlayerTournamentHeroStat(
                    modifier = Modifier.weight(1f),
                    label = "FORMAT",
                    value = selectedFormat ?: "Any"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlayerTournamentHeroStat(
                    modifier = Modifier.weight(1f),
                    label = "STATUS",
                    value = selectedStatus ?: "Any"
                )

                PlayerTournamentHeroStat(
                    modifier = Modifier.weight(1f),
                    label = "REGION",
                    value = selectedRegion ?: "Any"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            PlayerTournamentHeroStat(
                modifier = Modifier.fillMaxWidth(),
                label = "DATES",
                value = "$fromDate → $toDate"
            )
        }
    }
}

@Composable
fun PlayerTournamentHeroStat(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(BrandWhite.copy(alpha = 0.10f))
            .padding(horizontal = 12.dp, vertical = 11.dp)
    ) {
        Text(
            text = label,
            color = BrandGreen,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            color = BrandWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun PlayerTournamentFilterCard(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = title,
                color = BrandBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = Color(0xFF8D94A3),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
    }
}

@Composable
fun PlayerTournamentSmallLabel(
    text: String
) {
    Text(
        text = text,
        color = Color(0xFF6D7486),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.1.sp
    )
}

@Composable
fun PlayerTournamentFilterButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    selectedBackground: Color = BrandBlue,
    selectedTextColor: Color = BrandWhite,
    selectedBorderColor: Color = BrandBlue
) {
    Box(
        modifier = Modifier
            .height(44.dp)
            .clip(RoundedCornerShape(50))
            .background(if (selected) selectedBackground else Color(0xFFF4F7FB))
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = if (selected) selectedBorderColor else Color(0xFFE0E4EE)
                ),
                shape = RoundedCornerShape(50)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) selectedTextColor else Color(0xFF6D7486),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerTournamentDateField(
    value: String,
    onValueChange: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            formatter.timeZone = TimeZone.getTimeZone("UTC")
                            onValueChange(formatter.format(Date(millis)))
                        }
                    }
                ) {
                    Text("OK", color = BrandGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false }
                ) {
                    Text("CANCEL", color = Color(0xFF7D8497), fontWeight = FontWeight.Bold)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor = BrandWhite
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    selectedDayContainerColor = BrandGreen,
                    todayDateBorderColor = BrandGreen,
                    todayContentColor = BrandGreen
                )
            )
        }
    }

    Box {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF4F7FB),
                unfocusedContainerColor = Color(0xFFF4F7FB),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = BrandGreen,
                focusedTextColor = BrandBlue,
                unfocusedTextColor = BrandBlue
            )
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable { showDatePicker = true }
        )
    }
}

@Composable
fun PlayerTournamentFeeRangeMock() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFFDDE1EB))
            )

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.weight(0.22f))

                Box(
                    modifier = Modifier
                        .height(6.dp)
                        .weight(0.48f)
                        .clip(RoundedCornerShape(50))
                        .background(BrandGreen)
                )

                Spacer(modifier = Modifier.weight(0.30f))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.weight(0.20f))

                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(BrandWhite)
                        .border(5.dp, BrandGreen, CircleShape)
                )

                Spacer(modifier = Modifier.weight(0.48f))

                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(BrandWhite)
                        .border(5.dp, BrandGreen, CircleShape)
                )

                Spacer(modifier = Modifier.weight(0.28f))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "€10",
                color = Color(0xFF8D94A3),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(BrandGreen.copy(alpha = 0.12f))
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "€10 — €70",
                    color = BrandGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "€100+",
                color = Color(0xFF8D94A3),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Preview(showBackground = true, name = "Player Tournament Filters Screen")
@Composable
fun PlayerTournamentFiltersScreenPreview() {
    PlayerTournamentFiltersScreen()
}
