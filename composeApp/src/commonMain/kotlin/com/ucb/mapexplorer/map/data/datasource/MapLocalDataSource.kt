package com.ucb.mapexplorer.map.data.datasource

import com.ucb.mapexplorer.map.data.dao.TileDao
import com.ucb.mapexplorer.map.data.entity.TileEntity
import com.ucb.mapexplorer.map.data.mapper.toModel
import com.ucb.mapexplorer.map.domain.model.TileModel

class MapLocalDataSource(
    private val tileDao: TileDao
) {

    suspend fun unlockTile(
        uid: String,
        x: Int,
        y: Int
    ): Boolean {

        val existing = tileDao.getTile(uid, x, y)
        val now = System.currentTimeMillis()

        if (existing != null) {
            tileDao.updateVisit(uid, x, y, now)
            return false
        }

        tileDao.insertTile(
            TileEntity(
                uid = uid,
                tileX = x,
                tileY = y,
                descubiertoEn = now,
                vecesVisitado = 1,
                porcentajeExplorado = 100f,
                ultimoIngreso = now,
                sincronizado = false
            )
        )

        return true
    }

    suspend fun getTiles(uid: String): List<TileModel> {
        return tileDao.getTilesByUser(uid).map { it.toModel() }
    }
}