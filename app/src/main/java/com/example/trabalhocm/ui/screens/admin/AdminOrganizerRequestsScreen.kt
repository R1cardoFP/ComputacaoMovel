package com.example.trabalhocm.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.model.OrganizerRequest
import com.example.trabalhocm.data.repository.AdminOrganizerRequestRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.AppIcons
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.DarkBlue
import com.example.trabalhocm.ui.theme.ErrorRed
import com.example.trabalhocm.ui.theme.InputBg
import com.example.trabalhocm.ui.theme.LightBlueBadge
import com.example.trabalhocm.ui.theme.LightRedBadge
import com.example.trabalhocm.ui.theme.PrimaryBlue
import com.example.trabalhocm.ui.theme.TealGreen
import com.example.trabalhocm.ui.theme.TextGray
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
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
    var actionMessageIsError by remember { mutableStateOf(false) }

    val errorLoadingRequestsText = stringResource(R.string.admin_organizer_requests_error_loading)
    val rejectedSuccessText = stringResource(R.string.admin_organizer_requests_rejected_success)
    val approvedSuccessText = stringResource(R.string.admin_organizer_requests_approved_success)
    val rejectErrorText = stringResource(R.string.admin_organizer_requests_reject_error)
    val approveErrorText = stringResource(R.string.admin_organizer_requests_approve_error)

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
                    errorMessage = "$errorLoadingRequestsText: ${erro.message}"
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
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.admin_organizer_requests_title),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                    IconButton(onClick = onNotificationsClick) {
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
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = TealGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = 24.dp,
                    end = 24.dp,
                    top = 20.dp,
                    bottom = 32.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    AdminOrganizerRequestsHeroCard(
                        pendingCount = pendingCount,
                        approvedCount = approvedCount,
                        rejectedCount = rejectedCount,
                        weeklyRequests = weeklyRequests
                    )
                }

                item {
                    AdminOrganizerRequestsSearchBox(
                        value = searchText,
                        onValueChange = { searchText = it }
                    )
                }

                item {
                    OrganizerRequestFilterRow(
                        selectedFilter = selectedFilter,
                        pendingCount = pendingCount,
                        approvedCount = approvedCount,
                        rejectedCount = rejectedCount,
                        onFilterClick = { selectedFilter = it }
                    )
                }

                if (errorMessage.isNotBlank()) {
                    item {
                        AdminOrganizerRequestMessage(
                            message = errorMessage,
                            isError = true
                        )
                    }
                }

                if (actionMessage.isNotBlank()) {
                    item {
                        AdminOrganizerRequestMessage(
                            message = actionMessage,
                            isError = actionMessageIsError
                        )
                    }
                }

                item {
                    AdminOrganizerRequestSectionHeader(
                        title = stringResource(
                            R.string.admin_organizer_requests_approval_section,
                            selectedFilterDisplayName(selectedFilter).uppercase(),
                            filteredRequests.size
                        ),
                        subtitle = stringResource(R.string.admin_organizer_requests_description)
                    )
                }

                if (filteredRequests.isEmpty()) {
                    item {
                        EmptyOrganizerRequestCard()
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
                                        actionMessage = rejectedSuccessText
                                        actionMessageIsError = false
                                        carregarPedidos(mostrarLoading = false)
                                    }
                                    .onFailure { erro ->
                                        actionMessage = "$rejectErrorText: ${erro.message}"
                                        actionMessageIsError = true
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
                                        actionMessage = approvedSuccessText
                                        actionMessageIsError = false
                                        carregarPedidos(mostrarLoading = false)
                                    }
                                    .onFailure { erro ->
                                        actionMessage = "$approveErrorText: ${erro.message}"
                                        actionMessageIsError = true
                                    }

                                isUpdating = false
                            }
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
private fun AdminOrganizerRequestsHeroCard(
    pendingCount: Int,
    approvedCount: Int,
    rejectedCount: Int,
    weeklyRequests: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.admin_organizer_requests_console).uppercase(),
                        color = TealGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.6.sp
                    )

                    Text(
                        text = stringResource(R.string.admin_organizer_requests_title),
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 28.sp
                    )

                    Text(
                        text = stringResource(R.string.admin_organizer_requests_description),
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.Profile,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(27.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HeroMetric(
                    label = stringResource(R.string.admin_organizer_requests_pending_review),
                    value = pendingCount.toString(),
                    modifier = Modifier.weight(1f)
                )

                HeroMetric(
                    label = stringResource(R.string.admin_organizer_requests_this_week),
                    value = weeklyRequests.toString(),
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SmallStatusMetric(
                    label = stringResource(R.string.admin_organizer_requests_filter_approved),
                    value = approvedCount,
                    color = TealGreen,
                    modifier = Modifier.weight(1f)
                )

                SmallStatusMetric(
                    label = stringResource(R.string.admin_organizer_requests_filter_rejected),
                    value = rejectedCount,
                    color = ErrorRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun HeroMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.12f)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label.uppercase(),
                color = Color.White.copy(alpha = 0.66f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = value,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun SmallStatusMetric(
    label: String,
    value: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color.White.copy(alpha = 0.74f),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Text(
                text = value.toString(),
                color = color,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdminOrganizerRequestsSearchBox(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = stringResource(R.string.admin_organizer_requests_search_placeholder),
                color = TextGray,
                fontSize = 13.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = AppIcons.Search,
                contentDescription = stringResource(R.string.admin_organizer_requests_search_content_description),
                tint = TextGray,
                modifier = Modifier.size(20.dp)
            )
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            cursorColor = TealGreen,
            focusedTextColor = DarkBlue,
            unfocusedTextColor = DarkBlue
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
            text = stringResource(R.string.admin_organizer_requests_filter_pending),
            value = "Pending",
            count = pendingCount,
            selected = selectedFilter == "Pending",
            modifier = Modifier.weight(1f),
            onClick = onFilterClick
        )

        RequestFilterChip(
            text = stringResource(R.string.admin_organizer_requests_filter_approved),
            value = "Approved",
            count = approvedCount,
            selected = selectedFilter == "Approved",
            modifier = Modifier.weight(1f),
            onClick = onFilterClick
        )

        RequestFilterChip(
            text = stringResource(R.string.admin_organizer_requests_filter_rejected),
            value = "Rejected",
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
    value: String,
    count: Int,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    val background = if (selected) PrimaryBlue else Color.White
    val contentColor = if (selected) Color.White else DarkBlue
    val badgeBackground = if (selected) Color.White.copy(alpha = 0.18f) else InputBg
    val badgeColor = if (selected) Color.White else PrimaryBlue

    Surface(
        modifier = modifier
            .height(46.dp)
            .clickable { onClick(value) },
        shape = RoundedCornerShape(16.dp),
        color = background,
        border = BorderStroke(1.dp, if (selected) PrimaryBlue else Color.Transparent)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = contentColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.width(6.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(badgeBackground)
                    .padding(horizontal = 7.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count.toString(),
                    color = badgeColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}

@Composable
private fun AdminOrganizerRequestSectionHeader(
    title: String,
    subtitle: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = title,
            color = DarkBlue,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Text(
            text = subtitle,
            color = TextGray,
            fontSize = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun OrganizerRequestCard(
    request: OrganizerRequest,
    isUpdating: Boolean,
    onRejectClick: (OrganizerRequest) -> Unit,
    onApproveClick: (OrganizerRequest) -> Unit
) {
    val initials = getInitials(request.name)
    val avatarColor = getAvatarColor(request.name)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(avatarColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = request.name,
                            color = DarkBlue,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        val userLine = if (request.username.isNotBlank()) {
                            "${request.email} · ${request.username}"
                        } else {
                            request.email
                        }

                        Text(
                            text = userLine,
                            color = TextGray,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                StatusBadge(status = request.status)
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = InputBg
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InfoColumn(
                            title = stringResource(R.string.admin_organizer_requests_sport).uppercase(),
                            value = request.sport,
                            modifier = Modifier.weight(1f)
                        )

                        InfoColumn(
                            title = stringResource(R.string.admin_organizer_requests_experience).uppercase(),
                            value = request.experience,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(color = Color.White, thickness = 1.dp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        InfoColumn(
                            title = stringResource(R.string.admin_organizer_requests_frequency).uppercase(),
                            value = request.frequency,
                            modifier = Modifier.weight(1f)
                        )

                        InfoColumn(
                            title = stringResource(R.string.admin_organizer_requests_applied).uppercase(),
                            value = request.applied,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            if (request.description.isNotBlank()) {
                Text(
                    text = "\"${request.description}\"",
                    color = TextGray,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            if (request.status == "PENDING") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RequestActionButton(
                        text = stringResource(R.string.admin_organizer_requests_reject).uppercase(),
                        icon = AppIcons.Cancel,
                        color = ErrorRed,
                        background = LightRedBadge,
                        borderColor = Color.Transparent,
                        enabled = !isUpdating,
                        modifier = Modifier.weight(1f),
                        onClick = { onRejectClick(request) }
                    )

                    RequestActionButton(
                        text = stringResource(R.string.admin_organizer_requests_approve).uppercase(),
                        icon = AppIcons.Confirm,
                        color = Color.White,
                        background = TealGreen,
                        borderColor = TealGreen,
                        enabled = !isUpdating,
                        modifier = Modifier.weight(1f),
                        onClick = { onApproveClick(request) }
                    )
                }
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
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Text(
            text = title,
            color = TextGray,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.7.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = value.ifBlank { "—" },
            color = DarkBlue,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 16.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun StatusBadge(status: String) {
    val background = when (status) {
        "APPROVED" -> Color(0xFFEAF8F5)
        "REJECTED" -> LightRedBadge
        else -> Color(0xFFFFF7DE)
    }

    val textColor = when (status) {
        "APPROVED" -> TealGreen
        "REJECTED" -> ErrorRed
        else -> Color(0xFFE2A600)
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = background
    ) {
        Text(
            text = organizerRequestStatusText(status).uppercase(),
            color = textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            maxLines = 1
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
        modifier = modifier.height(44.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, borderColor),
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = color,
            disabledContainerColor = background.copy(alpha = 0.5f),
            disabledContentColor = color.copy(alpha = 0.5f)
        ),
        contentPadding = PaddingValues(horizontal = 8.dp),
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
                    modifier = Modifier.size(15.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))
            }

            Text(
                text = text,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AdminOrganizerRequestMessage(
    message: String,
    isError: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) LightRedBadge else Color(0xFFEAF8F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = message,
            color = if (isError) ErrorRed else TealGreen,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(14.dp)
        )
    }
}

@Composable
private fun EmptyOrganizerRequestCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(InputBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "—",
                    color = TextGray,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = stringResource(R.string.admin_organizer_requests_empty),
                color = TextGray,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
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
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFEAF8F5))
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = AppIcons.Info,
                contentDescription = stringResource(R.string.admin_organizer_requests_info_content_description),
                tint = TealGreen,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = stringResource(R.string.admin_organizer_requests_info_message),
            color = TealGreen,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun selectedFilterDisplayName(filter: String): String {
    return when (filter) {
        "Pending" -> stringResource(R.string.admin_organizer_requests_filter_pending)
        "Approved" -> stringResource(R.string.admin_organizer_requests_filter_approved)
        "Rejected" -> stringResource(R.string.admin_organizer_requests_filter_rejected)
        else -> filter
    }
}

@Composable
private fun organizerRequestStatusText(status: String): String {
    return when (status) {
        "APPROVED" -> stringResource(R.string.admin_organizer_requests_status_approved)
        "REJECTED" -> stringResource(R.string.admin_organizer_requests_status_rejected)
        else -> stringResource(R.string.admin_organizer_requests_status_pending)
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
