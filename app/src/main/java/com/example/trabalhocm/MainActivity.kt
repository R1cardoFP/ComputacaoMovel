package com.example.trabalhocm

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.edit
import androidx.core.net.toUri
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.trabalhocm.ui.screens.BrowseTeamsScreen
import com.example.trabalhocm.ui.screens.CreateTeamScreen
import com.example.trabalhocm.ui.screens.auth.ChangePasswordScreen
import com.example.trabalhocm.ui.screens.organizador.HomeScreen
import com.example.trabalhocm.ui.screens.organizador.CreateTournamentScreen
import com.example.trabalhocm.ui.screens.auth.LoginScreen
import com.example.trabalhocm.ui.screens.OfflineScreen
import com.example.trabalhocm.ui.screens.onboarding.OnboardingFlow
import com.example.trabalhocm.ui.screens.player.PlayerBecomeOrganizerScreen
import com.example.trabalhocm.ui.screens.player.PlayerCasualMatchDetailsScreen
import com.example.trabalhocm.ui.screens.player.PlayerCreateTeamScreen
import com.example.trabalhocm.ui.screens.player.PlayerHomeScreen
import com.example.trabalhocm.ui.screens.player.PlayerLiveMatchScreen
import com.example.trabalhocm.ui.screens.player.PlayerManageTeamScreen
import com.example.trabalhocm.ui.screens.player.PlayerMatchCalendarScreen
import com.example.trabalhocm.ui.screens.player.PlayerMatchFiltersScreen
import com.example.trabalhocm.ui.screens.player.PlayerMatchesScreen
import com.example.trabalhocm.ui.screens.player.PlayerNotificationsScreen
import com.example.trabalhocm.ui.screens.player.PlayerProfileScreen
import com.example.trabalhocm.ui.screens.player.PlayerStatsScreen
import com.example.trabalhocm.ui.screens.player.PlayerTeamPlayerDetailsScreen
import com.example.trabalhocm.ui.screens.player.PlayerTeamsScreen
import com.example.trabalhocm.ui.screens.player.PlayerTournamentDetailsScreen
import com.example.trabalhocm.ui.screens.player.PlayerTournamentFiltersScreen
import com.example.trabalhocm.ui.screens.player.PlayerTournamentHistoryScreen
import com.example.trabalhocm.ui.screens.player.PlayerTournamentManagementScreen
import com.example.trabalhocm.ui.screens.player.PlayerTournamentRegistrationScreen
import com.example.trabalhocm.ui.screens.auth.RecoverPasswordScreen
import com.example.trabalhocm.ui.screens.auth.RegisterScreen
import com.example.trabalhocm.ui.screens.auth.VerifyAccountScreen
import com.example.trabalhocm.ui.screens.onboarding.SplashScreen
import com.example.trabalhocm.ui.screens.TorneiosScreen
import com.example.trabalhocm.ui.theme.TrabalhoCMTheme
import com.example.trabalhocm.ui.screens.admin.AdminHomeScreen
import com.example.trabalhocm.ui.screens.admin.AdminProfileScreen
import com.example.trabalhocm.ui.screens.organizador.CreateTournamentStep2Screen
import com.example.trabalhocm.ui.screens.organizador.CreateTournamentStep3Screen
import com.example.trabalhocm.ui.screens.organizador.CreateTournamentStep4Screen
import com.example.trabalhocm.ui.screens.organizador.TeamDetailsScreen
import com.example.trabalhocm.ui.screens.organizador.InvitePlayerScreen
import com.example.trabalhocm.ui.screens.organizador.OrganizerMatchesScreen
import com.example.trabalhocm.ui.screens.organizador.OrganizerProfileScreen
import com.example.trabalhocm.ui.screens.admin.AdminUserManagementScreen
import com.example.trabalhocm.ui.screens.admin.AdminNotificationsScreen
import com.example.trabalhocm.ui.screens.admin.AdminOrganizerRequestsScreen
import com.example.trabalhocm.ui.screens.player.PlayerMatchHistoryScreen
import com.example.trabalhocm.ui.screens.admin.AdminTournamentArchiveScreen
import com.example.trabalhocm.ui.screens.admin.AdminTournamentsScreen
import com.example.trabalhocm.ui.screens.admin.AdminTournamentDetailsScreen
import com.example.trabalhocm.ui.screens.admin.AdminTournamentEditScreen
import com.example.trabalhocm.ui.screens.admin.AdminTeamsScreen
import kotlinx.coroutines.launch
import com.example.trabalhocm.ui.screens.player.PlayerCalendarMatchDetailsScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trabalhocm.ui.screens.organizador.CreateTournamentViewModel
import com.example.trabalhocm.ui.screens.organizador.OrganizerMatchesViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            TrabalhoCMTheme {
                MatchLeagueApp()
            }
        }
    }
}

