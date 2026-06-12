package com.example.trabalhocm.ui.screens.organizador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
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
fun CreateTournamentScreen(
    viewModel: CreateTournamentViewModel,
    onBackClick: () -> Unit = {},
    onProceedClick: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val sportFootballStr = stringResource(R.string.sport_football)
    val sportVolleyballStr = stringResource(R.string.sport_volleyball)
    val sportBasketballStr = stringResource(R.string.sport_basketball)

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
            MatchLeagueBottomBar(
                selectedTab = "TOURNAMENTS",
                onHomeClick = onHomeClick
            )
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
            HeaderSection()
            UploadLogoSection()

            Column {
                SectionLabel(stringResource(R.string.label_tournament_name))
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = viewModel.tournamentName,
                    onValueChange = { viewModel.tournamentName = it },
                    placeholder = { Text(stringResource(R.string.placeholder_tournament_name), color = Color.LightGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputBg,
                        unfocusedContainerColor = InputBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }

            Column {
                SectionLabel(stringResource(R.string.label_sport_category))
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SportCard(
                        modifier = Modifier.weight(1f),
                        title = sportFootballStr,
                        icon = Icons.Default.Star,
                        isSelected = viewModel.selectedSport == "Football",
                        onClick = {
                            viewModel.selectedSport = "Football"
                            viewModel.selectedFormat = "League System"
                        }
                    )
                    SportCard(
                        modifier = Modifier.weight(1f),
                        title = sportVolleyballStr,
                        icon = Icons.Default.Star,
                        isSelected = viewModel.selectedSport == "Volleyball",
                        onClick = {
                            viewModel.selectedSport = "Volleyball"
                            viewModel.selectedFormat = "Pool Play + Playoffs"
                        }
                    )
                    SportCard(
                        modifier = Modifier.weight(1f),
                        title = sportBasketballStr,
                        icon = Icons.Default.Star,
                        isSelected = viewModel.selectedSport == "Basketball",
                        onClick = {
                            viewModel.selectedSport = "Basketball"
                            viewModel.selectedFormat = "Regular Season + Playoffs"
                        }
                    )
                }
            }

            Column {
                SectionLabel(stringResource(R.string.label_competition_format))
                Spacer(modifier = Modifier.height(8.dp))

                when (viewModel.selectedSport) {
                    "Football" -> {
                        FormatCard(
                            title = stringResource(R.string.format_league),
                            description = stringResource(R.string.desc_format_league),
                            icon = Icons.Default.List,
                            isSelected = viewModel.selectedFormat == "League System",
                            onClick = { viewModel.selectedFormat = "League System" }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FormatCard(
                            title = stringResource(R.string.format_knockout),
                            description = stringResource(R.string.desc_format_knockout),
                            icon = Icons.Default.Share,
                            isSelected = viewModel.selectedFormat == "Knockout",
                            onClick = { viewModel.selectedFormat = "Knockout" }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FormatCard(
                            title = stringResource(R.string.format_group_knockout),
                            description = stringResource(R.string.desc_format_group_knockout),
                            icon = Icons.Default.Check,
                            isSelected = viewModel.selectedFormat == "Group+Knockout",
                            onClick = { viewModel.selectedFormat = "Group+Knockout" }
                        )
                    }

                    "Volleyball" -> {
                        FormatCard(
                            title = stringResource(R.string.format_pool_play),
                            description = stringResource(R.string.desc_format_pool_play),
                            icon = Icons.Default.Menu,
                            isSelected = viewModel.selectedFormat == "Pool Play + Playoffs",
                            onClick = { viewModel.selectedFormat = "Pool Play + Playoffs" }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FormatCard(
                            title = stringResource(R.string.format_double_elimination),
                            description = stringResource(R.string.desc_format_double_elimination),
                            icon = Icons.Default.Share,
                            isSelected = viewModel.selectedFormat == "Double Elimination",
                            onClick = { viewModel.selectedFormat = "Double Elimination" }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FormatCard(
                            title = stringResource(R.string.format_league),
                            description = stringResource(R.string.desc_format_league),
                            icon = Icons.Default.List,
                            isSelected = viewModel.selectedFormat == "League System",
                            onClick = { viewModel.selectedFormat = "League System" }
                        )
                    }

                    "Basketball" -> {
                        FormatCard(
                            title = stringResource(R.string.format_regular_playoffs),
                            description = stringResource(R.string.desc_format_regular_playoffs),
                            icon = Icons.Default.DateRange,
                            isSelected = viewModel.selectedFormat == "Regular Season + Playoffs",
                            onClick = { viewModel.selectedFormat = "Regular Season + Playoffs" }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FormatCard(
                            title = stringResource(R.string.format_single_elimination),
                            description = stringResource(R.string.desc_format_single_elimination),
                            icon = Icons.Default.Share,
                            isSelected = viewModel.selectedFormat == "Single Elimination Bracket",
                            onClick = { viewModel.selectedFormat = "Single Elimination Bracket" }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        FormatCard(
                            title = stringResource(R.string.format_3x3_pool),
                            description = stringResource(R.string.desc_format_3x3_pool),
                            icon = Icons.Default.Person,
                            isSelected = viewModel.selectedFormat == "3x3 Pool Play",
                            onClick = { viewModel.selectedFormat = "3x3 Pool Play" }
                        )
                    }
                }
            }

            Column {
                SectionLabel(stringResource(R.string.label_description_rules))
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = viewModel.description,
                    onValueChange = { viewModel.description = it },
                    placeholder = { Text(stringResource(R.string.placeholder_description), color = Color.LightGray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = InputBg,
                        unfocusedContainerColor = InputBg,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    maxLines = 5
                )
            }

            Button(
                onClick = onProceedClick,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(R.string.btn_proceed_schedule), fontWeight = FontWeight.Bold, fontSize = 14.sp, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HeaderSection() {
    Column {
        Text(stringResource(R.string.step_1_of_4), color = PrimaryBlue, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(stringResource(R.string.title_tournament_basics), color = DarkBlue, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 36.sp)

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier
                    .width(24.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(TealGreen))
                Box(modifier = Modifier
                    .width(24.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFE2E8F0)))
                Box(modifier = Modifier
                    .width(24.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFE2E8F0)))
                Box(modifier = Modifier
                    .width(24.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFE2E8F0)))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.desc_tournament_basics),
            color = TextGray,
            fontSize = 14.sp,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun UploadLogoSection() {
    Column {
        SectionLabel(stringResource(R.string.label_tournament_logo))
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .drawBehind {
                    val stroke = Stroke(
                        width = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                    )
                    drawRoundRect(
                        color = Color(0xFFCBD5E1),
                        style = stroke,
                        cornerRadius = CornerRadius(12.dp.toPx())
                    )
                }
                .background(CardBg, RoundedCornerShape(12.dp))
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.AccountBox, contentDescription = null, tint = TextGray, modifier = Modifier.size(48.dp))
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(R.string.btn_upload_tournament_logo), color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(stringResource(R.string.desc_logo_format), color = TextGray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun SportCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) TealGreen else Color.Transparent
    val iconColor = if (isSelected) TealGreen else TextGray
    val textColor = if (isSelected) DarkBlue else TextGray

    Card(
        modifier = modifier
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        border = BorderStroke(2.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, color = textColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FormatCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) TealGreen else Color.Transparent

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
                Icon(icon, contentDescription = null, tint = TextGray, modifier = Modifier.padding(8.dp))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = DarkBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(description, color = TextGray, fontSize = 12.sp, lineHeight = 16.sp)
            }

            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = TealGreen)
            }
        }
    }
}

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        color = TextGray,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )
}