package com.ucb.mapexplorer.nearbyplaces.data.datasource

import com.ucb.mapexplorer.nearbyplaces.data.dto.OverpassResponseDto

expect class NearbyPlacesRemoteDataSource() {

    suspend fun fetchNearbyPlaces(lat: Double, lon: Double, radius: Int): OverpassResponseDto

    suspend fun saveLugarDescubierto(
        uid: String, lugarId: String, nombre: String,
        categoria: String, lat: Double, lon: Double
    )

    suspend fun saveLugarVisitado(
        uid: String, lugarId: String, nombre: String, categoria: String
    )

    // ── Favoritos ─────────────────────────────────────────────────────────
    suspend fun saveFavorito(
        uid: String, lugarId: String, nombre: String,
        categoria: String, lat: Double, lon: Double
    )

    suspend fun removeFavorito(uid: String, lugarId: String)

    // ── Guardados ─────────────────────────────────────────────────────────
    suspend fun saveGuardado(
        uid: String, lugarId: String, nombre: String,
        categoria: String, lat: Double, lon: Double
    )

    suspend fun removeGuardado(uid: String, lugarId: String)
}