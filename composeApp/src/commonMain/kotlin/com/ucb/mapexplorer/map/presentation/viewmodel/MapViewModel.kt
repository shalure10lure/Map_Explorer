package com.ucb.mapexplorer.map.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.core.session.Session
import com.ucb.mapexplorer.core.utils.TileUtils
import com.ucb.mapexplorer.dangerzone.domain.usecase.CheckDangerZoneUseCase
import com.ucb.mapexplorer.dangerzone.domain.usecase.SyncDangerZonesUseCase
import com.ucb.mapexplorer.map.domain.model.UserLocationModel
import com.ucb.mapexplorer.map.domain.usecase.GetCurrentLocationUseCase
import com.ucb.mapexplorer.map.domain.usecase.GetDiscoveredTilesUseCase
import com.ucb.mapexplorer.map.domain.usecase.SyncMapHistoryUseCase
import com.ucb.mapexplorer.map.domain.usecase.UnlockTileUseCase
import com.ucb.mapexplorer.map.presentation.state.MapEffect
import com.ucb.mapexplorer.map.presentation.state.MapEvent
import com.ucb.mapexplorer.map.presentation.state.MapUIState
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.GetNearbyPlacesUseCase
import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel
import com.ucb.mapexplorer.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.*

class MapViewModel(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val unlockTileUseCase: UnlockTileUseCase,
    private val getDiscoveredTilesUseCase: GetDiscoveredTilesUseCase,
    private val getNearbyPlacesUseCase: GetNearbyPlacesUseCase,
    private val syncMapHistoryUseCase: SyncMapHistoryUseCase,
    private val profileRepository: ProfileRepository,
    private val checkDangerZoneUseCase: CheckDangerZoneUseCase,
    private val syncDangerZonesUseCase: SyncDangerZonesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MapUIState())
    val state: StateFlow<MapUIState> = _state.asStateFlow()

    private val _effect = Channel<MapEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var locationJob: Job? = null
    private var profileJob: Job? = null
    private var lastProcessedTileKey: String? = null
    private var lastSearchLat: Double = 0.0
    private var lastSearchLon: Double = 0.0

    private var lastDangerAlertTile: String? = null
    private var tilesLoaded  = false    // ← NUEVO: evita la segunda carga al inicio
    private var syncDone     = false    // ← NUEVO: sincronización solo una vez por sesión

    init {
        onEvent(MapEvent.OnLoadMap)
    }

    fun onEvent(event: MapEvent) {
        when (event) {
            MapEvent.OnLoadMap            -> {
                loadUserProfile()
                if (!tilesLoaded) loadDiscoveredTiles()
                startLocationUpdates()
                syncDangerZones()
            }
            is MapEvent.OnLocationUpdated -> handleLocationUpdate(event.latitude, event.longitude)
            MapEvent.OnDismissError       -> _state.update { it.copy(errorMessage = null) }
            MapEvent.OnCenterOnUser       -> viewModelScope.launch { _effect.send(MapEffect.CenterMapOnUser) }
            is MapEvent.OnAvatarUpdated   -> updateAvatarFromProfile(event.config)
            MapEvent.OnDismissDangerAlert -> _state.update { it.copy(showDangerAlert = false) }
        }
    }

    private fun syncDangerZones() {
        viewModelScope.launch(Dispatchers.IO) {
            try { syncDangerZonesUseCase() } catch (e: Exception) { /* best effort */ }
        }
    }

    private suspend fun checkDanger(lat: Double, lon: Double) {
        val tileKey = "${TileUtils.lonToTileX(lon)}_${TileUtils.latToTileY(lat)}"
        if (tileKey == lastDangerAlertTile) return  // ya alertamos en este tile

        val zona = checkDangerZoneUseCase(lat, lon)
        if (zona != null) {
            lastDangerAlertTile = tileKey
            _state.update { it.copy(dangerZoneActual = zona, showDangerAlert = true) }
        }
    }

    private fun loadUserProfile() {
        val uid = Session.uid ?: return
        profileJob?.cancel()
        profileJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val profile = profileRepository.getProfile(uid)
                if (profile != null) {
                    _state.update { it.copy(avatarConfig = profile.avatarConfig) }
                }
            } catch (e: Exception) {
                if (e !is CancellationException) e.printStackTrace()
            }
        }
    }

    fun updateAvatarFromProfile(config: AvatarConfigModel) {
        _state.update { it.copy(avatarConfig = config) }
    }

    private fun startLocationUpdates() {
        if (locationJob?.isActive == true) return
        locationJob = viewModelScope.launch(Dispatchers.IO) {
            getCurrentLocationUseCase()
                .catch { e ->
                    if (e !is CancellationException) {
                        _effect.send(MapEffect.ShowError("Error GPS: ${e.message}"))
                    }
                }
                .collectLatest { location ->
                    _state.update {
                        it.copy(
                            userLat           = location.latitude,
                            userLng           = location.longitude,
                            isLoadingLocation = false
                        )
                    }
                    tryUnlockTile(location.latitude, location.longitude)
                    checkDanger(location.latitude, location.longitude)
                }
        }
    }

    private fun handleLocationUpdate(lat: Double, lon: Double) {
        _state.update { it.copy(userLat = lat, userLng = lon, isLoadingLocation = false) }
        viewModelScope.launch(Dispatchers.IO) {
            tryUnlockTile(lat, lon)
            checkDanger(lat, lon)
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
            val isNew = unlockTileUseCase(uid, UserLocationModel(lat, lon))
            if (isNew) {
                loadDiscoveredTiles()
                _effect.send(MapEffect.NewTileDiscovered(tileX, tileY))
            }
        } catch (e: Exception) {
            if (e !is CancellationException) _effect.send(MapEffect.ShowSnackbar("Error: ${e.message}"))
        }
    }

    private fun loadDiscoveredTiles() {
        val uid        = Session.uid ?: return
        val currentLat = state.value.userLat
        val currentLon = state.value.userLng

        viewModelScope.launch(Dispatchers.IO) {
            try {
                syncMapHistoryUseCase(uid)

                val tiles = getDiscoveredTilesUseCase(uid)
                val dist  = haversine(currentLat, currentLon, lastSearchLat, lastSearchLon)

                if (dist > 500 || lastSearchLat == 0.0) {
                    _state.update { it.copy(isLoadingTiles = true) }
                    val allPlaces = try {
                        getNearbyPlacesUseCase(currentLat, currentLon)
                    } catch (e: Exception) {
                        emptyList()
                    }

                    lastSearchLat = currentLat
                    lastSearchLon = currentLon
                    val filteredPlaces = allPlaces.filter { lugar ->
                        val (placeX, placeY) = TileUtils.latLngToTile(lugar.latitude, lugar.longitude)
                        tiles.any { it.tileX == placeX && it.tileY == placeY } && lugar.name.isNotBlank()
                    }

                    _state.update {
                        it.copy(
                            discoveredTiles    = tiles,
                            nearbyPlacesInMap  = filteredPlaces,
                            totalTilesUnlocked = tiles.size,
                            experience         = tiles.size * 10,
                            level              = (tiles.size / 10) + 1,
                            isLoadingTiles     = false
                        )
                    }
                } else {
                    _state.update {
                        it.copy(
                            discoveredTiles = tiles,
                            totalTilesUnlocked = tiles.size
                        )
                    }
                }
            } catch (e: Exception) {
                if (e !is CancellationException) {
                    e.printStackTrace()
                    _state.update { it.copy(isLoadingTiles = false) }
                }
            }
        }
    }

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r    = 6371000.0
        val dLat = (lat2 - lat1) * PI / 180.0
        val dLon = (lon2 - lon1) * PI / 180.0
        val a    = sin(dLat / 2).pow(2) +
                cos(lat1 * PI / 180.0) * cos(lat2 * PI / 180.0) * sin(dLon / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    fun centerMapOnLocation(lat: Double, lon: Double) {
        _state.update { it.copy(cameraTarget = Pair(lat, lon)) }
        viewModelScope.launch { _effect.send(MapEffect.CenterMapOnLocation(lat, lon)) }
    }

    override fun onCleared() {
        super.onCleared()
        locationJob?.cancel()
        profileJob?.cancel()
    }
}
