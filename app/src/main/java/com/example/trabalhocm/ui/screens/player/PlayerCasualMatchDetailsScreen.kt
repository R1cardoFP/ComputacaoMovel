package com.example.trabalhocm.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.model.Utilizador
import com.example.trabalhocm.data.repository.PeladinhaDetalhesInfo
import com.example.trabalhocm.data.repository.PeladinhaRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import kotlinx.coroutines.launch

private val CasualDetailsBg = Color(0xFFF6F7FB)
private val CasualDetailsCardBg = Color.White
private val CasualDetailsInputBg = Color(0xFFF0F3F8)
private val CasualDetailsTextGray = Color(0xFF657089)
private val CasualDetailsMuted = Color(0xFF8A92A6)
private val CasualDetailsDarkCard = Color(0xFF111827)
private val CasualDetailsBlue = Color(0xFF0757C8)
private val CasualDetailsRed = Color(0xFFE53935)

@Composable
fun PlayerCasualMatchDetailsScreen(
    idPeladinha: Long,
    onBackClick: () -> Unit = {},
    onJoinSuccess: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { PeladinhaRepository() }
    val scope = rememberCoroutineScope()

    var detalhes by remember { mutableStateOf<PeladinhaDetalhesInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isJoining by remember { mutableStateOf(false) }
    var mensagemErro by remember { mutableStateOf("") }
    var mensagemSucesso by remember { mutableStateOf("") }

    fun carregarDetalhes() {
        scope.launch {
            isLoading = true
            mensagemErro = ""

            repository.obterDetalhesPeladinha(idPeladinha)
                .onSuccess {
                    detalhes = it
                }
                .onFailure {
                    mensagemErro = it.message ?: "Erro ao carregar detalhes da partida."
                }

            isLoading = false
        }
    }

    LaunchedEffect(idPeladinha) {
        carregarDetalhes()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CasualDetailsBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        CasualMatchDetailsTopBar(
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 18.dp)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BrandGreen)
                    }
                }

                mensagemErro.isNotBlank() -> {
                    CasualDetailsMessageCard(
                        text = "Erro: $mensagemErro",
                        color = CasualDetailsRed
                    )
                }

                detalhes == null -> {
                    CasualDetailsMessageCard(
                        text = "Partida casual não encontrada.",
                        color = BrandBlue
                    )
                }

                else -> {
                    val info = detalhes!!
                    val peladinha = info.peladinha
                    val estadoNormalizado = peladinha.estado.lowercase()
                    val jaInscrito = info.participantes.any {
                        it.id == info.utilizadorAtualId
                    }

                    val podeMostrarJoin =
                        estadoNormalizado == "aberta" ||
                                estadoNormalizado == "em_direto" ||
                                estadoNormalizado == "live"

                    val podeEntrar =
                        podeMostrarJoin &&
                                !jaInscrito &&
                                !isJoining &&
                                info.jogadoresInscritos < peladinha.maxJogadores

                    CasualDetailsHeroCard(
                        info = info,
                        jaInscrito = jaInscrito
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    CasualDetailsScheduleCard(
                        data = peladinha.data,
                        hora = peladinha.hora
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CasualDetailsMatchInfoCard(
                        modalidadeNome = info.modalidadeNome,
                        preco = peladinha.preco
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CasualDetailsLocationCard(
                        local = peladinha.local
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CasualDetailsHostCard(
                        organizador = info.organizador
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CasualDetailsPlayersCard(
                        participantes = info.participantes,
                        utilizadorAtualId = info.utilizadorAtualId,
                        jogadoresInscritos = info.jogadoresInscritos,
                        maxJogadores = peladinha.maxJogadores
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    CasualDetailsAboutCard(
                        info = info
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    CasualDetailsInfoNotice()

                    if (mensagemSucesso.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))

                        CasualDetailsMessageCard(
                            text = mensagemSucesso,
                            color = BrandGreen
                        )
                    }

                    if (mensagemErro.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))

                        CasualDetailsMessageCard(
                            text = mensagemErro,
                            color = CasualDetailsRed
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    CasualDetailsActionsCard(
                        podeMostrarJoin = podeMostrarJoin,
                        podeEntrar = podeEntrar,
                        isJoining = isJoining,
                        jaInscrito = jaInscrito,
                        jogadoresInscritos = info.jogadoresInscritos,
                        maxJogadores = peladinha.maxJogadores,
                        onJoinClick = {
                            scope.launch {
                                isJoining = true
                                mensagemErro = ""
                                mensagemSucesso = ""

                                repository.entrarNaPeladinha(idPeladinha)
                                    .onSuccess {
                                        mensagemSucesso = "Inscrição realizada com sucesso."
                                        carregarDetalhes()
                                        onJoinSuccess()
                                    }
                                    .onFailure {
                                        mensagemErro = it.message ?: "Erro ao entrar na partida."
                                    }

                                isJoining = false
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        MatchLeagueBottomBar(
            selectedTab = "MATCHES",
            onHomeClick = onHomeClick,
            onTournamentsClick = onTournamentsClick,
            onMatchesClick = onMatchesClick,
            onTeamsClick = onTeamsClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun CasualMatchDetailsTopBar(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
            .background(CasualDetailsDarkCard)
            .padding(horizontal = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "‹",
            color = BrandWhite,
            fontSize = 34.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { onBackClick() }
        )

        Text(
            text = "Details",
            color = BrandWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(start = 8.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "♧",
            color = BrandWhite,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CasualDetailsHeroCard(
    info: PeladinhaDetalhesInfo,
    jaInscrito: Boolean
) {
    val peladinha = info.peladinha
    val estadoNormalizado = peladinha.estado.lowercase()
    val progresso = if (peladinha.maxJogadores > 0) {
        info.jogadoresInscritos.toFloat() / peladinha.maxJogadores.toFloat()
    } else {
        0f
    }

    val statusText = when (estadoNormalizado) {
        "aberta" -> "● OPEN"
        "fechada" -> "● CLOSED"
        "terminada" -> "● FINISHED"
        "em_direto", "live" -> "● LIVE NOW"
        else -> "● ${peladinha.estado.uppercase()}"
    }

    val statusColor = when (estadoNormalizado) {
        "aberta", "em_direto", "live" -> BrandGreen
        "fechada" -> Color(0xFFD39A00)
        "terminada" -> CasualDetailsMuted
        else -> CasualDetailsBlue
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = CasualDetailsDarkCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(54.dp)
                        .height(54.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.13f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "⚡",
                        color = BrandWhite,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "PICKUP GAME",
                        color = BrandGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = peladinha.descricao ?: "Partida casual",
                        color = BrandWhite,
                        fontSize = 22.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Friendly casual game at ${peladinha.local ?: "local por definir"}. Open to all levels.",
                color = Color(0xFFDCE3F2),
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CasualDetailsBadge(
                    text = statusText,
                    backgroundColor = statusColor.copy(alpha = 0.16f),
                    textColor = statusColor
                )

                CasualDetailsBadge(
                    text = info.modalidadeNome.uppercase(),
                    backgroundColor = Color.White.copy(alpha = 0.11f),
                    textColor = Color(0xFFDCE3F2)
                )

                if (jaInscrito) {
                    CasualDetailsBadge(
                        text = "YOU JOINED",
                        backgroundColor = BrandGreen.copy(alpha = 0.16f),
                        textColor = BrandGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CasualDetailsHeroMetric(
                    label = "SPOTS LEFT",
                    value = (peladinha.maxJogadores - info.jogadoresInscritos).coerceAtLeast(0).toString(),
                    modifier = Modifier.weight(1f)
                )
                CasualDetailsHeroMetric(
                    label = "JOINED",
                    value = "${info.jogadoresInscritos}/${peladinha.maxJogadores}",
                    modifier = Modifier.weight(1f)
                )
                CasualDetailsHeroMetric(
                    label = "COST",
                    value = if (peladinha.preco != null && peladinha.preco > 0.0) {
                        "€${"%.0f".format(peladinha.preco)}"
                    } else {
                        "FREE"
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "REGISTRATION",
                    color = Color(0xFFDCE3F2),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${info.jogadoresInscritos}/${peladinha.maxJogadores}",
                    color = BrandWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(7.dp))

            LinearProgressIndicator(
                progress = { progresso.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(20.dp)),
                color = statusColor,
                trackColor = Color.White.copy(alpha = 0.13f)
            )
        }
    }
}

@Composable
fun CasualDetailsHeroMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.13f))
            .padding(horizontal = 10.dp, vertical = 12.dp)
    ) {
        Column {
            Text(
                text = value,
                color = BrandWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = label,
                color = Color(0xFFDCE3F2),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.7.sp
            )
        }
    }
}

@Composable
fun CasualDetailsSummaryCard(
    info: PeladinhaDetalhesInfo
) {
    val peladinha = info.peladinha
    val estadoNormalizado = peladinha.estado.lowercase()

    val progresso = if (peladinha.maxJogadores > 0) {
        info.jogadoresInscritos.toFloat() / peladinha.maxJogadores.toFloat()
    } else {
        0f
    }

    val statusText = when (estadoNormalizado) {
        "aberta" -> "● OPEN"
        "fechada" -> "● CLOSED"
        "terminada" -> "● FINISHED"
        "em_direto", "live" -> "● LIVE NOW"
        else -> "● ${peladinha.estado.uppercase()}"
    }

    val statusColor = when (estadoNormalizado) {
        "aberta" -> BrandGreen
        "em_direto", "live" -> BrandGreen
        "fechada" -> Color(0xFFD39A00)
        "terminada" -> Color(0xFF7D8497)
        else -> CasualDetailsBlue
    }

    val registrationText = when (estadoNormalizado) {
        "aberta" -> "OPEN REGISTRATION"
        "fechada" -> "REGISTRATION CLOSED"
        "terminada" -> "FINISHED"
        "em_direto", "live" -> "LIVE MATCH"
        else -> peladinha.estado.uppercase()
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CasualDetailsCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CasualDetailsBadge(
                    text = statusText,
                    backgroundColor = statusColor.copy(alpha = 0.12f),
                    textColor = statusColor
                )

                CasualDetailsBadge(
                    text = registrationText,
                    backgroundColor = CasualDetailsBlue.copy(alpha = 0.10f),
                    textColor = CasualDetailsBlue
                )

                CasualDetailsBadge(
                    text = info.modalidadeNome.uppercase(),
                    backgroundColor = CasualDetailsInputBg,
                    textColor = Color(0xFF6D7486)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CasualDetailsMiniStat(
                    label = "SPOTS LEFT",
                    value = (peladinha.maxJogadores - info.jogadoresInscritos)
                        .coerceAtLeast(0)
                        .toString()
                )

                CasualDetailsMiniStat(
                    label = "JOINED",
                    value = info.jogadoresInscritos.toString()
                )

                CasualDetailsMiniStat(
                    label = "CAPACITY",
                    value = peladinha.maxJogadores.toString()
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "REGISTRATION",
                    color = CasualDetailsTextGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "${info.jogadoresInscritos}/${peladinha.maxJogadores}",
                    color = BrandBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progresso.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(20.dp)),
                color = statusColor,
                trackColor = CasualDetailsInputBg
            )
        }
    }
}

@Composable
fun CasualDetailsBadge(
    text: String,
    backgroundColor: Color,
    textColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .background(backgroundColor)
            .padding(horizontal = 9.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.7.sp
        )
    }
}

@Composable
fun CasualDetailsMiniStat(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            color = CasualDetailsTextGray,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            color = BrandBlue,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CasualDetailsScheduleCard(
    data: String?,
    hora: String?
) {
    CasualDetailsSectionCard(
        title = "Schedule",
        icon = "▣"
    ) {
        CasualDetailsInfoRow("Date", data ?: "Data por definir")
        CasualDetailsInfoRow("Start Time", hora?.take(5) ?: "Hora por definir")
        CasualDetailsInfoRow("End Time", calcularHoraFim(hora))
        CasualDetailsInfoRow("Duration", "2 hours", valueColor = CasualDetailsBlue)
    }
}

@Composable
fun CasualDetailsMatchInfoCard(
    modalidadeNome: String,
    preco: Double?
) {
    CasualDetailsSectionCard(
        title = "Match Info",
        icon = "◉"
    ) {
        CasualDetailsInfoRow("Skill Level", "INTERMEDIARY", valueColor = CasualDetailsBlue)
        CasualDetailsInfoRow(
            "Format",
            if (modalidadeNome.contains("voleibol", ignoreCase = true) ||
                modalidadeNome.contains("volleyball", ignoreCase = true)
            ) {
                "4 vs 4"
            } else {
                "5 vs 5"
            }
        )
        CasualDetailsInfoRow("Equipment", "Provided")
        CasualDetailsInfoRow(
            "Cost per Player",
            if (preco != null && preco > 0.0) {
                "€ ${"%.2f".format(preco)}"
            } else {
                "€ 5.00"
            },
            valueColor = BrandGreen
        )
    }
}

@Composable
fun CasualDetailsLocationCard(
    local: String?
) {
    CasualDetailsSectionCard(
        title = "Location",
        icon = "⌖"
    ) {
        Text(
            text = local ?: "Local por definir",
            color = BrandBlue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Av. 25 de abril, Viana do Castelo",
            color = CasualDetailsTextGray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFD8E1EE)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(CasualDetailsRed)
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⌖  ${local ?: "Local"}",
                    color = BrandWhite,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CasualDetailsHostCard(
    organizador: Utilizador?
) {
    CasualDetailsSectionCard(
        title = "Host",
        icon = "♙"
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            CasualDetailsAvatar(
                name = organizador?.nome ?: "Organizador"
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = organizador?.nome ?: "Organizador",
                    color = BrandBlue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Hosted 14 matches  ·  ★ 4.8",
                    color = CasualDetailsTextGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CasualDetailsPlayersCard(
    participantes: List<Utilizador>,
    utilizadorAtualId: String?,
    jogadoresInscritos: Int,
    maxJogadores: Int
) {
    CasualDetailsSectionCard(
        title = "Joined Players",
        icon = "👥"
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(CasualDetailsBlue.copy(alpha = 0.10f))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$jogadoresInscritos / $maxJogadores",
                    color = CasualDetailsBlue,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val listaMostrar = if (participantes.isEmpty()) {
            emptyList()
        } else {
            participantes.take(4)
        }

        if (listaMostrar.isEmpty()) {
            Text(
                text = "Ainda não existem jogadores inscritos.",
                color = CasualDetailsTextGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        } else {
            listaMostrar.forEach { participante ->
                CasualDetailsPlayerRow(
                    participante = participante,
                    isCurrentUser = participante.id == utilizadorAtualId
                )

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        OutlinedButton(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "LOAD MORE",
                color = BrandBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CasualDetailsPlayerRow(
    participante: Utilizador,
    isCurrentUser: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        CasualDetailsAvatar(
            name = participante.nome
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = participante.nome,
                color = BrandBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = if (isCurrentUser) "Joined 2h ago" else "Joined yesterday",
                color = CasualDetailsTextGray,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium
            )
        }

        if (isCurrentUser) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(BrandGreen.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "YOU",
                    color = BrandGreen,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CasualDetailsAboutCard(
    info: PeladinhaDetalhesInfo
) {
    val peladinha = info.peladinha

    val nomePartida = peladinha.descricao ?: "partida casual"
    val local = peladinha.local ?: "local por definir"
    val modalidade = info.modalidadeNome.lowercase()
    val precoTexto = if (peladinha.preco != null && peladinha.preco > 0.0) {
        "O custo por jogador é de € ${"%.2f".format(peladinha.preco)}."
    } else {
        "A participação não tem custo associado."
    }

    val texto = when {
        modalidade.contains("voleibol") || modalidade.contains("volleyball") -> {
            "$nomePartida é uma partida casual de voleibol em $local. Está aberta a jogadores de vários níveis. O objetivo é criar equipas equilibradas, jogar de forma descontraída e garantir uma boa experiência para todos. $precoTexto"
        }

        modalidade.contains("futebol") || modalidade.contains("football") -> {
            "$nomePartida é uma partida casual de futebol em $local. Os jogadores inscritos serão organizados em equipas antes do início da partida. Recomenda-se chegar alguns minutos antes da hora marcada. $precoTexto"
        }

        modalidade.contains("basquetebol") || modalidade.contains("basketball") -> {
            "$nomePartida é uma partida casual de basquetebol em $local. A partida destina-se a jogadores que queiram competir de forma simples e informal. $precoTexto"
        }

        else -> {
            "$nomePartida é uma partida casual em $local. A inscrição reserva uma vaga para o jogador e permite participar na atividade organizada pelo anfitrião. $precoTexto"
        }
    }

    CasualDetailsSectionCard(
        title = "About this match",
        icon = "i"
    ) {
        Text(
            text = texto,
            color = CasualDetailsTextGray,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CasualDetailsActionsCard(
    podeMostrarJoin: Boolean,
    podeEntrar: Boolean,
    isJoining: Boolean,
    jaInscrito: Boolean,
    jogadoresInscritos: Int,
    maxJogadores: Int,
    onJoinClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CasualDetailsCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Ações",
                color = BrandBlue,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (podeMostrarJoin) {
                Button(
                    onClick = onJoinClick,
                    enabled = podeEntrar,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen,
                        contentColor = BrandWhite,
                        disabledContainerColor = Color(0xFFD4D9E3),
                        disabledContentColor = CasualDetailsMuted
                    )
                ) {
                    Text(
                        text = when {
                            isJoining -> "A ENTRAR..."
                            jaInscrito -> "JÁ ESTÁS INSCRITO"
                            jogadoresInscritos >= maxJogadores -> "PARTIDA CHEIA"
                            else -> "✓  JOIN MATCH"
                        },
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CasualDetailsInputBg)
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Esta partida já não aceita novas inscrições.",
                        color = CasualDetailsTextGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun CasualDetailsInfoNotice() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CasualDetailsBlue.copy(alpha = 0.10f))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(
            text = "ⓘ  Joining this match will reserve a spot. You can cancel up to 2 hours before start time.",
            color = CasualDetailsBlue,
            fontSize = 11.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CasualDetailsSectionCard(
    title: String,
    icon: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CasualDetailsCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(34.dp)
                        .height(34.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(BrandGreen.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = icon,
                        color = BrandGreen,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    color = BrandBlue,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
    }
}

@Composable
fun CasualDetailsInfoRow(
    label: String,
    value: String,
    valueColor: Color = BrandBlue
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = CasualDetailsTextGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = value,
            color = valueColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CasualDetailsAvatar(
    name: String
) {
    Box(
        modifier = Modifier
            .height(42.dp)
            .width(42.dp)
            .clip(CircleShape)
            .background(CasualDetailsBlue.copy(alpha = 0.10f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = casualDetailsInitials(name),
            color = BrandBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CasualDetailsMessageCard(
    text: String,
    color: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CasualDetailsCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 13.sp,
            modifier = Modifier.padding(18.dp),
            fontWeight = FontWeight.Medium
        )
    }
}

fun calcularHoraFim(hora: String?): String {
    if (hora.isNullOrBlank()) {
        return "Hora por definir"
    }

    val partes = hora.take(5).split(":")

    if (partes.size != 2) {
        return "Hora por definir"
    }

    val horas = partes[0].toIntOrNull() ?: return "Hora por definir"
    val minutos = partes[1]

    val horaFim = (horas + 2).coerceAtMost(23)

    return "${horaFim.toString().padStart(2, '0')}:$minutos"
}

fun casualDetailsInitials(name: String): String {
    val words = name.split(" ").filter { it.isNotBlank() }

    return when {
        words.isEmpty() -> "?"
        words.size == 1 -> words.first().take(2).uppercase()
        else -> words.take(2).joinToString("") { it.first().uppercaseChar().toString() }
    }
}

@Preview(showBackground = true)
@Composable
fun PlayerCasualMatchDetailsScreenPreview() {
    PlayerCasualMatchDetailsScreen(idPeladinha = 1)
}