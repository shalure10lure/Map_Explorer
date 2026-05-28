package com.ucb.mapexplorer.profile.ownProfile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.ucb.mapexplorer.profile.ownProfile.presentation.state.*

class OwnProfileViewModel : ViewModel() {
    private val _state = MutableStateFlow(OwnProfileUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<OwnProfileEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: OwnProfileEvent) {
        when (event) {
            OwnProfileEvent.OnBackClick -> {
                viewModelScope.launch { _effect.emit(OwnProfileEffect.NavigateBack) }
            }
            OwnProfileEvent.OnEditProfileClick -> {
                viewModelScope.launch { _effect.emit(OwnProfileEffect.NavigateToEditProfile) }
            }
            OwnProfileEvent.OnViewRequestsClick -> {
                viewModelScope.launch { _effect.emit(OwnProfileEffect.NavigateToRequests) }
            }
            is OwnProfileEvent.OnFriendClick -> {
                viewModelScope.launch { _effect.emit(OwnProfileEffect.NavigateToFriendProfile(event.friendName)) }
            }
        }
    }
}
