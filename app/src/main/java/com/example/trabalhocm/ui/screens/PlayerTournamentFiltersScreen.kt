package com.example.trabalhocm.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite

@Composable
fun PlayerTournamentFiltersScreen(
    onCloseClick: () -> Unit = {},
    onApplyClick: () -> Unit = {}
) {
    var sportCategory by remember { mutableStateOf("Football") }
    var competitionFormat by remember { mutableStateOf("League") }
    var status by remember { mutableStateOf("Registration Open") }
    var region by remember { mutableStateOf("") }
    var regionQuick by remember { mutableStateOf("Lisbon") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(BrandBlue)
                .padding(horizontal = 28.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "×",
                color = BrandWhite,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
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
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                modifier = Modifier.clickable {
                    sportCategory = "Football"
                    competitionFormat = "League"
                    status = "Registration Open"
                    region = ""
                    regionQuick = "Lisbon"
                }
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 24.dp)
        ) {
            FilterSectionTitle("Sport Category")

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChipOption(
                    text = "⚽ Football",
                    selected = sportCategory == "Football",
                    onClick = { sportCategory = "Football" }
                )

                FilterChipOption(
                    text = "🏐 Volleyball",
                    selected = sportCategory == "Volleyball",
                    onClick = { sportCategory = "Volleyball" }
                )

                FilterChipOption(
                    text = "🏀 Basketball",
                    selected = sportCategory == "Basketball",
                    onClick = { sportCategory = "Basketball" }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            FilterSectionTitle("Competition Format")

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilterChipOption(
                    text = "League",
                    selected = competitionFormat == "League",
                    onClick = { competitionFormat = "League" }
                )

                FilterChipOption(
                    text = "Knockout",
                    selected = competitionFormat == "Knockout",
                    onClick = { competitionFormat = "Knockout" }
                )

                FilterChipOption(
                    text = "Group Stage",
                    selected = competitionFormat == "Group Stage",
                    onClick = { competitionFormat = "Group Stage" }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            FilterSectionTitle("Status")

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChipOption(
                    text = "Upcoming",
                    selected = status == "Upcoming",
                    onClick = { status = "Upcoming" }
                )

                FilterChipOption(
                    text = "Live",
                    selected = status == "Live",
                    outlinedSelected = true,
                    onClick = { status = "Live" }
                )

                FilterChipOption(
                    text = "Registration Open",
                    selected = status == "Registration Open",
                    onClick = { status = "Registration Open" }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChipOption(
                    text = "Completed",
                    selected = status == "Completed",
                    onClick = { status = "Completed" }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            FilterSectionTitle("Region")

            Spacer(modifier = Modifier.height(12.dp))

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
                        color = Color(0xFF8F96A5),
                        fontSize = 16.sp
                    )
                },
                leadingIcon = {
                    Text(
                        text = "⊙",
                        color = Color(0xFF9EA4B3),
                        fontSize = 18.sp
                    )
                },
                shape = RoundedCornerShape(9.dp),
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

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChipOption(
                    text = "📍 Lisbon",
                    selected = regionQuick == "Lisbon",
                    onClick = { regionQuick = "Lisbon" }
                )

                FilterChipOption(
                    text = "Porto",
                    selected = regionQuick == "Porto",
                    onClick = { regionQuick = "Porto" }
                )

                FilterChipOption(
                    text = "Coimbra",
                    selected = regionQuick == "Coimbra",
                    onClick = { regionQuick = "Coimbra" }
                )

                FilterChipOption(
                    text = "Braga",
                    selected = regionQuick == "Braga",
                    onClick = { regionQuick = "Braga" }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            FilterSectionTitle("Date Range")

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DateInputBox(
                    modifier = Modifier.weight(1f),
                    label = "FROM",
                    value = "01/06/2026"
                )

                DateInputBox(
                    modifier = Modifier.weight(1f),
                    label = "TO",
                    value = "30/09/2026"
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            FilterSectionTitle("Entry Fee Range")

            Spacer(modifier = Modifier.height(16.dp))

            FeeRangeMock()

            Spacer(modifier = Modifier.height(20.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF4F5FA))
                .padding(horizontal = 28.dp)
                .padding(bottom = 18.dp, top = 8.dp)
        ) {
            Button(
                onClick = onApplyClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(7.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreen,
                    contentColor = BrandWhite
                )
            ) {
                Text(
                    text = "APPLY FILTERS (4)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun FilterSectionTitle(text: String) {
    Text(
        text = text,
        color = Color(0xFF303646),
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun FilterChipOption(
    text: String,
    selected: Boolean,
    outlinedSelected: Boolean = false,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        selected && outlinedSelected -> Color(0xFFEAF7F5)
        selected -> Color(0xFF2949FF)
        else -> BrandWhite
    }

    val textColor = when {
        selected && outlinedSelected -> BrandGreen
        selected -> BrandWhite
        else -> Color(0xFF737B8C)
    }

    val borderColor = if (selected && outlinedSelected) BrandGreen else Color(0xFFE0E3EA)

    Box(
        modifier = Modifier
            .height(36.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .border(
                border = BorderStroke(1.dp, borderColor),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DateInputBox(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            color = Color(0xFF7D8497),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(9.dp),
            colors = CardDefaults.cardColors(containerColor = BrandWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = value,
                    color = Color(0xFF303646),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun FeeRangeMock() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE0E3EA))
            )

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.weight(0.22f))

                Box(
                    modifier = Modifier
                        .height(4.dp)
                        .weight(0.48f)
                        .background(Color(0xFF2949FF))
                )

                Spacer(modifier = Modifier.weight(0.30f))
            }

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.weight(0.20f))

                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(BrandWhite)
                        .border(3.dp, Color(0xFF2949FF), CircleShape)
                )

                Spacer(modifier = Modifier.weight(0.48f))

                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(BrandWhite)
                        .border(3.dp, Color(0xFF2949FF), CircleShape)
                )

                Spacer(modifier = Modifier.weight(0.28f))
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "€10",
                color = Color(0xFF7D8497),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "€10 — €70",
                color = Color(0xFF303646),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "€100+",
                color = Color(0xFF7D8497),
                fontSize = 12.sp,
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