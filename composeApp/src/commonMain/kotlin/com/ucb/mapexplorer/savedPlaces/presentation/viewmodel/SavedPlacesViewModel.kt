package com.ucb.mapexplorer.savedPlaces.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.core.session.Session
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.GetGuardadosUseCase
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.ToggleGuardadoUseCase
import com.ucb.mapexplorer.savedPlaces.presentation.state.SavedPlacesEffect
import com.ucb.mapexplorer.savedPlaces.presentation.state.SavedPlacesEvent
import com.ucb.mapexplorer.savedPlaces.presentation.state.SavedPlacesUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SavedPlacesViewModel(
    private val getGuardadosUseCase: GetGuardadosUseCase,
    private val toggleGuardadoUseCase: ToggleGuardadoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SavedPlacesUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SavedPlacesEffect>()
    val effect = _effect.asSharedFlow()

    init { loadGuardados() }

    fun loadGuardados() {
        val uid = Session.uid ?: return
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val guardados = getGuardadosUseCase(uid)
                _state.update { it.copy(guardados = guardados, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun onEvent(event: SavedPlacesEvent) {
        when (event) {
            SavedPlacesEvent.OnBackClick ->
                viewModelScope.launch { _effect.emit(SavedPlacesEffect.NavigateBack) }

            is SavedPlacesEvent.OnPlaceClick ->
                viewModelScope.launch {
                    _effect.emit(SavedPlacesEffect.NavigateToPlaceDetail(event.lugarId))
                }

            is SavedPlacesEvent.OnRemoveGuardado -> removeGuardado(event.lugarId)
        }
    }

    private fun removeGuardado(lugarId: String) {
        val uid = Session.uid ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val lugar = _state.value.guardados.find { it.lugarId == lugarId } ?: return@launch
                toggleGuardadoUseCase(
                    uid, lugarId, lugar.nombre, lugar.categoria,
                    lugar.latitud, lugar.longitud, lugar.iconoCategoria
                )
                val guardados = getGuardadosUseCase(uid)
                _state.update { it.copy(guardados = guardados) }
            } catch (e: Exception) {
                println("Error removiendo guardado: ${e.message}")
            }
        }
    }
}