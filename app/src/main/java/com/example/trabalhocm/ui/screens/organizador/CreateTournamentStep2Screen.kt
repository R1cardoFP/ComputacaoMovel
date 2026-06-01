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
import com.example.trabalhocm.ui.screens.MatchPointBottomBar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTournamentStep2Screen(
    onBackClick: () -> Unit = {},
    onProceedClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    // CORRIGIDO: Gerar datas sugeridas dinamicamente (Começa em 2 dias, acaba em 4 dias)
    val (defaultStart, defaultEnd, defaultDeadline) = remember {
        val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

        // Data de Início Sugerida: Daqui a 2 dias
        cal.add(Calendar.DAY_OF_YEAR, 2)
        val startStr = sdf.format(cal.time)

        // Data de Fim Sugerida: Daqui a 4 dias a contar de hoje (ou seja, 2 dias após o início)
        val endCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        endCal.add(Calendar.DAY_OF_YEAR, 4)
        val endStr = sdf.format(endCal.time)

        // Deadline de Inscrição Sugerida: 1 dia antes do início (Daqui a 1 dia)
        val deadlineCal = cal.clone() as Calendar
        deadlineCal.add(Calendar.DAY_OF_YEAR, -1)
        val deadlineStr = sdf.format(deadlineCal.time)

        Triple(startStr, endStr, deadlineStr)
    }

    // Variáveis de Estado usando as novas datas inteligentes
    var startDate by remember { mutableStateOf(defaultStart) }
    var endDate by remember { mutableStateOf(defaultEnd) }
    var registrationDeadline by remember { mutableStateOf(defaultDeadline) }
    var maxParticipants by remember { mutableStateOf("32") }
    var registrationFormat by remember { mutableStateOf("Open Registration") }

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
            Step2HeaderSection()

            // START & END DATES (Lado a lado com Calendário)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    SectionLabel("START DATE")
                    Spacer(modifier = Modifier.height(8.dp))
                    DatePickerField(
                        value = startDate,
                        maxDate = null, // CORRIGIDO: Retirámos o bloqueio! O utilizador pode escolher qualquer data futura
                        onValueChange = { newDate ->
                            startDate = newDate

                            try {
                                val sdf = SimpleDateFormat("dd/MM/yy", Locale.getDefault()).apply {
                                    timeZone = TimeZone.getTimeZone("UTC")
                                }
                                val parsedStart = sdf.parse(newDate)

                                if (parsedStart != null) {
                                    // 1. Atualizar automaticamente o Deadline para 1 dia antes da nova Start Date
                                    val deadlineCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                        time = parsedStart
                                        add(Calendar.DAY_OF_YEAR, -1)
                                    }
                                    registrationDeadline = sdf.format(deadlineCal.time)

                                    // 2. CORRIGIDO: Se a nova Start Date for maior ou igual à End Date atual, empurra a End Date para a frente
                                    val parsedEnd = sdf.parse(endDate)
                                    if (parsedEnd != null && parsedStart.time >= parsedEnd.time) {
                                        val endCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                            time = parsedStart
                                            add(Calendar.DAY_OF_YEAR, 2) // Ajusta para terminar 2 dias depois da nova data de início
                                        }
                                        endDate = sdf.format(endCal.time)
                                    }
                                }
                            } catch (e: Exception) { }
                        },
                        icon = Icons.Default.DateRange
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    SectionLabel("END DATE")
                    Spacer(modifier = Modifier.height(8.dp))
                    DatePickerField(
                        value = endDate,
                        minDate = startDate, // Mantemos o bloqueio para a End Date não ser escolhida antes da Start Date manualmente
                        onValueChange = { endDate = it },
                        icon = Icons.Default.DateRange
                    )
                }
            }

            // REGISTRATION DEADLINE (Com Calendário)
            Column {
                SectionLabel("REGISTRATION DEADLINE")
                Spacer(modifier = Modifier.height(8.dp))
                DatePickerField(
                    value = registrationDeadline,
                    maxDate = startDate, // Permite escolher até ao próprio dia de início (Start Date) inclusive
                    onValueChange = { registrationDeadline = it },
                    icon = Icons.Default.DateRange
                )
            }

            // MAX PARTICIPANTS
            Column {
                SectionLabel("MAX PARTICIPANTS")
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = maxParticipants,
                    onValueChange = { maxParticipants = it },
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
                SectionLabel("REGISTRATION FORMAT")
                Spacer(modifier = Modifier.height(8.dp))

                RegistrationFormatCard(
                    title = "Open Registration",
                    description = "Any team can apply directly through the platform.",
                    icon = Icons.Default.Person,
                    isSelected = registrationFormat == "Open Registration",
                    onClick = { registrationFormat = "Open Registration" }
                )
                Spacer(modifier = Modifier.height(12.dp))
                RegistrationFormatCard(
                    title = "Invite Only",
                    description = "You manually invite teams to participate. Add invitees from the tournament card after creation.",
                    icon = Icons.Default.Lock,
                    isSelected = registrationFormat == "Invite Only",
                    onClick = { registrationFormat = "Invite Only" }
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

            // Progress Bar (2 verdes, 2 cinzentos)
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

@Preview(showBackground = true)
@Composable
fun CreateTournamentStep2ScreenPreview() {
    MaterialTheme {
        CreateTournamentStep2Screen()
    }
}