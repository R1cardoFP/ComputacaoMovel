package com.example.trabalhocm.ui.screens.player

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

private val ScreenBg = Color(0xFFF4F6FB)
private val InputBg = Color(0xFFEFF3F8)
private val TextMuted = Color(0xFF6D7486)
private val CardBorder = Color(0xFFE7EAF2)
private val SoftGreen = Color(0xFFEAF8F5)
private val SoftBlue = Color(0xFFEAF1FF)

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
            .background(ScreenBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        CreateTeamTopBar(onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            CreateTeamHeroCard(
                teamName = teamName,
                initials = initials,
                selectedSport = selectedSport,
                selectedPrivacy = selectedPrivacy,
                homeCity = homeCity
            )

            Spacer(modifier = Modifier.height(18.dp))

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

            TeamPrivacyCard(
                selectedPrivacy = selectedPrivacy,
                onPrivacySelected = {
                    selectedPrivacy = it
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

                FeedbackCard(
                    message = "${stringResource(R.string.player_common_error)}: $errorMessage",
                    isError = true
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

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
                    .height(58.dp),
                shape = RoundedCornerShape(16.dp),
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
                        letterSpacing = 0.6.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
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
            .height(66.dp)
            .background(BrandBlue)
            .padding(horizontal = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "‹",
                color = BrandWhite,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = stringResource(R.string.player_teams_topbar_title),
            color = BrandWhite,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "⚑",
                color = BrandWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CreateTeamHeroCard(
    teamName: String,
    initials: String,
    selectedSport: String,
    selectedPrivacy: String,
    homeCity: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials.ifBlank { "TM" },
                        color = BrandWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.player_createteam_eyebrow).uppercase(),
                        color = BrandGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.8.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = teamName.ifBlank { stringResource(R.string.player_createteam_title) },
                        color = BrandWhite,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = stringResource(R.string.player_createteam_subtitle),
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HeroInfoChip(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.player_createteam_sport_label),
                    value = selectedSport
                )

                HeroInfoChip(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.player_createteam_privacy_label),
                    value = if (selectedPrivacy == "privada") {
                        stringResource(R.string.player_createteam_private_title)
                    } else {
                        stringResource(R.string.player_createteam_public_title)
                    }
                )

                HeroInfoChip(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.player_createteam_field_city),
                    value = homeCity.ifBlank { "--" }
                )
            }
        }
    }
}

@Composable
fun HeroInfoChip(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label.uppercase(),
            color = Color.White.copy(alpha = 0.58f),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            color = BrandWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun TeamPrivacyCard(
    selectedPrivacy: String,
    onPrivacySelected: (String) -> Unit
) {
    SectionCard(
        title = stringResource(R.string.player_createteam_privacy_label),
        subtitle = stringResource(R.string.player_createteam_private_desc)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
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

@Composable
fun PrivacySelectionBox(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) BrandGreen else CardBorder
    val bgColor = if (selected) SoftGreen else BrandWhite

    Column(
        modifier = modifier
            .height(88.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title,
            color = BrandBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = description,
            color = TextMuted,
            fontSize = 10.sp,
            lineHeight = 14.sp
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
    SectionCard(
        title = stringResource(R.string.player_createteam_field_name),
        subtitle = stringResource(R.string.player_createteam_subtitle)
    ) {
        TeamTextInput(
            label = stringResource(R.string.player_createteam_field_name),
            value = teamName,
            onValueChange = onTeamNameChange,
            focused = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TeamTextInput(
                label = stringResource(R.string.player_createteam_field_initials),
                value = initials,
                onValueChange = onInitialsChange,
                modifier = Modifier.weight(0.85f)
            )

            TeamTextInput(
                label = stringResource(R.string.player_createteam_field_city),
                value = homeCity,
                onValueChange = onHomeCityChange,
                modifier = Modifier.weight(1.15f)
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
    Column(modifier = modifier) {
        Text(
            text = label.uppercase(),
            color = TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.1.sp
        )

        Spacer(modifier = Modifier.height(7.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = if (focused) SoftGreen else InputBg,
                unfocusedContainerColor = InputBg,
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
    SectionCard(
        title = stringResource(R.string.player_createteam_sport_label),
        subtitle = stringResource(R.string.player_createteam_sport_label)
    ) {
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

@Composable
fun SportSelectionBox(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) BrandGreen else CardBorder
    val bgColor = if (selected) SoftGreen else BrandWhite

    Column(
        modifier = modifier
            .height(94.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(if (selected) BrandGreen.copy(alpha = 0.18f) else SoftBlue),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                fontSize = 17.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            color = BrandBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun SectionCard(
    title: String,
    subtitle: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Text(
                text = title,
                color = BrandBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}

@Composable
fun FeedbackCard(
    message: String,
    isError: Boolean
) {
    val bg = if (isError) Color(0xFFFFF1F1) else SoftGreen
    val textColor = if (isError) Color(0xFFD01818) else Color(0xFF087968)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = message,
            color = textColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, name = "Player Create Team Screen")
@Composable
fun PlayerCreateTeamScreenPreview() {
    PlayerCreateTeamScreen()
}
