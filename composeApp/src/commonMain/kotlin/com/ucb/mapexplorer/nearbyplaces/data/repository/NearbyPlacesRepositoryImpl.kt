package com.ucb.mapexplorer.nearbyplaces.data.repository


import com.ucb.mapexplorer.nearbyplaces.data.datasource.NearbyPlacesLocalDataSource
import com.ucb.mapexplorer.nearbyplaces.data.datasource.NearbyPlacesRemoteDataSource
import com.ucb.mapexplorer.nearbyplaces.data.mapper.toEntity
import com.ucb.mapexplorer.nearbyplaces.data.mapper.toModel
import com.ucb.mapexplorer.nearbyplaces.domain.model.PlaceModel
import com.ucb.mapexplorer.nearbyplaces.domain.repository.NearbyPlacesRepository
import kotlinx.datetime.Clock

class NearbyPlacesRepositoryImpl(
    private val local: NearbyPlacesLocalDataSource,
    private val remote: NearbyPlacesRemoteDataSource
) : NearbyPlacesRepository {

    private var lastUserLat = 0.0
    private var lastUserLon = 0.0

    // ── Overpass → Room ────────────────────────────────────────────────────
    override suspend fun fetchAndCacheNearbyPlaces(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int
    ): List<PlaceModel> {
        lastUserLat = latitude
        lastUserLon = longitude

        val now      = Clock.System.now().toEpochMilliseconds()
        val response = remote.fetchNearbyPlaces(latitude, longitude, radiusMeters)

        val entities = response.elements
            .mapNotNull { it.toEntity(now) }
            .distinctBy { it.lugarId }

        if (entities.isNotEmpty()) local.saveAll(entities)

        return entities
            .map { it.toModel(latitude, longitude) }
            .sortedBy { it.distanceMeters }
    }

    // ── Cache offline ──────────────────────────────────────────────────────
    override suspend fun getCachedPlaces(): List<PlaceModel> =
        local.getAll()
            .map { it.toModel(lastUserLat, lastUserLon) }
            .sortedBy { it.distanceMeters }

    // ── Detalle ────────────────────────────────────────────────────────────
    override suspend fun getPlaceById(id: String): PlaceModel? =
        local.getById(id)?.toModel(lastUserLat, lastUserLon)

    // ── Firebase: lugar descubierto ────────────────────────────────────────
    override suspend fun syncLugarDescubierto(uid: String, place: PlaceModel) {
        remote.saveLugarDescubierto(
            uid       = uid,
            lugarId   = place.id,
            nombre    = place.name,
            categoria = place.category,
            lat       = place.latitude,
            lon       = place.longitude
        )
    }

    // ── Firebase: lugar visitado ───────────────────────────────────────────
    override suspend fun syncLugarVisitado(uid: String, place: PlaceModel) {
        remote.saveLugarVisitado(
            uid       = uid,
            lugarId   = place.id,
            nombre    = place.name,
            categoria = place.category
        )
    }
}