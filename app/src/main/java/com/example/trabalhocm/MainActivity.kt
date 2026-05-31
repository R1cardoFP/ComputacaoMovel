package com.example.trabalhocm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trabalhocm.ui.screens.auth.ChangePasswordScreen
import com.example.trabalhocm.ui.screens.organizador.HomeScreen
import com.example.trabalhocm.ui.screens.organizador.CreateTournamentScreen
import com.example.trabalhocm.ui.screens.auth.LoginScreen
import com.example.trabalhocm.ui.screens.OfflineScreen
import com.example.trabalhocm.ui.screens.onboarding.OnboardingFlow
import com.example.trabalhocm.ui.screens.player.PlayerBecomeOrganizerScreen
import com.example.trabalhocm.ui.screens.player.PlayerHomeScreen
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val windowInsetsController = WindowInsetsControllerCompat(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

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

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onEnd = {
                    navController.navigate("onboarding") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("onboarding") {
            OnboardingFlow(
                onFinish = {
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("user_type") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onCreateAccount = {
                    navController.navigate("register")
                },
                onForgotPassword = {
                    navController.navigate("recover_password")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("user_type") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onGoToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable("recover_password") {
            RecoverPasswordScreen()
        }

        composable("change_password") {
            ChangePasswordScreen(
                onPasswordChanged = {
                    navController.popBackStack()
                }
            )
        }

        composable("user_type") {
            UserTypeScreen(
                onAdminClick = {
                    navController.navigate("admin_home")
                },
                onOrganizerClick = {
                    navController.navigate("home")
                },
                onPlayerClick = {
                    navController.navigate("player_home")
                }
            )
        }

        composable("offline") {
            OfflineScreen(
                onRetrySync = {
                    navController.popBackStack()
                },
                onHomeClick = {
                    navController.navigate("home")
                },
                onTournamentsClick = {
                    navController.navigate("torneios")
                },
                onMatchesClick = {},
                onTeamsClick = {},
                onProfileClick = {}
            )
        }

        composable("player_home") {
            PlayerHomeScreen(
                onTournamentsClick = {
                    navController.navigate("player_tournaments")
                },
                onCasualMatchesClick = {},
                onLiveMatchesClick = {},
                onTeamsClick = {},
                onProfileClick = {}
            )
        }

        composable("player_tournaments") {
            PlayerTournamentManagementScreen(
                onHomeClick = {
                    navController.navigate("player_home")
                },
                onTournamentsClick = {},
                onMatchesClick = {},
                onTeamsClick = {},
                onProfileClick = {},
                onDetailsClick = {
                    navController.navigate("player_tournament_details")
                },
                onRegisterClick = {
                    navController.navigate("player_tournament_registration")
                },
                onAskOrganizerClick = {
                    navController.navigate("player_become_organizer")
                },
                onHistoryClick = {
                    navController.navigate("player_tournament_history")
                },
                onFiltersClick = {
                    navController.navigate("player_tournament_filters")
                }
            )
        }

        composable("player_tournament_history") {
            PlayerTournamentHistoryScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onHomeClick = {
                    navController.navigate("player_home")
                },
                onTournamentsClick = {
                    navController.navigate("player_tournaments")
                },
                onMatchesClick = {},
                onTeamsClick = {},
                onProfileClick = {}
            )
        }

        composable("player_tournament_details") {
            PlayerTournamentDetailsScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onHomeClick = {
                    navController.navigate("player_home")
                },
                onTournamentsClick = {
                    navController.navigate("player_tournaments")
                },
                onMatchesClick = {},
                onTeamsClick = {},
                onProfileClick = {}
            )
        }

        composable("player_tournament_filters") {
            PlayerTournamentFiltersScreen(
                onCloseClick = {
                    navController.popBackStack()
                },
                onApplyClick = {
                    navController.popBackStack()
                }
            )
        }

        composable("player_tournament_registration") {
            PlayerTournamentRegistrationScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSubmitClick = {
                    navController.popBackStack()
                },
                onHomeClick = {
                    navController.navigate("player_home")
                },
                onTournamentsClick = {
                    navController.navigate("player_tournaments")
                },
                onMatchesClick = {},
                onTeamsClick = {},
                onProfileClick = {}
            )
        }

        composable("player_become_organizer") {
            PlayerBecomeOrganizerScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSubmitClick = {
                    navController.popBackStack()
                },
                onCancelClick = {
                    navController.popBackStack()
                },
                onHomeClick = {
                    navController.navigate("player_home")
                },
                onTournamentsClick = {
                    navController.navigate("player_tournaments")
                },
                onMatchesClick = {},
                onTeamsClick = {},
                onProfileClick = {}
            )
        }

        composable("home") {
            HomeScreen(
                onVerTorneios = {
                    navController.navigate("torneios")
                },
                onCreateTournamentClick = {
                    navController.navigate("create_tournament")
                },
                onHomeClick = {
                }
            )
        }

        composable("create_tournament") {
            CreateTournamentScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onProceedClick = {
                    // Aqui depois vai ser pra meter a rota para o schedule
                },
                onHomeClick = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("admin_home") {
            AdminHomeScreen(
                onManageUsersClick = {},
                onManageTeamsClick = {},
                onManageTournamentsClick = {
                    navController.navigate("torneios")
                },
                onReviewRequestsClick = {},
                onHomeClick = {},
                onTournamentsClick = {
                    navController.navigate("torneios")
                },
                onMatchesClick = {},
                onTeamsClick = {},
                onProfileClick = {}
            )
        }

        composable("torneios") {
            TorneiosScreen()
        }
    }
}