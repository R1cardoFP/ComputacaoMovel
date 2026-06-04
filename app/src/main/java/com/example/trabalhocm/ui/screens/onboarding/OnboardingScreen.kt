package com.example.trabalhocm.ui.screens.onboarding

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
private val AccentGreen = Color(0xFF499D82)
private val BgGray = Color(0xFFF9F9FB)
private val TextGray = Color(0xFF6B7280)

@Composable
fun OnboardingFlow(onFinish: () -> Unit) {
    var page by remember { mutableIntStateOf(1) }

    // O AnimatedContent vai tratar de animar qualquer mudança na variável 'page'
    AnimatedContent(
        targetState = page,
        transitionSpec = {
            // Animação: Desliza da direita para a esquerda (junto com um ligeiro fade in/out)
            (slideInHorizontally(
                animationSpec = tween(400),
                initialOffsetX = { fullWidth -> fullWidth }
            ) + fadeIn(animationSpec = tween(400))).togetherWith(
                slideOutHorizontally(
                    animationSpec = tween(400),
                    targetOffsetX = { fullWidth -> -fullWidth }
                ) + fadeOut(animationSpec = tween(400))
            )
        },
        label = "onboarding_animation"
    ) { targetPage ->
        when (targetPage) {
            1 -> Onboarding1(onSkip = onFinish, onNext = { page = 2 })
            2 -> Onboarding2(onSkip = onFinish, onNext = { page = 3 })
            else -> Onboarding3 { onFinish() }
        }
    }
}

@Composable
fun Onboarding1(onSkip: () -> Unit, onNext: () -> Unit) {
    OnboardingShell(page = 1, buttonText = "NEXT ›", onNext = onNext, onSkip = onSkip) {
        Spacer(Modifier.height(110.dp))

        Text(
            buildAnnotatedString {
                append("Manage Every\n")
                withStyle(SpanStyle(color = AccentGreen)) { append("Tournament.") }
            },
            color = BrandBlue,
            fontSize = 46.sp,
            lineHeight = 52.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        )

        Spacer(Modifier.height(24.dp))

        Desc(
            text = "Create leagues, knockout competitions,\nand professional sports events with\nease.",
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
fun Onboarding2(onSkip: () -> Unit, onNext: () -> Unit) {
    OnboardingShell(page = 2, buttonText = "NEXT ›", onNext = onNext, onSkip = onSkip) {
        Spacer(Modifier.height(70.dp))

        MatchCard()

        Spacer(Modifier.height(40.dp))

        Text(
            buildAnnotatedString {
                append("TRACK LIVE\n")
                withStyle(SpanStyle(color = AccentGreen)) { append("RESULTS.") }
            },
            color = BrandBlue,
            fontSize = 40.sp,
            lineHeight = 44.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        Desc(
            text = "Get real-time updates, live standings, and\ndetailed statistics for every match.",
            centered = true,
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun Onboarding3(onFinish: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgGray)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = BrandBlue, fontWeight = FontWeight.Normal)) { append("MATCH") }
                    withStyle(SpanStyle(color = AccentGreen, fontWeight = FontWeight.Normal)) { append("LEAGUE") }
                },
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
        }

        Spacer(Modifier.height(10.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 24.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            CommunityTopCard()
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
            colors = CardDefaults.cardColors(containerColor = BrandWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 36.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "COMMUNITY HUB",
                    color = TextGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.5.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    buildAnnotatedString {
                        append("Join the ")
                        withStyle(SpanStyle(color = AccentGreen)) { append("League.") }
                    },
                    color = BrandBlue,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                Desc(
                    text = "Invite players, manage teams, and book\ncasual pickup games in your\nneighborhood.",
                    centered = true,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                Indicators(page = 3)

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = onFinish,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
                ) {
                    Text(
                        text = "GET STARTED",
                        color = BrandWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.5.sp
                    )
                }
            }
        }
    }
}

@Composable
fun OnboardingShell(
    page: Int,
    buttonText: String,
    onNext: () -> Unit,
    onSkip: () -> Unit,
    body: @Composable ColumnScope.() -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(BgGray)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 28.dp, vertical = 24.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                buildAnnotatedString {
                    withStyle(SpanStyle(color = BrandBlue, fontWeight = FontWeight.Normal)) { append("MATCH") }
                    withStyle(SpanStyle(color = AccentGreen, fontWeight = FontWeight.Normal)) { append("LEAGUE") }
                },
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
            Text(
                text = "SKIP",
                color = TextGray,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onSkip() }
                    .padding(8.dp)
            )
        }

        body()

        Spacer(Modifier.weight(1f))

        Indicators(page = page)

        Spacer(Modifier.height(26.dp))

        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentGreen)
        ) {
            Text(
                text = buttonText,
                color = BrandWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.5.sp
            )
        }
    }
}

