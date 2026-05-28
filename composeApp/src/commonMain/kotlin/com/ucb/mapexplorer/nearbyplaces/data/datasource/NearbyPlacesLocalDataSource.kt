package com.ucb.mapexplorer.nearbyplaces.data.datasource



import com.ucb.mapexplorer.nearbyplaces.data.dao.PlaceDao
import com.ucb.mapexplorer.nearbyplaces.data.entity.LugarCacheEntity

class NearbyPlacesLocalDataSource(
    private val placeDao: PlaceDao
) {
    suspend fun getAll(): List<LugarCacheEntity> =
        placeDao.getAllCachedPlaces()

    suspend fun getById(id: String): LugarCacheEntity? =
        placeDao.getPlaceById(id)

    suspend fun saveAll(places: List<LugarCacheEntity>) {
        placeDao.clearCache()
        placeDao.insertAll(places)
    }
}