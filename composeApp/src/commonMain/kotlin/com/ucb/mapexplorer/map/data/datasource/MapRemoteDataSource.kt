package com.ucb.mapexplorer.map.data.datasource

import com.ucb.mapexplorer.map.domain.model.TileModel


expect class MapRemoteDataSource() {

    suspend fun syncTile(
        uid: String,
        tile: TileModel
    )
}