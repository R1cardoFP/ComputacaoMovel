package com.example.trabalhocm.ui.screens.player

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.trabalhocm.R
import com.example.trabalhocm.data.repository.AuthRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import kotlinx.coroutines.launch

private val BgGray = Color(0xFFF4F5FA)
private val CardBg = Color.White
private val InputBg = Color(0xFFF1F2FB)
private val TextGray = Color(0xFF7D8497)
private val TextDark = Color(0xFF303646)
private val SoftBlue = Color(0xFFE7ECFF)
private val PrimaryBlue = Color(0xFF3566C9)
private val DangerRed = Color(0xFFC62828)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerProfileScreen(
    initialUsername: String = stringResource(R.string.player_common_loading),
    initialName: String = stringResource(R.string.player_common_loading),
    initialEmail: String = stringResource(R.string.player_common_loading),
    initialBio: String = "",
    initialPhotoUri: Uri? = null,
    memberSinceYear: String = "2024",
    roles: List<String> = listOf("PLAYER"),
    tier: String = "BRONZE TIER",
    onLogoutClick: () -> Unit = {},
    onSaveChanges: (String, String, Uri?) -> Unit = { _, _, _ -> },
    onDashboardClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {}
) {
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var username by remember(initialUsername) { mutableStateOf(initialUsername) }
    var bio by remember(initialBio) { mutableStateOf(initialBio) }
    var selectedImageUri by remember(initialPhotoUri) { mutableStateOf(initialPhotoUri) }

    var isLoading by remember { mutableStateOf(false) }
    var mensagem by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) selectedImageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGray)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(R.string.player_common_profile),
                    color = BrandWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            },
            actions = {
                IconButton(onClick = onNotificationsClick) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = stringResource(R.string.player_common_notifications),
                        tint = BrandWhite
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandBlue)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            val usernameToShow = if (username.isBlank()) {
                stringResource(R.string.player_profile_no_username)
            } else {
                username
            }

            ProfileHeaderCard(
                username = usernameToShow,
                memberSince = memberSinceYear,
                roles = roles,
                tier = tier,
                selectedImageUri = selectedImageUri,
                onChangePhotoClick = { photoPickerLauncher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(18.dp))

            ProfileSectionCard {
                SectionHeader(
                    icon = Icons.Outlined.Person,
                    title = stringResource(R.string.player_profile_section_account)
                )

                Spacer(modifier = Modifier.height(18.dp))

                CustomTextField(
                    label = stringResource(R.string.player_profile_label_username),
                    value = username,
                    onValueChange = { username = it },
                    readOnly = false
                )
                Spacer(modifier = Modifier.height(12.dp))

                CustomTextField(
                    label = stringResource(R.string.player_profile_label_fullname),
                    value = initialName,
                    onValueChange = {},
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                CustomTextField(
                    label = stringResource(R.string.player_profile_label_email),
                    value = initialEmail,
                    onValueChange = {},
                    readOnly = true
                )
                Spacer(modifier = Modifier.height(12.dp))

                CustomTextField(
                    label = stringResource(R.string.player_profile_label_bio),
                    value = bio,
                    onValueChange = { bio = it },
                    singleLine = false,
                    placeholder = stringResource(R.string.player_profile_bio_placeholder),
                    modifier = Modifier.height(110.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            ProfileSectionCard {
                SectionHeader(
                    icon = Icons.Outlined.Settings,
                    title = stringResource(R.string.player_profile_section_preferences)
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = stringResource(R.string.player_profile_label_language),
                    color = TextGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                LanguageOption(
                    text = stringResource(R.string.player_profile_lang_en),
                    isSelected = true
                )
                Spacer(modifier = Modifier.height(10.dp))
                LanguageOption(
                    text = stringResource(R.string.player_profile_lang_pt),
                    isSelected = false
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            ProfileSectionCard {
                SectionHeader(
                    icon = Icons.AutoMirrored.Outlined.List,
                    title = stringResource(R.string.player_profile_section_dashboards)
                )

                Spacer(modifier = Modifier.height(18.dp))

                DashboardOption(onClick = onDashboardClick)
            }

            Spacer(modifier = Modifier.height(18.dp))

            ProfileSectionCard {
                SectionHeader(
                    icon = Icons.Outlined.Lock,
                    title = stringResource(R.string.player_profile_section_security)
                )

                Spacer(modifier = Modifier.height(18.dp))

                SecurityOption(onClick = onChangePasswordClick)
            }

            Spacer(modifier = Modifier.height(22.dp))

            Button(
                onClick = {
                    if (username.isBlank()) {
                        mensagem = context.getString(R.string.player_profile_err_empty_username)
                        isError = true
                        return@Button
                    }

                    scope.launch {
                        isLoading = true
                        mensagem = ""
                        isError = false

                        val resultado = authRepository.atualizarPerfil(username, bio)

                        resultado
                            .onSuccess {
                                mensagem = context.getString(R.string.player_profile_success)
                                isError = false
                                onSaveChanges(username, bio, selectedImageUri)
                            }
                            .onFailure { erro ->
                                isError = true
                                mensagem = if (erro.message?.contains("duplicate key") == true || erro.message?.contains("unique") == true) {
                                    context.getString(R.string.player_profile_err_username_taken)
                                } else {
                                    context.getString(R.string.player_profile_err_save, erro.message ?: "")
                                }
                            }

                        isLoading = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreen,
                    contentColor = BrandWhite
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = BrandWhite,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.player_profile_save),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            if (mensagem.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                ProfileMessageCard(
                    message = mensagem,
                    isError = isError
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, DangerRed),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = DangerRed)
            ) {
                Text(
                    text = stringResource(R.string.player_profile_logout),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        MatchLeagueBottomBar(
            selectedTab = "PROFILE",
            onHomeClick = onHomeClick,
            onTournamentsClick = onTournamentsClick,
            onMatchesClick = onMatchesClick,
            onTeamsClick = onTeamsClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun ProfileHeaderCard(
    username: String,
    memberSince: String,
    roles: List<String>,
    tier: String,
    selectedImageUri: Uri?,
    onChangePhotoClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.size(96.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(28.dp))
                        .border(2.dp, BrandWhite, RoundedCornerShape(28.dp))
                        .background(SoftBlue)
                        .clickable { onChangePhotoClick() }
                ) {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = stringResource(R.string.player_common_photo),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = "👤",
                            fontSize = 42.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BrandGreen)
                        .border(2.dp, BrandBlue, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = stringResource(R.string.player_common_selected),
                        tint = BrandWhite,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = stringResource(R.string.player_profile_member_since, memberSince),
                color = BrandGreen,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = username,
                color = BrandWhite,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                roles.forEach { role ->
                    ProfileBadge(
                        text = role,
                        backgroundColor = BrandGreen,
                        textColor = BrandWhite
                    )
                }

                ProfileBadge(
                    text = tier,
                    backgroundColor = PrimaryBlue,
                    textColor = BrandWhite
                )
            }
        }
    }
}

@Composable
fun SectionHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SoftBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = title,
            color = TextDark,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CustomTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    placeholder: String = ""
) {
    Column {
        Text(
            text = label,
            color = TextGray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(7.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = singleLine,
            readOnly = readOnly,
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(text = placeholder, color = TextGray)
                }
            },
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBg,
                unfocusedContainerColor = InputBg,
                disabledContainerColor = InputBg,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                disabledBorderColor = Color.Transparent,
                cursorColor = BrandGreen,
                focusedTextColor = if (readOnly) TextGray else TextDark,
                unfocusedTextColor = if (readOnly) TextGray else TextDark,
                disabledTextColor = TextGray
            )
        )
    }
}

@Composable
fun LanguageOption(text: String, isSelected: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) SoftBlue else InputBg)
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) PrimaryBlue else Color.Transparent,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = TextDark,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = stringResource(R.string.player_common_selected),
                tint = PrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun DashboardOption(onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(InputBg)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(SoftBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.player_profile_dashboard_player),
                color = TextDark,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.player_profile_dashboard_subtitle),
                color = TextGray,
                fontSize = 12.sp
            )
        }
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = BrandGreen,
            modifier = Modifier.size(21.dp)
        )
    }
}

@Composable
private fun ProfileSectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            content = content
        )
    }
}

@Composable
private fun SecurityOption(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(InputBg)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(SoftBlue),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Lock,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(21.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = stringResource(R.string.player_profile_change_password),
            color = PrimaryBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            letterSpacing = 0.7.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = BrandGreen,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun ProfileBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun ProfileMessageCard(
    message: String,
    isError: Boolean
) {
    val color = if (isError) MaterialTheme.colorScheme.error else BrandGreen
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) Color(0xFFFFEBEE) else Color(0xFFEAF7F0)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = message,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp)
        )
    }
}

@Preview(showBackground = true, name = "Profile Screen")
@Composable
fun ProfileScreenPreview() {
    PlayerProfileScreen()
}
