package com.example.trabalhocm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.trabalhocm.ui.screens.ChangePasswordScreen
import com.example.trabalhocm.ui.screens.HomeScreen
import com.example.trabalhocm.ui.screens.LoginScreen
import com.example.trabalhocm.ui.screens.OfflineScreen
import com.example.trabalhocm.ui.screens.OnboardingFlow
import com.example.trabalhocm.ui.screens.PlayerBecomeOrganizerScreen
import com.example.trabalhocm.ui.screens.PlayerHomeScreen
import com.example.trabalhocm.ui.screens.PlayerStatsScreen
import com.example.trabalhocm.ui.screens.PlayerTournamentDetailsScreen
import com.example.trabalhocm.ui.screens.PlayerTournamentFiltersScreen
import com.example.trabalhocm.ui.screens.PlayerTournamentHistoryScreen
import com.example.trabalhocm.ui.screens.PlayerTournamentManagementScreen
import com.example.trabalhocm.ui.screens.PlayerTournamentRegistrationScreen
import com.example.trabalhocm.ui.screens.ProfileScreen
import com.example.trabalhocm.ui.screens.RecoverPasswordScreen
import com.example.trabalhocm.ui.screens.RegisterScreen
import com.example.trabalhocm.ui.screens.SplashScreen
import com.example.trabalhocm.ui.screens.TorneiosScreen
import com.example.trabalhocm.ui.screens.UserTypeScreen
import com.example.trabalhocm.ui.theme.TrabalhoCMTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrabalhoCMTheme {
                MatchPointApp()
            }
        }
    }
}

@Composable
fun MatchPointApp() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            AppBottomBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") {
                SplashScreen(onEnd = { navController.navigate("onboarding") { popUpTo("splash") { inclusive = true } } })
            }
            composable("onboarding") {
                OnboardingFlow(onFinish = { navController.navigate("login") { popUpTo("onboarding") { inclusive = true } } })
            }
            composable("login") {
                LoginScreen(
                    onLoginSuccess = { navController.navigate("user_type") { popUpTo("login") { inclusive = true } } },
                    onCreateAccount = { navController.navigate("register") },
                    onForgotPassword = { navController.navigate("recover_password") }
                )
            }
            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = { navController.navigate("user_type") { popUpTo("register") { inclusive = true } } },
                    onGoToLogin = { navController.popBackStack() }
                )
            }
            composable("recover_password") { RecoverPasswordScreen(onGoToLogin = { navController.popBackStack() }) }
            composable("change_password") { ChangePasswordScreen(onPasswordChanged = { navController.popBackStack() }) }
            composable("user_type") {
                UserTypeScreen(
                    onAdminClick = { navController.navigate("home") },
                    onOrganizerClick = { navController.navigate("home") },
                    onPlayerClick = { navController.navigate("player_home") }
                )
            }
            composable("offline") { OfflineScreen(onRetrySync = { navController.popBackStack() }) }
            composable("player_home") { PlayerHomeScreen(onTournamentsClick = { navController.navigate("player_tournaments") }) }
            composable("player_tournaments") {
                PlayerTournamentManagementScreen(
                    onDetailsClick = { navController.navigate("player_tournament_details") },
                    onRegisterClick = { navController.navigate("player_tournament_registration") },
                    onAskOrganizerClick = { navController.navigate("player_become_organizer") },
                    onHistoryClick = { navController.navigate("player_tournament_history") },
                    onFiltersClick = { navController.navigate("player_tournament_filters") }
                )
            }
            composable("player_tournament_history") { PlayerTournamentHistoryScreen(onBackClick = { navController.popBackStack() }) }
            composable("player_tournament_details") { PlayerTournamentDetailsScreen(onBackClick = { navController.popBackStack() }) }
            composable("player_tournament_filters") {
                PlayerTournamentFiltersScreen(
                    onCloseClick = { navController.popBackStack() },
                    onApplyClick = { navController.popBackStack() }
                )
            }
            composable("player_tournament_registration") {
                PlayerTournamentRegistrationScreen(
                    onBackClick = { navController.popBackStack() },
                    onSubmitClick = { navController.popBackStack() }
                )
            }
            composable("player_become_organizer") {
                PlayerBecomeOrganizerScreen(
                    onBackClick = { navController.popBackStack() },
                    onSubmitClick = { navController.popBackStack() },
                    onCancelClick = { navController.popBackStack() }
                )
            }
            composable("home") { HomeScreen(onVerTorneios = { navController.navigate("torneios") }) }
            composable("torneios") { TorneiosScreen() }
            composable("player_stats") {
                PlayerStatsScreen(
                    onBackClick = { navController.popBackStack() },
                    playerName = "A carregar...",
                    footballGoals = 24,
                    footballAssists = 18,
                    basketballPoints = 412,
                    basketballWinRate = 65,
                    volleyballSpikes = 84,
                    volleyballWinRate = 82
                )
            }
            composable("profile") {
                val authRepository = remember { com.example.trabalhocm.data.repository.AuthRepository() }
                val scope = rememberCoroutineScope()
                var nomeUtilizador by remember { mutableStateOf("A carregar...") }
                var emailUtilizador by remember { mutableStateOf("A carregar...") }

                LaunchedEffect(Unit) {
                    authRepository.obterUtilizadorAtual().onSuccess { utilizador ->
                        nomeUtilizador = utilizador.nome
                        emailUtilizador = utilizador.email
                    }.onFailure {
                        nomeUtilizador = "Erro ao carregar"
                        emailUtilizador = "Erro ao carregar"
                    }
                }

                ProfileScreen(
                    initialName = nomeUtilizador,
                    initialEmail = emailUtilizador,
                    onLogoutClick = {
                        scope.launch {
                            authRepository.logout()
                            navController.navigate("login") { popUpTo(0) { inclusive = true } }
                        }
                    },
                    onSaveChanges = { novoNome, _, _ ->
                        scope.launch {
                            authRepository.atualizarNomeUtilizador(novoNome).onSuccess {
                                nomeUtilizador = novoNome
                                snackbarHostState.showSnackbar("Profile updated successfully.")
                            }.onFailure { erro ->
                                snackbarHostState.showSnackbar("Erro: ${erro.message}")
                            }
                        }
                    },
                    onDashboardClick = {
                        navController.navigate("player_stats")
                    }
                )
            }
        }
    }
}

