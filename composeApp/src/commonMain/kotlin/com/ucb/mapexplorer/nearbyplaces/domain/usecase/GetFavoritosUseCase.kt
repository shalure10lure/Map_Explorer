package com.ucb.mapexplorer.nearbyplaces.domain.usecase

import com.ucb.mapexplorer.nearbyplaces.domain.model.LugarSavedModel
import com.ucb.mapexplorer.nearbyplaces.domain.repository.FavoritosRepository

class GetFavoritosUseCase(private val repository: FavoritosRepository) {
    suspend operator fun invoke(uid: String): List<LugarSavedModel> =
        repository.getFavoritos(uid)
}
