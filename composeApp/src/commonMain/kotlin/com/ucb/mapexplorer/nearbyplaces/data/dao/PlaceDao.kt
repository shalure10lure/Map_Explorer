package com.ucb.mapexplorer.nearbyplaces.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ucb.mapexplorer.nearbyplaces.data.entity.LugarCacheEntity

@Dao
interface PlaceDao {

    @Query("SELECT * FROM lugares_cache ORDER BY actualizadoEn DESC")
    suspend fun getAllCachedPlaces(): List<LugarCacheEntity>

    @Query("SELECT * FROM lugares_cache WHERE lugarId = :id LIMIT 1")
    suspend fun getPlaceById(id: String): LugarCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(places: List<LugarCacheEntity>)

    @Query("DELETE FROM lugares_cache")
    suspend fun clearCache()
}