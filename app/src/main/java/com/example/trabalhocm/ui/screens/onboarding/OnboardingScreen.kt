package com.example.trabalhocm.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import com.example.trabalhocm.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable

private val BrandBlue = Color(0xFF0B1F3A)
private val AccentGreen = Color(0xFF008D7D)

@Composable
fun OnboardingFlow(onFinish: () -> Unit) {
    var page by remember { mutableIntStateOf(1) }

    when (page) {
        1 -> Onboarding1(onNext = { page = 2 }, onSkip = onFinish)
        2 -> Onboarding2(onNext = { page = 3 }, onSkip = onFinish)
        else -> Onboarding3 { onFinish() }
    }
}

@Composable
fun Onboarding1(onNext: () -> Unit, onSkip: () -> Unit) {
    OnboardingShell(page = 1, buttonText = "NEXT  →", onNext = onNext, onSkip = onSkip, centeredHeader = false) {
        Spacer(Modifier.height(42.dp))
        Text(
            buildAnnotatedString {
                append("Manage Every\n")
                withStyle(SpanStyle(color = AccentGreen)) { append("Tournament.") }
            },
            color = BrandBlue, fontSize = 48.sp, lineHeight = 54.sp, fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(18.dp))
        Desc("Create leagues, knockout\ncompetitions,\nand professional sports events with\nease.")
    }
}

@Composable
fun Onboarding2(onNext: () -> Unit, onSkip: () -> Unit) {
    OnboardingShell(page = 2, buttonText = "NEXT  >", onNext = onNext, onSkip = onSkip, centeredHeader = false) {
        Spacer(Modifier.height(10.dp))
        MatchCard()
        Spacer(Modifier.height(26.dp))
        Text(
            buildAnnotatedString {
                append("TRACK LIVE ")
                withStyle(SpanStyle(color = AccentGreen)) { append("RESULTS.") }
            },
            color = BrandBlue, fontSize = 42.sp, lineHeight = 48.sp, fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(14.dp))
        Desc("Get real-time updates, live standings, and\ndetailed statistics for every match.", centered = true)
    }
}

@Composable
fun Onboarding3(onFinish: () -> Unit) {
    OnboardingShell(page = 3, buttonText = "GET STARTED", onNext = onFinish, centeredHeader = true, skipVisible = false) {
        Spacer(Modifier.height(14.dp))
        CommunityTopCard()
        Spacer(Modifier.height(16.dp))
        Box(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .background(Color(0xFFF7F8FA)).padding(horizontal = 18.dp, vertical = 20.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Indicators(3, showText = false)
                Spacer(Modifier.height(14.dp))
                Text("COMMUNITY HUB", color = BrandBlue.copy(alpha = 0.55f), fontSize = 10.sp, letterSpacing = 1.8.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    buildAnnotatedString {
                        append("Join the ")
                        withStyle(SpanStyle(color = AccentGreen)) { append("League.") }
                    },
                    color = BrandBlue, fontSize = 42.sp, lineHeight = 48.sp, fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(14.dp))
                Desc("Invite players, manage teams, and book\ncasual pickup games in your\nneighborhood.", centered = true)
                Spacer(Modifier.height(18.dp))
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
fun OnboardingShell(
    page: Int,
    buttonText: String,
    onNext: () -> Unit,
    onSkip: () -> Unit = {},
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
                fontSize = 15.sp, fontWeight = FontWeight.Medium
            )
            if (!centeredHeader && skipVisible) {
                Text(
                    "SKIP",
                    color = BrandBlue.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onSkip() }
                        .padding(8.dp)
                )
            }
        }
        body()
        Spacer(Modifier.weight(1f))
        if (page != 3) {
            Indicators(page, showText = false)
            Spacer(Modifier.height(18.dp))
            Button(
                onClick = onNext, modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = BrandGreen)
            ) { Text(buttonText, color = BrandWhite, fontSize = 14.sp, letterSpacing = 1.5.sp) }
        }
    }
}

@Composable
fun MatchCard() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF0C1F41))
            .padding(16.dp)
    ) {
        Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("LIVE · 74'", color = AccentGreen, fontSize = 11.sp)
                Text("PREMIER LEAGUE", color = BrandBlue.copy(alpha = 0.55f), fontSize = 10.sp)
            }
            Spacer(Modifier.height(14.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0x33FFFFFF)))
            Spacer(Modifier.height(14.dp))
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
fun TeamBlock(logoRes: Int, team: String) {
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
fun CommunityTopCard() {
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
            Row {
                repeat(3) { idx ->
                    Image(
                        painter = painterResource(R.drawable.avatar_player),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.offset(x = (idx * -5).dp).size(26.dp).clip(CircleShape).border(1.dp, Color.White, CircleShape)
                    )
                }
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
fun Indicators(page: Int, showText: Boolean) {
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
            Text("1 OF 3", fontSize = 9.sp, color = BrandBlue.copy(alpha = 0.6f), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun Desc(text: String, centered: Boolean = false) {
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

@Preview(showBackground = true, name = "Onboarding Flow")
@Composable
fun OnboardingPreview() {
    OnboardingFlow(onFinish = {})
}