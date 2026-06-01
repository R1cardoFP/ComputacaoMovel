package com.example.trabalhocm.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountBox
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

// Cores locais (usamos private para não chocar com os outros ecrãs)
private val DarkBlue = Color(0xFF111827)
private val PrimaryBlue = Color(0xFF0346B8)
private val TealGreen = Color(0xFF0CA789)
private val TextGray = Color(0xFF64748B)
private val BgLight = Color(0xFFF8FAFC)
private val CardBg = Color(0xFFFFFFFF)
private val InputBg = Color(0xFFF1F5F9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTeamScreen(
    onBackClick: () -> Unit = {},
    onCreateClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    // Variáveis de Estado (Tudo vazio por defeito!)
    var teamName by remember { mutableStateOf("") }
    var initials by remember { mutableStateOf("") }
    var homeCity by remember { mutableStateOf("") }
    var selectedSport by remember { mutableStateOf("") } // Vazio, obriga a escolher

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Teams", color = Color.White, fontWeight = FontWeight.Bold) },
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
            MatchPointBottomBar(selectedTab = "TEAMS", onHomeClick = onHomeClick)
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
            // CABEÇALHO
            Column {
                Text("NEW TEAM", color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Team Identity", color = DarkBlue, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Define your team's name, sport and visual identity.", color = TextGray, fontSize = 14.sp)
            }

            // UPLOAD TEAM LOGO
            Column {
                Text("TEAM LOGO / PHOTO", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
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
                        .clickable { /* Lógica de abrir galeria */ },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.AccountBox, contentDescription = null, tint = TextGray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Upload Team Logo", color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("PNG or JPG • Max 5 MB", color = TextGray, fontSize = 12.sp)
                    }
                }
            }

            // TEAM NAME
            Column {
                Text("TEAM NAME", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = teamName,
                    onValueChange = { teamName = it },
                    placeholder = { Text("e.g. FC Mancos", color = Color.LightGray) },
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

            // INITIALS
            Column {
                Text("INITIALS", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = initials,
                    onValueChange = {
                        // Limita a 4 caracteres e mete tudo em maiúsculas automaticamente
                        if (it.length <= 4) initials = it.uppercase()
                    },
                    placeholder = { Text("e.g. FCM", color = Color.LightGray) },
                    modifier = Modifier
                        .fillMaxWidth(0.5f) // Ocupa só metade da largura como no design
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

            // HOME CITY
            Column {
                Text("HOME CITY", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = homeCity,
                    onValueChange = { homeCity = it },
                    placeholder = { Text("e.g. Viana do Castelo, PT", color = Color.LightGray) },
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
                Text("SPORT CATEGORY", color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TeamSportCard(
                        modifier = Modifier.weight(1f),
                        title = "Football",
                        icon = Icons.Default.Star, // Substituir pelo ícone de bola depois se quiseres
                        isSelected = selectedSport == "Football",
                        onClick = { selectedSport = "Football" }
                    )
                    TeamSportCard(
                        modifier = Modifier.weight(1f),
                        title = "Volleyball",
                        icon = Icons.Default.Star,
                        isSelected = selectedSport == "Volleyball",
                        onClick = { selectedSport = "Volleyball" }
                    )
                    TeamSportCard(
                        modifier = Modifier.weight(1f),
                        title = "Basketball",
                        icon = Icons.Default.Star,
                        isSelected = selectedSport == "Basketball",
                        onClick = { selectedSport = "Basketball" }
                    )
                }
            }

            // CREATE TEAM BUTTON
            Button(
                onClick = onCreateClick,
                colors = ButtonDefaults.buttonColors(containerColor = TealGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("CREATE TEAM", fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun TeamSportCard(
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

@Preview(showBackground = true)
@Composable
fun CreateTeamScreenPreview() {
    MaterialTheme {
        CreateTeamScreen()
    }
}
