package com.ucb.mapexplorer.profile.ownProfile.presentation.state



sealed interface OwnProfileEffect {
    data object NavigateBack : OwnProfileEffect
    data object NavigateToEditProfile : OwnProfileEffect
    data object NavigateToRequests : OwnProfileEffect
    data class NavigateToFriendProfile(val friendName: String) : OwnProfileEffect
}