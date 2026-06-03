package com.example.trabalhocm.ui.screens.admin

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.model.AdminUser
import com.example.trabalhocm.data.repository.AdminUserRepository
import com.example.trabalhocm.ui.theme.BgLight
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.CardBg
import com.example.trabalhocm.ui.theme.InputBg
import com.example.trabalhocm.ui.theme.LightBlueBadge
import com.example.trabalhocm.ui.theme.TextGray
import kotlinx.coroutines.launch

@Composable
fun AdminUserManagementScreen(
    onBackClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
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
    var userToDelete by remember { mutableStateOf<AdminUser?>(null) }

    fun carregarUsers() {
        scope.launch {
            isLoading = true
            errorMessage = ""

            repository.listarUtilizadores()
                .onSuccess {
                    users = it
                }
                .onFailure {
                    errorMessage = "Erro ao carregar utilizadores: ${it.message}"
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
            title = {
                Text(text = "Delete User")
            },
            text = {
                Text(text = "Tens a certeza que queres apagar ${selectedUserToDelete.nome}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            repository.apagarUtilizador(selectedUserToDelete.id)
                                .onSuccess {
                                    actionMessage = "Utilizador apagado."
                                    userToDelete = null
                                    carregarUsers()
                                }
                                .onFailure {
                                    actionMessage = "Erro: ${it.message}"
                                    userToDelete = null
                                }
                        }
                    }
                ) {
                    Text(
                        text = "Apagar",
                        color = Color(0xFFDC2626)
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        userToDelete = null
                    }
                ) {
                    Text(text = "Cancelar")
                }
            }
        )
    }

    Scaffold(
        containerColor = BgLight,
        topBar = {
            AdminUsersTopBar(
                title = "Manage Team",
                onBackClick = onBackClick,
                onNotificationsClick = onNotificationsClick
            )
        },
        bottomBar = {
            AdminUsersBottomBar(
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
                CircularProgressIndicator(color = BrandGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = 20.dp,
                    end = 20.dp,
                    top = 16.dp,
                    bottom = 28.dp
                ),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Column {
                        Text(
                            text = "ADMIN CONSOLE",
                            color = BrandGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "User Management",
                            color = BrandBlue,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Oversee every account on the platform.",
                            color = TextGray,
                            fontSize = 13.sp
                        )
                    }
                }

                item {
                    SearchBox(
                        value = searchText,
                        onValueChange = {
                            searchText = it
                            visibleCount = 6
                        }
                    )
                }

                item {
                    FilterRow(
                        selectedFilter = selectedFilter,
                        onFilterClick = {
                            selectedFilter = it
                            visibleCount = 6
                        }
                    )
                }

                item {
                    Text(
                        text = "${filteredUsers.size} Results founded",
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }

                if (errorMessage.isNotBlank()) {
                    item {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp
                        )
                    }
                }

                if (actionMessage.isNotBlank()) {
                    item {
                        Text(
                            text = actionMessage,
                            color = if (actionMessage.startsWith("Erro")) {
                                Color(0xFFDC2626)
                            } else {
                                BrandGreen
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                items(visibleUsers.size) { index ->
                    AdminUserCard(
                        user = visibleUsers[index],
                        onMakeAdministrator = { user ->
                            scope.launch {
                                repository.tornarAdministrador(user.id)
                                    .onSuccess {
                                        actionMessage = "User promoted to administrator."
                                        carregarUsers()
                                    }
                                    .onFailure {
                                        actionMessage = "Erro: ${it.message}"
                                    }
                            }
                        },
                        onMakeOrganizer = { user ->
                            scope.launch {
                                repository.tornarOrganizador(user.id)
                                    .onSuccess {
                                        actionMessage = "User promoted to organizer."
                                        carregarUsers()
                                    }
                                    .onFailure {
                                        actionMessage = "Erro: ${it.message}"
                                    }
                            }
                        },
                        onRevokeAdministrator = { user ->
                            scope.launch {
                                repository.removerAdministrador(user.id)
                                    .onSuccess {
                                        actionMessage = "Administrator removed"
                                        carregarUsers()
                                    }
                                    .onFailure {
                                        actionMessage = "Erro: ${it.message}"
                                    }
                            }
                        },
                        onRevokeOrganizer = { user ->
                            scope.launch {
                                repository.removerOrganizador(user.id)
                                    .onSuccess {
                                        actionMessage = "Organizer removed."
                                        carregarUsers()
                                    }
                                    .onFailure {
                                        actionMessage = "Erro: ${it.message}"
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
                                .height(54.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandGreen,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "LOAD MORE",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
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
private fun AdminUsersTopBar(
    title: String,
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrandBlue)
            .statusBarsPadding()
            .padding(horizontal = 18.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onBackClick() }
        ) {
            Text(
                text = "←",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "🔔",
            color = Color.White,
            fontSize = 21.sp,
            modifier = Modifier.clickable {
                onNotificationsClick()
            }
        )
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
                text = "Search users by name or email...",
                color = TextGray,
                fontSize = 13.sp
            )
        },
        leadingIcon = {
            Text(
                text = "⌕",
                color = TextGray,
                fontSize = 18.sp
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text
        ),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = Color(0xFFD8DEE9),
            unfocusedBorderColor = Color(0xFFD8DEE9),
            focusedTextColor = BrandBlue,
            unfocusedTextColor = BrandBlue,
            cursorColor = BrandGreen
        )
    )
}

@Composable
private fun FilterRow(
    selectedFilter: String,
    onFilterClick: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip("All", selectedFilter == "All", onFilterClick)
        FilterChip("Admins", selectedFilter == "Admins", onFilterClick)
        FilterChip("Organizers", selectedFilter == "Organizers", onFilterClick)
        FilterChip("Players", selectedFilter == "Players", onFilterClick)
    }
}

@Composable
private fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (selected) Color(0xFF0057C8) else LightBlueBadge)
            .clickable { onClick(text) }
            .padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color(0xFF0057C8),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AdminUserCard(
    user: AdminUser,
    onMakeAdministrator: (AdminUser) -> Unit,
    onMakeOrganizer: (AdminUser) -> Unit,
    onRevokeAdministrator: (AdminUser) -> Unit,
    onRevokeOrganizer: (AdminUser) -> Unit,
    onDeleteUser: (AdminUser) -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(user = user)

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = user.nome,
                        color = BrandBlue,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    RoleBadge(role = user.role)
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "${user.email} · ${user.status}",
                    color = TextGray,
                    fontSize = 12.sp
                )
            }

            Box {
                Text(
                    text = "⋮",
                    color = BrandBlue,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        menuExpanded = true
                    }
                )

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
                                    Text(
                                        text = "🛡 Revoke Administrator",
                                        color = BrandBlue,
                                        fontSize = 13.sp
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onRevokeAdministrator(user)
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "🗑 Delete User",
                                        color = Color(0xFFDC2626),
                                        fontSize = 13.sp
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
                                    Text(
                                        text = "🛡 Make Administrator",
                                        color = BrandBlue,
                                        fontSize = 13.sp
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onMakeAdministrator(user)
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "👤 Revoke Organizer",
                                        color = Color(0xFFDC2626),
                                        fontSize = 13.sp
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onRevokeOrganizer(user)
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "🗑 Delete User",
                                        color = Color(0xFFDC2626),
                                        fontSize = 13.sp
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
                                    Text(
                                        text = "🛡 Make Administrator",
                                        color = BrandBlue,
                                        fontSize = 13.sp
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onMakeAdministrator(user)
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "👤 Make Organizer",
                                        color = BrandBlue,
                                        fontSize = 13.sp
                                    )
                                },
                                onClick = {
                                    menuExpanded = false
                                    onMakeOrganizer(user)
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "🗑 Delete User",
                                        color = Color(0xFFDC2626),
                                        fontSize = 13.sp
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
        "ADMINISTRATOR" -> Color(0xFF0E8A6F)
        "ORGANIZER" -> Color(0xFF0346B8)
        "PLAYER" -> Color(0xFFE45B5B)
        else -> Color(0xFF64748B)
    }

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
}

@Composable
private fun RoleBadge(role: String) {
    val badgeColor = when (role) {
        "ADMINISTRATOR" -> BrandGreen
        "ORGANIZER" -> Color(0xFF0057C8)
        "PLAYER" -> LightBlueBadge
        else -> InputBg
    }

    val textColor = when (role) {
        "PLAYER" -> Color(0xFF0057C8)
        else -> Color.White
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(badgeColor)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = role,
            color = textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun AdminUsersBottomBar(
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
        AdminUsersBottomItem("⌂", "HOME", selected == "home", onHomeClick)
        AdminUsersBottomItem("♜", "TOURNAMENTS", selected == "tournaments", onTournamentsClick)
        AdminUsersBottomItem("◎", "MATCHES", selected == "matches", onMatchesClick)
        AdminUsersBottomItem("♟", "TEAMS", selected == "teams", onTeamsClick)
        AdminUsersBottomItem("♙", "PROFILE", selected == "profile", onProfileClick)
    }
}

@Composable
private fun AdminUsersBottomItem(
    icon: String,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            color = if (selected) Color(0xFF0057C8) else Color(0xFF9AA5B5),
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = if (selected) Color(0xFF0057C8) else Color(0xFF9AA5B5),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminUserManagementScreenPreview() {
    AdminUserManagementScreen()
}