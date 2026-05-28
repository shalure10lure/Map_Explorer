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
import com.ucb.mapexplorer.navigation.MainTab
import com.ucb.mapexplorer.navigation.NavRoute
import com.ucb.mapexplorer.navigation.composable.MainTopBar
import com.ucb.mapexplorer.profile.presentation.screen.OwnProfileScreen
import com.ucb.mapexplorer.profile.presentation.viewmodel.OwnProfileViewModel
import com.ucb.mapexplorer.social.presentation.screen.SocialSpaceScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(
    navController: NavController
) {
    // rememberSaveable mantiene la pestaña incluso si navegamos a otra ruta y volvemos
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.NEARBY) }
    val ownProfileViewModel: OwnProfileViewModel = koinViewModel()
    val profileState by ownProfileViewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                MainTab.NEARBY -> {
                    MapScreen()
                }
                
                MainTab.SOCIAL -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                        Spacer(modifier = Modifier.height(70.dp)) 
                        SocialSpaceScreen(
                            onBack = { selectedTab = MainTab.NEARBY },
                            onNavigateToMessages = { /* TODO */ },
                            onNavigateToNearby = { selectedTab = MainTab.NEARBY },
                            onNavigateToProfile = { selectedTab = MainTab.PROFILE }
                        )
                    }
                }
                
                MainTab.PROFILE -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                        Spacer(modifier = Modifier.height(70.dp))
                        OwnProfileScreen(
                            viewModel      = ownProfileViewModel,
                            onBack         = { selectedTab = MainTab.NEARBY },
                            onEditProfile  = { navController.navigate(NavRoute.EditProfile) },
                            onViewRequests = { },
                            onViewFriend   = { }
                        )
                    }
                }
            }
        }

        // La TopBar usa el avatar real cargado en el ViewModel de Perfil
        MainTopBar(
            selectedTab   = selectedTab,
            avatarConfig  = profileState.avatarConfig,
            onTabSelected = { selectedTab = it },
            onAvatarClick = { selectedTab = MainTab.PROFILE },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(10f)
        )
    }
}
