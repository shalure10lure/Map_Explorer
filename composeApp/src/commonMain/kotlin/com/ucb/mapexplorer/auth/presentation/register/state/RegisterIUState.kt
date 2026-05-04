package com.ucb.mapexplorer.auth.presentation.register.state

data class RegisterUIState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val description: String = "",
    val isLoading: Boolean = false
)