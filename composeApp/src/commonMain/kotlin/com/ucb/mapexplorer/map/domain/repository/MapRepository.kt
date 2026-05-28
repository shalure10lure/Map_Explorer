package com.ucb.mapexplorer.map.domain.repository

import com.ucb.mapexplorer.map.domain.model.TileModel
import com.ucb.mapexplorer.map.domain.model.UserLocationModel
import kotlinx.coroutines.flow.Flow

interface MapRepository {
    /** Flujo continuo de la ubicación GPS del usuario. */
    fun observeLocation(): Flow<UserLocationModel>

    /**
     * Desbloquea el tile en la posición dada.
     * Si es nuevo → lo guarda en Room y sincroniza con Firebase.
     * Si ya existe → solo actualiza la visita en Room.
     * @return true si fue un tile NUEVO (nunca antes visitado).
     */
    suspend fun unlockTile(uid: String, location: UserLocationModel): Boolean

    /** Tiles descubiertos del usuario, leídos desde Room (fuente de verdad local). */
    suspend fun getDiscoveredTiles(uid: String): List<TileModel>
}
