package com.example.trabalhocm.ui.screens.player

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.repository.EquipaRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import kotlinx.coroutines.launch

@Composable
fun PlayerCreateTeamScreen(
    onBackClick: () -> Unit = {},
    onCreateTeamClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { EquipaRepository() }
    val scope = rememberCoroutineScope()

    var teamName by remember { mutableStateOf("") }
    var initials by remember { mutableStateOf("") }
    var homeCity by remember { mutableStateOf("") }
    var selectedSport by remember { mutableStateOf("Football") }
    var selectedPrivacy by remember { mutableStateOf("privada") }

    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        CreateTeamTopBar(
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 18.dp)
        ) {
            Text(
                text = stringResource(R.string.player_createteam_eyebrow),
                color = Color(0xFF0757C8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.player_createteam_title),
                color = BrandBlue,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = stringResource(R.string.player_createteam_subtitle),
                color = Color(0xFF6D7486),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            TeamPrivacyCard(
                selectedPrivacy = selectedPrivacy,
                onPrivacySelected = {
                    selectedPrivacy = it
                    errorMessage = ""
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            TeamIdentityFieldsCard(
                teamName = teamName,
                onTeamNameChange = {
                    teamName = it
                    errorMessage = ""
                },
                initials = initials,
                onInitialsChange = {
                    initials = it.take(4).uppercase()
                    errorMessage = ""
                },
                homeCity = homeCity,
                onHomeCityChange = {
                    homeCity = it
                    errorMessage = ""
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            SportCategoryCard(
                selectedSport = selectedSport,
                onSportSelected = {
                    selectedSport = it
                    errorMessage = ""
                }
            )

            if (errorMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(7.dp),
                    colors = CardDefaults.cardColors(containerColor = BrandWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "${stringResource(R.string.player_common_error)}: $errorMessage",
                        color = Color(0xFFD01818),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        isSaving = true
                        errorMessage = ""

                        repository.criarEquipa(
                            nome = teamName,
                            iniciais = initials,
                            cidade = homeCity,
                            modalidadeNome = selectedSport,
                            tipoEntrada = selectedPrivacy
                        )
                            .onSuccess {
                                isSaving = false
                                onCreateTeamClick()
                            }
                            .onFailure {
                                errorMessage = it.message ?: "Erro ao criar equipa."
                                isSaving = false
                            }
                    }
                },
                enabled = !isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(5.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreen,
                    contentColor = BrandWhite,
                    disabledContainerColor = Color(0xFFD4D9E3),
                    disabledContentColor = Color(0xFF7D8497)
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = BrandWhite,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "${stringResource(R.string.player_teams_create_team)}  →",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
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
fun CreateTeamTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(BrandBlue)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "←",
            color = BrandWhite,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                onBackClick()
            }
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = stringResource(R.string.player_teams_topbar_title),
            color = BrandWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic
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
fun TeamPrivacyCard(
    selectedPrivacy: String,
    onPrivacySelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Text(
                text = stringResource(R.string.player_createteam_privacy_label),
                color = Color(0xFF7D8497),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PrivacySelectionBox(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.player_createteam_private_title),
                    description = stringResource(R.string.player_createteam_private_desc),
                    selected = selectedPrivacy == "privada",
                    onClick = { onPrivacySelected("privada") }
                )

                PrivacySelectionBox(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.player_createteam_public_title),
                    description = stringResource(R.string.player_createteam_public_desc),
                    selected = selectedPrivacy == "publica",
                    onClick = { onPrivacySelected("publica") }
                )
            }
        }
    }
}

@Composable
fun PrivacySelectionBox(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) BrandGreen else Color(0xFFE8EAF2)
    val bgColor = if (selected) BrandGreen.copy(alpha = 0.05f) else BrandWhite

    Column(
        modifier = modifier
            .height(70.dp)
            .clip(RoundedCornerShape(5.dp))
            .border(1.dp, borderColor, RoundedCornerShape(5.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            color = BrandBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = description,
            color = Color(0xFF7D8497),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun TeamIdentityFieldsCard(
    teamName: String,
    onTeamNameChange: (String) -> Unit,
    initials: String,
    onInitialsChange: (String) -> Unit,
    homeCity: String,
    onHomeCityChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            TeamTextInput(
                label = stringResource(R.string.player_createteam_field_name),
                value = teamName,
                onValueChange = onTeamNameChange,
                focused = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            TeamTextInput(
                label = stringResource(R.string.player_createteam_field_initials),
                value = initials,
                onValueChange = onInitialsChange,
                modifier = Modifier.width(150.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            TeamTextInput(
                label = stringResource(R.string.player_createteam_field_city),
                value = homeCity,
                onValueChange = onHomeCityChange
            )
        }
    }
}

@Composable
fun TeamTextInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    focused: Boolean = false
) {
    Column {
        Text(
            text = label,
            color = Color(0xFF7D8497),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.4.sp
        )

        Spacer(modifier = Modifier.height(7.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.height(54.dp),
            singleLine = true,
            shape = RoundedCornerShape(5.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = if (focused) Color(0xFFEAF7F5) else Color(0xFFEFF1F6),
                unfocusedContainerColor = Color(0xFFEFF1F6),
                focusedBorderColor = BrandGreen,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = BrandGreen,
                focusedTextColor = BrandBlue,
                unfocusedTextColor = BrandBlue
            )
        )
    }
}

@Composable
fun SportCategoryCard(
    selectedSport: String,
    onSportSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Text(
                text = stringResource(R.string.player_createteam_sport_label),
                color = Color(0xFF7D8497),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SportSelectionBox(
                    modifier = Modifier.weight(1f),
                    icon = "⚽",
                    title = stringResource(R.string.player_sport_football),
                    selected = selectedSport == "Football",
                    onClick = { onSportSelected("Football") }
                )

                SportSelectionBox(
                    modifier = Modifier.weight(1f),
                    icon = "🏐",
                    title = stringResource(R.string.player_sport_volleyball),
                    selected = selectedSport == "Volleyball",
                    onClick = { onSportSelected("Volleyball") }
                )

                SportSelectionBox(
                    modifier = Modifier.weight(1f),
                    icon = "🏀",
                    title = stringResource(R.string.player_sport_basketball),
                    selected = selectedSport == "Basketball",
                    onClick = { onSportSelected("Basketball") }
                )
            }
        }
    }
}

@Composable
fun SportSelectionBox(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) BrandGreen else Color(0xFFE8EAF2)

    Column(
        modifier = modifier
            .height(78.dp)
            .clip(RoundedCornerShape(5.dp))
            .border(1.dp, borderColor, RoundedCornerShape(5.dp))
            .background(BrandWhite)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = icon,
            color = BrandGreen,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            color = Color(0xFF303646),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, name = "Player Create Team Screen")
@Composable
fun PlayerCreateTeamScreenPreview() {
    PlayerCreateTeamScreen()
}