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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.model.OrganizerRequest
import com.example.trabalhocm.data.repository.AdminOrganizerRequestRepository
import com.example.trabalhocm.ui.theme.AppIcons
import kotlinx.coroutines.launch

private val AdminBlue = Color(0xFF0B1F3A)
private val AdminGreen = Color(0xFF008D7D)
private val AdminBackground = Color(0xFFF4F5FA)
private val TextMuted = Color(0xFF6F7A8A)
private val CardWhite = Color.White
private val LightBlueBadge = Color(0xFFEAF3FF)
private val BorderGray = Color(0xFFD8DEE9)
private val RejectRed = Color(0xFFDC2626)

@Composable
fun AdminOrganizerRequestsScreen(
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { AdminOrganizerRequestRepository() }
    val scope = rememberCoroutineScope()

    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Pending") }
    var requests by remember { mutableStateOf<List<OrganizerRequest>>(emptyList()) }

    var isLoading by remember { mutableStateOf(true) }
    var isUpdating by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var actionMessage by remember { mutableStateOf("") }

    fun carregarPedidos(mostrarLoading: Boolean = true) {
        scope.launch {
            if (mostrarLoading) {
                isLoading = true
            }

            errorMessage = ""

            repository.listarPedidos()
                .onSuccess { pedidos ->
                    requests = pedidos
                }
                .onFailure { erro ->
                    errorMessage = "Erro ao carregar pedidos: ${erro.message}"
                }

            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        carregarPedidos()
    }

    val pendingCount = requests.count { it.status == "PENDING" }
    val approvedCount = requests.count { it.status == "APPROVED" }
    val rejectedCount = requests.count { it.status == "REJECTED" }
    val weeklyRequests = pendingCount

    val filteredRequests = requests.filter { request ->
        val matchesSearch =
            request.name.contains(searchText, ignoreCase = true) ||
                    request.username.contains(searchText, ignoreCase = true) ||
                    request.email.contains(searchText, ignoreCase = true) ||
                    request.sport.contains(searchText, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Pending" -> request.status == "PENDING"
            "Approved" -> request.status == "APPROVED"
            "Rejected" -> request.status == "REJECTED"
            else -> true
        }

        matchesSearch && matchesFilter
    }

    Scaffold(
        containerColor = AdminBackground,
        topBar = {
            OrganizerRequestsTopBar(
                title = "Organizer Requests",
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick
            )
        },
        bottomBar = {
            OrganizerRequestsBottomBar(
                selected = "profile",
                onHomeClick = onHomeClick,
                onTournamentsClick = onTournamentsClick,
                onMatchesClick = onMatchesClick,
                onTeamsClick = onTeamsClick,
                onProfileClick = onProfileClick
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AdminGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 14.dp,
                    bottom = 28.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Column {
                        Text(
                            text = "ADMIN CONSOLE",
                            color = AdminGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Organizer Requests",
                            color = AdminBlue,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(5.dp))

                        Text(
                            text = "Review and approve players who applied to become\norganizers.",
                            color = TextMuted,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }

                item {
                    PendingReviewCard(
                        pendingCount = pendingCount,
                        weeklyRequests = weeklyRequests
                    )
                }

                item {
                    SearchBox(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                        }
                    )
                }

                item {
                    OrganizerRequestFilterRow(
                        selectedFilter = selectedFilter,
                        pendingCount = pendingCount,
                        approvedCount = approvedCount,
                        rejectedCount = rejectedCount,
                        onFilterClick = {
                            selectedFilter = it
                        }
                    )
                }

                if (errorMessage.isNotBlank()) {
                    item {
                        Text(
                            text = errorMessage,
                            color = RejectRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (actionMessage.isNotBlank()) {
                    item {
                        Text(
                            text = actionMessage,
                            color = if (actionMessage.startsWith("Erro")) RejectRed else AdminGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                item {
                    Text(
                        text = "${selectedFilter.uppercase()} APPROVAL (${filteredRequests.size})",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }

                if (filteredRequests.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(9.dp),
                            colors = CardDefaults.cardColors(containerColor = CardWhite),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                text = "Sem pedidos para mostrar.",
                                color = TextMuted,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(18.dp)
                            )
                        }
                    }
                }

                items(filteredRequests.size) { index ->
                    OrganizerRequestCard(
                        request = filteredRequests[index],
                        isUpdating = isUpdating,
                        onRejectClick = { request ->
                            scope.launch {
                                isUpdating = true
                                actionMessage = ""

                                repository.rejeitarPedido(request.id)
                                    .onSuccess {
                                        actionMessage = "Pedido rejeitado."
                                        carregarPedidos(mostrarLoading = false)
                                    }
                                    .onFailure { erro ->
                                        actionMessage = "Erro ao rejeitar: ${erro.message}"
                                    }

                                isUpdating = false
                            }
                        },
                        onApproveClick = { request ->
                            scope.launch {
                                isUpdating = true
                                actionMessage = ""

                                repository.aprovarPedido(
                                    idPedido = request.id,
                                    idUtilizador = request.userId
                                )
                                    .onSuccess {
                                        actionMessage = "Pedido aprovado e utilizador promovido a organizador."
                                        carregarPedidos(mostrarLoading = false)
                                    }
                                    .onFailure { erro ->
                                        actionMessage = "Erro ao aprovar: ${erro.message}"
                                    }

                                isUpdating = false
                            }
                        },
                        onDetailsClick = {
                        }
                    )
                }

                item {
                    InfoBox()
                }
            }
        }
    }
}

@Composable
private fun OrganizerRequestsTopBar(
    title: String,
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AdminBlue)
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onBackClick() }
        ) {
            Icon(
                imageVector = AppIcons.Back,
                contentDescription = "Voltar",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Icon(
            imageVector = AppIcons.Notifications,
            contentDescription = "Notificações",
            tint = Color.White,
            modifier = Modifier
                .size(22.dp)
                .clickable {
                    onNotificationsClick()
                }
        )
    }
}

@Composable
private fun PendingReviewCard(
    pendingCount: Int,
    weeklyRequests: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = AdminBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 15.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "PENDING REVIEW",
                    color = Color(0xFFB9C4D8),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = pendingCount.toString(),
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "THIS WEEK",
                    color = Color(0xFFB9C4D8),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "+$weeklyRequests new requests",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SearchBox(
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = "Search applicants...",
                color = TextMuted,
                fontSize = 12.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = AppIcons.Search,
                contentDescription = "Pesquisar",
                tint = TextMuted,
                modifier = Modifier.size(18.dp)
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = BorderGray,
            unfocusedBorderColor = BorderGray,
            focusedTextColor = AdminBlue,
            unfocusedTextColor = AdminBlue,
            cursorColor = AdminGreen
        )
    )
}

@Composable
private fun OrganizerRequestFilterRow(
    selectedFilter: String,
    pendingCount: Int,
    approvedCount: Int,
    rejectedCount: Int,
    onFilterClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        RequestFilterChip(
            text = "Pending",
            count = pendingCount,
            selected = selectedFilter == "Pending",
            modifier = Modifier.weight(1f),
            onClick = onFilterClick
        )

        RequestFilterChip(
            text = "Approved",
            count = approvedCount,
            selected = selectedFilter == "Approved",
            modifier = Modifier.weight(1f),
            onClick = onFilterClick
        )

        RequestFilterChip(
            text = "Rejected",
            count = rejectedCount,
            selected = selectedFilter == "Rejected",
            modifier = Modifier.weight(1f),
            onClick = onFilterClick
        )
    }
}

@Composable
private fun RequestFilterChip(
    text: String,
    count: Int,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    Row(
        modifier = modifier
            .height(34.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(if (selected) Color(0xFF0057C8) else LightBlueBadge)
            .clickable { onClick(text) }
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color(0xFF0057C8),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(4.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(if (selected) Color.White.copy(alpha = 0.22f) else Color.White)
                .padding(horizontal = 5.dp, vertical = 1.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                color = if (selected) Color.White else Color(0xFF0057C8),
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun OrganizerRequestCard(
    request: OrganizerRequest,
    isUpdating: Boolean,
    onRejectClick: (OrganizerRequest) -> Unit,
    onDetailsClick: (OrganizerRequest) -> Unit,
    onApproveClick: (OrganizerRequest) -> Unit
) {
    val initials = getInitials(request.name)
    val avatarColor = getAvatarColor(request.name)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = request.name,
                        color = AdminBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    val userLine = if (request.username.isNotBlank()) {
                        "${request.email} · ${request.username}"
                    } else {
                        request.email
                    }

                    Text(
                        text = userLine,
                        color = TextMuted,
                        fontSize = 10.sp,
                        lineHeight = 13.sp
                    )
                }

                StatusBadge(status = request.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                InfoColumn(
                    title = "SPORT",
                    value = request.sport,
                    modifier = Modifier.weight(1f)
                )

                InfoColumn(
                    title = "EXPERIENCE",
                    value = request.experience,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                InfoColumn(
                    title = "FREQUENCY",
                    value = request.frequency,
                    modifier = Modifier.weight(1f)
                )

                InfoColumn(
                    title = "APPLIED",
                    value = request.applied,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "\"${request.description}\"",
                color = TextMuted,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (request.status == "PENDING") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RequestActionButton(
                        text = "REJECT",
                        icon = AppIcons.Cancel,
                        color = RejectRed,
                        background = Color(0xFFFFF5F5),
                        borderColor = Color(0xFFF2B8B5),
                        enabled = !isUpdating,
                        modifier = Modifier.weight(1f),
                        onClick = { onRejectClick(request) }
                    )

                    RequestActionButton(
                        text = "DETAILS",
                        color = Color(0xFF0057C8),
                        background = Color.White,
                        borderColor = Color(0xFF0057C8),
                        enabled = !isUpdating,
                        modifier = Modifier.weight(1f),
                        onClick = { onDetailsClick(request) }
                    )

                    RequestActionButton(
                        text = "APPROVE",
                        icon = AppIcons.Confirm,
                        color = Color.White,
                        background = AdminGreen,
                        borderColor = AdminGreen,
                        enabled = !isUpdating,
                        modifier = Modifier.weight(1f),
                        onClick = { onApproveClick(request) }
                    )
                }
            } else {
                RequestActionButton(
                    text = "DETAILS",
                    color = Color(0xFF0057C8),
                    background = Color.White,
                    borderColor = Color(0xFF0057C8),
                    enabled = !isUpdating,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onDetailsClick(request) }
                )
            }
        }
    }
}

@Composable
private fun InfoColumn(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = title,
            color = TextMuted,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.7.sp
        )

        Text(
            text = value,
            color = AdminBlue,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 13.sp
        )
    }
}

@Composable
private fun StatusBadge(status: String) {
    val background = when (status) {
        "APPROVED" -> Color(0xFFEAF8F5)
        "REJECTED" -> Color(0xFFFEE2E2)
        else -> Color(0xFFFFF7DE)
    }

    val textColor = when (status) {
        "APPROVED" -> AdminGreen
        "REJECTED" -> RejectRed
        else -> Color(0xFFE2A600)
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RequestActionButton(
    text: String,
    icon: ImageVector? = null,
    color: Color,
    background: Color,
    borderColor: Color,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(34.dp),
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = color,
            disabledContainerColor = background.copy(alpha = 0.5f),
            disabledContentColor = color.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(horizontal = 4.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = text,
                    tint = color,
                    modifier = Modifier.size(12.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))
            }

            Text(
                text = text,
                color = color,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun InfoBox() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFEAF8F5))
            .border(BorderStroke(1.dp, Color(0xFFC7EDE5)), RoundedCornerShape(6.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = AppIcons.Info,
            contentDescription = "Informação",
            tint = AdminGreen,
            modifier = Modifier.size(17.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "Approving a request immediately grants the user organizer permissions and notifies them by email.",
            color = AdminGreen,
            fontSize = 11.sp,
            lineHeight = 15.sp
        )
    }
}

@Composable
private fun OrganizerRequestsBottomBar(
    selected: String,
    onHomeClick: () -> Unit,
    onTournamentsClick: () -> Unit,
    onMatchesClick: () -> Unit,
    onTeamsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomItem(AppIcons.Home, "HOME", selected == "home", onHomeClick)
        BottomItem(AppIcons.Tournaments, "TOURNAMENTS", selected == "tournaments", onTournamentsClick)
        BottomItem(AppIcons.Games, "MATCHES", selected == "matches", onMatchesClick)
        BottomItem(AppIcons.Teams, "TEAMS", selected == "teams", onTeamsClick)
        BottomItem(AppIcons.Profile, "PROFILE", selected == "profile", onProfileClick)
    }
}

@Composable
private fun BottomItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) Color(0xFF0057C8) else Color(0xFF9AA5B5)

    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = color,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = color,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp
        )
    }
}

private fun getInitials(name: String): String {
    return name
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "U" }
}

private fun getAvatarColor(name: String): Color {
    val colors = listOf(
        Color(0xFFE45B5B),
        Color(0xFF5B3FD6),
        Color(0xFF0E8A6F),
        Color(0xFFE2A600),
        Color(0xFF0346B8),
        Color(0xFF64748B)
    )

    val index = kotlin.math.abs(name.hashCode()) % colors.size
    return colors[index]
}

@Preview(showBackground = true)
@Composable
fun AdminOrganizerRequestsScreenPreview() {
    AdminOrganizerRequestsScreen()
}