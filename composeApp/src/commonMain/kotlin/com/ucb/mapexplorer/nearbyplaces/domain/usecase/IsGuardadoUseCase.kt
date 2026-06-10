package com.ucb.mapexplorer.nearbyplaces.domain.usecase

import com.ucb.mapexplorer.nearbyplaces.domain.repository.GuardadosRepository

class IsGuardadoUseCase(private val repository: GuardadosRepository) {
    suspend operator fun invoke(uid: String, lugarId: String): Boolean =
        repository.isGuardado(uid, lugarId)
}