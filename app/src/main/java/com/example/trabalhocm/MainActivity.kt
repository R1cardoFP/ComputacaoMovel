package com.example.trabalhocm

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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trabalhocm.ui.screens.ChangePasswordScreen
import com.example.trabalhocm.ui.screens.HomeScreen
import com.example.trabalhocm.ui.screens.LoginScreen
import com.example.trabalhocm.ui.screens.OfflineScreen
import com.example.trabalhocm.ui.screens.OnboardingFlow
import com.example.trabalhocm.ui.screens.PlayerBecomeOrganizerScreen
import com.example.trabalhocm.ui.screens.PlayerHomeScreen
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
    override fun onCreate(savedInstanceState: Bundle?): Unit {
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
            RecoverPasswordScreen(
                onGoToLogin = {
                    navController.popBackStack()
                }
            )
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
                    navController.navigate("home")
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
                onProfileClick = {
                    navController.navigate("profile")
                }
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
                onProfileClick = {
                    navController.navigate("profile")
                }
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
                onProfileClick = {
                    navController.navigate("profile")
                },
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
                onProfileClick = {
                    navController.navigate("profile")
                }
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
                onProfileClick = {
                    navController.navigate("profile")
                }
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
                onProfileClick = {
                    navController.navigate("profile")
                }
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
                onProfileClick = {
                    navController.navigate("profile")
                }
            )
        }

        composable("home") {
            HomeScreen(
                onVerTorneios = {
                    navController.navigate("torneios")
                }
            )
        }

        composable("torneios") {
            TorneiosScreen()
        }

        composable("profile") {
            val authRepository = remember { com.example.trabalhocm.data.repository.AuthRepository() }
            val scope = rememberCoroutineScope()

            // Variáveis de estado que guardam os dados do utilizador
            var nomeUtilizador by remember { mutableStateOf("A carregar...") }
            var emailUtilizador by remember { mutableStateOf("A carregar...") }

            // O LaunchedEffect corre automaticamente quando o ecrã abre para ir buscar os dados reais
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
                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                },
                onSaveChanges = { novoNome, novoEmail, bio ->
                    // Lógica futura para guardar as alterações na base de dados
                },
                onHomeClick = {
                    navController.navigate("player_home")
                },
                onTournamentsClick = {
                    navController.navigate("player_tournaments")
                }
            )
        }
    }
}