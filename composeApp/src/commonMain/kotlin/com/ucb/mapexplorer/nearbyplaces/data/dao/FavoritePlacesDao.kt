package com.ucb.mapexplorer.nearbyplaces.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ucb.mapexplorer.nearbyplaces.data.entity.LugarFavoritoEntity

@Dao
interface LugarFavoritoDao {

    @Query("SELECT * FROM lugares_favoritos WHERE uid = :uid ORDER BY agregadoEn DESC")
    suspend fun getFavoritos(uid: String): List<LugarFavoritoEntity>

    @Query("SELECT * FROM lugares_favoritos WHERE uid = :uid AND lugarId = :lugarId LIMIT 1")
    suspend fun getFavorito(uid: String, lugarId: String): LugarFavoritoEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: LugarFavoritoEntity): Long

    @Query("DELETE FROM lugares_favoritos WHERE uid = :uid AND lugarId = :lugarId")
    suspend fun delete(uid: String, lugarId: String)

    @Query("SELECT COUNT(*) > 0 FROM lugares_favoritos WHERE uid = :uid AND lugarId = :lugarId")
    suspend fun isFavorito(uid: String, lugarId: String): Boolean

    @Query("SELECT COUNT(*) FROM lugares_favoritos WHERE uid = :uid")
    suspend fun getCount(uid: String): Int
}