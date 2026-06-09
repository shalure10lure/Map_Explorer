package com.ucb.mapexplorer.friends.domain.model

data class FriendModel(
    val uid: String,          // uid del amigo (ej: "boris_gmail_com")
    val username: String,
    val description: String = "",
    val avatarId: String = "",
    val desde: Long = 0L      // timestamp cuando se hicieron amigos
)

