package com.ucb.mapexplorer.auth.presentation.login.state

data class LoginUIState (
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false
)