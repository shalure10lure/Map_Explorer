package com.ucb.mapexplorer.profile.editProfile.presentation.state

import com.ucb.mapexplorer.profile.domain.model.AvatarAccessory
import com.ucb.mapexplorer.profile.domain.model.AvatarBody
import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel
import com.ucb.mapexplorer.profile.domain.model.AvatarHat

data class EditProfileUIState(
    val name: String        = "Usuario",
    val description: String = "Explorador de mapas",
    val email: String       = "usuario@gmail.com",

    // Avatar actual
    val avatarConfig: AvatarConfigModel = AvatarConfigModel(),

    // Qué categoría está seleccionada en el selector
    val selectedTab: AvatarTab = AvatarTab.BODY,

    val isLoading: Boolean  = false,
    val isSaved: Boolean    = false
)

enum class AvatarTab(val label: String) {
    BODY("Cuerpo"),
    HAT("Sombrero"),
    ACCESSORY("Accesorio")
}