package com.ucb.mapexplorer.friends.data.repository

import com.ucb.mapexplorer.friends.data.datasource.FriendsRemoteDataSource
import com.ucb.mapexplorer.friends.domain.model.FriendModel
import com.ucb.mapexplorer.friends.domain.model.FriendRequestModel
import com.ucb.mapexplorer.friends.domain.model.UserSearchModel
import com.ucb.mapexplorer.friends.domain.repository.FriendsRepository

class FriendsRepositoryImpl(
    private val remote: FriendsRemoteDataSource
) : FriendsRepository {

    override suspend fun getFriends(uid: String): List<FriendModel> =
        remote.getFriends(uid)

    override suspend fun searchUsers(query: String, currentUid: String): List<UserSearchModel> {
        if (query.length < 2) return emptyList()
        return remote.searchUsers(query, currentUid)
    }

    override suspend fun sendFriendRequest(fromUid: String, toUid: String, fromUsername: String): Boolean =
        remote.sendFriendRequest(fromUid, toUid, fromUsername)

    override suspend fun getPendingRequests(uid: String): List<FriendRequestModel> =
        remote.getPendingRequests(uid)

    override suspend fun acceptFriendRequest(
        requestId: String,
        myUid: String,
        friendUid: String,
        myUsername: String,
        friendUsername: String
    ): Boolean = remote.acceptFriendRequest(requestId, myUid, friendUid, myUsername, friendUsername)

    override suspend fun declineFriendRequest(requestId: String): Boolean =
        remote.declineFriendRequest(requestId)

    override suspend fun removeFriend(myUid: String, friendUid: String): Boolean =
        remote.removeFriend(myUid, friendUid)

    override suspend fun isFriend(myUid: String, otherUid: String): Boolean =
        remote.isFriend(myUid, otherUid)

    override suspend fun getUserProfile(uid: String): UserSearchModel? =
        remote.getUserProfile(uid)

    override suspend fun hasPendingRequest(fromUid: String, toUid: String): Boolean =
        remote.hasPendingRequest(fromUid, toUid)
}
