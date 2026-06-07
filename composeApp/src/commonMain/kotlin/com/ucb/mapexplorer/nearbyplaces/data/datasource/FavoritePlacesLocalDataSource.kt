package com.ucb.mapexplorer.nearbyplaces.data.datasource

import com.ucb.mapexplorer.nearbyplaces.data.dao.LugarFavoritoDao
import com.ucb.mapexplorer.nearbyplaces.data.entity.LugarFavoritoEntity
import kotlin.time.Clock

class LugarFavoritoLocalDataSource(
    private val dao: LugarFavoritoDao
) {
    suspend fun getFavoritos(uid: String): List<LugarFavoritoEntity> =
        dao.getFavoritos(uid)

    suspend fun isFavorito(uid: String, lugarId: String): Boolean =
        dao.isFavorito(uid, lugarId)

    /**
     * Agrega o quita de favoritos (toggle).
     * @return true si quedó como favorito, false si se quitó.
     */
    suspend fun toggleFavorito(
        uid: String,
        lugarId: String,
        nombre: String,
        categoria: String,
        latitud: Double,
        longitud: Double,
        iconoCategoria: String
    ): Boolean {
        val exists = dao.getFavorito(uid, lugarId)
        return if (exists != null) {
            dao.delete(uid, lugarId)
            false
        } else {
            dao.insert(
                LugarFavoritoEntity(
                    uid = uid,
                    lugarId = lugarId,
                    nombre = nombre,
                    categoria = categoria,
                    latitud = latitud,
                    longitud = longitud,
                    iconoCategoria = iconoCategoria,
                    agregadoEn = Clock.System.now().toEpochMilliseconds()
                )
            )
            true
        }
    }
}

