package com.ucb.mapexplorer.dangerzone.data.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ucb.mapexplorer.dangerzone.data.entity.ZonaPeligrosaEntity

@Dao
interface ZonaPeligrosaDao {

    @Query("SELECT * FROM zonas_peligrosas WHERE activa = 1")
    suspend fun getZonasActivas(): List<ZonaPeligrosaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(zonas: List<ZonaPeligrosaEntity>)

    @Query("DELETE FROM zonas_peligrosas")
    suspend fun clearAll()

    @Query("""
        SELECT * FROM zonas_peligrosas
        WHERE activa = 1
        AND latitud BETWEEN :minLat AND :maxLat
        AND longitud BETWEEN :minLon AND :maxLon
    """)
    suspend fun getZonasCerca(
        minLat: Double, maxLat: Double,
        minLon: Double, maxLon: Double
    ): List<ZonaPeligrosaEntity>
}
