package com.ucb.mapexplorer.profile.friendsRequests.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.ucb.mapexplorer.profile.friendsRequests.presentation.state.*

class FriendsRequestsViewModel : ViewModel() {
    private val _state = MutableStateFlow(FriendsRequestsUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<FriendsRequestsEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: FriendsRequestsEvent) {
        when (event) {
            FriendsRequestsEvent.OnBackClick -> {
                viewModelScope.launch { _effect.emit(FriendsRequestsEffect.NavigateBack) }
            }
            is FriendsRequestsEvent.OnAcceptClick -> {
                acceptRequest(event.friendName)
            }
            is FriendsRequestsEvent.OnDeclineClick -> {
                declineRequest(event.friendName)
            }
        }
    }

    private fun acceptRequest(friendName: String) {
        _state.value = _state.value.copy(
            requests = _state.value.requests.filter { it != friendName }
        )
        viewModelScope.launch { _effect.emit(FriendsRequestsEffect.ShowToast("Solicitud de $friendName aceptada")) }
    }

    private fun declineRequest(friendName: String) {
        _state.value = _state.value.copy(
            requests = _state.value.requests.filter { it != friendName }
        )
        viewModelScope.launch { _effect.emit(FriendsRequestsEffect.ShowToast("Solicitud de $friendName rechazada")) }
    }
}
