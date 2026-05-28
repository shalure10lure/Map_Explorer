package com.ucb.mapexplorer.profile.friendProfile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.ucb.mapexplorer.profile.friendProfile.presentation.state.*

class FriendProfileViewModel : ViewModel() {
    private val _state = MutableStateFlow(FriendProfileUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<FriendProfileEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: FriendProfileEvent) {
        when (event) {
            FriendProfileEvent.OnBackClick -> {
                viewModelScope.launch { _effect.emit(FriendProfileEffect.NavigateBack) }
            }
            FriendProfileEvent.OnBackToProfileClick -> {
                viewModelScope.launch { _effect.emit(FriendProfileEffect.NavigateToOwnProfile) }
            }
            FriendProfileEvent.OnTrackRealtimeClick -> {
                viewModelScope.launch { _effect.emit(FriendProfileEffect.StartRealtimeTracking) }
            }
        }
    }
}