@Composable
fun AppBottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val ecransComBarra = listOf(
        "player_home",
        "player_tournaments",
        "player_tournament_details",
        "player_become_organizer",
        "player_tournament_history",
        "profile"
    )

    if (currentRoute !in ecransComBarra) return

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(66.dp)
            .background(Color(0xFFFFFFFF))
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        GlobalBottomItem(
            icon = "🏠", title = "HOME",
            selected = currentRoute == "player_home",
            onClick = { if (currentRoute != "player_home") navController.navigate("player_home") { popUpTo("player_home") { inclusive = false } } }
        )
        GlobalBottomItem(
            icon = "🏆", title = "TOURNAMENTS",
            selected = currentRoute?.startsWith("player_tournament") == true,
            onClick = { if (currentRoute != "player_tournaments") navController.navigate("player_tournaments") { popUpTo("player_home") { inclusive = false } } }
        )
        GlobalBottomItem(
            icon = "⚽", title = "MATCHES",
            selected = currentRoute == "matches",
            onClick = {}
        )
        GlobalBottomItem(
            icon = "👥", title = "TEAMS",
            selected = currentRoute == "teams",
            onClick = {}
        )
        GlobalBottomItem(
            icon = "👤", title = "PROFILE",
            selected = currentRoute == "profile",
            onClick = { if (currentRoute != "profile") navController.navigate("profile") { popUpTo("player_home") { inclusive = false } } }
        )
    }
}

@Composable
fun GlobalBottomItem(icon: String, title: String, selected: Boolean, onClick: () -> Unit) {
    val color = if (selected) Color(0xFF3566C9) else Color(0xFF9EA4B3)
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 20.sp,
            modifier = Modifier
                .graphicsLayer(alpha = 0.99f)
                .drawWithContent {
                    drawContent()
                    drawRect(
                        color = color,
                        blendMode = BlendMode.SrcIn
                    )
                }
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(text = title, color = color, fontSize = 8.sp, fontWeight = FontWeight.Bold)
    }
}