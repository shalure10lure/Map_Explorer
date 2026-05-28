package com.ucb.mapexplorer.profile.domain.usecase

import com.ucb.mapexplorer.profile.domain.repository.ProfileModel
import com.ucb.mapexplorer.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow

class ObserveProfileUseCase(
    private val repository: ProfileRepository
) {
    operator fun invoke(uid: String): Flow<ProfileModel?> {
        return repository.observeProfile(uid)
    }
}
