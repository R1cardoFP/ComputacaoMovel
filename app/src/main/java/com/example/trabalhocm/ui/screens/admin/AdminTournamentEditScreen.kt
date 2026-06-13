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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.trabalhocm.R
import com.example.trabalhocm.data.model.AdminTournamentDetails
import com.example.trabalhocm.data.repository.AdminTournamentRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.AppIcons
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.CardBg
import com.example.trabalhocm.ui.theme.DarkBlue
import com.example.trabalhocm.ui.theme.ErrorRed
import com.example.trabalhocm.ui.theme.InputBg
import com.example.trabalhocm.ui.theme.PrimaryBlue
import com.example.trabalhocm.ui.theme.TealGreen
import com.example.trabalhocm.ui.theme.LightBlueBadge
import com.example.trabalhocm.ui.theme.TextGray
import kotlinx.coroutines.launch
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTournamentEditScreen(
    tournamentId: String,
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSaveSuccess: () -> Unit = {},
    onCancelSuccess: () -> Unit = {},
    onDiscardChangesClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { AdminTournamentRepository() }
    val scope = rememberCoroutineScope()

    var originalDetails by remember { mutableStateOf<AdminTournamentDetails?>(null) }

    var nome by remember { mutableStateOf("") }
    var modalidade by remember { mutableStateOf("") }
    var dataInicio by remember { mutableStateOf("") }
    var dataFim by remember { mutableStateOf("") }
    var formato by remember { mutableStateOf("") }
    var local by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var premio by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("open") }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var actionMessage by remember { mutableStateOf("") }
    var actionMessageIsError by remember { mutableStateOf(false) }

    val errorLoadingTournamentText = stringResource(R.string.admin_tournament_edit_error_loading)
    val nameEmptyErrorText = stringResource(R.string.admin_tournament_edit_name_empty_error)
    val saveSuccessText = stringResource(R.string.admin_tournament_edit_save_success)
    val saveErrorText = stringResource(R.string.admin_tournament_edit_save_error)

    fun preencherCampos(details: AdminTournamentDetails) {
        nome = details.nome
        modalidade = details.modalidade
        dataInicio = details.dataInicio
        dataFim = details.dataFim
        formato = details.formato
        local = details.local
        descricao = details.descricao
        premio = details.premio
        estado = details.estado
    }

    LaunchedEffect(tournamentId) {
        repository.obterDetalhesTorneio(tournamentId)
            .onSuccess {
                originalDetails = it
                preencherCampos(it)
            }
            .onFailure {
                errorMessage = "$errorLoadingTournamentText: ${it.message}"
            }

        isLoading = false
    }

    val modalidadeEditavel = when (originalDetails?.estado?.lowercase()?.trim()) {
        "rascunho", "aberto", "draft", "open" -> true
        else -> false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = AppIcons.Back,
                            contentDescription = stringResource(R.string.admin_common_back),
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.admin_tournament_edit_top_title),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = AppIcons.Notifications,
                            contentDescription = stringResource(R.string.admin_common_notifications),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        bottomBar = {
            MatchLeagueBottomBar(
                selectedTab = "TOURNAMENTS",
                onHomeClick = onHomeClick,
                onTournamentsClick = onTournamentsClick,
                onMatchesClick = onMatchesClick,
                onTeamsClick = onTeamsClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = BgLight
    ) { innerPadding ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TealGreen)
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

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentPadding = PaddingValues(
                        start = 24.dp,
                        end = 24.dp,
                        top = 20.dp,
                        bottom = 32.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Column {
                            Text(
                                text = stringResource(R.string.admin_tournament_edit_console).uppercase(),
                                color = TealGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = stringResource(R.string.admin_tournament_edit_title),
                                color = DarkBlue,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = stringResource(R.string.admin_tournament_edit_description),
                                color = TextGray,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }

                    item {
                        EditHeroCard(
                            nome = nome,
                            modalidade = modalidade,
                            estado = estado,
                            season = originalDetails?.season ?: "--/--",
                            premio = premio,
                            teams = originalDetails?.teamsCount?.toString() ?: "0",
                            defaultSportText = stringResource(R.string.admin_tournament_edit_default_sport),
                            defaultTournamentNameText = stringResource(R.string.admin_tournament_edit_default_tournament_name)
                        )
                    }

                    item {
                        EditSectionCard(title = stringResource(R.string.admin_tournament_edit_section_identity)) {
                            EditInput(
                                label = stringResource(R.string.admin_tournament_edit_label_tournament_name).uppercase(),
                                value = nome,
                                onValueChange = {
                                    nome = it
                                }
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.admin_tournament_edit_label_sport).uppercase(),
                                    color = TextGray,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )

                                if (!modalidadeEditavel) {
                                    Text(
                                        text = stringResource(R.string.admin_tournament_edit_locked).uppercase(),
                                        color = ErrorRed,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.8.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                EditChip(
                                    text = stringResource(R.string.admin_teams_filter_football),
                                    selected = modalidade.equals("Futebol", true) ||
                                            modalidade.contains("fut", true),
                                    enabled = modalidadeEditavel
                                ) {
                                    modalidade = "Futebol"
                                }

                                EditChip(
                                    text = stringResource(R.string.admin_teams_filter_basketball),
                                    selected = modalidade.equals("Basquetebol", true) ||
                                            modalidade.contains("basquet", true) ||
                                            modalidade.contains("basket", true),
                                    enabled = modalidadeEditavel
                                ) {
                                    modalidade = "Basquetebol"
                                }

                                EditChip(
                                    text = stringResource(R.string.admin_teams_filter_volleyball),
                                    selected = modalidade.equals("Voleibol", true) ||
                                            modalidade.contains("volei", true) ||
                                            modalidade.contains("volley", true),
                                    enabled = modalidadeEditavel
                                ) {
                                    modalidade = "Voleibol"
                                }
                            }

                            if (!modalidadeEditavel) {
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = stringResource(R.string.admin_tournament_edit_sport_locked_message),
                                    color = TextGray,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = stringResource(R.string.admin_tournament_edit_label_format).uppercase(),
                                color = TextGray,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                EditChip(
                                    text = stringResource(R.string.admin_tournament_edit_format_league),
                                    selected = formato.equals("liga", true) ||
                                            formato.contains("league", true)
                                ) {
                                    formato = "liga"
                                }

                                EditChip(
                                    text = stringResource(R.string.admin_tournament_edit_format_knockout),
                                    selected = formato.equals("eliminatorias", true) ||
                                            formato.contains("knockout", true)
                                ) {
                                    formato = "eliminatorias"
                                }

                                EditChip(
                                    text = stringResource(R.string.admin_tournament_edit_format_groups),
                                    selected = formato.equals("grupos", true) ||
                                            formato.contains("group", true)
                                ) {
                                    formato = "grupos"
                                }
                            }
                        }
                    }

                    item {
                        EditSectionCard(title = stringResource(R.string.admin_tournament_edit_section_schedule)) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                DateInput(
                                    label = stringResource(R.string.admin_tournament_edit_label_start_date).uppercase(),
                                    value = dataInicio,
                                    onDateSelected = {
                                        dataInicio = it
                                    },
                                    modifier = Modifier.weight(1f)
                                )

                                DateInput(
                                    label = stringResource(R.string.admin_tournament_edit_label_end_date).uppercase(),
                                    value = dataFim,
                                    onDateSelected = {
                                        dataFim = it
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    item {
                        EditSectionCard(title = stringResource(R.string.admin_tournament_edit_section_location_organizer)) {
                            EditInput(
                                label = stringResource(R.string.admin_tournament_edit_label_venue).uppercase(),
                                value = local,
                                onValueChange = {
                                    local = it
                                }
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            EditInput(
                                label = stringResource(R.string.admin_tournament_edit_label_organizer).uppercase(),
                                value = originalDetails?.organizerName ?: "",
                                onValueChange = {},
                                enabled = false
                            )
                        }
                    }

                    item {
                        EditSectionCard(title = stringResource(R.string.admin_tournament_edit_section_description_rules)) {
                            EditInput(
                                label = stringResource(R.string.admin_tournament_edit_label_description_rules).uppercase(),
                                value = descricao,
                                onValueChange = {
                                    descricao = it
                                },
                                singleLine = false,
                                height = 94.dp
                            )
                        }
                    }

                    item {
                        EditSectionCard(title = stringResource(R.string.admin_tournament_edit_section_prize_pool)) {
                            EditInput(
                                label = stringResource(R.string.admin_tournament_edit_label_prize_pool).uppercase(),
                                value = premio,
                                onValueChange = {
                                    premio = it
                                },
                                keyboardType = KeyboardType.Number
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = stringResource(R.string.admin_tournament_edit_status_auto_message),
                                color = TextGray,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }

                    if (actionMessage.isNotBlank()) {
                        item {
                            EditMessageCard(
                                message = actionMessage,
                                isError = actionMessageIsError
                            )
                        }
                    }

                    item {
                        EditAdminActionsCard(
                            isSaving = isSaving,
                            adminActionsText = stringResource(R.string.admin_tournament_edit_admin_actions).uppercase(),
                            savingText = stringResource(R.string.admin_tournament_edit_saving).uppercase(),
                            saveChangesText = stringResource(R.string.admin_tournament_edit_save_changes).uppercase(),
                            discardChangesText = stringResource(R.string.admin_tournament_edit_discard_changes).uppercase(),
                            onSaveClick = {
                                scope.launch {
                                    isSaving = true
                                    actionMessage = ""

                                    if (nome.isBlank()) {
                                        actionMessage = nameEmptyErrorText
                                        actionMessageIsError = true
                                        isSaving = false
                                        return@launch
                                    }

                                    repository.atualizarTorneioAdmin(
                                        tournamentId = tournamentId,
                                        nome = nome.trim(),
                                        modalidade = modalidade.trim(),
                                        dataInicio = dataInicio.trim(),
                                        dataFim = dataFim.trim(),
                                        formato = formato.trim(),
                                        local = local.trim(),
                                        descricao = descricao.trim(),
                                        premio = premio.trim(),
                                        estado = estado.trim()
                                    )
                                        .onSuccess {
                                            actionMessage = saveSuccessText
                                            actionMessageIsError = false
                                            onSaveSuccess()
                                        }
                                        .onFailure {
                                            actionMessage = "$saveErrorText: ${it.message}"
                                            actionMessageIsError = true
                                        }

                                    isSaving = false
                                }
                            },
                            onDiscardClick = {
                                onDiscardChangesClick()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EditHeroCard(
    nome: String,
    modalidade: String,
    estado: String,
    season: String,
    premio: String,
    teams: String,
    defaultSportText: String,
    defaultTournamentNameText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EditHeroBadge(
                    text = modalidade.ifBlank { defaultSportText },
                    background = LightBlueBadge,
                    textColor = Color(0xFF0057C8)
                )

                EditHeroBadge(
                    text = translatedEditStatus(estado),
                    background = TealGreen,
                    textColor = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = nome.ifBlank { defaultTournamentNameText },
                color = Color.White,
                fontSize = 19.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                EditHeroStat(stringResource(R.string.admin_tournament_details_season).uppercase(), season)
                EditHeroStat(stringResource(R.string.admin_tournament_details_prize_pool).uppercase(), premio)
                EditHeroStat(stringResource(R.string.admin_tournament_details_teams).uppercase(), teams)
            }
        }
    }
}


@Composable
private fun translatedEditStatus(status: String): String {
    val normalized = status.lowercase()

    return when {
        normalized.contains("aberto") || normalized.contains("open") ->
            stringResource(R.string.admin_status_open).uppercase()

        normalized.contains("decorrer") || normalized.contains("live") ->
            stringResource(R.string.admin_status_live).uppercase()

        normalized.contains("terminado") || normalized.contains("completed") || normalized.contains("archived") ->
            stringResource(R.string.admin_status_completed).uppercase()

        normalized.contains("cancelado") ->
            stringResource(R.string.admin_status_cancelled).uppercase()

        normalized.contains("rascunho") || normalized.contains("draft") ->
            stringResource(R.string.admin_status_draft).uppercase()

        else -> status.uppercase()
    }
}

@Composable
private fun EditHeroBadge(
    text: String,
    background: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .padding(horizontal = 9.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.uppercase(),
            color = textColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EditHeroStat(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            color = Color(0xFFB9C4D8),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = value,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EditSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = title,
                color = DarkBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

@Composable
private fun EditInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    height: Dp = 56.dp,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            color = TextGray,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(5.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(height),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBg,
                unfocusedContainerColor = InputBg,
                disabledContainerColor = InputBg,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                focusedTextColor = DarkBlue,
                unfocusedTextColor = DarkBlue,
                disabledTextColor = TextGray,
                cursorColor = TealGreen
            )
        )
    }
}

@Composable
private fun EditChip(
    text: String,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        selected -> PrimaryBlue
        enabled -> InputBg
        else -> Color(0xFFE5E7EB)
    }

    val textColor = when {
        selected -> Color.White
        enabled -> PrimaryBlue
        else -> Color(0xFF9AA5B5)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(backgroundColor)
            .clickable(enabled = enabled) {
                onClick()
            }
            .padding(horizontal = 12.dp, vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EditAdminActionsCard(
    isSaving: Boolean,
    adminActionsText: String,
    savingText: String,
    saveChangesText: String,
    discardChangesText: String,
    onSaveClick: () -> Unit,
    onDiscardClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = adminActionsText,
                color = TextGray,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onSaveClick,
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (isSaving) savingText else saveChangesText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onDiscardClick,
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, ErrorRed),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = ErrorRed
                )
            ) {
                Text(
                    text = discardChangesText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AdminEditTopBar(
    title: String,
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
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Icon(
            imageVector = AppIcons.Notifications,
            contentDescription = stringResource(R.string.admin_common_notifications),
            tint = Color.White,
            modifier = Modifier
                .size(23.dp)
                .clickable {
                    onNotificationsClick()
                }
        )
    }
}

@Composable
private fun AdminEditBottomBar(
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
            .background(Color.White)
            .navigationBarsPadding()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        EditBottomItem(AppIcons.Home, stringResource(R.string.admin_nav_home).uppercase(), selected == "home", onHomeClick)
        EditBottomItem(AppIcons.Tournaments, stringResource(R.string.admin_nav_tournaments).uppercase(), selected == "tournaments", onTournamentsClick)
        EditBottomItem(AppIcons.Games, stringResource(R.string.admin_nav_matches).uppercase(), selected == "matches", onMatchesClick)
        EditBottomItem(AppIcons.Teams, stringResource(R.string.admin_nav_teams).uppercase(), selected == "teams", onTeamsClick)
        EditBottomItem(AppIcons.Profile, stringResource(R.string.admin_nav_profile).uppercase(), selected == "profile", onProfileClick)
    }
}

@Composable
private fun EditBottomItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) Color(0xFF0057C8) else Color(0xFF9AA5B5)

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

@Composable
private fun EditMessageCard(
    message: String,
    isError: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) Color(0xFFFFF1F2) else Color(0xFFEFFCF6)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isError) ErrorRed.copy(alpha = 0.12f) else TealGreen.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isError) "!" else "✓",
                    color = if (isError) ErrorRed else TealGreen,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = message,
                color = if (isError) ErrorRed else DarkBlue,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTournamentEditScreenPreview() {
    AdminTournamentEditScreen(
        tournamentId = "1"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateInput(
    label: String,
    value: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            color = TextGray,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(5.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(InputBg)
                .clickable {
                    showPicker = true
                }
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = value.ifBlank { stringResource(R.string.admin_tournament_edit_select_date) },
                color = DarkBlue,
                fontSize = 13.sp
            )
        }
    }

    if (showPicker) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = {
                showPicker = false
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis

                        if (millis != null) {
                            val date = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()

                            onDateSelected(date.toString())
                        }

                        showPicker = false
                    }
                ) {
                    Text(text = stringResource(R.string.admin_tournament_edit_confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPicker = false
                    }
                ) {
                    Text(text = stringResource(R.string.admin_common_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}