package com.ucb.mapexplorer.auth.data.service

import com.ucb.mapexplorer.auth.data.dao.AuthDao
import com.ucb.mapexplorer.auth.data.entity.UserEntity

class AuthDbService(val authDao: AuthDao) {
    suspend fun save(entity: UserEntity) = authDao.insert(entity)
    suspend fun getSession() = authDao.getSession()
    suspend fun clear() = authDao.deleteSession()
}