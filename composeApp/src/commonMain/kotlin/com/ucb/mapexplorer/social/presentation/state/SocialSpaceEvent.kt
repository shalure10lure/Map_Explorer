package com.ucb.mapexplorer.social.presentation.state

sealed interface SocialSpaceEvent {
    data class OnSearchQueryChanged(val query: String) : SocialSpaceEvent
    data object OnBackClick : SocialSpaceEvent
    data object OnMessageClick : SocialSpaceEvent
    data class OnAddFriendClick(val authorUid: String) : SocialSpaceEvent
    data class OnViewPlaceDetail(val lugarId: String) : SocialSpaceEvent
}