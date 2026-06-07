package com.ucb.mapexplorer.nearbyplaces.data.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ucb.mapexplorer.nearbyplaces.data.entity.LugarGuardadoEntity

@Dao
interface LugarGuardadoDao {

    @Query("SELECT * FROM lugares_guardados WHERE uid = :uid ORDER BY guardadoEn DESC")
    suspend fun getGuardados(uid: String): List<LugarGuardadoEntity>

    @Query("SELECT * FROM lugares_guardados WHERE uid = :uid AND lugarId = :lugarId LIMIT 1")
    suspend fun getGuardado(uid: String, lugarId: String): LugarGuardadoEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: LugarGuardadoEntity): Long

    @Query("DELETE FROM lugares_guardados WHERE uid = :uid AND lugarId = :lugarId")
    suspend fun delete(uid: String, lugarId: String)

    @Query("SELECT COUNT(*) > 0 FROM lugares_guardados WHERE uid = :uid AND lugarId = :lugarId")
    suspend fun isGuardado(uid: String, lugarId: String): Boolean

    @Query("SELECT COUNT(*) FROM lugares_guardados WHERE uid = :uid")
    suspend fun getCount(uid: String): Int
}
