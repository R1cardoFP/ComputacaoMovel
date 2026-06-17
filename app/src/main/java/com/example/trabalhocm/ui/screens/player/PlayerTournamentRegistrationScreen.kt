package com.example.trabalhocm.ui.screens.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.data.remote.SupabaseClient
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private val ScreenBg = Color(0xFFF4F6FB)
private val CardBg = Color.White
private val InputBg = Color(0xFFEFF3F8)
private val TextMuted = Color(0xFF6D7486)
private val BorderLine = Color(0xFFE6EAF2)
private val SoftGreen = Color(0xFFEAF8F5)
private val SoftBlue = Color(0xFFEAF1FF)
private val SoftWarning = Color(0xFFFFF4D8)
private val WarningText = Color(0xFF8A6500)

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
    var isSuccess by remember { mutableStateOf(false) } // NOVO: Controla a mensagem de sucesso!
    var errorMessage by remember { mutableStateOf("") }

    var torneio by remember { mutableStateOf<TorneioRegDTO?>(null) }
    var equipasInscritas by remember { mutableIntStateOf(0) }

    var minhaEquipa by remember { mutableStateOf<EquipaRegDTO?>(null) }
    var souCapitao by remember { mutableStateOf(false) }

    var selectedPayment by remember { mutableStateOf("Revolut") }

    // Variáveis de estado para a simulação de pagamento
    var cardNumber by remember { mutableStateOf("") }
    var cardExpiry by remember { mutableStateOf("") }
    var cardCvv by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var phonePrefix by remember { mutableStateOf("🇵🇹 +351") }

    LaunchedEffect(idTorneio) {
        if (idTorneio == 0L) {
            errorMessage = "Invalid Tournament ID."
            isLoading = false
            return@LaunchedEffect
        }

        try {
            // 1. Carregar detalhes do Torneio
            val t = SupabaseClient.client.from("torneio").select {
                filter { eq("id", idTorneio) }
            }.decodeSingleOrNull<TorneioRegDTO>()
            torneio = t

            // 2. Contar equipas na tabela correta (torneio_equipa)
            val inscritas = SupabaseClient.client.from("torneio_equipa").select {
                filter {
                    eq("id_torneio", idTorneio)
                    neq("estado", "rejeitada")
                }
            }.decodeList<TorneioEquipaSimplesDTO>()
            equipasInscritas = inscritas.size

            // 3. Descobrir a equipa do utilizador logado PARA ESTA MODALIDADE
            val currentUserId = SupabaseClient.client.auth.currentUserOrNull()?.id
            if (currentUserId != null && t?.idModalidade != null) {
                val memberRows = SupabaseClient.client.from("membro_equipa").select {
                    filter { eq("id_utilizador", currentUserId); eq("estado_convite", "aceite") }
                }.decodeList<MembroEquipaSimplesRegDTO>()

                val idsEquipasUser = memberRows.mapNotNull { it.idEquipa }

                if (idsEquipasUser.isNotEmpty()) {
                    val equipasDesporto = SupabaseClient.client.from("equipa").select {
                        filter {
                            isIn("id", idsEquipasUser)
                            eq("id_modalidade", t.idModalidade)
                        }
                    }.decodeList<EquipaRegDTO>()

                    val equipaValida = equipasDesporto.firstOrNull()

                    if (equipaValida != null) {
                        minhaEquipa = equipaValida
                        val membership = memberRows.find { it.idEquipa == equipaValida.id }
                        souCapitao = membership?.papel?.lowercase() == "capitao"
                    }
                }
            }
        } catch (e: Exception) {
            errorMessage = e.message ?: "Error loading data"
        }
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        RegistrationTopBar(onBackClick = onBackClick)

        when {
            isLoading -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandGreen)
                }
            }

            errorMessage.isNotBlank() -> {
                Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    RegistrationFeedbackCard(title = "Não foi possível carregar a inscrição", message = errorMessage, isError = true)
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    torneio?.let { info ->
                        val isFree = (info.custo ?: 0.0) <= 0.0

                        RegistrationTournamentHeaderCard(torneio = info)
                        Spacer(modifier = Modifier.height(16.dp))

                        RegistrationProgressCard(inscritos = equipasInscritas, maxEquipas = info.maxEquipas ?: 16)
                        Spacer(modifier = Modifier.height(14.dp))

                        RegistrationTeamSelectionCard(equipa = minhaEquipa, isCapitao = souCapitao)
                        Spacer(modifier = Modifier.height(14.dp))

                        if (!isFree) {
                            RegistrationPaymentMethodCard(
                                selectedPayment = selectedPayment,
                                onPaymentSelected = { selectedPayment = it }
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            // AQUI ESTÁ O NOVO BLOCO ANIMADO DE INTRODUÇÃO DE DADOS
                            AnimatedVisibility(
                                visible = true,
                                enter = expandVertically(animationSpec = tween(300)),
                                exit = shrinkVertically(animationSpec = tween(300))
                            ) {
                                RegistrationPaymentDetailsInputCard(
                                    selectedPayment = selectedPayment,
                                    cardNumber = cardNumber, onCardNumberChange = { cardNumber = it },
                                    cardExpiry = cardExpiry, onCardExpiryChange = { cardExpiry = it },
                                    cardCvv = cardCvv, onCardCvvChange = { cardCvv = it },
                                    phoneNumber = phoneNumber, onPhoneNumberChange = { phoneNumber = it },
                                    phonePrefix = phonePrefix, onPhonePrefixChange = { phonePrefix = it }
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        val paymentText = if (isFree) "None (Free Entry)"
                        else if (selectedPayment == "MB Way") "MB Way"
                        else "$selectedPayment / Card"

                        RegistrationSummaryCard(payment = paymentText, equipaNome = minhaEquipa?.nome ?: "None", entryFee = info.custo ?: 0.0)
                        Spacer(modifier = Modifier.height(18.dp))

                        val maxEquipasInt = info.maxEquipas ?: 16
                        val isCheio = equipasInscritas >= maxEquipasInt

                        // Validação para saber se os dados simulados foram preenchidos
                        val isPaymentDataValid = isFree || when(selectedPayment) {
                            "MB Way" -> phoneNumber.isNotBlank()
                            "Apple Pay" -> true // Apple Pay não precisa de inputs textuais
                            else -> cardNumber.isNotBlank() && cardExpiry.isNotBlank() && cardCvv.isNotBlank()
                        }

                        val podeInscrever = minhaEquipa != null && souCapitao && !isSubmitting && !isCheio && isPaymentDataValid && !isSuccess

                        Button(
                            onClick = {
                                if (podeInscrever) {
                                    scope.launch {
                                        isSubmitting = true
                                        errorMessage = ""
                                        try {
                                            // Pequeno delay apenas para "simular" o processamento do pagamento
                                            if (!isFree) delay(1200)

                                            SupabaseClient.client.from("torneio_equipa").insert(
                                                TorneioEquipaInsertDTO(
                                                    idTorneio = info.id,
                                                    idEquipa = minhaEquipa!!.id,
                                                    estado = "pendente",
                                                    pagamentoEstado = if (isFree) "pago" else "pendente",
                                                    mensagem = "Pedido de inscrição pendente."
                                                )
                                            )

                                            // Atualiza a UI para Sucesso e dá 1.5s para o utilizador ler
                                            isSubmitting = false
                                            isSuccess = true
                                            delay(1500)

                                            onSubmitClick()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            errorMessage = "Erro na Base de Dados: ${e.message}"
                                            isSubmitting = false
                                        }
                                    }
                                }
                            },
                            enabled = podeInscrever || isSuccess,
                            modifier = Modifier.fillMaxWidth().height(58.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BrandGreen,
                                contentColor = BrandWhite,
                                disabledContainerColor = Color(0xFFD8DDE7),
                                disabledContentColor = Color(0xFF7D8497)
                            )
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = BrandWhite, modifier = Modifier.size(22.dp), strokeWidth = 2.dp)
                            } else if (isSuccess) {
                                Text(
                                    text = if (isFree) "REGISTRATION COMPLETED ✓" else "TRANSFER SUCCESSFUL ✓",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            } else {
                                Text(
                                    text = when {
                                        isCheio -> "TOURNAMENT FULL"
                                        minhaEquipa == null -> "NO COMPATIBLE TEAM"
                                        !souCapitao -> "ONLY CAPTAINS CAN REGISTER"
                                        !isPaymentDataValid -> "FILL PAYMENT DETAILS"
                                        else -> "SUBMIT REGISTRATION  →"
                                    },
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = onBackClick,
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, BorderLine),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = CardBg, contentColor = BrandBlue)
                        ) {
                            Text("BACK", fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                        }

                        Spacer(modifier = Modifier.height(22.dp))
                    }
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

// --- NOVO CARTÃO DE INTRODUÇÃO DE DADOS DE PAGAMENTO ---
@Composable
fun RegistrationPaymentDetailsInputCard(
    selectedPayment: String,
    cardNumber: String, onCardNumberChange: (String) -> Unit,
    cardExpiry: String, onCardExpiryChange: (String) -> Unit,
    cardCvv: String, onCardCvvChange: (String) -> Unit,
    phoneNumber: String, onPhoneNumberChange: (String) -> Unit,
    phonePrefix: String, onPhonePrefixChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "PAYMENT DETAILS",
                color = BrandBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(14.dp))

            when (selectedPayment) {
                "MB Way" -> {
                    Text("Phone Number", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        // Dropdown do prefixo simulado
                        var expanded by remember { mutableStateOf(false) }
                        val prefixes = listOf("🇵🇹 +351", "🇪🇸 +34", "🇫🇷 +33", "🇬🇧 +44")

                        Box {
                            Row(
                                modifier = Modifier
                                    .height(54.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(InputBg)
                                    .clickable { expanded = true }
                                    .padding(horizontal = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = phonePrefix, color = BrandBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("▾", color = BrandBlue, fontSize = 18.sp)
                            }
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                prefixes.forEach { prefix ->
                                    DropdownMenuItem(
                                        text = { Text(prefix, fontWeight = FontWeight.Medium) },
                                        onClick = { onPhonePrefixChange(prefix); expanded = false }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Input do número
                        TextField(
                            value = phoneNumber,
                            onValueChange = { if(it.length <= 9) onPhoneNumberChange(it) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = InputBg, unfocusedContainerColor = InputBg,
                                focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                            ),
                            placeholder = { Text("912 345 678", color = Color(0xFFA6AFBD)) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(54.dp),
                            singleLine = true
                        )
                    }
                }

                "Apple Pay" -> {
                    Box(modifier = Modifier.fillMaxWidth().height(60.dp).clip(RoundedCornerShape(12.dp)).background(Color.Black), contentAlignment = Alignment.Center) {
                        Text(" Pay (Double click side button)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }

                else -> { // Revolut ou Credit Card
                    Text("Card Number", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(6.dp))
                    TextField(
                        value = cardNumber,
                        onValueChange = { if(it.length <= 16) onCardNumberChange(it) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = InputBg, unfocusedContainerColor = InputBg,
                            focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                        ),
                        placeholder = { Text("0000 0000 0000 0000", color = Color(0xFFA6AFBD)) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Expiry", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(6.dp))
                            TextField(
                                value = cardExpiry,
                                onValueChange = { if(it.length <= 5) onCardExpiryChange(it) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = InputBg, unfocusedContainerColor = InputBg,
                                    focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                                ),
                                placeholder = { Text("MM/YY", color = Color(0xFFA6AFBD)) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(54.dp),
                                singleLine = true
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("CVV", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(6.dp))
                            TextField(
                                value = cardCvv,
                                onValueChange = { if(it.length <= 3) onCardCvvChange(it) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = InputBg, unfocusedContainerColor = InputBg,
                                    focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent
                                ),
                                placeholder = { Text("123", color = Color(0xFFA6AFBD)) },
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().height(54.dp),
                                singleLine = true
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun RegistrationTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
            .background(BrandBlue)
            .padding(horizontal = 22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
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

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = "Registration",
            color = BrandWhite,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "MatchLeague Logo",
                modifier = Modifier.size(24.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun RegistrationTournamentHeaderCard(torneio: TorneioRegDTO) {
    val modalidade = when (torneio.idModalidade) {
        1 -> "FOOTBALL"
        2 -> "BASKETBALL"
        3 -> "VOLLEYBALL"
        else -> "SPORT"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BrandBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF17345D),
                            Color(0xFF0B1F3A)
                        )
                    )
                )
                .padding(22.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RegistrationBadge(
                        text = torneio.estado?.uppercase() ?: "OPEN",
                        color = BrandGreen,
                        light = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    RegistrationBadge(
                        text = modalidade,
                        color = Color(0xFFB8C2D6),
                        light = false
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = torneio.nome,
                    color = BrandWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${torneio.local ?: "TBD"} · ${torneio.dataInicio ?: "TBD"}",
                    color = Color(0xFFB8C2D6),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    RegistrationHeaderInfo(
                        label = "PRIZE",
                        value = "€ ${torneio.premio ?: "0.0"}",
                        modifier = Modifier.weight(1f)
                    )
                    RegistrationHeaderInfo(
                        label = "ENTRY",
                        value = "€ ${torneio.custo ?: "0.0"}",
                        modifier = Modifier.weight(1f)
                    )
                    RegistrationHeaderInfo(
                        label = "FORMAT",
                        value = torneio.formato ?: "League",
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun RegistrationBadge(text: String, color: Color, light: Boolean) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(if (light) color.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.12f))
            .padding(horizontal = 11.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "● $text",
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.6.sp
        )
    }
}

@Composable
fun RegistrationHeaderInfo(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.10f))
            .padding(horizontal = 10.dp, vertical = 12.dp)
    ) {
        Text(
            text = label,
            color = Color(0xFFB8C2D6),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp
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
fun RegistrationProgressCard(inscritos: Int, maxEquipas: Int) {
    val progresso = if (maxEquipas > 0) inscritos.toFloat() / maxEquipas.toFloat() else 0f
    val lugaresLivres = maxEquipas - inscritos

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tournament capacity",
                        color = BrandBlue,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "$inscritos / $maxEquipas teams registered",
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(if (lugaresLivres > 0) SoftGreen else SoftWarning)
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = if (lugaresLivres > 0) "$lugaresLivres spots left" else "FULL",
                        color = if (lugaresLivres > 0) BrandGreen else WarningText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { progresso.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(50.dp)),
                color = BrandGreen,
                trackColor = InputBg
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Registration is open while there are available spots.",
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun RegistrationTeamSelectionCard(equipa: EquipaRegDTO?, isCapitao: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "SELECT YOUR TEAM",
                color = BrandGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose which team to register for this tournament.",
                color = TextMuted,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (equipa != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, if (isCapitao) BrandGreen else BorderLine, RoundedCornerShape(18.dp))
                        .background(if (isCapitao) SoftGreen else InputBg)
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(BrandBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = equipa.nome.take(2).uppercase(),
                            color = BrandWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = equipa.nome,
                            color = BrandBlue,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "${equipa.divisao ?: "Unranked"}${if (!isCapitao) " · Not Captain" else " · Captain"}",
                            color = if (!isCapitao) Color(0xFFE15C5C) else TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    SelectionCircle(selected = isCapitao)
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(InputBg)
                        .padding(18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "You don't have an active team for this sport.",
                        color = BrandBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun RegistrationPaymentMethodCard(selectedPayment: String, onPaymentSelected: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "CHOOSE PAYMENT METHOD",
                color = BrandBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            PaymentMethodOption(
                name = "Revolut",
                description = "Instant transfer",
                color = BrandBlue,
                selected = selectedPayment == "Revolut"
            ) { onPaymentSelected("Revolut") }

            Spacer(modifier = Modifier.height(10.dp))

            PaymentMethodOption(
                name = "MB Way",
                description = "Confirm via phone",
                color = BrandGreen,
                selected = selectedPayment == "MB Way"
            ) { onPaymentSelected("MB Way") }

            Spacer(modifier = Modifier.height(10.dp))

            PaymentMethodOption(
                name = "Credit Card",
                description = "Visa, Mastercard",
                color = Color(0xFF3B4A66),
                selected = selectedPayment == "Credit Card"
            ) { onPaymentSelected("Credit Card") }

            Spacer(modifier = Modifier.height(10.dp))

            PaymentMethodOption(
                name = "Apple Pay",
                description = "Fast payment",
                color = Color(0xFF101010),
                selected = selectedPayment == "Apple Pay"
            ) { onPaymentSelected("Apple Pay") }
        }
    }
}

@Composable
fun PaymentMethodOption(name: String, description: String, color: Color, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) SoftBlue else InputBg)
            .border(1.dp, if (selected) BrandBlue.copy(alpha = 0.35f) else BorderLine, RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1),
                color = BrandWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = BrandBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                color = TextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        SelectionCircle(selected = selected)
    }
}

@Composable
fun SelectionCircle(selected: Boolean) {
    Box(
        modifier = Modifier
            .size(22.dp)
            .clip(CircleShape)
            .border(
                width = 2.dp,
                color = if (selected) BrandGreen else Color(0xFFC5CBD6),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(BrandGreen)
            )
        }
    }
}

@Composable
fun RegistrationSummaryCard(payment: String, equipaNome: String, entryFee: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "REGISTRATION SUMMARY",
                color = BrandGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            SummaryRow("Team", equipaNome)
            SummaryRow("Entry Fee", "€ $entryFee", valueColor = BrandGreen)
            SummaryRow("Payment", payment)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Status after submission",
                    color = TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(SoftWarning)
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "Pending Approval",
                        color = WarningText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, valueColor: Color = BrandBlue) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(BorderLine)
    )
}

@Composable
fun RegistrationFeedbackCard(title: String, message: String, isError: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isError) Color(0xFFFFEFEF) else SoftGreen
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                color = if (isError) Color(0xFFD64A4A) else BrandGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                color = if (isError) Color(0xFF9A3A3A) else TextMuted,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// --- DTOs PARA A BASE DE DADOS ---

@Serializable
data class TorneioRegDTO(
    val id: Long,
    val nome: String,
    val local: String? = null,
    @SerialName("data_inicio") val dataInicio: String? = null,
    val formato: String? = null,
    @SerialName("taxa_inscricao") val custo: Double? = null,
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
    val divisao: String? = null,
    @SerialName("id_modalidade") val idModalidade: Int? = null
)

@Serializable
data class TorneioEquipaSimplesDTO(
    @SerialName("id_equipa") val idEquipa: Long? = null,
    val estado: String? = null
)

@Serializable
data class TorneioEquipaInsertDTO(
    @SerialName("id_torneio") val idTorneio: Long,
    @SerialName("id_equipa") val idEquipa: Long,
    val estado: String,
    @SerialName("pagamento_estado") val pagamentoEstado: String,
    val mensagem: String? = null
)