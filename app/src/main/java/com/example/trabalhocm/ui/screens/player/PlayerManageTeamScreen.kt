package com.example.trabalhocm.ui.screens.player

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.trabalhocm.data.remote.SupabaseClient
import com.example.trabalhocm.data.repository.EquipaGestaoInfo
import com.example.trabalhocm.data.repository.EquipaRepository
import com.example.trabalhocm.data.repository.MembroEquipaGestaoInfo
import com.example.trabalhocm.data.repository.UtilizadorConviteInfo
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

@Composable
fun PlayerManageTeamScreen(
    idEquipa: Long = 0L,
    onBackClick: () -> Unit = {},
    onViewPlayerProfileClick: (String) -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {}
) {
    val repository = remember { EquipaRepository() }
    val scope = rememberCoroutineScope()

    var gestaoEquipa by remember { mutableStateOf<EquipaGestaoInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isActionLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    var search by remember { mutableStateOf("") }
    var selectedPosition by remember { mutableStateOf("All") }

    var showOptions by remember { mutableStateOf(false) }
    var selectedMember by remember { mutableStateOf<MembroEquipaGestaoInfo?>(null) }

    var showInviteDialog by remember { mutableStateOf(false) }
    var inviteSearch by remember { mutableStateOf("") }
    var invitePosition by remember { mutableStateOf("Forward") }
    var inviteResults by remember { mutableStateOf<List<UtilizadorConviteInfo>>(emptyList()) }
    var inviteLoading by remember { mutableStateOf(false) }
    var inviteMessage by remember { mutableStateOf("") }

    var currentUserId by remember { mutableStateOf("") }
    var resolvedTeamId by remember { mutableLongStateOf(idEquipa) }

    fun reloadTeam() {
        val teamId = resolvedTeamId

        if (teamId == 0L) {
            errorMessage = "Could not identify team."
            return
        }

        scope.launch {
            isActionLoading = true
            errorMessage = ""
            successMessage = ""

            repository.obterGestaoEquipa(teamId)
                .onSuccess {
                    gestaoEquipa = it
                }
                .onFailure {
                    errorMessage = it.message ?: "Error updating team."
                }

            isActionLoading = false
        }
    }

    fun reloadInviteResults() {
        val teamId = resolvedTeamId

        if (teamId == 0L) {
            return
        }

        scope.launch {
            inviteLoading = true
            inviteMessage = ""

            repository.pesquisarJogadoresParaConvite(
                idEquipa = teamId,
                pesquisa = inviteSearch
            )
                .onSuccess {
                    inviteResults = it
                }
                .onFailure {
                    inviteMessage = it.message ?: "Error searching players."
                }

            inviteLoading = false
        }
    }

    LaunchedEffect(idEquipa) {
        isLoading = true
        errorMessage = ""
        successMessage = ""

        currentUserId = SupabaseClient.client.auth.currentUserOrNull()?.id ?: ""

        var targetTeamId = idEquipa

        if (targetTeamId == 0L) {
            targetTeamId = repository.listarEquipasComInfo()
                .getOrNull()
                ?.firstOrNull { it.utilizadorPertence }
                ?.equipa
                ?.id
                ?: 0L
        }

        resolvedTeamId = targetTeamId

        if (targetTeamId != 0L) {
            repository.obterGestaoEquipa(targetTeamId)
                .onSuccess {
                    gestaoEquipa = it
                }
                .onFailure {
                    errorMessage = it.message ?: "Error loading roster."
                }
        } else {
            errorMessage = "You are not associated with any team."
        }

        isLoading = false
    }

    LaunchedEffect(showInviteDialog, inviteSearch) {
        if (showInviteDialog) {
            reloadInviteResults()
        }
    }

    val rosterList = gestaoEquipa?.membros
        ?.filter { it.estadoConvite.lowercase() != "recusado" }
        ?: emptyList()

    val isCurrentUserCaptain = rosterList.any {
        it.utilizador.id == currentUserId && it.isCaptain
    }

    val filteredPlayers = rosterList.filter { membro ->
        val matchesPosition =
            selectedPosition == "All" ||
                    membro.posicao.equals(selectedPosition, ignoreCase = true)

        val searchText =
            "${membro.utilizador.nome} ${membro.utilizador.username} ${membro.utilizador.email}".lowercase()

        val matchesSearch =
            search.isBlank() || searchText.contains(search.lowercase())

        matchesPosition && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        ManageTeamTopBar(
            onBackClick = onBackClick,
            onNotificationsClick = onNotificationsClick
        )

        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandGreen)
                }
            }

            errorMessage.isNotBlank() && gestaoEquipa == null -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = errorMessage,
                        color = Color(0xFFD01818),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 22.dp, vertical = 18.dp)
                ) {
                    Text(
                        text = "TEAM MANAGEMENT",
                        color = Color(0xFF0757C8),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.8.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Manage your roster, invite new players and oversee\nteam composition.",
                        color = Color(0xFF6D7486),
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    gestaoEquipa?.let { info ->
                        ManageTeamHeaderCard(
                            nomeEquipa = info.equipaInfo.equipa.nome,
                            iniciais = info.equipaInfo.iniciais,
                            divisao = info.equipaInfo.divisao,
                            numeroJogadores = rosterList.size
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (isCurrentUserCaptain) {
                            ManageTeamPrivacyCard(
                                isPublic = info.equipaInfo.tipoEntrada.lowercase() == "publica",
                                onPrivacyChange = { newPrivacy ->
                                    scope.launch {
                                        isActionLoading = true
                                        errorMessage = ""
                                        successMessage = ""
                                        repository.atualizarPrivacidadeEquipa(resolvedTeamId, newPrivacy)
                                            .onSuccess {
                                                successMessage = "Team privacy updated to ${newPrivacy.uppercase()}"
                                                repository.obterGestaoEquipa(resolvedTeamId)
                                                    .onSuccess { gestaoEquipa = it }
                                            }
                                            .onFailure {
                                                errorMessage = "Error updating privacy."
                                            }
                                        isActionLoading = false
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = {
                                    showInviteDialog = true
                                    inviteSearch = ""
                                    inviteMessage = ""
                                },
                                enabled = !isActionLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                shape = RoundedCornerShape(5.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0757C8),
                                    contentColor = BrandWhite,
                                    disabledContainerColor = Color(0xFFD4D9E3),
                                    disabledContentColor = Color(0xFF7D8497)
                                )
                            ) {
                                Text(
                                    text = "♙+  INVITE PLAYER",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    OutlinedTextField(
                        value = search,
                        onValueChange = { search = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = "Search roster...",
                                color = Color(0xFF9EA4B3),
                                fontSize = 12.sp
                            )
                        },
                        leadingIcon = {
                            Text(
                                text = "⌕",
                                color = Color(0xFF8D94A3),
                                fontSize = 16.sp
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text
                        ),
                        shape = RoundedCornerShape(7.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BrandWhite,
                            unfocusedContainerColor = BrandWhite,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = BrandGreen,
                            focusedTextColor = BrandBlue,
                            unfocusedTextColor = BrandBlue
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    ManageTeamPositionFilters(
                        selectedPosition = selectedPosition,
                        onPositionSelected = { selectedPosition = it }
                    )

                    if (errorMessage.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))

                        ManageTeamMessageCard(
                            text = errorMessage,
                            isError = true
                        )
                    }

                    if (successMessage.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))

                        ManageTeamMessageCard(
                            text = successMessage,
                            isError = false
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "ROSTER (${filteredPlayers.size})",
                        color = Color(0xFF7D8497),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (filteredPlayers.isEmpty()) {
                        ManageTeamEmptyCard(
                            text = "No players found with these filters."
                        )
                    } else {
                        filteredPlayers.forEach { membro ->
                            ManageTeamPlayerRow(
                                membro = membro,
                                onOptionsClick = {
                                    selectedMember = membro
                                    showOptions = true
                                }
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        MatchLeagueBottomBar(
            selectedTab = "TEAMS",
            onHomeClick = onHomeClick,
            onTournamentsClick = onTournamentsClick,
            onMatchesClick = onMatchesClick,
            onTeamsClick = onTeamsClick,
            onProfileClick = onProfileClick
        )
    }

    if (isActionLoading && !isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = BrandGreen)
        }
    }

    if (showOptions && selectedMember != null) {
        val member = selectedMember!!
        val isSelectedPlayerSelf = member.utilizador.id == currentUserId
        val isSelectedPlayerAlreadyCaptain = member.isCaptain

        ManagePlayerOptionsDialog(
            isCurrentUserCaptain = isCurrentUserCaptain,
            isSelectedPlayerSelf = isSelectedPlayerSelf,
            isSelectedPlayerAlreadyCaptain = isSelectedPlayerAlreadyCaptain,
            onDismiss = {
                showOptions = false
            },
            onViewProfileClick = {
                showOptions = false
                onViewPlayerProfileClick(member.utilizador.id)
            },
            onMakeCaptainClick = {
                showOptions = false
                scope.launch {
                    isActionLoading = true
                    errorMessage = ""
                    successMessage = ""

                    repository.tornarJogadorCapitao(
                        idEquipa = resolvedTeamId,
                        idUtilizador = member.utilizador.id
                    )
                        .onSuccess {
                            // Sucesso! Vamos direcionar o utilizador para a página das Equipas
                            onTeamsClick()
                        }
                        .onFailure {
                            errorMessage = it.message ?: "Error updating team."
                            isActionLoading = false // Só paramos o loading se der erro.
                        }
                }
            },
            onRemoveFromTeamClick = {
                showOptions = false
                scope.launch {
                    isActionLoading = true
                    errorMessage = ""
                    successMessage = ""

                    repository.removerJogadorDaEquipa(
                        idEquipa = resolvedTeamId,
                        idUtilizador = member.utilizador.id
                    )
                        .onSuccess {
                            successMessage = "${member.utilizador.nome} was removed from the team."
                            repository.obterGestaoEquipa(resolvedTeamId)
                                .onSuccess { gestaoEquipa = it }
                        }
                        .onFailure {
                            errorMessage = it.message ?: "Error updating team."
                        }

                    isActionLoading = false
                }
            }
        )
    }

    if (showInviteDialog) {
        InvitePlayerDialog(
            search = inviteSearch,
            onSearchChange = {
                inviteSearch = it
            },
            selectedPosition = invitePosition,
            onPositionSelected = {
                invitePosition = it
            },
            results = inviteResults,
            isLoading = inviteLoading,
            message = inviteMessage,
            onDismiss = {
                showInviteDialog = false
            },
            onInviteClick = { jogador ->
                scope.launch {
                    inviteLoading = true
                    inviteMessage = ""

                    repository.convidarJogadorParaEquipa(
                        idEquipa = resolvedTeamId,
                        idUtilizador = jogador.utilizador.id,
                        mensagem = "Invitation sent by team management.",
                        posicao = invitePosition
                    )
                        .onSuccess {
                            inviteMessage = "Invitation sent to ${jogador.utilizador.nome}."
                            repository.obterGestaoEquipa(resolvedTeamId)
                                .onSuccess { gestaoEquipa = it }
                            repository.pesquisarJogadoresParaConvite(
                                idEquipa = resolvedTeamId,
                                pesquisa = inviteSearch
                            )
                                .onSuccess { inviteResults = it }
                        }
                        .onFailure {
                            inviteMessage = it.message ?: "Error inviting player."
                        }

                    inviteLoading = false
                }
            }
        )
    }
}

@Composable
fun ManageTeamTopBar(
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
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
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                onBackClick()
            }
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = "Teams",
            color = BrandWhite,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Notifications",
                tint = BrandWhite,
                modifier = Modifier
                    .size(26.dp)
                    .clickable { onNotificationsClick() }
            )

            Spacer(modifier = Modifier.width(18.dp))

            Text(
                text = "♧",
                color = BrandWhite,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ManageTeamHeaderCard(
    nomeEquipa: String,
    iniciais: String,
    divisao: String,
    numeroJogadores: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(74.dp)
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFF4A555C).copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = iniciais.take(3).uppercase(),
                    color = BrandWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = nomeEquipa,
                    color = BrandWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$numeroJogadores players · $divisao",
                    color = Color(0xFFB8C2D3),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun ManageTeamPrivacyCard(
    isPublic: Boolean,
    onPrivacyChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "TEAM PRIVACY",
                color = Color(0xFF7D8497),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(if (!isPublic) BrandGreen.copy(alpha = 0.1f) else Color(0xFFF0F2FA))
                        .border(1.dp, if (!isPublic) BrandGreen else Color.Transparent, RoundedCornerShape(5.dp))
                        .clickable { if (isPublic) onPrivacyChange("privada") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "PRIVATE 🔒",
                        color = if (!isPublic) BrandGreen else Color(0xFF7D8497),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(if (isPublic) BrandGreen.copy(alpha = 0.1f) else Color(0xFFF0F2FA))
                        .border(1.dp, if (isPublic) BrandGreen else Color.Transparent, RoundedCornerShape(5.dp))
                        .clickable { if (!isPublic) onPrivacyChange("publica") },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "PUBLIC 🔓",
                        color = if (isPublic) BrandGreen else Color(0xFF7D8497),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isPublic) "Anyone can instantly join your team." else "Players must request to join, and you can accept them in Notifications.",
                color = Color(0xFF6D7486),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun ManageTeamPositionFilters(
    selectedPosition: String,
    onPositionSelected: (String) -> Unit
) {
    val positions = listOf(
        "All",
        "Forward",
        "Midfielder",
        "Defender",
        "Goalkeeper"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            positions.take(3).forEach { position ->
                ManageTeamFilterButton(
                    text = position,
                    selected = selectedPosition == position,
                    onClick = { onPositionSelected(position) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            positions.drop(3).forEach { position ->
                ManageTeamFilterButton(
                    text = position,
                    selected = selectedPosition == position,
                    onClick = { onPositionSelected(position) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun ManageTeamFilterButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(30.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(
                if (selected) Color(0xFF0757C8) else Color(0xFFEAF0FF)
            )
            .clickable {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) BrandWhite else Color(0xFF0757C8),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ManageTeamPlayerRow(
    membro: MembroEquipaGestaoInfo,
    onOptionsClick: () -> Unit
) {
    val nome = membro.utilizador.nome
    val estado = membro.estadoConvite.lowercase()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp),
        shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    if (membro.isCaptain) {
                        drawLine(
                            color = Color(0xFFB72D2D),
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            strokeWidth = 8f
                        )
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF0F2FA)),
                    contentAlignment = Alignment.Center
                ) {
                    if (!membro.utilizador.fotoUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = membro.utilizador.fotoUrl,
                            contentDescription = "Foto",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            text = memberInitials(nome),
                            color = BrandBlue,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = nome,
                            color = BrandBlue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (membro.isCaptain) {
                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFFFFE4E4))
                                    .padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "CAPTAIN",
                                    color = Color(0xFFB72D2D),
                                    fontSize = 7.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "${membro.posicao} · ${if (estado == "pendente") "PENDING" else "ACTIVE"}",
                        color = if (estado == "pendente") Color(0xFFD19A00) else Color(0xFF7D8497),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "⋮",
                    color = Color(0xFF7D8497),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            onOptionsClick()
                        }
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun ManagePlayerOptionsDialog(
    isCurrentUserCaptain: Boolean,
    isSelectedPlayerSelf: Boolean,
    isSelectedPlayerAlreadyCaptain: Boolean,
    onDismiss: () -> Unit,
    onViewProfileClick: () -> Unit,
    onMakeCaptainClick: () -> Unit,
    onRemoveFromTeamClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 34.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = BrandWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                ManagePlayerOptionRow(
                    icon = "♙",
                    text = "View Profile",
                    textColor = BrandBlue,
                    onClick = onViewProfileClick
                )

                if (isCurrentUserCaptain && !isSelectedPlayerSelf) {
                    if (!isSelectedPlayerAlreadyCaptain) {
                        ManagePlayerOptionRow(
                            icon = "☆",
                            text = "Make Captain",
                            textColor = BrandBlue,
                            onClick = onMakeCaptainClick
                        )
                    }

                    ManagePlayerOptionRow(
                        icon = "♜",
                        text = "Remove from Team",
                        textColor = Color(0xFFD01818),
                        onClick = onRemoveFromTeamClick
                    )
                }
            }
        }
    }
}

@Composable
fun ManagePlayerOptionRow(
    icon: String,
    text: String,
    textColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable {
                onClick()
            }
            .padding(horizontal = 26.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = icon,
            color = textColor,
            fontSize = 21.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = text,
            color = textColor,
            fontSize = 19.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun InvitePlayerDialog(
    search: String,
    onSearchChange: (String) -> Unit,
    selectedPosition: String,
    onPositionSelected: (String) -> Unit,
    results: List<UtilizadorConviteInfo>,
    isLoading: Boolean,
    message: String,
    onDismiss: () -> Unit,
    onInviteClick: (UtilizadorConviteInfo) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = BrandWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Invite Player",
                        color = BrandBlue,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = "×",
                        color = BrandBlue,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            onDismiss()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = search,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "Search player...",
                            color = Color(0xFF9EA4B3),
                            fontSize = 12.sp
                        )
                    },
                    leadingIcon = {
                        Text(
                            text = "⌕",
                            color = Color(0xFF8D94A3),
                            fontSize = 16.sp
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    shape = RoundedCornerShape(7.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF4F5FA),
                        unfocusedContainerColor = Color(0xFFF4F5FA),
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = BrandGreen,
                        focusedTextColor = BrandBlue,
                        unfocusedTextColor = BrandBlue
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "POSITION",
                    color = Color(0xFF7D8497),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                ManageTeamPositionFilters(
                    selectedPosition = selectedPosition,
                    onPositionSelected = onPositionSelected
                )

                if (message.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    ManageTeamMessageCard(
                        text = message,
                        isError = message.startsWith("Error", ignoreCase = true)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = BrandGreen)
                    }
                } else if (results.isEmpty()) {
                    ManageTeamEmptyCard(
                        text = "No players found."
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        results.forEach { jogador ->
                            InvitePlayerRow(
                                jogador = jogador,
                                onInviteClick = {
                                    onInviteClick(jogador)
                                }
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InvitePlayerRow(
    jogador: UtilizadorConviteInfo,
    onInviteClick: () -> Unit
) {
    val utilizador = jogador.utilizador

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F8FC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEAF0FF)),
                contentAlignment = Alignment.Center
            ) {
                if (!utilizador.fotoUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = utilizador.fotoUrl,
                        contentDescription = "Foto",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = memberInitials(utilizador.nome),
                        color = BrandBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = utilizador.nome,
                    color = BrandBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = utilizador.email,
                    color = Color(0xFF7D8497),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (jogador.jaPertenceEquipa) {
                Text(
                    text = if (jogador.estadoConvite?.lowercase() == "pendente") "INVITED" else "IN TEAM",
                    color = Color(0xFF7D8497),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Button(
                    onClick = onInviteClick,
                    modifier = Modifier.height(34.dp),
                    shape = RoundedCornerShape(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen,
                        contentColor = BrandWhite
                    )
                ) {
                    Text(
                        text = "INVITE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ManageTeamMessageCard(
    text: String,
    isError: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) Color(0xFFFFF0F0) else Color(0xFFEAF7F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            color = if (isError) Color(0xFFD01818) else BrandGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(12.dp)
        )
    }
}

@Composable
fun ManageTeamEmptyCard(
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            color = Color(0xFF7D8497),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(14.dp)
        )
    }
}

fun memberInitials(nome: String): String {
    val palavras = nome
        .split(" ")
        .filter { it.isNotBlank() }

    return when {
        palavras.isEmpty() -> "?"
        palavras.size == 1 -> palavras.first().take(2).uppercase()
        else -> palavras.take(2).joinToString("") {
            it.first().uppercaseChar().toString()
        }
    }
}

@Preview(showBackground = true, name = "Player Manage Team Screen")
@Composable
fun PlayerManageTeamScreenPreview() {
    PlayerManageTeamScreen()
}