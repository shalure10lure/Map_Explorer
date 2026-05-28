package com.ucb.mapexplorer.editProfile.presentation.state

import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel

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