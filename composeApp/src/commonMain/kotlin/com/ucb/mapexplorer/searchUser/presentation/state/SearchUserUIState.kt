package com.ucb.mapexplorer.searchUser.presentation.state

import com.ucb.mapexplorer.auth.domain.model.UserModel

data class SearchUserUIState(
    val searchQuery: String = "",
    val searchResults: List<UserModel> = emptyList(),
    val isLoading: Boolean = false,
    val selectedUser: UserModel? = null,
    val showConfirmationDialog: Boolean = false
)