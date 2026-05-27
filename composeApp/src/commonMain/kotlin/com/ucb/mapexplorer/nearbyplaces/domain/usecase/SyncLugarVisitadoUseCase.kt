package com.ucb.mapexplorer.nearbyplaces.domain.usecase

import com.ucb.mapexplorer.nearbyplaces.domain.model.PlaceModel
import com.ucb.mapexplorer.nearbyplaces.domain.repository.NearbyPlacesRepository

class SyncLugarVisitadoUseCase(
    private val repository: NearbyPlacesRepository
) {
    suspend operator fun invoke(uid: String, place: PlaceModel) =
        repository.syncLugarVisitado(uid, place)
}