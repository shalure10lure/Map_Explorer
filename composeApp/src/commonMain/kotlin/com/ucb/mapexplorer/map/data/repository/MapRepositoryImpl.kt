package com.ucb.mapexplorer.map.data.repository

import com.ucb.mapexplorer.core.utils.TileUtils
import com.ucb.mapexplorer.map.data.datasource.MapLocalDataSource
import com.ucb.mapexplorer.map.data.datasource.MapRemoteDataSource
import com.ucb.mapexplorer.map.data.service.LocalitationService
import com.ucb.mapexplorer.map.domain.model.TileModel
import com.ucb.mapexplorer.map.domain.model.UserLocationModel
import com.ucb.mapexplorer.map.domain.repository.MapRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock

class MapRepositoryImpl(
    private val localDataSource: MapLocalDataSource,
    private val remoteDataSource: MapRemoteDataSource,
    private val locationService: LocalitationService
) : MapRepository {

    override fun observeLocation(): Flow<UserLocationModel> =
        locationService.observeLocation()

    override suspend fun unlockTile(uid: String, location: UserLocationModel): Boolean {
        val (x, y) = TileUtils.latLngToTile(location.latitude, location.longitude)
        val isNew = localDataSource.unlockTile(uid, x, y)

        if (isNew) {
            // Sincronización Firebase en segundo plano (no bloquea UI)
            val now = Clock.System.now().toEpochMilliseconds()
            val tile = TileModel(
                tileX = x,
                tileY = y,
                discoveredAt = now,
                visitCount = 1,
                lastVisited = now
            )
            try {
                remoteDataSource.syncTile(uid, tile)
                localDataSource.markAsSynced(uid, x, y)
            } catch (e: Exception) {
                // Sin internet → el tile queda en Room con sincronizado=false
                // Se sincronizará en la próxima sesión con internet.
                println("[MapRepo] Firebase sync failed, tile queued: ${x}_$y")
            }
        }

        return isNew
    }
    override suspend fun downloadHistoryIfEmpty(uid: String) {
        try {
            // Obtenemos lo que hay en Room
            val localTiles = localDataSource.getTiles(uid)

            // SI NO HAY NADA (esto pasa cuando borras caché), descargamos de Firebase
            if (localTiles.isEmpty()) {
                println("🔄 Room vacío para $uid. Descargando de Firebase...")
                val remoteTiles = remoteDataSource.getAllVisitedTiles(uid)

                if (remoteTiles.isNotEmpty()) {
                    // ESTA ES LA CLAVE: Guardamos en Room para que se pinte el mapa
                    localDataSource.saveTilesToLocal(uid, remoteTiles)
                }
            }
        } catch (e: Exception) {
            println("❌ Error en downloadHistory: ${e.message}")
        }
    }

    override suspend fun getDiscoveredTiles(uid: String): List<TileModel> =
        localDataSource.getTiles(uid)


}
