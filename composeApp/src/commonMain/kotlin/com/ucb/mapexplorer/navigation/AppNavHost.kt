package com.ucb.mapexplorer.navigation

import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ucb.mapexplorer.auth.presentation.login.screen.LoginScreen
import com.ucb.mapexplorer.auth.presentation.register.screen.RegisterScreen
import com.ucb.mapexplorer.dollar.presentation.screen.DollarScreen

@Composable
fun AppNavHost() {

    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }//agregar para el manejo de errores

    Scaffold(//manejo de errores
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        NavHost(navController = navController, startDestination = NavRoute.Login) {
            composable<NavRoute.Dollar> {
                DollarScreen()
            }

            composable<NavRoute.Login> {
                LoginScreen(
                    navController = navController
                )
            }
            composable<NavRoute.Register> {
                RegisterScreen(
                    navController = navController,
                    snackbarHostState = snackbarHostState
                )
            }

        }
    }
}
