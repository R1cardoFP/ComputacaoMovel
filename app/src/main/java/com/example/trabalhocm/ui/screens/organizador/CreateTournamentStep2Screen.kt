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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

import com.example.trabalhocm.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTournamentStep2Screen(
    viewModel: CreateTournamentViewModel,
    onBackClick: () -> Unit = {},
    onProceedClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    LaunchedEffect(Unit) {
        if (viewModel.startDate.isEmpty()) {
            val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).apply { timeZone = TimeZone.getTimeZone("UTC") }
            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            cal.add(Calendar.DAY_OF_YEAR, 2)
            viewModel.startDate = sdf.format(cal.time)

            val endCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            endCal.add(Calendar.DAY_OF_YEAR, 4)
            viewModel.endDate = sdf.format(endCal.time)

            val deadlineCal = cal.clone() as Calendar
            deadlineCal.add(Calendar.DAY_OF_YEAR, -1)
            viewModel.registrationDeadline = sdf.format(deadlineCal.time)
        }
    }

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
            Step2HeaderSection()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Step2SectionLabel("START DATE")
                    Spacer(modifier = Modifier.height(8.dp))
                    DatePickerField(
                        value = viewModel.startDate,
                        maxDate = null,
                        onValueChange = { newDate ->
                            viewModel.startDate = newDate

                            try {
                                val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).apply {
                                    timeZone = TimeZone.getTimeZone("UTC")
                                }
                                val parsedStart = sdf.parse(newDate)

                                if (parsedStart != null) {
                                    val deadlineCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                        time = parsedStart
                                        add(Calendar.DAY_OF_YEAR, -1)
                                    }
                                    viewModel.registrationDeadline = sdf.format(deadlineCal.time)

                                    val parsedEnd = sdf.parse(viewModel.endDate)
                                    if (parsedEnd != null && parsedStart.time >= parsedEnd.time) {
                                        val endCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                            time = parsedStart
                                            add(Calendar.DAY_OF_YEAR, 2)
                                        }
                                        viewModel.endDate = sdf.format(endCal.time)
                                    }
                                }
                            } catch (e: Exception) { }
                        },
                        icon = Icons.Default.DateRange
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Step2SectionLabel("END DATE")
                    Spacer(modifier = Modifier.height(8.dp))
                    DatePickerField(
                        value = viewModel.endDate,
                        minDate = viewModel.startDate,
                        onValueChange = { viewModel.endDate = it },
                        icon = Icons.Default.DateRange
                    )
                }
            }

            Column {
                Step2SectionLabel("REGISTRATION DEADLINE")
                Spacer(modifier = Modifier.height(8.dp))
                DatePickerField(
                    value = viewModel.registrationDeadline,
                    maxDate = viewModel.startDate,
                    onValueChange = { viewModel.registrationDeadline = it },
                    icon = Icons.Default.DateRange
                )
            }

            // MAX PARTICIPANTS
            Column {
                Step2SectionLabel("MAX PARTICIPANTS")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = viewModel.maxParticipants,
                    onValueChange = { viewModel.maxParticipants = it },
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
                    trailingIcon = {
                        Text("teams", color = TextGray, fontSize = 14.sp, modifier = Modifier.padding(end = 16.dp))
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Recommended: 8, 16, 32 or 64 teams for balanced brackets", color = TextGray, fontSize = 12.sp)
            }

            // REGISTRATION FORMAT
            Column {
                Step2SectionLabel("REGISTRATION FORMAT")
                Spacer(modifier = Modifier.height(8.dp))

                RegistrationFormatCard(
                    title = "Open Registration",
                    description = "Any team can apply directly through the platform.",
                    icon = Icons.Default.Person,
                    isSelected = viewModel.registrationFormat == "Open Registration",
                    onClick = { viewModel.registrationFormat = "Open Registration" }
                )
                Spacer(modifier = Modifier.height(12.dp))
                RegistrationFormatCard(
                    title = "Invite Only",
                    description = "You manually invite teams to participate. Add invitees from the tournament card after creation.",
                    icon = Icons.Default.Lock,
                    isSelected = viewModel.registrationFormat == "Invite Only",
                    onClick = { viewModel.registrationFormat = "Invite Only" }
                )
            }

            // BOTÕES DE NAVEGAÇÃO
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
                        Text("PROCEED TO LOCATION", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
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
fun Step2HeaderSection() {
    Column {
        Text("STEP 2 OF 4", color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Schedule and\nRegistration", color = DarkBlue, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 36.sp)

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.width(24.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(TealGreen))
                Box(modifier = Modifier.width(24.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(TealGreen))
                Box(modifier = Modifier.width(24.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFE2E8F0)))
                Box(modifier = Modifier.width(24.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(Color(0xFFE2E8F0)))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Set the timeline and registration window for your tournament.",
            color = TextGray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    minDate: String? = null,
    maxDate: String? = null
) {
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val sdf = remember {
            SimpleDateFormat("dd/MM/yy", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
        }

        val initialDateMillis = remember(value) {
            try { sdf.parse(value)?.time } catch (e: Exception) { null }
        }

        val selectableDates = remember(minDate, maxDate) {
            object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    var isValid = true

                    if (minDate != null) {
                        try {
                            val minMillis = sdf.parse(minDate)?.time ?: 0L
                            if (utcTimeMillis < minMillis) isValid = false
                        } catch (e: Exception) {}
                    }

                    if (maxDate != null) {
                        try {
                            val maxMillis = sdf.parse(maxDate)?.time ?: Long.MAX_VALUE
                            if (utcTimeMillis > maxMillis) isValid = false
                        } catch (e: Exception) {}
                    }

                    return isValid
                }
            }
        }

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialDateMillis,
            selectableDates = selectableDates
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        onValueChange(sdf.format(Date(millis)))
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = value,
            onValueChange = {},
            readOnly = true,
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
                Icon(icon, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
            }
        )
        Spacer(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .clickable { showDatePicker = true }
        )
    }
}

@Composable
fun RegistrationFormatCard(
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

            Spacer(modifier = Modifier.width(8.dp))

            if (isSelected) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = TealGreen)
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

private @Composable
fun Step2SectionLabel(text: String) {
    Text(
        text = text,
        color = TextGray,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
}

