package com.example.trabalhocm.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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

object PlayerMatchFiltersState {
    var selectedSport by mutableStateOf<String?>(null)
    var selectedStatus by mutableStateOf<String?>(null)
    var selectedRegion by mutableStateOf<String?>(null)
    var cityOrRegion by mutableStateOf("")
    var fromDate by mutableStateOf("01/06/2026")
    var toDate by mutableStateOf("30/09/2026")
    var priceStart by mutableFloatStateOf(0f)
    var priceEnd by mutableFloatStateOf(100f)

    fun reset() {
        selectedSport = null
        selectedStatus = null
        selectedRegion = null
        cityOrRegion = ""
        fromDate = "01/06/2026"
        toDate = "30/09/2026"
        priceStart = 0f
        priceEnd = 100f
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlayerMatchFiltersScreen(
    onCloseClick: () -> Unit = {},
    onResetClick: () -> Unit = {},
    onApplyClick: () -> Unit = {}
) {
    var selectedSport by remember { mutableStateOf(PlayerMatchFiltersState.selectedSport) }
    var selectedFormat by remember { mutableStateOf("League") }
    var selectedStatus by remember { mutableStateOf(PlayerMatchFiltersState.selectedStatus) }
    var selectedRegion by remember { mutableStateOf(PlayerMatchFiltersState.selectedRegion) }
    var cityOrRegion by remember { mutableStateOf(PlayerMatchFiltersState.cityOrRegion) }
    var fromDate by remember { mutableStateOf(PlayerMatchFiltersState.fromDate) }
    var toDate by remember { mutableStateOf(PlayerMatchFiltersState.toDate) }
    var priceStart by remember { mutableFloatStateOf(PlayerMatchFiltersState.priceStart) }
    var priceEnd by remember { mutableFloatStateOf(PlayerMatchFiltersState.priceEnd) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        PlayerMatchFiltersTopBar(
            onCloseClick = onCloseClick,
            onResetClick = {
                PlayerMatchFiltersState.reset()

                selectedSport = null
                selectedFormat = "League"
                selectedStatus = null
                selectedRegion = null
                cityOrRegion = ""
                fromDate = "01/06/2026"
                toDate = "30/09/2026"
                priceStart = 0f
                priceEnd = 100f

                onResetClick()
            }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 22.dp)
        ) {
            PlayerMatchFilterSectionTitle("Sport Category")

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlayerMatchFilterButton(
                    text = "⚽ Football",
                    selected = selectedSport == "Football",
                    onClick = { selectedSport = "Football" }
                )

                PlayerMatchFilterButton(
                    text = "🏐 Volleyball",
                    selected = selectedSport == "Volleyball",
                    onClick = { selectedSport = "Volleyball" }
                )

                PlayerMatchFilterButton(
                    text = "🏀 Basketball",
                    selected = selectedSport == "Basketball",
                    onClick = { selectedSport = "Basketball" }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            PlayerMatchFilterSectionTitle("Competition Format")

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlayerMatchFilterButton(
                    text = "League",
                    selected = selectedFormat == "League",
                    onClick = { selectedFormat = "League" }
                )

                PlayerMatchFilterButton(
                    text = "Knockout",
                    selected = selectedFormat == "Knockout",
                    onClick = { selectedFormat = "Knockout" }
                )

                PlayerMatchFilterButton(
                    text = "Group Stage",
                    selected = selectedFormat == "Group Stage",
                    onClick = { selectedFormat = "Group Stage" }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            PlayerMatchFilterSectionTitle("Status")

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlayerMatchFilterButton(
                    text = "Upcoming",
                    selected = selectedStatus == "Upcoming",
                    onClick = { selectedStatus = "Upcoming" }
                )

                PlayerMatchFilterButton(
                    text = "Live",
                    selected = selectedStatus == "Live",
                    onClick = { selectedStatus = "Live" },
                    selectedBackground = BrandGreen.copy(alpha = 0.12f),
                    selectedTextColor = BrandGreen,
                    selectedBorderColor = BrandGreen
                )

                PlayerMatchFilterButton(
                    text = "Registration Open",
                    selected = selectedStatus == "Registration Open",
                    onClick = { selectedStatus = "Registration Open" }
                )

                PlayerMatchFilterButton(
                    text = "Completed",
                    selected = selectedStatus == "Completed",
                    onClick = { selectedStatus = "Completed" }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            PlayerMatchFilterSectionTitle("Region")

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = cityOrRegion,
                onValueChange = { cityOrRegion = it },
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
                        text = "⊙",
                        color = Color(0xFF9EA4B3),
                        fontSize = 18.sp
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                shape = RoundedCornerShape(12.dp),
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

            Spacer(modifier = Modifier.height(14.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlayerMatchFilterButton(
                    text = "📍 Lisbon",
                    selected = selectedRegion == "Lisbon",
                    onClick = { selectedRegion = "Lisbon" }
                )

                PlayerMatchFilterButton(
                    text = "Porto",
                    selected = selectedRegion == "Porto",
                    onClick = { selectedRegion = "Porto" }
                )

                PlayerMatchFilterButton(
                    text = "Coimbra",
                    selected = selectedRegion == "Coimbra",
                    onClick = { selectedRegion = "Coimbra" }
                )

                PlayerMatchFilterButton(
                    text = "Braga",
                    selected = selectedRegion == "Braga",
                    onClick = { selectedRegion = "Braga" }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            PlayerMatchFilterSectionTitle("Date Range")

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    PlayerMatchFilterSmallLabel("FROM")

                    Spacer(modifier = Modifier.height(8.dp))

                    PlayerMatchDateField(
                        value = fromDate,
                        onValueChange = { fromDate = it }
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    PlayerMatchFilterSmallLabel("TO")

                    Spacer(modifier = Modifier.height(8.dp))

                    PlayerMatchDateField(
                        value = toDate,
                        onValueChange = { toDate = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            PlayerMatchFilterSectionTitle("Entry Fee Range")

            Spacer(modifier = Modifier.height(6.dp))

            RangeSlider(
                value = priceStart..priceEnd,
                onValueChange = { range ->
                    priceStart = range.start
                    priceEnd = range.endInclusive
                },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF244BFF),
                    activeTrackColor = Color(0xFF244BFF),
                    inactiveTrackColor = Color(0xFFDDE1EB)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "€0",
                    color = Color(0xFF8D94A3),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "€${priceStart.toInt()} — €${priceEnd.toInt()}",
                    color = BrandBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "€100+",
                    color = Color(0xFF8D94A3),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    PlayerMatchFiltersState.selectedSport = selectedSport
                    PlayerMatchFiltersState.selectedStatus = selectedStatus
                    PlayerMatchFiltersState.selectedRegion = selectedRegion
                    PlayerMatchFiltersState.cityOrRegion = cityOrRegion
                    PlayerMatchFiltersState.fromDate = fromDate
                    PlayerMatchFiltersState.toDate = toDate
                    PlayerMatchFiltersState.priceStart = priceStart
                    PlayerMatchFiltersState.priceEnd = priceEnd

                    onApplyClick()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreen,
                    contentColor = BrandWhite
                )
            ) {
                Text(
                    text = "APPLY FILTERS",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun PlayerMatchFiltersTopBar(
    onCloseClick: () -> Unit,
    onResetClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(BrandBlue)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "×",
            color = BrandWhite,
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable {
                onCloseClick()
            }
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Filters",
            color = BrandWhite,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "RESET",
            color = BrandGreen,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                onResetClick()
            }
        )
    }
}

@Composable
fun PlayerMatchFilterSectionTitle(
    text: String
) {
    Text(
        text = text,
        color = BrandBlue,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun PlayerMatchFilterSmallLabel(
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
fun PlayerMatchFilterButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    selectedBackground: Color = Color(0xFF244BFF),
    selectedTextColor: Color = BrandWhite,
    selectedBorderColor: Color = Color(0xFF244BFF)
) {
    Box(
        modifier = Modifier
            .height(42.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(if (selected) selectedBackground else BrandWhite)
            .border(
                width = 1.dp,
                color = if (selected) selectedBorderColor else Color(0xFFD8DCE6),
                shape = RoundedCornerShape(5.dp)
            )
            .clickable {
                onClick()
            }
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

@Composable
fun PlayerMatchDateField(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        shape = RoundedCornerShape(10.dp),
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
}

@Preview(showBackground = true, name = "Player Match Filters Screen")
@Composable
fun PlayerMatchFiltersScreenPreview() {
    PlayerMatchFiltersScreen()
}