package com.ucb.mapexplorer.friends.domain.usecase

import com.ucb.mapexplorer.friends.domain.repository.FriendsRepository

class AcceptFriendRequestUseCase(private val repository: FriendsRepository) {
    suspend operator fun invoke(
        requestId: String,
        myUid: String,
        friendUid: String,
        myUsername: String,
        friendUsername: String
    ): Boolean = repository.acceptFriendRequest(requestId, myUid, friendUid, myUsername, friendUsername)
}
