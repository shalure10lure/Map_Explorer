package com.ucb.mapexplorer.friends.domain.model



data class UserSearchModel(
    val uid: String,
    val username: String,
    val description: String = "",
    val avatarId: String = ""
)