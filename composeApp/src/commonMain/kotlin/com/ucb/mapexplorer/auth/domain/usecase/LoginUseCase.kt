package com.ucb.mapexplorer.auth.domain.usecase

import com.ucb.mapexplorer.auth.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(user: String, pass: String): Boolean {
        return repository.login(user, pass)
    }
}