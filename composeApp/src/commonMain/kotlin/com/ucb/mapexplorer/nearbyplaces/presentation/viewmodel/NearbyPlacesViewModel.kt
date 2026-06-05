package com.ucb.mapexplorer.nearbyplaces.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.GetPlaceDetailUseCase
import com.ucb.mapexplorer.nearbyplaces.presentation.state.NearbyPlacesEvent
import com.ucb.mapexplorer.nearbyplaces.presentation.state.NearbyPlacesUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NearbyPlacesViewModel(
    private val getPlaceDetailUseCase: GetPlaceDetailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NearbyPlacesUIState())
    val state: StateFlow<NearbyPlacesUIState> = _state.asStateFlow()

    fun onEvent(event: NearbyPlacesEvent) {
        when (event) {
            is NearbyPlacesEvent.OnSearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
            }

            is NearbyPlacesEvent.OnCategorySelected -> {
                _state.update {
                    it.copy(selectedCategory = if (it.selectedCategory == event.category) null else event.category)
                }
            }

            // 🎯 Gatilla la carga del detalle del Félix Capriles o cualquier sitio seleccionado
            is NearbyPlacesEvent.OnSelectPlace -> {
                loadPlaceDetail(event.placeId)
            }

            NearbyPlacesEvent.OnDismissDetail -> {
                _state.update { it.copy(selectedPlace = null) }
            }

            NearbyPlacesEvent.OnDismissError -> {
                _state.update { it.copy(errorMessage = null) }
            }

            // Ignoramos OnLoadPlaces y OnRetry ya que la sincronización espacial por Tiles
            // ahora la gestiona el MapViewModel reactivamente con el GPS
            is NearbyPlacesEvent.OnLoadPlaces -> { /* Delegado al ciclo del mapa */ }
            NearbyPlacesEvent.OnRetry -> { /* Delegado al ciclo del mapa */ }
        }
    }

    /**
     * ── Carga de Detalles desde el Servidor / API ───────────────────────────
     * Obtiene la descripción detallada, imágenes y datos extras del lugar específico
     * usando su ID único.
     */
    private fun loadPlaceDetail(id: String) {
        _state.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val detail = getPlaceDetailUseCase(id) // O getPlaceDetailUseCase.execute(id) según tu firma
                _state.update {
                    it.copy(
                        selectedPlace = detail,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "No se pudo obtener el detalle: ${e.message}"
                    )
                }
            }
        }
    }
}