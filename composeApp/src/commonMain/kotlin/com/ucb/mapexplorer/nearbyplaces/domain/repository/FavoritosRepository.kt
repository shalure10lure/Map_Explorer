package com.ucb.mapexplorer.nearbyplaces.domain.repository

import com.ucb.mapexplorer.nearbyplaces.domain.model.LugarSavedModel


interface FavoritosRepository {
    suspend fun getFavoritos(uid: String): List<LugarSavedModel>
    suspend fun isFavorito(uid: String, lugarId: String): Boolean

    /**
     * Agrega o quita de favoritos.
     * @return true si el lugar quedó marcado como favorito.
     */
    suspend fun toggleFavorito(uid: String, lugarId: String, nombre: String,
                               categoria: String, lat: Double, lon: Double,
                               iconoCategoria: String): Boolean
}
