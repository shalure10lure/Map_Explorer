package com.ucb.mapexplorer.friends.data.datasource

import com.ucb.mapexplorer.friends.domain.model.FriendModel
import com.ucb.mapexplorer.friends.domain.model.FriendRequestModel
import com.ucb.mapexplorer.friends.domain.model.UserSearchModel

expect class FriendsRemoteDataSource() {
    suspend fun getFriends(uid: String): List<FriendModel>
    suspend fun isFriend(myUid: String, otherUid: String): Boolean
    suspend fun removeFriend(myUid: String, friendUid: String): Boolean
    suspend fun sendFriendRequest(fromUid: String, toUid: String, fromUsername: String): Boolean
    suspend fun getPendingRequests(uid: String): List<FriendRequestModel>
    suspend fun acceptFriendRequest(requestId: String, myUid: String, friendUid: String, myUsername: String, friendUsername: String): Boolean
    suspend fun declineFriendRequest(requestId: String): Boolean
    suspend fun hasPendingRequest(fromUid: String, toUid: String): Boolean
    suspend fun searchUsers(query: String, currentUid: String): List<UserSearchModel>
    suspend fun getUserProfile(uid: String): UserSearchModel?
}
