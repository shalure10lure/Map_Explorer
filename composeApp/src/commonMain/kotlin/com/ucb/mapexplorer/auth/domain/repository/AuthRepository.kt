package com.ucb.mapexplorer.auth.domain.repository

import com.ucb.mapexplorer.auth.domain.model.UserModel

interface AuthRepository {
    suspend fun login(email: String, password: String): Boolean
    suspend fun register(user: UserModel): Boolean
}