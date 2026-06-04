package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
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

private val DarkBlue = Color(0xFF111827)
private val PrimaryBlue = Color(0xFF0346B8)
private val TealGreen = Color(0xFF008D7D)
private val ErrorRed = Color(0xFFDC2626)
private val TextGray = Color(0xFF64748B)
private val BgLight = Color(0xFFF8FAFC)
private val CardBg = Color(0xFFFFFFFF)
private val InputBg = Color(0xFFF1F5F9)
private val LightBlueBadge = Color(0xFFE0E7FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTournamentScreen(
    onBackClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    var tournamentName by remember { mutableStateOf("Premier Summer Cup 2026") }
    var sportType by remember { mutableStateOf("Football") }
    var format by remember { mutableStateOf("League System") }
    var location by remember { mutableStateOf("Estádio Cidade de Barcelos") }
    var prizePool by remember { mutableStateOf("125 000") }

    var startDate by remember { mutableStateOf("07/15/2026") }
    var endDate by remember { mutableStateOf("07/20/2026") }
    var regDeadline by remember { mutableStateOf("07/01/2026") }
    var maxParticipants by remember { mutableStateOf("32") }

    var teamName by remember { mutableStateOf("FC Mancos") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        containerColor = BgLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column {
                Text("TOURNAMENT MANAGEMENT", color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Summer Slam 2026", color = DarkBlue, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onCancelClick,
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, ErrorRed),
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                ) {
                    Text("CANCEL", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Button(
                    onClick = onSaveClick,
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealGreen),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text("SAVE CHANGES", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Tournament Identity", color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    Column {
                        FieldLabel("TOURNAMENT LOGO")
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .drawBehind {
                                    drawRoundRect(
                                        color = Color(0xFFCBD5E1),
                                        style = Stroke(width = 4f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)),
                                        cornerRadius = CornerRadius(8.dp.toPx())
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(DarkBlue))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Replace Logo", color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Current: summer-slam.png - Max 5 MB", color = TextGray, fontSize = 10.sp)
                            }
                        }
                    }

                    EditField("TOURNAMENT NAME", tournamentName) { tournamentName = it }
                    EditField("SPORT TYPE", sportType) { sportType = it }
                    EditField("FORMAT", format) { format = it }

                    Column {
                        FieldLabel("LOCATION")
                        TextField(
                            value = location,
                            onValueChange = { location = it },
                            leadingIcon = { Icon(Icons.Outlined.Place, contentDescription = null, tint = TextGray, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(6.dp)),
                            colors = TextFieldDefaults.colors(focusedContainerColor = InputBg, unfocusedContainerColor = InputBg, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = DarkBlue)
                        )
                    }

                    EditField("PRIZE POOL (€)", prizePool) { prizePool = it }
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Schedule and Registration", color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    EditField("START DATE", startDate) { startDate = it }
                    EditField("END DATE", endDate) { endDate = it }

                    Column {
                        FieldLabel("REGISTRATION DEADLINE")
                        TextField(
                            value = regDeadline,
                            onValueChange = { regDeadline = it },
                            modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(6.dp)),
                            colors = TextFieldDefaults.colors(focusedContainerColor = InputBg, unfocusedContainerColor = InputBg, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = DarkBlue)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("⚠ Deadline is approaching", color = ErrorRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    EditField("MAX PARTICIPANTS", maxParticipants) { maxParticipants = it }
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text("My Role", color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("Your role for this tournament is locked once published.", color = TextGray, fontSize = 10.sp)
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = LightBlueBadge),
                        border = BorderStroke(1.dp, PrimaryBlue),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Person, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Participate as Player", color = DarkBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("Manage and compete", color = TextGray, fontSize = 10.sp)
                            }
                            Icon(Icons.Default.Lock, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                        }
                    }

                    EditField("MY SEED / TEAM NAME", teamName) { teamName = it }
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Event Status", color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Registration Status", color = DarkBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Surface(color = LightBlueBadge, shape = RoundedCornerShape(12.dp)) {
                            Text("• OPEN", color = PrimaryBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Visibility", color = DarkBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Surface(color = InputBg, shape = RoundedCornerShape(12.dp)) {
                            Text("PUBLIC", color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }
                }
            }

            Button(
                onClick = onDeleteClick,
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Tournament", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun EditField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column {
        FieldLabel(label)
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(6.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = InputBg,
                unfocusedContainerColor = InputBg,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = DarkBlue)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditTournamentScreenPreview() {
    MaterialTheme {
        EditTournamentScreen()
    }
}