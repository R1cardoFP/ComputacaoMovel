package com.example.trabalhocm.ui.screens.player

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.ui.screens.MatchPointBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import androidx.compose.foundation.layout.ColumnScope

@Composable
fun PlayerProfileSettingsScreen(
    onBackClick: () -> Unit = {},
    onChangePasswordClick: () -> Unit = {},
    onSaveClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var selectedLanguage by remember { mutableStateOf("English (US)") }
    var twoFactorEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        PlayerSettingsTopBar(
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 18.dp)
        ) {
            PlayerSettingsHeroCard()

            Spacer(modifier = Modifier.height(18.dp))

            AccountSettingsCard()

            Spacer(modifier = Modifier.height(14.dp))

            PreferencesCard(
                selectedLanguage = selectedLanguage,
                onLanguageSelected = { selectedLanguage = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            ActiveDashboardsCard()

            Spacer(modifier = Modifier.height(14.dp))

            SecurityCard(
                twoFactorEnabled = twoFactorEnabled,
                onTwoFactorChange = { twoFactorEnabled = it },
                onChangePasswordClick = onChangePasswordClick
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onSaveClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(2.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreen,
                    contentColor = BrandWhite
                )
            ) {
                Text(
                    text = "SAVE CHANGES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(2.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFFE53935)
                )
            ) {
                Text(
                    text = "LOG OUT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        MatchPointBottomBar(
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
fun PlayerSettingsTopBar(
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
            text = "Profile",
            color = BrandWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
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
fun PlayerSettingsHeroCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(205.dp),
        shape = RoundedCornerShape(7.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF102845),
                            BrandBlue
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.avatar_player),
                    contentDescription = "Cristiano Ronaldo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(74.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color(0xFFF0F2FA))
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "MEMBER SINCE 2021",
                    color = BrandGreen,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Cristiano Ronaldo",
                    color = BrandWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(9.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SettingsBadge(
                        text = "COMPETITIVE PLAYER",
                        backgroundColor = BrandGreen.copy(alpha = 0.20f),
                        textColor = BrandGreen
                    )

                    SettingsBadge(
                        text = "CR7 2023",
                        backgroundColor = Color(0xFF2B3F60),
                        textColor = Color(0xFFB6C0D0)
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
fun AccountSettingsCard() {
    SettingsSectionCard(
        icon = "♙",
        title = "Account Settings"
    ) {
        SettingsTextFieldBlock(
            label = "FULL NAME",
            value = "Cristiano Ronaldo"
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingsTextFieldBlock(
            label = "EMAIL ADDRESS",
            value = "cr7@gmail.com"
        )

        Spacer(modifier = Modifier.height(12.dp))

        SettingsTextFieldBlock(
            label = "BIO",
            value = "Seasoned striker and tournament coordinator for the Western Conference. Passionate about youth development.",
            height = 82
        )
    }
}

@Composable
fun PreferencesCard(
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    SettingsSectionCard(
        icon = "◎",
        title = "Preferences"
    ) {
        Text(
            text = "LANGUAGE",
            color = Color(0xFF7D8497),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        LanguageOption(
            text = "English (US)",
            selected = selectedLanguage == "English (US)",
            onClick = { onLanguageSelected("English (US)") }
        )

        LanguageOption(
            text = "Portuguese (PT)",
            selected = selectedLanguage == "Portuguese (PT)",
            onClick = { onLanguageSelected("Portuguese (PT)") }
        )
    }
}

@Composable
fun ActiveDashboardsCard() {
    SettingsSectionCard(
        icon = "♙",
        title = "Active Dashboards"
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFFF9FAFD))
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFEAF0FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "♙",
                    color = Color(0xFF0757C8),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Player",
                    color = BrandBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Personal career stats",
                    color = Color(0xFF7D8497),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .border(2.dp, BrandGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(BrandGreen)
                )
            }
        }
    }
}

@Composable
fun SecurityCard(
    twoFactorEnabled: Boolean,
    onTwoFactorChange: (Boolean) -> Unit,
    onChangePasswordClick: () -> Unit
) {
    SettingsSectionCard(
        icon = "♜",
        title = "Security"
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color(0xFFF9FAFD))
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "▣",
                color = Color(0xFF7D8497),
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = "Two-Factor Auth",
                color = BrandBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = twoFactorEnabled,
                onCheckedChange = onTwoFactorChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = BrandWhite,
                    checkedTrackColor = BrandGreen,
                    uncheckedThumbColor = BrandWhite,
                    uncheckedTrackColor = Color(0xFFB8BDC8)
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(
            onClick = onChangePasswordClick,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "→ CHANGE PASSWORD",
                color = Color(0xFF4167C8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun SettingsSectionCard(
    icon: String,
    title: String,
    content: @Composable ColumnScope.() -> Unit
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = icon,
                    color = Color(0xFF7D8497),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = title,
                    color = BrandBlue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}

@Composable
fun SettingsTextFieldBlock(
    label: String,
    value: String,
    height: Int = 44
) {
    Column {
        Text(
            text = label,
            color = Color(0xFF7D8497),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height.dp)
                .background(Color(0xFFF0F2FA))
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = value,
                color = BrandBlue,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun LanguageOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(if (selected) BrandWhite else Color(0xFFF0F2FA))
            .border(
                width = if (selected) 1.dp else 0.dp,
                color = if (selected) Color(0xFF2949FF) else Color.Transparent,
                shape = RoundedCornerShape(1.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            color = if (selected) Color(0xFF2949FF) else Color(0xFF7D8497),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        if (selected) {
            Text(
                text = "●",
                color = Color(0xFF2949FF),
                fontSize = 11.sp
            )
        }
    }
}

@Preview(showBackground = true, name = "Player Profile Settings Screen")
@Composable
fun PlayerProfileSettingsScreenPreview() {
    PlayerProfileSettingsScreen()
}