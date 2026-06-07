package com.ucb.mapexplorer.searchUser.presentation.state

import com.ucb.mapexplorer.auth.domain.model.UserModel

sealed interface SearchUserEvent {
    data class OnQueryChanged(val query: String) : SearchUserEvent
    data class OnUserSelected(val user: UserModel) : SearchUserEvent
    data object OnConfirmSendRequest : SearchUserEvent
    data object OnDismissDialog : SearchUserEvent
    data object OnBackClick : SearchUserEvent
}