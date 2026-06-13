package com.example.trabalhocm.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.model.AdminNotification
import com.example.trabalhocm.data.repository.AdminNotificationRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.AppIcons
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.CardBg
import com.example.trabalhocm.ui.theme.DarkBlue
import com.example.trabalhocm.ui.theme.InputBg
import com.example.trabalhocm.ui.theme.PrimaryBlue
import com.example.trabalhocm.ui.theme.TealGreen
import com.example.trabalhocm.ui.theme.TextGray
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.Normalizer
import java.util.Locale

private data class NotificationVisual(
    val icon: ImageVector,
    val iconColor: Color,
    val iconBackground: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNotificationsScreen(
    onBackClick: () -> Unit = {},
    onViewUserProfileClick: (String) -> Unit = {},
    onViewTournamentDetailsClick: (String) -> Unit = {},
    onViewTeamDetailsClick: (String) -> Unit = {},
    onUserManagementClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { AdminNotificationRepository() }
    val scope = rememberCoroutineScope()

    var selectedFilter by remember { mutableStateOf("ALL") }
    var notifications by remember { mutableStateOf<List<AdminNotification>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var actionMessage by remember { mutableStateOf("") }
    var actionMessageIsError by remember { mutableStateOf(false) }

    val errorLoadingNotificationsText = stringResource(R.string.admin_notifications_error_loading)
    val errorUpdatingNotificationText = stringResource(R.string.admin_notifications_error_updating)
    val actionMissingTargetText = stringResource(R.string.admin_notifications_missing_target)

    fun carregarNotificacoes(mostrarLoading: Boolean = true) {
        scope.launch {
            if (mostrarLoading) {
                isLoading = true
            }

            errorMessage = ""

            repository.listarNotificacoes()
                .onSuccess {
                    notifications = it
                }
                .onFailure {
                    errorMessage = "$errorLoadingNotificationsText: ${it.message}"
                }

            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        carregarNotificacoes()

        while (true) {
            delay(60_000)
            carregarNotificacoes(mostrarLoading = false)
        }
    }

    val visibleNotifications = notifications.filter {
        selectedFilter == "ALL" || it.type == selectedFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.admin_notifications_title),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = AppIcons.Back,
                            contentDescription = stringResource(R.string.admin_common_back),
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { carregarNotificacoes() }) {
                        Icon(
                            imageVector = AppIcons.Notifications,
                            contentDescription = stringResource(R.string.admin_common_notifications),
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        bottomBar = {
            MatchLeagueBottomBar(
                selectedTab = "PROFILE",
                onHomeClick = onHomeClick,
                onTournamentsClick = onTournamentsClick,
                onMatchesClick = onMatchesClick,
                onTeamsClick = onTeamsClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = BgLight
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(R.string.admin_notifications_console).uppercase(),
                    color = PrimaryBlue,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.admin_notifications_title).uppercase(),
                    color = DarkBlue,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = stringResource(R.string.admin_notifications_description),
                    color = TextGray,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                AdminNotificationFilterRow(
                    selectedFilter = selectedFilter,
                    onFilterClick = {
                        selectedFilter = it
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            if (errorMessage.isNotBlank()) {
                item {
                    AdminNotificationMessageCard(
                        text = errorMessage,
                        isError = true
                    )
                }
            }

            if (actionMessage.isNotBlank()) {
                item {
                    AdminNotificationMessageCard(
                        text = actionMessage,
                        isError = actionMessageIsError
                    )
                }
            }

            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = PrimaryBlue)
                    }
                }
            } else if (visibleNotifications.isEmpty()) {
                item {
                    AdminNotificationsEmptyState()
                }
            } else {
                items(visibleNotifications.size) { index ->
                    AdminNotificationCard(
                        notification = visibleNotifications[index],
                        onNotificationClick = { notification ->
                            if (notification.unread) {
                                scope.launch {
                                    repository.marcarComoLida(notification.id)
                                        .onSuccess {
                                            carregarNotificacoes()
                                        }
                                        .onFailure {
                                            errorMessage = "$errorUpdatingNotificationText: ${it.message}"
                                        }
                                }
                            }
                        },
                        onViewUserProfileClick = onViewUserProfileClick,
                        onViewTournamentDetailsClick = onViewTournamentDetailsClick,
                        onViewTeamDetailsClick = onViewTeamDetailsClick,
                        onUserManagementClick = onUserManagementClick,
                        onActionWithoutTarget = {
                            actionMessage = actionMissingTargetText
                            actionMessageIsError = true
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = AppIcons.Notifications,
                            contentDescription = null,
                            tint = TextGray.copy(alpha = 0.5f),
                            modifier = Modifier.size(32.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = stringResource(R.string.msg_end_of_feed),
                            color = TextGray.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminNotificationFilterRow(
    selectedFilter: String,
    onFilterClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBg, RoundedCornerShape(8.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AdminNotificationFilterChip(
            text = stringResource(R.string.admin_notifications_filter_all),
            value = "ALL",
            selected = selectedFilter == "ALL",
            modifier = Modifier.weight(1f),
            onClick = onFilterClick
        )

        AdminNotificationFilterChip(
            text = stringResource(R.string.admin_notifications_filter_moderation),
            value = "MODERATION",
            selected = selectedFilter == "MODERATION",
            modifier = Modifier.weight(1f),
            onClick = onFilterClick
        )

        AdminNotificationFilterChip(
            text = stringResource(R.string.admin_notifications_filter_system),
            value = "SYSTEM",
            selected = selectedFilter == "SYSTEM",
            modifier = Modifier.weight(1f),
            onClick = onFilterClick
        )
    }
}

@Composable
private fun AdminNotificationFilterChip(
    text: String,
    value: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    Surface(
        color = if (selected) PrimaryBlue else Color.Transparent,
        shape = RoundedCornerShape(6.dp),
        modifier = modifier.clickable { onClick(value) }
    ) {
        Text(
            text = text.uppercase(),
            color = if (selected) Color.White else TextGray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp)
        )
    }
}

@Composable
private fun AdminNotificationMessageCard(
    text: String,
    isError: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) Color(0xFFFFF1F2) else Color(0xFFEAF8F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            color = if (isError) Color(0xFFDC2626) else TealGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(14.dp)
        )
    }
}

@Composable
private fun AdminNotificationsEmptyState() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBg),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = InputBg,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = AppIcons.Notifications,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.admin_notifications_empty),
                color = TextGray.copy(alpha = 0.7f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
private fun AdminNotificationCard(
    notification: AdminNotification,
    onNotificationClick: (AdminNotification) -> Unit,
    onViewUserProfileClick: (String) -> Unit,
    onViewTournamentDetailsClick: (String) -> Unit,
    onViewTeamDetailsClick: (String) -> Unit,
    onUserManagementClick: () -> Unit,
    onActionWithoutTarget: () -> Unit
) {
    val visual = notificationVisual(notification)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onNotificationClick(notification)
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    if (notification.unread) {
                        drawLine(
                            color = PrimaryBlue,
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            strokeWidth = 12f
                        )
                    }
                }
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = visual.iconBackground,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = visual.icon,
                    contentDescription = null,
                    tint = visual.iconColor,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = notification.title,
                        color = DarkBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = if (notification.unread) PrimaryBlue.copy(alpha = 0.1f) else Color.Transparent,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = notification.timeText,
                                color = if (notification.unread) PrimaryBlue else TextGray,
                                fontSize = if (notification.unread) 8.sp else 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        if (notification.unread) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlue)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.description,
                    color = TextGray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                if (notification.actionText != null) {
                    Spacer(modifier = Modifier.height(16.dp))

                    val destination = notificationDestination(notification)
                    val actionEnabled = destination != null

                    NotificationActionButton(
                        text = translatedNotificationActionText(notification.actionText),
                        enabled = actionEnabled,
                        onClick = {
                            onNotificationClick(notification)

                            when (destination) {
                                "USER" -> {
                                    val userId = notification.userId
                                    if (userId.isNullOrBlank()) {
                                        onUserManagementClick()
                                    } else {
                                        onViewUserProfileClick(userId)
                                    }
                                }

                                "TOURNAMENT" -> {
                                    val tournamentId = notification.tournamentId
                                    if (tournamentId.isNullOrBlank()) {
                                        onActionWithoutTarget()
                                    } else {
                                        onViewTournamentDetailsClick(tournamentId)
                                    }
                                }

                                "TEAM" -> {
                                    val teamId = notification.teamId
                                    if (teamId.isNullOrBlank()) {
                                        onActionWithoutTarget()
                                    } else {
                                        onViewTeamDetailsClick(teamId)
                                    }
                                }

                                else -> onActionWithoutTarget()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun translatedNotificationActionText(text: String): String {
    val normalized = normalizarTexto(text)

    return when {
        normalized.contains("user profile") || normalized.contains("perfil utilizador") ->
            stringResource(R.string.admin_notifications_action_view_user_profile).uppercase()

        normalized.contains("tournament") || normalized.contains("torneio") ->
            stringResource(R.string.admin_notifications_action_view_tournament_details).uppercase()

        normalized.contains("team") || normalized.contains("equipa") ->
            stringResource(R.string.admin_notifications_action_view_team_details).uppercase()

        else -> text.uppercase()
    }
}

@Composable
private fun NotificationActionButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, if (enabled) InputBg else Color(0xFFE5E7EB)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (enabled) Color.White else Color(0xFFE5E7EB),
            contentColor = if (enabled) DarkBlue else TextGray,
            disabledContainerColor = Color(0xFFE5E7EB),
            disabledContentColor = TextGray
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

private fun notificationDestination(notification: AdminNotification): String? {
    val action = normalizarTexto(notification.actionText.orEmpty())
    val title = normalizarTexto(notification.title)
    val description = normalizarTexto(notification.description)
    val text = "$action $title $description"

    return when {
        text.contains("team") || text.contains("equipa") -> "TEAM"
        text.contains("tournament") || text.contains("torneio") -> "TOURNAMENT"
        text.contains("user") || text.contains("profile") || text.contains("utilizador") || text.contains("organizer") || text.contains("organizador") -> "USER"
        !notification.teamId.isNullOrBlank() -> "TEAM"
        !notification.tournamentId.isNullOrBlank() -> "TOURNAMENT"
        !notification.userId.isNullOrBlank() -> "USER"
        else -> null
    }
}

private fun notificationVisual(notification: AdminNotification): NotificationVisual {
    val title = normalizarTexto(notification.title)
    val description = normalizarTexto(notification.description)

    return when {
        notification.type == "SYSTEM" -> NotificationVisual(
            icon = AppIcons.Sync,
            iconColor = TextGray,
            iconBackground = InputBg
        )

        title.contains("tournament") || description.contains("tournament") || title.contains("torneio") || description.contains("torneio") -> NotificationVisual(
            icon = AppIcons.Tournaments,
            iconColor = PrimaryBlue,
            iconBackground = PrimaryBlue.copy(alpha = 0.1f)
        )

        title.contains("team") || description.contains("team") || title.contains("equipa") || description.contains("equipa") -> NotificationVisual(
            icon = AppIcons.Teams,
            iconColor = TealGreen,
            iconBackground = TealGreen.copy(alpha = 0.1f)
        )

        title.contains("suspended") || description.contains("suspended") || title.contains("suspenso") || description.contains("suspenso") || title.contains("suspensa") || description.contains("suspensa") -> NotificationVisual(
            icon = AppIcons.Cancel,
            iconColor = Color(0xFFDC2626),
            iconBackground = Color(0xFFFEE2E2)
        )

        title.contains("payment") || description.contains("payment") || title.contains("pagamento") || description.contains("pagamento") -> NotificationVisual(
            icon = AppIcons.Payment,
            iconColor = TealGreen,
            iconBackground = TealGreen.copy(alpha = 0.1f)
        )

        else -> NotificationVisual(
            icon = AppIcons.Profile,
            iconColor = TealGreen,
            iconBackground = TealGreen.copy(alpha = 0.1f)
        )
    }
}

private fun normalizarTexto(texto: String): String {
    return Normalizer.normalize(texto, Normalizer.Form.NFD)
        .replace("\\p{Mn}+".toRegex(), "")
        .lowercase(Locale.getDefault())
}

@Preview(showBackground = true)
@Composable
fun AdminNotificationsScreenPreview() {
    AdminNotificationsScreen()
}
