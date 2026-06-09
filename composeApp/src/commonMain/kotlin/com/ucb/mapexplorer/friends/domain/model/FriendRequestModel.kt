package com.ucb.mapexplorer.friends.domain.model



data class FriendRequestModel(
    val requestId: String,
    val emisorUid: String,
    val emisorUsername: String,
    val receptorUid: String,
    val estado: String,
    val fecha: Long
)