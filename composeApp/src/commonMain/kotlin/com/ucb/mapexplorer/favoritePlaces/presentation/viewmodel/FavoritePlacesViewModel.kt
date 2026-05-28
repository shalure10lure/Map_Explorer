package com.ucb.mapexplorer.favoritePlaces.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.favoritePlaces.presentation.state.FavoritePlacesEffect
import com.ucb.mapexplorer.favoritePlaces.presentation.state.FavoritePlacesEvent
import com.ucb.mapexplorer.favoritePlaces.presentation.state.FavoritePlacesUIState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritePlacesViewModel : ViewModel() {
    private val _state = MutableStateFlow(FavoritePlacesUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<FavoritePlacesEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: FavoritePlacesEvent) {
        when (event) {
            FavoritePlacesEvent.OnBackClick -> {
                viewModelScope.launch { _effect.emit(FavoritePlacesEffect.NavigateBack) }
            }
            is FavoritePlacesEvent.OnPlaceClick -> {
                viewModelScope.launch { _effect.emit(FavoritePlacesEffect.NavigateToPlaceDetail(event.place)) }
            }
        }
    }
}