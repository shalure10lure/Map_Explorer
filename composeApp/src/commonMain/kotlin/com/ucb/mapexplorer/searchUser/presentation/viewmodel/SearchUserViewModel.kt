package com.ucb.mapexplorer.searchUser.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.core.session.Session
import com.ucb.mapexplorer.friends.domain.model.UserSearchModel
import com.ucb.mapexplorer.friends.domain.usecase.HasPendingRequestUseCase
import com.ucb.mapexplorer.friends.domain.usecase.IsFriendUseCase
import com.ucb.mapexplorer.friends.domain.usecase.SearchUsersUseCase
import com.ucb.mapexplorer.friends.domain.usecase.SendFriendRequestUseCase
import com.ucb.mapexplorer.profile.domain.usecase.GetProfileUseCase
import com.ucb.mapexplorer.searchUser.presentation.state.SearchUserEffect
import com.ucb.mapexplorer.searchUser.presentation.state.SearchUserEvent
import com.ucb.mapexplorer.searchUser.presentation.state.SearchUserUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchUserViewModel(
    private val searchUsersUseCase: SearchUsersUseCase,
    private val sendFriendRequestUseCase: SendFriendRequestUseCase,
    private val isFriendUseCase: IsFriendUseCase,
    private val hasPendingRequestUseCase: HasPendingRequestUseCase,
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUserUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SearchUserEffect>()
    val effect = _effect.asSharedFlow()

    private var searchJob: Job? = null

    fun onEvent(event: SearchUserEvent) {
        when (event) {
            is SearchUserEvent.OnQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
                searchDebounced(event.query)
            }
            is SearchUserEvent.OnUserSelected -> checkAndShowDialog(event.user)
            SearchUserEvent.OnConfirmSendRequest -> sendRequest()
            SearchUserEvent.OnDismissDialog ->
                _state.update { it.copy(showConfirmationDialog = false, selectedUser = null) }
            SearchUserEvent.OnBackClick ->
                viewModelScope.launch { _effect.emit(SearchUserEffect.NavigateBack) }
            SearchUserEvent.OnDismissError ->
                _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun searchDebounced(query: String) {
        searchJob?.cancel()
        if (query.length < 2) {
            _state.update { it.copy(searchResults = emptyList()) }
            return
        }
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            delay(400) // debounce
            _state.update { it.copy(isLoading = true) }
            val myUid = Session.uid ?: ""
            val results = searchUsersUseCase(query, myUid)
            _state.update { it.copy(searchResults = results, isLoading = false) }
        }
    }

    private fun checkAndShowDialog(user: UserSearchModel) {
        val myUid = Session.uid ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val isAlreadyFriend = isFriendUseCase(myUid, user.uid)
            val hasPending = hasPendingRequestUseCase(myUid, user.uid)
            _state.update {
                it.copy(
                    selectedUser = user,
                    showConfirmationDialog = true,
                    alreadyFriend = isAlreadyFriend,
                    requestSent = hasPending
                )
            }
        }
    }

    private fun sendRequest() {
        val myUid = Session.uid ?: return
        val toUser = _state.value.selectedUser ?: return
        _state.update { it.copy(sendingRequest = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val myProfile = getProfileUseCase(myUid)
            val myUsername = myProfile?.name ?: myUid
            val ok = sendFriendRequestUseCase(myUid, toUser.uid, myUsername)
            _state.update {
                it.copy(
                    sendingRequest = false,
                    showConfirmationDialog = false,
                    selectedUser = null
                )
            }
            _effect.emit(
                if (ok) SearchUserEffect.ShowToast("¡Solicitud enviada a ${toUser.username}! 🎉")
                else SearchUserEffect.ShowToast("No se pudo enviar la solicitud")
            )
        }
    }
}