package com.ucb.mapexplorer.friendsRequests.presentation.state

import com.ucb.mapexplorer.friends.domain.model.FriendRequestModel


data class FriendsRequestsUIState(
    val requests: List<FriendRequestModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
