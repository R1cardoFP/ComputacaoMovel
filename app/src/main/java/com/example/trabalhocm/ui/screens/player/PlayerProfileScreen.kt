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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
private val InputBg = Color(0xFFF1F2FB)
private val TextGray = Color(0xFF7D8497)
private val TextDark = Color(0xFF303646)

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
    onChangePasswordClick: () -> Unit = {} // <-- NOVO PARAMETRO AQUI
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp)
                .background(BrandBlue)
                .padding(horizontal = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.player_common_profile),
                color = BrandWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = stringResource(R.string.player_common_notifications),
                tint = BrandWhite,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onNotificationsClick() }
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            ProfileHeaderCard(
                username = username.ifBlank { stringResource(R.string.player_profile_no_username) },
                memberSince = memberSinceYear,
                roles = roles,
                tier = tier,
                selectedImageUri = selectedImageUri,
                onChangePhotoClick = { photoPickerLauncher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(icon = Icons.Outlined.Person, title = stringResource(R.string.player_profile_section_account))
            Spacer(modifier = Modifier.height(16.dp))

            CustomTextField(label = stringResource(R.string.player_profile_label_username), value = username, onValueChange = { username = it }, readOnly = false)
            Spacer(modifier = Modifier.height(12.dp))

            CustomTextField(label = stringResource(R.string.player_profile_label_fullname), value = initialName, onValueChange = {}, readOnly = true)
            Spacer(modifier = Modifier.height(12.dp))
            CustomTextField(label = stringResource(R.string.player_profile_label_email), value = initialEmail, onValueChange = {}, readOnly = true)
            Spacer(modifier = Modifier.height(12.dp))

            CustomTextField(
                label = stringResource(R.string.player_profile_label_bio),
                value = bio,
                onValueChange = { bio = it },
                singleLine = false,
                placeholder = stringResource(R.string.player_profile_bio_placeholder),
                modifier = Modifier.height(100.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            SectionHeader(icon = Icons.Outlined.Settings, title = stringResource(R.string.player_profile_section_preferences))
            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.player_profile_label_language), color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            LanguageOption(text = stringResource(R.string.player_profile_lang_en), isSelected = true)
            LanguageOption(text = stringResource(R.string.player_profile_lang_pt), isSelected = false)

            Spacer(modifier = Modifier.height(32.dp))

            SectionHeader(icon = Icons.AutoMirrored.Outlined.List, title = stringResource(R.string.player_profile_section_dashboards))
            Spacer(modifier = Modifier.height(16.dp))
            DashboardOption(onClick = onDashboardClick)

            Spacer(modifier = Modifier.height(32.dp))

            SectionHeader(icon = Icons.Outlined.Lock, title = stringResource(R.string.player_profile_section_security))
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(containerColor = InputBg),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onChangePasswordClick() },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = Color(0xFF3566C9),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.player_profile_change_password),
                            color = Color(0xFF3566C9),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

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
                    .height(54.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = BrandWhite, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                } else {
                    Text(stringResource(R.string.player_profile_save), fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                }
            }

            if (mensagem.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = mensagem,
                    color = if (isError) MaterialTheme.colorScheme.error else BrandGreen,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(6.dp),
                border = BorderStroke(1.dp, Color(0xFFC62828)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828))
            ) {
                Text(stringResource(R.string.player_profile_logout), fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
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
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(2.dp, BrandWhite, RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFE2E6F2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("👤", fontSize = 40.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.player_profile_member_since, memberSince),
                color = BrandGreen,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = username,
                color = BrandWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                roles.forEach { role ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(BrandGreen)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(role, color = BrandWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF3566C9))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(tier, color = BrandWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SectionHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(imageVector = icon, contentDescription = null, tint = TextDark, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, color = TextDark, fontSize = 16.sp, fontWeight = FontWeight.Medium)
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
        Text(text = label, color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(6.dp))
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
            shape = RoundedCornerShape(6.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InputBg,
                unfocusedContainerColor = InputBg,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = BrandGreen,
                focusedTextColor = if (readOnly) TextGray else TextDark,
                unfocusedTextColor = if (readOnly) TextGray else TextDark
            )
        )
    }
}

@Composable
fun LanguageOption(text: String, isSelected: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) BrandWhite else InputBg, RoundedCornerShape(6.dp))
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) Color(0xFF3566C9) else Color.Transparent,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = text, color = TextDark, fontSize = 14.sp)
        if (isSelected) {
            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = stringResource(R.string.player_common_selected), tint = Color(0xFF3566C9))
        }
    }
}

@Composable
fun DashboardOption(onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(InputBg, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFE2E6F2), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Outlined.Person, contentDescription = null, tint = Color(0xFF3566C9))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(stringResource(R.string.player_profile_dashboard_player), color = TextDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.player_profile_dashboard_subtitle), color = TextGray, fontSize = 12.sp)
        }
        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = BrandGreen)
    }
}

@Preview(showBackground = true, name = "Profile Screen")
@Composable
fun ProfileScreenPreview() {
    PlayerProfileScreen()
}