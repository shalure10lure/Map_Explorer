package com.ucb.mapexplorer.nearbyplaces.data.datasource

import com.ucb.mapexplorer.nearbyplaces.data.dto.OverpassResponseDto

expect class NearbyPlacesRemoteDataSource() {

    // Overpass API → lugares cercanos
    suspend fun fetchNearbyPlaces(
        lat: Double,
        lon: Double,
        radius: Int
    ): OverpassResponseDto

    // Firebase → usuarios/{uid}/exploracion/lugares_descubiertos/{lugarId}
    suspend fun saveLugarDescubierto(
        uid: String,
        lugarId: String,
        nombre: String,
        categoria: String,
        lat: Double,
        lon: Double
    )

    // Firebase → usuarios/{uid}/exploracion/lugares_visitados/{lugarId}
    suspend fun saveLugarVisitado(
        uid: String,
        lugarId: String,
        nombre: String,
        categoria: String
    )
}