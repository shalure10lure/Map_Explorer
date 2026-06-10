package com.ucb.mapexplorer.friends.domain.usecase

import com.ucb.mapexplorer.friends.domain.model.FriendRequestModel
import com.ucb.mapexplorer.friends.domain.repository.FriendsRepository

class GetPendingRequestsUseCase(private val repository: FriendsRepository) {
    suspend operator fun invoke(uid: String): List<FriendRequestModel> =
        repository.getPendingRequests(uid)
}