package com.ucb.mapexplorer.auth.domain.usecase

import com.ucb.mapexplorer.auth.domain.model.UserModel
import com.ucb.mapexplorer.auth.domain.repository.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(user: UserModel): Boolean {
        return repository.register(user)
    }
}