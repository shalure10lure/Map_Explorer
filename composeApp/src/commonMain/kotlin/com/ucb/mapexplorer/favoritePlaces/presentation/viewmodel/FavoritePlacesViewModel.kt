package com.ucb.mapexplorer.favoritePlaces.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.core.session.Session
import com.ucb.mapexplorer.favoritePlaces.presentation.state.FavoritePlacesEffect
import com.ucb.mapexplorer.favoritePlaces.presentation.state.FavoritePlacesEvent
import com.ucb.mapexplorer.favoritePlaces.presentation.state.FavoritePlacesUIState
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.GetFavoritosUseCase
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.ToggleFavoritoUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritePlacesViewModel(
    private val getFavoritosUseCase: GetFavoritosUseCase,
    private val toggleFavoritoUseCase: ToggleFavoritoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritePlacesUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<FavoritePlacesEffect>()
    val effect = _effect.asSharedFlow()

    init { loadFavoritos() }

    fun loadFavoritos() {
        val uid = Session.uid ?: return
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val favoritos = getFavoritosUseCase(uid)
                _state.update { it.copy(favoritos = favoritos, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun onEvent(event: FavoritePlacesEvent) {
        when (event) {
            FavoritePlacesEvent.OnBackClick ->
                viewModelScope.launch { _effect.emit(FavoritePlacesEffect.NavigateBack) }

            is FavoritePlacesEvent.OnPlaceClick ->
                viewModelScope.launch {
                    _effect.emit(FavoritePlacesEffect.NavigateToPlaceDetail(event.lugarId))
                }

            is FavoritePlacesEvent.OnRemoveFavorito -> removeFavorito(event.lugarId)
        }
    }

    private fun removeFavorito(lugarId: String) {
        val uid = Session.uid ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val lugar = _state.value.favoritos.find { it.lugarId == lugarId } ?: return@launch
                toggleFavoritoUseCase(
                    uid, lugarId, lugar.nombre, lugar.categoria,
                    lugar.latitud, lugar.longitud, lugar.iconoCategoria
                )
                // Recargar lista
                val favoritos = getFavoritosUseCase(uid)
                _state.update { it.copy(favoritos = favoritos) }
            } catch (e: Exception) {
                println("Error removiendo favorito: ${e.message}")
            }
        }
    }
}