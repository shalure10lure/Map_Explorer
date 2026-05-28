package com.ucb.mapexplorer.nearbyplaces.domain.repository


import com.ucb.mapexplorer.nearbyplaces.domain.model.PlaceModel

interface NearbyPlacesRepository {
    suspend fun fetchAndCacheNearbyPlaces(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int = 500
    ): List<PlaceModel>

    suspend fun getCachedPlaces(): List<PlaceModel>

    suspend fun getPlaceById(id: String): PlaceModel?

    suspend fun syncLugarDescubierto(uid: String, place: PlaceModel)

    suspend fun syncLugarVisitado(uid: String, place: PlaceModel)
}