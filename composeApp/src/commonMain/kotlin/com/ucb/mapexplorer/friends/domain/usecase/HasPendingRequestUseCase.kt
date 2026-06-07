package com.ucb.mapexplorer.friends.domain.usecase

import com.ucb.mapexplorer.friends.domain.repository.FriendsRepository

class HasPendingRequestUseCase(private val repository: FriendsRepository) {
    suspend operator fun invoke(fromUid: String, toUid: String): Boolean =
        repository.hasPendingRequest(fromUid, toUid)
}
