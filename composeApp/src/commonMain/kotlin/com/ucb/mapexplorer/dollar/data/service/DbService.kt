package com.ucb.mapexplorer.dollar.data.service

import com.ucb.mapexplorer.dollar.data.dao.DollarDao
import com.ucb.mapexplorer.dollar.data.datasource.DollarLocalDataSource
import com.ucb.mapexplorer.dollar.data.entity.DollarEntity


class DbService(val dollarDao: DollarDao): DollarLocalDataSource {
    override suspend fun save(entity: DollarEntity) {
        dollarDao.insert(entity)
    }

    override suspend fun list(): List<DollarEntity> {
        return dollarDao.getList()
    }
}