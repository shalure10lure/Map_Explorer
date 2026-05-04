package com.ucb.mapexplorer.dollar.domain.repository

import com.ucb.mapexplorer.dollar.domain.model.DollarModel

interface DollarRepository {
    suspend fun save(model: DollarModel)
    suspend fun getList(): List<DollarModel>
}