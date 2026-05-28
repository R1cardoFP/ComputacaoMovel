package com.example.trabalhocm.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
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
fun RegisterScreen(
    onRegisterSuccess: () -> Unit = {},
    onGoToLogin: () -> Unit = {}
) {
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(18.dp))

        AppLogoRegister()

        Spacer(modifier = Modifier.height(22.dp))

        Text(
            text = "Create Account",
            color = Color(0xFF2F3138),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = BrandWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SocialButton(
                    text = "Continue with Google",
                    icon = "G",
                    backgroundColor = BrandWhite,
                    contentColor = Color(0xFF4B4E57)
                )

                Spacer(modifier = Modifier.height(12.dp))

                SocialButton(
                    text = "Continue with Apple",
                    icon = "",
                    backgroundColor = Color(0xFF050A0A),
                    contentColor = BrandWhite
                )

                Spacer(modifier = Modifier.height(28.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFE4E5EB)
                    )

                    Text(
                        text = "OR SIGN UP WITH EMAIL",
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = Color(0xFF777A83),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )

                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFE4E5EB)
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                RegisterInput(
                    label = "Full Name",
                    value = nome,
                    onValueChange = { nome = it },
                    placeholder = "Enter your full name",
                    leading = "♙"
                )

                Spacer(modifier = Modifier.height(14.dp))

                RegisterInput(
                    label = "Email Address",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "you@example.com",
                    leading = "✉",
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(14.dp))

                RegisterInput(
                    label = "Password",
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Create a strong password",
                    leading = "🔒",
                    keyboardType = KeyboardType.Password,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                RegisterInput(
                    label = "Confirm Password",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "Repeat your password",
                    leading = "↻",
                    keyboardType = KeyboardType.Password,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (nome.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                            mensagem = "Preenche todos os campos."
                            return@Button
                        }

                        if (password != confirmPassword) {
                            mensagem = "As passwords não coincidem."
                            return@Button
                        }

                        if (password.length < 6) {
                            mensagem = "A password deve ter pelo menos 6 caracteres."
                            return@Button
                        }

                        scope.launch {
                            isLoading = true
                            mensagem = ""

                            val resultado = authRepository.registar(
                                nome = nome,
                                email = email,
                                password = password
                            )

                            resultado
                                .onSuccess {
                                    mensagem = "Conta criada com sucesso."
                                    onRegisterSuccess()
                                }
                                .onFailure { erro ->
                                    mensagem = "Erro ao criar conta: ${erro.message}"
                                }

                            isLoading = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(6.dp),
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
                            text = "Create Player Profile  →",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
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

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Already have an account?",
                color = Color(0xFF777A83),
                fontSize = 14.sp
            )

            TextButton(onClick = onGoToLogin) {
                Text(
                    text = "Log In",
                    color = BrandBlue.copy(alpha = 0.75f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AppLogoRegister() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
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
}

@Composable
fun SocialButton(
    text: String,
    icon: String,
    backgroundColor: Color,
    contentColor: Color
) {
    Button(
        onClick = {},
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = contentColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = icon,
            modifier = Modifier.padding(end = 18.dp),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )

        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = contentColor
        )
    }
}

@Composable
fun RegisterInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leading: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            color = Color(0xFF4C4F58),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(7.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            singleLine = true,
            placeholder = {
                Text(
                    text = placeholder,
                    color = Color(0xFFA8ABB5),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Text(
                    text = leading,
                    color = Color(0xFF8C909A),
                    fontSize = 16.sp
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType
            ),
            visualTransformation = if (isPassword) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
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

@Preview(showBackground = true, name = "Register Screen")
@Composable
fun RegisterScreenPreview() {
    RegisterScreen()
}