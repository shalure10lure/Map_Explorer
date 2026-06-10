package com.ucb.mapexplorer.nearbyplaces.domain.usecase

import com.ucb.mapexplorer.nearbyplaces.domain.repository.GuardadosRepository

class ToggleGuardadoUseCase(private val repository: GuardadosRepository) {
    suspend operator fun invoke(
        uid: String, lugarId: String, nombre: String,
        categoria: String, lat: Double, lon: Double, iconoCategoria: String
    ): Boolean = repository.toggleGuardado(uid, lugarId, nombre, categoria, lat, lon, iconoCategoria)
}