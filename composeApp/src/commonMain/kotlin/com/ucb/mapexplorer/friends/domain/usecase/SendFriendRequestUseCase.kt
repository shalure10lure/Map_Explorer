package com.ucb.mapexplorer.friends.domain.usecase

import com.ucb.mapexplorer.friends.domain.repository.FriendsRepository

class SendFriendRequestUseCase(private val repository: FriendsRepository) {
    suspend operator fun invoke(fromUid: String, toUid: String, fromUsername: String): Boolean =
        repository.sendFriendRequest(fromUid, toUid, fromUsername)
}