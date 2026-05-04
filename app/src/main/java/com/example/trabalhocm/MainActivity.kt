package com.example.trabalhocm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import com.example.trabalhocm.ui.theme.TrabalhoCMTheme
import kotlinx.coroutines.delay

// --- Cores da app
private val BrandBlue = Color(0xFF0B1F3A)
private val AccentGreen = Color(0xFF008D7D)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // --- Composable de entrada da app
        setContent { TrabalhoCMTheme { MatchLeagueFlow() } }
    }
}

@Composable
private fun MatchLeagueFlow() {
    // --- Fluxo simples em 4 passos: splash + 3 ecras de onboarding
    var page by remember { mutableIntStateOf(0) }
    // --- Encaminha para o ecrã atual
    when (page) {
        0 -> SplashScreen { page = 1 }
        1 -> Onboarding1 { page = 2 }
        2 -> Onboarding2 { page = 3 }
        else -> Onboarding3 {}
    }
}

@Composable
private fun SplashScreen(onEnd: () -> Unit) {
    // --- Avança automaticamente do splash após um curto atraso
    LaunchedEffect(Unit) { delay(2400); onEnd() }
    Box(modifier = Modifier.fillMaxSize()) {
        // --- Imagem de fundo do estádio
        Image(
            painter = painterResource(R.drawable.estadio),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // --- Gradiente escuro por cima para legibilidade
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0x3A1A2E47), Color(0xAA122236)))))

        // --- Cantos decorativos
        SplashCorner(Modifier.align(Alignment.TopStart).padding(start = 22.dp, top = 24.dp))
        SplashCorner(Modifier.align(Alignment.TopEnd).padding(end = 22.dp, top = 24.dp), right = true)
        SplashCorner(Modifier.align(Alignment.BottomStart).padding(start = 22.dp, bottom = 22.dp), bottom = true)
        SplashCorner(Modifier.align(Alignment.BottomEnd).padding(end = 22.dp, bottom = 22.dp), right = true, bottom = true)

        // --- Bloco de branding centrado
        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "MatchLeague logo",
                modifier = Modifier.size(400.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(Modifier.height(6.dp))
            // --- Título da marca
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = BrandBlue, fontWeight = FontWeight.Bold)) { append("MATCH") }
                    withStyle(SpanStyle(color = AccentGreen, fontWeight = FontWeight.Bold)) { append("LEAGUE") }
                },
                fontSize = 34.sp
            )
            Spacer(Modifier.height(20.dp))
            // --- Linha de destaque vertical
            Box(Modifier.width(2.dp).height(50.dp).background(Color(0xFF18C9BB).copy(alpha = 0.7f)))
        }

        // --- Indicador de sincronização no rodapé
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 82.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("SYNCING DATA", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, letterSpacing = 1.sp)
            Spacer(Modifier.height(4.dp))
            // --- Pontos de paginação
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.3f)))
                Box(Modifier.size(6.dp).clip(CircleShape).background(AccentGreen))
                Box(Modifier.size(6.dp).clip(CircleShape).background(AccentGreen))
            }
        }
    }
}

@Composable
private fun SplashCorner(modifier: Modifier, right: Boolean = false, bottom: Boolean = false) {
    // --- Forma de canto usada no splash
    Box(modifier.size(34.dp)) {
        Box(
            Modifier
                .align(if (bottom) Alignment.BottomStart else Alignment.TopStart)
                .width(34.dp)
                .height(1.dp)
                .background(AccentGreen.copy(alpha = 0.55f))
        )
        Box(
            Modifier
                .align(if (right) Alignment.TopEnd else Alignment.TopStart)
                .width(1.dp)
                .height(34.dp)
                .background(AccentGreen.copy(alpha = 0.55f))
        )
    }
}

