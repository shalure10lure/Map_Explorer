package com.ucb.mapexplorer.map.domain.usecase

import com.ucb.mapexplorer.map.domain.repository.MapRepository

class FetchAndCacheNearbyPlacesUseCase(private val repository: MapRepository) {
    /*suspend operator fun invoke(latitude: Double, longitude: Double) {
        repository.fetchAndCacheNearbyPlaces(latitude, longitude)
    }*/
}
