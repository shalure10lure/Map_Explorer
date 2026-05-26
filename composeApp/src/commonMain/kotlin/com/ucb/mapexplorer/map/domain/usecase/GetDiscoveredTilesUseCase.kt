package com.ucb.mapexplorer.map.domain.usecase

import com.ucb.mapexplorer.map.domain.repository.MapRepository

class GetDiscoveredTilesUseCase(
    private val repository: MapRepository
) {
    suspend operator fun invoke(uid: String) =
        repository.getDiscoveredTiles(uid)
}