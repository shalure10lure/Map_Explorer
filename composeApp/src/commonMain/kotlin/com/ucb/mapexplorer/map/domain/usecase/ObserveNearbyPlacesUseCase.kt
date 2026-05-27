package com.ucb.mapexplorer.map.domain.usecase

import com.ucb.mapexplorer.map.domain.model.PlaceModel
import com.ucb.mapexplorer.map.domain.repository.MapRepository
import kotlinx.coroutines.flow.Flow

class ObserveNearbyPlacesUseCase(private val repository: MapRepository) {
    //operator fun invoke(): Flow<List<PlaceModel>> = repository.observeNearbyPlaces()
}
