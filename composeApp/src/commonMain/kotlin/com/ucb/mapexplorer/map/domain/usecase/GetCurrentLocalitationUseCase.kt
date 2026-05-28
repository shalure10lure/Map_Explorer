package com.ucb.mapexplorer.map.domain.usecase

import com.ucb.mapexplorer.map.domain.repository.MapRepository

class GetCurrentLocationUseCase(
    private val repository: MapRepository
) {
    operator fun invoke() = repository.observeLocation()
}