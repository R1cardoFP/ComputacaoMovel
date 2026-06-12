package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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

private val sportOptions = listOf("Football", "Basketball", "Volleyball")
private val formatOptions = listOf("League System", "Knockout", "Group Stage + Knockout")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTournamentScreen(
    idTorneio: Long,
    viewModel: EditTournamentViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onSaveSuccess: () -> Unit = {},
    onDeleteSuccess: () -> Unit = {}
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(idTorneio) {
        viewModel.carregarTorneio(idTorneio)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.dialog_delete_tournament_title), fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.dialog_delete_tournament_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.apagarTorneio(onSuccess = onDeleteSuccess)
                    }
                ) {
                    Text(stringResource(R.string.btn_delete_tournament), color = ErrorRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.btn_cancel), color = TextGray)
                }
            }
        )
    }

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
        if (viewModel.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TealGreen)
            }
            return@Scaffold
        }

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
                Text(viewModel.tournamentName, color = DarkBlue, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            }

            if (viewModel.errorMessage.isNotBlank()) {
                Text(
                    text = viewModel.errorMessage,
                    color = ErrorRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onCancelClick,
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, ErrorRed),
                    modifier = Modifier.weight(1f).height(40.dp),
                    enabled = !viewModel.isSaving,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)
                ) {
                    Text(stringResource(R.string.btn_cancel_caps), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Button(
                    onClick = { viewModel.guardarAlteracoes(onSuccess = onSaveSuccess) },
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealGreen),
                    modifier = Modifier.weight(1f).height(40.dp),
                    enabled = !viewModel.isSaving
                ) {
                    if (viewModel.isSaving) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text(stringResource(R.string.btn_save_changes_caps), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(stringResource(R.string.title_tournament_identity), color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    EditField(stringResource(R.string.label_tournament_name), viewModel.tournamentName) { viewModel.tournamentName = it }

                    DropdownField(
                        label = stringResource(R.string.label_sport_type_caps),
                        value = viewModel.selectedSport,
                        options = sportOptions,
                        onSelect = { viewModel.selectedSport = it }
                    )

                    DropdownField(
                        label = stringResource(R.string.label_format_caps),
                        value = viewModel.selectedFormat,
                        options = formatOptions,
                        onSelect = { viewModel.selectedFormat = it }
                    )

                    Column {
                        FieldLabel(stringResource(R.string.label_location_caps))
                        TextField(
                            value = viewModel.venue,
                            onValueChange = { viewModel.venue = it },
                            leadingIcon = { Icon(Icons.Outlined.Place, contentDescription = null, tint = TextGray, modifier = Modifier.size(18.dp)) },
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)),
                            colors = TextFieldDefaults.colors(focusedContainerColor = InputBg, unfocusedContainerColor = InputBg, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = DarkBlue),
                            singleLine = true
                        )
                    }

                    EditField(stringResource(R.string.label_description_caps), viewModel.description, singleLine = false) { viewModel.description = it }
                    EditField(stringResource(R.string.label_rules_caps), viewModel.rules, singleLine = false) { viewModel.rules = it }
                    EditField(stringResource(R.string.label_prize_pool_eur), viewModel.prizePool) { viewModel.prizePool = it }
                    EditField(stringResource(R.string.label_entry_fee), viewModel.entryFee) { viewModel.entryFee = it }
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(stringResource(R.string.title_schedule_registration_edit), color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    EditField(stringResource(R.string.label_start_date), viewModel.startDate) { viewModel.startDate = it }
                    EditField(stringResource(R.string.label_end_date), viewModel.endDate) { viewModel.endDate = it }
                }
            }

            Card(colors = CardDefaults.cardColors(containerColor = CardBg), shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(stringResource(R.string.title_event_status), color = DarkBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(stringResource(R.string.label_registration_status), color = DarkBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Surface(color = LightBlueBadge, shape = RoundedCornerShape(12.dp)) {
                            Text(
                                viewModel.estado.uppercase(),
                                color = PrimaryBlue,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Button(
                onClick = { showDeleteDialog = true },
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = !viewModel.isSaving
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.btn_delete_tournament), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String,
    value: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        FieldLabel(label)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            TextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .menuAnchor(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = InputBg,
                    unfocusedContainerColor = InputBg,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = DarkBlue)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, fontSize = 13.sp) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(text, color = TextGray, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
    Spacer(modifier = Modifier.height(6.dp))
}

@Composable
private fun EditField(
    label: String,
    value: String,
    singleLine: Boolean = true,
    onValueChange: (String) -> Unit
) {
    Column {
        FieldLabel(label)
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = InputBg,
                unfocusedContainerColor = InputBg,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 13.sp, color = DarkBlue),
            singleLine = singleLine
        )
    }
}
