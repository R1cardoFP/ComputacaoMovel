package com.example.trabalhocm.ui.screens.player

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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.BrandBlue
import com.example.trabalhocm.ui.theme.BrandGreen
import com.example.trabalhocm.ui.theme.BrandWhite

private val InviteScreenBg = Color(0xFFF4F6FB)
private val InviteInputBg = Color(0xFFEFF3F8)
private val InviteTextMuted = Color(0xFF6D7486)
private val InviteCardBorder = Color(0xFFE7EAF2)
private val InviteSoftGreen = Color(0xFFEAF8F5)
private val InviteSoftBlue = Color(0xFFEAF1FF)

@Composable
fun PlayerInvitePlayerScreen(
    onBackClick: () -> Unit = {},
    onSendInviteClick: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    val defaultMessage = stringResource(R.string.player_inviteplayer_default_message)

    var search by remember { mutableStateOf("") }
    var selectedPlayer by remember { mutableStateOf("Cristiano Ronaldo") }
    var selectedRole by remember { mutableStateOf("Midfielder") }
    var message by remember { mutableStateOf(defaultMessage) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(InviteScreenBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        InvitePlayerTopBar(onBackClick = onBackClick)

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            InvitePlayerHeroCard(
                selectedPlayer = selectedPlayer,
                selectedRole = selectedRole
            )

            Spacer(modifier = Modifier.height(16.dp))

            InviteSuccessBanner()

            Spacer(modifier = Modifier.height(14.dp))

            InviteSearchCard(
                search = search,
                onSearchChange = { search = it }
            )

            Spacer(modifier = Modifier.height(14.dp))

            InviteSectionCard(
                title = stringResource(R.string.player_inviteplayer_recommended),
                subtitle = stringResource(R.string.player_inviteplayer_subtitle)
            ) {
                RecommendedPlayerCard(
                    name = "Cristiano Ronaldo",
                    position = stringResource(R.string.player_pos_striker),
                    selected = selectedPlayer == "Cristiano Ronaldo",
                    onClick = { selectedPlayer = "Cristiano Ronaldo" }
                )

                Spacer(modifier = Modifier.height(10.dp))

                RecommendedPlayerCard(
                    name = "João Silva",
                    position = stringResource(R.string.player_pos_point_guard),
                    selected = selectedPlayer == "João Silva",
                    onClick = { selectedPlayer = "João Silva" }
                )

                Spacer(modifier = Modifier.height(10.dp))

                RecommendedPlayerCard(
                    name = "André Lima",
                    position = stringResource(R.string.player_pos_defender),
                    selected = selectedPlayer == "André Lima",
                    onClick = { selectedPlayer = "André Lima" }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            InviteSectionCard(
                title = stringResource(R.string.player_inviteplayer_details_title),
                subtitle = stringResource(R.string.player_inviteplayer_details_subtitle)
            ) {
                Text(
                    text = stringResource(R.string.player_inviteplayer_role_label).uppercase(),
                    color = InviteTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        InviteRoleButton(
                            text = stringResource(R.string.player_pos_striker),
                            selected = selectedRole == "Striker",
                            onClick = { selectedRole = "Striker" },
                            modifier = Modifier.weight(1f)
                        )

                        InviteRoleButton(
                            text = stringResource(R.string.player_pos_defender),
                            selected = selectedRole == "Defender",
                            onClick = { selectedRole = "Defender" },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        InviteRoleButton(
                            text = stringResource(R.string.player_pos_midfielder),
                            selected = selectedRole == "Midfielder",
                            onClick = { selectedRole = "Midfielder" },
                            modifier = Modifier.weight(1f)
                        )

                        InviteRoleButton(
                            text = stringResource(R.string.player_pos_goalkeeper),
                            selected = selectedRole == "Goalkeeper",
                            onClick = { selectedRole = "Goalkeeper" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.player_inviteplayer_message_label).uppercase(),
                        color = InviteTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = stringResource(R.string.player_common_optional),
                        color = InviteTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(108.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = InviteInputBg,
                        unfocusedContainerColor = InviteInputBg,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        cursorColor = BrandGreen,
                        focusedTextColor = BrandBlue,
                        unfocusedTextColor = BrandBlue
                    )
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onSendInviteClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BrandGreen,
                    contentColor = BrandWhite
                )
            ) {
                Text(
                    text = "${stringResource(R.string.player_inviteplayer_send)}  →",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )
            }

            Spacer(modifier = Modifier.height(22.dp))
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
}

@Composable
private fun InvitePlayerTopBar(
    onBackClick: () -> Unit
) {
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
            text = stringResource(R.string.player_inviteplayer_topbar),
            color = BrandWhite,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✉",
                color = BrandWhite,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun InvitePlayerHeroCard(
    selectedPlayer: String,
    selectedRole: String
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
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "＋",
                        color = BrandWhite,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.player_inviteplayer_eyebrow).uppercase(),
                        color = BrandGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.8.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.player_inviteplayer_title),
                        color = BrandWhite,
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp
                    )

                    Spacer(modifier = Modifier.height(5.dp))

                    Text(
                        text = stringResource(R.string.player_inviteplayer_subtitle),
                        color = Color.White.copy(alpha = 0.72f),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                InviteHeroInfoChip(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.player_inviteplayer_recommended),
                    value = "3"
                )

                InviteHeroInfoChip(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.player_inviteplayer_role_label),
                    value = selectedRole
                )

                InviteHeroInfoChip(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.player_inviteplayer_search_label),
                    value = selectedPlayer.take(10)
                )
            }
        }
    }
}

