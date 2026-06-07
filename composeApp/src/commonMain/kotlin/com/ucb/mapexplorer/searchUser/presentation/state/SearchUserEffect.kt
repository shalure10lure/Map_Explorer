package com.ucb.mapexplorer.searchUser.presentation.state

sealed interface SearchUserEffect {
    data object NavigateBack : SearchUserEffect
    data class ShowToast(val message: String) : SearchUserEffect
}