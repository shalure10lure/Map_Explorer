package com.ucb.mapexplorer.auth.presentation.register.state

sealed interface RegisterEvent {

    object OnClick : RegisterEvent

    object OnClickLogin : RegisterEvent

    data class OnUsernameChanged(val value: String) : RegisterEvent
    data class OnEmailChanged(val value: String) : RegisterEvent
    data class OnPasswordChanged(val value: String) : RegisterEvent
    data class OnConfirmPasswordChanged(val value: String) : RegisterEvent
    data class OnDescriptionChanged(val value: String) : RegisterEvent
}