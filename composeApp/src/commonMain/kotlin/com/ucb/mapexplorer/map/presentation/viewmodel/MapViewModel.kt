package com.ucb.mapexplorer.map.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.core.session.Session
import com.ucb.mapexplorer.core.utils.TileUtils
import com.ucb.mapexplorer.dangerzone.domain.usecase.CheckDangerZoneUseCase
import com.ucb.mapexplorer.dangerzone.domain.usecase.SyncDangerZonesUseCase
import com.ucb.mapexplorer.friends.domain.usecase.ObserveFriendRequestsUseCase
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
    private val syncDangerZonesUseCase: SyncDangerZonesUseCase,
    private val observeFriendRequestsUseCase: ObserveFriendRequestsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MapUIState())
    val state: StateFlow<MapUIState> = _state.asStateFlow()

    private val _effect = Channel<MapEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    // ── Jobs ──────────────────────────────────────────────────────────────────
    private var locationJob: Job? = null
    private var profileJob:  Job? = null
    private var placesJob:   Job? = null   // Job separado para lugares, cancelable

    // ── Guards: evitan trabajo duplicado ─────────────────────────────────────
    private var lastProcessedTileKey:  String? = null
    private val lastDangerAlerts = mutableMapOf<String, Long>() // zonaId -> timestamp
    private val ALERT_INTERVAL_MS = 15 * 60 * 1000L // 15 minutos
    private var lastPlacesSearchLat:   Double  = 0.0
    private var lastPlacesSearchLon:   Double  = 0.0

    // ── Flags de estado de carga ──────────────────────────────────────────────
    private var initialSyncDone:       Boolean = false  // Firebase sync solo 1 vez
    private var initialLoadDone:       Boolean = false  // carga de tiles solo 1 vez al inicio
    private var placesLoadedOnce:      Boolean = false  // lugares cargados al menos 1 vez

    companion object {
        // Caché estático: sobrevive recomposiciones y navegaciones
        // Se limpia solo cuando cambia el uid (logout)
        private var cachedTiles: List<TileModel>? = null
        private var cachedPlaces: List<PlaceModel>? = null
        private var cachedUid: String? = null

        fun invalidateCache() {
            cachedTiles  = null
            cachedPlaces = null
            cachedUid    = null
        }
    }

    init {
        onEvent(MapEvent.OnLoadMap)
    }

    // ── Entrada de eventos ────────────────────────────────────────────────────

    fun onEvent(event: MapEvent) {
        when (event) {
            MapEvent.OnLoadMap -> {
                loadProfileIfNeeded()
                loadTilesIfNeeded()
                startLocationUpdatesIfNeeded()
                startObservingFriendRequests()
                // Sync primero, LUEGO arrancar location
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        syncDangerZonesUseCase()
                        println("✅ Sync peligro completado, arrancando GPS")
                    } catch (_: Exception) { }
                    // El GPS ya corre independiente, esto solo asegura que el sync ocurra
                }
            }
            is MapEvent.OnLocationUpdated -> handleLocationUpdate(event.latitude, event.longitude)
            MapEvent.OnDismissError       -> _state.update { it.copy(errorMessage = null) }
            MapEvent.OnCenterOnUser       -> viewModelScope.launch { _effect.send(MapEffect.CenterMapOnUser) }
            is MapEvent.OnAvatarUpdated   -> _state.update { it.copy(avatarConfig = event.config) }
            MapEvent.OnDismissDangerAlert -> {
                _state.update { it.copy(showDangerAlert = false, dangerZoneActual = null) }
            }
        }
    }

    private fun startObservingFriendRequests() {
        val uid = Session.uid ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                observeFriendRequestsUseCase(uid)
                println("👥 Observando solicitudes de amistad para: $uid")
            } catch (e: Exception) {
                println("❌ Error al iniciar observación de amigos: ${e.message}")
            }
        }
    }

    // ── 1. Perfil / Avatar ────────────────────────────────────────────────────

    private fun loadProfileIfNeeded() {
        // Si el avatar ya es distinto al default, ya fue cargado (o actualizado por EditProfile)
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

    // Llamado desde MainScreen cuando OwnProfileViewModel actualiza el perfil
    fun updateAvatarFromProfile(config: AvatarConfigModel) {
        _state.update { it.copy(avatarConfig = config) }
    }

    // ── 2. Carga inicial de tiles ─────────────────────────────────────────────
    //
    // Se ejecuta UNA SOLA VEZ por sesión de usuario.
    // Si hay caché (mismo uid), usa los datos en memoria — sin Room, sin Firebase.
    // Solo si no hay caché hace: Firebase sync → Room read.

    private fun loadTilesIfNeeded() {
        if (initialLoadDone) return
        val uid = Session.uid ?: return

        initialLoadDone = true  // marcar antes del launch para que recomposiciones no relancen

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _state.update { it.copy(isLoadingTiles = true) }

                val tiles: List<TileModel> = if (cachedTiles != null && cachedUid == uid) {
                    // Caso rápido: caché en memoria, 0 I/O
                    cachedTiles!!
                } else {
                    // Primera vez en esta sesión: sync Firebase → Room → leer Room
                    if (!initialSyncDone) {
                        initialSyncDone = true
                        try { syncMapHistoryUseCase(uid) } catch (_: Exception) { /* sin red */ }
                    }
                    val t = getDiscoveredTilesUseCase(uid)
                    cachedTiles = t
                    cachedUid   = uid
                    t
                }

                // Actualizar tiles en el estado inmediatamente (el mapa ya puede pintar fog)
                _state.update {
                    it.copy(
                        discoveredTiles    = tiles,
                        totalTilesUnlocked = tiles.size,
                        experience         = tiles.size * 10,
                        level              = (tiles.size / 10) + 1,
                        isLoadingTiles     = false
                    )
                }

                // Si ya tenemos GPS, cargar lugares ahora mismo
                val lat = state.value.userLat
                val lon = state.value.userLng
                if (lat != 0.0 && lon != 0.0 && !placesLoadedOnce) {
                    loadNearbyPlaces(tiles, lat, lon)
                }
                // Si no hay GPS todavía, los lugares se cargarán en handleLocationUpdate
                // cuando llegue la primera ubicación válida

            } catch (e: Exception) {
                if (e !is CancellationException) {
                    initialLoadDone = false  // permitir reintento si falla
                    _state.update { it.copy(isLoadingTiles = false) }
                }
            }
        }
    }

    // ── 3. GPS ────────────────────────────────────────────────────────────────

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
                            userLat           = location.latitude,
                            userLng           = location.longitude,
                            isLoadingLocation = false
                        )
                    }
                    tryUnlockTile(location.latitude, location.longitude)
                    checkDangerIfNeeded(location.latitude, location.longitude)
                }
        }
    }

    // Llamado desde MapViewContainer (Android) cuando llega ubicación por el canal nativo
    private fun handleLocationUpdate(lat: Double, lon: Double) {
        _state.update { it.copy(userLat = lat, userLng = lon, isLoadingLocation = false) }

        viewModelScope.launch(Dispatchers.IO) {
            tryUnlockTile(lat, lon)
            checkDangerIfNeeded(lat, lon)

            // Primera ubicación válida: cargar lugares si aún no se hizo
            if (!placesLoadedOnce && initialLoadDone) {
                val tiles = state.value.discoveredTiles
                if (tiles.isNotEmpty()) {
                    loadNearbyPlaces(tiles, lat, lon)
                }
            }
        }
    }

    // ── 4. Desbloqueo de tiles ────────────────────────────────────────────────

    private suspend fun tryUnlockTile(lat: Double, lon: Double) {
        val uid = Session.uid ?: return
        val (tileX, tileY) = TileUtils.latLngToTile(lat, lon)
        val tileKey = "${tileX}_${tileY}"

        if (tileKey == lastProcessedTileKey) return  // mismo tile, ignorar
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

    // Se llama solo cuando hay un tile genuinamente nuevo
    private fun onNewTileDiscovered(tileX: Int, tileY: Int, lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Leer tiles actualizados de Room (operación local, rápida)
                val uid   = Session.uid ?: return@launch
                val tiles = getDiscoveredTilesUseCase(uid)
                cachedTiles = tiles   // actualizar caché
                cachedUid   = uid

                // 2. Actualizar estado con nuevos tiles
                _state.update {
                    it.copy(
                        discoveredTiles    = tiles,
                        totalTilesUnlocked = tiles.size,
                        experience         = tiles.size * 10,
                        level              = (tiles.size / 10) + 1
                    )
                }

                // 3. Recargar lugares SOLO si el usuario se movió lo suficiente
                //    (evita llamar a Overpass en cada tile nuevo)
                val dist = haversine(lat, lon, lastPlacesSearchLat, lastPlacesSearchLon)
                if (dist > 500 || !placesLoadedOnce) {
                    loadNearbyPlaces(tiles, lat, lon)
                }

                // 4. Efecto visual/háptico de tile nuevo
                _effect.send(MapEffect.NewTileDiscovered(tileX, tileY))

            } catch (e: Exception) {
                if (e !is CancellationException) e.printStackTrace()
            }
        }
    }

    // ── 5. Lugares cercanos ───────────────────────────────────────────────────
    //
    // Reglas:
    //   - Solo se llama si el usuario se movió > 500m desde la última búsqueda
    //   - Cancela la búsqueda anterior si todavía estaba en curso
    //   - Usa caché de lugares si están disponibles (mismo uid, misma zona)
    //   - Filtra solo los lugares que están en tiles descubiertos

    private fun loadNearbyPlaces(tiles: List<TileModel>, lat: Double, lon: Double) {
        val dist = haversine(lat, lon, lastPlacesSearchLat, lastPlacesSearchLon)

        // Si ya tenemos lugares cargados del caché Y no nos movimos mucho, no volver a pedir
        if (placesLoadedOnce && dist < 500 && cachedPlaces != null) {
            // Solo re-filtrar con los tiles actuales (puede haber tiles nuevos)
            val filtered = filterPlacesByTiles(cachedPlaces!!, tiles)
            _state.update { it.copy(nearbyPlacesInMap = filtered) }
            return
        }

        // Cancelar búsqueda anterior si sigue corriendo
        placesJob?.cancel()

        placesJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                lastPlacesSearchLat = lat
                lastPlacesSearchLon = lon

                // Llamada a Overpass API (puede tardar 2-5 segundos)
                val allPlaces = getNearbyPlacesUseCase(lat, lon)

                // Guardar en caché para reutilizar
                cachedPlaces = allPlaces

                // Filtrar: solo mostrar lugares en zonas ya descubiertas
                val filtered = filterPlacesByTiles(allPlaces, tiles)

                placesLoadedOnce = true
                _state.update { it.copy(nearbyPlacesInMap = filtered) }

            } catch (e: Exception) {
                if (e !is CancellationException) {
                    // Silencioso: si falla Overpass, el mapa sigue funcionando
                    println("⚠️ Overpass falló: ${e.message}")
                }
            }
        }
    }

    private fun filterPlacesByTiles(places: List<PlaceModel>, tiles: List<TileModel>): List<PlaceModel> {
        if (tiles.isEmpty()) return emptyList()

        // Construir set de claves de tiles para O(1) lookup en vez de O(n*m)
        val tileKeys = tiles.map { "${it.tileX}_${it.tileY}" }.toHashSet()

        return places.filter { lugar ->
            val (px, py) = TileUtils.latLngToTile(lugar.latitude, lugar.longitude)
            val key = "${px}_${py}"
            key in tileKeys && lugar.name.isNotBlank()
        }
    }

    // ── 6. Zonas peligrosas ───────────────────────────────────────────────────

    private fun syncDangerZonesIfNeeded() {
        // Solo sincroniza una vez por sesión de ViewModel
        // (el companion object NO guarda esto, así que si se recrea el VM, vuelve a sync)
        viewModelScope.launch(Dispatchers.IO) {
            try { syncDangerZonesUseCase() } catch (_: Exception) { /* best effort */ }
        }
    }

    private suspend fun checkDangerIfNeeded(lat: Double, lon: Double) {
        val zona = checkDangerZoneUseCase(lat, lon)
        if (zona != null) {
            val now = Clock.System.now().toEpochMilliseconds()
            val lastAlertTime = lastDangerAlerts[zona.zonaId] ?: 0L

            if (now - lastAlertTime >= ALERT_INTERVAL_MS) {
                println("🚨 Lanzando alerta para zona: ${zona.nombre} (ID: ${zona.zonaId})")
                lastDangerAlerts[zona.zonaId] = now
                _state.update { it.copy(dangerZoneActual = zona, showDangerAlert = true) }
                // Enviar efecto para vibración y notificación local
                _effect.send(MapEffect.DangerZoneAlertTriggered(zona))
            }
        }
    }

    // ── 7. Cámara ─────────────────────────────────────────────────────────────

    fun centerMapOnLocation(lat: Double, lon: Double) {
        _state.update { it.copy(cameraTarget = Pair(lat, lon)) }
        viewModelScope.launch { _effect.send(MapEffect.CenterMapOnLocation(lat, lon)) }
    }

    // ── 8. Utilidades ─────────────────────────────────────────────────────────

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r    = 6371000.0
        val dLat = (lat2 - lat1) * PI / 180.0
        val dLon = (lon2 - lon1) * PI / 180.0
        val a    = sin(dLat / 2).pow(2) +
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
