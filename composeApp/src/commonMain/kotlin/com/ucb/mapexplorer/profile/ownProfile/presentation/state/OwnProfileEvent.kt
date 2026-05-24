package com.ucb.mapexplorer.profile.ownProfile.presentation.state



sealed interface OwnProfileEvent {
    data object OnBackClick : OwnProfileEvent
    data object OnEditProfileClick : OwnProfileEvent
    data object OnViewRequestsClick : OwnProfileEvent
    data class OnFriendClick(val friendName: String) : OwnProfileEvent
}