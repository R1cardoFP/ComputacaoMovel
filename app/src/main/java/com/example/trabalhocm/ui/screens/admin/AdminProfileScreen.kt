package com.example.trabalhocm.ui.screens.admin

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.repository.AdminProfileRepository
import kotlinx.coroutines.launch

private val AdminBlue = Color(0xFF0B1F3A)
private val AdminGreen = Color(0xFF008D7D)
private val AdminBackground = Color(0xFFF4F5FA)
private val TextMuted = Color(0xFF6F7A8A)
private val CardWhite = Color.White

@Composable
fun AdminProfileScreen(
    onBackClick: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { AdminProfileRepository() }
    val scope = rememberCoroutineScope()

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var language by remember { mutableStateOf("English (US)") }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var mensagem by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        repository.carregarPerfilAtual()
            .onSuccess { perfil ->
                nome = perfil.nome
                email = perfil.email
                bio = perfil.bio.ifBlank {
                    "Platform administrator. Overseeing global league operations and user management."
                }
                language = perfil.language
            }
            .onFailure { erro ->
                mensagem = "Erro ao carregar perfil: ${erro.message}"
            }

        isLoading = false
    }

    Scaffold(
        containerColor = AdminBackground,
        topBar = {
            AdminProfileTopBar(
                title = "Profile",
                onBackClick = onBackClick
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
                        title = "Account Settings",
                        icon = "♙"
                    ) {
                        ProfileInput(
                            label = "FULL NAME",
                            value = nome,
                            onValueChange = { nome = it }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileInput(
                            label = "EMAIL ADDRESS",
                            value = email,
                            onValueChange = {},
                            enabled = false,
                            keyboardType = KeyboardType.Email
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileInput(
                            label = "BIO",
                            value = bio,
                            onValueChange = { bio = it },
                            singleLine = false
                        )
                    }
                }

                item {
                    ProfileSectionCard(
                        title = "Preferences",
                        icon = "◎"
                    ) {
                        Text(
                            text = "LANGUAGE",
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.4.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        LanguageOption(
                            text = "English (US)",
                            selected = language == "English (US)",
                            onClick = { language = "English (US)" }
                        )

                        LanguageOption(
                            text = "Portuguese (PT)",
                            selected = language == "Portuguese (PT)",
                            onClick = { language = "Portuguese (PT)" }
                        )
                    }
                }

                item {
                    ProfileSectionCard(
                        title = "Active Dashboards",
                        icon = "♜"
                    ) {
                        DashboardOption()
                    }
                }

                item {
                    ProfileSectionCard(
                        title = "Security",
                        icon = "◇"
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF4F6FB), RoundedCornerShape(4.dp))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Two-Factor Auth",
                                color = AdminBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Switch(
                                checked = true,
                                onCheckedChange = {},
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = AdminGreen
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "REQUIRED FOR ADMIN",
                            color = AdminGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.End)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "↬ CHANGE PASSWORD",
                            color = Color(0xFF0057C8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }

                item {
                    Button(
                        onClick = {
                            scope.launch {
                                isSaving = true
                                mensagem = ""

                                repository.atualizarPerfil(
                                    nome = nome,
                                    bio = bio,
                                    language = language
                                )
                                    .onSuccess {
                                        mensagem = "Perfil atualizado com sucesso."
                                    }
                                    .onFailure { erro ->
                                        mensagem = "Erro ao guardar: ${erro.message}"
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
                                text = "SAVE CHANGES",
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
                            contentColor = Color(0xFFC53030)
                        )
                    ) {
                        Text(
                            text = "LOG OUT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.4.sp
                        )
                    }

                    if (mensagem.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = mensagem,
                            color = if (mensagem.startsWith("Erro")) Color(0xFFC53030) else AdminGreen,
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
                Text(
                    text = "👨🏽‍💻",
                    fontSize = 36.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = nome.ifBlank { "Administrador" },
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
                    text = "ADMINISTRATOR",
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
    onBackClick: () -> Unit
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onBackClick() }
        ) {
            Text(
                text = "←",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "♢",
            color = Color.White,
            fontSize = 23.sp
        )
    }
}

@Composable
private fun ProfileSectionCard(
    title: String,
    icon: String,
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
    icon: String,
    content: @Composable ColumnScope.() -> Unit
): @Composable ColumnScope.() -> Unit = {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            color = AdminBlue,
            fontSize = 14.sp
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
            singleLine = singleLine,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(if (singleLine) 46.dp else 78.dp),
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
private fun LanguageOption(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .background(
                if (selected) Color.White else Color(0xFFF4F6FB),
                RoundedCornerShape(2.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            color = AdminBlue,
            fontSize = 12.sp
        )

        if (selected) {
            Text(
                text = "●",
                color = Color(0xFF0057C8),
                fontSize = 12.sp
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
            Text(
                text = "⌂",
                color = Color.White,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.size(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Admin",
                color = AdminBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Global league management",
                color = TextMuted,
                fontSize = 10.sp
            )
        }

        Text(
            text = "›",
            color = AdminBlue,
            fontSize = 22.sp
        )
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
        AdminProfileBottomItem("⌂", "HOME", selected == "home", onHomeClick)
        AdminProfileBottomItem("♜", "TOURNAMENTS", selected == "tournaments", onTournamentsClick)
        AdminProfileBottomItem("◎", "MATCHES", selected == "matches", onMatchesClick)
        AdminProfileBottomItem("♟", "TEAMS", selected == "teams", onTeamsClick)
        AdminProfileBottomItem("♙", "PROFILE", selected == "profile", onProfileClick)
    }
}

@Composable
private fun AdminProfileBottomItem(
    icon: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            color = if (selected) Color(0xFF0057C8) else Color(0xFF9AA5B5),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = if (selected) Color(0xFF0057C8) else Color(0xFF9AA5B5),
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