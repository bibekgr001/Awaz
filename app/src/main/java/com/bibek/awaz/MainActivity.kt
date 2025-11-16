package com.bibek.awaz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bibek.awaz.ui.authscreens.*
import com.bibek.awaz.viewmodel.AuthViewModel
import com.bibek.awaz.ui.theme.AwazTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AwazTheme {

                val navController = rememberNavController()
                val viewModel = remember { AuthViewModel() }

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {

                    composable("login") {
                        LoginScreen(
                            viewModel = viewModel,
                            onLoginSuccess = { navController.navigate("home") },
                            onCreateAccountClick = { navController.navigate("register") }
                        )
                    }

                    composable("register") {
                        RegisterScreen(
                            viewModel = viewModel,
                            onRegisterSuccess = { navController.navigate("home") }
                        )
                    }

                    composable("home") {
                        HomeScreen()
                    }
                }
            }
        }
    }
}