@Composable
fun MatchLeagueApp() {
    val navController = rememberNavController()

    val createTournamentViewModel: CreateTournamentViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "admin_home"
    ) {
        // ==========================================
        // 1. INICIALIZAÇÃO & AUTENTICAÇÃO
        // ==========================================
        composable("splash") {
            SplashScreen(onEnd = {
                navController.navigate("onboarding") {
                    popUpTo("splash") {
                        inclusive = true
                    }
                }
            })
        }

        composable("onboarding") {
            OnboardingFlow(onFinish = {
                navController.navigate("login") {
                    popUpTo("onboarding") {
                        inclusive = true
                    }
                }
            })
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("user_type") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                },
                onCreateAccount = { navController.navigate("register") },
                onForgotPassword = { navController.navigate("recover_password") }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = { email ->
                    val encodedEmail = Uri.encode(email)
                    navController.navigate("verify_account/$encodedEmail") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onGoToLogin = { navController.popBackStack() }
            )
        }

        composable(
            route = "verify_account/{email}",
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedEmail = backStackEntry.arguments?.getString("email") ?: ""
            val email = Uri.decode(encodedEmail)

            VerifyAccountScreen(
                email = email,
                onVerificationSuccess = {
                    navController.navigate("user_type") {
                        popUpTo("verify_account") { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("recover_password") {
            RecoverPasswordScreen(onBackClick = { navController.popBackStack() })
        }

        composable("change_password") {
            ChangePasswordScreen(onPasswordChanged = { navController.popBackStack() })
        }

        composable("user_type") {
            val authRepository = remember { com.example.trabalhocm.data.repository.AuthRepository() }

            LaunchedEffect(Unit) {
                authRepository.obterUtilizadorAtual().onSuccess { utilizador ->
                    authRepository.obterPapeisUtilizador(utilizador.id).onSuccess { papeis ->
                        val destino = when {
                            papeis.contains(1) -> "admin_home"
                            papeis.contains(2) -> "home"
                            else -> "player_home"
                        }

                        navController.navigate(destino) {
                            popUpTo("user_type") { inclusive = true }
                        }
                    }.onFailure {
                        navController.navigate("player_home") {
                            popUpTo("user_type") { inclusive = true }
                        }
                    }
                }.onFailure {
                    navController.navigate("login") {
                        popUpTo("user_type") { inclusive = true }
                    }
                }
            }

            androidx.compose.foundation.layout.Box(
                modifier = androidx.compose.ui.Modifier
                    .fillMaxSize()
                    .background(androidx.compose.ui.graphics.Color(0xFFF4F5FA)),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator(
                    color = androidx.compose.ui.graphics.Color(0xFF008D7D)
                )
            }
        }

        // ==========================================
        // 2. ÁREA DO JOGADOR (Player)
        // ==========================================
        composable("player_home") {
            PlayerHomeScreen(
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onCasualMatchesClick = { navController.navigate("player_matches") },
                onLiveMatchesClick = { navController.navigate("player_matches") },
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = { navController.navigate("player_profile") }
            )
        }

        composable("player_matches") {
            PlayerMatchesScreen(
                onCalendarClick = {
                    navController.navigate("player_match_calendar")
                },
                onHistoryClick = {
                    navController.navigate("player_match_history")
                },
                onFiltersClick = {
                    navController.navigate("player_match_filters")
                },
                onDetailsClick = { idPeladinha ->
                    navController.navigate("player_casual_match_details/$idPeladinha")
                },
                onJoinMatchClick = {},
                onWatchLiveClick = { idJogo ->
                    navController.navigate("player_live_match/$idJogo")
                },
                onAskOrganizerClick = { navController.navigate("player_become_organizer") },
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onMatchesClick = {},
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = { navController.navigate("player_profile") }
            )
        }

        composable("player_match_calendar") {
            PlayerMatchCalendarScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onWatchLiveClick = { idJogo ->
                    navController.navigate("player_live_match/$idJogo")
                },
                onDetailsClick = { idJogo ->
                    navController.navigate("player_calendar_match_details/$idJogo")
                },
                onHomeClick = {
                    navController.navigate("player_home")
                },
                onTournamentsClick = {
                    navController.navigate("player_tournaments")
                },
                onMatchesClick = {
                    navController.navigate("player_matches")
                },
                onTeamsClick = {
                    navController.navigate("player_teams")
                },
                onProfileClick = {
                    navController.navigate("player_profile")
                }
            )
        }

        composable(
            route = "player_calendar_match_details/{idJogo}",
            arguments = listOf(navArgument("idJogo") { type = NavType.LongType })
        ) { backStackEntry ->
            val idJogo = backStackEntry.arguments?.getLong("idJogo") ?: 0L

            PlayerCalendarMatchDetailsScreen(
                idJogo = idJogo,
                onBackClick = {
                    navController.popBackStack()
                },
                onWatchLiveClick = { jogoId ->
                    navController.navigate("player_live_match/$jogoId")
                },
                onHomeClick = {
                    navController.navigate("player_home")
                },
                onTournamentsClick = {
                    navController.navigate("player_tournaments")
                },
                onMatchesClick = {
                    navController.navigate("player_matches")
                },
                onTeamsClick = {
                    navController.navigate("player_teams")
                },
                onProfileClick = {
                    navController.navigate("player_profile")
                }
            )
        }

        composable(
            route = "player_casual_match_details/{idPeladinha}",
            arguments = listOf(navArgument("idPeladinha") { type = NavType.LongType })
        ) { backStackEntry ->
            val idPeladinha = backStackEntry.arguments?.getLong("idPeladinha") ?: 0L

            PlayerCasualMatchDetailsScreen(
                idPeladinha = idPeladinha,
                onBackClick = {
                    navController.popBackStack()
                },
                onJoinSuccess = {},
                onHomeClick = {
                    navController.navigate("player_home")
                },
                onTournamentsClick = {
                    navController.navigate("player_tournaments")
                },
                onMatchesClick = {
                    navController.navigate("player_matches")
                },
                onTeamsClick = {
                    navController.navigate("player_teams")
                },
                onProfileClick = {
                    navController.navigate("player_profile")
                }
            )
        }

        composable(
            route = "player_live_match/{idJogo}",
            arguments = listOf(navArgument("idJogo") { type = NavType.LongType })
        ) { backStackEntry ->
            val idJogo = backStackEntry.arguments?.getLong("idJogo") ?: 0L

            PlayerLiveMatchScreen(
                idJogo = idJogo,
                onBackClick = {
                    navController.popBackStack()
                },
                onCalendarClick = {
                    navController.navigate("player_match_calendar")
                },
                onHistoryClick = {
                    navController.navigate("player_match_history")
                },
                onCasualMatchesClick = {
                    navController.navigate("player_matches")
                },
                onHomeClick = {
                    navController.navigate("player_home")
                },
                onTournamentsClick = {
                    navController.navigate("player_tournaments")
                },
                onMatchesClick = {
                    navController.navigate("player_matches")
                },
                onTeamsClick = {
                    navController.navigate("player_teams")
                },
                onProfileClick = {
                    navController.navigate("player_profile")
                }
            )
        }

        composable("player_match_history") {
            PlayerMatchHistoryScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onHomeClick = {
                    navController.navigate("player_home")
                },
                onTournamentsClick = {
                    navController.navigate("player_tournaments")
                },
                onMatchesClick = {
                    navController.navigate("player_matches")
                },
                onTeamsClick = {
                    navController.navigate("player_teams")
                },
                onProfileClick = {
                    navController.navigate("player_profile")
                }
            )
        }

        composable("player_match_filters") {
            PlayerMatchFiltersScreen(
                onCloseClick = {
                    navController.popBackStack()
                },
                onResetClick = {},
                onApplyClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("player_profile") {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val authRepository = remember { com.example.trabalhocm.data.repository.AuthRepository() }

            var usernameUtilizador by remember { mutableStateOf("A carregar...") }
            var nomeUtilizador by remember { mutableStateOf("A carregar...") }
            var emailUtilizador by remember { mutableStateOf("A carregar...") }
            var bioUtilizador by remember { mutableStateOf("") }
            var photoUri by remember { mutableStateOf<Uri?>(null) }
            var userId by remember { mutableStateOf("") }
            var rolesUtilizador by remember { mutableStateOf(listOf("A carregar...")) }

            var anoMembro by remember { mutableStateOf("...") }

            val sharedPrefs = remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }

            LaunchedEffect(Unit) {
                authRepository.obterUtilizadorAtual().onSuccess { utilizador ->
                    usernameUtilizador = utilizador.username
                    nomeUtilizador = utilizador.nome
                    emailUtilizador = utilizador.email
                    userId = utilizador.id

                    val dataCriacaoBD = utilizador.dataCriacao
                    if (!dataCriacaoBD.isNullOrEmpty() && dataCriacaoBD.length >= 4) {
                        anoMembro = dataCriacaoBD.substring(0, 4)
                    } else {
                        anoMembro = "2026"
                    }

                    if (!utilizador.fotoUrl.isNullOrEmpty()) {
                        val urlAtualizada = "${utilizador.fotoUrl}?v=${System.currentTimeMillis()}"
                        photoUri = urlAtualizada.toUri()
                    } else {
                        val uriRegisto = sharedPrefs.getString("avatar_${utilizador.email}", null)

                        if (uriRegisto != null) {
                            val localUri = uriRegisto.toUri()
                            photoUri = localUri

                            try {
                                val imageStream = context.contentResolver.openInputStream(localUri)
                                val imageBytes = imageStream?.readBytes()
                                imageStream?.close()

                                if (imageBytes != null) {
                                    authRepository.atualizarFotoPerfil(imageBytes)
                                    sharedPrefs.edit { remove("avatar_${utilizador.email}") }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }

                    authRepository.obterPapeisUtilizador(utilizador.id).onSuccess { papeis ->
                        val tags = mutableListOf<String>()

                        if (papeis.contains(1)) tags.add("ADMIN")
                        if (papeis.contains(2)) tags.add("ORGANIZADOR")
                        if (papeis.contains(3)) tags.add("PLAYER")

                        if (tags.isEmpty()) tags.add("PLAYER")

                        rolesUtilizador = tags
                    }

                    val savedBio = sharedPrefs.getString("bio_$userId", null)
                    if (savedBio != null) bioUtilizador = savedBio
                }.onFailure {
                    usernameUtilizador = "Erro"
                    nomeUtilizador = "Erro ao carregar"
                    emailUtilizador = "Erro ao carregar"
                }
            }

            PlayerProfileScreen(
                initialUsername = usernameUtilizador,
                initialName = nomeUtilizador,
                initialEmail = emailUtilizador,
                initialBio = bioUtilizador,
                initialPhotoUri = photoUri,
                roles = rolesUtilizador,
                memberSinceYear = anoMembro,
                onLogoutClick = {
                    scope.launch {
                        authRepository.logout()
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    }
                },
                onSaveChanges = { novoUsername, novaBio, novaPhotoUri ->
                    scope.launch {
                        authRepository.atualizarPerfil(novoUsername, novaBio)

                        if (novaPhotoUri != null && novaPhotoUri != photoUri && novaPhotoUri.toString().startsWith("content://")) {
                            try {
                                val imageStream = context.contentResolver.openInputStream(novaPhotoUri)
                                val imageBytes = imageStream?.readBytes()
                                imageStream?.close()

                                if (imageBytes != null) {
                                    authRepository.atualizarFotoPerfil(imageBytes)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }

                        sharedPrefs.edit {
                            putString("bio_$userId", novaBio)
                        }

                        usernameUtilizador = novoUsername
                        bioUtilizador = novaBio
                        if (novaPhotoUri != null) photoUri = novaPhotoUri
                    }
                },
                onDashboardClick = { navController.navigate("player_stats") },
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onMatchesClick = { navController.navigate("player_matches") },
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = {},
                onNotificationsClick = { navController.navigate("player_notifications") }
            )
        }

        composable("player_stats") {
            val authRepository = remember { com.example.trabalhocm.data.repository.AuthRepository() }

            var nomeUtilizador by remember { mutableStateOf("A carregar...") }
            var usernameUtilizador by remember { mutableStateOf("") }
            var fotoUriUtilizador by remember { mutableStateOf<Uri?>(null) }

            var futPontuacao by remember { mutableIntStateOf(0) }
            var futVitorias by remember { mutableIntStateOf(0) }

            var basqPontuacao by remember { mutableIntStateOf(0) }
            var basqWinRate by remember { mutableIntStateOf(0) }

            var volPontuacao by remember { mutableIntStateOf(0) }
            var volWinRate by remember { mutableIntStateOf(0) }

            LaunchedEffect(Unit) {
                authRepository.obterUtilizadorAtual().onSuccess { utilizador ->
                    nomeUtilizador = utilizador.nome
                    usernameUtilizador = utilizador.username

                    if (!utilizador.fotoUrl.isNullOrEmpty()) {
                        val urlAtualizada = "${utilizador.fotoUrl}?v=${System.currentTimeMillis()}"
                        fotoUriUtilizador = urlAtualizada.toUri()
                    }

                    authRepository.obterEstatisticasJogador(utilizador.id).onSuccess { listaEstatisticas ->
                        listaEstatisticas.forEach { estatistica ->

                            val winRate = if (estatistica.numJogos > 0) {
                                (estatistica.vitorias * 100) / estatistica.numJogos
                            } else 0

                            when (estatistica.idModalidade) {
                                1 -> {
                                    futPontuacao = estatistica.pontuacao
                                    futVitorias = estatistica.vitorias
                                }
                                2 -> {
                                    basqPontuacao = estatistica.pontuacao
                                    basqWinRate = winRate
                                }
                                3 -> {
                                    volPontuacao = estatistica.pontuacao
                                    volWinRate = winRate
                                }
                            }
                        }
                    }
                }
            }

            PlayerStatsScreen(
                playerName = nomeUtilizador,
                playerUsername = usernameUtilizador,
                playerPhotoUri = fotoUriUtilizador,

                footballGoals = futPontuacao,
                footballAssists = futVitorias,
                basketballPoints = basqPontuacao,
                basketballWinRate = basqWinRate,
                volleyballSpikes = volPontuacao,
                volleyballWinRate = volWinRate,

                onBackClick = { navController.popBackStack() },
                onNotificationsClick = { navController.navigate("player_notifications") }
            )
        }

        composable("player_notifications") {
            PlayerNotificationsScreen(
                onBackClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onMatchesClick = { navController.navigate("player_matches") },
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = { navController.navigate("player_profile") }
            )
        }

        composable("player_tournaments") {
            PlayerTournamentManagementScreen(
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = {},
                onMatchesClick = { navController.navigate("player_matches") },
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = { navController.navigate("player_profile") },
                onDetailsClick = { idTorneio ->
                    navController.navigate("player_tournament_details/$idTorneio")
                },
                onRegisterClick = { navController.navigate("player_tournament_registration") },
                onAskOrganizerClick = { navController.navigate("player_become_organizer") },
                onHistoryClick = { navController.navigate("player_tournament_history") },
                onFiltersClick = { navController.navigate("player_tournament_filters") }
            )
        }

        composable(
            route = "player_tournament_details/{idTorneio}",
            arguments = listOf(navArgument("idTorneio") { type = NavType.LongType })
        ) { backStackEntry ->
            val idTorneio = backStackEntry.arguments?.getLong("idTorneio") ?: 0L

            PlayerTournamentDetailsScreen(
                idTorneio = idTorneio,
                onBackClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onMatchesClick = { navController.navigate("player_matches") },
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = { navController.navigate("player_profile") }
            )
        }

        composable("player_tournament_history") {
            PlayerTournamentHistoryScreen(
                onBackClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onMatchesClick = { navController.navigate("player_matches") },
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = { navController.navigate("player_profile") }
            )
        }

        composable("player_tournament_details") {
            PlayerTournamentDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onMatchesClick = { navController.navigate("player_matches") },
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = { navController.navigate("player_profile") }
            )
        }

        composable("player_tournament_filters") {
            PlayerTournamentFiltersScreen(
                onCloseClick = { navController.popBackStack() },
                onApplyClick = { navController.popBackStack() }
            )
        }

        composable("player_tournament_registration") {
            PlayerTournamentRegistrationScreen(
                onBackClick = { navController.popBackStack() },
                onSubmitClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onMatchesClick = { navController.navigate("player_matches") },
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = { navController.navigate("player_profile") }
            )
        }

        composable("player_become_organizer") {
            PlayerBecomeOrganizerScreen(
                onBackClick = { navController.popBackStack() },
                onSubmitClick = { navController.popBackStack() },
                onCancelClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onMatchesClick = { navController.navigate("player_matches") },
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = { navController.navigate("player_profile") }
            )
        }

        composable("player_teams") {
            PlayerTeamsScreen(
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onMatchesClick = { navController.navigate("player_matches") },
                onTeamsClick = {},
                onProfileClick = { navController.navigate("player_profile") },
                onTeamDetailsClick = { isUserTeam -> navController.navigate("player_team_details/$isUserTeam") },
                onManageTeamClick = { navController.navigate("player_manage_team") },
                onCreateTeamClick = { navController.navigate("player_create_team") }
            )
        }

        composable(
            route = "player_team_details/{isUserTeam}",
            arguments = listOf(navArgument("isUserTeam") { type = NavType.BoolType })
        ) { backStackEntry ->
            val isUserTeam = backStackEntry.arguments?.getBoolean("isUserTeam") ?: false

            TeamDetailsScreen(
                isUserTeam = isUserTeam,
                onBackClick = { navController.popBackStack() },
                onInvitePlayerClick = { navController.navigate("player_invite_player") },
                onViewPlayerProfileClick = { navController.navigate("player_team_player_details") },
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onMatchesClick = { navController.navigate("player_matches") },
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = { navController.navigate("player_profile") }
            )
        }

        composable("player_team_player_details") {
            PlayerTeamPlayerDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onMatchesClick = { navController.navigate("player_matches") },
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = { navController.navigate("player_profile") }
            )
        }

        composable("player_invite_player") {
            com.example.trabalhocm.ui.screens.player.PlayerInvitePlayerScreen(
                onBackClick = { navController.popBackStack() },
                onSendInviteClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onMatchesClick = { navController.navigate("player_matches") },
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = { navController.navigate("player_profile") }
            )
        }

        composable("player_manage_team") {
            PlayerManageTeamScreen(
                onBackClick = { navController.popBackStack() },
                onInvitePlayerClick = { navController.navigate("player_invite_player") },
                onViewPlayerProfileClick = { navController.navigate("player_team_player_details") },
                onMakeCaptainClick = {},
                onRemoveFromTeamClick = {},
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onMatchesClick = { navController.navigate("player_matches") },
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = { navController.navigate("player_profile") }
            )
        }

        composable("player_create_team") {
            PlayerCreateTeamScreen(
                onBackClick = { navController.popBackStack() },
                onCreateTeamClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onMatchesClick = { navController.navigate("player_matches") },
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = { navController.navigate("player_profile") }
            )
        }

        // ==========================================
        // 3. ÁREA DO ORGANIZADOR
        // ==========================================
        composable("home") {
            HomeScreen(
                onVerTorneios = { navController.navigate("torneios") },
                onCreateTournamentClick = { navController.navigate("create_tournament") },
                onHomeClick = { },
                onTournamentsClick = { navController.navigate("torneios") },
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = { navController.navigate("teams") },
                onProfileClick = { navController.navigate("organizador_profile") }
            )
        }

        composable("torneios") {
            com.example.trabalhocm.ui.screens.organizador.OrganizerTournamentsScreen(
                onHistoryClick = { navController.navigate("organizador_tournament_history") },
                onFiltersClick = { navController.navigate("organizador_tournament_filters") },
                onCreateNewClick = { navController.navigate("create_tournament") },
                onDetailsClick = { navController.navigate("organizador_tournament_details") },
                onInviteTeamsClick = { navController.navigate("invite_teams") },
                onManageRegistrationClick = { navController.navigate("manage_registration") },
                onEditClick = { navController.navigate("edit_tournament") },
                onHomeClick = { navController.navigate("home") },
                onTournamentsClick = { },
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = { navController.navigate("teams") },
                onProfileClick = { navController.navigate("organizador_profile") }
            )
        }

        composable("invite_teams") {
            com.example.trabalhocm.ui.screens.organizador.InviteTeamsScreen(
                onBackClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("home") },
                onTournamentsClick = { navController.navigate("torneios") },
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = { navController.navigate("teams") },
                onProfileClick = { navController.navigate("organizador_profile") }
            )
        }

        composable("manage_registration") {
            com.example.trabalhocm.ui.screens.organizador.ManageRegistrationScreen(
                onBackClick = { navController.popBackStack() },
                onInviteTeamClick = { navController.navigate("invite_teams") },
                onHomeClick = { navController.navigate("home") },
                onTournamentsClick = { navController.navigate("torneios") },
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = { navController.navigate("teams") },
                onProfileClick = { navController.navigate("organizador_profile") }
            )
        }

        composable("organizador_tournament_details") {
            com.example.trabalhocm.ui.screens.organizador.OrganizerTournamentDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onEditClick = { navController.navigate("edit_tournament") },
                onHomeClick = { navController.navigate("home") },
                onTournamentsClick = { navController.navigate("torneios") },
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = { navController.navigate("teams") },
                onProfileClick = { navController.navigate("organizador_profile") }
            )
        }

        composable("organizador_profile") {
            val context = androidx.compose.ui.platform.LocalContext.current
            val scope = rememberCoroutineScope()
            val authRepository = remember { com.example.trabalhocm.data.repository.AuthRepository() }

            var nomeUtilizador by remember { mutableStateOf("") }
            var emailUtilizador by remember { mutableStateOf("") }
            var bioUtilizador by remember { mutableStateOf("") }
            var anoMembro by remember { mutableStateOf("2026") }
            var userId by remember { mutableStateOf("") }

            val sharedPrefs = remember { context.getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE) }

            LaunchedEffect(Unit) {
                authRepository.obterUtilizadorAtual().onSuccess { utilizador ->
                    nomeUtilizador = utilizador.nome
                    emailUtilizador = utilizador.email
                    userId = utilizador.id

                    val dataCriacao = utilizador.dataCriacao
                    if (!dataCriacao.isNullOrEmpty() && dataCriacao.length >= 4) {
                        anoMembro = dataCriacao.substring(0, 4)
                    }

                    val savedBio = sharedPrefs.getString("bio_$userId", null)
                    if (savedBio != null) bioUtilizador = savedBio
                }
            }

            OrganizerProfileScreen(
                initialName = nomeUtilizador,
                initialEmail = emailUtilizador,
                initialBio = bioUtilizador,
                memberSinceYear = anoMembro,
                onLogoutClick = {
                    scope.launch {
                        authRepository.logout()
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onSaveChanges = { novoNome, novoEmail, novaBio ->
                    scope.launch {
                        sharedPrefs.edit {
                            putString("bio_$userId", novaBio)
                        }

                        authRepository.atualizarPerfil(novoNome, novaBio)

                        nomeUtilizador = novoNome
                        bioUtilizador = novaBio
                    }
                },
                onPlayerDashboardClick = { navController.navigate("player_stats") },
                onHomeClick = { navController.navigate("home") },
                onTournamentsClick = { navController.navigate("torneios") },
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = { navController.navigate("teams") },
                onProfileClick = {  }
            )
        }


        composable("create_tournament") {
            CreateTournamentScreen(
                viewModel = createTournamentViewModel,
                onBackClick = { navController.popBackStack() },
                onProceedClick = { navController.navigate("create_tournament_step_2") },
                onHomeClick = {
                    navController.navigate("home") {
                        popUpTo("home") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("create_tournament_step_2") {
            CreateTournamentStep2Screen(
                viewModel = createTournamentViewModel,
                onBackClick = { navController.popBackStack() },
                onProceedClick = { navController.navigate("create_tournament_step_3") },
                onHomeClick = {
                    navController.navigate("home") {
                        popUpTo("home") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("create_tournament_step_3") {
            CreateTournamentStep3Screen(
                viewModel = createTournamentViewModel,
                onBackClick = { navController.popBackStack() },
                onProceedClick = { navController.navigate("create_tournament_step_4") },
                onHomeClick = {
                    navController.navigate("home") {
                        popUpTo("home") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("create_tournament_step_4") {
            CreateTournamentStep4Screen(
                viewModel = createTournamentViewModel,
                onBackClick = { navController.popBackStack() },
                onPublishClick = {
                    navController.navigate("home") {
                        popUpTo("home") {
                            inclusive = true
                        }
                    }
                },
                onHomeClick = {
                    navController.navigate("home") {
                        popUpTo("home") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("organizador_match_center") {
            com.example.trabalhocm.ui.screens.organizador.OrganizerMatchCenterScreen(
                onLiveMatchClick = { navController.navigate("organizador_live_match") },
                onHistoryClick = { navController.navigate("organizador_matches") },
                onHomeClick = { navController.navigate("home") },
                onTournamentsClick = { navController.navigate("torneios") },
                onMatchesClick = { },
                onTeamsClick = { navController.navigate("teams") },
                onProfileClick = { navController.navigate("organizador_profile") },
                onCreateCasualMatchClick = { navController.navigate("organizador_create_casual") }
            )
        }

        composable("organizador_live_match") {
            com.example.trabalhocm.ui.screens.organizador.OrganizerLiveMatchScreen(
                onBackClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("home") },
                onTournamentsClick = { navController.navigate("torneios") },
                onMatchesClick = { navController.navigate("organizador_matches") },
                onTeamsClick = { navController.navigate("teams") },
                onProfileClick = { navController.navigate("organizador_profile") }
            )
        }

        composable("organizador_matches") {
            val matchesViewModel: OrganizerMatchesViewModel = viewModel()

            OrganizerMatchesScreen(
                viewModel = matchesViewModel, // <-- Passamos a mochila
                onBackClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                onTournamentsClick = { navController.navigate("torneios") },
                onMatchesClick = { },
                onTeamsClick = { navController.navigate("teams") },
                onProfileClick = { navController.navigate("organizador_profile") }
            )
        }

        composable("organizador_casual_details") {
            com.example.trabalhocm.ui.screens.organizador.CasualMatchDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("home") },
                onTournamentsClick = { navController.navigate("torneios") },
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = { navController.navigate("teams") },
                onProfileClick = { navController.navigate("organizador_profile") }
            )
        }

        composable("organizador_create_casual") {
            com.example.trabalhocm.ui.screens.organizador.OrganizerCreateCasualMatchScreen(
                onBackClick = { navController.popBackStack() },
                onPublishClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("home") },
                onTournamentsClick = { navController.navigate("torneios") },
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = { navController.navigate("teams") },
                onProfileClick = { navController.navigate("organizador_profile") }
            )
        }

        composable("teams") {
            BrowseTeamsScreen(
                onCreateTeamClick = { navController.navigate("create_team") },
                onManageTeamClick = { navController.navigate("manage_team") },
                onViewDetailsClick = { isUserTeam -> navController.navigate("organizador_team_details/$isUserTeam") },
                onHomeClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                onTournamentsClick = { navController.navigate("torneios") },
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = {  },
                onProfileClick = { navController.navigate("organizador_profile") }
            )
        }

        composable("manage_team") {
            com.example.trabalhocm.ui.screens.TeamManagementScreen(
                onBackClick = { navController.popBackStack() },
                onInviteClick = { navController.navigate("organizador_invite_player") }
            )
        }

        composable(
            route = "organizador_team_details/{isUserTeam}",
            arguments = listOf(navArgument("isUserTeam") { type = NavType.BoolType })
        ) { backStackEntry ->
            val isUserTeam = backStackEntry.arguments?.getBoolean("isUserTeam") ?: false

            TeamDetailsScreen(
                isUserTeam = isUserTeam,
                onBackClick = { navController.popBackStack() },
                onInvitePlayerClick = { navController.navigate("organizador_invite_player") },
                onViewPlayerProfileClick = { },
                onHomeClick = { navController.navigate("home") },
                onTournamentsClick = { navController.navigate("torneios") },
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = { navController.navigate("teams") },
                onProfileClick = {}
            )
        }

        composable("create_team") {
            CreateTeamScreen(
                onBackClick = { navController.popBackStack() },
                onCreateClick = { navController.popBackStack() },
                onHomeClick = {
                    navController.navigate("home") {
                        popUpTo("home") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("organizador_invite_player") {
            com.example.trabalhocm.ui.screens.organizador.InvitePlayerScreen(
                onBackClick = { navController.popBackStack() },
                onSendInviteClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("home") },
                onTournamentsClick = { navController.navigate("torneios") },
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = { navController.navigate("teams") },
                onProfileClick = {}
            )
        }

        composable("organizador_tournament_history") {
            com.example.trabalhocm.ui.screens.organizador.OrganizerTournamentHistoryScreen(
                onBackClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("home") },
                onTournamentsClick = { navController.navigate("torneios") },
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = { navController.navigate("teams") },
                onProfileClick = { navController.navigate("organizador_profile") }
            )
        }

        composable("edit_tournament") {
            com.example.trabalhocm.ui.screens.organizador.EditTournamentScreen(
                onBackClick = { navController.popBackStack() },
                onCancelClick = { navController.popBackStack() },
                onSaveClick = { navController.popBackStack() },
                onDeleteClick = {
                    navController.navigate("home") { popUpTo(0) }
                }
            )
        }

        composable("organizador_tournament_filters") {
            com.example.trabalhocm.ui.screens.organizador.OrganizerTournamentFiltersScreen(
                onCloseClick = { navController.popBackStack() },
                onApplyClick = { navController.popBackStack() }
            )
        }

        // ==========================================
        // 4. ÁREA DO ADMIN
        // ==========================================
        composable("admin_home") {
            AdminHomeScreen(
                onManageUsersClick = {
                    navController.navigate("admin_users")
                },
                onManageTeamsClick = { navController.navigate("admin_teams") },
                onManageTournamentsClick = { navController.navigate("admin_tournaments") },
                onReviewRequestsClick = { navController.navigate("admin_organizer_requests") },
                onHomeClick = {},
                onTournamentsClick = { navController.navigate("admin_tournaments") },
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = { navController.navigate("admin_teams") },
                onProfileClick = { navController.navigate("admin_profile") }
            )
        }

        composable("admin_profile") {
            AdminProfileScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutSuccess = {
                    navController.navigate("login") {
                        popUpTo(0) {
                            inclusive = true
                        }
                    }
                },
                onChangePasswordClick = { navController.navigate("change_password") },
                onDashboardClick = {
                    navController.navigate("admin_home") {
                        popUpTo("admin_home") {
                            inclusive = false
                        }
                    }
                },
                onHomeClick = {
                    navController.navigate("admin_home") {
                        popUpTo("admin_home") {
                            inclusive = false
                        }
                    }
                },
                onTournamentsClick = { navController.navigate("torneios") },
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = {},
                onProfileClick = {}
            )
        }

        composable("admin_users") {
            AdminUserManagementScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onHomeClick = {
                    navController.navigate("admin_home")
                },
                onNotificationsClick = {
                    navController.navigate("admin_notifications")
                },
                onTournamentsClick = {
                    navController.navigate("torneios")
                },
                onMatchesClick = {},
                onTeamsClick = {},
                onProfileClick = {
                    navController.navigate("admin_profile")
                }
            )
        }

        composable("admin_notifications") {
            AdminNotificationsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onHomeClick = {
                    navController.navigate("admin_home")
                },
                onTournamentsClick = {
                    navController.navigate("torneios")
                },
                onMatchesClick = {},
                onTeamsClick = {},
                onProfileClick = {
                    navController.navigate("admin_profile")
                }
            )
        }

        composable("admin_organizer_requests") {
            AdminOrganizerRequestsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onHomeClick = {
                    navController.navigate("admin_home")
                },
                onNotificationsClick = {
                    navController.navigate("admin_notifications")
                },
                onTournamentsClick = {
                    navController.navigate("admin_tournaments")
                },
                onMatchesClick = {
                    navController.navigate("organizador_match_center")
                },
                onTeamsClick = {
                    navController.navigate("teams")
                },
                onProfileClick = {
                    navController.navigate("admin_profile")
                }
            )
        }

        composable("admin_tournaments") {
            AdminTournamentsScreen(
                onBackClick = { navController.popBackStack() },
                onNotificationsClick = { navController.navigate("admin_notifications") },
                onArchiveClick = { navController.navigate("admin_tournament_archive") },
                onTournamentDetailsClick = { tournamentId ->
                    navController.navigate("admin_tournament_details/$tournamentId")
                },
                onManageRegistrationClick = {},
                onHomeClick = { navController.navigate("admin_home") },
                onTournamentsClick = {},
                onMatchesClick = {},
                onTeamsClick = {},
                onProfileClick = { navController.navigate("admin_profile") }
            )
        }

        composable("admin_tournament_archive") {
            AdminTournamentArchiveScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onNotificationsClick = {
                    navController.navigate("admin_notifications")
                },
                onHomeClick = {
                    navController.navigate("admin_home")
                },
                onTournamentsClick = {
                    navController.navigate("admin_tournaments")
                },
                onMatchesClick = {},
                onTeamsClick = {},
                onProfileClick = {
                    navController.navigate("admin_profile")
                }
            )
        }

        composable(
            route = "admin_tournament_details/{tournamentId}",
            arguments = listOf(
                navArgument("tournamentId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val tournamentId = backStackEntry.arguments?.getString("tournamentId") ?: ""

            AdminTournamentDetailsScreen(
                tournamentId = tournamentId,
                onBackClick = {
                    navController.popBackStack()
                },
                onNotificationsClick = {
                    navController.navigate("admin_notifications")
                },
                onManageRegistrationClick = { id ->
                },
                onEditTournamentClick = { id ->
                    navController.navigate("admin_tournament_edit/$id")
                },
                onDeleteTournamentSuccess = {
                    navController.navigate("admin_tournaments") {
                        popUpTo("admin_tournaments") {
                            inclusive = false
                        }
                        launchSingleTop = true
                    }
                },
                onHomeClick = {
                    navController.navigate("admin_home")
                },
                onTournamentsClick = {
                    navController.navigate("admin_tournaments")
                },
                onMatchesClick = {},
                onTeamsClick = {},
                onProfileClick = {
                    navController.navigate("admin_profile")
                }
            )
        }

        composable(
            route = "admin_tournament_edit/{tournamentId}",
            arguments = listOf(
                navArgument("tournamentId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val tournamentId = backStackEntry.arguments?.getString("tournamentId") ?: ""

            AdminTournamentEditScreen(
                tournamentId = tournamentId,
                onBackClick = {
                    navController.popBackStack()
                },
                onNotificationsClick = {
                    navController.navigate("admin_notifications")
                },
                onSaveSuccess = {
                    navController.navigate("admin_tournament_details/$tournamentId") {
                        popUpTo("admin_tournament_details/{tournamentId}") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onCancelSuccess = {
                    navController.navigate("admin_tournaments") {
                        popUpTo("admin_tournaments") {
                            inclusive = false
                        }
                    }
                },
                onDiscardChangesClick = {
                    navController.popBackStack()
                },
                onHomeClick = {
                    navController.navigate("admin_home")
                },
                onTournamentsClick = {
                    navController.navigate("admin_tournaments")
                },
                onMatchesClick = {},
                onTeamsClick = {},
                onProfileClick = {
                    navController.navigate("admin_profile")
                }
            )
        }

        composable("admin_teams") {
            AdminTeamsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onNotificationsClick = {
                    navController.navigate("admin_notifications")
                },
                onViewDetailsClick = { teamId ->
                },
                onManageTeamClick = { teamId ->
                },
                onHomeClick = {
                    navController.navigate("admin_home")
                },
                onTournamentsClick = {
                    navController.navigate("admin_tournaments")
                },
                onMatchesClick = {},
                onTeamsClick = {},
                onProfileClick = {
                    navController.navigate("admin_profile")
                }
            )
        }

        // ==========================================
        // 5. TELAS COMUNS / UTILIDADES
        // ==========================================
        composable("offline") {
            OfflineScreen(
                onRetrySync = { navController.popBackStack() },
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = { navController.navigate("player_profile") }
            )
        }
    }
}