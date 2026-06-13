package com.example.trabalhocm.ui.screens.admin

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.core.os.LocaleListCompat
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.repository.AdminProfileRepository
import com.example.trabalhocm.ui.theme.AppIcons
import kotlinx.coroutines.launch

private val AdminBlue = Color(0xFF0B1F3A)
private val AdminGreen = Color(0xFF008D7D)
private val AdminBackground = Color(0xFFF4F5FA)
private val TextMuted = Color(0xFF6F7A8A)
private val CardWhite = Color.White
private val ErrorRed = Color(0xFFC53030)

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
        containerColor = AdminBackground,
        topBar = {
            AdminProfileTopBar(
                title = stringResource(R.string.admin_profile_title),
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick
            )
        },
        bottomBar = {
            AdminProfileBottomBar(
                selected = "profile",
                onHomeClick = onHomeClick,
                onTournamentsClick = onTournamentsClick,
                onMatchesClick = onMatchesClick,
                onTeamsClick = onTeamsClick,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AdminGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 12.dp,
                    bottom = 28.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    AdminProfileHeader(nome = nome)
                }

                item {
                    ProfileSectionCard(
                        title = stringResource(R.string.admin_profile_account_settings),
                        icon = AppIcons.Profile
                    ) {
                        ProfileInput(
                            label = stringResource(R.string.admin_profile_username).uppercase(),
                            value = username,
                            onValueChange = { username = it },
                            enabled = true,
                            readOnly = false
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileInput(
                            label = stringResource(R.string.admin_profile_full_name).uppercase(),
                            value = nome,
                            onValueChange = {},
                            enabled = true,
                            readOnly = true
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileInput(
                            label = stringResource(R.string.admin_profile_email_address).uppercase(),
                            value = email,
                            onValueChange = {},
                            enabled = true,
                            readOnly = true,
                            keyboardType = KeyboardType.Email
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileInput(
                            label = stringResource(R.string.admin_profile_bio).uppercase(),
                            value = bio,
                            onValueChange = { bio = it },
                            singleLine = false
                        )
                    }
                }

                item {
                    ProfileSectionCard(
                        title = stringResource(R.string.admin_profile_preferences),
                        icon = AppIcons.Settings
                    ) {
                        Text(
                            text = stringResource(R.string.admin_profile_language).uppercase(),
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.4.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val idiomaAtual = AppCompatDelegate.getApplicationLocales().toLanguageTags()
                        val isPortugues = idiomaAtual.startsWith("pt", ignoreCase = true)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            LanguageButton(
                                text = stringResource(R.string.admin_profile_language_portuguese),
                                selected = isPortugues,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    language = "Portuguese (PT)"
                                    AppCompatDelegate.setApplicationLocales(
                                        LocaleListCompat.forLanguageTags("pt-PT")
                                    )
                                }
                            )

                            LanguageButton(
                                text = stringResource(R.string.admin_profile_language_english),
                                selected = !isPortugues,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    language = "English (US)"
                                    AppCompatDelegate.setApplicationLocales(
                                        LocaleListCompat.forLanguageTags("en")
                                    )
                                }
                            )
                        }
                    }
                }

                item {
                    ProfileSectionCard(
                        title = stringResource(R.string.admin_profile_active_dashboards),
                        icon = AppIcons.Home
                    ) {
                        DashboardOption()
                    }
                }

                item {
                    ProfileSectionCard(
                        title = stringResource(R.string.admin_profile_security),
                        icon = AppIcons.Security
                    ) {
                        Row(
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .clickable {
                                    onChangePasswordClick()
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = AppIcons.Edit,
                                contentDescription = stringResource(R.string.admin_profile_change_password),
                                tint = Color(0xFF0057C8),
                                modifier = Modifier.size(16.dp)
                            )

                            Spacer(modifier = Modifier.size(6.dp))

                            Text(
                                text = stringResource(R.string.admin_profile_change_password).uppercase(),
                                color = Color(0xFF0057C8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                item {
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
                            .height(46.dp),
                        enabled = !isSaving,
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AdminGreen,
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
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.4.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                repository.logout()
                                onLogoutSuccess()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        shape = RoundedCornerShape(2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = ErrorRed
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.admin_profile_logout).uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.4.sp
                        )
                    }

                    if (mensagem.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = mensagem,
                            color = if (mensagemIsError) ErrorRed else AdminGreen,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminProfileHeader(nome: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = AdminBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFE6EEF8)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AppIcons.Profile,
                    contentDescription = stringResource(R.string.admin_profile_admin_content_description),
                    tint = AdminBlue,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = nome.ifBlank { stringResource(R.string.admin_profile_admin_default_name) },
                color = Color.White,
                fontSize = 23.sp,
                fontWeight = FontWeight.Normal
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(AdminGreen)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.admin_profile_admin_role).uppercase(),
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AdminProfileTopBar(
    title: String,
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AdminBlue)
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

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
private fun ProfileSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = contentWithTitle(title = title, icon = icon, content = content)
        )
    }
}

private fun contentWithTitle(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
): @Composable ColumnScope.() -> Unit = {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = AdminBlue,
            modifier = Modifier.size(17.dp)
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = title,
            color = AdminBlue,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }

    Spacer(modifier = Modifier.height(14.dp))

    content()
}

@Composable
private fun ProfileInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    singleLine: Boolean = true,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.4.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            textStyle = TextStyle(
                fontSize = if (singleLine) 13.sp else 14.sp,
                color = AdminBlue
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(if (singleLine) 56.dp else 96.dp),
            shape = RoundedCornerShape(4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF1F2FB),
                unfocusedContainerColor = Color(0xFFF1F2FB),
                disabledContainerColor = Color(0xFFF1F2FB),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                focusedTextColor = AdminBlue,
                unfocusedTextColor = AdminBlue,
                disabledTextColor = TextMuted,
                cursorColor = AdminGreen
            )
        )
    }
}

