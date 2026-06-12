package com.example.trabalhocm.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.repository.EquipaComInfo
import com.example.trabalhocm.data.repository.EquipaRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import kotlinx.coroutines.launch

@Composable
fun PlayerTeamsScreen(
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onTeamDetailsClick: (Long) -> Unit = {},
    onManageTeamClick: (Long) -> Unit = {},
    onCreateTeamClick: () -> Unit = {}
) {
    val repository = remember { EquipaRepository() }
    val scope = rememberCoroutineScope()

    var search by remember { mutableStateOf("") }

    var teams by remember { mutableStateOf<List<EquipaComInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    // Serve para saber qual botão de "JOIN" tem a rodinha de loading a girar
    var actionLoadingId by remember { mutableStateOf<Long?>(null) }

    fun carregarEquipas() {
        scope.launch {
            isLoading = true
            errorMessage = ""
            repository.listarEquipasComInfo()
                .onSuccess { teams = it }
                .onFailure { errorMessage = it.message ?: "Erro ao carregar equipas." }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        carregarEquipas()
    }

    val filteredTeams = teams.filter { team ->
        search.isBlank() ||
                team.equipa.nome.contains(search, ignoreCase = true) ||
                team.modalidadeNome.contains(search, ignoreCase = true) ||
                team.cidade.contains(search, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        PlayerTeamsTopBar(onNotificationsClick = onNotificationsClick)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 18.dp)
        ) {
            Text(
                text = "PREMIER LEAGUE TEAMS",
                color = Color(0xFF0757C8),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.8.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Browse Teams",
                color = BrandBlue,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Discover all active teams across the league\necosystem.",
                color = Color(0xFF6D7486),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Search for teams...",
                        color = Color(0xFF9EA4B3),
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Text(
                        text = "⌕",
                        color = Color(0xFF8D94A3),
                        fontSize = 17.sp
                    )
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                shape = RoundedCornerShape(7.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BrandWhite,
                    unfocusedContainerColor = BrandWhite,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = BrandGreen,
                    focusedTextColor = BrandBlue,
                    unfocusedTextColor = BrandBlue
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = BrandGreen
                        )
                    }
                }

                errorMessage.isNotBlank() -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "Erro: $errorMessage",
                            color = Color(0xFFD01818),
                            fontSize = 13.sp,
                            modifier = Modifier.padding(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                filteredTeams.isEmpty() -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = BrandWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                    ) {
                        Text(
                            text = "Não existem equipas com estes filtros.",
                            color = BrandBlue,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                }

                else -> {
                    filteredTeams.forEach { team ->
                        PlayerTeamCard(
                            team = team,
                            isSubmitting = actionLoadingId == team.equipa.id,
                            onDetailsClick = {
                                onTeamDetailsClick(team.equipa.id)
                            },
                            onManageTeamClick = {
                                onManageTeamClick(team.equipa.id)
                            },
                            onJoinClick = {
                                scope.launch {
                                    actionLoadingId = team.equipa.id
                                    repository.solicitarEntradaEquipa(team.equipa.id, team.tipoEntrada)
                                        .onSuccess { carregarEquipas() }
                                    actionLoadingId = null
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }

            Button(
                onClick = onCreateTeamClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0757C8),
                    contentColor = BrandWhite
                )
            ) {
                Text(
                    text = "⊙  CREATE TEAM",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        MatchLeagueBottomBar(
            selectedTab = "TEAMS",
            onHomeClick = onHomeClick,
            onTournamentsClick = onTournamentsClick,
            onMatchesClick = onMatchesClick,
            onTeamsClick = onTeamsClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun PlayerTeamsTopBar(onNotificationsClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(BrandBlue)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Teams",
            color = BrandWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = "Notifications",
            tint = BrandWhite,
            modifier = Modifier
                .size(26.dp)
                .clickable { onNotificationsClick() }
        )
    }
}

@Composable
fun PlayerTeamCard(
    team: EquipaComInfo,
    isSubmitting: Boolean,
    onDetailsClick: () -> Unit,
    onManageTeamClick: () -> Unit,
    onJoinClick: () -> Unit
) {
    val logoColor = teamColorFromName(team.equipa.nome)
    val isPublic = team.tipoEntrada.lowercase() == "publica"
    val estadoConvite = team.estadoConviteAtual?.lowercase()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (team.utilizadorPertence) 1.dp else 0.dp,
                color = if (team.utilizadorPertence) BrandGreen else Color.Transparent,
                shape = RoundedCornerShape(7.dp)
            ),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                TeamLogoBox(
                    text = team.iniciais,
                    color = logoColor
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = team.equipa.nome,
                        color = BrandBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = when {
                            team.utilizadorCapitao -> {
                                "YOUR TEAM · CAPTAIN · ${team.divisao}".uppercase()
                            }

                            team.utilizadorPertence -> {
                                "YOUR TEAM · ${team.divisao}".uppercase()
                            }

                            else -> {
                                team.divisao.uppercase()
                            }
                        },
                        color = if (team.utilizadorPertence) BrandGreen else Color(0xFF0757C8),
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                // Public / Private Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isPublic) BrandGreen.copy(alpha = 0.1f) else Color(0xFFE53935).copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isPublic) "PUBLIC 🔓" else "PRIVATE 🔒",
                        color = if (isPublic) BrandGreen else Color(0xFFFF8A80),
                        fontSize = 7.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TeamStat(
                    label = "WINS",
                    value = team.vitorias.toString(),
                    valueColor = Color(0xFF0757C8)
                )

                TeamStat(
                    label = "LOSSES",
                    value = team.derrotas.toString(),
                    valueColor = BrandBlue
                )

                TeamStat(
                    label = "STREAK",
                    value = team.streak,
                    valueColor = if (team.streakGood) BrandGreen else Color(0xFFE53935)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDetailsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                    shape = RoundedCornerShape(2.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF0757C8)
                    )
                ) {
                    Text(
                        text = "VIEW DETAILS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (team.utilizadorCapitao) {
                    Button(
                        onClick = onManageTeamClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp),
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandGreen,
                            contentColor = BrandWhite
                        )
                    ) {
                        Text(
                            text = "MANAGE TEAM",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else if (!team.utilizadorPertence && estadoConvite != "aceite") {
                    if (estadoConvite == "pendente") {
                        Button(
                            onClick = { },
                            enabled = false,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            shape = RoundedCornerShape(2.dp),
                            colors = ButtonDefaults.buttonColors(
                                disabledContainerColor = Color(0xFFDDE1EA),
                                disabledContentColor = Color(0xFF7D8497)
                            )
                        ) {
                            Text(
                                text = "PENDING",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Button(
                            onClick = onJoinClick,
                            enabled = !isSubmitting,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            shape = RoundedCornerShape(2.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandGreen,
                                contentColor = BrandWhite
                            )
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    color = BrandWhite,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = if (isPublic) "JOIN TEAM" else "REQUEST",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TeamLogoBox(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(color.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text.take(3).uppercase(),
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TeamStat(
    label: String,
    value: String,
    valueColor: Color
) {
    Column {
        Text(
            text = label,
            color = Color(0xFF7D8497),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = value,
            color = valueColor,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun teamColorFromName(nome: String): Color {
    val nomeNormalizado = nome.lowercase()

    return when {
        nomeNormalizado.contains("benfica") -> Color(0xFFE53935)
        nomeNormalizado.contains("porto") -> Color(0xFF0757C8)
        nomeNormalizado.contains("sporting") -> BrandGreen
        nomeNormalizado.contains("vianense") -> Color(0xFFD19A00)
        nomeNormalizado.contains("mancos") -> Color(0xFF4A555C)
        else -> Color(0xFF49617F)
    }
}

@Preview(showBackground = true, name = "Player Teams Screen")
@Composable
fun PlayerTeamsScreenPreview() {
    PlayerTeamsScreen()
}