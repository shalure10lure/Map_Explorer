package com.ucb.mapexplorer.profile.domain.model

data class ProfileModel(
    val uid: String,
    val name: String,
    val email: String,
    val description: String,
    val avatarConfig: AvatarConfigModel,
    val level: Int = 1,
    val age: Int = 0
)