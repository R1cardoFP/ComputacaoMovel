package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.trabalhocm.data.repository.EquipaSimples

private val DarkBlue = Color(0xFF111827)
private val TealGreen = Color(0xFF008D7D)
private val ErrorRed = Color(0xFFDC2626)
private val TextGray = Color(0xFF64748B)
private val BgLight = Color(0xFFF8FAFC)
private val CardBg = Color(0xFFFFFFFF)
private val InputBg = Color(0xFFF1F5F9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMatchScreen(
    idTorneio: Long,
    viewModel: CreateMatchViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onMatchCreated: (Long) -> Unit = {}
) {
    LaunchedEffect(idTorneio) {
        viewModel.carregar(idTorneio)
    }

    val msgSelectBoth = stringResource(R.string.msg_select_both_teams)
    val msgMissingDate = stringResource(R.string.msg_missing_datetime)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_create_match), color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.desc_back), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        containerColor = BgLight
    ) { paddingValues ->
        if (viewModel.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TealGreen)
            }
            return@Scaffold
        }

        if (viewModel.equipas.size < 2) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(24.dp), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.msg_need_two_teams), color = TextGray, fontSize = 14.sp)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            TeamDropdown(
                label = stringResource(R.string.label_home_team),
                selected = viewModel.equipaCasa,
                options = viewModel.equipas,
                onSelect = { viewModel.equipaCasa = it }
            )

            TeamDropdown(
                label = stringResource(R.string.label_away_team),
                selected = viewModel.equipaFora,
                options = viewModel.equipas,
                onSelect = { viewModel.equipaFora = it }
            )

            LabeledField(stringResource(R.string.label_match_date), viewModel.data, "2026-07-15") { viewModel.data = it }
            LabeledField(stringResource(R.string.label_match_time), viewModel.hora, "18:30") { viewModel.hora = it }
            LabeledField(stringResource(R.string.label_match_location), viewModel.local, "Estádio Municipal") { viewModel.local = it }

            if (viewModel.errorMessage.isNotBlank()) {
                val texto = when (viewModel.errorMessage) {
                    "SELECT_BOTH" -> msgSelectBoth
                    "MISSING_DATE" -> msgMissingDate
                    else -> viewModel.errorMessage
                }
                Text(texto, color = ErrorRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { viewModel.criarJogo(onSuccess = onMatchCreated) },
                enabled = !viewModel.isSaving,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealGreen),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                if (viewModel.isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.btn_create_match), fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 1.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TeamDropdown(
    label: String,
    selected: EquipaSimples?,
    options: List<EquipaSimples>,
    onSelect: (EquipaSimples) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(label, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(6.dp))
        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
            TextField(
                value = selected?.nome ?: stringResource(R.string.btn_select_team),
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).menuAnchor(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = InputBg, unfocusedContainerColor = InputBg,
                    focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = DarkBlue)
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { equipa ->
                    DropdownMenuItem(
                        text = { Text(equipa.nome, fontSize = 14.sp) },
                        onClick = {
                            onSelect(equipa)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(label, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(6.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = TextGray, fontSize = 13.sp) },
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = InputBg, unfocusedContainerColor = InputBg,
                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
            ),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp, color = DarkBlue),
            singleLine = true
        )
    }
}
