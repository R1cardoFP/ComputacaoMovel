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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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

@Composable
fun PlayerTeamsScreen(
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onTeamDetailsClick: (Long) -> Unit = {},
    onManageTeamClick: (Long) -> Unit = {},
    onCreateTeamClick: () -> Unit = {}
) {
    val repository = remember { EquipaRepository() }

    var selectedDivision by remember { mutableStateOf("All Teams") }
    var search by remember { mutableStateOf("") }

    var teams by remember { mutableStateOf<List<EquipaComInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        isLoading = true
        errorMessage = ""

        repository.listarEquipasComInfo()
            .onSuccess {
                teams = it
            }
            .onFailure {
                errorMessage = it.message ?: "Erro ao carregar equipas."
            }

        isLoading = false
    }

    val filteredTeams = teams.filter { team ->
        val matchesDivision =
            selectedDivision == "All Teams" ||
                    team.divisao.equals(selectedDivision, ignoreCase = true)

        val matchesSearch =
            search.isBlank() ||
                    team.equipa.nome.contains(search, ignoreCase = true) ||
                    team.modalidadeNome.contains(search, ignoreCase = true) ||
                    team.cidade.contains(search, ignoreCase = true)

        matchesDivision && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        PlayerTeamsTopBar()

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

            Spacer(modifier = Modifier.height(14.dp))

            TeamsDivisionTabs(
                selectedDivision = selectedDivision,
                onDivisionSelected = { selectedDivision = it }
            )

            Spacer(modifier = Modifier.height(18.dp))

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
                            onDetailsClick = {
                                onTeamDetailsClick(team.equipa.id)
                            },
                            onManageTeamClick = {
                                onManageTeamClick(team.equipa.id)
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
fun PlayerTeamsTopBar() {
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

        Text(
            text = "♧",
            color = BrandWhite,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TeamsDivisionTabs(
    selectedDivision: String,
    onDivisionSelected: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        TeamDivisionButton(
            text = "All Teams",
            selected = selectedDivision == "All Teams",
            onClick = { onDivisionSelected("All Teams") }
        )

        TeamDivisionButton(
            text = "Division A",
            selected = selectedDivision == "Division A",
            onClick = { onDivisionSelected("Division A") }
        )

        TeamDivisionButton(
            text = "Division B",
            selected = selectedDivision == "Division B",
            onClick = { onDivisionSelected("Division B") }
        )
    }
}

@Composable
fun TeamDivisionButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .height(32.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(if (selected) Color(0xFF0757C8) else Color(0xFFEAF0FF))
            .clickable { onClick() }
            .padding(horizontal = 13.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) BrandWhite else Color(0xFF0757C8),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PlayerTeamCard(
    team: EquipaComInfo,
    onDetailsClick: () -> Unit,
    onManageTeamClick: () -> Unit
) {
    val logoColor = teamColorFromName(team.equipa.nome)

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
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamLogoBox(
                    text = team.iniciais,
                    color = logoColor
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
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
                        text = "VIEW DETAILS  →",
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