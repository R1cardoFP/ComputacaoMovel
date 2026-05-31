package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.ui.screens.MatchPointBottomBar

// Como estas cores só existem neste ficheiro, declaramos como private para não chocar com outras
private val TealGreen = Color(0xFF0CA789)
private val CardBg = Color(0xFFFFFFFF)
private val InputBg = Color(0xFFF1F5F9)
private val PrimaryBlue = Color(0xFF0346B8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTournamentScreen(
    onBackClick: () -> Unit = {},
    onProceedClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    var tournamentName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedSport by remember { mutableStateOf("Football") }
    var selectedFormat by remember { mutableStateOf("League") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
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
            MatchPointBottomBar(
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
            HeaderSection()
            UploadLogoSection()

            Column {
                SectionLabel("TOURNAMENT NAME")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = tournamentName,
                    onValueChange = { tournamentName = it },
                    placeholder = { Text("e.g: FC Mancos", color = Color.LightGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputBg,
                        unfocusedContainerColor = InputBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            // SPORT CATEGORY
            Column {
                SectionLabel("SPORT CATEGORY")
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SportCard(
                        modifier = Modifier.weight(1f),
                        title = "Football",
                        icon = Icons.Default.Star,
                        isSelected = selectedSport == "Football",
                        onClick = {
                            selectedSport = "Football"
                            selectedFormat = "League System" // Reset
                        }
                    )
                    SportCard(
                        modifier = Modifier.weight(1f),
                        title = "Volleyball",
                        icon = Icons.Default.Star,
                        isSelected = selectedSport == "Volleyball",
                        onClick = {
                            selectedSport = "Volleyball"
                            selectedFormat = "Pool Play + Playoffs" // Reset
                        }
                    )
                    SportCard(
                        modifier = Modifier.weight(1f),
                        title = "Basketball",
                        icon = Icons.Default.Star,
                        isSelected = selectedSport == "Basketball",
                        onClick = {
                            selectedSport = "Basketball"
                            selectedFormat = "Regular Season + Playoffs" // Reset para a 1ª opção do Basket
                        }
                    )
                }
            }

            // COMPETITION FORMAT
            Column {
                SectionLabel("COMPETITION FORMAT")
                Spacer(modifier = Modifier.height(8.dp))

                when (selectedSport) {
                    "Football" -> {
                        FormatCard(
                            title = "League System",
                            description = "Round-robin format where teams earn points based on match outcomes.",
                            icon = Icons.Default.List,
                            isSelected = selectedFormat == "League System",
                            onClick = { selectedFormat = "League System" }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FormatCard(
                            title = "Knockout",
                            description = "Bracket-style elimination. Win to advance, lose to exit the tournament.",
                            icon = Icons.Default.Share,
                            isSelected = selectedFormat == "Knockout",
                            onClick = { selectedFormat = "Knockout" }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FormatCard(
                            title = "Group Stage + Knockout",
                            description = "Group round followed by elimination bracket. Hybrid competition format.",
                            icon = Icons.Default.Check,
                            isSelected = selectedFormat == "Group+Knockout",
                            onClick = { selectedFormat = "Group+Knockout" }
                        )
                    }

                    "Volleyball" -> {
                        FormatCard(
                            title = "Pool Play + Playoffs",
                            description = "Teams compete in groups, with the top teams advancing to an elimination bracket.",
                            icon = Icons.Default.Menu,
                            isSelected = selectedFormat == "Pool Play + Playoffs",
                            onClick = { selectedFormat = "Pool Play + Playoffs" }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FormatCard(
                            title = "Double Elimination Bracket",
                            description = "A team is only eliminated from the tournament after losing two matches.",
                            icon = Icons.Default.Share,
                            isSelected = selectedFormat == "Double Elimination",
                            onClick = { selectedFormat = "Double Elimination" }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FormatCard(
                            title = "League System",
                            description = "Round-robin format where teams earn points based on match outcomes.",
                            icon = Icons.Default.List,
                            isSelected = selectedFormat == "League System",
                            onClick = { selectedFormat = "League System" }
                        )
                    }

                    "Basketball" -> {
                        FormatCard(
                            title = "Regular Season + Playoffs",
                            description = "A full regular season followed by a playoff bracket to determine the champion.",
                            icon = Icons.Default.DateRange, // Ícone de calendário
                            isSelected = selectedFormat == "Regular Season + Playoffs",
                            onClick = { selectedFormat = "Regular Season + Playoffs" }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FormatCard(
                            title = "Single Elimination Bracket",
                            description = "Standard knockout tournament. Win to advance, lose and you're out.",
                            icon = Icons.Default.Share, // Ícone que parece um bracket
                            isSelected = selectedFormat == "Single Elimination Bracket",
                            onClick = { selectedFormat = "Single Elimination Bracket" }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FormatCard(
                            title = "3x3 Pool Play",
                            description = "Teams are divided into pools for round-robin play, with top teams advancing.",
                            icon = Icons.Default.Person,
                            isSelected = selectedFormat == "3x3 Pool Play",
                            onClick = { selectedFormat = "3x3 Pool Play" }
                        )
                    }
                }
            }

            Column {
                SectionLabel("DESCRIPTION & RULES")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Briefly describe the tournament goal, prizes, and specific house rules...", color = Color.LightGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputBg,
                        unfocusedContainerColor = InputBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    maxLines = 5
                )
            }

            Button(
                onClick = onProceedClick,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("PROCEED TO SCHEDULE", fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HeaderSection() {
    Column {
        Text("STEP 1 OF 4", color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Tournament\nBasics", color = DarkBlue, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 36.sp)

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.width(24.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(TealGreen))
                Box(modifier = Modifier.width(24.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFE2E8F0)))
                Box(modifier = Modifier.width(24.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFE2E8F0)))
                Box(modifier = Modifier.width(24.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFE2E8F0)))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Define the fundamental identity and competitive structure of your league.",
            color = TextGray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun UploadLogoSection() {
    Column {
        SectionLabel("TOURNAMENT LOGO")
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
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
                .background(CardBg, RoundedCornerShape(12.dp))
                .clickable { /* Abrir galeria */ },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.AccountBox, contentDescription = null, tint = TextGray, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text("Upload Tournament Logo", color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("PNG or JPG • Max 5 MB", color = TextGray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun SportCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) TealGreen else Color.Transparent
    val iconColor = if (isSelected) TealGreen else TextGray
    val textColor = if (isSelected) DarkBlue else TextGray

    Card(
        modifier = modifier
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(2.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FormatCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) TealGreen else Color.Transparent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(2.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = InputBg,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(icon, contentDescription = null, tint = TextGray, modifier = Modifier.padding(8.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, color = TextGray, fontSize = 12.sp, lineHeight = 16.sp)
            }

            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = TealGreen)
            }
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        color = TextGray,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
}

@Preview(showBackground = true)
@Composable
fun CreateTournamentScreenPreview() {
    MaterialTheme {
        CreateTournamentScreen()
    }
}