@Composable
private fun InviteHeroInfoChip(
    modifier: Modifier = Modifier,
    label: String,
    value: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label.uppercase(),
            color = Color.White.copy(alpha = 0.58f),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            color = BrandWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun InviteSuccessBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(InviteSoftGreen)
            .border(1.dp, BrandGreen.copy(alpha = 0.20f), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(BrandGreen.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✓",
                color = BrandGreen,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = stringResource(R.string.player_inviteplayer_success),
            color = BrandGreen,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 16.sp
        )
    }
}

@Composable
private fun InviteSearchCard(
    search: String,
    onSearchChange: (String) -> Unit
) {
    InviteSectionCard(
        title = stringResource(R.string.player_inviteplayer_search_label),
        subtitle = stringResource(R.string.player_inviteplayer_search_placeholder)
    ) {
        OutlinedTextField(
            value = search,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            singleLine = true,
            placeholder = {
                Text(
                    text = stringResource(R.string.player_inviteplayer_search_placeholder),
                    color = Color(0xFF9EA4B3),
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Text(
                    text = "⌕",
                    color = InviteTextMuted,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = InviteInputBg,
                unfocusedContainerColor = InviteInputBg,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = BrandGreen,
                focusedTextColor = BrandBlue,
                unfocusedTextColor = BrandBlue
            )
        )
    }
}

@Composable
private fun InviteSectionCard(
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = BrandWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = title,
                color = BrandBlue,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            if (!subtitle.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = subtitle,
                    color = InviteTextMuted,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
    }
}

@Composable
private fun RecommendedPlayerCard(
    name: String,
    position: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (selected) BrandGreen else InviteCardBorder
    val bgColor = if (selected) InviteSoftGreen else BrandWhite

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.drawable.avatar_player),
            contentDescription = name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(InviteSoftBlue)
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = BrandBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = position,
                color = InviteTextMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(if (selected) BrandGreen else InviteInputBg),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (selected) "✓" else "+",
                color = if (selected) BrandWhite else BrandBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun InviteRoleButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = if (selected) BrandGreen else InviteCardBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .background(if (selected) BrandGreen else InviteInputBg)
            .clickable { onClick() }
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) BrandWhite else BrandBlue,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true, name = "Player Invite Player Screen")
@Composable
fun PlayerInvitePlayerScreenPreview() {
    PlayerInvitePlayerScreen()
}
