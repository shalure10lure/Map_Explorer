package com.ucb.mapexplorer.nearbyplaces.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.core.session.Session
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.GetNearbyPlacesUseCase
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.GetPlaceDetailUseCase
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.SyncLugarDescubiertoUseCase
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.SyncLugarVisitadoUseCase
import com.ucb.mapexplorer.nearbyplaces.presentation.state.NearbyPlacesEffect
import com.ucb.mapexplorer.nearbyplaces.presentation.state.NearbyPlacesEvent
import com.ucb.mapexplorer.nearbyplaces.presentation.state.NearbyPlacesUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NearbyPlacesViewModel(
    private val getNearbyPlacesUseCase: GetNearbyPlacesUseCase,
    private val getPlaceDetailUseCase: GetPlaceDetailUseCase,
    private val syncLugarDescubiertoUseCase: SyncLugarDescubiertoUseCase,
    private val syncLugarVisitadoUseCase: SyncLugarVisitadoUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NearbyPlacesUIState())
    val state: StateFlow<NearbyPlacesUIState> = _state.asStateFlow()

    private val _effect = Channel<NearbyPlacesEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: NearbyPlacesEvent) {
        when (event) {
            is NearbyPlacesEvent.OnLoadPlaces       -> loadPlaces(event.lat, event.lon)
            is NearbyPlacesEvent.OnSelectPlace      -> selectPlace(event.placeId)
            NearbyPlacesEvent.OnDismissDetail       -> _state.update { it.copy(selectedPlace = null) }
            is NearbyPlacesEvent.OnSearchQueryChanged -> _state.update { it.copy(searchQuery = event.query) }
            is NearbyPlacesEvent.OnCategorySelected -> _state.update { it.copy(selectedCategory = event.category) }
            NearbyPlacesEvent.OnDismissError        -> _state.update { it.copy(errorMessage = null) }
            NearbyPlacesEvent.OnRetry               -> loadPlaces(_state.value.userLat, _state.value.userLon)
        }
    }

    private fun loadPlaces(lat: Double, lon: Double) {
        _state.update { it.copy(isLoading = true, userLat = lat, userLon = lon) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val places = getNearbyPlacesUseCase(lat, lon)
                _state.update { it.copy(places = places, isLoading = false) }

                // Sincronizar lugares descubiertos con Firebase
                val uid = Session.uid ?: return@launch
                places.forEach { place ->
                    syncLugarDescubiertoUseCase(uid, place)
                }

            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                _effect.send(NearbyPlacesEffect.ShowError(e.message ?: "Error desconocido"))
            }
        }
    }

    private fun selectPlace(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val place = getPlaceDetailUseCase(id)
                ?: _state.value.places.find { it.id == id }

            _state.update { it.copy(selectedPlace = place) }

            // Sincronizar lugar visitado con Firebase al abrir detalle
            val uid = Session.uid ?: return@launch
            place?.let { syncLugarVisitadoUseCase(uid, it) }
        }
    }
}