package com.example.trabalhocm.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LoginScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Ecrã de Login a Construir!")
    }
}

@Preview(showBackground = true, name = "Login Screen")
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}