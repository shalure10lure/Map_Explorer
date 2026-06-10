package com.ucb.mapexplorer.friends.domain.usecase

import com.ucb.mapexplorer.friends.domain.repository.FriendsRepository

class ObserveFriendRequestsUseCase(
    private val repository: FriendsRepository
) {
    suspend operator fun invoke(uid: String) {
        repository.startObservingRequests(uid)
    }
}
