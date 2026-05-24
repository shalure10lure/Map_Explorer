package com.ucb.mapexplorer.profile.editProfile.presentation.state



sealed interface EditProfileEffect {
    data object NavigateBack : EditProfileEffect
    data object ShowSaveSuccess : EditProfileEffect
}
