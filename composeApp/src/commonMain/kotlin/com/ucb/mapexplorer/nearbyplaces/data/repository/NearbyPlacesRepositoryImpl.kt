package com.ucb.mapexplorer.nearbyplaces.data.repository

import com.ucb.mapexplorer.nearbyplaces.data.datasource.NearbyPlacesLocalDataSource
import com.ucb.mapexplorer.nearbyplaces.data.datasource.NearbyPlacesRemoteDataSource
import com.ucb.mapexplorer.nearbyplaces.data.mapper.toEntity
import com.ucb.mapexplorer.nearbyplaces.data.mapper.toModel
import com.ucb.mapexplorer.nearbyplaces.domain.model.PlaceModel
import com.ucb.mapexplorer.nearbyplaces.domain.repository.NearbyPlacesRepository
import kotlinx.datetime.Clock
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class NearbyPlacesRepositoryImpl(
    private val local: NearbyPlacesLocalDataSource,
    private val remote: NearbyPlacesRemoteDataSource
) : NearbyPlacesRepository {

    private var lastUserLat = 0.0
    private var lastUserLon = 0.0

    // Si los datos tienen menos de 5 min, devolvemos Room directamente sin llamar Overpass.
    private val CACHE_TTL_MS = 5 * 60 * 1000L
    private var lastFetchTimestamp = 0L
    private var lastFetchLat = 0.0
    private var lastFetchLon = 0.0

    override suspend fun fetchAndCacheNearbyPlaces(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int
    ): List<PlaceModel> {
        lastUserLat = latitude
        lastUserLon = longitude

        val now = Clock.System.now().toEpochMilliseconds()
        val timeSinceLastFetch = now - lastFetchTimestamp
        val distanceSinceLastFetch = haversine(latitude, longitude, lastFetchLat, lastFetchLon)

        // CAMBIO: Devolver caché local si los datos son recientes Y estamos cerca
        // En lugar de siempre llamar a Overpass (que tarda 2-5 segundos)
        if (timeSinceLastFetch.toLong() < CACHE_TTL_MS && distanceSinceLastFetch < radiusMeters.toDouble()){
            val cached = local.getAll()
            if (cached.isNotEmpty()) {
                return cached
                    .map { it.toModel(latitude, longitude) }
                    .sortedBy { it.distanceMeters }
            }
        }

        // Si el caché está vacío o expirado, llamar a Overpass
        val response = remote.fetchNearbyPlaces(latitude, longitude, radiusMeters)

        val entities = response.elements
            .mapNotNull { it.toEntity(now) }
            .distinctBy { it.lugarId }

        if (entities.isNotEmpty()) {
            local.saveAll(entities)
            lastFetchTimestamp = now
            lastFetchLat = latitude
            lastFetchLon = longitude
            return entities
                .map { it.toModel(latitude, longitude) }
                .sortedBy { it.distanceMeters }
        }

        // Fallback a Room si Overpass falla
        return local.getAll()
            .map { it.toModel(latitude, longitude) }
            .sortedBy { it.distanceMeters }
    }

    override suspend fun getCachedPlaces(): List<PlaceModel> =
        local.getAll()
            .map { it.toModel(lastUserLat, lastUserLon) }
            .sortedBy { it.distanceMeters }

    override suspend fun getPlaceById(id: String): PlaceModel? =
        local.getById(id)?.toModel(lastUserLat, lastUserLon)

    override suspend fun syncLugarDescubierto(uid: String, place: PlaceModel) {
        remote.saveLugarDescubierto(
            uid = uid,
            lugarId = place.id,
            nombre = place.name,
            categoria = place.category,
            lat = place.latitude,
            lon = place.longitude
        )
    }

    override suspend fun syncLugarVisitado(uid: String, place: PlaceModel) {
        remote.saveLugarVisitado(
            uid = uid,
            lugarId = place.id,
            nombre = place.name,
            categoria = place.category
        )
    }
    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = (lat2 - lat1) * PI / 180.0
        val dLon = (lon2 - lon1) * PI / 180.0
        val a = sin(dLat / 2).pow(2) +
                cos(lat1 * PI / 180.0) * cos(lat2 * PI / 180.0) * sin(dLon / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

}