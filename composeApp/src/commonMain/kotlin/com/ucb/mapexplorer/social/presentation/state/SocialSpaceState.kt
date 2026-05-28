package com.ucb.mapexplorer.social.presentation.state

data class SocialSpaceState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val posts: List<SocialPost> = emptyList()
)

data class SocialPost(
    val id: String,
    val userName: String,
    val locationName: String,
    val rating: Int,
    val category: String,
    val userExperience: String,
    val isFriend: Boolean,
    val imageUrl: String? = null
)

sealed interface SocialSpaceEvent {
    data class OnSearchQueryChanged(val query: String) : SocialSpaceEvent
    data object OnBackClick : SocialSpaceEvent
    data object OnMessageClick : SocialSpaceEvent
    data class OnAddFriendClick(val postId: String) : SocialSpaceEvent
    data class OnViewOnMapClick(val postId: String) : SocialSpaceEvent
}

sealed interface SocialSpaceEffect {
    data object NavigateBack : SocialSpaceEffect
    data object NavigateToMessages : SocialSpaceEffect
    data class ShowError(val message: String) : SocialSpaceEffect
}
