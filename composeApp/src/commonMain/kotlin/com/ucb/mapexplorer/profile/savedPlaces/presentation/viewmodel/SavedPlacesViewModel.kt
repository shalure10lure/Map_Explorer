package com.ucb.mapexplorer.profile.savedPlaces.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.ucb.mapexplorer.profile.savedPlaces.presentation.state.*

class SavedPlacesViewModel : ViewModel() {
    private val _state = MutableStateFlow(SavedPlacesUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SavedPlacesEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: SavedPlacesEvent) {
        when (event) {
            SavedPlacesEvent.OnBackClick -> {
                viewModelScope.launch { _effect.emit(SavedPlacesEffect.NavigateBack) }
            }
            is SavedPlacesEvent.OnPlaceClick -> {
                viewModelScope.launch { _effect.emit(SavedPlacesEffect.NavigateToPlaceDetail(event.place)) }
            }
        }
    }
}