@Composable
fun MatchCard() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF131B2A))
            .padding(24.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(AccentGreen)
                )
                Spacer(Modifier.width(8.dp))
                Text("LIVE • 74'", color = AccentGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            }
            Spacer(Modifier.height(24.dp))
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TeamBlock(R.drawable.team_vianense, "VIANE\nNSE")

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("2", color = BrandWhite, fontSize = 48.sp, fontWeight = FontWeight.Medium)
                    Text("  :  ", color = Color(0xFF333E50), fontSize = 28.sp, fontWeight = FontWeight.Medium)
                    Text("1", color = BrandWhite, fontSize = 48.sp, fontWeight = FontWeight.Medium)
                }

                TeamBlock(R.drawable.team_sporting, "SPORT\nING")
            }
        }
    }
}

@Composable
fun TeamBlock(logoRes: Int, team: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(60.dp)
    ) {
        Image(
            painter = painterResource(logoRes),
            contentDescription = team,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(6.dp)
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = team,
            color = BrandWhite,
            fontSize = 10.sp,
            lineHeight = 14.sp,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun CommunityTopCard() {
    Box(
        Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        Column(
            Modifier
                .width(230.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(BrandWhite)
                .padding(18.dp)
                .align(Alignment.TopStart)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(6.dp).clip(CircleShape).background(AccentGreen))
                Spacer(Modifier.width(8.dp))
                Text("LIVE NEARBY", color = AccentGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            }
            Spacer(Modifier.height(16.dp))
            Text("Central Park 5-a-side", color = BrandBlue, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("👥", fontSize = 12.sp)
                Spacer(Modifier.width(6.dp))
                Text("8/10 Players joined", color = TextGray, fontSize = 11.sp)
            }
        }

        Column(
            Modifier
                .align(Alignment.BottomEnd)
                .offset(y = 30.dp)
                .width(170.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(BrandWhite)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(horizontalArrangement = Arrangement.Center) {
                Box(contentAlignment = Alignment.Center) {
                    Row {
                        repeat(3) { idx ->
                            Image(
                                painter = painterResource(R.drawable.avatar_player),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, BrandWhite, CircleShape)
                            )
                            if (idx < 2) Spacer(modifier = Modifier.width((-8).dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE8EEFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+12", color = Color(0xFF3566C9), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(16.dp))
            Text("Kings League Team", color = BrandBlue, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFF3F6FF))
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Text("NEW MATCH INVITE", color = Color(0xFF3566C9), fontSize = 7.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            }
        }
    }
}

@Composable
fun Indicators(page: Int) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val arrangementModifier = if (page == 3) Modifier.fillMaxWidth() else Modifier
        Row(modifier = arrangementModifier, horizontalArrangement = if (page == 3) Arrangement.Center else Arrangement.Start) {
            repeat(3) { idx ->
                val active = idx + 1 == page
                Box(
                    Modifier
                        .padding(end = 6.dp)
                        .width(if (active) 32.dp else 20.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (active) Color(0xFF3566C9) else Color(0xFFC7CBD6))
                )
            }
        }
    }
}

@Composable
fun Desc(text: String, centered: Boolean = false, fontSize: androidx.compose.ui.unit.TextUnit = 14.sp, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = TextGray,
        fontSize = fontSize,
        lineHeight = 24.sp,
        fontWeight = FontWeight.Normal,
        textAlign = if (centered) TextAlign.Center else TextAlign.Start,
        modifier = modifier
    )
}

@Preview(showBackground = true, name = "Onboarding Flow")
@Composable
fun OnboardingPreview() {
    OnboardingFlow(onFinish = {})
}