package com.ucb.mapexplorer.map.data.datasource

import com.ucb.mapexplorer.map.domain.model.TileModel

expect class MapRemoteDataSource() {
    /**
     * Sincroniza un tile descubierto con Firebase Realtime Database.
     * La estructura en Firebase queda:
     *   usuarios/{uid}/exploracion/tiles_descubiertos/{tileX}_{tileY}
     */
    suspend fun syncTile(uid: String, tile: TileModel)
}

