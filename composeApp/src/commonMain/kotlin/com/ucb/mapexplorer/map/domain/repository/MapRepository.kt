package com.ucb.mapexplorer.map.domain.repository

import com.ucb.mapexplorer.map.domain.model.PlaceModel
import com.ucb.mapexplorer.map.domain.model.TileModel
import com.ucb.mapexplorer.map.domain.model.UserLocationModel
import kotlinx.coroutines.flow.Flow

interface MapRepository {

    fun observeLocation(): Flow<UserLocationModel>

    suspend fun getNearbyPlaces(
        latitude: Double,
        longitude: Double
    ): List<PlaceModel>

    suspend fun unlockTile(
        uid: String,
        location: UserLocationModel
    )

    suspend fun getDiscoveredTiles(
        uid: String
    ): List<TileModel>
}