package com.ucb.mapexplorer.profile.domain.usecase

import com.ucb.mapexplorer.profile.domain.repository.ProfileModel
import com.ucb.mapexplorer.profile.domain.repository.ProfileRepository

class GetProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(uid: String): ProfileModel? {
        return repository.getProfile(uid)
    }
}
