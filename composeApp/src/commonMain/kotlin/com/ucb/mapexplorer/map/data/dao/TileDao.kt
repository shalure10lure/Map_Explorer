package com.ucb.mapexplorer.map.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ucb.mapexplorer.map.data.entity.TileEntity

@Dao
interface TileDao {

    @Query("""
        SELECT * FROM tiles_descubiertos
        WHERE uid = :uid
    """)
    suspend fun getTilesByUser(uid: String): List<TileEntity>

    @Query("""
        SELECT * FROM tiles_descubiertos
        WHERE uid = :uid
        AND tileX = :x
        AND tileY = :y
        LIMIT 1
    """)
    suspend fun getTile(
        uid: String,
        x: Int,
        y: Int
    ): TileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTile(tile: TileEntity)

    @Query("""
        UPDATE tiles_descubiertos
        SET vecesVisitado = vecesVisitado + 1,
            ultimoIngreso = :time
        WHERE uid = :uid
        AND tileX = :x
        AND tileY = :y
    """)
    suspend fun updateVisit(
        uid: String,
        x: Int,
        y: Int,
        time: Long
    )

    @Query("""
        UPDATE tiles_descubiertos
        SET sincronizado = 1
        WHERE uid = :uid
        AND tileX = :x
        AND tileY = :y
    """)
    suspend fun markAsSynced(
        uid: String,
        x: Int,
        y: Int
    )

    @Query("""
        SELECT * FROM tiles_descubiertos
        WHERE sincronizado = 0
    """)
    suspend fun getUnsyncedTiles(): List<TileEntity>
}