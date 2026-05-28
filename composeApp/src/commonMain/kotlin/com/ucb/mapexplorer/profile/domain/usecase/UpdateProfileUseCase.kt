package com.ucb.mapexplorer.profile.domain.usecase

import com.ucb.mapexplorer.profile.domain.repository.ProfileModel
import com.ucb.mapexplorer.profile.domain.repository.ProfileRepository

class UpdateProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(profile: ProfileModel): Boolean {
        return repository.updateProfile(profile)
    }
}
