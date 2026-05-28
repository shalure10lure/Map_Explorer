package com.ucb.mapexplorer.map.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ucb.mapexplorer.map.data.entity.TileEntity

@Dao
interface TileDao {

    /** Todos los tiles descubiertos de un usuario. */
    @Query("SELECT * FROM tiles_descubiertos WHERE uid = :uid ORDER BY descubiertoEn DESC")
    suspend fun getTilesByUser(uid: String): List<TileEntity>

    /** Busca un tile específico de un usuario. Retorna null si no existe. */
    @Query("""
        SELECT * FROM tiles_descubiertos
        WHERE uid = :uid AND tileX = :x AND tileY = :y
        LIMIT 1
    """)
    suspend fun getTile(uid: String, x: Int, y: Int): TileEntity?

    /**
     * Inserta un tile nuevo. IGNORE si ya existe (evita sobreescribir
     * vecesVisitado que se maneja con updateVisit).
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTile(tile: TileEntity): Long

    /** Incrementa vecesVisitado y actualiza ultimoIngreso. */
    @Query("""
        UPDATE tiles_descubiertos
        SET vecesVisitado = vecesVisitado + 1,
            ultimoIngreso = :time
        WHERE uid = :uid AND tileX = :x AND tileY = :y
    """)
    suspend fun updateVisit(uid: String, x: Int, y: Int, time: Long)

    /** Marca el tile como sincronizado con Firebase. */
    @Query("""
        UPDATE tiles_descubiertos
        SET sincronizado = 1
        WHERE uid = :uid AND tileX = :x AND tileY = :y
    """)
    suspend fun markAsSynced(uid: String, x: Int, y: Int)

    /** Tiles pendientes de sincronización con Firebase. */
    @Query("SELECT * FROM tiles_descubiertos WHERE sincronizado = 0")
    suspend fun getUnsyncedTiles(): List<TileEntity>

    /** Cantidad total de tiles descubiertos por usuario. */
    @Query("SELECT COUNT(*) FROM tiles_descubiertos WHERE uid = :uid")
    suspend fun getTileCount(uid: String): Int
}
