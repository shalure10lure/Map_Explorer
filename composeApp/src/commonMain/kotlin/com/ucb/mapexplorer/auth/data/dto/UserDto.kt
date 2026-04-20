package com.ucb.mapexplorer.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val username: String,
    val email: String,
    val password: String,
    val description: String? = null,
    val photoUrl: String? = null
)
