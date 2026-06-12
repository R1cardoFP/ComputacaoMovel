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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R

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
                title = { Text(stringResource(R.string.title_edit), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.desc_back), tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = stringResource(R.string.desc_notifications), tint = Color.White)
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
                Text(stringResource(R.string.tag_tournament_management), color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(stringResource(R.string.mock_tournament_slam), color = DarkBlue, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onCancelClick,
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, ErrorRed),
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                ) {
                    Text(stringResource(R.string.btn_cancel_caps), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Button(
                    onClick = onSaveClick,
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealGreen),
                    modifier = Modifier.weight(1f).height(40.dp)
                ) {
                    Text(stringResource(R.string.btn_save_changes_caps), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(stringResource(R.string.title_tournament_identity), color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    Column {
                        FieldLabel(stringResource(R.string.label_tournament_logo))
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
                                Text(stringResource(R.string.btn_replace_logo), color = DarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(stringResource(R.string.desc_current_logo), color = TextGray, fontSize = 10.sp)
                            }
                        }
                    }

                    EditField(stringResource(R.string.label_tournament_name), tournamentName) { tournamentName = it }
                    EditField(stringResource(R.string.label_sport_type_caps), sportType) { sportType = it }
                    EditField(stringResource(R.string.label_format_caps), format) { format = it }

                    Column {
                        FieldLabel(stringResource(R.string.label_location_caps))
                        TextField(
                            value = location,
                            onValueChange = { location = it },
                            leadingIcon = { Icon(Icons.Outlined.Place, contentDescription = null, tint = TextGray, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(6.dp)),
                            colors = TextFieldDefaults.colors(focusedContainerColor = InputBg, unfocusedContainerColor = InputBg, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = DarkBlue)
                        )
                    }

                    EditField(stringResource(R.string.label_prize_pool_eur), prizePool) { prizePool = it }
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(stringResource(R.string.title_schedule_registration_edit), color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    EditField(stringResource(R.string.label_start_date), startDate) { startDate = it }
                    EditField(stringResource(R.string.label_end_date), endDate) { endDate = it }

                    Column {
                        FieldLabel(stringResource(R.string.label_registration_deadline))
                        TextField(
                            value = regDeadline,
                            onValueChange = { regDeadline = it },
                            modifier = Modifier.fillMaxWidth().height(50.dp).clip(RoundedCornerShape(6.dp)),
                            colors = TextFieldDefaults.colors(focusedContainerColor = InputBg, unfocusedContainerColor = InputBg, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = DarkBlue)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(stringResource(R.string.msg_deadline_approaching), color = ErrorRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    EditField(stringResource(R.string.label_max_participants), maxParticipants) { maxParticipants = it }
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text(stringResource(R.string.title_my_role), color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(stringResource(R.string.desc_role_locked), color = TextGray, fontSize = 10.sp)
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
                                Text(stringResource(R.string.role_participate_player), color = DarkBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(stringResource(R.string.desc_manage_compete), color = TextGray, fontSize = 10.sp)
                            }
                            Icon(Icons.Default.Lock, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                        }
                    }

                    EditField(stringResource(R.string.label_my_seed_team), teamName) { teamName = it }
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(stringResource(R.string.title_event_status), color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.label_registration_status), color = DarkBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Surface(color = LightBlueBadge, shape = RoundedCornerShape(12.dp)) {
                            Text(stringResource(R.string.badge_open), color = PrimaryBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.label_visibility), color = DarkBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Surface(color = InputBg, shape = RoundedCornerShape(12.dp)) {
                            Text(stringResource(R.string.badge_public), color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
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
                Text(stringResource(R.string.btn_delete_tournament), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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