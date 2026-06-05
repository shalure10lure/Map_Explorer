package com.ucb.mapexplorer.map.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.core.session.Session
import com.ucb.mapexplorer.core.utils.TileUtils
import com.ucb.mapexplorer.map.domain.model.UserLocationModel
import com.ucb.mapexplorer.map.domain.usecase.GetCurrentLocationUseCase
import com.ucb.mapexplorer.map.domain.usecase.GetDiscoveredTilesUseCase
import com.ucb.mapexplorer.map.domain.usecase.UnlockTileUseCase
import com.ucb.mapexplorer.map.presentation.state.MapEffect
import com.ucb.mapexplorer.map.presentation.state.MapEvent
import com.ucb.mapexplorer.map.presentation.state.MapUIState
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.GetNearbyPlacesUseCase
import com.ucb.mapexplorer.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.math.cos
import kotlin.math.sin
class MapViewModel(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val unlockTileUseCase: UnlockTileUseCase,
    private val getDiscoveredTilesUseCase: GetDiscoveredTilesUseCase,
    private val getNearbyPlacesUseCase: GetNearbyPlacesUseCase,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MapUIState())
    val state: StateFlow<MapUIState> = _state.asStateFlow()

    private val _effect = Channel<MapEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var locationJob: Job? = null
    private var lastProcessedTileKey: String? = null

    // Caché para evitar peticiones repetitivas a la API
    private var lastSearchLat: Double = 0.0
    private var lastSearchLon: Double = 0.0

    init {
        onEvent(MapEvent.OnLoadMap)
        loadUserProfile()
    }

    fun onEvent(event: MapEvent) {
        when (event) {
            MapEvent.OnLoadMap -> {
                loadDiscoveredTiles()
                startLocationUpdates()
            }
            is MapEvent.OnLocationUpdated -> {
                handleLocationUpdate(event.latitude, event.longitude)
            }
            MapEvent.OnDismissError -> {
                _state.update { it.copy(errorMessage = null) }
            }
            MapEvent.OnCenterOnUser -> {
                viewModelScope.launch { _effect.send(MapEffect.CenterMapOnUser) }
            }
        }
    }

    private fun loadUserProfile() {
        val uid = Session.uid ?: return
        viewModelScope.launch(Dispatchers.IO) {
            profileRepository.observeProfile(uid).collect { profile ->
                profile?.let {
                    _state.update { it.copy(avatarConfig = profile.avatarConfig) }
                }
            }
        }
    }

    private fun startLocationUpdates() {
        if (locationJob?.isActive == true) return

        locationJob = viewModelScope.launch(Dispatchers.IO) {
            getCurrentLocationUseCase()
                .catch { e ->
                    _effect.send(MapEffect.ShowError("Error GPS: ${e.message}"))
                }
                .collectLatest { location ->
                    _state.update {
                        it.copy(
                            userLat = location.latitude,
                            userLng = location.longitude,
                            isLoadingLocation = false
                        )
                    }
                    tryUnlockTile(location.latitude, location.longitude)
                }
        }
    }

    private fun handleLocationUpdate(lat: Double, lon: Double) {
        _state.update {
            it.copy(
                userLat = lat,
                userLng = lon,
                isLoadingLocation = false
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            tryUnlockTile(lat, lon)
            loadDiscoveredTiles()
        }
    }

    private suspend fun tryUnlockTile(lat: Double, lon: Double) {
        val uid = Session.uid ?: return
        val (tileX, tileY) = TileUtils.latLngToTile(lat, lon)
        val tileKey = "${tileX}_${tileY}"

        if (tileKey == lastProcessedTileKey) return
        lastProcessedTileKey = tileKey

        try {
            val isNewTile = unlockTileUseCase(uid, UserLocationModel(lat, lon))
            if (isNewTile) {
                loadDiscoveredTiles()
                _effect.send(MapEffect.NewTileDiscovered(tileX, tileY))
            }
        } catch (e: Exception) {
            _effect.send(MapEffect.ShowSnackbar("Error: ${e.message}"))
        }
    }

    private fun loadDiscoveredTiles() {
        val uid = Session.uid ?: return
        val currentLat = state.value.userLat
        val currentLon = state.value.userLng

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val tiles = getDiscoveredTilesUseCase(uid)

                // Cálculo de distancia para optimizar API Overpass
                val dist = haversine(currentLat, currentLon, lastSearchLat, lastSearchLon)

                if (dist > 500 || lastSearchLat == 0.0) {
                    _state.update { it.copy(isLoadingTiles = true) }
                    val allPlaces = getNearbyPlacesUseCase(currentLat, currentLon)
                    lastSearchLat = currentLat
                    lastSearchLon = currentLon

                    val filteredPlaces = allPlaces.filter { lugar ->
                        val (placeX, placeY) = TileUtils.latLngToTile(lugar.latitude, lugar.longitude)
                        tiles.any { tile -> tile.tileX == placeX && tile.tileY == placeY }
                    }

                    _state.update {
                        it.copy(
                            discoveredTiles = tiles,
                            nearbyPlacesInMap = filteredPlaces,
                            totalTilesUnlocked = tiles.size,
                            experience = tiles.size * 10,
                            level = (tiles.size / 10) + 1,
                            isLoadingTiles = false
                        )
                    }
                } else {
                    _state.update { it.copy(discoveredTiles = tiles) }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoadingTiles = false) }
            }
        }
    }

    private fun haversine(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {

        val r = 6371000.0

        val dLat = (lat2 - lat1) * kotlin.math.PI / 180.0
        val dLon = (lon2 - lon1) * kotlin.math.PI / 180.0

        val a =
            sin(dLat / 2) * sin(dLat / 2) +
                    cos(lat1 * kotlin.math.PI / 180.0) *
                    cos(lat2 * kotlin.math.PI / 180.0) *
                    sin(dLon / 2) * sin(dLon / 2)

        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    fun centerMapOnLocation(lat: Double, lon: Double) {
        viewModelScope.launch {
            _effect.send(MapEffect.CenterMapOnLocation(lat, lon))
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationJob?.cancel()
    }
}