@Composable
private fun LanguageButton(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(50.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(3.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) Color(0xFF3566C9) else Color(0xFFF4F6FB)
        ),
        border = if (selected) null else BorderStroke(1.dp, Color(0xFFD4DCE8)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (selected) {
                Icon(
                    imageVector = AppIcons.Confirm,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.size(6.dp))
            }

            Text(
                text = text,
                color = if (selected) Color.White else TextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun DashboardOption() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(Color(0xFFEAF3FF), RoundedCornerShape(3.dp))
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(AdminGreen, RoundedCornerShape(2.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = AppIcons.Home,
                contentDescription = stringResource(R.string.admin_profile_dashboard_content_description),
                tint = Color.White,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(modifier = Modifier.size(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(R.string.admin_profile_dashboard_title),
                color = AdminBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.admin_profile_dashboard_desc),
                color = TextMuted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun AdminProfileBottomBar(
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
        AdminProfileBottomItem(AppIcons.Home, stringResource(R.string.admin_nav_home).uppercase(), selected == "home", onHomeClick)
        AdminProfileBottomItem(AppIcons.Tournaments, stringResource(R.string.admin_nav_tournaments).uppercase(), selected == "tournaments", onTournamentsClick)
        AdminProfileBottomItem(AppIcons.Games, stringResource(R.string.admin_nav_matches).uppercase(), selected == "matches", onMatchesClick)
        AdminProfileBottomItem(AppIcons.Teams, stringResource(R.string.admin_nav_teams).uppercase(), selected == "teams", onTeamsClick)
        AdminProfileBottomItem(AppIcons.Profile, stringResource(R.string.admin_nav_profile).uppercase(), selected == "profile", onProfileClick)
    }
}

@Composable
private fun AdminProfileBottomItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) Color(0xFF0057C8) else Color(0xFF9AA5B5)

    Column(
        modifier = Modifier.clickable { onClick() },
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

@Preview(showBackground = true)
@Composable
fun AdminProfileScreenPreview() {
    AdminProfileScreen()
}
