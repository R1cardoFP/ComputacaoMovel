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
            .background(Color(0xFFF4F5FA))
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
                .padding(horizontal = 20.dp, vertical = 18.dp)
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
                        color = Color(0xFFD01818)
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

                    Text(
                        text = "PICKUP GAME",
                        color = Color(0xFF0757C8),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.3.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = peladinha.descricao ?: "Partida casual",
                        color = BrandBlue,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Friendly casual game at ${peladinha.local ?: "local por definir"}.\nOpen to all levels.",
                        color = Color(0xFF51607A),
                        fontSize = 13.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    CasualDetailsSummaryCard(
                        info = info
                    )

                    Spacer(modifier = Modifier.height(14.dp))

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
                            color = Color(0xFFD01818)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (podeMostrarJoin) {
                        Button(
                            onClick = {
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
                            },
                            enabled = podeEntrar,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandGreen,
                                contentColor = BrandWhite,
                                disabledContainerColor = Color(0xFFD4D9E3),
                                disabledContentColor = Color(0xFF7D8497)
                            )
                        ) {
                            Text(
                                text = when {
                                    isJoining -> "A ENTRAR..."
                                    jaInscrito -> "JÁ ESTÁS INSCRITO"
                                    info.jogadoresInscritos >= peladinha.maxJogadores -> "PARTIDA CHEIA"
                                    else -> "✓  JOIN MATCH"
                                },
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "↗  SHARE MATCH",
                            color = BrandBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
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
            .height(72.dp)
            .background(BrandBlue)
            .padding(horizontal = 20.dp),
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
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(start = 6.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "♧",
            color = BrandWhite,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold
        )
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
        else -> Color(0xFF0757C8)
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
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
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
                    backgroundColor = Color(0xFFEAF0FB),
                    textColor = Color(0xFF0757C8)
                )

                CasualDetailsBadge(
                    text = info.modalidadeNome.uppercase(),
                    backgroundColor = Color(0xFFEFF1F6),
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
                    color = Color(0xFF6D7486),
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
                trackColor = Color(0xFFE8EAF2)
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
            color = Color(0xFF6D7486),
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
        title = "▣  Schedule"
    ) {
        CasualDetailsInfoRow("Date", data ?: "Data por definir")
        CasualDetailsInfoRow("Start Time", hora?.take(5) ?: "Hora por definir")
        CasualDetailsInfoRow("End Time", calcularHoraFim(hora))
        CasualDetailsInfoRow("Duration", "2 hours", valueColor = Color(0xFF0757C8))
    }
}

@Composable
fun CasualDetailsMatchInfoCard(
    modalidadeNome: String,
    preco: Double?
) {
    CasualDetailsSectionCard(
        title = "◉  Match Info"
    ) {
        CasualDetailsInfoRow("Skill Level", "INTERMEDIARY", valueColor = Color(0xFF0757C8))
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
        title = "⌖  Location"
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
            color = Color(0xFF6D7486),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(7.dp))
                .background(Color(0xFFAFC4D4)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFFD01818))
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

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(38.dp),
            shape = RoundedCornerShape(5.dp)
        ) {
            Text(
                text = "⌖  OPEN IN MAPS",
                color = BrandBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CasualDetailsHostCard(
    organizador: Utilizador?
) {
    CasualDetailsSectionCard(
        title = "♙  Host"
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
                    color = Color(0xFF6D7486),
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
        title = "Joined Players"
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFEAF0FB))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$jogadoresInscritos / $maxJogadores",
                    color = Color(0xFF0757C8),
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
                color = Color(0xFF6D7486),
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
            shape = RoundedCornerShape(5.dp)
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
                color = Color(0xFF6D7486),
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
        title = "About this match"
    ) {
        Text(
            text = texto,
            color = Color(0xFF51607A),
            fontSize = 12.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CasualDetailsInfoNotice() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(7.dp))
            .background(Color(0xFFE5F0FF))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(
            text = "ⓘ  Joining this match will reserve a spot. You can cancel up to 2 hours before start time.",
            color = Color(0xFF0757C8),
            fontSize = 11.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun CasualDetailsSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                color = BrandBlue,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

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
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFF6D7486),
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
            .background(Color(0xFFEAF0FB)),
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
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
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