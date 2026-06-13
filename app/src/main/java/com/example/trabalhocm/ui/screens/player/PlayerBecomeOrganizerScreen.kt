package com.example.trabalhocm.ui.screens.player

import android.net.Uri
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.trabalhocm.R
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.trabalhocm.data.repository.AuthRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import kotlinx.coroutines.launch

private val ScreenBg = Color(0xFFF6F7FB)
private val CardBg = Color.White
private val InputBg = Color(0xFFF0F2F7)
private val TextMuted = Color(0xFF687086)
private val LightBorder = Color(0xFFE4E8F0)
private val AccentBlue = Color(0xFF0757C8)
private val SoftGreen = Color(0xFFE7F8F0)
private val SoftBlue = Color(0xFFEAF2FF)

@Composable
fun PlayerBecomeOrganizerScreen(
    onBackClick: () -> Unit = {},
    onSubmitClick: () -> Unit = {},
    onCancelClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val authRepository = remember { AuthRepository() }
    val requestRepository = remember { com.example.trabalhocm.data.repository.PlayerOrganizerRepository() }
    val scope = androidx.compose.runtime.rememberCoroutineScope()
    var isSubmitting by remember { mutableStateOf(false) }

    var sport by remember { mutableStateOf("Volleyball") }
    var experience by remember { mutableStateOf("Intermediate") }
    var tournamentsPerYear by remember { mutableStateOf("1–3 tournaments") }
    var motivation by remember { mutableStateOf("") }
    var reference by remember { mutableStateOf("") }
    var acceptedTerms by remember { mutableStateOf(false) }

    var userName by remember { mutableStateOf("A carregar...") }
    var userEmail by remember { mutableStateOf("") }
    var userMemberSince by remember { mutableStateOf("2024") }
    var userPhotoUri by remember { mutableStateOf<Uri?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val dropdownOptions = listOf("1–3 tournaments", "4–6 tournaments", "7–10 tournaments", "10+ tournaments")

    LaunchedEffect(Unit) {
        authRepository.obterUtilizadorAtual().onSuccess { utilizador ->
            userName = utilizador.nome
            userEmail = utilizador.email

            if (!utilizador.dataCriacao.isNullOrEmpty() && utilizador.dataCriacao.length >= 4) {
                userMemberSince = utilizador.dataCriacao.substring(0, 4)
            }

            if (!utilizador.fotoUrl.isNullOrEmpty()) {
                val urlAtualizada = "${utilizador.fotoUrl}?v=${System.currentTimeMillis()}"
                userPhotoUri = urlAtualizada.toUri()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        BecomeOrganizerTopBar(
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 18.dp)
        ) {
            OrganizerHeroCard()

            Spacer(modifier = Modifier.height(16.dp))

            OrganizerInfoBanner()

            Spacer(modifier = Modifier.height(16.dp))

            OrganizerUserInfoCard(
                name = userName,
                email = userEmail,
                memberSince = userMemberSince,
                photoUri = userPhotoUri
            )

            Spacer(modifier = Modifier.height(18.dp))

            OrganizerFormCard(
                sport = sport,
                onSportChange = { sport = it },
                experience = experience,
                onExperienceChange = { experience = it },
                tournamentsPerYear = tournamentsPerYear,
                onTournamentsPerYearChange = { tournamentsPerYear = it },
                dropdownOptions = dropdownOptions,
                isDropdownExpanded = isDropdownExpanded,
                onDropdownExpandedChange = { isDropdownExpanded = it },
                motivation = motivation,
                onMotivationChange = { if (it.length <= 500) motivation = it },
                reference = reference,
                onReferenceChange = { reference = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OrganizerTermsBox(
                checked = acceptedTerms,
                onCheckedChange = { acceptedTerms = !acceptedTerms }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OrganizerActionsCard(
                isSubmitting = isSubmitting,
                canSubmit = acceptedTerms && motivation.isNotBlank() && !isSubmitting,
                onSubmit = {
                    scope.launch {
                        isSubmitting = true

                        // Chama o repositório passando os dados do state
                        requestRepository.submeterPedido(
                            modalidade = sport,
                            experiencia = experience,
                            frequencia = tournamentsPerYear,
                            motivo = motivation
                        ).onSuccess {
                            isSubmitting = false
                            onSubmitClick() // Volta para a Home apenas se gravar com sucesso!
                        }.onFailure { erro ->
                            isSubmitting = false
                            println("Erro ao submeter pedido: ${erro.message}")
                        }
                    }
                },
                onCancelClick = onCancelClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.player_org_disclaimer),
                color = TextMuted,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        MatchLeagueBottomBar(
            selectedTab = "TOURNAMENTS",
            onHomeClick = onHomeClick,
            onTournamentsClick = onTournamentsClick,
            onMatchesClick = onMatchesClick,
            onTeamsClick = onTeamsClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun BecomeOrganizerTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(BrandBlue)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "←",
                color = BrandWhite,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = stringResource(R.string.player_org_topbar),
            color = BrandWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(Color.White.copy(alpha = 0.12f))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.player_org_player_badge),
                color = BrandWhite,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun OrganizerHeroCard() {
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
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏆",
                        fontSize = 23.sp
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.player_org_tag),
                        color = BrandGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.6.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = stringResource(R.string.player_org_title),
                        color = BrandWhite,
                        fontSize = 24.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = stringResource(R.string.player_org_subtitle),
                color = BrandWhite.copy(alpha = 0.78f),
                fontSize = 14.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OrganizerHeroMetric(
                    modifier = Modifier.weight(1f),
                    value = "1",
                    label = stringResource(R.string.player_org_player_badge)
                )

                OrganizerHeroMetric(
                    modifier = Modifier.weight(1f),
                    value = "3",
                    label = stringResource(R.string.player_org_sport_label)
                )

                OrganizerHeroMetric(
                    modifier = Modifier.weight(1f),
                    value = "500",
                    label = stringResource(R.string.player_org_char_count, 0)
                )
            }
        }
    }
}

@Composable
fun OrganizerHeroMetric(
    modifier: Modifier = Modifier,
    value: String,
    label: String
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Column {
            Text(
                text = value,
                color = BrandWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = label.uppercase(),
                color = BrandWhite.copy(alpha = 0.62f),
                fontSize = 8.sp,
                lineHeight = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
        }
    }
}

@Composable
fun OrganizerInfoBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SoftGreen)
            .border(1.dp, BrandGreen.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
            .padding(horizontal = 15.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(BrandGreen.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "i",
                color = BrandGreen,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = stringResource(R.string.player_org_banner),
            color = Color(0xFF21734F),
            fontSize = 13.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun OrganizerUserInfoCard(
    name: String,
    email: String,
    memberSince: String,
    photoUri: Uri?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, LightBorder.copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SoftBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "👤",
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = stringResource(R.string.player_org_your_info),
                    color = BrandBlue,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (photoUri != null) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = stringResource(R.string.player_common_photo),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(InputBg)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .clip(CircleShape)
                            .background(InputBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 25.sp)
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = name,
                        color = BrandBlue,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = stringResource(R.string.player_org_member_since, email, memberSince),
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(SoftBlue)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.player_org_player_badge),
                        color = AccentBlue,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun OrganizerFormCard(
    sport: String,
    onSportChange: (String) -> Unit,
    experience: String,
    onExperienceChange: (String) -> Unit,
    tournamentsPerYear: String,
    onTournamentsPerYearChange: (String) -> Unit,
    dropdownOptions: List<String>,
    isDropdownExpanded: Boolean,
    onDropdownExpandedChange: (Boolean) -> Unit,
    motivation: String,
    onMotivationChange: (String) -> Unit,
    reference: String,
    onReferenceChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, LightBorder.copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            OrganizerSectionTitle(stringResource(R.string.player_org_sport_label))

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OrganizerSportCard(
                    modifier = Modifier.weight(1f),
                    icon = "⚽",
                    title = stringResource(R.string.player_sport_football),
                    selected = sport == "Football",
                    onClick = { onSportChange("Football") }
                )

                OrganizerSportCard(
                    modifier = Modifier.weight(1f),
                    icon = "🏐",
                    title = stringResource(R.string.player_sport_volleyball),
                    selected = sport == "Volleyball",
                    onClick = { onSportChange("Volleyball") }
                )

                OrganizerSportCard(
                    modifier = Modifier.weight(1f),
                    icon = "🏀",
                    title = stringResource(R.string.player_sport_basketball),
                    selected = sport == "Basketball",
                    onClick = { onSportChange("Basketball") }
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            OrganizerSectionTitle(stringResource(R.string.player_org_exp_label))

            Spacer(modifier = Modifier.height(10.dp))

            OrganizerExperienceOption(
                title = stringResource(R.string.player_org_exp_intermediate_title),
                subtitle = stringResource(R.string.player_org_exp_intermediate_sub),
                selected = experience == "Intermediate",
                onClick = { onExperienceChange("Intermediate") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OrganizerExperienceOption(
                title = stringResource(R.string.player_org_exp_beginner_title),
                subtitle = stringResource(R.string.player_org_exp_beginner_sub),
                selected = experience == "Beginner",
                onClick = { onExperienceChange("Beginner") }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OrganizerExperienceOption(
                title = stringResource(R.string.player_org_exp_experienced_title),
                subtitle = stringResource(R.string.player_org_exp_experienced_sub),
                selected = experience == "Experienced",
                onClick = { onExperienceChange("Experienced") }
            )

            Spacer(modifier = Modifier.height(22.dp))

            OrganizerSectionTitle(stringResource(R.string.player_org_freq_label))

            Spacer(modifier = Modifier.height(10.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = tournamentsPerYear,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .clickable { onDropdownExpandedChange(true) },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = stringResource(R.string.player_org_select_freq),
                            tint = BrandBlue,
                            modifier = Modifier.clickable { onDropdownExpandedChange(true) }
                        )
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = InputBg,
                        unfocusedContainerColor = InputBg,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = BrandBlue,
                        unfocusedTextColor = BrandBlue,
                        cursorColor = BrandGreen
                    )
                )

                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { onDropdownExpandedChange(false) },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .background(BrandWhite)
                ) {
                    dropdownOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option, color = BrandBlue) },
                            onClick = {
                                onTournamentsPerYearChange(option)
                                onDropdownExpandedChange(false)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            OrganizerSectionTitle(stringResource(R.string.player_org_motivation_label))

            Spacer(modifier = Modifier.height(10.dp))

            OrganizerSimpleInputBox(
                value = motivation,
                onValueChange = onMotivationChange,
                minHeight = 122
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.player_org_char_count, motivation.length),
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OrganizerSectionTitle(stringResource(R.string.player_org_reference_label))

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = stringResource(R.string.player_common_optional),
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OrganizerSimpleInputBox(
                value = reference,
                onValueChange = onReferenceChange,
                minHeight = 70
            )
        }
    }
}

@Composable
fun OrganizerSectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        color = TextMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp
    )
}

@Composable
fun OrganizerSportCard(
    modifier: Modifier = Modifier,
    icon: String,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) BrandGreen else LightBorder
    val background = if (selected) SoftGreen else InputBg

    Card(
        modifier = modifier
            .height(76.dp)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = title,
                color = if (selected) BrandGreen else BrandBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun OrganizerExperienceOption(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val background = if (selected) SoftBlue else InputBg
    val borderColor = if (selected) AccentBlue else LightBorder

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                color = BrandBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Medium
            )
        }

        OrganizerRadio(selected = selected)
    }
}

@Composable
fun OrganizerRadio(
    selected: Boolean
) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .border(
                width = 2.dp,
                color = if (selected) AccentBlue else Color(0xFFD1D6E0),
                shape = CircleShape
            )
            .background(if (selected) Color.White else Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(11.dp)
                    .clip(CircleShape)
                    .background(AccentBlue)
            )
        }
    }
}

