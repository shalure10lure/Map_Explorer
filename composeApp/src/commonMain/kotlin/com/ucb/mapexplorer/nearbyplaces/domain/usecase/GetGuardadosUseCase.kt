package com.ucb.mapexplorer.nearbyplaces.domain.usecase

import com.ucb.mapexplorer.nearbyplaces.domain.model.LugarSavedModel
import com.ucb.mapexplorer.nearbyplaces.domain.repository.GuardadosRepository

class GetGuardadosUseCase(private val repository: GuardadosRepository) {
    suspend operator fun invoke(uid: String): List<LugarSavedModel> =
        repository.getGuardados(uid)
}
