package com.ucb.mapexplorer.searchUser.presentation.state

import com.ucb.mapexplorer.friends.domain.model.UserSearchModel

sealed interface SearchUserEvent {
    data class OnQueryChanged(val query: String) : SearchUserEvent
    data class OnUserSelected(val user: UserSearchModel) : SearchUserEvent
    data object OnConfirmSendRequest : SearchUserEvent
    data object OnDismissDialog : SearchUserEvent
    data object OnBackClick : SearchUserEvent
    data object OnDismissError : SearchUserEvent
}