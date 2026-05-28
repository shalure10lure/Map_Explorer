package com.ucb.mapexplorer.auth.presentation.register.state

import com.ucb.mapexplorer.profile.domain.model.AvatarAccessory
import com.ucb.mapexplorer.profile.domain.model.AvatarBody
import com.ucb.mapexplorer.profile.domain.model.AvatarHat

sealed interface RegisterEvent {
    // Paso 1
    data class OnUsernameChanged(val value: String)        : RegisterEvent
    data class OnEmailChanged(val value: String)           : RegisterEvent
    data class OnPasswordChanged(val value: String)        : RegisterEvent
    data class OnConfirmPasswordChanged(val value: String) : RegisterEvent
    object OnNextStep                                      : RegisterEvent
    object OnClickLogin                                    : RegisterEvent

    // Paso 2
    data class OnDescriptionChanged(val value: String)    : RegisterEvent
    data class OnAgeChanged(val value: Int)               : RegisterEvent
    data class OnBodySelected(val body: AvatarBody)       : RegisterEvent
    data class OnHatSelected(val hat: AvatarHat)          : RegisterEvent
    data class OnAccessorySelected(val acc: AvatarAccessory) : RegisterEvent
    data class OnAvatarTabSelected(val tab: AvatarTab)    : RegisterEvent
    object OnBackStep                                     : RegisterEvent
    object OnClick                                        : RegisterEvent
}