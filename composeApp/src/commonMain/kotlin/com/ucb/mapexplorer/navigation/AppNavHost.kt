package com.ucb.mapexplorer.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ucb.mapexplorer.auth.presentation.login.screen.LoginScreen
import com.ucb.mapexplorer.auth.presentation.register.screen.RegisterScreen
import com.ucb.mapexplorer.explanation.explanation1.presentation.screen.Explanation1Screen
import com.ucb.mapexplorer.explanation.explanation2.presentation.screen.Explanation2Screen
import com.ucb.mapexplorer.explanation.explanation3.presentation.screen.Explanation3Screen
import com.ucb.mapexplorer.explanation.explanation4.presentation.screen.Explanation4Screen
import com.ucb.mapexplorer.map.presentation.screen.MapScreen
import com.ucb.mapexplorer.nearbyplaces.presentation.screen.NearbyPlacesScreen
import com.ucb.mapexplorer.nearbyplaces.presentation.screen.PlaceDetailScreen

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    // Quitamos el paddingValues del NavHost para que cada pantalla maneje sus propios insets (Edge-to-Edge)
    // Esto permite que el ExploreTopBar rojo llegue hasta el borde superior de la pantalla.
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { _ -> 
        NavHost(
            navController = navController,
            startDestination = NavRoute.Login,
            modifier = Modifier.fillMaxSize()
        ) {
            composable<NavRoute.Login> {
                LoginScreen(navController = navController)
            }
            composable<NavRoute.Register> {
                RegisterScreen(
                    navController = navController,
                    snackbarHostState = snackbarHostState
                )
            }
            composable<NavRoute.Explanation1> {
                Explanation1Screen(navController = navController)
            }
            composable<NavRoute.Explanation2> {
                Explanation2Screen(navController = navController)
            }
            composable<NavRoute.Explanation3> {
                Explanation3Screen(navController = navController)
            }
            composable<NavRoute.Explanation4> {
                Explanation4Screen(navController = navController)
            }
            composable<NavRoute.Map> {
                MapScreen(
                    onNavigateToNearbyPlaces = { lat, lon ->
                        navController.navigate(NavRoute.NearbyPlaces(lat, lon))
                    }
                )
            }
            composable<NavRoute.NearbyPlaces> { backStackEntry ->
                val route = backStackEntry.toRoute<NavRoute.NearbyPlaces>()
                NearbyPlacesScreen(
                    userLat = route.lat,
                    userLon = route.lon,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onPlaceSelected = { placeId ->
                        navController.navigate(NavRoute.PlaceDetail(placeId))
                    }
                )
            }
            composable<NavRoute.PlaceDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<NavRoute.PlaceDetail>()
                PlaceDetailScreen(
                    placeId = route.placeId,
                    onNavigateBack = {
                        navController.popBackStack()
                    },
                    onNavigateToMap = { _, _ ->
                        navController.navigate(NavRoute.Map) {
                            popUpTo(NavRoute.Map) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
