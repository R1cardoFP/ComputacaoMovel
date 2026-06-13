package com.example.trabalhocm.ui.screens.player

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
            .background(Color(0xFFF4F7FB))
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
            PlayerMatchFiltersHeroCard(
                selectedSport = selectedSport,
                selectedStatus = selectedStatus,
                selectedRegion = selectedRegion,
                priceStart = priceStart,
                priceEnd = priceEnd
            )

            Spacer(modifier = Modifier.height(18.dp))

            PlayerMatchFilterCard(
                title = "Sport Category",
                subtitle = "Choose the sport you want to play."
            ) {
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            PlayerMatchFilterCard(
                title = "Competition Format",
                subtitle = "Select the type of competition."
            ) {
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            PlayerMatchFilterCard(
                title = "Status",
                subtitle = "Filter matches by their current state."
            ) {
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
                        selectedBackground = BrandGreen.copy(alpha = 0.14f),
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            PlayerMatchFilterCard(
                title = "Region",
                subtitle = "Search by city or choose a popular region."
            ) {
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            PlayerMatchFilterCard(
                title = "Date Range",
                subtitle = "Define the date interval for the matches."
            ) {
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            PlayerMatchFilterCard(
                title = "Entry Fee Range",
                subtitle = "Choose the price range that fits you."
            ) {
                RangeSlider(
                    value = priceStart..priceEnd,
                    onValueChange = { range ->
                        priceStart = range.start
                        priceEnd = range.endInclusive
                    },
                    valueRange = 0f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = BrandGreen,
                        activeTrackColor = BrandGreen,
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

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(BrandGreen.copy(alpha = 0.12f))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "€${priceStart.toInt()} — €${priceEnd.toInt()}",
                            color = BrandGreen,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "€100+",
                        color = Color(0xFF8D94A3),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

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
                shape = RoundedCornerShape(18.dp),
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
                text = "Refine your matches",
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
fun PlayerMatchFiltersHeroCard(
    selectedSport: String?,
    selectedStatus: String?,
    selectedRegion: String?,
    priceStart: Float,
    priceEnd: Float
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
                text = "Match Filters",
                color = BrandWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Adjust the options below to find the best games for you.",
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
                PlayerMatchHeroStat(
                    modifier = Modifier.weight(1f),
                    label = "SPORT",
                    value = selectedSport ?: "Any"
                )

                PlayerMatchHeroStat(
                    modifier = Modifier.weight(1f),
                    label = "STATUS",
                    value = selectedStatus ?: "Any"
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlayerMatchHeroStat(
                    modifier = Modifier.weight(1f),
                    label = "REGION",
                    value = selectedRegion ?: "Any"
                )

                PlayerMatchHeroStat(
                    modifier = Modifier.weight(1f),
                    label = "FEE",
                    value = "€${priceStart.toInt()}-€${priceEnd.toInt()}"
                )
            }
        }
    }
}

@Composable
fun PlayerMatchHeroStat(
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
fun PlayerMatchFilterCard(
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
            PlayerMatchFilterSectionTitle(title)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = Color(0xFF8D94A3),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
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
                width = 1.dp,
                color = if (selected) selectedBorderColor else Color(0xFFE0E4EE),
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
}

@Preview(showBackground = true, name = "Player Match Filters Screen")
@Composable
fun PlayerMatchFiltersScreenPreview() {
    PlayerMatchFiltersScreen()
}
