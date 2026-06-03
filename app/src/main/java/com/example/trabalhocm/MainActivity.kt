package com.example.trabalhocm

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.trabalhocm.ui.screens.player.PlayerCreateTeamScreen
import com.example.trabalhocm.ui.screens.player.PlayerHomeScreen
import com.example.trabalhocm.ui.screens.player.PlayerManageTeamScreen
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
import com.example.trabalhocm.ui.screens.onboarding.SplashScreen
import com.example.trabalhocm.ui.screens.TorneiosScreen
import com.example.trabalhocm.ui.screens.auth.UserTypeScreen
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

import kotlinx.coroutines.launch

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

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // ==========================================
        // 1. INICIALIZAÇÃO & AUTENTICAÇÃO
        // ==========================================
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

        composable("recover_password") { RecoverPasswordScreen() }

        composable("change_password") { ChangePasswordScreen(onPasswordChanged = { navController.popBackStack() }) }

        composable("user_type") {
            UserTypeScreen(
                onAdminClick = { navController.navigate("admin_home") },
                onOrganizerClick = { navController.navigate("home") },
                onPlayerClick = { navController.navigate("player_home") }
            )
        }

        // ==========================================
        // 2. ÁREA DO JOGADOR (Player)
        // ==========================================
        composable("player_home") {
            PlayerHomeScreen(
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onCasualMatchesClick = {},
                onLiveMatchesClick = {},
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = { navController.navigate("player_profile") }
            )
        }

        composable("player_profile") {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val authRepository = remember { com.example.trabalhocm.data.repository.AuthRepository() }

            var nomeUtilizador by remember { mutableStateOf("A carregar...") }
            var emailUtilizador by remember { mutableStateOf("A carregar...") }
            var bioUtilizador by remember { mutableStateOf("") }
            var photoUri by remember { mutableStateOf<Uri?>(null) }
            var userId by remember { mutableStateOf("") }

            val sharedPrefs = remember { context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE) }

            LaunchedEffect(Unit) {
                authRepository.obterUtilizadorAtual().onSuccess { utilizador ->
                    nomeUtilizador = utilizador.nome
                    emailUtilizador = utilizador.email
                    userId = utilizador.id

                    if (!utilizador.fotoUrl.isNullOrEmpty()) {
                        photoUri = utilizador.fotoUrl.toUri()
                    }

                    val savedUriStr = sharedPrefs.getString("avatar_$userId", null)
                    if (savedUriStr != null) photoUri = savedUriStr.toUri()

                    val savedBio = sharedPrefs.getString("bio_$userId", null)
                    if (savedBio != null) bioUtilizador = savedBio
                }.onFailure {
                    nomeUtilizador = "Erro ao carregar"
                    emailUtilizador = "Erro ao carregar"
                }
            }

            PlayerProfileScreen(
                initialName = nomeUtilizador,
                initialEmail = emailUtilizador,
                initialBio = bioUtilizador,
                initialPhotoUri = photoUri,
                onLogoutClick = {
                    scope.launch {
                        authRepository.logout()
                        navController.navigate("login") { popUpTo(0) { inclusive = true } }
                    }
                },
                onSaveChanges = { novaBio, novaPhotoUri ->
                    scope.launch {
                        sharedPrefs.edit {
                            if (novaPhotoUri != null) {
                                putString("avatar_$userId", novaPhotoUri.toString())
                            } else {
                                remove("avatar_$userId")
                            }
                            putString("bio_$userId", novaBio)
                        }
                    }
                },
                onDashboardClick = { navController.navigate("player_stats") },
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = {}
            )
        }

        composable("player_stats") {
            val authRepository = remember { com.example.trabalhocm.data.repository.AuthRepository() }
            var nomeUtilizador by remember { mutableStateOf("A carregar...") }

            LaunchedEffect(Unit) {
                authRepository.obterUtilizadorAtual().onSuccess { utilizador ->
                    nomeUtilizador = utilizador.nome
                }
            }

            PlayerStatsScreen(
                playerName = nomeUtilizador,
                footballGoals = 24,
                footballAssists = 18,
                basketballPoints = 412,
                basketballWinRate = 65,
                volleyballSpikes = 84,
                volleyballWinRate = 82,
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("player_profile_settings") { navController.popBackStack() }

        composable("player_notifications") {
            PlayerNotificationsScreen(
                onBackClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = { navController.navigate("player_profile") }
            )
        }

        composable("player_tournaments") {
            PlayerTournamentManagementScreen(
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = {},
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = { navController.navigate("player_profile") },
                onDetailsClick = { navController.navigate("player_tournament_details") },
                onRegisterClick = { navController.navigate("player_tournament_registration") },
                onAskOrganizerClick = { navController.navigate("player_become_organizer") },
                onHistoryClick = { navController.navigate("player_tournament_history") },
                onFiltersClick = { navController.navigate("player_tournament_filters") }
            )
        }

        composable("player_tournament_history") {
            PlayerTournamentHistoryScreen(
                onBackClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = { navController.navigate("player_profile") }
            )
        }

        composable("player_tournament_details") {
            PlayerTournamentDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onMatchesClick = { navController.navigate("organizador_match_center") },
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
                onMatchesClick = { navController.navigate("organizador_match_center") },
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
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = { navController.navigate("player_profile") }
            )
        }

        composable("player_teams") {
            PlayerTeamsScreen(
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onMatchesClick = { navController.navigate("organizador_match_center") },
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
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = { navController.navigate("player_teams") },
                onProfileClick = { navController.navigate("player_profile") }
            )
        }

        composable("player_team_player_details") {
            PlayerTeamPlayerDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("player_home") },
                onTournamentsClick = { navController.navigate("player_tournaments") },
                onMatchesClick = { navController.navigate("organizador_match_center") },
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
                onMatchesClick = { navController.navigate("organizador_match_center") },
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
                onMatchesClick = { navController.navigate("organizador_match_center") },
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
                onMatchesClick = { navController.navigate("organizador_match_center") },
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
                onCreateTournamentClick = { navController.navigate("create_tournament") }
            )
        }

        composable("organizador_profile") {
            OrganizerProfileScreen(
                onLogoutClick = { navController.navigate("login") { popUpTo(0) { inclusive = true } } },
                onPlayerDashboardClick = { navController.navigate("player_stats") },
                onHomeClick = { navController.navigate("home") },
                onTournamentsClick = { navController.navigate("torneios") },
                onTeamsClick = { navController.navigate("teams") }
            )
        }

        composable("create_tournament") {
            CreateTournamentScreen(
                onBackClick = { navController.popBackStack() },
                onProceedClick = { navController.navigate("create_tournament_step_2") },
                onHomeClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } }
            )
        }

        composable("create_tournament_step_2") {
            CreateTournamentStep2Screen(
                onBackClick = { navController.popBackStack() },
                onProceedClick = { navController.navigate("create_tournament_step_3") },
                onHomeClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } }
            )
        }

        composable("create_tournament_step_3") {
            CreateTournamentStep3Screen(
                onBackClick = { navController.popBackStack() },
                onProceedClick = { navController.navigate("create_tournament_step_4") },
                onHomeClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } }
            )
        }

        composable("create_tournament_step_4") {
            CreateTournamentStep4Screen(
                onBackClick = { navController.popBackStack() },
                onPublishClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } },
                onHomeClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } }
            )
        }

        composable("organizador_match_center") {
            com.example.trabalhocm.ui.screens.organizador.OrganizerMatchCenterScreen(
                onLiveMatchClick = { navController.navigate("organizador_live_match") },
                onHistoryClick = { navController.navigate("organizador_matches") },
                onHomeClick = { navController.navigate("home") },
                onTournamentsClick = { navController.navigate("torneios") },
                onMatchesClick = {  },
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
            OrganizerMatchesScreen(
                onBackClick = { navController.popBackStack() },
                onHomeClick = { navController.navigate("home") },
                onTournamentsClick = { navController.navigate("torneios") },
                onMatchesClick = {  },
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
                onHomeClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } }
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
                onHomeClick = { navController.navigate("home") { popUpTo("home") { inclusive = true } } }
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

        composable("admin_home") {
            AdminHomeScreen(
                onManageUsersClick = {
                    navController.navigate("admin_users")
                },
                onManageTeamsClick = {},
                onManageTournamentsClick = { navController.navigate("torneios") },
                onReviewRequestsClick = {},
                onHomeClick = {},
                onTournamentsClick = { navController.navigate("torneios") },
                onMatchesClick = { navController.navigate("organizador_match_center") },
                onTeamsClick = {},
                onProfileClick = { navController.navigate("admin_profile") }
            )
        }

        composable("admin_profile") {
            AdminProfileScreen(
                onBackClick = { navController.popBackStack() },
                onLogoutSuccess = { navController.navigate("login") { popUpTo(0) { inclusive = true } } },
                onChangePasswordClick = { navController.navigate("change_password") },
                onDashboardClick = { navController.navigate("admin_home") { popUpTo("admin_home") { inclusive = false } } },
                onHomeClick = { navController.navigate("admin_home") { popUpTo("admin_home") { inclusive = false } } },
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

        composable("torneios") {
            TorneiosScreen()
        }
    }
}