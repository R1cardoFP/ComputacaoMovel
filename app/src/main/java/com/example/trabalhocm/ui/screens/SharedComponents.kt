package com.example.trabalhocm.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trabalhocm.R
import com.example.trabalhocm.ui.theme.AppIcons

@Composable
fun MatchLeagueBottomBar(
    selectedTab: String,
    onHomeClick: () -> Unit = {},
    onTournamentsClick: () -> Unit = {},
    onMatchesClick: () -> Unit = {},
    onTeamsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
            .background(Color.White)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        BottomNavItem(
            icon = AppIcons.Home,
            title = stringResource(R.string.nav_home),
            selected = selectedTab == "HOME",
            onClick = onHomeClick
        )

        BottomNavItem(
            icon = AppIcons.Tournaments,
            title = stringResource(R.string.nav_tournaments),
            selected = selectedTab == "TOURNAMENTS",
            onClick = onTournamentsClick
        )

        BottomNavItem(
            icon = AppIcons.Games,
            title = stringResource(R.string.nav_matches),
            selected = selectedTab == "MATCHES",
            onClick = onMatchesClick
        )

        BottomNavItem(
            icon = AppIcons.Teams,
            title = stringResource(R.string.nav_teams),
            selected = selectedTab == "TEAMS",
            onClick = onTeamsClick
        )

        BottomNavItem(
            icon = AppIcons.Profile,
            title = stringResource(R.string.nav_profile),
            selected = selectedTab == "PROFILE",
            onClick = onProfileClick
        )
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    title: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val color = if (selected) Color(0xFF3566C9) else Color(0xFF9EA4B3)

    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = color,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = title,
            color = color,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}