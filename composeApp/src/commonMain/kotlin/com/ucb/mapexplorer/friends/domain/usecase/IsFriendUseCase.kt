package com.ucb.mapexplorer.friends.domain.usecase

import com.ucb.mapexplorer.friends.domain.repository.FriendsRepository

class IsFriendUseCase(private val repository: FriendsRepository) {
    suspend operator fun invoke(myUid: String, otherUid: String): Boolean =
        repository.isFriend(myUid, otherUid)
}