@Composable
private fun Onboarding1(onNext: () -> Unit) {
    // --- Ecrã de introdução com título e descrição
    OnboardingShell(page = 1, buttonText = "NEXT  →", onNext = onNext, centeredHeader = false) {
        Spacer(Modifier.height(42.dp))
        // --- Título
        Text(
            buildAnnotatedString {
                append("Manage Every\n")
                withStyle(SpanStyle(color = AccentGreen)) { append("Tournament.") }
            },
            color = BrandBlue,
            fontSize = 48.sp,
            lineHeight = 54.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(18.dp))
        // --- Texto de apoio
        Desc("Create leagues, knockout\ncompetitions,\nand professional sports events with\nease.")
    }
}

@Composable
private fun Onboarding2(onNext: () -> Unit) {
    // --- Pré-visualização de jogo ao vivo + mensagem
    OnboardingShell(page = 2, buttonText = "NEXT  >", onNext = onNext, centeredHeader = false) {
        Spacer(Modifier.height(10.dp))
        // --- Cartão de jogo (pré-visualização)
        MatchCard()
        Spacer(Modifier.height(26.dp))
        // --- Título
        Text(
            buildAnnotatedString {
                append("TRACK LIVE ")
                withStyle(SpanStyle(color = AccentGreen)) { append("RESULTS.") }
            },
            color = BrandBlue,
            fontSize = 42.sp,
            lineHeight = 48.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(14.dp))
        // --- Texto de apoio
        Desc("Get real-time updates, live standings, and\ndetailed statistics for every match.", centered = true)
    }
}

@Composable
private fun Onboarding3(onFinish: () -> Unit) {
    // --- Destaque da comunidade com CTA
    OnboardingShell(page = 3, buttonText = "GET STARTED", onNext = onFinish, centeredHeader = true, skipVisible = false) {
        Spacer(Modifier.height(14.dp))
        // --- Cartão de pré-visualização da comunidade
        CommunityTopCard()
        Spacer(Modifier.height(16.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color(0xFFF7F8FA))
                .padding(horizontal = 18.dp, vertical = 20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // --- Indicadores de progresso
                Indicators(3, showText = false)
                Spacer(Modifier.height(14.dp))
                // --- Rótulo da secção
                Text("COMMUNITY HUB", color = BrandBlue.copy(alpha = 0.55f), fontSize = 10.sp, letterSpacing = 1.8.sp)
                Spacer(Modifier.height(12.dp))
                // --- Título
                Text(
                    buildAnnotatedString {
                        append("Join the ")
                        withStyle(SpanStyle(color = AccentGreen)) { append("League.") }
                    },
                    color = BrandBlue,
                    fontSize = 42.sp,
                    lineHeight = 48.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(14.dp))
                // --- Texto de apoio
                Desc("Invite players, manage teams, and book\ncasual pickup games in your\nneighborhood.", centered = true)
                Spacer(Modifier.height(18.dp))
                // --- CTA principal
                Button(
                    onClick = onFinish,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
                ) { Text("GET STARTED", color = BrandWhite, fontSize = 14.sp, letterSpacing = 1.5.sp) }
            }
        }
    }
}

@Composable
private fun OnboardingShell(
    page: Int,
    buttonText: String,
    onNext: () -> Unit,
    centeredHeader: Boolean,
    skipVisible: Boolean = true,
    body: @Composable ColumnScope.() -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F3F9))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp, vertical = 18.dp)
    ) {
        // --- Linha de cabeçalho com logo e opcional de "Skip"
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = if (centeredHeader) Arrangement.Center else Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = BrandBlue)) { append("MATCH") }
                    withStyle(SpanStyle(color = AccentGreen)) { append("LEAGUE") }
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            if (!centeredHeader && skipVisible) {
                Text("SKIP", color = BrandBlue.copy(alpha = 0.6f), fontSize = 11.sp)
            }
        }
        // --- Conteúdo do corpo injetado por ecrã
        body()
        Spacer(Modifier.weight(1f))
        if (page != 3) {
            // --- Rodapé: indicadores + ação principal
            Indicators(page, showText = false)
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) { Text(buttonText, color = BrandWhite, fontSize = 14.sp, letterSpacing = 1.5.sp) }
        }
    }
}

