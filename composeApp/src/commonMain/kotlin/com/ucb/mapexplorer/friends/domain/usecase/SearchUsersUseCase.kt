package com.ucb.mapexplorer.friends.domain.usecase

import com.ucb.mapexplorer.friends.domain.repository.FriendsRepository

class SearchUsersUseCase(private val repository: FriendsRepository) {
    suspend operator fun invoke(query: String, currentUid: String) =
        repository.searchUsers(query, currentUid)
}