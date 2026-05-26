package com.ucb.mapexplorer.map.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
    // Pestaña activa — empieza en el mapa (ninguna pestaña seleccionada)
    // pero usamos NEARBY como pantalla de inicio
    var selectedTab by remember { mutableStateOf(MainTab.NEARBY) }

    // Avatar del usuario — en el futuro vendrá del ViewModel de sesión
    val avatarConfig = remember { AvatarConfigModel() }

    Column(modifier = Modifier.fillMaxSize()) {

        // ── BARRA SUPERIOR ROJA ─────────────────────────
        MainTopBar(
            selectedTab   = selectedTab,
            avatarConfig  = avatarConfig,
            onTabSelected = { selectedTab = it },
            onAvatarClick = { selectedTab = MainTab.PROFILE }
        )

        // ── CONTENIDO SEGÚN PESTAÑA ─────────────────────
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                MainTab.NEARBY  -> MapScreen()
                MainTab.SOCIAL  -> SocialScreen(navController = navController)
                MainTab.PROFILE -> OwnProfileScreen(
                    viewModel       = koinViewModel(),
                    onBack          = { selectedTab = MainTab.NEARBY },
                    onEditProfile   = { navController.navigate(com.ucb.mapexplorer.navigation.NavRoute.EditProfile) },
                    onViewRequests  = { /* navegar a solicitudes */ },
                    onViewFriend    = { /* navegar al perfil del amigo */ }
                )
            }
        }
    }
}