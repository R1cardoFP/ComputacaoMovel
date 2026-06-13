package com.example.trabalhocm.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.trabalhocm.R
import com.example.trabalhocm.data.model.AdminEditCasualMatch
import com.example.trabalhocm.data.repository.AdminEditCasualMatchRepository
import com.example.trabalhocm.ui.theme.AppIcons
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import com.example.trabalhocm.ui.theme.CardBg
import com.example.trabalhocm.ui.theme.ErrorRed
import com.example.trabalhocm.ui.theme.PrimaryBlue
import com.example.trabalhocm.ui.theme.TextGray
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminEditCasualMatchScreen(
    matchId: String,
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSaved: () -> Unit = {},
    onCanceled: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { AdminEditCasualMatchRepository() }
    val scope = rememberCoroutineScope()

    var match by remember { mutableStateOf<AdminEditCasualMatch?>(null) }

    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var local by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var actionMessage by remember { mutableStateOf("") }
    var refreshKey by remember { mutableStateOf(0) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var actionMessageIsError by remember { mutableStateOf(false) }

    val errorLoadingMatchText = stringResource(R.string.admin_edit_casual_match_error_loading)
    val emptyNameErrorText = stringResource(R.string.admin_edit_casual_match_empty_name_error)
    val missingFieldsErrorText = stringResource(R.string.admin_edit_casual_match_missing_fields_error)
    val updateSuccessText = stringResource(R.string.admin_edit_casual_match_update_success)
    val saveErrorText = stringResource(R.string.admin_edit_casual_match_save_error)
    val cancelSuccessText = stringResource(R.string.admin_edit_casual_match_cancel_success)
    val cancelErrorText = stringResource(R.string.admin_edit_casual_match_cancel_error)

    if (showDatePicker) {
        EditDatePickerDialog(
            currentDate = date,
            onDateSelected = { selectedDate ->
                date = selectedDate
                showDatePicker = false
            },
            onDismiss = {
                showDatePicker = false
            }
        )
    }

    if (showTimePicker) {
        EditTimePickerDialog(
            currentTime = time,
            onTimeSelected = { selectedTime ->
                time = selectedTime
                showTimePicker = false
            },
            onDismiss = {
                showTimePicker = false
            }
        )
    }

    LaunchedEffect(matchId, refreshKey) {
        isLoading = true
        errorMessage = ""

        repository.obterPeladinha(matchId)
            .onSuccess { loaded ->
                match = loaded
                title = loaded.title
                date = loaded.date
                time = loaded.time.take(5)
                local = loaded.local
                estado = loaded.estado
            }
            .onFailure {
                errorMessage = "$errorLoadingMatchText: ${it.message}"
            }

        isLoading = false
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            AdminEditCasualMatchTopBar(
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick
            )
        },
        bottomBar = {
            AdminEditCasualMatchBottomBar(
                selected = "matches",
                onHomeClick = onHomeClick,
                onTournamentsClick = onTournamentsClick,
                onMatchesClick = onMatchesClick,
                onTeamsClick = onTeamsClick,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandGreen)
                }
            }

            errorMessage.isNotBlank() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage,
                        color = ErrorRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            match != null -> {
                val isCanceled = estado.lowercase() == "cancelada"

                AdminEditCasualMatchContent(
                    match = match!!,
                    title = title,
                    date = date,
                    time = time,
                    local = local,
                    estado = estado,
                    isCanceled = isCanceled,
                    isSaving = isSaving,
                    actionMessage = actionMessage,
                    actionMessageIsError = actionMessageIsError,
                    innerPadding = innerPadding,
                    onTitleChange = { title = it },
                    onDateClick = {
                        showDatePicker = true
                    },
                    onTimeClick = {
                        showTimePicker = true
                    },
                    onLocalChange = { local = it },
                    onSaveClick = {
                        if (title.isBlank()) {
                            actionMessage = emptyNameErrorText
                            actionMessageIsError = true
                            return@AdminEditCasualMatchContent
                        }

                        if (date.isBlank() || time.isBlank() || local.isBlank()) {
                            actionMessage = missingFieldsErrorText
                            actionMessageIsError = true
                            return@AdminEditCasualMatchContent
                        }

                        scope.launch {
                            isSaving = true
                            actionMessage = ""
                            actionMessageIsError = false

                            repository.atualizarPeladinha(
                                matchId = matchId,
                                title = title.trim(),
                                date = date.trim(),
                                time = time.trim(),
                                local = local.trim()
                            )
                                .onSuccess {
                                    actionMessage = updateSuccessText
                                    actionMessageIsError = false
                                    refreshKey++
                                    onSaved()
                                }
                                .onFailure {
                                    actionMessage = "$saveErrorText: ${it.message}"
                                    actionMessageIsError = true
                                }

                            isSaving = false
                        }
                    },
                    onDiscardClick = onBackClick,
                    onCancelMatchClick = {
                        scope.launch {
                            isSaving = true
                            actionMessage = ""
                            actionMessageIsError = false

                            repository.cancelarPeladinha(matchId)
                                .onSuccess {
                                    actionMessage = cancelSuccessText
                                    actionMessageIsError = false
                                    refreshKey++
                                    onCanceled()
                                }
                                .onFailure {
                                    actionMessage = "$cancelErrorText: ${it.message}"
                                    actionMessageIsError = true
                                }

                            isSaving = false
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun AdminEditCasualMatchContent(
    match: AdminEditCasualMatch,
    title: String,
    date: String,
    time: String,
    local: String,
    estado: String,
    isCanceled: Boolean,
    isSaving: Boolean,
    actionMessage: String,
    actionMessageIsError: Boolean,
    innerPadding: PaddingValues,
    onTitleChange: (String) -> Unit,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    onLocalChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onDiscardClick: () -> Unit,
    onCancelMatchClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(
            start = 18.dp,
            end = 18.dp,
            top = 16.dp,
            bottom = 28.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.admin_edit_casual_match_console).uppercase(),
                color = BrandGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.admin_edit_casual_match_title),
                color = BrandBlue,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.admin_edit_casual_match_description),
                color = TextGray,
                fontSize = 12.sp
            )
        }

        item {
            EditMatchSummaryCard(
                title = title,
                modalidade = match.modalidade,
                estado = estado
            )
        }

        item {
            EditMatchFormCard(
                title = title,
                date = date,
                time = time,
                local = local,
                enabled = !isCanceled && !isSaving,
                onTitleChange = onTitleChange,
                onDateClick = onDateClick,
                onTimeClick = onTimeClick,
                onLocalChange = onLocalChange
            )
        }

        if (isCanceled) {
            item {
                Text(
                    text = stringResource(R.string.admin_edit_casual_match_canceled_locked_message),
                    color = ErrorRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (actionMessage.isNotBlank()) {
            item {
                Text(
                    text = actionMessage,
                    color = if (actionMessageIsError) {
                        ErrorRed
                    } else {
                        BrandGreen
                    },
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            EditMatchActionsCard(
                isCanceled = isCanceled,
                isSaving = isSaving,
                onSaveClick = onSaveClick,
                onDiscardClick = onDiscardClick,
                onCancelMatchClick = onCancelMatchClick
            )
        }
    }
}

@Composable
private fun EditMatchSummaryCard(
    title: String,
    modalidade: String,
    estado: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatusBadge(status = estado)

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .background(
                            color = Color(0xFFE0E7FF),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = modalidade.uppercase(),
                        color = PrimaryBlue,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title.ifBlank { stringResource(R.string.admin_edit_casual_match_default_title) },
                color = BrandWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun EditMatchFormCard(
    title: String,
    date: String,
    time: String,
    local: String,
    enabled: Boolean,
    onTitleChange: (String) -> Unit,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    onLocalChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(11.dp)
        ) {
            Text(
                text = stringResource(R.string.admin_edit_casual_match_match_information),
                color = BrandBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            EditInput(
                label = stringResource(R.string.admin_edit_casual_match_label_name_description).uppercase(),
                value = title,
                enabled = enabled,
                onValueChange = onTitleChange,
                placeholder = stringResource(R.string.admin_edit_casual_match_placeholder_name)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PickerInput(
                    label = stringResource(R.string.admin_edit_casual_match_label_date).uppercase(),
                    value = date,
                    placeholder = "2026-06-14",
                    enabled = enabled,
                    icon = AppIcons.Calendar,
                    onClick = onDateClick,
                    modifier = Modifier.weight(1f)
                )

                PickerInput(
                    label = stringResource(R.string.admin_edit_casual_match_label_time).uppercase(),
                    value = time.take(5),
                    placeholder = "19:30",
                    enabled = enabled,
                    icon = AppIcons.Calendar,
                    onClick = onTimeClick,
                    modifier = Modifier.weight(1f)
                )
            }

            EditInput(
                label = stringResource(R.string.admin_edit_casual_match_label_location).uppercase(),
                value = local,
                enabled = enabled,
                onValueChange = onLocalChange,
                placeholder = stringResource(R.string.admin_edit_casual_match_placeholder_location)
            )
        }
    }
}

@Composable
private fun PickerInput(
    label: String,
    value: String,
    placeholder: String,
    enabled: Boolean,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            color = TextGray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
                .background(
                    color = if (enabled) Color(0xFFF1F5F9) else Color(0xFFE5E7EB),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(enabled = enabled) {
                    onClick()
                }
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = value.ifBlank { placeholder },
                color = if (value.isBlank()) TextGray else BrandBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = TextGray,
                modifier = Modifier.size(17.dp)
            )
        }
    }
}

@Composable
private fun EditInput(
    label: String,
    value: String,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier.fillMaxWidth()
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            color = TextGray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(5.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    color = TextGray,
                    fontSize = 12.sp
                )
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF1F5F9),
                unfocusedContainerColor = Color(0xFFF1F5F9),
                disabledContainerColor = Color(0xFFE5E7EB),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
private fun EditMatchActionsCard(
    isCanceled: Boolean,
    isSaving: Boolean,
    onSaveClick: () -> Unit,
    onDiscardClick: () -> Unit,
    onCancelMatchClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEAF8F5)),
        border = BorderStroke(1.dp, BrandGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(15.dp)
        ) {
            Text(
                text = stringResource(R.string.admin_edit_casual_match_admin_actions),
                color = BrandGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(13.dp))

            Button(
                onClick = onSaveClick,
                enabled = !isCanceled && !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = BrandWhite,
                    disabledContainerColor = TextGray,
                    disabledContentColor = BrandWhite
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = if (isSaving) stringResource(R.string.admin_edit_casual_match_saving).uppercase() else stringResource(R.string.admin_edit_casual_match_save_changes).uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(9.dp))

            Button(
                onClick = onDiscardClick,
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(5.dp),
                border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandWhite,
                    contentColor = BrandBlue,
                    disabledContainerColor = Color(0xFFE5E7EB),
                    disabledContentColor = TextGray
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = stringResource(R.string.admin_edit_casual_match_discard_changes).uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(9.dp))

            Button(
                onClick = onCancelMatchClick,
                enabled = !isCanceled && !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCanceled) TextGray else ErrorRed,
                    contentColor = BrandWhite,
                    disabledContainerColor = TextGray,
                    disabledContentColor = BrandWhite
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = if (isCanceled) stringResource(R.string.admin_edit_casual_match_canceled).uppercase() else stringResource(R.string.admin_edit_casual_match_cancel_match).uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditDatePickerDialog(
    currentDate: String,
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = dateStringToMillis(currentDate)
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedMillis = datePickerState.selectedDateMillis

                    if (selectedMillis != null) {
                        onDateSelected(millisToDateString(selectedMillis))
                    } else {
                        onDismiss()
                    }
                }
            ) {
                Text(
                    text = stringResource(R.string.admin_edit_casual_match_ok).uppercase(),
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = stringResource(R.string.admin_common_cancel).uppercase(),
                    color = TextGray,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditTimePickerDialog(
    currentTime: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val initialHour = currentTime.take(2).toIntOrNull() ?: 19
    val initialMinute = currentTime.drop(3).take(2).toIntOrNull() ?: 30

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selectedTime = String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        timePickerState.hour,
                        timePickerState.minute
                    )

                    onTimeSelected(selectedTime)
                }
            ) {
                Text(
                    text = stringResource(R.string.admin_edit_casual_match_ok).uppercase(),
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = stringResource(R.string.admin_common_cancel).uppercase(),
                    color = TextGray,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        title = {
            Text(
                text = stringResource(R.string.admin_edit_casual_match_select_time),
                color = BrandBlue,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            TimePicker(
                state = timePickerState
            )
        },
        containerColor = BrandWhite
    )
}

private fun dateStringToMillis(value: String): Long? {
    return try {
        LocalDate.parse(value.take(10))
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    } catch (e: Exception) {
        null
    }
}

private fun millisToDateString(millis: Long): String {
    return Instant.ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .toString()
}

@Composable
private fun StatusBadge(status: String) {
    val normalized = status.lowercase()

    val background = when (normalized) {
        "aberta" -> Color(0xFFEAF8F5)
        "fechada" -> Color(0xFFFEF3C7)
        "cancelada" -> Color(0xFFFEE2E2)
        else -> Color(0xFFE5E7EB)
    }

    val color = when (normalized) {
        "aberta" -> BrandGreen
        "fechada" -> Color(0xFFEAB308)
        "cancelada" -> ErrorRed
        else -> TextGray
    }

    val text = when (normalized) {
        "aberta" -> stringResource(R.string.admin_edit_casual_match_status_open).uppercase()
        "fechada" -> stringResource(R.string.admin_edit_casual_match_status_closed).uppercase()
        "cancelada" -> stringResource(R.string.admin_edit_casual_match_status_canceled).uppercase()
        else -> status.uppercase()
    }

    Box(
        modifier = Modifier
            .background(
                color = background,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AdminEditCasualMatchTopBar(
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandBlue)
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable {
                onBackClick()
            }
        ) {
            Icon(
                imageVector = AppIcons.Back,
                contentDescription = stringResource(R.string.admin_common_back),
                tint = BrandWhite,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.admin_edit_casual_match_top_title),
                color = BrandWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Icon(
            imageVector = AppIcons.Notifications,
            contentDescription = stringResource(R.string.admin_common_notifications),
            tint = BrandWhite,
            modifier = Modifier
                .size(23.dp)
                .clickable {
                    onNotificationsClick()
                }
        )
    }
}

@Composable
private fun AdminEditCasualMatchBottomBar(
    selected: String,
    onHomeClick: () -> Unit,
    onTournamentsClick: () -> Unit,
    onMatchesClick: () -> Unit,
    onTeamsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandWhite)
            .navigationBarsPadding()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomEditMatchItem(AppIcons.Home, stringResource(R.string.admin_nav_home).uppercase(), selected == "home", onHomeClick)
        BottomEditMatchItem(AppIcons.Tournaments, stringResource(R.string.admin_nav_tournaments).uppercase(), selected == "tournaments", onTournamentsClick)
        BottomEditMatchItem(AppIcons.Games, stringResource(R.string.admin_nav_matches).uppercase(), selected == "matches", onMatchesClick)
        BottomEditMatchItem(AppIcons.Teams, stringResource(R.string.admin_nav_teams).uppercase(), selected == "teams", onTeamsClick)
        BottomEditMatchItem(AppIcons.Profile, stringResource(R.string.admin_nav_profile).uppercase(), selected == "profile", onProfileClick)
    }
}

@Composable
private fun BottomEditMatchItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) PrimaryBlue else TextGray

    Column(
        modifier = Modifier.clickable {
            onClick()
        },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = color,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp
        )
    }
}