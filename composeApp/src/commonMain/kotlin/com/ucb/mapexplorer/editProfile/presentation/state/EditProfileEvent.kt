package com.ucb.mapexplorer.editProfile.presentation.state

import com.ucb.mapexplorer.profile.domain.model.AvatarAccessory
import com.ucb.mapexplorer.profile.domain.model.AvatarBody
import com.ucb.mapexplorer.profile.domain.model.AvatarHat

sealed interface EditProfileEvent {
    data class OnNameChange(val name: String)           : EditProfileEvent
    data class OnDescriptionChange(val description: String) : EditProfileEvent
    data class OnEmailChange(val email: String)         : EditProfileEvent

    // Selección de partes del avatar
    data class OnBodySelected(val body: AvatarBody)           : EditProfileEvent
    data class OnHatSelected(val hat: AvatarHat)              : EditProfileEvent
    data class OnAccessorySelected(val acc: AvatarAccessory)  : EditProfileEvent
    data class OnTabSelected(val tab: AvatarTab)              : EditProfileEvent

    data object OnSaveClick   : EditProfileEvent
    data object OnCancelClick : EditProfileEvent
}