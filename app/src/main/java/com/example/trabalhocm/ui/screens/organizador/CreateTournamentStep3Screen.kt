package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTournamentStep3Screen(
    onBackClick: () -> Unit = {},
    onProceedClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    var venue by remember { mutableStateOf("") }
    var entryFee by remember { mutableStateOf("100.00") }
    var prize1 by remember { mutableStateOf("75000") }
    var prize2 by remember { mutableStateOf("35000") }
    var prize3 by remember { mutableStateOf("20000") }
    var notes by remember { mutableStateOf("") }

    val totalPrizePool = remember(prize1, prize2, prize3) {
        val p1 = prize1.filter { it.isDigit() }.toLongOrNull() ?: 0L
        val p2 = prize2.filter { it.isDigit() }.toLongOrNull() ?: 0L
        val p3 = prize3.filter { it.isDigit() }.toLongOrNull() ?: 0L
        p1 + p2 + p3
    }

    val numberFormat = NumberFormat.getNumberInstance(Locale("pt", "PT"))

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
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
                selectedTab = "TOURNAMENTS",
                onHomeClick = onHomeClick
            )
        },
        containerColor = BgLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Step3HeaderSection()

            Column {
                SectionLabel("VENUE / LOCATION")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = venue,
                    onValueChange = { venue = it },
                    placeholder = { Text("Pesquisar morada ou recinto...", color = Color.LightGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputBg,
                        unfocusedContainerColor = InputBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    leadingIcon = {
                        Icon(Icons.Default.Place, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE2E8F0))
                        .clickable { },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = TextGray, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Escolher localização", color = TextGray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column {
                SectionLabel("ENTRY FEE PER TEAM")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = entryFee,
                    onValueChange = { entryFee = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputBg,
                        unfocusedContainerColor = InputBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    prefix = { Text("€ ", color = DarkBlue, fontWeight = FontWeight.Bold) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Leave at 0.00 for free tournament", color = TextGray, fontSize = 12.sp)
            }

            Column {
                SectionLabel("PRIZE POOL DISTRIBUTION")
                Spacer(modifier = Modifier.height(12.dp))

                PrizeInputRow(
                    rank = "1st Place",
                    iconColor = Color(0xFFFBBF24),
                    value = prize1,
                    onValueChange = { prize1 = it }
                )
                Spacer(modifier = Modifier.height(12.dp))

                PrizeInputRow(
                    rank = "2nd Place",
                    iconColor = Color(0xFF9CA3AF),
                    value = prize2,
                    onValueChange = { prize2 = it }
                )
                Spacer(modifier = Modifier.height(12.dp))

                PrizeInputRow(
                    rank = "3rd Place",
                    iconColor = Color(0xFFD97706),
                    value = prize3,
                    onValueChange = { prize3 = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Total prize pool: ", color = TextGray, fontSize = 12.sp)
                    Text("€ ${numberFormat.format(totalPrizePool)}", color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Column {
                SectionLabel("VENUE NOTES (OPTIONAL)")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("Parking info, entrance details, special instructions...", color = Color.LightGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputBg,
                        unfocusedContainerColor = InputBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    maxLines = 4
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onBackClick,
                    modifier = Modifier.weight(0.4f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = DarkBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("BACK", color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Button(
                    onClick = onProceedClick,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(0.6f)
                        .height(56.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("PROCEED TO ROLE", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun Step3HeaderSection() {
    Column {
        Text("STEP 3 OF 4", color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Location &\nPrizes", color = DarkBlue, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 36.sp)

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.width(24.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(TealGreen))
                Box(modifier = Modifier.width(24.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(TealGreen))
                Box(modifier = Modifier.width(24.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(TealGreen))
                Box(modifier = Modifier.width(24.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFE2E8F0)))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Define where the action happens and what's at stake for the winners.",
            color = TextGray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun PrizeInputRow(
    rank: String,
    iconColor: Color,
    value: String,
    onValueChange: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = iconColor.copy(alpha = 0.2f),
                modifier = Modifier.size(32.dp)
            ) {
                Icon(Icons.Default.Star, contentDescription = rank, tint = iconColor, modifier = Modifier.padding(6.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(rank, color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))

            TextField(
                value = value,
                onValueChange = onValueChange,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .width(130.dp)
                    .height(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = InputBg,
                    unfocusedContainerColor = InputBg,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                prefix = { Text("€ ", color = TextGray) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateTournamentStep3ScreenPreview() {
    MaterialTheme {
        CreateTournamentStep3Screen()
    }
}