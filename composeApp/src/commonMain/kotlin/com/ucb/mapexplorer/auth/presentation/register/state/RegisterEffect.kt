package com.ucb.mapexplorer.auth.presentation.register.state

sealed interface RegisterEffect {
    object NavigateToLogin : RegisterEffect
    object NavigateToHome : RegisterEffect
    data class ShowError(val message: String) : RegisterEffect
}