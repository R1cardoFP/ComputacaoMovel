package com.example.trabalhocm.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.model.AdminUser
import com.example.trabalhocm.data.repository.AdminUserRepository
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.AppIcons
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.CardBg
import com.example.trabalhocm.ui.theme.DarkBlue
import com.example.trabalhocm.ui.theme.ErrorRed
import com.example.trabalhocm.ui.theme.InputBg
import com.example.trabalhocm.ui.theme.LightBlueBadge
import com.example.trabalhocm.ui.theme.PrimaryBlue
import com.example.trabalhocm.ui.theme.TealGreen
import com.example.trabalhocm.ui.theme.TextGray
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserManagementScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onUserClick: (String) -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { AdminUserRepository() }
    val scope = rememberCoroutineScope()

    var users by remember { mutableStateOf<List<AdminUser>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }
    var searchText by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var visibleCount by remember { mutableIntStateOf(6) }
    var actionMessage by remember { mutableStateOf("") }
    var actionMessageIsError by remember { mutableStateOf(false) }
    var userToDelete by remember { mutableStateOf<AdminUser?>(null) }

    val errorLoadingUsersText = stringResource(R.string.admin_users_error_loading)
    val deleteUserSuccessText = stringResource(R.string.admin_users_delete_success)
    val deleteUserErrorText = stringResource(R.string.admin_users_delete_error)
    val makeAdministratorSuccessText = stringResource(R.string.admin_users_make_admin_success)
    val makeOrganizerSuccessText = stringResource(R.string.admin_users_make_organizer_success)
    val revokeAdministratorSuccessText = stringResource(R.string.admin_users_revoke_admin_success)
    val revokeOrganizerSuccessText = stringResource(R.string.admin_users_revoke_organizer_success)
    val genericErrorText = stringResource(R.string.admin_users_generic_error)

    fun carregarUsers() {
        scope.
        launch {
            isLoading = true
            errorMessage = ""

            repository.listarUtilizadores()
                .onSuccess {
                    users = it
                }
                .onFailure {
                    errorMessage = "$errorLoadingUsersText: ${it.message}"
                }

            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        carregarUsers()
    }

    val filteredUsers = users.filter { user ->
        val matchesSearch =
            user.nome.contains(searchText, ignoreCase = true) ||
                    user.email.contains(searchText, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "Admins" -> user.role == "ADMINISTRATOR"
            "Organizers" -> user.role == "ORGANIZER"
            "Players" -> user.role == "PLAYER"
            else -> true
        }

        matchesSearch && matchesFilter
    }

    val visibleUsers = filteredUsers.take(visibleCount)
    val selectedUserToDelete = userToDelete

    if (selectedUserToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                userToDelete = null
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            title = {
                Text(
                    text = stringResource(R.string.admin_users_delete_title),
                    color = DarkBlue,
                    fontWeight = FontWeight.ExtraBold
                )
            },
            text = {
                Text(
                    text = stringResource(
                        R.string.admin_users_delete_message,
                        selectedUserToDelete.nome
                    ),
                    color = TextGray,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            repository.apagarUtilizador(selectedUserToDelete.id)
                                .onSuccess {
                                    actionMessage = deleteUserSuccessText
                                    actionMessageIsError = false
                                    userToDelete = null
                                    carregarUsers()
                                }
                                .onFailure {
                                    actionMessage = "$deleteUserErrorText: ${it.message}"
                                    actionMessageIsError = true
                                    userToDelete = null
                                }
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.admin_users_delete_button),
                        color = ErrorRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        userToDelete = null
                    }
                ) {
                    Text(
                        text = stringResource(R.string.admin_common_cancel),
                        color = TextGray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = AppIcons.Back,
                            contentDescription = stringResource(R.string.admin_common_back),
                            tint = Color.White
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(R.string.admin_users_top_title),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
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
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = stringResource(R.string.admin_users_console).uppercase(),
                        color = TealGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.6.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.admin_users_title),
                        color = DarkBlue,
                        fontSize = 28.sp,
                        lineHeight = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.admin_users_description),
                        color = TextGray,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }

                item {
                    AdminUsersSummaryCard(
                        totalUsers = users.size,
                        admins = users.count { it.role == "ADMINISTRATOR" },
                        organizers = users.count { it.role == "ORGANIZER" },
                        players = users.count { it.role == "PLAYER" }
                    )
                }

                item {
                    UserSearchBox(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            visibleCount = 6
                        }
                    )
                }

                item {
                    UserFilterRow(
                        selectedFilter = selectedFilter,
                        onFilterClick = {
                            selectedFilter = it
                            visibleCount = 6
                        }
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(R.string.admin_users_results_found, filteredUsers.size),
                            color = TextGray,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = selectedFilter,
                            color = PrimaryBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (errorMessage.isNotBlank()) {
                    item {
                        UserStatusMessageCard(
                            message = errorMessage,
                            isError = true
                        )
                    }
                }

                if (actionMessage.isNotBlank()) {
                    item {
                        UserStatusMessageCard(
                            message = actionMessage,
                            isError = actionMessageIsError
                        )
                    }
                }

                if (visibleUsers.isEmpty()) {
                    item {
                        EmptyUsersCard()
                    }
                }

                items(visibleUsers.size) { index ->
                    AdminUserCard(
                        user = visibleUsers[index],
                        onUserClick = { user ->
                            onUserClick(user.id)
                        },
                        onMakeAdministrator = { user ->
                            scope.launch {
                                repository.tornarAdministrador(user.id)
                                    .onSuccess {
                                        actionMessage = makeAdministratorSuccessText
                                        actionMessageIsError = false
                                        carregarUsers()
                                    }
                                    .onFailure {
                                        actionMessage = "$genericErrorText: ${it.message}"
                                        actionMessageIsError = true
                                    }
                            }
                        },
                        onMakeOrganizer = { user ->
                            scope.launch {
                                repository.tornarOrganizador(user.id)
                                    .onSuccess {
                                        actionMessage = makeOrganizerSuccessText
                                        actionMessageIsError = false
                                        carregarUsers()
                                    }
                                    .onFailure {
                                        actionMessage = "$genericErrorText: ${it.message}"
                                        actionMessageIsError = true
                                    }
                            }
                        },
                        onRevokeAdministrator = { user ->
                            scope.launch {
                                repository.removerAdministrador(user.id)
                                    .onSuccess {
                                        actionMessage = revokeAdministratorSuccessText
                                        actionMessageIsError = false
                                        carregarUsers()
                                    }
                                    .onFailure {
                                        actionMessage = "$genericErrorText: ${it.message}"
                                        actionMessageIsError = true
                                    }
                            }
                        },
                        onRevokeOrganizer = { user ->
                            scope.launch {
                                repository.removerOrganizador(user.id)
                                    .onSuccess {
                                        actionMessage = revokeOrganizerSuccessText
                                        actionMessageIsError = false
                                        carregarUsers()
                                    }
                                    .onFailure {
                                        actionMessage = "$genericErrorText: ${it.message}"
                                        actionMessageIsError = true
                                    }
                            }
                        },
                        onDeleteUser = { user ->
                            userToDelete = user
                        }
                    )
                }

                if (visibleCount < filteredUsers.size) {
                    item {
                        Button(
                            onClick = {
                                visibleCount += 6
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TealGreen,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.admin_users_load_more).uppercase(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminUsersSummaryCard(
    totalUsers: Int,
    admins: Int,
    organizers: Int,
    players: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = DarkBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.admin_users_top_title),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Controlo de permissões e perfis da plataforma",
                        color = Color.White.copy(alpha = 0.78f),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color.White.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = AppIcons.Security,
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
                SummaryMetric(
                    label = "Total",
                    value = totalUsers.toString(),
                    modifier = Modifier.weight(1f)
                )
                SummaryMetric(
                    label = "Admins",
                    value = admins.toString(),
                    modifier = Modifier.weight(1f)
                )
                SummaryMetric(
                    label = "Org.",
                    value = organizers.toString(),
                    modifier = Modifier.weight(1f)
                )
                SummaryMetric(
                    label = "Players",
                    value = players.toString(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SummaryMetric(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                color = Color.White.copy(alpha = 0.74f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun UserSearchBox(
    value: String,
    onValueChange: (String) -> Unit
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = stringResource(R.string.admin_users_search_placeholder),
                color = TextGray,
                fontSize = 14.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = AppIcons.Search,
                contentDescription = stringResource(R.string.admin_users_search_content_description),
                tint = TextGray,
                modifier = Modifier.size(20.dp)
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(18.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = InputBg,
            unfocusedContainerColor = InputBg,
            disabledContainerColor = InputBg,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = DarkBlue,
            unfocusedTextColor = DarkBlue,
            cursorColor = TealGreen
        )
    )
}

@Composable
private fun UserFilterRow(
    selectedFilter: String,
    onFilterClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        UserFilterChip(
            text = stringResource(R.string.admin_users_filter_all),
            value = "All",
            selected = selectedFilter == "All",
            onClick = onFilterClick
        )

        UserFilterChip(
            text = stringResource(R.string.admin_users_filter_admins),
            value = "Admins",
            selected = selectedFilter == "Admins",
            onClick = onFilterClick
        )

        UserFilterChip(
            text = stringResource(R.string.admin_users_filter_organizers),
            value = "Organizers",
            selected = selectedFilter == "Organizers",
            onClick = onFilterClick
        )

        UserFilterChip(
            text = stringResource(R.string.admin_users_filter_players),
            value = "Players",
            selected = selectedFilter == "Players",
            onClick = onFilterClick
        )
    }
}

@Composable
private fun UserFilterChip(
    text: String,
    value: String,
    selected: Boolean,
    onClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onClick(value) },
        shape = RoundedCornerShape(50),
        color = if (selected) DarkBlue else Color.White,
        border = if (selected) null else BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = if (selected) 0.dp else 1.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            color = if (selected) Color.White else TextGray,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun UserStatusMessageCard(
    message: String,
    isError: Boolean
) {
    val color = if (isError) ErrorRed else TealGreen
    val background = if (isError) Color(0xFFFFF1F2) else Color(0xFFEFFCF6)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isError) AppIcons.Delete else AppIcons.Security,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = message,
                color = color,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun EmptyUsersCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(InputBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = AppIcons.Search,
                    contentDescription = null,
                    tint = TextGray,
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Nenhum utilizador encontrado",
                color = DarkBlue,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Experimenta mudar a pesquisa ou o filtro selecionado.",
                color = TextGray,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun AdminUserCard(
    user: AdminUser,
    onUserClick: (AdminUser) -> Unit,
    onMakeAdministrator: (AdminUser) -> Unit,
    onMakeOrganizer: (AdminUser) -> Unit,
    onRevokeAdministrator: (AdminUser) -> Unit,
    onRevokeOrganizer: (AdminUser) -> Unit,
    onDeleteUser: (AdminUser) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onUserClick(user)
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                UserAvatar(user = user)

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = user.nome,
                            color = DarkBlue,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = user.email,
                        color = TextGray,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                Box {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(InputBg)
                            .clickable {
                                menuExpanded = true
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = AppIcons.MoreVert,
                            contentDescription = stringResource(R.string.admin_users_options_content_description),
                            tint = DarkBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = {
                            menuExpanded = false
                        },
                        modifier = Modifier.background(Color.White)
                    ) {
                        when (user.role) {
                            "ADMINISTRATOR" -> {
                                DropdownMenuItem(
                                    text = {
                                        MenuItemContent(
                                            icon = AppIcons.Security,
                                            text = stringResource(R.string.admin_users_revoke_administrator),
                                            color = DarkBlue
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onRevokeAdministrator(user)
                                    }
                                )

                                DropdownMenuItem(
                                    text = {
                                        MenuItemContent(
                                            icon = AppIcons.Delete,
                                            text = stringResource(R.string.admin_users_delete_title),
                                            color = ErrorRed
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onDeleteUser(user)
                                    }
                                )
                            }

                            "ORGANIZER" -> {
                                DropdownMenuItem(
                                    text = {
                                        MenuItemContent(
                                            icon = AppIcons.Security,
                                            text = stringResource(R.string.admin_users_make_administrator),
                                            color = DarkBlue
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onMakeAdministrator(user)
                                    }
                                )

                                DropdownMenuItem(
                                    text = {
                                        MenuItemContent(
                                            icon = AppIcons.Profile,
                                            text = stringResource(R.string.admin_users_revoke_organizer),
                                            color = ErrorRed
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onRevokeOrganizer(user)
                                    }
                                )

                                DropdownMenuItem(
                                    text = {
                                        MenuItemContent(
                                            icon = AppIcons.Delete,
                                            text = stringResource(R.string.admin_users_delete_title),
                                            color = ErrorRed
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onDeleteUser(user)
                                    }
                                )
                            }

                            else -> {
                                DropdownMenuItem(
                                    text = {
                                        MenuItemContent(
                                            icon = AppIcons.Security,
                                            text = stringResource(R.string.admin_users_make_administrator),
                                            color = DarkBlue
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onMakeAdministrator(user)
                                    }
                                )

                                DropdownMenuItem(
                                    text = {
                                        MenuItemContent(
                                            icon = AppIcons.Profile,
                                            text = stringResource(R.string.admin_users_make_organizer),
                                            color = DarkBlue
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onMakeOrganizer(user)
                                    }
                                )

                                DropdownMenuItem(
                                    text = {
                                        MenuItemContent(
                                            icon = AppIcons.Delete,
                                            text = stringResource(R.string.admin_users_delete_title),
                                            color = ErrorRed
                                        )
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onDeleteUser(user)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoleBadge(role = user.role)
                StatusBadge(status = user.status)
            }
        }
    }
}

@Composable
private fun MenuItemContent(
    icon: ImageVector,
    text: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = color,
            modifier = Modifier.size(18.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = text,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun UserAvatar(user: AdminUser) {
    val initials = user.nome
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "U" }

    val avatarColor = when (user.role) {
        "ADMINISTRATOR" -> TealGreen
        "ORGANIZER" -> PrimaryBlue
        "PLAYER" -> Color(0xFFE45B5B)
        else -> Color(0xFF64748B)
    }

    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(RoundedCornerShape(18.dp))
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
}

@Composable
private fun RoleBadge(role: String) {
    val badgeColor = when (role) {
        "ADMINISTRATOR" -> TealGreen
        "ORGANIZER" -> PrimaryBlue
        "PLAYER" -> LightBlueBadge
        else -> InputBg
    }

    val textColor = when (role) {
        "PLAYER" -> PrimaryBlue
        else -> Color.White
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(badgeColor)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = roleDisplayName(role),
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun StatusBadge(status: String) {
    val normalized = status.lowercase()
    val isInactive = normalized.contains("suspended") ||
            normalized.contains("inativo") ||
            normalized.contains("blocked") ||
            normalized.contains("bloqueado")

    val color = if (isInactive) ErrorRed else TealGreen
    val background = if (isInactive) Color(0xFFFFF1F2) else Color(0xFFEFFCF6)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = status.ifBlank { "STATUS" }.uppercase(),
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

@Composable
private fun roleDisplayName(role: String): String {
    return when (role) {
        "ADMINISTRATOR" -> stringResource(R.string.admin_users_role_administrator).uppercase()
        "ORGANIZER" -> stringResource(R.string.admin_users_role_organizer).uppercase()
        "PLAYER" -> stringResource(R.string.admin_users_role_player).uppercase()
        else -> role
    }
}

@Preview(showBackground = true)
@Composable
fun AdminUserManagementScreenPreview() {
    AdminUserManagementScreen()
}
