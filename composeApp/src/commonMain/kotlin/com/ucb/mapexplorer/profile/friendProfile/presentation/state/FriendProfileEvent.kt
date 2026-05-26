package com.ucb.mapexplorer.profile.friendProfile.presentation.state


sealed interface FriendProfileEvent {
    data object OnBackClick : FriendProfileEvent
    data object OnBackToProfileClick : FriendProfileEvent
    data object OnTrackRealtimeClick : FriendProfileEvent
}