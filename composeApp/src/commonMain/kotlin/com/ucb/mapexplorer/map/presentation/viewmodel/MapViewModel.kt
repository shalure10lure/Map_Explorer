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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MapViewModel(
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val unlockTileUseCase: UnlockTileUseCase,
    private val getDiscoveredTilesUseCase: GetDiscoveredTilesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MapUIState())
    val state: StateFlow<MapUIState> = _state.asStateFlow()

    private val _effect = Channel<MapEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    /** Job del flujo GPS (se puede cancelar/reiniciar). */
    private var locationJob: Job? = null

    /**
     * Último tile procesado. Evita guardar en BD si el usuario
     * no se movió a un tile diferente.
     */
    private var lastProcessedTileKey: String? = null

    init {
        onEvent(MapEvent.OnLoadMap)
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

    private fun startLocationUpdates() {
        if (locationJob?.isActive == true) return

        locationJob = viewModelScope.launch(Dispatchers.IO) {
            getCurrentLocationUseCase()
                .catch { e ->
                    _effect.send(MapEffect.ShowError("Error GPS: ${e.message}"))
                }
                .collectLatest { location ->
                    // Actualiza UI con la nueva posición
                    _state.update {
                        it.copy(
                            userLat = location.latitude,
                            userLng = location.longitude,
                            isLoadingLocation = false
                        )
                    }
                    // Intenta desbloquear el tile
                    tryUnlockTile(location.latitude, location.longitude)
                }
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Maneja actualización de ubicación desde la capa de UI (MapViewContainer)
    // Esto es para cuando el GPS se controla desde la vista (Android OSMDroid).
    // ─────────────────────────────────────────────────────────────────────
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
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // Core: Desbloquear tile
    // ─────────────────────────────────────────────────────────────────────

    private suspend fun tryUnlockTile(lat: Double, lon: Double) {
        val uid = Session.uid
        println("🔍 SESSION UID = $uid")

        if (uid.isNullOrEmpty()) {
            println("❌ UID VACÍO - no se puede guardar")
            _effect.send(MapEffect.ShowSnackbar("Inicia sesión para explorar"))
            return
        }

        val (tileX, tileY) = TileUtils.latLngToTile(lat, lon)
        println("📍 GPS: lat=$lat lon=$lon → tile=${tileX}_${tileY}")

        val (centerLat, centerLon) = TileUtils.tileToCenterLatLng(tileX, tileY)
        val diffLat = kotlin.math.abs(lat - centerLat) * 111000
        val diffLon = kotlin.math.abs(lon - centerLon) * 111000
        println("🎯 Centro tile: lat=$centerLat lon=$centerLon")
        println("📏 Desplazamiento: ${diffLat.toInt()}m lat, ${diffLon.toInt()}m lon")

        val tileKey = "${tileX}_${tileY}"
        if (tileKey == lastProcessedTileKey) return
        lastProcessedTileKey = tileKey

        try {
            val isNewTile = unlockTileUseCase(uid, UserLocationModel(lat, lon))
            println("✅ TILE NUEVO = $isNewTile")  // ← y esto

            if (isNewTile) {
                loadDiscoveredTiles()
                _effect.send(MapEffect.NewTileDiscovered(tileX, tileY))
            }
        } catch (e: Exception) {
            println("❌ ERROR TILE: ${e.message}")
            _effect.send(MapEffect.ShowSnackbar("Error: ${e.message}"))
        }
    }
    // ─────────────────────────────────────────────────────────────────────
    // Carga tiles desde Room
    // ─────────────────────────────────────────────────────────────────────

    private fun loadDiscoveredTiles() {
        val uid = Session.uid ?: return

        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoadingTiles = true) }
            try {
                val tiles = getDiscoveredTilesUseCase(uid)
                val totalTiles = tiles.size
                _state.update {
                    it.copy(
                        discoveredTiles = tiles,
                        totalTilesUnlocked = totalTiles,
                        experience = totalTiles * 10,
                        level = (totalTiles / 10) + 1,
                        isLoadingTiles = false
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoadingTiles = false) }
                _effect.send(MapEffect.ShowSnackbar("Error cargando mapa: ${e.message}"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationJob?.cancel()
    }
}
