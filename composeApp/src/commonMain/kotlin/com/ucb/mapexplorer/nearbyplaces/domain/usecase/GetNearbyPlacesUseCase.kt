package com.ucb.mapexplorer.nearbyplaces.domain.usecase


import com.ucb.mapexplorer.nearbyplaces.domain.model.PlaceModel
import com.ucb.mapexplorer.nearbyplaces.domain.repository.NearbyPlacesRepository

class GetNearbyPlacesUseCase(
    private val repository: NearbyPlacesRepository
) {
    suspend operator fun invoke(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int = 500
    ): List<PlaceModel> {
        return try {
            repository.fetchAndCacheNearbyPlaces(latitude, longitude, radiusMeters)
        } catch (e: Exception) {
            println("Overpass error, usando cache: ${e.message}")
            repository.getCachedPlaces()
        }
    }
}