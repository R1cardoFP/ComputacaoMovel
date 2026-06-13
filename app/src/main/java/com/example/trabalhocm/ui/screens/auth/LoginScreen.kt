package com.example.trabalhocm.ui.screens.auth

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
import com.example.trabalhocm.R
import com.example.trabalhocm.data.repository.AuthRepository
import com.example.trabalhocm.data.remote.SupabaseClient
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite

import io.github.jan.supabase.compose.auth.composeAuth
import io.github.jan.supabase.compose.auth.composable.NativeSignInResult
import io.github.jan.supabase.compose.auth.composable.rememberSignInWithGoogle
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit = {},
    onCreateAccount: () -> Unit = {},
    onForgotPassword: () -> Unit = {},
    onSuspendedAccount: () -> Unit = {}
){
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()

    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var mensagem by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val composeAuth = SupabaseClient.client.composeAuth
    val googleSignIn = composeAuth.rememberSignInWithGoogle(
        onResult = { result ->
            when (result) {
                is NativeSignInResult.Success -> {
                    mensagem = "Creating player profile..."
                    isLoading = true

                    scope.launch {
                        authRepository.sincronizarUtilizadorGoogle()
                            .onSuccess {
                                mensagem = "Login successful!"
                                isLoading = false
                                onLoginSuccess()
                            }
                            .onFailure { erro ->
                                mensagem = "Database error: ${erro.message}"
                                isLoading = false
                            }
                    }
                }
                is NativeSignInResult.ClosedByUser -> {
                    isLoading = false
                }
                is NativeSignInResult.Error -> {
                    mensagem = "Google error. Missing SHA-1 fingerprint."
                    isLoading = false
                }
                is NativeSignInResult.NetworkError -> {
                    mensagem = "Network error connecting to Google."
                    isLoading = false
                }
            }
        },
        fallback = {
            mensagem = "Error: Google Login is not supported on this device."
            isLoading = false
        }
    )

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
        Spacer(modifier = Modifier.height(38.dp))

        LoginLogo()

        Spacer(modifier = Modifier.height(26.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = BrandWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 34.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "ACCESS ARENA",
                    color = Color(0xFF7D8497),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Welcome Back",
                    color = BrandBlue,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(28.dp))

                LoginInput(
                    label = "EMAIL OR USERNAME",
                    value = emailOrUsername,
                    onValueChange = { emailOrUsername = it },
                    placeholder = "name@example.com or player123",
                    keyboardType = KeyboardType.Text
                )

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PASSWORD",
                        color = Color(0xFF7D8497),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    Text(
                        text = "FORGOT PASSWORD?",
                        color = Color(0xFF3566C9),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            onForgotPassword()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LoginInputField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "••••••••",
                    keyboardType = KeyboardType.Password,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (emailOrUsername.isBlank() || password.isBlank()) {
                            mensagem = "Please fill in the identifier and password."
                            return@Button
                        }

                        scope.launch {
                            isLoading = true
                            mensagem = ""

                            val resultado = authRepository.login(
                                identificador = emailOrUsername.trim(),
                                password = password
                            )

                            resultado
                                .onSuccess { utilizador ->
                                    mensagem = "Login successful. Welcome, ${utilizador.nome}!"
                                    onLoginSuccess()
                                }
                                .onFailure { erro ->
                                    val erroTexto = erro.message.orEmpty()

                                    if (
                                        erroTexto.contains("suspended", ignoreCase = true) ||
                                        erroTexto.contains("suspensa", ignoreCase = true)
                                    ) {
                                        onSuspendedAccount()
                                    } else {
                                        mensagem = "Login error: ${erro.message}"
                                    }
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
                            text = "LOGIN →",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp
                        )
                    }
                }

                if (mensagem.isNotBlank()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = mensagem,
                        color = if (mensagem.contains("error", ignoreCase = true) ||
                            mensagem.startsWith("Please", ignoreCase = true)) {
                            MaterialTheme.colorScheme.error
                        } else {
                            BrandBlue
                        },
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(34.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFE4E5EB)
                    )

                    Text(
                        text = "OR CONTINUE WITH",
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = Color(0xFF9AA0AF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.8.sp
                    )

                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = Color(0xFFE4E5EB)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                LoginSocialButton(
                    modifier = Modifier.fillMaxWidth(),
                    icon = "G",
                    text = "CONTINUE WITH GOOGLE",
                    onClick = {
                        isLoading = true
                        mensagem = ""
                        googleSignIn.startFlow()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Don't have an account?",
                color = Color(0xFF7D8497),
                fontSize = 14.sp
            )

            TextButton(onClick = onCreateAccount) {
                Text(
                    text = "Create Account",
                    color = BrandGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun LoginLogo() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "MatchLeague Logo",
                modifier = Modifier.size(64.dp), // <-- AUMENTADO AQUI
                contentScale = ContentScale.Fit
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
                fontSize = 28.sp, // <-- Aumentei um pouco o texto para acompanhar o logo
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
fun LoginInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
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

        LoginInputField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            keyboardType = keyboardType
        )
    }
}

@Composable
fun LoginInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp),
        singleLine = true,
        placeholder = {
            Text(
                text = placeholder,
                color = Color(0xFFA7ACBA),
                fontSize = 15.sp
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

@Composable
fun LoginSocialButton(
    modifier: Modifier = Modifier,
    icon: String,
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(3.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandWhite,
            contentColor = BrandBlue
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = icon,
            color = BrandGreen,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = text,
            color = Color(0xFF333842),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, name = "Login Screen")
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}