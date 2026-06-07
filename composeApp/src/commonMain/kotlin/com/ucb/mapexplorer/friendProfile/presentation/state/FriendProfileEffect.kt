package com.ucb.mapexplorer.friendProfile.presentation.state


sealed interface FriendProfileEffect {
    data object NavigateBack : FriendProfileEffect
    data object NavigateToOwnProfile : FriendProfileEffect
    data object FriendRemoved : FriendProfileEffect   // navegar atrás tras borrar
    data class ShowToast(val message: String) : FriendProfileEffect
}
