package com.ucb.mapexplorer.friendsRequests.presentation.state



sealed interface FriendsRequestsEffect {
    data object NavigateBack : FriendsRequestsEffect
    data class ShowToast(val message: String) : FriendsRequestsEffect
}
