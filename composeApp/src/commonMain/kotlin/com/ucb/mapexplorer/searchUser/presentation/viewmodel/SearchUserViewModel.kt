package com.ucb.mapexplorer.searchUser.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.auth.domain.model.UserModel
import com.ucb.mapexplorer.searchUser.presentation.state.SearchUserEvent
import com.ucb.mapexplorer.searchUser.presentation.state.SearchUserUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchUserViewModel : ViewModel() {
    private val _state = MutableStateFlow(SearchUserUIState())
    val state = _state.asStateFlow()

    // Mock data removed as requested
    private val allUsers = emptyList<UserModel>()

    fun onEvent(event: SearchUserEvent) {
        when (event) {
            is SearchUserEvent.OnQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
                performSearch(event.query)
            }
            is SearchUserEvent.OnUserSelected -> {
                _state.update { it.copy(selectedUser = event.user, showConfirmationDialog = true) }
            }
            SearchUserEvent.OnConfirmSendRequest -> {
                sendFriendRequest(_state.value.selectedUser)
                _state.update { it.copy(showConfirmationDialog = false, selectedUser = null) }
            }
            SearchUserEvent.OnDismissDialog -> {
                _state.update { it.copy(showConfirmationDialog = false, selectedUser = null) }
            }
            SearchUserEvent.OnBackClick -> {
                // Handled by navigation
            }
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            _state.update { it.copy(searchResults = emptyList()) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // Filter logic remains but list is empty now
            val filtered = allUsers.filter { it.username.contains(query, ignoreCase = true) }
            _state.update { it.copy(searchResults = filtered, isLoading = false) }
        }
    }

    private fun sendFriendRequest(user: UserModel?) {
        user?.let {
            println("Friend request sent to ${it.username}")
        }
    }
}
