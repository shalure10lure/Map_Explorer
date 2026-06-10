package com.ucb.mapexplorer.friendProfile.presentation.state

import com.ucb.mapexplorer.friends.domain.model.FriendModel

data class FriendProfileUIState(
    val friendUid: String = "",
    val friendName: String = "",
    val description: String = "",
    val avatarId: String = "",
    val level: Int = 1,
    val email: String = "",
    val mutualFriends: List<String> = emptyList(),
    val friendsList: List<FriendModel> = emptyList(),
    val isLoading: Boolean = false,
    val isRemoving: Boolean = false,
    val showRemoveDialog: Boolean = false
)
