package com.ucb.mapexplorer.dollar.domain.usecase

import com.ucb.mapexplorer.dollar.domain.model.DollarModel
import com.ucb.mapexplorer.dollar.domain.repository.DollarRepository

class GetDollarListUsecase(
    val repository: DollarRepository
) {

    suspend fun invoke(): List<DollarModel> {
        return repository.getList()
    }
}