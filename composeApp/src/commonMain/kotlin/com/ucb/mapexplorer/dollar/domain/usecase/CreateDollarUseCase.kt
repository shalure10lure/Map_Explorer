package com.ucb.mapexplorer.dollar.domain.usecase

import com.ucb.mapexplorer.dollar.domain.model.DollarModel
import com.ucb.mapexplorer.dollar.domain.repository.DollarRepository

class CreateDollarUseCase(
    private val repository: DollarRepository
) {

    suspend fun invoke(model: DollarModel) {
        repository.save(model)
    }
}