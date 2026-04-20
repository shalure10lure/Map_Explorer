package com.ucb.mapexplorer.auth.presentation.login.state

sealed interface LoginEvent {

    object OnClick: LoginEvent

    object OnClickRegister : LoginEvent

    data class OnEmailChanged(
        val value: String
    ): LoginEvent

    data class OnPasswordChanged(
        val value: String
    ): LoginEvent
}