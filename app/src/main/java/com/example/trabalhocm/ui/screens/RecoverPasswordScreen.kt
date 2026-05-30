package com.example.trabalhocm.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@Composable
fun RecoverPasswordScreen(
    onGoToLogin: () -> Unit = {}
) {
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
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

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            IconButton(
                onClick = onGoToLogin,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color(0xFF2F3138)
                )
            }

            RecoverLogo()
        }

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
                    text = "Recover\nPassword",
                    color = BrandBlue,
                    fontSize = 34.sp,
                    lineHeight = 38.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "ENTER YOUR EMAIL TO RECEIVE A\nREPLACEMENT LINK.",
                    color = Color(0xFF8B92A5),
                    fontSize = 12.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "EMAIL",
                    color = Color(0xFF7D8497),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                RecoverInputField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "name@example.com",
                    keyboardType = KeyboardType.Email
                )

                Spacer(modifier = Modifier.height(26.dp))

                Button(
                    onClick = {
                        if (email.isBlank()) {
                            mensagem = "Preenche o email."
                            return@Button
                        }

                        scope.launch {
                            isLoading = true
                            mensagem = ""

                            val resultado = authRepository.recuperarPassword(email)

                            resultado
                                .onSuccess {
                                    mensagem = "Foi enviado um email de recuperação."
                                }
                                .onFailure { erro ->
                                    mensagem = "Erro ao recuperar password: ${erro.message}"
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
                            text = "RECOVER ACCOUNT →",
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

@Composable
fun RecoverLogo() {
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
fun RecoverInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text
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
        visualTransformation = VisualTransformation.None,
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

@Preview(showBackground = true, name = "Recover Password Screen")
@Composable
fun RecoverPasswordScreenPreview() {
    RecoverPasswordScreen()
}