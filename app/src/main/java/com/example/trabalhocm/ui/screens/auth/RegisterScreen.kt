package com.example.trabalhocm.ui.screens.auth

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import coil.compose.AsyncImage
import com.example.trabalhocm.data.repository.AuthRepository
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegisterSuccess: (String) -> Unit = {},
    onGoToLogin: () -> Unit = {}
) {
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var nome by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var mensagem by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) } // Controla se a mensagem é vermelha ou não
    var isLoading by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) photoUri = uri
    }

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

        // --- CABEÇALHO COM SETA DE VOLTAR E LOGO ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 22.dp),
            contentAlignment = Alignment.Center
        ) {
            // Seta alinhada à esquerda
            Text(
                text = "←",
                color = BrandBlue,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { onGoToLogin() }
                    .padding(end = 16.dp, bottom = 8.dp, top = 8.dp) // Área de clique mais confortável
            )

            // Logo centrado
            AppLogoRegister()
        }

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

                // SELETOR DE FOTO
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .border(2.dp, BrandGreen, CircleShape)
                        .background(Color(0xFFF1F2FB))
                        .clickable { photoPickerLauncher.launch("image/*") }
                ) {
                    if (photoUri != null) {
                        AsyncImage(
                            model = photoUri,
                            contentDescription = "Profile Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📷", fontSize = 24.sp)
                            Text("Add Photo", fontSize = 10.sp, color = Color(0xFF7D8497))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                RegisterInput(
                    label = "Full Name",
                    value = nome,
                    onValueChange = { nome = it },
                    placeholder = "Enter your full name",
                    leading = "♙"
                )

                Spacer(modifier = Modifier.height(14.dp))

                RegisterInput(
                    label = "Username",
                    value = username,
                    onValueChange = { username = it },
                    placeholder = "player123",
                    leading = "@"
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
                        if (nome.isBlank() || username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                            mensagem = "Please fill in all fields."
                            isError = true
                            return@Button
                        }

                        if (password != confirmPassword) {
                            mensagem = "Passwords do not match."
                            isError = true
                            return@Button
                        }

                        if (password.length < 6) {
                            mensagem = "Password must be at least 6 characters long."
                            isError = true
                            return@Button
                        }

                        scope.launch {
                            isLoading = true
                            mensagem = ""
                            isError = false

                            val resultado = authRepository.registar(
                                nome = nome,
                                username = username,
                                email = email,
                                password = password
                            )

                            resultado
                                .onSuccess {
                                    // COPIA A IMAGEM PARA O TELEMÓVEL DE FORMA SEGURA ENQUANTO O EMAIL NÃO É CONFIRMADO
                                    if (photoUri != null) {
                                        val uriPermanente = guardarImagemInternamente(context, photoUri!!, email)
                                        if (uriPermanente != null) {
                                            val sharedPrefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                            sharedPrefs.edit {
                                                putString("avatar_$email", uriPermanente)
                                            }
                                        }
                                    }

                                    isError = false
                                    onRegisterSuccess(email)
                                }
                                .onFailure { erro ->
                                    isError = true
                                    mensagem = erro.message ?: "An unexpected error occurred."
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
                        color = if (isError) MaterialTheme.colorScheme.error else BrandBlue,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
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

// FUNÇÃO PARA GUARDAR IMAGEM TEMPORARIAMENTE NO TELEMÓVEL
fun guardarImagemInternamente(context: Context, uri: Uri, identificador: String): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = java.io.File(context.filesDir, "avatar_$identificador.jpg")
        val outputStream = java.io.FileOutputStream(file)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        Uri.fromFile(file).toString()
    } catch (e: Exception) {
        null
    }
}