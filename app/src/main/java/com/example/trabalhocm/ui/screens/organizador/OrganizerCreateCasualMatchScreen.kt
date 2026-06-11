package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.repository.PeladinhaRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerCreateCasualMatchScreen(
    onBackClick: () -> Unit = {},
    onPublishClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { PeladinhaRepository() }
    val scope = rememberCoroutineScope()

    var selectedSport by remember { mutableStateOf("Volleyball") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var matchLevel by remember { mutableStateOf("Intermediary") }
    var openings by remember { mutableStateOf("10") }
    var registrationType by remember { mutableStateOf("Open Registration") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Match", color = Color.White, fontWeight = FontWeight.Bold) },
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
                selectedTab = "MATCHES",
                onHomeClick = onHomeClick,
                onTournamentsClick = onTournamentsClick,
                onMatchesClick = onMatchesClick,
                onTeamsClick = onTeamsClick,
                onProfileClick = onProfileClick
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Column {
                Text(
                    text = "Create a casual match",
                    color = DarkBlue,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Organize a quick game with the community.",
                    color = TextGray,
                    fontSize = 14.sp
                )
            }

            Column {
                CasualSectionLabel("SPORTS CATEGORY")

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SportSelectionCard(
                        modifier = Modifier.weight(1f),
                        title = "Football",
                        icon = Icons.Outlined.Star,
                        isSelected = selectedSport == "Football",
                        onClick = { selectedSport = "Football" }
                    )

                    SportSelectionCard(
                        modifier = Modifier.weight(1f),
                        title = "Volleyball",
                        icon = Icons.Outlined.Star,
                        isSelected = selectedSport == "Volleyball",
                        onClick = { selectedSport = "Volleyball" }
                    )

                    SportSelectionCard(
                        modifier = Modifier.weight(1f),
                        title = "Basketball",
                        icon = Icons.Outlined.Star,
                        isSelected = selectedSport == "Basketball",
                        onClick = { selectedSport = "Basketball" }
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Date",
                        color = DarkBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = date,
                        onValueChange = { date = it },
                        placeholder = { Text("dd / mm / aaaa", color = TextGray) },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.DateRange,
                                contentDescription = null,
                                tint = DarkBlue
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = InputBg,
                            unfocusedContainerColor = InputBg,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Time",
                        color = DarkBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = time,
                        onValueChange = { time = it },
                        placeholder = { Text("--:--", color = TextGray) },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Face,
                                contentDescription = "Clock",
                                tint = DarkBlue
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = InputBg,
                            unfocusedContainerColor = InputBg,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        )
                    )
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Localization",
                        color = DarkBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        placeholder = { Text("Address, club, etc.", color = TextGray) },
                        leadingIcon = {
                            Icon(
                                Icons.Outlined.Place,
                                contentDescription = null,
                                tint = DarkBlue
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Outlined.Place,
                                contentDescription = null,
                                tint = TextGray,
                                modifier = Modifier.size(32.dp)
                            )

                            Text(
                                text = "Map Preview",
                                color = TextGray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Match Level",
                        color = DarkBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LevelChip(
                            modifier = Modifier.weight(1f),
                            title = "Beginner",
                            isSelected = matchLevel == "Beginner"
                        ) {
                            matchLevel = "Beginner"
                        }

                        LevelChip(
                            modifier = Modifier.weight(1.2f),
                            title = "Intermediary",
                            isSelected = matchLevel == "Intermediary"
                        ) {
                            matchLevel = "Intermediary"
                        }

                        LevelChip(
                            modifier = Modifier.weight(1f),
                            title = "Advanced",
                            isSelected = matchLevel == "Advanced"
                        ) {
                            matchLevel = "Advanced"
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Job Openings Available",
                        color = DarkBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = openings,
                            onValueChange = { openings = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(80.dp),
                            shape = RoundedCornerShape(8.dp),
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            )
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = "required players",
                            color = TextGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Column {
                CasualRegistrationCard(
                    title = "Open Registration",
                    description = "Any team can apply directly through the platform.",
                    icon = Icons.Outlined.Person,
                    isSelected = registrationType == "Open Registration",
                    onClick = { registrationType = "Open Registration" }
                )

                Spacer(modifier = Modifier.height(12.dp))

                CasualRegistrationCard(
                    title = "Invite Only",
                    description = "You manually invite teams to participate. Add invitees from the tournament card after creation.",
                    icon = Icons.Outlined.Lock,
                    isSelected = registrationType == "Invite Only",
                    onClick = { registrationType = "Invite Only" }
                )
            }

            if (errorMessage.isNotBlank()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            Button(
                onClick = {
                    errorMessage = ""

                    val dataFormatada = formatarDataParaSupabase(date)
                    val horaFormatada = formatarHoraParaSupabase(time)
                    val maxJogadores = openings.toIntOrNull() ?: 0

                    if (dataFormatada == null) {
                        errorMessage = "Indica a data no formato dd/mm/aaaa."
                        return@Button
                    }

                    if (horaFormatada == null) {
                        errorMessage = "Indica a hora no formato hh:mm."
                        return@Button
                    }

                    scope.launch {
                        isLoading = true

                        repository.criarPeladinha(
                            idModalidade = obterIdModalidadeCasual(selectedSport),
                            data = dataFormatada,
                            hora = horaFormatada,
                            local = location,
                            maxJogadores = maxJogadores,
                            nivel = matchLevel,
                            tipoInscricao = registrationType
                        ).onSuccess {
                            isLoading = false
                            onPublishClick()
                        }.onFailure { erro ->
                            isLoading = false
                            errorMessage = erro.message ?: "Erro ao criar partida casual."
                        }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Outlined.AddCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Publish Match",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private @Composable
fun CasualSectionLabel(text: String) {
    Text(
        text = text,
        color = TextGray,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
}

private @Composable
fun SportSelectionCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) TealGreen else Color(0xFFE2E8F0)

    Card(
        modifier = modifier
            .height(80.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) TealGreen else DarkBlue,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = DarkBlue,
                fontSize = 12.sp
            )
        }
    }
}

private @Composable
fun LevelChip(
    modifier: Modifier = Modifier,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) Color(0xFFBAE6FD) else CardBg
    val borderColor = if (isSelected) PrimaryBlue else Color(0xFFE2E8F0)

    Surface(
        modifier = modifier.clickable { onClick() },
        color = bgColor,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Text(
            text = title,
            color = DarkBlue,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 10.dp)
        )
    }
}

private @Composable
fun CasualRegistrationCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) TealGreen else Color(0xFFE2E8F0)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isSelected) TealGreen.copy(alpha = 0.1f) else InputBg,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) TealGreen else PrimaryBlue,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = DarkBlue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    color = TextGray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = TealGreen
                )
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

private fun obterIdModalidadeCasual(
    selectedSport: String
): Long {
    return when (selectedSport) {
        "Football" -> 1L
        "Basketball" -> 2L
        "Volleyball" -> 3L
        else -> 1L
    }
}

private fun formatarDataParaSupabase(
    value: String
): String? {
    val normalizada = value
        .trim()
        .replace(" ", "")

    val formatosEntrada = listOf(
        "dd/MM/yyyy",
        "dd-MM-yyyy",
        "dd/MM/yy",
        "dd-MM-yy"
    )

    val output = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    formatosEntrada.forEach { formato ->
        val input = SimpleDateFormat(formato, Locale.getDefault())
        input.isLenient = false

        val data = runCatching {
            input.parse(normalizada)
        }.getOrNull()

        if (data != null) {
            return output.format(data)
        }
    }

    return null
}

private fun formatarHoraParaSupabase(
    value: String
): String? {
    val normalizada = value
        .trim()
        .replace(" ", "")

    val regex = Regex("^([01]?\\d|2[0-3]):[0-5]\\d$")

    if (!regex.matches(normalizada)) {
        return null
    }

    return normalizada.take(5)
}

@Preview(showBackground = true)
@Composable
fun OrganizerCreateCasualMatchScreenPreview() {
    MaterialTheme {
        OrganizerCreateCasualMatchScreen()
    }
}