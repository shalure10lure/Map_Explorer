package com.ucb.mapexplorer.auth.domain.model

data class UserModel(
    val username: String,
    val email: String,
    val password: String,
    val description: String? = null,
    val photoUrl: String? = null
)