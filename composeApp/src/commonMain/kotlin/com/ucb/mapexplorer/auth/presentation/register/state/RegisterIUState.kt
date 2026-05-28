package com.ucb.mapexplorer.auth.presentation.register.state

import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel

data class RegisterUIState(
    // Paso 1
    val username: String        = "",
    val email: String           = "",
    val password: String        = "",
    val confirmPassword: String = "",

    // Paso 2
    val description: String     = "",
    val age: Int                = 15,
    val avatarConfig: AvatarConfigModel = AvatarConfigModel(),
    val selectedTab: AvatarTab  = AvatarTab.BODY,

    // Control de flujo
    val currentStep: Int        = 1,   // 1 o 2
    val isLoading: Boolean      = false
)

enum class AvatarTab(val label: String) {
    BODY("Cuerpo"),
    HAT("Sombrero"),
    ACCESSORY("Accesorio")
}