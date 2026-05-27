package com.ucb.mapexplorer.map.domain.usecase

import com.ucb.mapexplorer.map.domain.model.PlaceModel
import com.ucb.mapexplorer.map.domain.repository.MapRepository

class GetNearbyPlacesUseCase(private val repository: MapRepository) {
    /*suspend operator fun invoke(latitude: Double, longitude: Double): List<PlaceModel> {
        return repository.getNearbyPlaces(latitude, longitude)
    }*/
}
