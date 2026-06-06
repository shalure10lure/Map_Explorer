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
    private val profileRepository: ProfileRepository
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

    init {
        loadUserProfile()
        onEvent(MapEvent.OnLoadMap)
    }

    fun onEvent(event: MapEvent) {
        when (event) {
            MapEvent.OnLoadMap            -> { loadDiscoveredTiles(); startLocationUpdates() }
            is MapEvent.OnLocationUpdated -> handleLocationUpdate(event.latitude, event.longitude)
            MapEvent.OnDismissError       -> _state.update { it.copy(errorMessage = null) }
            MapEvent.OnCenterOnUser       -> viewModelScope.launch { _effect.send(MapEffect.CenterMapOnUser) }
        }
    }

    /**
     * Usa el mismo ProfileRepository que OwnProfileViewModel.
     * observeProfile() ya funciona correctamente — emite el perfil al suscribirse
     * (gracias al onStart { getProfile(uid)?.let { emit(it) } } en el repo).
     *
     * Solo tomamos el PRIMER valor con .first() para no mantener un listener
     * activo que se cancele al navegar.
     */
    private fun loadUserProfile() {
        val uid = Session.uid ?: return

        profileJob?.cancel()
        profileJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                // getProfile() es una llamada única — no se cancela al navegar
                val profile = profileRepository.getProfile(uid)
                if (profile != null) {
                    _state.update { it.copy(avatarConfig = profile.avatarConfig) }
                    println("✅ MapViewModel: avatar cargado → ${profile.avatarConfig.body.name}|${profile.avatarConfig.hat.name}|${profile.avatarConfig.accessory.name}")
                }
            } catch (e: CancellationException) {
                throw e // re-lanzar para que Coroutines maneje bien la cancelación
            } catch (e: Exception) {
                println("MapViewModel: no se pudo cargar el avatar: ${e.message}")
                // Silencioso — el marcador usará el avatar por defecto
            }
        }
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
                }
        }
    }

    private fun handleLocationUpdate(lat: Double, lon: Double) {
        _state.update { it.copy(userLat = lat, userLng = lon, isLoadingLocation = false) }
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
            val isNew = unlockTileUseCase(uid, UserLocationModel(lat, lon))
            if (isNew) {
                loadDiscoveredTiles()
                _effect.send(MapEffect.NewTileDiscovered(tileX, tileY))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _effect.send(MapEffect.ShowSnackbar("Error: ${e.message}"))
        }
    }

    private fun loadDiscoveredTiles() {
        val uid        = Session.uid ?: return
        val currentLat = state.value.userLat
        val currentLon = state.value.userLng

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val tiles = getDiscoveredTilesUseCase(uid)
                val dist  = haversine(currentLat, currentLon, lastSearchLat, lastSearchLon)

                if (dist > 500 || lastSearchLat == 0.0) {
                    _state.update { it.copy(isLoadingTiles = true) }

                    val allPlaces = try {
                        getNearbyPlacesUseCase(currentLat, currentLon)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        println("❌ Overpass error: ${e.message}")
                        emptyList()
                    }

                    lastSearchLat = currentLat
                    lastSearchLon = currentLon

                    val filteredPlaces = allPlaces.filter { lugar ->
                        val (placeX, placeY) = TileUtils.latLngToTile(lugar.latitude, lugar.longitude)
                        val enTile = tiles.any { it.tileX == placeX && it.tileY == placeY }
                        enTile && lugar.name.isNotBlank() && lugar.name != "Lugar"
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
                    _state.update { it.copy(discoveredTiles = tiles) }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.update { it.copy(isLoadingTiles = false) }
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
    fun updateAvatarFromProfile(config: AvatarConfigModel) {
        _state.update { it.copy(avatarConfig = config) }
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