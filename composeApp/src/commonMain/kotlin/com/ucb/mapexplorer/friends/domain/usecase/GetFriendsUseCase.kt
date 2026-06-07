package com.ucb.mapexplorer.friends.domain.usecase

import com.ucb.mapexplorer.friends.domain.model.FriendModel
import com.ucb.mapexplorer.friends.domain.repository.FriendsRepository

class GetFriendsUseCase(private val repository: FriendsRepository) {
    suspend operator fun invoke(uid: String): List<FriendModel> =
        repository.getFriends(uid)
}