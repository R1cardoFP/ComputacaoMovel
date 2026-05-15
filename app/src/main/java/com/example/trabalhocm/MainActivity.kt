package pt.ipvc.matchpoint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.trabalhocm.ui.theme.TrabalhoCMTheme
import com.example.trabalhocm.ui.screens.OnboardingFlow
import com.example.trabalhocm.ui.screens.SplashScreen
import com.example.trabalhocm.ui.screens.LoginScreen

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

    // NavHost é o contentor que gere os ecrãs
    NavHost(navController = navController, startDestination = "splash") {

        // Ecrã 1: Splash
        composable("splash") {
            SplashScreen(onEnd = {
                // Navega para o onboarding e apaga o splash do histórico (para o botão "Voltar" não vir para o Splash)
                navController.navigate("onboarding") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        // Ecrã 2: Onboarding (gere as 3 páginas internamente)
        composable("onboarding") {
            OnboardingFlow(onFinish = {
                navController.navigate("login") {
                    popUpTo("onboarding") { inclusive = true }
                }
            })
        }

        // Ecrã 3: Login
        composable("login") {
            LoginScreen()
        }
    }
}