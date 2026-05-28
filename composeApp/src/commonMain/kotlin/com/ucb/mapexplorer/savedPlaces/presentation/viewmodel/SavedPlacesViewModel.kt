package com.ucb.mapexplorer.savedPlaces.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.savedPlaces.presentation.state.SavedPlacesEffect
import com.ucb.mapexplorer.savedPlaces.presentation.state.SavedPlacesEvent
import com.ucb.mapexplorer.savedPlaces.presentation.state.SavedPlacesUIState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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