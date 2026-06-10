package com.ucb.mapexplorer.friends.domain.repository

import com.ucb.mapexplorer.friends.domain.model.FriendModel
import com.ucb.mapexplorer.friends.domain.model.FriendRequestModel
import com.ucb.mapexplorer.friends.domain.model.UserSearchModel

interface FriendsRepository {

    /** Obtiene la lista de amigos del usuario */
    suspend fun getFriends(uid: String): List<FriendModel>

    /** Busca usuarios por username, uid o correo */
    suspend fun searchUsers(query: String, currentUid: String): List<UserSearchModel>

    /** Envía solicitud de amistad */
    suspend fun sendFriendRequest(fromUid: String, toUid: String, fromUsername: String): Boolean

    /** Obtiene solicitudes de amistad recibidas (pendientes) */
    suspend fun getPendingRequests(uid: String): List<FriendRequestModel>

    /** Acepta solicitud de amistad */
    suspend fun acceptFriendRequest(requestId: String, myUid: String, friendUid: String, myUsername: String, friendUsername: String): Boolean

    /** Rechaza solicitud de amistad */
    suspend fun declineFriendRequest(requestId: String): Boolean

    /** Elimina un amigo */
    suspend fun removeFriend(myUid: String, friendUid: String): Boolean

    /** Verifica si dos usuarios son amigos */
    suspend fun isFriend(myUid: String, otherUid: String): Boolean

    /** Obtiene el perfil público de otro usuario */
    suspend fun getUserProfile(uid: String): UserSearchModel?

    /** Verifica si ya hay solicitud pendiente entre dos usuarios */
    suspend fun hasPendingRequest(fromUid: String, toUid: String): Boolean

    /** Inicia la observación de solicitudes para notificaciones en tiempo real */
    suspend fun startObservingRequests(uid: String)
}
