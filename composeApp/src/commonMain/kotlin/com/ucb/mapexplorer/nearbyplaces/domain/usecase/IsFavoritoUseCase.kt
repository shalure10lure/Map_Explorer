package com.ucb.mapexplorer.nearbyplaces.domain.usecase

import com.ucb.mapexplorer.nearbyplaces.domain.repository.FavoritosRepository


class IsFavoritoUseCase(private val repository: FavoritosRepository) {
    suspend operator fun invoke(uid: String, lugarId: String): Boolean =
        repository.isFavorito(uid, lugarId)
}
