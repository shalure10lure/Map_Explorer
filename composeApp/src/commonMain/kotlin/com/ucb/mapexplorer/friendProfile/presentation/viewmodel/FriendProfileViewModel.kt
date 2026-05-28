package com.ucb.mapexplorer.friendProfile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.friendProfile.presentation.state.FriendProfileEffect
import com.ucb.mapexplorer.friendProfile.presentation.state.FriendProfileEvent
import com.ucb.mapexplorer.friendProfile.presentation.state.FriendProfileUIState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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