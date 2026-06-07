package com.ucb.mapexplorer.friendsRequests.presentation.state

import com.ucb.mapexplorer.friends.domain.model.FriendRequestModel


sealed interface FriendsRequestsEvent {
    data object OnBackClick : FriendsRequestsEvent
    data object OnLoadRequests : FriendsRequestsEvent
    data class OnAcceptClick(val request: FriendRequestModel) : FriendsRequestsEvent
    data class OnDeclineClick(val request: FriendRequestModel) : FriendsRequestsEvent
}