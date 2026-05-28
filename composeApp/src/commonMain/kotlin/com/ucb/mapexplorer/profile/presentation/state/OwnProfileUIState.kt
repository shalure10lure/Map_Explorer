package com.ucb.mapexplorer.profile.presentation.state

import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel

data class OwnProfileUIState(
    val userName: String = "",
    val description: String = "",
    val level: Int = 1,
    val email: String = "",
    val friends: List<String> = emptyList(),
    val avatarConfig: AvatarConfigModel = AvatarConfigModel(),
    val isLoading: Boolean = false
)
