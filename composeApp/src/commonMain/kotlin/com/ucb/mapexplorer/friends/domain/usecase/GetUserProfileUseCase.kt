package com.ucb.mapexplorer.friends.domain.usecase

import com.ucb.mapexplorer.friends.domain.repository.FriendsRepository

class GetUserProfileUseCase(private val repository: FriendsRepository) {
    suspend operator fun invoke(uid: String) =
        repository.getUserProfile(uid)
}