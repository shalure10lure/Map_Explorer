package com.ucb.mapexplorer.map.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import com.ucb.mapexplorer.navigation.MainTab
import com.ucb.mapexplorer.navigation.composable.MainTopBar
import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel
import com.ucb.mapexplorer.profile.ownProfile.presentation.screen.OwnProfileScreen
import com.ucb.mapexplorer.profile.ownProfile.presentation.viewmodel.OwnProfileViewModel
import com.ucb.mapexplorer.social.presentation.screen.SocialScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainScreen(
    navController: NavController
) {
    var selectedTab by remember { mutableStateOf(MainTab.NEARBY) }
    val avatarConfig = remember { AvatarConfigModel() }
    val ownProfileViewModel: OwnProfileViewModel = koinViewModel()

    // Usamos Box para asegurar que la TopBar siempre esté en la capa superior (Z-order)
    Box(modifier = Modifier.fillMaxSize()) {
        
        // 1. Contenido principal (Map, Social, Perfil)
        // Se coloca primero para que quede al fondo
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                MainTab.NEARBY -> {
                    // El mapa ocupa toda la pantalla. 
                    // La TopBar flotará encima.
                    MapScreen()
                }
                
                MainTab.SOCIAL -> {
                    // Añadimos un espaciador para que el contenido no empiece debajo de la TopBar
                    Column(modifier = Modifier.fillMaxSize()) {
                        Spacer(modifier = Modifier.statusBarsPadding())
                        Spacer(modifier = Modifier.height(80.dp)) // Altura estimada de la barra
                        SocialScreen(navController = navController)
                    }
                }
                
                MainTab.PROFILE -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Spacer(modifier = Modifier.statusBarsPadding())
                        Spacer(modifier = Modifier.height(80.dp))
                        OwnProfileScreen(
                            viewModel      = ownProfileViewModel,
                            onBack         = { selectedTab = MainTab.NEARBY },
                            onEditProfile  = { navController.navigate(com.ucb.mapexplorer.navigation.NavRoute.EditProfile) },
                            onViewRequests = { },
                            onViewFriend   = { }
                        )
                    }
                }
            }
        }

        // 2. MainTopBar (Encima de todo)
        // Al estar al final del Box y con zIndex, se garantiza su visibilidad sobre el mapa nativo.
        MainTopBar(
            selectedTab   = selectedTab,
            avatarConfig  = avatarConfig,
            onTabSelected = { selectedTab = it },
            onAvatarClick = { selectedTab = MainTab.PROFILE },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(10f)
        )
    }
}
