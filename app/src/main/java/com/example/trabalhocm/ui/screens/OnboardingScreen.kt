package com.example.trabalhocm.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.trabalhocm.R
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite

private val BrandBlue = Color(0xFF0B1F3A)
private val AccentGreen = Color(0xFF008D7D)

@Composable
fun OnboardingFlow(onFinish: () -> Unit) {
    var page by remember { mutableIntStateOf(1) }

    AnimatedContent(
        targetState = page,
        transitionSpec = {
            if (targetState > initialState) {
                slideInHorizontally(
                    animationSpec = tween(400),
                    initialOffsetX = { fullWidth -> fullWidth }
                ) + fadeIn(tween(400)) togetherWith
                        slideOutHorizontally(
                            animationSpec = tween(400),
                            targetOffsetX = { fullWidth -> -fullWidth }
                        ) + fadeOut(tween(400))
            } else {
                slideInHorizontally(
                    animationSpec = tween(400),
                    initialOffsetX = { fullWidth -> -fullWidth }
                ) + fadeIn(tween(400)) togetherWith
                        slideOutHorizontally(
                            animationSpec = tween(400),
                            targetOffsetX = { fullWidth -> fullWidth }
                        ) + fadeOut(tween(400))
            }
        },
        label = "Onboarding Animation"
    ) { targetPage ->
        when (targetPage) {
            1 -> Onboarding1(onNext = { page = 2 }, onSkip = { page = 3 })
            2 -> Onboarding2(onNext = { page = 3 }, onSkip = { page = 3 })
            else -> Onboarding3(onFinish = onFinish)
        }
    }
}

@Composable
fun Onboarding1(onNext: () -> Unit, onSkip: () -> Unit) {
    OnboardingShell(
        page = 1,
        buttonText = "NEXT >",
        onNext = onNext,
        onSkip = onSkip,
        centeredHeader = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                buildAnnotatedString {
                    append("Manage Every\n")
                    withStyle(SpanStyle(color = AccentGreen)) { append("Tournament.") }
                },
                color = BrandBlue,
                fontSize = 44.sp,
                lineHeight = 50.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(Modifier.height(18.dp))
            Desc("Create leagues, knockout competitions,\nand professional sports events with\nease.", centered = false)
        }
    }
}

@Composable
fun Onboarding2(onNext: () -> Unit, onSkip: () -> Unit) {
    OnboardingShell(
        page = 2,
        buttonText = "NEXT >",
        onNext = onNext,
        onSkip = onSkip,
        centeredHeader = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MatchCard()
            Spacer(Modifier.height(40.dp))
            Text(
                buildAnnotatedString {
                    append("TRACK LIVE\n")
                    withStyle(SpanStyle(color = AccentGreen)) { append("RESULTS.") }
                },
                color = BrandBlue,
                fontSize = 38.sp,
                lineHeight = 44.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(16.dp))
            Desc("Get real-time updates, live standings, and\ndetailed statistics for every match.", centered = true)
        }
    }
}

@Composable
fun Onboarding3(onFinish: () -> Unit) {
    OnboardingShell(
        page = 3,
        buttonText = "GET STARTED",
        onNext = onFinish,
        onSkip = {},
        centeredHeader = true,
        skipVisible = false
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(Modifier.weight(1f))

            Box(Modifier.padding(horizontal = 24.dp)) {
                CommunityTopCard()
            }

            Spacer(Modifier.weight(1f))

            Box(
                Modifier
                    .offset(y = (20).dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(BrandWhite)
                    .padding(horizontal = 24.dp, vertical = 40.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                        Indicators(3)
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "COMMUNITY HUB",
                        color = Color(0xFF7D8497),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.8.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        buildAnnotatedString {
                            append("Join the ")
                            withStyle(SpanStyle(color = AccentGreen)) { append("League.") }
                        },
                        color = BrandBlue,
                        fontSize = 36.sp,
                        lineHeight = 42.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(16.dp))

                    Desc(
                        text = "Invite players, manage teams, and book\ncasual pickup games in your\nneighborhood.",
                        centered = true
                    )

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = onFinish,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                    ) {
                        Text("GET STARTED", color = BrandWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp).background(BrandWhite))
        }
    }
}

@Composable
fun OnboardingShell(
    page: Int,
    buttonText: String,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    centeredHeader: Boolean,
    skipVisible: Boolean = true,
    body: @Composable ColumnScope.() -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 24.dp),
            contentAlignment = if (centeredHeader) Alignment.Center else Alignment.CenterStart
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = BrandBlue)) { append("MATCH") }
                    withStyle(SpanStyle(color = AccentGreen)) { append("LEAGUE") }
                },
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            if (!centeredHeader && skipVisible) {
                Text(
                    text = "SKIP",
                    color = BrandBlue.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clip(RoundedCornerShape(4.dp))
                        .clickable { onSkip() }
                        .padding(8.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            body()
        }

        if (page != 3) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 32.dp)
            ) {
                Indicators(page = page)
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = onNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGreen),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Text(buttonText, color = BrandWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                }
            }
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AccentGreen))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("LIVE • 74'", color = AccentGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(14.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0x1AFFFFFF)))
            Spacer(Modifier.height(20.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                TeamBlock(R.drawable.team_vianense, "VIANENSE")
                Text("2", color = BrandWhite, fontSize = 54.sp, fontWeight = FontWeight.Medium)
                Text(":", color = BrandBlue.copy(alpha = 0.5f), fontSize = 36.sp, modifier = Modifier.padding(bottom = 8.dp))
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
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .padding(6.dp)
        )
        Spacer(Modifier.height(10.dp))
        Text(team, color = BrandWhite, fontSize = 11.sp, fontWeight = FontWeight.Medium)
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
                .padding(14.dp)
                .width(220.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF7F8FA))
                .padding(14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AccentGreen))
                Spacer(modifier = Modifier.width(6.dp))
                Text("LIVE NEARBY", color = AccentGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))
            Text("Central Park 5-a-side", color = BrandBlue, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("👥", fontSize = 10.sp)
                Spacer(Modifier.width(4.dp))
                Text("8/10 Players joined", color = BrandBlue.copy(alpha = 0.7f), fontSize = 12.sp)
            }
        }

        Column(
            Modifier
                .align(Alignment.BottomEnd)
                .padding(14.dp)
                .width(150.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF7F8FA))
                .padding(12.dp)
        ) {
            Row {
                repeat(3) { idx ->
                    Image(
                        painter = painterResource(R.drawable.avatar_player),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .offset(x = (idx * -5).dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .border(1.dp, Color.White, CircleShape)
                    )
                }
                Box(
                    Modifier
                        .offset(x = (-12).dp)
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFC2D2FF)),
                    contentAlignment = Alignment.Center
                ) { Text("+12", color = Color(0xFF0757C8), fontSize = 10.sp, fontWeight = FontWeight.Bold) }
            }
            Spacer(Modifier.height(10.dp))
            Text("Kings League Team", color = BrandBlue, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFEAF0FF))
                    .padding(horizontal = 6.dp, vertical = 4.dp)
            ) {
                Text("NEW MATCH INVITE", color = Color(0xFF0757C8), fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun Indicators(page: Int) {
    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { idx ->
            val active = idx + 1 == page
            Box(
                Modifier
                    .padding(end = 8.dp)
                    .width(if (active) 28.dp else 10.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (active) Color(0xFF0757C8) else Color(0xFFD1D6E0))
            )
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