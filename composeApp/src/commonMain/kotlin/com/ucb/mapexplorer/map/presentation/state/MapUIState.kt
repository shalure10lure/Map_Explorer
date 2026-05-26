package com.ucb.mapexplorer.map.presentation.state

import com.ucb.mapexplorer.map.domain.model.PlaceModel
import com.ucb.mapexplorer.map.domain.model.TileModel
import com.ucb.mapexplorer.map.domain.model.UserLocationModel

data class MapUIState(
    // tiles descubiertos por el usuario
    val discoveredTiles: List<TileModel> = emptyList(),

    // ubicación actual
    val currentLocation: UserLocationModel? = null,

    // coordenadas rápidas (útiles para cámara / fog / cálculos)
    val userLat: Double = 0.0,
    val userLng: Double = 0.0,

    // total desbloqueados
    val unlockedTiles: Int = 0,

    // loading
    val isLoading: Boolean = false,

    // errores
    val error: String? = null
    //val nearbyPlaces: List<PlaceModel> = emptyList(),
    //val selectedPlace: PlaceModel? = null,

)