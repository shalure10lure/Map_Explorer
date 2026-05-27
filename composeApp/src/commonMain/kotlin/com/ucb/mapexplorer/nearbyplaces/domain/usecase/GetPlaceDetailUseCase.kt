package com.ucb.mapexplorer.nearbyplaces.domain.usecase


import com.ucb.mapexplorer.nearbyplaces.domain.model.PlaceModel
import com.ucb.mapexplorer.nearbyplaces.domain.repository.NearbyPlacesRepository

class GetPlaceDetailUseCase(
    private val repository: NearbyPlacesRepository
) {
    suspend operator fun invoke(id: String): PlaceModel? =
        repository.getPlaceById(id)
}