@Composable
private fun MatchCard() {
    // --- Cartão com snapshot de jogo ao vivo
    Box(
        Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0C1F41))
            .padding(16.dp)
    ) {
        Column {
            // --- Linha de cabeçalho
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("LIVE · 74'", color = AccentGreen, fontSize = 11.sp)
                Text("PREMIER LEAGUE", color = BrandBlue.copy(alpha = 0.55f), fontSize = 10.sp)
            }
            Spacer(Modifier.height(14.dp))
            // --- Separador
            Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0x33FFFFFF)))
            Spacer(Modifier.height(14.dp))
            // --- Linha de equipas + resultado
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                TeamBlock(R.drawable.team_vianense, "VIANENSE")
                Text("2", color = BrandWhite, fontSize = 54.sp, fontWeight = FontWeight.Medium)
                Text(":", color = BrandBlue.copy(alpha = 0.5f), fontSize = 36.sp)
                Text("1", color = BrandWhite, fontSize = 54.sp, fontWeight = FontWeight.Medium)
                TeamBlock(R.drawable.team_sporting, "SPORTING")
            }
        }
    }
}

@Composable
private fun TeamBlock(logoRes: Int, team: String) {
    // --- Logótipo e nome da equipa usados no cartão
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(logoRes),
            contentDescription = team,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(52.dp).clip(RoundedCornerShape(10.dp)).background(Color.White).padding(4.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(team, color = BrandWhite, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun CommunityTopCard() {
    // --- Layout com dois cartões para pré-visualizar a comunidade
    Box(
        Modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFEAEDF8))
    ) {
        Column(
            Modifier
                .padding(10.dp)
                .width(220.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF7F8FA))
                .padding(12.dp)
        ) {
            // --- Cartão de informação à esquerda
            Text("LIVE NEARBY", color = AccentGreen, fontSize = 10.sp)
            Spacer(Modifier.height(10.dp))
            Text("Central Park 5-a-side", color = BrandBlue, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(10.dp))
            Text("8/10 Players joined", color = BrandBlue.copy(alpha = 0.7f), fontSize = 12.sp)
        }

        Column(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(10.dp)
                .width(142.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF7F8FA))
                .padding(10.dp)
        ) {
            // --- Avatares empilhados à direita
            Row {
                repeat(3) { idx ->
                    Image(
                        painter = painterResource(R.drawable.avatar_player),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.offset(x = (idx * -5).dp).size(26.dp).clip(CircleShape).border(1.dp, Color.White, CircleShape)
                    )
                }
                // --- Selo "+N"
                Box(
                    Modifier.offset(x = (-12).dp).size(26.dp).clip(CircleShape).background(Color(0xFFC2D2FF)),
                    contentAlignment = Alignment.Center
                ) { Text("+12", color = BrandBlue, fontSize = 9.sp, fontWeight = FontWeight.Bold) }
            }
            Spacer(Modifier.height(8.dp))
            Text("Kings League Team", color = BrandBlue, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(6.dp))
            Text("NEW MATCH INVITE", color = BrandBlue, fontSize = 9.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun Indicators(page: Int, showText: Boolean) {
    // --- Pontos de progresso das páginas de onboarding
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        repeat(3) { idx ->
            val active = idx + 1 == page
            Box(
                Modifier
                    .padding(horizontal = 4.dp)
                    .width(if (active) 42.dp else 8.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (active) BrandBlue else BrandBlue.copy(alpha = 0.25f))
            )
        }
        if (showText) {
            Spacer(Modifier.width(8.dp))
            // --- Rótulo opcional "1 DE 3"
            Text("1 OF 3", fontSize = 9.sp, color = BrandBlue.copy(alpha = 0.6f), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun Desc(text: String, centered: Boolean = false) {
    // --- Estilo partilhado para texto de descrição
    Text(
        text,
        color = BrandBlue.copy(alpha = 0.7f),
        fontSize = 15.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Medium,
        textAlign = if (centered) TextAlign.Center else TextAlign.Start,
        modifier = if (centered) Modifier.fillMaxWidth() else Modifier
    )
}
