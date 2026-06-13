package com.example.trabalhocm.ui.screens.player

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.trabalhocm.R
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

private val ManageScreenBg = Color(0xFFF4F6FB)
private val ManageInputBg = Color(0xFFEFF3F8)
private val ManageTextMuted = Color(0xFF6D7486)
private val ManageCardBorder = Color(0xFFE7EAF2)
private val ManageSoftGreen = Color(0xFFEAF8F5)
private val ManageSoftBlue = Color(0xFFEAF1FF)
private val ManageSoftRed = Color(0xFFFFF0F0)

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
    val context = LocalContext.current

    var gestaoEquipa by remember { mutableStateOf<EquipaGestaoInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isActionLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var successMessage by remember { mutableStateOf("") }

    var search by remember { mutableStateOf("") }

    var showOptions by remember { mutableStateOf(false) }
    var selectedMember by remember { mutableStateOf<MembroEquipaGestaoInfo?>(null) }

    var showInviteDialog by remember { mutableStateOf(false) }
    var inviteSearch by remember { mutableStateOf("") }
    var inviteResults by remember { mutableStateOf<List<UtilizadorConviteInfo>>(emptyList()) }
    var inviteLoading by remember { mutableStateOf(false) }
    var inviteMessage by remember { mutableStateOf("") }
    var inviteMessageIsError by remember { mutableStateOf(false) }

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    var currentUserId by remember { mutableStateOf("") }
    var resolvedTeamId by remember { mutableLongStateOf(idEquipa) }

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
                    inviteMessage = it.message ?: context.getString(R.string.player_manageteam_err_search)
                    inviteMessageIsError = true
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
                    errorMessage = it.message ?: context.getString(R.string.player_manageteam_err_load)
                }
        } else {
            errorMessage = context.getString(R.string.player_manageteam_no_team)
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
        val searchText =
            "${membro.utilizador.nome} ${membro.utilizador.username} ${membro.utilizador.email}".lowercase()

        search.isBlank() || searchText.contains(search.lowercase())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ManageScreenBg)
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
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = stringResource(R.string.player_manageteam_eyebrow),
                        color = Color(0xFF0757C8),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.8.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.player_manageteam_subtitle),
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
                                                val privacyLabel = if (newPrivacy == "publica")
                                                    context.getString(R.string.player_common_public)
                                                else
                                                    context.getString(R.string.player_common_private)
                                                successMessage = context.getString(R.string.player_manageteam_privacy_updated, privacyLabel)
                                                repository.obterGestaoEquipa(resolvedTeamId)
                                                    .onSuccess { gestaoEquipa = it }
                                            }
                                            .onFailure {
                                                errorMessage = context.getString(R.string.player_manageteam_err_privacy)
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
                                    .height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = BrandGreen,
                                    contentColor = BrandWhite,
                                    disabledContainerColor = Color(0xFFD4D9E3),
                                    disabledContentColor = Color(0xFF7D8497)
                                )
                            ) {
                                Text(
                                    text = "＋  ${stringResource(R.string.player_manageteam_invite_player)}",
                                    fontSize = 13.sp,
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
                            .height(56.dp),
                        singleLine = true,
                        placeholder = {
                            Text(
                                text = stringResource(R.string.player_manageteam_search_placeholder),
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
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = ManageInputBg,
                            unfocusedContainerColor = ManageInputBg,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = BrandGreen,
                            focusedTextColor = BrandBlue,
                            unfocusedTextColor = BrandBlue
                        )
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
                        text = stringResource(R.string.player_manageteam_roster_count, filteredPlayers.size),
                        color = Color(0xFF7D8497),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (filteredPlayers.isEmpty()) {
                        ManageTeamEmptyCard(
                            text = stringResource(R.string.player_common_no_players_found)
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

                    if (isCurrentUserCaptain) {
                        Spacer(modifier = Modifier.height(24.dp))

                        OutlinedButton(
                            onClick = { showDeleteConfirmDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(58.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFFC62828)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828))
                        ) {
                            Text(
                                text = stringResource(R.string.player_manageteam_delete_team),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
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
                            onTeamsClick()
                        }
                        .onFailure {
                            errorMessage = it.message ?: context.getString(R.string.player_manageteam_err_update)
                            isActionLoading = false
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
                            successMessage = context.getString(R.string.player_manageteam_player_removed, member.utilizador.nome)
                            repository.obterGestaoEquipa(resolvedTeamId)
                                .onSuccess { gestaoEquipa = it }
                        }
                        .onFailure {
                            errorMessage = it.message ?: context.getString(R.string.player_manageteam_err_update)
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
            results = inviteResults,
            isLoading = inviteLoading,
            message = inviteMessage,
            messageIsError = inviteMessageIsError,
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
                        posicao = "Player"
                    )
                        .onSuccess {
                            inviteMessage = context.getString(R.string.player_manageteam_invitation_sent, jogador.utilizador.nome)
                            inviteMessageIsError = false
                            repository.obterGestaoEquipa(resolvedTeamId)
                                .onSuccess { gestaoEquipa = it }
                            repository.pesquisarJogadoresParaConvite(
                                idEquipa = resolvedTeamId,
                                pesquisa = inviteSearch
                            )
                                .onSuccess { inviteResults = it }
                        }
                        .onFailure {
                            inviteMessage = it.message ?: context.getString(R.string.player_manageteam_err_invite)
                            inviteMessageIsError = true
                        }

                    inviteLoading = false
                }
            }
        )
    }

    if (showDeleteConfirmDialog) {
        Dialog(onDismissRequest = { showDeleteConfirmDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = BrandWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.player_manageteam_delete_title),
                        color = Color(0xFFD01818),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.player_manageteam_delete_message),
                        color = Color(0xFF6D7486),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showDeleteConfirmDialog = false }) {
                            Text(stringResource(R.string.player_common_cancel), color = BrandBlue, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                showDeleteConfirmDialog = false
                                scope.launch {
                                    isActionLoading = true
                                    repository.eliminarEquipa(resolvedTeamId)
                                        .onSuccess {
                                            onTeamsClick()
                                        }
                                        .onFailure {
                                            errorMessage = it.message ?: context.getString(R.string.player_manageteam_err_delete)
                                            isActionLoading = false
                                        }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD01818))
                        ) {
                            Text(stringResource(R.string.player_manageteam_delete_confirm), color = BrandWhite, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
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
            .height(76.dp)
            .background(BrandBlue)
            .padding(horizontal = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f))
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "‹",
                color = BrandWhite,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.player_teams_topbar_title),
                color = BrandWhite,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.player_manageteam_eyebrow).uppercase(),
                color = BrandGreen,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp
            )
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f))
                .clickable { onNotificationsClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = stringResource(R.string.player_common_notifications),
                tint = BrandWhite,
                modifier = Modifier.size(22.dp)
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(22.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.14f))
                        .border(1.dp, Color.White.copy(alpha = 0.16f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = iniciais.take(3).uppercase(),
                        color = BrandWhite,
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.player_manageteam_eyebrow).uppercase(),
                        color = BrandGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.8.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = nomeEquipa,
                        color = BrandWhite,
                        fontSize = 24.sp,
                        lineHeight = 28.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = stringResource(R.string.player_manageteam_players_division, numeroJogadores, divisao),
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ManageTeamHeroChip(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.player_manageteam_roster_count, numeroJogadores),
                    value = numeroJogadores.toString()
                )

                ManageTeamHeroChip(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.player_manageteam_subtitle),
                    value = divisao
                )
            }
        }
    }
}

