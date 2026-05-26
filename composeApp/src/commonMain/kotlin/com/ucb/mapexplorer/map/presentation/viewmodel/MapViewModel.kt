package com.ucb.mapexplorer.map.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.core.utils.TileUtils
import com.ucb.mapexplorer.map.domain.model.UserLocationModel
import com.ucb.mapexplorer.map.domain.usecase.GetCurrentLocationUseCase
import com.ucb.mapexplorer.map.domain.usecase.GetDiscoveredTilesUseCase
import com.ucb.mapexplorer.map.domain.usecase.UnlockTileUseCase
import com.ucb.mapexplorer.map.presentation.state.MapUIState
import com.ucb.mapexplorer.map.presentation.state.MapEvent
import com.ucb.mapexplorer.map.presentation.state.MapEffect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MapViewModel(
    private val uid: String,
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase,
    private val unlockTileUseCase: UnlockTileUseCase,
    private val getDiscoveredTilesUseCase: GetDiscoveredTilesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MapUIState())
    val state: StateFlow<MapUIState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MapEffect>()
    val effect: SharedFlow<MapEffect> = _effect.asSharedFlow()

    private var locationJob: Job? = null
    private var lastTileKey: String? = null

    init {
        onEvent(MapEvent.OnLoadMap)
    }

    fun onEvent(event: MapEvent) {
        when (event) {
            MapEvent.OnLoadMap -> {
                loadDiscoveredTiles()
                startLocation()
            }
            MapEvent.OnRefreshLocation -> {
                startLocation(force = true)
            }
        }
    }

    private fun startLocation(force: Boolean = false) {
        if (locationJob != null && !force) return

        locationJob?.cancel()
        locationJob = viewModelScope.launch(Dispatchers.IO) {
            getCurrentLocationUseCase().collectLatest { location ->
                _state.update { current ->
                    current.copy(
                        currentLocation = location,
                        userLat = location.latitude,
                        userLng = location.longitude
                    )
                }
                autoUnlockTile(location)
            }
        }
    }

    private suspend fun autoUnlockTile(location: UserLocationModel) {
        val tile = TileUtils.latLngToTile(location.latitude, location.longitude)
        val key = "${tile.first}_${tile.second}"

        if (key == lastTileKey) return
        lastTileKey = key

        try {
            unlockTileUseCase(uid, location)
            val tiles = getDiscoveredTilesUseCase(uid)

            _state.update {
                it.copy(
                    discoveredTiles = tiles,
                    unlockedTiles = tiles.size
                )
            }
        } catch (e: Exception) {
            _effect.emit(MapEffect.ShowError(e.message ?: "Error desbloqueando tile"))
        }
    }

    private fun loadDiscoveredTiles() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val tiles = getDiscoveredTilesUseCase(uid)
                _state.update {
                    it.copy(
                        discoveredTiles = tiles,
                        unlockedTiles = tiles.size
                    )
                }
            } catch (e: Exception) {
                _effect.emit(MapEffect.ShowError(e.message ?: "Error cargando tiles"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        locationJob?.cancel()
    }
}
