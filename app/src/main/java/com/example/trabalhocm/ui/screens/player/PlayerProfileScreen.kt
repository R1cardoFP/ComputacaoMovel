package com.example.trabalhocm.ui.screens.player

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite

private val BgGray = Color(0xFFF4F5FA)
private val InputBg = Color(0xFFF1F2FB)
private val TextGray = Color(0xFF7D8497)
private val TextDark = Color(0xFF303646)

@Composable
fun PlayerProfileScreen(
    initialName: String = "A carregar...",
    initialEmail: String = "A carregar...",
    initialBio: String = "",
    initialPhotoUri: Uri? = null,
    memberSinceYear: String = "2024",
    roles: List<String> = listOf("PLAYER"),
    tier: String = "BRONZE TIER",
    onLogoutClick: () -> Unit = {},
    onSaveChanges: (String, Uri?) -> Unit = { _, _ -> },
    onDashboardClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var bio by remember(initialBio) { mutableStateOf(initialBio) }
    var twoFactorEnabled by remember { mutableStateOf(true) }
    var selectedImageUri by remember(initialPhotoUri) { mutableStateOf(initialPhotoUri) }

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
                .height(64.dp)
                .background(BrandBlue)
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Profile",
                color = BrandWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notificações",
                tint = BrandWhite
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            ProfileHeaderCard(
                name = initialName.ifBlank { "Sem Nome" },
                memberSince = memberSinceYear,
                roles = roles,
                tier = tier,
                selectedImageUri = selectedImageUri,
                onChangePhotoClick = { photoPickerLauncher.launch("image/*") }
            )

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(icon = Icons.Outlined.Person, title = "Account Settings")
            Spacer(modifier = Modifier.height(16.dp))

            // NOME E EMAIL SÃO READ ONLY
            CustomTextField(label = "FULL NAME", value = initialName, onValueChange = {}, readOnly = true)
            Spacer(modifier = Modifier.height(12.dp))
            CustomTextField(label = "EMAIL ADDRESS", value = initialEmail, onValueChange = {}, readOnly = true)
            Spacer(modifier = Modifier.height(12.dp))

            // BIO É EDITÁVEL (O placeholder aparece quando "bio" é vazio "")
            CustomTextField(
                label = "BIO",
                value = bio,
                onValueChange = { bio = it },
                singleLine = false,
                placeholder = "Sem biografia definida.",
                modifier = Modifier.height(100.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            SectionHeader(icon = Icons.Outlined.Settings, title = "Preferences")
            Spacer(modifier = Modifier.height(16.dp))
            Text("LANGUAGE", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            LanguageOption(text = "English (US)", isSelected = true)
            LanguageOption(text = "Portuguese (PT)", isSelected = false)

            Spacer(modifier = Modifier.height(32.dp))

            SectionHeader(icon = Icons.AutoMirrored.Outlined.List, title = "Active Dashboards")
            Spacer(modifier = Modifier.height(16.dp))
            DashboardOption(onClick = onDashboardClick)

            Spacer(modifier = Modifier.height(32.dp))

            SectionHeader(icon = Icons.Outlined.Lock, title = "Security")
            Spacer(modifier = Modifier.height(16.dp))
            SecurityToggle(
                checked = twoFactorEnabled,
                onCheckedChange = { twoFactorEnabled = it }
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                onClick = { },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("CHANGE PASSWORD", color = Color(0xFF3566C9), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { onSaveChanges(bio, selectedImageUri) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) {
                Text("SAVE CHANGES", fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
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
                Text("LOG OUT", fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
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
    name: String,
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
                        contentDescription = "User Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // SUBSTITUÍDO: Agora mostra o boneco cinzento em vez do Ronaldo
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
                text = "MEMBER SINCE $memberSince",
                color = BrandGreen,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = name,
                color = BrandWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (roles.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(BrandGreen)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(roles.first(), color = BrandWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
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
            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Selected", tint = Color(0xFF3566C9))
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
            Text("Player", color = TextDark, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("Personal career stats", color = TextGray, fontSize = 12.sp)
        }
        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = BrandGreen)
    }
}

@Composable
fun SecurityToggle(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(InputBg, RoundedCornerShape(6.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = Icons.Outlined.Phone, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text("Two-Factor Auth", color = TextDark, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = BrandWhite,
                checkedTrackColor = BrandGreen,
                uncheckedThumbColor = BrandWhite,
                uncheckedTrackColor = Color.LightGray
            )
        )
    }
}

@Preview(showBackground = true, name = "Profile Screen")
@Composable
fun ProfileScreenPreview() {
    PlayerProfileScreen()
}