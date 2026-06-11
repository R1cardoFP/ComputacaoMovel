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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.data.remote.SupabaseClient
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Composable
fun PlayerTournamentRegistrationScreen(
    idTorneio: Long = 0L,
    onBackClick: () -> Unit = {},
    onSubmitClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }
    var isSubmitting by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    var torneio by remember { mutableStateOf<TorneioRegDTO?>(null) }
    var equipasInscritas by remember { mutableIntStateOf(0) }

    var minhaEquipa by remember { mutableStateOf<EquipaRegDTO?>(null) }
    var souCapitao by remember { mutableStateOf(false) }

    var selectedPayment by remember { mutableStateOf("Revolut") }

    LaunchedEffect(idTorneio) {
        if (idTorneio == 0L) {
            errorMessage = "ID do Torneio inválido."
            isLoading = false
            return@LaunchedEffect
        }

        try {
            // 1. Carregar detalhes do Torneio
            val t = SupabaseClient.client.from("torneio").select {
                filter { eq("id", idTorneio) }
            }.decodeSingleOrNull<TorneioRegDTO>()
            torneio = t

            // 2. Contar equipas já inscritas (pendentes ou aceites)
            val inscritas = SupabaseClient.client.from("torneio_equipa").select {
                filter { eq("id_torneio", idTorneio) }
            }.decodeList<TorneioEquipaSimplesDTO>()
            equipasInscritas = inscritas.size

            // 3. Descobrir a equipa do utilizador logado e se é capitão
            val currentUserId = SupabaseClient.client.auth.currentUserOrNull()?.id
            if (currentUserId != null) {
                val memberRows = SupabaseClient.client.from("membro_equipa").select {
                    filter { eq("id_utilizador", currentUserId); eq("estado_convite", "aceite") }
                }.decodeList<MembroEquipaSimplesRegDTO>()

                val userMembership = memberRows.firstOrNull()
                if (userMembership != null && userMembership.idEquipa != null) {
                    souCapitao = userMembership.papel?.lowercase() == "capitao"

                    minhaEquipa = SupabaseClient.client.from("equipa").select {
                        filter { eq("id", userMembership.idEquipa) }
                    }.decodeSingleOrNull<EquipaRegDTO>()
                }
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "Erro ao carregar dados"
        }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        RegistrationTopBar(onBackClick = onBackClick)

        if (isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BrandGreen)
            }
        } else if (errorMessage.isNotBlank()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(text = errorMessage, color = Color.Red, fontWeight = FontWeight.Bold)
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 18.dp)
            ) {
                torneio?.let { info ->
                    RegistrationTournamentHeaderCard(torneio = info)

                    Spacer(modifier = Modifier.height(14.dp))

                    RegistrationProgressCard(
                        inscritos = equipasInscritas,
                        maxEquipas = info.maxEquipas ?: 16
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    RegistrationTeamSelectionCard(
                        equipa = minhaEquipa,
                        isCapitao = souCapitao
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    RegistrationPaymentMethodCard(
                        selectedPayment = selectedPayment,
                        onPaymentSelected = { selectedPayment = it }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    RegistrationSummaryCard(
                        payment = if (selectedPayment == "MB Way") "MB Way" else "$selectedPayment / Card",
                        equipaNome = minhaEquipa?.nome ?: "Nenhuma",
                        entryFee = info.custo ?: 0.0
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val maxEquipasInt = info.maxEquipas ?: 16
                    val isCheio = equipasInscritas >= maxEquipasInt
                    val podeInscrever = minhaEquipa != null && souCapitao && !isSubmitting && !isCheio

                    Button(
                        onClick = {
                            if (podeInscrever) {
                                scope.launch {
                                    isSubmitting = true
                                    try {
                                        SupabaseClient.client.from("torneio_equipa").insert(
                                            TorneioEquipaInsertDTO(
                                                idTorneio = info.id,
                                                idEquipa = minhaEquipa!!.id,
                                                estado = "pendente"
                                            )
                                        )
                                        onSubmitClick() // Volta para trás / sucesso
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                    isSubmitting = false
                                }
                            }
                        },
                        enabled = podeInscrever,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BrandGreen,
                            contentColor = BrandWhite,
                            disabledContainerColor = Color(0xFFDDE1EA),
                            disabledContentColor = Color(0xFF7D8497)
                        )
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = BrandWhite, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = if (isCheio) "TOURNAMENT FULL" else if (!souCapitao) "ONLY CAPTAINS CAN REGISTER" else "⊙  SUBMIT REGISTRATION",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = onBackClick,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, Color(0xFFD5DAE5)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandBlue)
                    ) {
                        Text("←  BACK", fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        MatchLeagueBottomBar(
            selectedTab = "TOURNAMENTS",
            onHomeClick = onHomeClick,
            onTournamentsClick = onTournamentsClick,
            onMatchesClick = onMatchesClick,
            onTeamsClick = onTeamsClick,
            onProfileClick = onProfileClick
        )
    }
}

@Composable
fun RegistrationTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(72.dp).background(BrandBlue).padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("←", color = BrandWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onBackClick() })
        Spacer(modifier = Modifier.width(16.dp))
        Text("Registration", color = BrandWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
        Spacer(modifier = Modifier.weight(1f))
        Text("♧", color = BrandWhite, fontSize = 27.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RegistrationTournamentHeaderCard(torneio: TorneioRegDTO) {
    val modalidade = when(torneio.idModalidade) {
        1 -> "FOOTBALL"
        2 -> "BASKETBALL"
        3 -> "VOLLEYBALL"
        else -> "SPORT"
    }

    Card(
        modifier = Modifier.fillMaxWidth().height(132.dp),
        shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF17345D), Color(0xFF0B1F3A)))).padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RegistrationBadge(text = torneio.estado?.uppercase() ?: "OPEN", color = BrandGreen, light = true)
                    Spacer(modifier = Modifier.width(6.dp))
                    RegistrationBadge(text = modalidade, color = Color(0xFF9EA8BA), light = false)
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(torneio.nome, color = BrandWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${torneio.local ?: "TBD"} · ${torneio.dataInicio ?: "TBD"}", color = Color(0xFF9EA8BA), fontSize = 12.sp, fontWeight = FontWeight.Medium)

                Spacer(modifier = Modifier.height(14.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                    RegistrationHeaderInfo("PRIZE POOL", "€ ${torneio.premio ?: "0.0"}")
                    RegistrationHeaderInfo("ENTRY FEE", "€ ${torneio.custo ?: "0.0"}")
                    RegistrationHeaderInfo("FORMAT", torneio.formato ?: "League")
                }
            }
        }
    }
}

@Composable
fun RegistrationBadge(text: String, color: Color, light: Boolean) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(if (light) color.copy(alpha = 0.15f) else Color(0xFF2B3F60)).padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("● $text", color = color, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
    }
}

@Composable
fun RegistrationHeaderInfo(label: String, value: String) {
    Column {
        Text(label, color = Color(0xFF9EA8BA), fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
        Spacer(modifier = Modifier.height(3.dp))
        Text(value, color = BrandWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun RegistrationProgressCard(inscritos: Int, maxEquipas: Int) {
    val progresso = if (maxEquipas > 0) inscritos.toFloat() / maxEquipas.toFloat() else 0f
    val lugaresLivres = maxEquipas - inscritos

    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("$inscritos / $maxEquipas Teams Registered", color = BrandBlue, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(BrandGreen.copy(alpha = 0.12f)).padding(horizontal = 12.dp, vertical = 5.dp)) {
                    Text(if (lugaresLivres > 0) "$lugaresLivres spots left" else "FULL", color = BrandGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { progresso.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(5.dp).clip(RoundedCornerShape(10.dp)),
                color = BrandGreen, trackColor = Color(0xFFECEEF7)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("◷  Registration open", color = Color(0xFF8B92A5), fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun RegistrationTeamSelectionCard(equipa: EquipaRegDTO?, isCapitao: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)) {
            Text("SELECT YOUR TEAM", color = BrandGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Choose which team to register for this\ntournament.", color = Color(0xFF7D8497), fontSize = 13.sp, lineHeight = 18.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(16.dp))

            if (equipa != null) {
                Row(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).border(1.dp, BrandGreen, RoundedCornerShape(8.dp)).background(BrandGreen.copy(alpha = 0.08f)).padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF9AA1A6)), contentAlignment = Alignment.Center) {
                        Text(equipa.nome.take(2).uppercase(), color = BrandWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(equipa.nome, color = BrandBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("${equipa.divisao ?: "Unranked"} ${if(!isCapitao) "· (Not Captain)" else ""}", color = if(!isCapitao) Color.Red else Color(0xFF7D8497), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                    SelectionCircle(selected = isCapitao)
                }
            } else {
                Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFFF0F2FA)).padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("Não tens nenhuma equipa ativa no momento.", color = BrandBlue, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun RegistrationPaymentMethodCard(selectedPayment: String, onPaymentSelected: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)) {
            Text("ESCOLHE O METODO", color = Color(0xFF7D8497), fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.8.sp)
            Spacer(modifier = Modifier.height(14.dp))
            PaymentMethodOption("Revolut", "Transferência imediata", BrandBlue, selectedPayment == "Revolut") { onPaymentSelected("Revolut") }
            Spacer(modifier = Modifier.height(10.dp))
            PaymentMethodOption("MB Way", "Confirma pelo telemóvel", BrandGreen, selectedPayment == "MB Way") { onPaymentSelected("MB Way") }
            Spacer(modifier = Modifier.height(10.dp))
            PaymentMethodOption("Cartao de credito", "Visa, Mastercard", Color(0xFF3B4A66), selectedPayment == "Cartao de credito") { onPaymentSelected("Cartao de credito") }
            Spacer(modifier = Modifier.height(10.dp))
            PaymentMethodOption("Apple Pay", "Pagamento rápido", Color(0xFF101010), selectedPayment == "Apple Pay") { onPaymentSelected("Apple Pay") }
        }
    }
}

@Composable
fun PaymentMethodOption(name: String, description: String, color: Color, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().height(68.dp).clip(RoundedCornerShape(8.dp)).background(BrandWhite).border(1.dp, Color(0xFFE1E5EE), RoundedCornerShape(8.dp)).clickable { onClick() }.padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(46.dp).clip(RoundedCornerShape(7.dp)).background(color))
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(name, color = BrandBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(description, color = Color(0xFF7D8497), fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
        SelectionCircle(selected = selected)
    }
}

@Composable
fun SelectionCircle(selected: Boolean) {
    Box(
        modifier = Modifier.size(22.dp).clip(CircleShape).border(width = 2.dp, color = if (selected) BrandGreen else Color(0xFFC5CBD6), shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (selected) Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(BrandGreen))
    }
}

@Composable
fun RegistrationSummaryCard(payment: String, equipaNome: String, entryFee: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(9.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite), elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp)) {
            Text("REGISTRATION SUMMARY", color = BrandGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Spacer(modifier = Modifier.height(14.dp))
            SummaryRow("Team", equipaNome)
            SummaryRow("Entry Fee", "€ $entryFee", valueColor = BrandGreen)
            SummaryRow("Payment", payment)

            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 9.dp),
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Status after submission", color = Color(0xFF7D8497), fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(Color(0xFFFFF3CD)).padding(horizontal = 12.dp, vertical = 5.dp)) {
                    Text("Pending Approval", color = Color(0xFF856404), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, valueColor: Color = BrandBlue) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 9.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFF7D8497), fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Text(value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE8EAF2)))
}

// --- DTOs PARA A BASE DE DADOS ---

@Serializable
data class TorneioRegDTO(
    val id: Long,
    val nome: String,
    val local: String? = null,
    @SerialName("data_inicio") val dataInicio: String? = null,
    val formato: String? = null,
    val custo: Double? = null,
    val premio: Double? = null,
    @SerialName("id_modalidade") val idModalidade: Int? = null,
    val estado: String? = null,
    @SerialName("max_equipas") val maxEquipas: Int? = null
)

@Serializable
data class MembroEquipaSimplesRegDTO(
    @SerialName("id_equipa") val idEquipa: Long? = null,
    val papel: String? = null
)

@Serializable
data class EquipaRegDTO(
    val id: Long,
    val nome: String,
    val divisao: String? = null
)

@Serializable
data class TorneioEquipaSimplesDTO(
    @SerialName("id_equipa") val idEquipa: Long? = null
)

@Serializable
data class TorneioEquipaInsertDTO(
    @SerialName("id_torneio") val idTorneio: Long,
    @SerialName("id_equipa") val idEquipa: Long,
    val estado: String
)