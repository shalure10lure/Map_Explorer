package com.ucb.mapexplorer.nearbyplaces.data.repository

import com.ucb.mapexplorer.nearbyplaces.data.datasource.LugarFavoritoLocalDataSource
import com.ucb.mapexplorer.nearbyplaces.data.datasource.NearbyPlacesRemoteDataSource
import com.ucb.mapexplorer.nearbyplaces.domain.model.LugarSavedModel
import com.ucb.mapexplorer.nearbyplaces.domain.repository.FavoritosRepository

class FavoritosRepositoryImpl(
    private val local: LugarFavoritoLocalDataSource,
    private val remote: NearbyPlacesRemoteDataSource
) : FavoritosRepository {

    override suspend fun getFavoritos(uid: String): List<LugarSavedModel> =
        local.getFavoritos(uid).map {
            LugarSavedModel(
                lugarId = it.lugarId,
                nombre = it.nombre,
                categoria = it.categoria,
                iconoCategoria = it.iconoCategoria,
                latitud = it.latitud,
                longitud = it.longitud,
                guardadoEn = it.agregadoEn
            )
        }

    override suspend fun isFavorito(uid: String, lugarId: String): Boolean =
        local.isFavorito(uid, lugarId)

    override suspend fun toggleFavorito(
        uid: String, lugarId: String, nombre: String,
        categoria: String, lat: Double, lon: Double, iconoCategoria: String
    ): Boolean {
        val isFav = local.toggleFavorito(uid, lugarId, nombre, categoria, lat, lon, iconoCategoria)

        // Sincronizar con Firebase en segundo plano (best-effort)
        try {
            if (isFav) {
                remote.saveFavorito(uid, lugarId, nombre, categoria, lat, lon)
            } else {
                remote.removeFavorito(uid, lugarId)
            }
        } catch (e: Exception) {
            println("[FavoritosRepo] Firebase sync failed: ${e.message}")
        }

        return isFav
    }
}
