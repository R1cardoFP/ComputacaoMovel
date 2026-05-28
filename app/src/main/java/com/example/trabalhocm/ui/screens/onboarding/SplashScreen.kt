package com.example.trabalhocm.ui.screens.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import kotlinx.coroutines.delay

private val BrandBlue = Color(0xFF0B1F3A)
private val AccentGreen = Color(0xFF008D7D)

@Composable
fun SplashScreen(onEnd: () -> Unit) {
    LaunchedEffect(Unit) { delay(2400); onEnd() }
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.estadio), contentDescription = null,
            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
        )
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0x3A1A2E47), Color(0xAA122236)))))

        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.logo), contentDescription = "MatchPoint logo",
                modifier = Modifier.size(400.dp), contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(6.dp))
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = BrandBlue, fontWeight = FontWeight.Bold)) { append("MATCH") }
                    withStyle(SpanStyle(color = AccentGreen, fontWeight = FontWeight.Bold)) { append("POINT") }
                }, fontSize = 34.sp
            )
            Spacer(Modifier.height(20.dp))
            Box(Modifier.width(2.dp).height(50.dp).background(Color(0xFF18C9BB).copy(alpha = 0.7f)))
        }
    }
}

@Preview(showBackground = true, name = "Splash Screen")
@Composable
fun SplashScreenPreview() {
    SplashScreen(onEnd = {})
}