package com.ucb.mapexplorer.social.presentation.state

data class SocialSpaceState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val posts: List<SocialPost> = emptyList()
)

data class SocialPost(
    val id: String,
    val authorUid: String       = "",
    val userName: String,
    val locationName: String,
    val rating: Int,
    val category: String,
    val categoryIcon: String    = "📍",
    val userExperience: String,
    val isFriend: Boolean,
    val imageUrl: String?       = null,
    val avatarId: String        = "",
    val requestSent: Boolean    = false
)



