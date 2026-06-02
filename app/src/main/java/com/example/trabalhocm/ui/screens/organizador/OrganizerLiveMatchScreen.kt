package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar

// Import das tuas cores centralizadas
import com.example.trabalhocm.ui.theme.*

enum class EventType { GOAL, HALF_TIME, YELLOW_CARD, SUBSTITUTION, SAVE, START }

data class MatchEvent(
    val time: String,
    val type: EventType,
    val title: String,
    val player: String,
    val description: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrganizerLiveMatchScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    // 1. Transformámos a lista estática numa lista reativa (mutableStateListOf)
    val matchEvents = remember {
        mutableStateListOf(
            MatchEvent("68'", EventType.GOAL, "GOAL!", "M. Rashford", "Clinical finish into the bottom left corner."),
            MatchEvent("55'", EventType.GOAL, "GOAL!", "B. Fernandes", "Assisted by Casemiro."),
            MatchEvent("45'", EventType.HALF_TIME, "HALF TIME", "Teams heading into the tunnel."),
            MatchEvent("38'", EventType.YELLOW_CARD, "YELLOW CARD", "L. Shaw", "Tactical foul to stop the counter attack."),
            MatchEvent("24'", EventType.SUBSTITUTION, "SUBSTITUTION", "A. Garnacho", "Replaces Antony due to a minor injury."),
            MatchEvent("12'", EventType.SAVE, "GREAT SAVE", "A. Onana", "Brilliant diving save to deny a powerful long-range effort.")
        )
    }

    // 2. Controlar se o botão já foi clicado para o esconder depois
    var hasLoadedMore by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Matches", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        bottomBar = {
            MatchLeagueBottomBar(
                selectedTab = "MATCHES",
                onHomeClick = onHomeClick,
                onTournamentsClick = onTournamentsClick,
                onMatchesClick = onMatchesClick,
                onTeamsClick = onTeamsClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = BgLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            LiveScoreCard()

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Match Events", color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Surface(color = PrimaryBlue.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                            Text("REAL-TIME", color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    matchEvents.forEach { event ->
                        EventRow(event)
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = InputBg)
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // 3. A Lógica do Botão
                    if (!hasLoadedMore) {
                        Button(
                            onClick = {
                                // Adiciona novos eventos ao fundo da lista
                                matchEvents.add(MatchEvent("08'", EventType.YELLOW_CARD, "YELLOW CARD", "D. Dalot", "Late challenge on the winger."))
                                matchEvents.add(MatchEvent("04'", EventType.SAVE, "GREAT SAVE", "D. Costa", "Crucial early block."))
                                matchEvents.add(MatchEvent("00'", EventType.START, "KICK-OFF", "Referee", "The match gets underway!"))
                                hasLoadedMore = true // Esconde o botão
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = TealGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Text("LOAD MORE EVENTS", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                        }
                    } else {
                        // Mensagem simples quando já não há mais eventos
                        Text(
                            text = "NO MORE EVENTS",
                            color = TextGray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("MATCHES CALENDAR", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                }

                OutlinedButton(
                    onClick = { },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, PrimaryBlue),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("MATCH HISTORY", color = PrimaryBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                }

                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CASUAL MATCHES", fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LiveScoreCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = ErrorRed, shape = RoundedCornerShape(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("LIVE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("75'", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("SLB", color = ErrorRed, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("BENFICA", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Text("2 : 1", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = 4.sp)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(56.dp).clip(CircleShape).background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("FCP", color = PrimaryBlue, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("PORTO", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun EventRow(event: MatchEvent) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top // O verticalAlignment aqui está perfeitinho agora!
    ) {
        Text(
            text = event.time,
            color = PrimaryBlue,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.width(32.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(modifier = Modifier.padding(top = 2.dp)) {
            when (event.type) {
                EventType.GOAL -> Icon(Icons.Default.CheckCircle, contentDescription = null, tint = TealGreen, modifier = Modifier.size(16.dp))
                EventType.HALF_TIME -> Icon(Icons.Default.Info, contentDescription = null, tint = TealGreen, modifier = Modifier.size(16.dp))
                EventType.YELLOW_CARD -> Box(modifier = Modifier.size(12.dp).background(Color(0xFFFACC15), RoundedCornerShape(2.dp)))
                EventType.SUBSTITUTION -> Icon(Icons.Default.Refresh, contentDescription = null, tint = TealGreen, modifier = Modifier.size(16.dp))
                EventType.SAVE -> Icon(Icons.Default.Star, contentDescription = null, tint = TealGreen, modifier = Modifier.size(16.dp))
                EventType.START -> Icon(Icons.Default.Info, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(event.title, color = DarkBlue, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
            Text(event.player, color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            if (event.description != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(event.description, color = TextGray, fontSize = 10.sp, lineHeight = 14.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrganizerLiveMatchScreenPreview() {
    MaterialTheme {
        OrganizerLiveMatchScreen()
    }
}