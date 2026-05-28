package com.ucb.mapexplorer.map.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
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
    // Iniciamos en la pestaña del Mapa por defecto
    var selectedTab by rememberSaveable { mutableStateOf(MainTab.MAP) }
    val ownProfileViewModel: OwnProfileViewModel = koinViewModel()
    val profileState by ownProfileViewModel.state.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.background)
    ) {
        
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                MainTab.MAP -> {
                    MapScreen()
                }
                
                MainTab.SOCIAL -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                        Spacer(modifier = Modifier.height(70.dp)) 
                        SocialSpaceScreen(
                            onBack = { selectedTab = MainTab.MAP },
                            onNavigateToMessages = { /* TODO */ },
                            onNavigateToNearby = { selectedTab = MainTab.NEARBY },
                            onNavigateToProfile = { selectedTab = MainTab.PROFILE }
                        )
                    }
                }

                MainTab.NEARBY -> {
                    // Pantalla temporal vacía o placeholder
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Próximamente: Lugares Cercanos", color = AppTheme.colors.textSecondary)
                    }
                }
                
                MainTab.PROFILE -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
                        Spacer(modifier = Modifier.height(70.dp))
                        OwnProfileScreen(
                            viewModel      = ownProfileViewModel,
                            onBack         = { selectedTab = MainTab.MAP },
                            onEditProfile  = { navController.navigate(NavRoute.EditProfile) },
                            onViewRequests = { },
                            onViewFriend   = { }
                        )
                    }
                }
            }
        }

        // La TopBar con las nuevas opciones: Social Media, Mapa y Lugares Cercanos
        MainTopBar(
            selectedTab   = selectedTab,
            avatarConfig  = profileState.avatarConfig,
            onTabSelected = { tab ->
                // "Lugares Cercanos" (NEARBY) está deshabilitado temporalmente si se desea
                // Pero lo dejamos navegable para que se vea el placeholder
                selectedTab = tab
            },
            onAvatarClick = { selectedTab = MainTab.PROFILE },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(10f)
        )
    }
}
