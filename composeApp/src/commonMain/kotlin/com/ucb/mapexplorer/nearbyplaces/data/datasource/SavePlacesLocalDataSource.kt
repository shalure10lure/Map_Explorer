package com.ucb.mapexplorer.nearbyplaces.data.datasource



import com.ucb.mapexplorer.nearbyplaces.data.dao.LugarGuardadoDao
import com.ucb.mapexplorer.nearbyplaces.data.entity.LugarGuardadoEntity
import kotlin.time.Clock

class LugarGuardadoLocalDataSource(
    private val dao: LugarGuardadoDao
) {
    suspend fun getGuardados(uid: String): List<LugarGuardadoEntity> =
        dao.getGuardados(uid)

    suspend fun isGuardado(uid: String, lugarId: String): Boolean =
        dao.isGuardado(uid, lugarId)

    /**
     * Agrega o quita de guardados (toggle).
     * @return true si quedó guardado, false si se quitó.
     */
    suspend fun toggleGuardado(
        uid: String,
        lugarId: String,
        nombre: String,
        categoria: String,
        latitud: Double,
        longitud: Double,
        iconoCategoria: String
    ): Boolean {
        val exists = dao.getGuardado(uid, lugarId)
        return if (exists != null) {
            dao.delete(uid, lugarId)
            false
        } else {
            dao.insert(
                LugarGuardadoEntity(
                    uid = uid,
                    lugarId = lugarId,
                    nombre = nombre,
                    categoria = categoria,
                    latitud = latitud,
                    longitud = longitud,
                    iconoCategoria = iconoCategoria,
                    guardadoEn = Clock.System.now().toEpochMilliseconds()
                )
            )
            true
        }
    }
}