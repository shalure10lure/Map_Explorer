package com.ucb.mapexplorer.profile.friendsRequests.presentation.state



sealed interface FriendsRequestsEvent {
    data object OnBackClick : FriendsRequestsEvent
    data class OnAcceptClick(val friendName: String) : FriendsRequestsEvent
    data class OnDeclineClick(val friendName: String) : FriendsRequestsEvent
}