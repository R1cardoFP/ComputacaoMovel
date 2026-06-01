package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.ui.screens.MatchPointBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTournamentStep4Screen(
    onBackClick: () -> Unit = {},
    onPublishClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    // Estado para o papel no torneio
    var selectedRole by remember { mutableStateOf("Participate as Player") }

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
            // CABEÇALHO (Step 4 of 4)
            Step4HeaderSection()

            // MY ROLE IN THIS TOURNAMENT
            Column {
                SectionLabel("MY ROLE IN THIS TOURNAMENT")
                Spacer(modifier = Modifier.height(8.dp))

                RoleCard(
                    title = "Organizer Only",
                    description = "Manage the event, brackets, and scores. You will not play.",
                    icon = Icons.Default.Build, // Temporário para a mala
                    isSelected = selectedRole == "Organizer Only",
                    onClick = { selectedRole = "Organizer Only" }
                )
                Spacer(modifier = Modifier.height(12.dp))
                RoleCard(
                    title = "Participate as Player",
                    description = "Manage the event and compete in the bracket.",
                    icon = Icons.Default.Person,
                    isSelected = selectedRole == "Participate as Player",
                    onClick = { selectedRole = "Participate as Player" }
                )
            }

            // MY SEED / TEAM NAME
            Column {
                SectionLabel("MY SEED / TEAM NAME")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = "FC Mancos",
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        disabledContainerColor = InputBg,
                        disabledIndicatorColor = Color.Transparent,
                        disabledTextColor = DarkBlue
                    ),
                    textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold),
                    trailingIcon = {
                        Icon(Icons.Outlined.Lock, contentDescription = "Locked", tint = TextGray, modifier = Modifier.size(20.dp))
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Auto-filled with your current team. Cannot be changed for this tournament.", color = TextGray, fontSize = 12.sp, lineHeight = 16.sp)
            }

            // EVENT STATUS
            Column {
                SectionLabel("EVENT STATUS")
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Registration Status", color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        StatusBadge("OPEN", PrimaryBlue)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Visibility", color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        StatusBadge("PUBLIC", PrimaryBlue)
                    }
                }
            }

            // TOURNAMENT DETAILS REVIEW
            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Tournament Details", color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("EDIT", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.clickable { onBackClick() })
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ReviewRow("Name", "FC Mancos")
                    Spacer(modifier = Modifier.height(8.dp))
                    ReviewRow("Sport", "Football")
                    Spacer(modifier = Modifier.height(8.dp))
                    ReviewRow("Format", "League System")
                    Spacer(modifier = Modifier.height(8.dp))
                    ReviewRow("Schedule", "15/07 - 20/07/26")
                    Spacer(modifier = Modifier.height(8.dp))
                    ReviewRow("Max Teams", "32")
                    Spacer(modifier = Modifier.height(8.dp))
                    ReviewRow("Location", "Metro City Sports")
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Prize Pool", color = TextGray, fontSize = 14.sp)
                        Text("€ 125 000", color = TealGreen, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    }
                }
            }

            // BOTÕES FINAIS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onBackClick,
                    modifier = Modifier.weight(0.35f)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = DarkBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("BACK", color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Button(
                    onClick = onPublishClick,
                    colors = ButtonDefaults.buttonColors(containerColor = TealGreen), // Botão Verde Final!
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(0.65f)
                        .height(56.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("PUBLISH TOURNAMENT", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun Step4HeaderSection() {
    Column {
        Text("STEP 4 OF 4", color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Role &\nReview", color = DarkBlue, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 36.sp)

            // Progress Bar (Tudo a verde!)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.width(24.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(TealGreen))
                Box(modifier = Modifier.width(24.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(TealGreen))
                Box(modifier = Modifier.width(24.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(TealGreen))
                Box(modifier = Modifier.width(24.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(TealGreen))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Choose your level of involvement and confirm everything looks right.",
            color = TextGray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun RoleCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) PrimaryBlue else Color.Transparent

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
                Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.padding(8.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, color = TextGray, fontSize = 12.sp, lineHeight = 16.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isSelected) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = PrimaryBlue)
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(2.dp, Color(0xFFE2E8F0), CircleShape)
                )
            }
        }
    }
}

@Composable
fun StatusBadge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
    }
}

@Composable
fun ReviewRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextGray, fontSize = 14.sp)
        Text(value, color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun CreateTournamentStep4ScreenPreview() {
    MaterialTheme {
        CreateTournamentStep4Screen()
    }
}