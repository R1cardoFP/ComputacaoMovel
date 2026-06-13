package com.example.trabalhocm.ui.screens.admin

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import com.example.trabalhocm.R
import com.example.trabalhocm.data.repository.AdminProfileRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.AppIcons
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.CardBg
import com.example.trabalhocm.ui.theme.DarkBlue
import com.example.trabalhocm.ui.theme.ErrorRed
import com.example.trabalhocm.ui.theme.InputBg
import com.example.trabalhocm.ui.theme.PrimaryBlue
import com.example.trabalhocm.ui.theme.TealGreen
import com.example.trabalhocm.ui.theme.TextGray
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProfileScreen(
    onBackClick: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onDashboardClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { AdminProfileRepository() }
    val scope = rememberCoroutineScope()

    val defaultBioText = stringResource(R.string.admin_profile_default_bio)
    val errorLoadingProfileText = stringResource(R.string.admin_profile_error_loading)
    val usernameEmptyErrorText = stringResource(R.string.admin_profile_username_empty_error)
    val saveSuccessText = stringResource(R.string.admin_profile_save_success)
    val saveErrorText = stringResource(R.string.admin_profile_save_error)

    var username by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("English (US)") }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var mensagem by remember { mutableStateOf("") }
    var mensagemIsError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        repository.carregarPerfilAtual()
            .onSuccess { perfil ->
                username = perfil.username
                nome = perfil.nome
                email = perfil.email
                bio = perfil.bio.ifBlank { defaultBioText }
                language = perfil.language
            }
            .onFailure { erro ->
                mensagem = "$errorLoadingProfileText: ${erro.message}"
                mensagemIsError = true
            }

        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.admin_profile_title),
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
                selectedTab = "PROFILE",
                onHomeClick = onHomeClick,
                onTournamentsClick = onTournamentsClick,
                onMatchesClick = onMatchesClick,
                onTeamsClick = onTeamsClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = BgLight
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TealGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    top = 16.dp,
                    bottom = 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    AdminProfileHeader(nome = nome)
                }

                item {
                    AdminProfileSectionTitle(
                        title = stringResource(R.string.admin_profile_account_settings),
                        icon = AppIcons.Profile
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AdminProfileSectionLabel(stringResource(R.string.admin_profile_username).uppercase())
                    Spacer(modifier = Modifier.height(8.dp))
                    AdminProfileTextField(
                        value = username,
                        onValueChange = { username = it }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AdminProfileSectionLabel(stringResource(R.string.admin_profile_full_name).uppercase())
                    Spacer(modifier = Modifier.height(8.dp))
                    AdminProfileTextField(
                        value = nome,
                        onValueChange = {},
                        readOnly = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AdminProfileSectionLabel(stringResource(R.string.admin_profile_email_address).uppercase())
                    Spacer(modifier = Modifier.height(8.dp))
                    AdminProfileTextField(
                        value = email,
                        onValueChange = {},
                        readOnly = true,
                        keyboardType = KeyboardType.Email
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AdminProfileSectionLabel(stringResource(R.string.admin_profile_bio).uppercase())
                    Spacer(modifier = Modifier.height(8.dp))
                    AdminProfileTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        singleLine = false,
                        height = 100.dp
                    )
                }

                item {
                    AdminProfileSectionTitle(
                        title = stringResource(R.string.admin_profile_preferences),
                        icon = AppIcons.Settings
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AdminProfileSectionLabel(stringResource(R.string.admin_profile_language).uppercase())
                    Spacer(modifier = Modifier.height(8.dp))

                    val idiomaAtual = AppCompatDelegate.getApplicationLocales().toLanguageTags()
                    val isPortugues = idiomaAtual.startsWith("pt", ignoreCase = true)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AdminLanguageChoiceButton(
                            text = stringResource(R.string.admin_profile_language_portuguese),
                            selected = isPortugues,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                if (!isPortugues) {
                                    language = "Portuguese (PT)"
                                    AppCompatDelegate.setApplicationLocales(
                                        LocaleListCompat.forLanguageTags("pt-PT")
                                    )
                                }
                            }
                        )

                        AdminLanguageChoiceButton(
                            text = stringResource(R.string.admin_profile_language_english),
                            selected = !isPortugues,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                if (isPortugues) {
                                    language = "English (US)"
                                    AppCompatDelegate.setApplicationLocales(
                                        LocaleListCompat.forLanguageTags("en")
                                    )
                                }
                            }
                        )
                    }
                }

                item {
                    AdminProfileSectionTitle(
                        title = stringResource(R.string.admin_profile_active_dashboards),
                        icon = AppIcons.Home
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AdminDashboardOption(onClick = onDashboardClick)
                }

                item {
                    AdminProfileSectionTitle(
                        title = stringResource(R.string.admin_profile_security),
                        icon = AppIcons.Security
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = InputBg),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onChangePasswordClick() }
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = AppIcons.Edit,
                                contentDescription = stringResource(R.string.admin_profile_change_password),
                                tint = PrimaryBlue,
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = stringResource(R.string.admin_profile_change_password).uppercase(),
                                color = PrimaryBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(
                            onClick = {
                                scope.launch {
                                    isSaving = true
                                    mensagem = ""
                                    mensagemIsError = false

                                    if (username.isBlank()) {
                                        mensagem = usernameEmptyErrorText
                                        mensagemIsError = true
                                        isSaving = false
                                        return@launch
                                    }

                                    repository.atualizarPerfil(
                                        username = username.trim(),
                                        bio = bio,
                                        language = language
                                    )
                                        .onSuccess {
                                            mensagem = saveSuccessText
                                            mensagemIsError = false
                                        }
                                        .onFailure { erro ->
                                            mensagem = "$saveErrorText: ${erro.message}"
                                            mensagemIsError = true
                                        }

                                    isSaving = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            enabled = !isSaving,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TealGreen,
                                contentColor = Color.White
                            )
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.admin_profile_save_changes).uppercase(),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    repository.logout()
                                    onLogoutSuccess()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, ErrorRed),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = ErrorRed
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.admin_profile_logout).uppercase(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        if (mensagem.isNotBlank()) {
                            Text(
                                text = mensagem,
                                color = if (mensagemIsError) ErrorRed else TealGreen,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminProfileHeader(nome: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(3.dp, Color.White, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AppIcons.Profile,
                    contentDescription = stringResource(R.string.admin_profile_admin_content_description),
                    tint = DarkBlue,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.admin_profile_admin_role).uppercase(),
                color = TealGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = nome.ifBlank { stringResource(R.string.admin_profile_admin_default_name) },
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = TealGreen,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.admin_profile_admin_role).uppercase(),
                    color = DarkBlue,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun AdminProfileSectionTitle(
    title: String,
    icon: ImageVector
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = DarkBlue,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            color = DarkBlue,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AdminProfileSectionLabel(text: String) {
    Text(
        text = text,
        color = TextGray,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
}

@Composable
private fun AdminProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    height: Dp = 56.dp,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        readOnly = readOnly,
        singleLine = singleLine,
        maxLines = if (singleLine) 1 else 5,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(8.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = InputBg,
            unfocusedContainerColor = InputBg,
            disabledContainerColor = InputBg,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = DarkBlue,
            unfocusedTextColor = DarkBlue,
            disabledTextColor = TextGray,
            cursorColor = TealGreen
        )
    )
}

@Composable
private fun AdminLanguageChoiceButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (selected) PrimaryBlue else CardBg
        ),
        shape = RoundedCornerShape(8.dp),
        border = if (selected) null else BorderStroke(1.dp, InputBg),
        modifier = modifier
            .height(50.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selected) {
                Icon(
                    imageVector = AppIcons.Confirm,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))
            }

            Text(
                text = text,
                color = if (selected) Color.White else TextGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AdminDashboardOption(onClick: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PrimaryBlue.copy(alpha = 0.1f)),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(2.dp, TealGreen),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = TealGreen
            ) {
                Icon(
                    imageVector = AppIcons.Home,
                    contentDescription = stringResource(R.string.admin_profile_dashboard_content_description),
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.admin_profile_dashboard_title),
                    color = DarkBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = stringResource(R.string.admin_profile_dashboard_desc),
                    color = TextGray,
                    fontSize = 12.sp
                )
            }

            Icon(
                imageVector = AppIcons.Confirm,
                contentDescription = null,
                tint = DarkBlue,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AdminProfileScreenPreview() {
    AdminProfileScreen()
}
