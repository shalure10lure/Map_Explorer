package com.ucb.mapexplorer.map.data.repository

import com.ucb.mapexplorer.core.utils.TileUtils
import com.ucb.mapexplorer.map.data.datasource.MapLocalDataSource
import com.ucb.mapexplorer.map.data.datasource.MapRemoteDataSource
import com.ucb.mapexplorer.map.data.service.LocalitationService
import com.ucb.mapexplorer.map.domain.model.PlaceModel
import com.ucb.mapexplorer.map.domain.model.TileModel
import com.ucb.mapexplorer.map.domain.model.UserLocationModel
import com.ucb.mapexplorer.map.domain.repository.MapRepository
import kotlinx.coroutines.flow.Flow

class MapRepositoryImpl(
    private val localDataSource: MapLocalDataSource,
    private val remoteDataSource: MapRemoteDataSource,
    private val locationService: LocalitationService
) : MapRepository {

    override fun observeLocation(): Flow<UserLocationModel> {
        return locationService.observeLocation()
    }

    override suspend fun unlockTile(
        uid: String,
        location: UserLocationModel
    ) {
        val (x, y) = TileUtils.latLngToTile(
            location.latitude,
            location.longitude
        )

        val isNew = localDataSource.unlockTile(
            uid = uid,
            x = x,
            y = y
        )

        if (isNew) {
            val tiles = localDataSource.getTiles(uid)
            val newTile = tiles.first {
                it.tileX == x && it.tileY == y
            }

            remoteDataSource.syncTile(uid, newTile)
        }
    }

    override suspend fun getDiscoveredTiles(
        uid: String
    ): List<TileModel> {
        return localDataSource.getTiles(uid)
    }

    override suspend fun getNearbyPlaces(
        latitude: Double,
        longitude: Double
    ): List<PlaceModel> {
        return emptyList()
    }
}