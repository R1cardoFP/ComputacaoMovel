package com.example.trabalhocm.ui.screens.auth

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.repository.AuthRepository
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import kotlinx.coroutines.launch

@Composable
fun ChangePasswordScreen(
    onBackClick: () -> Unit = {}, // <-- NOVO PARAMETRO
    onPasswordChanged: () -> Unit = {}
) {
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var mensagem by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val strength = getPasswordStrength(newPassword)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // --- NOVA TOP BAR COM SETA DE VOLTAR ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "←",
                color = BrandBlue,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onBackClick() }
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            ChangePasswordLogo()

            Spacer(modifier = Modifier.height(28.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = BrandWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 36.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Change\nPassword",
                        color = BrandBlue,
                        fontSize = 34.sp,
                        lineHeight = 38.sp,
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "ENTER YOUR CURRENT PASSWORD\nAND THEN YOUR NEW ONE.",
                        color = Color(0xFF8B92A5),
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    PasswordFieldBlock(
                        label = "CURRENT PASSWORD",
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        visible = showCurrentPassword,
                        onToggleVisible = { showCurrentPassword = !showCurrentPassword }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    PasswordFieldBlock(
                        label = "NEW PASSWORD",
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        visible = showNewPassword,
                        onToggleVisible = { showNewPassword = !showNewPassword }
                    )

                    Spacer(modifier = Modifier.height(22.dp))

                    PasswordStrengthBar(strength = strength)

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = {
                            if (currentPassword.isBlank() || newPassword.isBlank()) {
                                mensagem = "Preenche todos os campos."
                                return@Button
                            }

                            if (newPassword.length < 6) {
                                mensagem = "A nova password deve ter pelo menos 6 caracteres."
                                return@Button
                            }

                            if (currentPassword == newPassword) {
                                mensagem = "A nova password deve ser diferente da atual."
                                return@Button
                            }

                            scope.launch {
                                isLoading = true
                                mensagem = ""

                                val resultado = authRepository.alterarPassword(
                                    passwordAtual = currentPassword,
                                    novaPassword = newPassword
                                )

                                resultado
                                    .onSuccess {
                                        mensagem = "Password alterada com sucesso."
                                        onPasswordChanged()
                                    }
                                    .onFailure { erro ->
                                        mensagem = "Erro ao alterar password: ${erro.message}"
                                    }

                                isLoading = false
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp),
                        enabled = !isLoading,
                        shape = RoundedCornerShape(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandGreen,
                            contentColor = BrandWhite
                        )
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = BrandWhite,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "CHANGE PASSWORD →",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        }
                    }

                    if (mensagem.isNotBlank()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = mensagem,
                            color = if (mensagem.startsWith("Erro")) {
                                MaterialTheme.colorScheme.error
                            } else {
                                BrandBlue
                            },
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChangePasswordLogo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ML",
                color = BrandBlue,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = BrandBlue)) {
                        append("Match")
                    }
                    withStyle(SpanStyle(color = BrandGreen)) {
                        append("League")
                    }
                },
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .width(44.dp)
                .height(3.dp)
                .background(BrandGreen)
        )
    }
}

@Composable
fun PasswordFieldBlock(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    visible: Boolean,
    onToggleVisible: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            color = Color(0xFF7D8497),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = if (visible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                TextButton(onClick = onToggleVisible) {
                    Text(
                        text = if (visible) "HIDE" else "SHOW",
                        color = Color(0xFF7D8497),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            shape = RoundedCornerShape(4.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF1F2FB),
                unfocusedContainerColor = Color(0xFFF1F2FB),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = BrandGreen,
                focusedTextColor = BrandBlue,
                unfocusedTextColor = BrandBlue
            )
        )
    }
}

@Composable
fun PasswordStrengthBar(strength: Int) {
    val label = when (strength) {
        0 -> ""
        1 -> "Weak"
        2 -> "Reasonable"
        else -> "Strong"
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(4) { index ->
                val active = index < strength
                val color = when {
                    !active -> Color(0xFFE3E5EB)
                    index == 0 -> Color(0xFF3566C9)
                    index == 1 -> BrandGreen
                    index == 2 -> Color(0xFF28A745)
                    else -> Color(0xFF1E7E34)
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color)
                )
            }
        }

        if (label.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = label,
                color = BrandGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

fun getPasswordStrength(password: String): Int {
    if (password.isBlank()) return 0

    var score = 0

    if (password.length >= 6) score++
    if (password.length >= 8) score++
    if (password.any { it.isDigit() }) score++
    if (password.any { it.isUpperCase() } || password.any { !it.isLetterOrDigit() }) score++

    return score.coerceIn(1, 4)
}

@Preview(showBackground = true, name = "Change Password Screen")
@Composable
fun ChangePasswordScreenPreview() {
    ChangePasswordScreen()
}