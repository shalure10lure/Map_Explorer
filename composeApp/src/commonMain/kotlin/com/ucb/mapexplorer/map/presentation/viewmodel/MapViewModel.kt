package com.ucb.mapexplorer.map.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.core.session.Session
import com.ucb.mapexplorer.core.utils.TileUtils
import com.ucb.mapexplorer.dangerzone.domain.usecase.CheckDangerZoneUseCase
import com.ucb.mapexplorer.dangerzone.domain.usecase.SyncDangerZonesUseCase
import com.ucb.mapexplorer.map.domain.model.TileModel
import com.ucb.mapexplorer.map.domain.model.UserLocationModel
import com.ucb.mapexplorer.map.domain.usecase.GetCurrentLocationUseCase
import com.ucb.mapexplorer.map.domain.usecase.GetDiscoveredTilesUseCase
import com.ucb.mapexplorer.map.domain.usecase.SyncMapHistoryUseCase
import com.ucb.mapexplorer.map.domain.usecase.UnlockTileUseCase
import com.ucb.mapexplorer.map.presentation.state.MapEffect
import com.ucb.mapexplorer.map.presentation.state.MapEvent
import com.ucb.mapexplorer.map.presentation.state.MapUIState
import com.ucb.mapexplorer.nearbyplaces.domain.model.PlaceModel
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.GetNearbyPlacesUseCase
import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel
import com.ucb.mapexplorer.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
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

    // ── Jobs ──────────────────────────────────────────────────────────────
    private var locationJob: Job? = null
    private var profileJob: Job? = null
    private var placesJob: Job? = null

    // ── Guards ────────────────────────────────────────────────────────────
    private var lastProcessedTileKey: String? = null
    private val lastDangerAlerts = mutableMapOf<String, Long>()
    private val ALERT_INTERVAL_MS = 15 * 60 * 1000L
    private var lastPlacesSearchLat: Double = 0.0
    private var lastPlacesSearchLon: Double = 0.0

    // ── Flags ─────────────────────────────────────────────────────────────
    private var initialSyncDone: Boolean = false
    private var initialLoadDone: Boolean = false
    private var placesLoadedOnce: Boolean = false

    private val PLACES_RADIUS_METERS = 200

    private val PLACES_REFRESH_DISTANCE_METERS = 50.0

    companion object {
        private var cachedTiles: List<TileModel>? = null
        private var cachedPlaces: List<PlaceModel>? = null
        private var cachedUid: String? = null

        fun invalidateCache() {
            cachedTiles = null
            cachedPlaces = null
            cachedUid = null
        }
    }

    init {
        onEvent(MapEvent.OnLoadMap)
    }

    fun onEvent(event: MapEvent) {
        when (event) {
            MapEvent.OnLoadMap -> {
                loadProfileIfNeeded()
                loadTilesIfNeeded()
                startLocationUpdatesIfNeeded()
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        syncDangerZonesUseCase()
                    } catch (_: Exception) { }
                }
            }
            is MapEvent.OnLocationUpdated -> handleLocationUpdate(event.latitude, event.longitude)
            MapEvent.OnDismissError -> _state.update { it.copy(errorMessage = null) }
            MapEvent.OnCenterOnUser -> viewModelScope.launch { _effect.send(MapEffect.CenterMapOnUser) }
            is MapEvent.OnAvatarUpdated -> _state.update { it.copy(avatarConfig = event.config) }
            MapEvent.OnDismissDangerAlert -> {
                _state.update { it.copy(showDangerAlert = false, dangerZoneActual = null) }
            }
        }
    }

    // ── 1. Perfil ─────────────────────────────────────────────────────────
    private fun loadProfileIfNeeded() {
        if (_state.value.avatarConfig != AvatarConfigModel()) return
        val uid = Session.uid ?: return

        profileJob?.cancel()
        profileJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val profile = profileRepository.getProfile(uid)
                profile?.let { _state.update { s -> s.copy(avatarConfig = it.avatarConfig) } }
            } catch (e: Exception) {
                if (e !is CancellationException) e.printStackTrace()
            }
        }
    }

    fun updateAvatarFromProfile(config: AvatarConfigModel) {
        _state.update { it.copy(avatarConfig = config) }
    }

    // ── 2. Carga inicial de tiles ─────────────────────────────────────────
    // CAMBIO CLAVE 3: Se muestra Room inmediatamente, Firebase sync en background
    private fun loadTilesIfNeeded() {
        if (initialLoadDone) return
        val uid = Session.uid ?: return

        initialLoadDone = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _state.update { it.copy(isLoadingTiles = true) }

                // PASO 1: Leer Room inmediatamente (sin esperar Firebase)
                // Esto hace que el mapa se muestre al instante con datos locales
                val localTiles = if (cachedTiles != null && cachedUid == uid) {
                    cachedTiles!!
                } else {
                    getDiscoveredTilesUseCase(uid)
                }

                // PASO 2: Mostrar tiles locales en UI sin esperar sync
                if (localTiles.isNotEmpty()) {
                    cachedTiles = localTiles
                    cachedUid = uid
                    _state.update {
                        it.copy(
                            discoveredTiles = localTiles,
                            totalTilesUnlocked = localTiles.size,
                            experience = localTiles.size * 10,
                            level = (localTiles.size / 10) + 1,
                            isLoadingTiles = false
                        )
                    }
                }

                // PASO 3: Sync Firebase en background SOLO si Room estaba vacío
                if (localTiles.isEmpty() && !initialSyncDone) {
                    initialSyncDone = true
                    try {
                        syncMapHistoryUseCase(uid)
                        // Re-leer Room después del sync
                        val syncedTiles = getDiscoveredTilesUseCase(uid)
                        cachedTiles = syncedTiles
                        cachedUid = uid
                        _state.update {
                            it.copy(
                                discoveredTiles = syncedTiles,
                                totalTilesUnlocked = syncedTiles.size,
                                experience = syncedTiles.size * 10,
                                level = (syncedTiles.size / 10) + 1,
                                isLoadingTiles = false
                            )
                        }
                    } catch (_: Exception) {
                        _state.update { it.copy(isLoadingTiles = false) }
                    }
                } else {
                    initialSyncDone = true
                    _state.update { it.copy(isLoadingTiles = false) }
                }

                // PASO 4: Cargar lugares si ya tenemos GPS
                val lat = state.value.userLat
                val lon = state.value.userLng
                if (lat != 0.0 && lon != 0.0 && !placesLoadedOnce) {
                    loadNearbyPlaces(state.value.discoveredTiles, lat, lon, forceRefresh = true)
                }

            } catch (e: Exception) {
                if (e !is CancellationException) {
                    initialLoadDone = false
                    _state.update { it.copy(isLoadingTiles = false) }
                }
            }
        }
    }

    // ── 3. GPS ────────────────────────────────────────────────────────────
    private fun startLocationUpdatesIfNeeded() {
        if (locationJob?.isActive == true) return

        locationJob = viewModelScope.launch(Dispatchers.IO) {
            getCurrentLocationUseCase()
                .catch { e ->
                    if (e !is CancellationException)
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
                    checkDangerIfNeeded(location.latitude, location.longitude)
                }
        }
    }

    private fun handleLocationUpdate(lat: Double, lon: Double) {
        _state.update { it.copy(userLat = lat, userLng = lon, isLoadingLocation = false) }

        viewModelScope.launch(Dispatchers.IO) {
            tryUnlockTile(lat, lon)
            checkDangerIfNeeded(lat, lon)

            // Cargar lugares en la primera ubicación válida
            if (!placesLoadedOnce && initialLoadDone) {
                val tiles = state.value.discoveredTiles
                loadNearbyPlaces(tiles, lat, lon, forceRefresh = true)
            }
        }
    }

    // ── 4. Desbloqueo de tiles ────────────────────────────────────────────
    private suspend fun tryUnlockTile(lat: Double, lon: Double) {
        val uid = Session.uid ?: return
        val (tileX, tileY) = TileUtils.latLngToTile(lat, lon)
        val tileKey = "${tileX}_${tileY}"

        if (tileKey == lastProcessedTileKey) return
        lastProcessedTileKey = tileKey

        try {
            val isNew = unlockTileUseCase(uid, UserLocationModel(lat, lon))
            if (isNew) {
                onNewTileDiscovered(tileX, tileY, lat, lon)
            }
        } catch (e: Exception) {
            if (e !is CancellationException)
                _effect.send(MapEffect.ShowSnackbar("Error guardando tile: ${e.message}"))
        }
    }

    private fun onNewTileDiscovered(tileX: Int, tileY: Int, lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val uid = Session.uid ?: return@launch
                val tiles = getDiscoveredTilesUseCase(uid)
                cachedTiles = tiles
                cachedUid = uid

                _state.update {
                    it.copy(
                        discoveredTiles = tiles,
                        totalTilesUnlocked = tiles.size,
                        experience = tiles.size * 10,
                        level = (tiles.size / 10) + 1
                    )
                }

                // CAMBIO: usar PLACES_REFRESH_DISTANCE_METERS reducido
                val dist = haversine(lat, lon, lastPlacesSearchLat, lastPlacesSearchLon)
                if (dist > PLACES_REFRESH_DISTANCE_METERS || !placesLoadedOnce) {
                    loadNearbyPlaces(tiles, lat, lon, forceRefresh = false)
                }

                _effect.send(MapEffect.NewTileDiscovered(tileX, tileY))

            } catch (e: Exception) {
                if (e !is CancellationException) e.printStackTrace()
            }
        }
    }

    // ── 5. Lugares cercanos ───────────────────────────────────────────────
    // CAMBIO CLAVE 4: Radio de búsqueda reducido a 200m, refresh más inteligente
    private fun loadNearbyPlaces(
        tiles: List<TileModel>,
        lat: Double,
        lon: Double,
        forceRefresh: Boolean = false
    ) {
        val dist = haversine(lat, lon, lastPlacesSearchLat, lastPlacesSearchLon)

        // Si ya tenemos caché y no nos movimos más de PLACES_REFRESH_DISTANCE_METERS
        if (!forceRefresh && placesLoadedOnce && dist < PLACES_REFRESH_DISTANCE_METERS && cachedPlaces != null) {
            val filtered = filterPlacesByTiles(cachedPlaces!!, tiles)
            _state.update { it.copy(nearbyPlacesInMap = filtered) }
            return
        }

        placesJob?.cancel()

        placesJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                lastPlacesSearchLat = lat
                lastPlacesSearchLon = lon

                // CAMBIO: Radio reducido a 200m en lugar de 1500m
                val allPlaces = getNearbyPlacesUseCase(lat, lon, PLACES_RADIUS_METERS)

                cachedPlaces = allPlaces
                val filtered = filterPlacesByTiles(allPlaces, tiles)

                placesLoadedOnce = true
                _state.update { it.copy(nearbyPlacesInMap = filtered) }

            } catch (e: Exception) {
                if (e !is CancellationException) {
                    println("Overpass falló: ${e.message}")
                }
            }
        }
    }

    private fun filterPlacesByTiles(places: List<PlaceModel>, tiles: List<TileModel>): List<PlaceModel> {
        if (tiles.isEmpty()) return emptyList()
        val tileKeys = tiles.map { "${it.tileX}_${it.tileY}" }.toHashSet()
        return places.filter { lugar ->
            val (px, py) = TileUtils.latLngToTile(lugar.latitude, lugar.longitude)
            "${px}_${py}" in tileKeys && lugar.name.isNotBlank()
        }
    }

    // ── 6. Zonas peligrosas ───────────────────────────────────────────────
    private suspend fun checkDangerIfNeeded(lat: Double, lon: Double) {
        val zona = checkDangerZoneUseCase(lat, lon)
        if (zona != null) {
            val now: Long = Clock.System.now().toEpochMilliseconds()
            val lastAlertTime: Long = lastDangerAlerts[zona.zonaId] ?: 0L
            val timeElapsed = now - lastAlertTime

            // 4. Comparamos asegurándonos de que ambos sean Long
            if (timeElapsed >= ALERT_INTERVAL_MS) {
                lastDangerAlerts[zona.zonaId] = now
                _state.update { it.copy(dangerZoneActual = zona, showDangerAlert = true) }
                _effect.send(MapEffect.DangerZoneAlertTriggered(zona))
            }
        }
    }

    // ── 7. Cámara ─────────────────────────────────────────────────────────
    fun centerMapOnLocation(lat: Double, lon: Double) {
        _state.update { it.copy(cameraTarget = Pair(lat, lon)) }
        viewModelScope.launch { _effect.send(MapEffect.CenterMapOnLocation(lat, lon)) }
    }

    // ── 8. Utilidades ─────────────────────────────────────────────────────
    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = (lat2 - lat1) * PI / 180.0
        val dLon = (lon2 - lon1) * PI / 180.0
        val a = sin(dLat / 2).pow(2) +
                cos(lat1 * PI / 180.0) * cos(lat2 * PI / 180.0) * sin(dLon / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    override fun onCleared() {
        super.onCleared()
        locationJob?.cancel()
        profileJob?.cancel()
        placesJob?.cancel()
    }
}
