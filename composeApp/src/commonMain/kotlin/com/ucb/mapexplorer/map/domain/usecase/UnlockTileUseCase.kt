package com.ucb.mapexplorer.map.domain.usecase

import com.ucb.mapexplorer.map.domain.model.UserLocationModel
import com.ucb.mapexplorer.map.domain.repository.MapRepository

class UnlockTileUseCase(
    private val repository: MapRepository
) {
    /** @return true si se descubrió un tile NUEVO. */
    suspend operator fun invoke(uid: String, location: UserLocationModel): Boolean {
        return repository.unlockTile(uid, location)
    }
}
