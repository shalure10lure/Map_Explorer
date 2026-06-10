package com.ucb.mapexplorer.searchUser.presentation.state

import com.ucb.mapexplorer.friends.domain.model.UserSearchModel

data class SearchUserUIState(
    val searchQuery: String = "",
    val searchResults: List<UserSearchModel> = emptyList(),
    val isLoading: Boolean = false,
    val selectedUser: UserSearchModel? = null,
    val showConfirmationDialog: Boolean = false,
    val sendingRequest: Boolean = false,
    val requestSent: Boolean = false,        // para mostrar "Ya enviada"
    val alreadyFriend: Boolean = false,      // si ya son amigos
    val errorMessage: String? = null
)