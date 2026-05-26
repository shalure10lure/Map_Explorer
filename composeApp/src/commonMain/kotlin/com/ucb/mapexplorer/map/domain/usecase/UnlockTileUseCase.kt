package com.ucb.mapexplorer.map.domain.usecase

import com.ucb.mapexplorer.map.domain.model.UserLocationModel
import com.ucb.mapexplorer.map.domain.repository.MapRepository

class UnlockTileUseCase(
    private val repository: MapRepository
) {
    suspend operator fun invoke(
        uid: String,
        location: UserLocationModel
    ) {
        repository.unlockTile(uid, location)
    }
}