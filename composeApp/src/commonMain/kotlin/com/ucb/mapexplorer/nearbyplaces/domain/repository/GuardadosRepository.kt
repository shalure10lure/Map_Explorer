package com.ucb.mapexplorer.nearbyplaces.domain.repository

import com.ucb.mapexplorer.nearbyplaces.domain.model.LugarSavedModel


interface GuardadosRepository {
    suspend fun getGuardados(uid: String): List<LugarSavedModel>
    suspend fun isGuardado(uid: String, lugarId: String): Boolean

    /**
     * Agrega o quita de guardados.
     * @return true si el lugar quedó guardado.
     */
    suspend fun toggleGuardado(uid: String, lugarId: String, nombre: String,
                               categoria: String, lat: Double, lon: Double,
                               iconoCategoria: String): Boolean
}