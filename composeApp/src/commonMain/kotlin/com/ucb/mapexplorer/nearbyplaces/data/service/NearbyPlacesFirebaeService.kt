package com.ucb.mapexplorer.nearbyplaces.data.service

import com.ucb.mapexplorer.nearbyplaces.data.datasource.NearbyPlacesRemoteDataSource
import com.ucb.mapexplorer.nearbyplaces.domain.model.PlaceModel

class NearbyPlacesFirebaseService(
    private val remote: NearbyPlacesRemoteDataSource
) {

    suspend fun saveDiscoveredPlace(
        uid: String,
        place: PlaceModel
    ) {

        remote.saveLugarDescubierto(
            uid,
            place.id,
            place.name,
            place.category,
            place.latitude,
            place.longitude
        )
    }

    suspend fun saveVisitedPlace(
        uid: String,
        place: PlaceModel
    ) {

        remote.saveLugarVisitado(
            uid,
            place.id,
            place.name,
            place.category
        )
    }
}