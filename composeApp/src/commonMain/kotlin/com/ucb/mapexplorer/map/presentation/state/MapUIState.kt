package com.ucb.mapexplorer.map.presentation.state

import com.ucb.mapexplorer.dangerzone.domain.model.ZonaPeligrosaModel
import com.ucb.mapexplorer.map.domain.model.TileModel
import com.ucb.mapexplorer.nearbyplaces.domain.model.PlaceModel
import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel

data class MapUIState(
    val discoveredTiles: List<TileModel> = emptyList(),
    val userLat: Double = 0.0,
    val userLng: Double = 0.0,
    val isLoadingLocation: Boolean = true,
    val isLoadingTiles: Boolean = false,
    val errorMessage: String? = null,
    val totalTilesUnlocked: Int = 0,
    val level: Int = 1,
    val experience: Int = 0,
    val nearbyPlacesInMap: List<PlaceModel> = emptyList(),
    val avatarConfig: AvatarConfigModel = AvatarConfigModel(),

    val cameraTarget: Pair<Double, Double>? = null,

    val dangerZoneActual: ZonaPeligrosaModel? = null,
    val showDangerAlert: Boolean              = false
)
