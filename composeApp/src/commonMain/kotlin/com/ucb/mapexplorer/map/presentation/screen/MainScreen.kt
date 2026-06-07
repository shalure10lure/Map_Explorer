package com.ucb.mapexplorer.map.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.ucb.designsystem.theme.AppTheme
import com.ucb.mapexplorer.map.presentation.state.MapEvent
import com.ucb.mapexplorer.map.presentation.viewmodel.MapViewModel
import com.ucb.mapexplorer.navigation.MainTab
import com.ucb.mapexplorer.navigation.NavRoute
import com.ucb.mapexplorer.navigation.composable.MainTopBar
import com.ucb.mapexplorer.nearbyplaces.presentation.screen.NearbyPlacesScreen
import com.ucb.mapexplorer.nearbyplaces.presentation.viewmodel.NearbyPlacesViewModel
import com.ucb.mapexplorer.profile.presentation.screen.OwnProfileScreen
import com.ucb.mapexplorer.profile.presentation.viewmodel.OwnProfileViewModel
import com.ucb.mapexplorer.social.presentation.screen.SocialSpaceScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(
    navController: NavController
) {
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.MAP) }

    val mapViewModel: MapViewModel = koinViewModel()
    val mapState by mapViewModel.state.collectAsState()

    val ownProfileViewModel: OwnProfileViewModel = koinViewModel()
    val profileState by ownProfileViewModel.state.collectAsState()

    LaunchedEffect(profileState.avatarConfig) {
        val mapAvatar     = mapState.avatarConfig
        val profileAvatar = profileState.avatarConfig
        if (mapAvatar != profileAvatar) {
            mapViewModel.onEvent(MapEvent.OnAvatarUpdated(profileAvatar))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                MainTab.MAP -> {
                    MapScreen(navController = navController, viewModel = mapViewModel)
                }

                MainTab.SOCIAL -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                        Spacer(modifier = Modifier.height(70.dp))
                        SocialSpaceScreen(
                            onBack = { selectedTab = MainTab.MAP },
                            onNavigateToFriendsRequests = { navController.navigate(NavRoute.FriendsRequests) },
                            onNavigateToNearby = { selectedTab = MainTab.NEARBY },
                            onNavigateToProfile = { selectedTab = MainTab.PROFILE }
                        )
                    }
                }

                MainTab.NEARBY -> {
                    val nearbyViewModel: NearbyPlacesViewModel = koinViewModel()
                    Column(modifier = Modifier.fillMaxSize()) {
                        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                        Spacer(modifier = Modifier.height(70.dp))
                        NearbyPlacesScreen(
                            mapViewModel = mapViewModel,
                            viewModel = nearbyViewModel,
                            onPlaceClick = { placeId ->
                                navController.navigate(NavRoute.PlaceDetail(placeId))
                            },
                            onBack = { selectedTab = MainTab.MAP }
                        )
                    }
                }

                MainTab.PROFILE -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                        Spacer(modifier = Modifier.height(70.dp))
                        OwnProfileScreen(
                            viewModel = ownProfileViewModel,
                            onBack = { selectedTab = MainTab.MAP },
                            onEditProfile = { navController.navigate(NavRoute.EditProfile) },
                            onViewRequests = { navController.navigate(NavRoute.FriendsRequests) },
                            onViewFriend = { friendUid ->
                                navController.navigate(NavRoute.FriendProfile(friendUid))
                            }
                        )
                    }
                }
            }
        }

        MainTopBar(
            selectedTab = selectedTab,
            avatarConfig = profileState.avatarConfig,
            onTabSelected = { tab -> selectedTab = tab },
            onAvatarClick = { selectedTab = MainTab.PROFILE },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(10f)
        )
    }
}