@Composable
fun OrganizerSimpleInputBox(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    minHeight: Int
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(minHeight.dp),
        placeholder = {
            if (placeholder.isNotBlank()) {
                Text(
                    text = placeholder,
                    color = Color(0xFF9EA4B3),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        },
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

@Composable
fun OrganizerTermsBox(
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (checked) SoftGreen else CardBg)
            .border(
                width = 1.dp,
                color = if (checked) BrandGreen.copy(alpha = 0.45f) else LightBorder,
                shape = RoundedCornerShape(18.dp)
            )
            .clickable { onCheckedChange() }
            .padding(horizontal = 15.dp, vertical = 15.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(if (checked) BrandGreen else InputBg)
                .border(1.dp, if (checked) BrandGreen else LightBorder, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Text(
                    text = "✓",
                    color = BrandWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = stringResource(R.string.player_org_terms),
            color = if (checked) Color(0xFF21734F) else TextMuted,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun OrganizerActionsCard(
    isSubmitting: Boolean,
    canSubmit: Boolean,
    onSubmit: () -> Unit,
    onCancelClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, LightBorder.copy(alpha = 0.7f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = canSubmit,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreen,
                    contentColor = BrandWhite,
                    disabledContainerColor = Color(0xFFDDE1EA),
                    disabledContentColor = TextMuted
                )
            ) {
                Text(
                    text = if (isSubmitting) stringResource(R.string.player_org_submitting) else stringResource(R.string.player_org_submit),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onCancelClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, LightBorder),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = BrandBlue,
                    containerColor = CardBg
                )
            ) {
                Text(
                    text = stringResource(R.string.player_common_cancel),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.0.sp
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Player Become Organizer Screen")
@Composable
fun PlayerBecomeOrganizerScreenPreview() {
    PlayerBecomeOrganizerScreen()
}
