package com.ucb.mapexplorer.dollar.data.datasource

import com.ucb.mapexplorer.dollar.data.entity.DollarEntity

interface DollarLocalDataSource {
    suspend fun save(entity: DollarEntity)
    suspend fun list() : List<DollarEntity>
}