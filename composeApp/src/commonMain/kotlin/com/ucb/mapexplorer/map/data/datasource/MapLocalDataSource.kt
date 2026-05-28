package com.ucb.mapexplorer.map.data.datasource

import com.ucb.mapexplorer.map.data.dao.TileDao
import com.ucb.mapexplorer.map.data.entity.TileEntity
import com.ucb.mapexplorer.map.data.mapper.toModel
import com.ucb.mapexplorer.map.domain.model.TileModel
import kotlinx.datetime.Clock

class MapLocalDataSource(
    private val tileDao: TileDao
) {

    /**
     * Intenta desbloquear el tile (x, y) para el usuario [uid].
     *
     * @return true  → tile NUEVO descubierto, guardado en Room.
     *         false → tile ya existía, solo se actualizó la visita.
     */
    suspend fun unlockTile(uid: String, x: Int, y: Int): Boolean {
        val now = Clock.System.now().toEpochMilliseconds()
        val existing = tileDao.getTile(uid, x, y)
        println("🗄️ ROOM - buscando tile ${x}_${y} para uid=$uid → existing=$existing")
        return if (existing == null) {
            // Tile nuevo → insertar
            tileDao.insertTile(
                TileEntity(
                    uid = uid,
                    tileX = x,
                    tileY = y,
                    descubiertoEn = now,
                    vecesVisitado = 1,
                    ultimoIngreso = now,
                    sincronizado = false
                )
            )
            println("🆕 TILE NUEVO insertado en Room")  // ← AGREGAR
            true
        } else {
            // Tile conocido → actualizar visita
            tileDao.updateVisit(uid, x, y, now)
            println("🔄 TILE YA EXISTE en Room, solo update visita")
            false
        }
    }

    suspend fun getTiles(uid: String): List<TileModel> =
        tileDao.getTilesByUser(uid).map { it.toModel() }

    suspend fun markAsSynced(uid: String, x: Int, y: Int) =
        tileDao.markAsSynced(uid, x, y)

    suspend fun getUnsyncedTiles(): List<TileEntity> =
        tileDao.getUnsyncedTiles()
}
