package com.ucb.mapexplorer.auth.presentation.login.state

sealed interface LoginEffect {
    object NavigateToHome: LoginEffect
    object NavigateToRegister : LoginEffect

    data class ShowError(
        val message: String
    ): LoginEffect
}