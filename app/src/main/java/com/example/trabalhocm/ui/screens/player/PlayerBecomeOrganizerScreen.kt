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
import androidx.compose.ui.text.font.FontWeight
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
            .background(Color(0xFFF4F5FA))
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
                .padding(horizontal = 22.dp, vertical = 18.dp)
        ) {
            Text(
                text = "ROLE UPGRADE",
                color = Color(0xFF0757C8),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Request Organizer\nAccess",
                color = BrandBlue,
                fontSize = 28.sp,
                lineHeight = 31.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Submit your application to host and manage\ntournaments on the platform.",
                color = Color(0xFF6D7486),
                fontSize = 15.sp,
                lineHeight = 21.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(18.dp))

            OrganizerInfoBanner()

            Spacer(modifier = Modifier.height(18.dp))

            OrganizerUserInfoCard(
                name = userName,
                email = userEmail,
                memberSince = userMemberSince,
                photoUri = userPhotoUri
            )

            Spacer(modifier = Modifier.height(18.dp))

            OrganizerSectionTitle("PRIMARY SPORT TO ORGANIZE")

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OrganizerSportCard(
                    modifier = Modifier.weight(1f),
                    icon = "⚽",
                    title = "Football",
                    selected = sport == "Football",
                    onClick = { sport = "Football" }
                )

                OrganizerSportCard(
                    modifier = Modifier.weight(1f),
                    icon = "🏐",
                    title = "Volleyball",
                    selected = sport == "Volleyball",
                    onClick = { sport = "Volleyball" }
                )

                OrganizerSportCard(
                    modifier = Modifier.weight(1f),
                    icon = "🏀",
                    title = "Basketball",
                    selected = sport == "Basketball",
                    onClick = { sport = "Basketball" }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OrganizerSectionTitle("EXPERIENCE LEVEL")

            Spacer(modifier = Modifier.height(10.dp))

            OrganizerExperienceOption(
                title = "Intermediate",
                subtitle = "I have organized small tournaments or local events before.",
                selected = experience == "Intermediate",
                onClick = { experience = "Intermediate" }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OrganizerExperienceOption(
                title = "Beginner",
                subtitle = "First time organizing a competition on a platform.",
                selected = experience == "Beginner",
                onClick = { experience = "Beginner" }
            )

            Spacer(modifier = Modifier.height(10.dp))

            OrganizerExperienceOption(
                title = "Experienced",
                subtitle = "I run regular leagues or large-scale tournaments.",
                selected = experience == "Experienced",
                onClick = { experience = "Experienced" }
            )

            Spacer(modifier = Modifier.height(24.dp))

            OrganizerSectionTitle("ESTIMATED TOURNAMENTS PER YEAR")

            Spacer(modifier = Modifier.height(10.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = tournamentsPerYear,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clickable { isDropdownExpanded = true },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select options",
                            modifier = Modifier.clickable { isDropdownExpanded = true }
                        )
                    },
                    shape = RoundedCornerShape(7.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFEFF1F6),
                        unfocusedContainerColor = Color(0xFFEFF1F6),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = BrandBlue,
                        unfocusedTextColor = BrandBlue
                    )
                )

                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false },
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .background(BrandWhite)
                ) {
                    dropdownOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(text = option, color = BrandBlue) },
                            onClick = {
                                tournamentsPerYear = option
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            OrganizerSectionTitle("WHY DO YOU WANT TO BE AN ORGANIZER?")

            Spacer(modifier = Modifier.height(10.dp))

            OrganizerSimpleInputBox(
                value = motivation,
                onValueChange = { if (it.length <= 500) motivation = it },
                minHeight = 118
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${motivation.length} / 500 characters",
                color = Color(0xFF7D8497),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.align(Alignment.End)
            )

            Spacer(modifier = Modifier.height(22.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                OrganizerSectionTitle("REFERENCE")

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "(Optional)",
                    color = Color(0xFF7D8497),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OrganizerSimpleInputBox(
                value = reference,
                onValueChange = { reference = it },
                minHeight = 64
            )

            Spacer(modifier = Modifier.height(14.dp))

            OrganizerTermsBox(
                checked = acceptedTerms,
                onCheckedChange = { acceptedTerms = !acceptedTerms }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = acceptedTerms && motivation.isNotBlank() && !isSubmitting,
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreen,
                    contentColor = BrandWhite,
                    disabledContainerColor = Color(0xFFDDE1EA),
                    disabledContentColor = Color(0xFF7D8497)
                )
            ) {
                Text(
                    text = if (isSubmitting) "SUBMITTING..." else "✈  SUBMIT REQUEST",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onCancelClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, Color(0xFFE1E5EE)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = BrandBlue
                )
            ) {
                Text(
                    text = "CANCEL",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "🔒 Your request will be reviewed by an administrator. You'll\nbe notified within 48 hours.",
                color = Color(0xFF7D8497),
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

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Become Organizer",
            color = BrandWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.4.sp
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
fun OrganizerInfoBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(7.dp))
            .background(BrandGreen.copy(alpha = 0.12f))
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "○",
            color = BrandGreen,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "As an organizer you can create\ntournaments, casual matches,\ninvite teams, and manage registrations. An\nadmin will review your request.",
            color = BrandGreen,
            fontSize = 13.sp,
            lineHeight = 17.sp,
            fontWeight = FontWeight.Medium
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
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "♙",
                    color = Color(0xFF0757C8),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Your Information",
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
                        contentDescription = "User Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF0F2FA))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF0F2FA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 24.sp)
                    }
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = name,
                        color = BrandBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "$email · Member since\n$memberSince",
                        color = Color(0xFF7D8497),
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFEAF0FF))
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "PLAYER",
                        color = Color(0xFF0757C8),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun OrganizerSectionTitle(text: String) {
    Text(
        text = text,
        color = Color(0xFF7D8497),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.8.sp
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
    val borderColor = if (selected) BrandGreen else Color.Transparent

    Card(
        modifier = modifier
            .height(70.dp)
            .border(1.dp, borderColor, RoundedCornerShape(3.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(3.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                color = BrandGreen,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = title,
                color = Color(0xFF303646),
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
    val background = if (selected) Color(0xFFEAF2FF) else BrandWhite
    val borderColor = if (selected) Color(0xFF0757C8) else Color(0xFFE1E5EE)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
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
                color = Color(0xFF6D7486),
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
            .size(22.dp)
            .clip(CircleShape)
            .border(
                width = 2.dp,
                color = if (selected) Color(0xFF0757C8) else Color(0xFFD1D6E0),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Text(
                text = "✓",
                color = Color(0xFF0757C8),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
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
        shape = RoundedCornerShape(7.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFEFF1F6),
            unfocusedContainerColor = Color(0xFFEFF1F6),
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
            .clip(RoundedCornerShape(7.dp))
            .background(Color(0xFFEFF1F6))
            .clickable { onCheckedChange() }
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (checked) BrandGreen else BrandWhite)
                .border(1.dp, BrandGreen, RoundedCornerShape(2.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Text(
                    text = "✓",
                    color = BrandWhite,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "I understand that as an organizer I must follow the\nplatform's community guidelines and that\nviolations may result in account suspension.",
            color = Color(0xFF6D7486),
            fontSize = 12.sp,
            lineHeight = 17.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, name = "Player Become Organizer Screen")
@Composable
fun PlayerBecomeOrganizerScreenPreview() {
    PlayerBecomeOrganizerScreen()
}