@Composable
private fun ManageTeamHeroChip(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.11f))
            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(
            text = label.uppercase(),
            color = Color.White.copy(alpha = 0.62f),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            color = BrandWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun ManageTeamPrivacyCard(
    isPublic: Boolean,
    onPrivacyChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        border = BorderStroke(1.dp, ManageCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = stringResource(R.string.player_createteam_privacy_label).uppercase(),
                color = ManageTextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.4.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ManagePrivacyOption(
                    modifier = Modifier.weight(1f),
                    selected = !isPublic,
                    title = "${stringResource(R.string.player_common_private)} 🔒",
                    onClick = { if (isPublic) onPrivacyChange("privada") }
                )

                ManagePrivacyOption(
                    modifier = Modifier.weight(1f),
                    selected = isPublic,
                    title = "${stringResource(R.string.player_common_public)} 🔓",
                    onClick = { if (!isPublic) onPrivacyChange("publica") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = if (isPublic) stringResource(R.string.player_manageteam_privacy_public_desc) else stringResource(R.string.player_manageteam_privacy_private_desc),
                color = ManageTextMuted,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ManagePrivacyOption(
    modifier: Modifier = Modifier,
    selected: Boolean,
    title: String,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) ManageSoftGreen else ManageInputBg)
            .border(1.dp, if (selected) BrandGreen else Color.Transparent, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (selected) BrandGreen else ManageTextMuted,
            fontSize = 12.sp,
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
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        border = BorderStroke(1.dp, ManageCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    if (membro.isCaptain) {
                        drawLine(
                            color = BrandGreen,
                            start = Offset(0f, 18f),
                            end = Offset(0f, size.height - 18f),
                            strokeWidth = 8f
                        )
                    }
                }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(ManageSoftBlue),
                contentAlignment = Alignment.Center
            ) {
                if (!membro.utilizador.fotoUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = membro.utilizador.fotoUrl,
                        contentDescription = stringResource(R.string.player_common_photo),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = memberInitials(nome),
                        color = BrandBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = nome,
                        color = BrandBlue,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    if (membro.isCaptain) {
                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color(0xFFFFE9E9))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.player_common_captain).uppercase(),
                                color = Color(0xFFB72D2D),
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.6.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(5.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (estado == "pendente") Color(0xFFFFF4D8) else ManageSoftGreen)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (estado == "pendente") stringResource(R.string.player_common_pending) else stringResource(R.string.player_common_active),
                            color = if (estado == "pendente") Color(0xFFD19A00) else BrandGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = membro.posicao,
                        color = ManageTextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(ManageInputBg)
                    .clickable { onOptionsClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⋮",
                    color = ManageTextMuted,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
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
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = BrandWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 14.dp)
            ) {
                ManagePlayerOptionRow(
                    icon = "♙",
                    text = stringResource(R.string.player_manageteam_opt_view_profile),
                    textColor = BrandBlue,
                    onClick = onViewProfileClick
                )

                if (isCurrentUserCaptain && !isSelectedPlayerSelf) {
                    if (!isSelectedPlayerAlreadyCaptain) {
                        ManagePlayerOptionRow(
                            icon = "☆",
                            text = stringResource(R.string.player_manageteam_opt_make_captain),
                            textColor = BrandBlue,
                            onClick = onMakeCaptainClick
                        )
                    }

                    ManagePlayerOptionRow(
                        icon = "♜",
                        text = stringResource(R.string.player_manageteam_opt_remove),
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
            .height(58.dp)
            .clickable { onClick() }
            .padding(horizontal = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (textColor == BrandBlue) ManageSoftBlue else ManageSoftRed),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = icon,
                color = textColor,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun InvitePlayerDialog(
    search: String,
    onSearchChange: (String) -> Unit,
    results: List<UtilizadorConviteInfo>,
    isLoading: Boolean,
    message: String,
    messageIsError: Boolean,
    onDismiss: () -> Unit,
    onInviteClick: (UtilizadorConviteInfo) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BrandWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.player_manageteam_invite_title),
                            color = BrandBlue,
                            fontSize = 21.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = stringResource(R.string.player_manageteam_invite_search_placeholder),
                            color = ManageTextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ManageInputBg)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "×",
                            color = BrandBlue,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = search,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.player_manageteam_invite_search_placeholder),
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
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ManageInputBg,
                        unfocusedContainerColor = ManageInputBg,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = BrandGreen,
                        focusedTextColor = BrandBlue,
                        unfocusedTextColor = BrandBlue
                    )
                )

                if (message.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))

                    ManageTeamMessageCard(
                        text = message,
                        isError = messageIsError
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
                        text = stringResource(R.string.player_common_no_players_found)
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(270.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        results.forEach { jogador ->
                            InvitePlayerRow(
                                jogador = jogador,
                                onInviteClick = {
                                    onInviteClick(jogador)
                                }
                            )

                            Spacer(modifier = Modifier.height(10.dp))
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
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F8FC)),
        border = BorderStroke(1.dp, ManageCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(ManageSoftBlue),
                contentAlignment = Alignment.Center
            ) {
                if (!utilizador.fotoUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = utilizador.fotoUrl,
                        contentDescription = stringResource(R.string.player_common_photo),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = memberInitials(utilizador.nome),
                        color = BrandBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = utilizador.nome,
                    color = BrandBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = utilizador.email,
                    color = ManageTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (jogador.jaPertenceEquipa) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(ManageInputBg)
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = if (jogador.estadoConvite?.lowercase() == "pendente") stringResource(R.string.player_manageteam_invited) else stringResource(R.string.player_manageteam_in_team),
                        color = ManageTextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Button(
                    onClick = onInviteClick,
                    modifier = Modifier.height(38.dp),
                    shape = RoundedCornerShape(13.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BrandGreen,
                        contentColor = BrandWhite
                    )
                ) {
                    Text(
                        text = stringResource(R.string.player_manageteam_invite_btn),
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) ManageSoftRed else ManageSoftGreen
        ),
        border = BorderStroke(1.dp, if (isError) Color(0xFFFFD6D6) else Color(0xFFD4F1EA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            color = if (isError) Color(0xFFD01818) else BrandGreen,
            fontSize = 12.sp,
            lineHeight = 17.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(14.dp)
        )
    }
}

@Composable
fun ManageTeamEmptyCard(
    text: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        border = BorderStroke(1.dp, ManageCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(ManageInputBg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "⌕",
                    color = ManageTextMuted,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = text,
                color = ManageTextMuted,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                fontWeight = FontWeight.Medium
            )
        }
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