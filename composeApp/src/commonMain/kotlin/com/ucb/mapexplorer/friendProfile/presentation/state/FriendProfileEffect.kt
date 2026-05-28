package com.ucb.mapexplorer.friendProfile.presentation.state


sealed interface FriendProfileEffect {
    data object NavigateBack : FriendProfileEffect
    data object NavigateToOwnProfile : FriendProfileEffect
    data object StartRealtimeTracking : FriendProfileEffect
}
