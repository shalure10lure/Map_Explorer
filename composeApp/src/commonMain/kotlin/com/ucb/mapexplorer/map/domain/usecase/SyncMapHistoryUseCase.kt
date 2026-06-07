package com.ucb.mapexplorer.map.domain.usecase

import com.ucb.mapexplorer.map.domain.repository.MapRepository

class SyncMapHistoryUseCase(private val repository: MapRepository) {

    suspend operator fun invoke(uid: String) {
        // Simplemente delegamos la lógica al repositorio
        repository.downloadHistoryIfEmpty(uid)
    }
}