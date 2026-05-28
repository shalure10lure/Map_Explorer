package com.ucb.mapexplorer.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ucb.mapexplorer.auth.presentation.login.screen.LoginScreen
import com.ucb.mapexplorer.auth.presentation.register.screen.RegisterScreen
import com.ucb.mapexplorer.explanation.explanation1.presentation.screen.Explanation1Screen
import com.ucb.mapexplorer.explanation.explanation2.presentation.screen.Explanation2Screen
import com.ucb.mapexplorer.explanation.explanation3.presentation.screen.Explanation3Screen
import com.ucb.mapexplorer.explanation.explanation4.presentation.screen.Explanation4Screen
import com.ucb.mapexplorer.map.presentation.screen.MainScreen
import com.ucb.mapexplorer.map.presentation.screen.MapScreen
import com.ucb.mapexplorer.nearbyplaces.presentation.screen.NearbyPlacesScreen
import com.ucb.mapexplorer.nearbyplaces.presentation.screen.PlaceDetailScreen
import com.ucb.mapexplorer.nearbyplaces.presentation.viewmodel.NearbyPlacesViewModel
import com.ucb.mapexplorer.onboarding.presentation.screen.OnboardingScreen
import com.ucb.mapexplorer.profile.editProfile.presentation.screen.EditProfileScreen
import com.ucb.mapexplorer.profile.editProfile.presentation.viewmodel.EditProfileViewModel
import com.ucb.mapexplorer.social.presentation.screen.SocialSpaceScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = NavRoute.Login,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<NavRoute.Login> {
                LoginScreen(navController = navController)
            }
            composable<NavRoute.Register> {
                RegisterScreen(
                    navController = navController,
                    snackbarHostState = snackbarHostState
                )
            }/*
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
            }*/
            composable<NavRoute.EditProfile> {
                val vm: EditProfileViewModel = koinViewModel()
                EditProfileScreen(
                    viewModel = vm,
                    onCancel = { navController.popBackStack() },
                    onSave   = { navController.popBackStack() }
                )
            }
            composable<NavRoute.Main> {
                MainScreen(navController = navController)
            }
            composable<NavRoute.Map> {
                MapScreen()
            }
            composable<NavRoute.Onboarding> {
                OnboardingScreen(navController = navController)
            }

            // 🌐 Espacio Social
            composable<NavRoute.SocialSpace> {
                SocialSpaceScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToMessages = { /* TODO */ },
                    onNavigateToNearby = { navController.navigate(NavRoute.NearbyPlaces) },
                    onNavigateToProfile = { navController.navigate(NavRoute.Profile) }
                )
            }

            // 📍 Pantalla de lista de lugares
            composable<NavRoute.NearbyPlaces> {
                val viewModel: NearbyPlacesViewModel = koinViewModel()
                NearbyPlacesScreen(
                    viewModel = viewModel,
                    onPlaceClick = { placeId ->
                        navController.navigate(NavRoute.PlaceDetail(placeId))
                    }
                )
            }

            // 🖼️ Pantalla de detalle de lugar
            composable<NavRoute.PlaceDetail> {
                val viewModel: NearbyPlacesViewModel = koinViewModel()
                PlaceDetailScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            // 👤 Perfil
            composable<NavRoute.Profile> {
                // Implementación pendiente o redirección
                navController.navigate(NavRoute.Map)
            }
        }
    }
}
