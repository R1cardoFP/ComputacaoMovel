package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.ui.screens.MatchLeagueBottomBar
import com.example.trabalhocm.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTournamentStep4Screen(
    viewModel: CreateTournamentViewModel,
    onBackClick: () -> Unit = {},
    onPublishClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.title_create), color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.desc_back), tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Outlined.Notifications, contentDescription = stringResource(R.string.desc_notifications), tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DarkBlue)
            )
        },
        bottomBar = {
            MatchLeagueBottomBar(selectedTab = "TOURNAMENTS", onHomeClick = onHomeClick)
        },
        containerColor = BgLight
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Step4HeaderSection()

            Column {
                Step4SectionLabel(stringResource(R.string.label_my_role))
                Spacer(modifier = Modifier.height(8.dp))

                RoleCard(
                    title = stringResource(R.string.role_organizer_only),
                    description = stringResource(R.string.desc_organizer_only),
                    icon = Icons.Default.Build,
                    isSelected = viewModel.selectedRole == "Organizer Only",
                    onClick = { viewModel.selectedRole = "Organizer Only" }
                )
                Spacer(modifier = Modifier.height(12.dp))
                RoleCard(
                    title = stringResource(R.string.role_participate_player),
                    description = stringResource(R.string.desc_participate_player),
                    icon = Icons.Default.Person,
                    isSelected = viewModel.selectedRole == "Participate as Player",
                    onClick = { viewModel.selectedRole = "Participate as Player" }
                )
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = CardBg),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(stringResource(R.string.title_tournament_details), color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ReviewRow(stringResource(R.string.label_name), viewModel.tournamentName.ifBlank { stringResource(R.string.val_no_name) })
                    Spacer(modifier = Modifier.height(8.dp))
                    ReviewRow(stringResource(R.string.label_sport), viewModel.selectedSport)
                    Spacer(modifier = Modifier.height(8.dp))
                    ReviewRow(stringResource(R.string.label_format), viewModel.selectedFormat)
                    Spacer(modifier = Modifier.height(8.dp))
                    ReviewRow(stringResource(R.string.label_schedule), "${viewModel.startDate} - ${viewModel.endDate}")
                    Spacer(modifier = Modifier.height(8.dp))
                    ReviewRow(stringResource(R.string.label_max_teams), viewModel.maxParticipants)
                    Spacer(modifier = Modifier.height(8.dp))
                    ReviewRow(stringResource(R.string.label_location), viewModel.venue.ifBlank { "TBD" })
                }
            }

            if (viewModel.errorMessage.isNotBlank()) {
                Text(
                    text = viewModel.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 14.sp
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onBackClick,
                    modifier = Modifier.weight(0.35f),
                    enabled = !viewModel.isLoading
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = DarkBlue, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.btn_back_caps), color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }

                Button(
                    onClick = {
                        viewModel.publishTournament(onSuccess = onPublishClick)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = TealGreen),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(0.65f)
                        .height(56.dp),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(color = BrandWhite, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(stringResource(R.string.btn_publish_tournament), fontWeight = FontWeight.Bold, fontSize = 12.sp, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun Step4HeaderSection() {
    Column {
        Text(stringResource(R.string.step_4_of_4), color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.title_role_review), color = DarkBlue, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 36.sp)

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.width(24.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(TealGreen))
                Box(modifier = Modifier.width(24.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(TealGreen))
                Box(modifier = Modifier.width(24.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(TealGreen))
                Box(modifier = Modifier.width(24.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(TealGreen))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.desc_role_review),
            color = TextGray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun RoleCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) PrimaryBlue else Color.Transparent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(2.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = InputBg,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(icon, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.padding(8.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, color = TextGray, fontSize = 12.sp, lineHeight = 16.sp)
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isSelected) {
                Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = PrimaryBlue)
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(2.dp, Color(0xFFE2E8F0), CircleShape)
                )
            }
        }
    }
}

@Composable
fun StatusBadge(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
    }
}

@Composable
fun ReviewRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextGray, fontSize = 14.sp)
        Text(value, color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

private @Composable
fun Step4SectionLabel(text: String) {
    Text(
        text = text,
        color = TextGray,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
}