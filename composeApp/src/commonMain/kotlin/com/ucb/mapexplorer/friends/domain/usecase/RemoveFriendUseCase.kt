package com.ucb.mapexplorer.friends.domain.usecase

import com.ucb.mapexplorer.friends.domain.repository.FriendsRepository

class RemoveFriendUseCase(private val repository: FriendsRepository) {
    suspend operator fun invoke(myUid: String, friendUid: String): Boolean =
        repository.removeFriend(myUid, friendUid)
}