package com.example.trabalhocm.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.repository.AuthRepository
import com.example.trabalhocm.data.repository.ConviteEquipaInfo
import com.example.trabalhocm.data.repository.EquipaRepository
import com.example.trabalhocm.data.repository.Notificacao
import com.example.trabalhocm.data.repository.PedidoEntradaEquipaInfo
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.OffsetDateTime
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon

@Composable
fun PlayerNotificationsScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf("ALL") }

    val authRepository = remember { AuthRepository() }
    val equipaRepository = remember { EquipaRepository() }
    val scope = rememberCoroutineScope()

    var notificacoes by remember { mutableStateOf<List<Notificacao>>(emptyList()) }
    var convitesEquipa by remember { mutableStateOf<List<ConviteEquipaInfo>>(emptyList()) }
    var pedidosParaCapitao by remember { mutableStateOf<List<PedidoEntradaEquipaInfo>>(emptyList()) }

    var isLoading by remember { mutableStateOf(true) }
    var isActionLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    fun carregarDados() {
        scope.launch {
            isLoading = true
            errorMessage = ""

            authRepository.obterUtilizadorAtual()
                .onSuccess { utilizador ->
                    authRepository.obterNotificacoes(utilizador.id)
                        .onSuccess { lista -> notificacoes = lista }
                        .onFailure { notificacoes = emptyList() }
                }
                .onFailure {
                    notificacoes = emptyList()
                }

            equipaRepository.listarConvitesPendentesDoUtilizador()
                .onSuccess { lista -> convitesEquipa = lista }
                .onFailure {
                    errorMessage = it.message ?: "Erro ao carregar convites."
                    convitesEquipa = emptyList()
                }

            equipaRepository.listarPedidosDeEntradaParaCapitao()
                .onSuccess { lista -> pedidosParaCapitao = lista }
                .onFailure { pedidosParaCapitao = emptyList() }

            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        carregarDados()
    }

    val notificacoesFiltradas = notificacoes.filter { notif ->
        when (selectedTab) {
            "MATCHES" -> notif.tipo.uppercase() == "MATCH" || notif.tipo.uppercase() == "RESULT"
            "TEAMS" -> notif.tipo.uppercase() == "TEAM_INVITE"
            "SYSTEM" -> notif.tipo.uppercase() == "SYSTEM"
            else -> true
        }
    }

    val mostrarConvitesEquipa = selectedTab == "ALL" || selectedTab == "TEAMS"

    val existemItens =
        (mostrarConvitesEquipa && (convitesEquipa.isNotEmpty() || pedidosParaCapitao.isNotEmpty())) ||
                notificacoesFiltradas.isNotEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        NotificationsTopBar(onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 20.dp)
        ) {
            Text(
                text = "UPDATES CENTER",
                color = Color(0xFF4167C8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "NOTIFICATIONS",
                color = BrandBlue,
                fontSize = 28.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(26.dp))

            NotificationsTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (errorMessage.isNotBlank()) {
                NotificationsMessageCard(text = errorMessage, isError = true)
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (successMessage.isNotBlank()) {
                NotificationsMessageCard(text = successMessage, isError = false)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(6.dp))

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BrandGreen)
                    }
                }

                !existemItens -> {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Sem notificações de momento.", color = Color.Gray)
                    }
                }

                else -> {
                    if (mostrarConvitesEquipa) {

                        // 1. Mostrar os pedidos que os JOGADORES fizeram para entrar na Equipa do Capitão
                        pedidosParaCapitao.forEach { pedido ->
                            TeamInvitationNotificationCard(
                                title = "Team Join Request",
                                description = "O jogador ${pedido.nomeJogador} pediu para entrar na tua equipa ${pedido.nomeEquipa}.",
                                time = "PENDING",
                                equipaNome = pedido.nomeEquipa,
                                equipaInfo = null,
                                message = null,
                                isActionLoading = isActionLoading,
                                onAcceptClick = {
                                    scope.launch {
                                        isActionLoading = true
                                        errorMessage = ""
                                        successMessage = ""
                                        equipaRepository.aceitarPedidoDeEntrada(pedido.idEquipa, pedido.idUtilizador)
                                            .onSuccess {
                                                successMessage = "Pedido aceite. O jogador agora faz parte da equipa."
                                                carregarDados()
                                            }
                                            .onFailure { errorMessage = it.message ?: "Erro ao aceitar pedido." }
                                        isActionLoading = false
                                    }
                                },
                                onDeclineClick = {
                                    scope.launch {
                                        isActionLoading = true
                                        errorMessage = ""
                                        successMessage = ""
                                        equipaRepository.recusarPedidoDeEntrada(pedido.idEquipa, pedido.idUtilizador)
                                            .onSuccess {
                                                successMessage = "Pedido recusado e eliminado."
                                                carregarDados()
                                            }
                                            .onFailure { errorMessage = it.message ?: "Erro ao recusar pedido." }
                                        isActionLoading = false
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // 2. Mostrar os convites que O CAPITÃO enviou para o Jogador Logado
                        convitesEquipa.forEach { convite ->
                            TeamInvitationNotificationCard(
                                title = "Team Invitation",
                                description = "Foste convidado para entrar na equipa ${convite.nomeEquipa} como ${convite.posicao}.",
                                time = "PENDING",
                                equipaNome = convite.nomeEquipa,
                                equipaInfo = "${convite.modalidadeNome} · ${convite.divisao}",
                                message = convite.mensagem,
                                isActionLoading = isActionLoading,
                                onAcceptClick = {
                                    scope.launch {
                                        isActionLoading = true
                                        errorMessage = ""
                                        successMessage = ""
                                        equipaRepository.aceitarConviteEquipa(convite.idEquipa)
                                            .onSuccess {
                                                successMessage = "Convite da equipa ${convite.nomeEquipa} aceite."
                                                carregarDados()
                                            }
                                            .onFailure { errorMessage = it.message ?: "Erro ao aceitar convite." }
                                        isActionLoading = false
                                    }
                                },
                                onDeclineClick = {
                                    scope.launch {
                                        isActionLoading = true
                                        errorMessage = ""
                                        successMessage = ""
                                        equipaRepository.recusarConviteEquipa(convite.idEquipa)
                                            .onSuccess {
                                                successMessage = "Convite da equipa ${convite.nomeEquipa} recusado."
                                                carregarDados()
                                            }
                                            .onFailure { errorMessage = it.message ?: "Erro ao recusar convite." }
                                        isActionLoading = false
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    notificacoesFiltradas.forEach { notif ->
                        DesenharNotificacao(notificacao = notif)
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
            EndOfFeed()
        }

        MatchLeagueBottomBar(
            selectedTab = "PROFILE",
            onHomeClick = onHomeClick,
            onTournamentsClick = onTournamentsClick,
            onMatchesClick = onMatchesClick,
            onTeamsClick = onTeamsClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun DesenharNotificacao(notificacao: Notificacao) {
    val tempoCalculado = calcularTempoAtras(notificacao.data)
    val tipo = notificacao.tipo.uppercase()

    when (tipo) {
        "TEAM_INVITE" -> {
            TeamInvitationNotificationCard(
                title = notificacao.titulo,
                description = notificacao.mensagem,
                time = tempoCalculado
            )
        }

        "MATCH" -> {
            NotificationCard(
                icon = "⏱",
                iconColor = Color(0xFF2949FF),
                title = notificacao.titulo,
                time = tempoCalculado,
                description = notificacao.mensagem,
                highlighted = !notificacao.lida,
                unread = !notificacao.lida
            )
        }

        "SYSTEM" -> {
            NotificationCard(
                icon = "☁",
                iconColor = Color(0xFF7D8497),
                title = notificacao.titulo,
                time = tempoCalculado,
                description = notificacao.mensagem,
                highlighted = !notificacao.lida,
                unread = !notificacao.lida
            )
        }

        "RESULT" -> {
            NotificationCard(
                icon = "◎",
                iconColor = Color(0xFF7D8497),
                title = notificacao.titulo,
                time = tempoCalculado,
                description = notificacao.mensagem,
                highlighted = !notificacao.lida,
                unread = !notificacao.lida
            )
        }

        else -> {
            NotificationCard(
                icon = "🔔",
                iconColor = BrandGreen,
                title = notificacao.titulo,
                time = tempoCalculado,
                description = notificacao.mensagem,
                highlighted = !notificacao.lida,
                unread = !notificacao.lida
            )
        }
    }
}

fun calcularTempoAtras(dataCriacao: String): String {
    return try {
        val dataPassada = OffsetDateTime.parse(dataCriacao)
        val agora = OffsetDateTime.now()
        val minutos = Duration.between(dataPassada, agora).toMinutes()

        when {
            minutos < 1 -> "JUST NOW"
            minutos < 60 -> "${minutos}M AGO"
            minutos < 1440 -> "${minutos / 60}H AGO"
            minutos < 2880 -> "YESTERDAY"
            else -> "${minutos / 1440}D AGO"
        }
    } catch (e: Exception) {
        "RECENTLY"
    }
}

@Composable
fun NotificationsTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(BrandBlue)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "←",
            color = BrandWhite,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onBackClick() }
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = "Notifications",
            color = BrandWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.3.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = "Notifications",
            tint = BrandWhite,
            modifier = Modifier.size(26.dp)
        )
    }
}

@Composable
fun NotificationsTabs(selectedTab: String, onTabSelected: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(46.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFFF0F2FA)).padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        NotificationTabButton("ALL", selectedTab == "ALL", { onTabSelected("ALL") }, Modifier.weight(1f))
        NotificationTabButton("MATCHES", selectedTab == "MATCHES", { onTabSelected("MATCHES") }, Modifier.weight(1f))
        NotificationTabButton("TEAMS", selectedTab == "TEAMS", { onTabSelected("TEAMS") }, Modifier.weight(1f))
        NotificationTabButton("SYSTEM", selectedTab == "SYSTEM", { onTabSelected("SYSTEM") }, Modifier.weight(1f))
    }
}

@Composable
fun NotificationTabButton(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().clip(RoundedCornerShape(3.dp)).background(if (selected) Color(0xFF2949FF) else Color.Transparent).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (selected) BrandWhite else Color(0xFF7D8497), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.6.sp)
    }
}

@Composable
fun NotificationCard(icon: String, iconColor: Color, title: String, time: String, description: String, highlighted: Boolean, unread: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            if (highlighted) Box(modifier = Modifier.width(4.dp).height(104.dp).background(Color(0xFF2949FF)))

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 18.dp), verticalAlignment = Alignment.Top) {
                Box(modifier = Modifier.size(42.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFFF0F2FA)), contentAlignment = Alignment.Center) {
                    Text(icon, color = iconColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(title, color = BrandBlue, fontSize = 15.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                        Text(time, color = Color(0xFF8D94A3), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        if (unread) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFF2949FF)))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(description, color = Color(0xFF6D7486), fontSize = 13.sp, lineHeight = 19.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun TeamInvitationNotificationCard(
    title: String, description: String, time: String, equipaNome: String? = null,
    equipaInfo: String? = null, message: String? = null, isActionLoading: Boolean = false,
    onAcceptClick: (() -> Unit)? = null, onDeclineClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 18.dp), verticalAlignment = Alignment.Top) {
            Box(modifier = Modifier.size(42.dp).clip(RoundedCornerShape(3.dp)).background(BrandGreen.copy(alpha = 0.10f)), contentAlignment = Alignment.Center) {
                Text("♙+", color = BrandGreen, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(title, color = BrandBlue, fontSize = 15.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    Text(time, color = Color(0xFF8D94A3), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                }
                if (!equipaNome.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(equipaNome, color = BrandBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                if (!equipaInfo.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(equipaInfo, color = BrandGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(description, color = Color(0xFF6D7486), fontSize = 13.sp, lineHeight = 19.sp, fontWeight = FontWeight.Medium)
                if (!message.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(message, color = Color(0xFF7D8497), fontSize = 12.sp, lineHeight = 17.sp, fontWeight = FontWeight.Medium)
                }

                if (onAcceptClick != null && onDeclineClick != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onAcceptClick, enabled = !isActionLoading, modifier = Modifier.width(82.dp).height(34.dp),
                            shape = RoundedCornerShape(2.dp), colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = BrandWhite)
                        ) { Text("ACCEPT", fontSize = 9.sp, fontWeight = FontWeight.Bold) }
                        OutlinedButton(
                            onClick = onDeclineClick, enabled = !isActionLoading, modifier = Modifier.width(92.dp).height(34.dp),
                            shape = RoundedCornerShape(2.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF7D8497))
                        ) { Text("DECLINE", fontSize = 8.sp, fontWeight = FontWeight.Bold) }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationsMessageCard(text: String, isError: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = if (isError) Color(0xFFFFF0F0) else Color(0xFFEAF7F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(text, color = if (isError) Color(0xFFD01818) else BrandGreen, fontSize = 12.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(12.dp))
    }
}

@Composable
fun EndOfFeed() {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 28.dp, bottom = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = Icons.Outlined.Notifications,
            contentDescription = "End of feed",
            tint = Color(0xFF9EA4B3),
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text("END OF FEED", color = Color(0xFF9EA4B3), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 3.sp)
    }
}

@Preview(showBackground = true, name = "Player Notifications Screen")
@Composable
fun PlayerNotificationsScreenPreview() {
    PlayerNotificationsScreen()
}