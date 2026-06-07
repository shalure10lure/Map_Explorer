package com.ucb.mapexplorer.nearbyplaces.data.repository

import com.ucb.mapexplorer.nearbyplaces.data.datasource.LugarGuardadoLocalDataSource
import com.ucb.mapexplorer.nearbyplaces.data.datasource.NearbyPlacesRemoteDataSource
import com.ucb.mapexplorer.nearbyplaces.domain.model.LugarSavedModel
import com.ucb.mapexplorer.nearbyplaces.domain.repository.GuardadosRepository


class GuardadosRepositoryImpl(
    private val local: LugarGuardadoLocalDataSource,
    private val remote: NearbyPlacesRemoteDataSource
) : GuardadosRepository {

    override suspend fun getGuardados(uid: String): List<LugarSavedModel> =
        local.getGuardados(uid).map {
            LugarSavedModel(
                lugarId = it.lugarId,
                nombre = it.nombre,
                categoria = it.categoria,
                iconoCategoria = it.iconoCategoria,
                latitud = it.latitud,
                longitud = it.longitud,
                guardadoEn = it.guardadoEn
            )
        }

    override suspend fun isGuardado(uid: String, lugarId: String): Boolean =
        local.isGuardado(uid, lugarId)

    override suspend fun toggleGuardado(
        uid: String, lugarId: String, nombre: String,
        categoria: String, lat: Double, lon: Double, iconoCategoria: String
    ): Boolean {
        val isGuard = local.toggleGuardado(uid, lugarId, nombre, categoria, lat, lon, iconoCategoria)

        try {
            if (isGuard) {
                remote.saveGuardado(uid, lugarId, nombre, categoria, lat, lon)
            } else {
                remote.removeGuardado(uid, lugarId)
            }
        } catch (e: Exception) {
            println("[GuardadosRepo] Firebase sync failed: ${e.message}")
        }

        return isGuard
    }
}