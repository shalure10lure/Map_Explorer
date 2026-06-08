package com.ucb.mapexplorer.dangerzone.domain.usecase

import com.ucb.mapexplorer.dangerzone.domain.repository.DangerZoneRepository


class SyncDangerZonesUseCase(private val repository: DangerZoneRepository) {
    suspend operator fun invoke() = repository.syncZonas()
}
