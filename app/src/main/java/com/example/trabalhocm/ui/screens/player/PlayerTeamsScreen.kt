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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.repository.EquipaComInfo
import com.example.trabalhocm.data.repository.EquipaRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import kotlinx.coroutines.launch

private val BgLight = Color(0xFFF4F7FB)
private val CardBg = Color.White
private val InputBg = Color(0xFFF1F4F8)
private val TextGray = Color(0xFF6D7486)
private val TextMuted = Color(0xFF9EA4B3)
private val PrimaryBlue = Color(0xFF0757C8)
private val DangerRed = Color(0xFFE53935)

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
            .background(BgLight)
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
            PlayerTeamsHeaderCard(
                totalTeams = teams.size,
                filteredTeams = filteredTeams.size,
                onCreateTeamClick = onCreateTeamClick
            )

            Spacer(modifier = Modifier.height(18.dp))

            PlayerTeamsSearchCard(
                search = search,
                onSearchChange = { search = it }
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
                        CircularProgressIndicator(color = BrandGreen)
                    }
                }

                errorMessage.isNotBlank() -> {
                    PlayerTeamsMessageCard(
                        title = stringResource(R.string.player_common_error),
                        message = errorMessage,
                        accentColor = DangerRed
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                }

                filteredTeams.isEmpty() -> {
                    PlayerTeamsMessageCard(
                        title = stringResource(R.string.player_teams_empty),
                        message = stringResource(R.string.player_teams_search_placeholder),
                        accentColor = PrimaryBlue
                    )

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

            Spacer(modifier = Modifier.height(8.dp))
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
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = stringResource(R.string.player_teams_topbar_title),
                color = BrandWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.player_teams_league_label),
                color = BrandWhite.copy(alpha = 0.72f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp
            )
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(BrandWhite.copy(alpha = 0.12f))
                .clickable { onNotificationsClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = stringResource(R.string.player_common_notifications),
                tint = BrandWhite,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
fun PlayerTeamsHeaderCard(
    totalTeams: Int,
    filteredTeams: Int,
    onCreateTeamClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.player_teams_league_label),
                color = BrandWhite.copy(alpha = 0.72f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.8.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.player_teams_title),
                color = BrandWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.player_teams_subtitle),
                color = BrandWhite.copy(alpha = 0.78f),
                fontSize = 13.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlayerTeamsMetric(
                    label = stringResource(R.string.player_teams_title),
                    value = totalTeams.toString(),
                    modifier = Modifier.weight(1f)
                )

                PlayerTeamsMetric(
                    label = stringResource(R.string.player_teams_search_placeholder),
                    value = filteredTeams.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onCreateTeamClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreen,
                    contentColor = BrandWhite
                )
            ) {
                Text(
                    text = "⊙  ${stringResource(R.string.player_teams_create_team)}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
fun PlayerTeamsMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite.copy(alpha = 0.12f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                text = label.uppercase(),
                color = BrandWhite.copy(alpha = 0.65f),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                color = BrandWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PlayerTeamsSearchCard(
    search: String,
    onSearchChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        OutlinedTextField(
            value = search,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
                .height(54.dp),
            singleLine = true,
            placeholder = {
                Text(
                    text = stringResource(R.string.player_teams_search_placeholder),
                    color = TextMuted,
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Text(
                    text = "⌕",
                    color = TextGray,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBg,
                unfocusedContainerColor = InputBg,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = BrandGreen,
                focusedTextColor = BrandBlue,
                unfocusedTextColor = BrandBlue
            )
        )
    }
}

@Composable
fun PlayerTeamsMessageCard(
    title: String,
    message: String,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "!",
                    color = accentColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = BrandBlue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = message,
                    color = TextGray,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }
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
                width = if (team.utilizadorPertence) 1.5.dp else 0.dp,
                color = if (team.utilizadorPertence) BrandGreen else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                TeamLogoBox(
                    text = team.iniciais,
                    color = logoColor
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = team.equipa.nome,
                        color = BrandBlue,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 21.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = when {
                            team.utilizadorCapitao -> {
                                stringResource(R.string.player_teams_badge_captain, team.divisao).uppercase()
                            }

                            team.utilizadorPertence -> {
                                stringResource(R.string.player_teams_badge_member, team.divisao).uppercase()
                            }

                            else -> {
                                team.divisao.uppercase()
                            }
                        },
                        color = if (team.utilizadorPertence) BrandGreen else PrimaryBlue,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = "${team.modalidadeNome} • ${team.cidade}",
                        color = TextGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                PlayerTeamsPrivacyBadge(isPublic = isPublic)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = InputBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 13.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TeamStat(
                        label = stringResource(R.string.player_common_wins),
                        value = team.vitorias.toString(),
                        valueColor = PrimaryBlue
                    )

                    TeamStat(
                        label = stringResource(R.string.player_common_losses),
                        value = team.derrotas.toString(),
                        valueColor = BrandBlue
                    )

                    TeamStat(
                        label = stringResource(R.string.player_common_streak),
                        value = team.streak,
                        valueColor = if (team.streakGood) BrandGreen else DangerRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onDetailsClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = BrandWhite,
                        contentColor = PrimaryBlue
                    )
                ) {
                    Text(
                        text = stringResource(R.string.player_common_view_details),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (team.utilizadorCapitao) {
                    Button(
                        onClick = onManageTeamClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandGreen,
                            contentColor = BrandWhite
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.player_teams_manage_team),
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
                                .height(46.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                disabledContainerColor = Color(0xFFDDE1EA),
                                disabledContentColor = Color(0xFF7D8497)
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.player_common_pending),
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
                                .height(46.dp),
                            shape = RoundedCornerShape(14.dp),
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
                                    text = if (isPublic) stringResource(R.string.player_teams_join) else stringResource(R.string.player_teams_request),
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
fun PlayerTeamsPrivacyBadge(isPublic: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isPublic) BrandGreen.copy(alpha = 0.12f) else DangerRed.copy(alpha = 0.10f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isPublic) "${stringResource(R.string.player_common_public)} 🔓" else "${stringResource(R.string.player_common_private)} 🔒",
            color = if (isPublic) BrandGreen else DangerRed,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TeamLogoBox(
    text: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
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
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label.uppercase(),
            color = TextGray,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            color = valueColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun teamColorFromName(nome: String): Color {
    val nomeNormalizado = nome.lowercase()

    return when {
        nomeNormalizado.contains("benfica") -> Color(0xFFE53935)
        nomeNormalizado.contains("porto") -> PrimaryBlue
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
