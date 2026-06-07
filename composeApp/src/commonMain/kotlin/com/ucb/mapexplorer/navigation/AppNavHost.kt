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
import com.ucb.mapexplorer.map.presentation.screen.MainScreen
import com.ucb.mapexplorer.map.presentation.screen.MapScreen
import com.ucb.mapexplorer.nearbyplaces.presentation.screen.NearbyPlacesScreen
import com.ucb.mapexplorer.nearbyplaces.presentation.screen.PlaceDetailScreen
import com.ucb.mapexplorer.nearbyplaces.presentation.viewmodel.NearbyPlacesViewModel
import com.ucb.mapexplorer.editProfile.presentation.screen.EditProfileScreen
import com.ucb.mapexplorer.editProfile.presentation.viewmodel.EditProfileViewModel
import com.ucb.mapexplorer.favoritePlaces.presentation.screen.FavoritePlacesScreen
import com.ucb.mapexplorer.friendProfile.presentation.screen.FriendProfileScreen
import com.ucb.mapexplorer.friendProfile.presentation.viewmodel.FriendProfileViewModel
import com.ucb.mapexplorer.friendsRequests.presentation.screen.FriendsRequestsScreen
import com.ucb.mapexplorer.friendsRequests.presentation.viewmodel.FriendsRequestsViewModel
import com.ucb.mapexplorer.map.presentation.screen.GuideMapScreen
import com.ucb.mapexplorer.map.presentation.viewmodel.MapViewModel
import com.ucb.mapexplorer.onboarding.presentation.screen.OnboardingScreen
import com.ucb.mapexplorer.publication.presentation.screen.PublicationScreen
import com.ucb.mapexplorer.savedPlaces.presentation.screen.SavedPlacesScreen
import com.ucb.mapexplorer.social.presentation.screen.SocialSpaceScreen
import com.ucb.mapexplorer.searchUser.presentation.screen.SearchUserScreen
import com.ucb.mapexplorer.searchUser.presentation.viewmodel.SearchUserViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

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
                MapScreen(navController = navController)
            }
            composable<NavRoute.Onboarding> {
                OnboardingScreen(navController = navController)
            }

            composable<NavRoute.SocialSpace> {
                SocialSpaceScreen(
                    onBack = { navController.popBackStack() },
                    onNavigateToFriendsRequests = { navController.navigate(NavRoute.FriendsRequests) },
                    onNavigateToNearby = { navController.navigate(NavRoute.NearbyPlaces) },
                    onNavigateToProfile = { navController.navigate(NavRoute.Profile) }
                )
            }

            composable<NavRoute.FriendsRequests> {
                val viewModel: FriendsRequestsViewModel = koinViewModel()
                FriendsRequestsScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigateToSearch = { navController.navigate(NavRoute.SearchUser) }
                )
            }

            composable<NavRoute.SearchUser> {
                val viewModel: SearchUserViewModel = koinViewModel()
                SearchUserScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable<NavRoute.NearbyPlaces> {
                val mapViewModel: MapViewModel = koinViewModel()

                val nearbyViewModel: NearbyPlacesViewModel = koinViewModel()
                NearbyPlacesScreen(
                    mapViewModel = mapViewModel,
                    viewModel = nearbyViewModel,
                    onPlaceClick = { placeId ->
                        navController.navigate(NavRoute.PlaceDetail(placeId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable<NavRoute.Profile> {
                navController.navigate(NavRoute.Map)
            }
            composable<NavRoute.FavoritePlaces> {
                FavoritePlacesScreen(
                    onBack              = { navController.popBackStack() },
                    onNavigateToDetail  = { placeId ->
                        navController.navigate(NavRoute.PlaceDetail(placeId))
                    }
                )
            }
            composable<NavRoute.SavedPlaces> {
                SavedPlacesScreen(
                    onBack              = { navController.popBackStack() },
                    onNavigateToDetail  = { placeId ->
                        navController.navigate(NavRoute.PlaceDetail(placeId))
                    }
                )
            }

            composable<NavRoute.PlaceDetail> { backStackEntry ->
                val route: NavRoute.PlaceDetail = backStackEntry.toRoute()
                val mapViewModel: MapViewModel = koinViewModel()
                val nearbyViewModel: NearbyPlacesViewModel = koinViewModel()

                PlaceDetailScreen(
                    placeId = route.placeId,
                    mapViewModel = mapViewModel,
                    nearbyViewModel = nearbyViewModel,
                    navController = navController,
                    onBack = { navController.popBackStack() }
                )
            }
            composable<NavRoute.GuideMap> { backStackEntry ->
                val route: NavRoute.GuideMap = backStackEntry.toRoute()
                GuideMapScreen(
                    userLat   = route.userLat,
                    userLon   = route.userLon,
                    destLat   = route.destLat,
                    destLon   = route.destLon,
                    placeName = route.placeName,
                    onBack    = { navController.popBackStack() }
                )
            }

            composable<NavRoute.Publication> { backStackEntry ->
                val route: NavRoute.Publication = backStackEntry.toRoute()
                PublicationScreen(
                    placeId     = route.placeId,
                    onBack      = { navController.popBackStack() },
                    onPublished = {
                        navController.navigate(NavRoute.Main) {
                            popUpTo(NavRoute.Main) { inclusive = false }
                        }
                    }
                )
            }
            composable<NavRoute.FriendProfile> { backStackEntry ->
                val route: NavRoute.FriendProfile = backStackEntry.toRoute()
                val vm: FriendProfileViewModel = koinViewModel()
                FriendProfileScreen(
                    friendUid = route.friendUid,
                    viewModel = vm,
                    onBack    = { navController.popBackStack() },
                    onBackToProfile = { navController.popBackStack() }
                )
            }

        }
    }
}
