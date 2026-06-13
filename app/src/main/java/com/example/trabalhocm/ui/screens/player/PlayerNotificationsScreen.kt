package com.example.trabalhocm.ui.screens.player

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
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

private val PlayerBgLight = Color(0xFFF6F8FB)
private val PlayerCardBg = Color.White
private val PlayerInputBg = Color(0xFFF0F3F8)
private val PlayerTextGray = Color(0xFF697386)
private val PlayerMutedGray = Color(0xFF98A1B2)
private val PlayerPrimaryBlue = Color(0xFF2949FF)
private val PlayerErrorBg = Color(0xFFFFF0F0)
private val PlayerSuccessBg = Color(0xFFEAF7F5)

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
    val context = LocalContext.current

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
            .background(PlayerBgLight)
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
            NotificationsHeaderCard(
                totalNotifications = notificacoes.size,
                teamItems = convitesEquipa.size + pedidosParaCapitao.size
            )

            Spacer(modifier = Modifier.height(18.dp))

            NotificationsTabs(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage.isNotBlank()) {
                NotificationsMessageCard(text = errorMessage, isError = true)
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (successMessage.isNotBlank()) {
                NotificationsMessageCard(text = successMessage, isError = false)
                Spacer(modifier = Modifier.height(12.dp))
            }

            when {
                isLoading -> {
                    LoadingNotificationsCard()
                }

                !existemItens -> {
                    EmptyNotificationsCard()
                }

                else -> {
                    if (mostrarConvitesEquipa) {

                        // 1. Mostrar os pedidos que os JOGADORES fizeram para entrar na Equipa do Capitão
                        pedidosParaCapitao.forEach { pedido ->
                            TeamInvitationNotificationCard(
                                title = stringResource(R.string.player_notif_join_request_title),
                                description = stringResource(R.string.player_notif_join_request_desc, pedido.nomeJogador, pedido.nomeEquipa),
                                time = stringResource(R.string.player_common_pending),
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
                                                successMessage = context.getString(R.string.player_notif_success_request_accepted)
                                                carregarDados()
                                            }
                                            .onFailure { errorMessage = it.message ?: context.getString(R.string.player_notif_err_accept_request) }
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
                                                successMessage = context.getString(R.string.player_notif_success_request_declined)
                                                carregarDados()
                                            }
                                            .onFailure { errorMessage = it.message ?: context.getString(R.string.player_notif_err_decline_request) }
                                        isActionLoading = false
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // 2. Mostrar os convites que O CAPITÃO enviou para o Jogador Logado
                        convitesEquipa.forEach { convite ->
                            TeamInvitationNotificationCard(
                                title = stringResource(R.string.player_notif_invitation_title),
                                description = stringResource(R.string.player_notif_invitation_desc, convite.nomeEquipa, convite.posicao),
                                time = stringResource(R.string.player_common_pending),
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
                                                successMessage = context.getString(R.string.player_notif_success_invite_accepted, convite.nomeEquipa)
                                                carregarDados()
                                            }
                                            .onFailure { errorMessage = it.message ?: context.getString(R.string.player_notif_err_accept_invite) }
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
                                                successMessage = context.getString(R.string.player_notif_success_invite_declined, convite.nomeEquipa)
                                                carregarDados()
                                            }
                                            .onFailure { errorMessage = it.message ?: context.getString(R.string.player_notif_err_decline_invite) }
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
                iconColor = PlayerPrimaryBlue,
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
                iconColor = PlayerMutedGray,
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
                iconColor = PlayerMutedGray,
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

@Composable
fun calcularTempoAtras(dataCriacao: String): String {
    val minutos: Long? = try {
        val dataPassada = OffsetDateTime.parse(dataCriacao)
        Duration.between(dataPassada, OffsetDateTime.now()).toMinutes()
    } catch (e: Exception) {
        null
    }

    return when {
        minutos == null -> stringResource(R.string.player_notif_recently)
        minutos < 1 -> stringResource(R.string.player_notif_just_now)
        minutos < 60 -> stringResource(R.string.player_notif_minutes_ago, minutos)
        minutos < 1440 -> stringResource(R.string.player_notif_hours_ago, minutos / 60)
        minutos < 2880 -> stringResource(R.string.player_notif_yesterday)
        else -> stringResource(R.string.player_notif_days_ago, minutos / 1440)
    }
}

@Composable
fun NotificationsTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(BrandBlue)
            .padding(horizontal = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(BrandWhite.copy(alpha = 0.12f))
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "‹",
                color = BrandWhite,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.player_common_notifications),
                color = BrandWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.player_notif_eyebrow),
                color = BrandWhite.copy(alpha = 0.72f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(BrandWhite.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = stringResource(R.string.player_common_notifications),
                tint = BrandWhite,
                modifier = Modifier.size(23.dp)
            )
        }
    }
}

@Composable
fun NotificationsHeaderCard(totalNotifications: Int, teamItems: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(BrandWhite.copy(alpha = 0.13f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                        tint = BrandWhite,
                        modifier = Modifier.size(25.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.player_notif_title),
                        color = BrandWhite,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 29.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.player_notif_eyebrow),
                        color = BrandWhite.copy(alpha = 0.74f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.6.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                HeaderStatPill(
                    label = stringResource(R.string.player_notif_tab_all),
                    value = totalNotifications.toString(),
                    modifier = Modifier.weight(1f)
                )
                HeaderStatPill(
                    label = stringResource(R.string.player_notif_tab_teams),
                    value = teamItems.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun HeaderStatPill(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(BrandWhite.copy(alpha = 0.10f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value,
            color = BrandWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label.uppercase(),
            color = BrandWhite.copy(alpha = 0.78f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun NotificationsTabs(selectedTab: String, onTabSelected: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PlayerCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            NotificationTabButton(stringResource(R.string.player_notif_tab_all), selectedTab == "ALL", { onTabSelected("ALL") }, Modifier.weight(1f))
            NotificationTabButton(stringResource(R.string.player_notif_tab_matches), selectedTab == "MATCHES", { onTabSelected("MATCHES") }, Modifier.weight(1f))
            NotificationTabButton(stringResource(R.string.player_notif_tab_teams), selectedTab == "TEAMS", { onTabSelected("TEAMS") }, Modifier.weight(1f))
            NotificationTabButton(stringResource(R.string.player_notif_tab_system), selectedTab == "SYSTEM", { onTabSelected("SYSTEM") }, Modifier.weight(1f))
        }
    }
}

@Composable
fun NotificationTabButton(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(42.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(if (selected) BrandBlue else PlayerInputBg)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) BrandWhite else PlayerTextGray,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun NotificationCard(icon: String, iconColor: Color, title: String, time: String, description: String, highlighted: Boolean, unread: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PlayerCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, if (highlighted) PlayerPrimaryBlue.copy(alpha = 0.20f) else Color.Transparent)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            if (highlighted) {
                Box(
                    modifier = Modifier
                        .width(5.dp)
                        .height(118.dp)
                        .background(PlayerPrimaryBlue)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(iconColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(icon, color = iconColor, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            color = BrandBlue,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )

                        TimeBadge(time = time)

                        if (unread) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(PlayerPrimaryBlue)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(9.dp))

                    Text(
                        text = description,
                        color = PlayerTextGray,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        fontWeight = FontWeight.Medium
                    )
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PlayerCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, BrandGreen.copy(alpha = 0.18f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 18.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BrandGreen.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text("♙+", color = BrandGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        color = BrandBlue,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    TimeBadge(time = time)
                }

                if (!equipaNome.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(equipaNome, color = BrandBlue, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }

                if (!equipaInfo.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(equipaInfo, color = BrandGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = description,
                    color = PlayerTextGray,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Medium
                )

                if (!message.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(PlayerInputBg)
                            .padding(12.dp)
                    ) {
                        Text(
                            text = message,
                            color = PlayerTextGray,
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (onAcceptClick != null && onDeclineClick != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onAcceptClick,
                            enabled = !isActionLoading,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = BrandWhite)
                        ) {
                            Text(stringResource(R.string.player_common_accept), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onDeclineClick,
                            enabled = !isActionLoading,
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, PlayerInputBg),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = PlayerInputBg,
                                contentColor = PlayerTextGray
                            )
                        ) {
                            Text(stringResource(R.string.player_common_decline), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimeBadge(time: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(PlayerInputBg)
            .padding(horizontal = 8.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = time,
            color = PlayerMutedGray,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun NotificationsMessageCard(text: String, isError: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = if (isError) PlayerErrorBg else PlayerSuccessBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(if (isError) Color(0xFFD01818) else BrandGreen)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = text,
                color = if (isError) Color(0xFFD01818) else BrandGreen,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun LoadingNotificationsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PlayerCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 44.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = BrandGreen)
        }
    }
}

@Composable
fun EmptyNotificationsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PlayerCardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 36.dp, horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(PlayerInputBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = null,
                    tint = PlayerMutedGray,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.player_notif_empty),
                color = PlayerTextGray,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 19.sp
            )
        }
    }
}

@Composable
fun EndOfFeed() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 28.dp, bottom = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(PlayerInputBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = stringResource(R.string.player_notif_end_of_feed),
                tint = PlayerMutedGray,
                modifier = Modifier.size(23.dp)
            )
        }
        Spacer(modifier = Modifier.height(9.dp))
        Text(
            stringResource(R.string.player_notif_end_of_feed),
            color = PlayerMutedGray,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
    }
}

@Preview(showBackground = true, name = "Player Notifications Screen")
@Composable
fun PlayerNotificationsScreenPreview() {
    PlayerNotificationsScreen()
}
