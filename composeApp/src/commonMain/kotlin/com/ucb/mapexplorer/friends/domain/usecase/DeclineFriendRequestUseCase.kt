package com.ucb.mapexplorer.friends.domain.usecase

import com.ucb.mapexplorer.friends.domain.repository.FriendsRepository

class DeclineFriendRequestUseCase(private val repository: FriendsRepository) {
    suspend operator fun invoke(requestId: String): Boolean =
        repository.declineFriendRequest(requestId)
}