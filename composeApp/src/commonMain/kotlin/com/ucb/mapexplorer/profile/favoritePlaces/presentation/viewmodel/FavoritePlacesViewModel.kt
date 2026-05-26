package com.ucb.mapexplorer.profile.favoritePlaces.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.ucb.mapexplorer.profile.favoritePlaces.presentation.state.*

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
