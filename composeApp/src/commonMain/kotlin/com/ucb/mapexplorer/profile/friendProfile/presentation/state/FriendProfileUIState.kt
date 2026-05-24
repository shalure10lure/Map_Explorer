package com.ucb.mapexplorer.profile.friendProfile.presentation.state

data class FriendProfileUIState(
    val friendName: String = "Amigo1",
    val description: String = "Descripción del usuario",
    val level: Int = 10,
    val email: String = "amigo1@gmail.com",
    val mutualFriends: List<String> = listOf("amigo1", "amigo2", "amigo3", "amigo4")
)

