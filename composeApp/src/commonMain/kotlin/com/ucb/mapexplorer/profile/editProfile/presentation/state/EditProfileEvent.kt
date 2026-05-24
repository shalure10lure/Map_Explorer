package com.ucb.mapexplorer.profile.editProfile.presentation.state


sealed interface EditProfileEvent {
    data class OnDescriptionChange(val description: String) : EditProfileEvent
    data class OnEmailChange(val email: String) : EditProfileEvent
    data object OnCancelClick : EditProfileEvent
    data object OnSaveClick : EditProfileEvent
}