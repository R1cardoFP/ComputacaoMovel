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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import com.example.trabalhocm.data.repository.EquipaDetalhesInfo
import com.example.trabalhocm.data.repository.EquipaRepository
import com.example.trabalhocm.data.repository.MembroEquipaDetalhesInfo
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class MemberSimpleLookupDTO(
    @SerialName("id_equipa") val idEquipa: Long
)

@Serializable
private data class UpdatePapelDTO(
    val papel: String
)

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun PlayerManageTeamScreen(
    idEquipa: Long = 0L,
    onBackClick: () -> Unit = {},
    onInvitePlayerClick: () -> Unit = {},
    onViewPlayerProfileClick: (String) -> Unit = {},
    onMakeCaptainClick: () -> Unit = {},
    onRemoveFromTeamClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val repository = remember { EquipaRepository() }
    val scope = rememberCoroutineScope()

    var detalhes by remember { mutableStateOf<EquipaDetalhesInfo?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf("") }

    var search by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("All") }

    var showOptions by remember { mutableStateOf(false) }
    var selectedPlayerId by remember { mutableStateOf("") }

    var currentUserId by remember { mutableStateOf("") }
    var resolvedTeamId by remember { mutableLongStateOf(idEquipa) }

    LaunchedEffect(idEquipa) {
        isLoading = true
        errorMessage = ""

        var targetTeamId = idEquipa

        try {
            val loggedInUserId = SupabaseClient.client.auth.currentUserOrNull()?.id
            if (loggedInUserId != null) {
                currentUserId = loggedInUserId
                if (targetTeamId == 0L) {
                    val userMembership = SupabaseClient.client.from("membro_equipa").select {
                        filter { eq("id_utilizador", currentUserId); eq("estado_convite", "aceite") }
                    }.decodeList<MemberSimpleLookupDTO>().firstOrNull()

                    if (userMembership != null) {
                        targetTeamId = userMembership.idEquipa
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        resolvedTeamId = targetTeamId

        if (targetTeamId != 0L) {
            repository.obterDetalhesEquipa(targetTeamId)
                .onSuccess { detalhes = it }
                .onFailure { errorMessage = it.message ?: "Erro ao carregar plantel." }
        } else {
            errorMessage = "Não estás associado a nenhuma equipa."
        }
        isLoading = false
    }

    val rosterList = detalhes?.membros?.filter { it.estadoConvite?.lowercase() == "aceite" } ?: emptyList()
    val isCurrentUserCaptain = rosterList.any { it.utilizador.id == currentUserId && it.isCaptain }

    val filteredPlayers = rosterList.filter { membro ->
        val roleLabel = when {
            membro.isCaptain -> "Captain"
            membro.papel.lowercase().contains("trein") || membro.papel.lowercase().contains("coach") -> "Coach"
            else -> "Player"
        }

        val matchesRole = selectedRole == "All" || roleLabel == selectedRole
        val matchesSearch = search.isBlank() || membro.utilizador.nome.contains(search, ignoreCase = true)

        matchesRole && matchesSearch
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        ManageTeamTopBar(onBackClick = onBackClick)

        if (isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandGreen)
            }
        } else if (errorMessage.isNotBlank()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                Text(errorMessage, color = Color(0xFFD01818), fontSize = 14.sp, fontWeight = FontWeight.Medium)
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 18.dp)
            ) {
                Text("TEAM MANAGEMENT", color = Color(0xFF0757C8), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.8.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Manage your roster, invite new players and oversee\nteam composition.", color = Color(0xFF6D7486), fontSize = 12.sp, lineHeight = 18.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(16.dp))

                detalhes?.let { info ->
                    ManageTeamHeaderCard(nomeEquipa = info.equipaInfo.equipa.nome, iniciais = info.equipaInfo.iniciais, divisao = info.equipaInfo.divisao, numeroJogadores = rosterList.size)
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isCurrentUserCaptain) {
                    Button(
                        onClick = onInvitePlayerClick,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(5.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0757C8), contentColor = BrandWhite)
                    ) {
                        Text("♙+  INVITE PLAYER", fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    singleLine = true,
                    placeholder = { Text("Search roster...", color = Color(0xFF9EA4B3), fontSize = 12.sp) },
                    leadingIcon = { Text("⌕", color = Color(0xFF8D94A3), fontSize = 16.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    shape = RoundedCornerShape(7.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BrandWhite, unfocusedContainerColor = BrandWhite,
                        focusedBorderColor = Color.Transparent, unfocusedBorderColor = Color.Transparent,
                        cursorColor = BrandGreen, focusedTextColor = BrandBlue, unfocusedTextColor = BrandBlue
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                ManageTeamRoleFilters(selectedRole = selectedRole, onRoleSelected = { selectedRole = it })

                Spacer(modifier = Modifier.height(18.dp))

                Text("ROSTER (${filteredPlayers.size})", color = Color(0xFF7D8497), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
                Spacer(modifier = Modifier.height(10.dp))

                filteredPlayers.forEach { membro ->
                    ManageTeamPlayerRow(
                        membro = membro,
                        onOptionsClick = {
                            selectedPlayerId = membro.utilizador.id
                            showOptions = true
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        MatchLeagueBottomBar(
            selectedTab = "TEAMS", onHomeClick = onHomeClick, onTournamentsClick = onTournamentsClick,
            onMatchesClick = onMatchesClick, onTeamsClick = onTeamsClick, onProfileClick = onProfileClick
        )
    }

    if (showOptions) {
        val isSelectedPlayerSelf = selectedPlayerId == currentUserId
        val isSelectedPlayerAlreadyCaptain = rosterList.find { it.utilizador.id == selectedPlayerId }?.isCaptain == true

        ManagePlayerOptionsDialog(
            isCurrentUserCaptain = isCurrentUserCaptain,
            isSelectedPlayerSelf = isSelectedPlayerSelf,
            isSelectedPlayerAlreadyCaptain = isSelectedPlayerAlreadyCaptain,
            onDismiss = { showOptions = false },
            onViewProfileClick = {
                showOptions = false
                onViewPlayerProfileClick(selectedPlayerId)
            },
            onMakeCaptainClick = {
                showOptions = false
                scope.launch {
                    isLoading = true
                    try {
                        SupabaseClient.client.from("membro_equipa").update(UpdatePapelDTO("capitao")) {
                            filter { eq("id_equipa", resolvedTeamId); eq("id_utilizador", selectedPlayerId) }
                        }
                        // Recarrega a equipa para atualizar os estados visuais
                        repository.obterDetalhesEquipa(resolvedTeamId).onSuccess { detalhes = it }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    isLoading = false
                }
            },
            onRemoveFromTeamClick = {
                showOptions = false
                scope.launch {
                    isLoading = true
                    try {
                        SupabaseClient.client.from("membro_equipa").delete {
                            filter { eq("id_equipa", resolvedTeamId); eq("id_utilizador", selectedPlayerId) }
                        }
                        // Recarrega a equipa para atualizar a lista
                        repository.obterDetalhesEquipa(resolvedTeamId).onSuccess { detalhes = it }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    isLoading = false
                }
            }
        )
    }
}

@Composable
fun ManageTeamTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(72.dp).background(BrandBlue).padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("←", color = BrandWhite, fontSize = 26.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onBackClick() })
        Spacer(modifier = Modifier.width(14.dp))
        Text("Teams", color = BrandWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)
        Spacer(modifier = Modifier.weight(1f))
        Text("♧", color = BrandWhite, fontSize = 27.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ManageTeamHeaderCard(nomeEquipa: String, iniciais: String, divisao: String, numeroJogadores: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().height(74.dp).padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(5.dp)).background(Color(0xFF4A555C).copy(alpha = 0.25f)), contentAlignment = Alignment.Center) {
                Text(iniciais.take(3).uppercase(), color = BrandWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(nomeEquipa, color = BrandWhite, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text("$numeroJogadores players · $divisao", color = Color(0xFFB8C2D3), fontSize = 11.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun ManageTeamRoleFilters(selectedRole: String, onRoleSelected: (String) -> Unit) {
    val roles = listOf("All", "Captain", "Player", "Coach")
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        roles.forEach { role ->
            ManageTeamRoleButton(text = role, selected = selectedRole == role, onClick = { onRoleSelected(role) }, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun ManageTeamRoleButton(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.height(30.dp).clip(RoundedCornerShape(3.dp)).background(if (selected) Color(0xFF0757C8) else Color(0xFFEAF0FF)).clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (selected) BrandWhite else Color(0xFF0757C8), fontSize = 8.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ManageTeamPlayerRow(membro: MembroEquipaDetalhesInfo, onOptionsClick: () -> Unit) {
    val nome = membro.utilizador.nome
    val roleLabel = when {
        membro.isCaptain -> "Captain"
        membro.papel.lowercase().contains("trein") || membro.papel.lowercase().contains("coach") -> "Coach"
        else -> "Player"
    }

    Card(
        modifier = Modifier.fillMaxWidth().height(68.dp), shape = RoundedCornerShape(5.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().drawBehind {
                if (membro.isCaptain) {
                    drawLine(color = Color(0xFFB72D2D), start = androidx.compose.ui.geometry.Offset(0f, 0f), end = androidx.compose.ui.geometry.Offset(0f, size.height), strokeWidth = 8f)
                }
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(Color(0xFFF0F2FA)), contentAlignment = Alignment.Center) {
                    if (!membro.utilizador.fotoUrl.isNullOrEmpty()) {
                        AsyncImage(model = membro.utilizador.fotoUrl, contentDescription = "Foto", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    } else {
                        Text(nome.split(" ").take(2).joinToString("") { it.take(1) }.uppercase(), color = BrandBlue, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(nome, color = BrandBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        if (membro.isCaptain) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.clip(RoundedCornerShape(2.dp)).background(Color(0xFFFFE4E4)).padding(horizontal = 6.dp, vertical = 3.dp)) {
                                Text("CAPTAIN", color = Color(0xFFB72D2D), fontSize = 7.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(roleLabel.uppercase(), color = Color(0xFF7D8497), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
                Text("⋮", color = Color(0xFF7D8497), fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onOptionsClick() }.padding(horizontal = 8.dp, vertical = 8.dp))
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
            modifier = Modifier.fillMaxWidth().padding(horizontal = 34.dp), shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = BrandWhite), elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                ManagePlayerOptionRow("♙", "View Profile", BrandBlue, onViewProfileClick)

                // Opções restritas apenas aos capitães e aplicáveis apenas aos outros jogadores
                if (isCurrentUserCaptain && !isSelectedPlayerSelf) {
                    if (!isSelectedPlayerAlreadyCaptain) {
                        ManagePlayerOptionRow("☆", "Make Captain", BrandBlue, onMakeCaptainClick)
                    }
                    ManagePlayerOptionRow("♜", "Remove from Team", Color(0xFFD01818), onRemoveFromTeamClick)
                }
            }
        }
    }
}

@Composable
fun ManagePlayerOptionRow(icon: String, text: String, textColor: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(56.dp).clickable { onClick() }.padding(horizontal = 26.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(icon, color = textColor, fontSize = 21.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(18.dp))
        Text(text, color = textColor, fontSize = 19.sp, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true, name = "Player Manage Team Screen")
@Composable
fun PlayerManageTeamScreenPreview() {
    PlayerManageTeamScreen()
}