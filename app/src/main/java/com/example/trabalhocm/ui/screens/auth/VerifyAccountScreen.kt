package com.example.trabalhocm.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
fun VerifyAccountScreen(
    email: String = "teste@email.com", // Vai receber o email do ecrã de registo
    onVerificationSuccess: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()

    var otpCode by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var mensagem by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 28.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        // Cabeçalho com Seta
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "←",
                color = BrandBlue,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clickable { onBackClick() }
                    .padding(end = 16.dp)
            )

            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = BrandBlue)) { append("Match") }
                    withStyle(SpanStyle(color = BrandGreen)) { append("League") }
                },
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = BrandWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Verify Email",
                    color = BrandBlue,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "We sent a 6-digit code to\n$email",
                    color = Color(0xFF7D8497),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Input Invisível com os 6 quadrados desenhados por cima
                BasicTextField(
                    value = otpCode,
                    onValueChange = {
                        if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                            otpCode = it
                            mensagem = ""
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    decorationBox = {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(6) { index ->
                                val char = when {
                                    index >= otpCode.length -> ""
                                    else -> otpCode[index].toString()
                                }
                                val isFocused = otpCode.length == index

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .background(Color(0xFFF1F2FB), RoundedCornerShape(6.dp))
                                        .border(
                                            width = if (isFocused) 2.dp else 1.dp,
                                            color = if (isFocused) BrandGreen else Color.Transparent,
                                            shape = RoundedCornerShape(6.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = char,
                                        fontSize = 24.sp,
                                        color = BrandBlue,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        if (otpCode.length < 6) {
                            mensagem = "Please enter the 6-digit code."
                            return@Button
                        }

                        scope.launch {
                            isLoading = true
                            mensagem = ""

                            val resultado = authRepository.verificarCodigoRegisto(email, otpCode)

                            resultado
                                .onSuccess {
                                    onVerificationSuccess()
                                }
                                .onFailure { erro ->
                                    mensagem = "Invalid code: ${erro.message}"
                                }

                            isLoading = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    enabled = !isLoading && otpCode.length == 6,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen,
                        contentColor = BrandWhite,
                        disabledContainerColor = BrandGreen.copy(alpha = 0.5f)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = BrandWhite, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "VERIFY CODE →",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp
                        )
                    }
                }

                if (mensagem.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = mensagem,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VerifyAccountScreenPreview() {
    VerifyAccountScreen()
}

