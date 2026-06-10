package com.ucb.mapexplorer.friends.data.datasource

import com.google.firebase.database.FirebaseDatabase
import com.ucb.mapexplorer.friends.domain.model.FriendModel
import com.ucb.mapexplorer.friends.domain.model.FriendRequestModel
import com.ucb.mapexplorer.friends.domain.model.UserSearchModel
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock

actual class FriendsRemoteDataSource actual constructor() {

    private val db = FirebaseDatabase.getInstance().reference

    // ── AMIGOS ────────────────────────────────────────────────────────────

    actual suspend fun getFriends(uid: String): List<FriendModel> {
        return try {
            val snapshot = db.child("amistades").child(uid).get().await()
            snapshot.children.mapNotNull { child ->
                val friendUid = child.key ?: return@mapNotNull null
                // Obtenemos username del amigo desde usuarios/{friendUid}/informacion/username
                val username = db
                    .child("usuarios").child(friendUid)
                    .child("informacion").child("username")
                    .get().await()
                    .getValue(String::class.java) ?: friendUid
                val description = db
                    .child("usuarios").child(friendUid)
                    .child("informacion").child("descripcion")
                    .get().await()
                    .getValue(String::class.java) ?: ""
                val avatarId = db
                    .child("usuarios").child(friendUid)
                    .child("informacion").child("avatar_id")
                    .get().await()
                    .getValue(String::class.java) ?: ""
                val desde = child.child("desde").getValue(Long::class.java) ?: 0L
                FriendModel(
                    uid = friendUid,
                    username = username,
                    description = description,
                    avatarId = avatarId,
                    desde = desde
                )
            }
        } catch (e: Exception) {
            println("❌ Error getFriends: ${e.message}")
            emptyList()
        }
    }

    actual suspend fun isFriend(myUid: String, otherUid: String): Boolean {
        return try {
            db.child("amistades").child(myUid).child(otherUid).get().await().exists()
        } catch (e: Exception) { false }
    }

    actual suspend fun removeFriend(myUid: String, friendUid: String): Boolean {
        return try {
            // Eliminamos en ambas direcciones
            db.child("amistades").child(myUid).child(friendUid).removeValue().await()
            db.child("amistades").child(friendUid).child(myUid).removeValue().await()
            true
        } catch (e: Exception) {
            println("❌ Error removeFriend: ${e.message}")
            false
        }
    }

    // ── SOLICITUDES ──────────────────────────────────────────────────────

    actual suspend fun sendFriendRequest(
        fromUid: String,
        toUid: String,
        fromUsername: String
    ): Boolean {
        return try {
            // Verificamos que no exista ya
            val existing = hasPendingRequest(fromUid, toUid)
            if (existing) return false

            val requestId = db.child("solicitudes_amistad").push().key ?: return false
            val now = Clock.System.now().toEpochMilliseconds()
            db.child("solicitudes_amistad").child(requestId).setValue(
                mapOf(
                    "emisor_uid"     to fromUid,
                    "emisor_username" to fromUsername,
                    "receptor_uid"   to toUid,
                    "estado"         to "pendiente",
                    "fecha"          to now
                )
            ).await()
            true
        } catch (e: Exception) {
            println("❌ Error sendFriendRequest: ${e.message}")
            false
        }
    }

    actual suspend fun getPendingRequests(uid: String): List<FriendRequestModel> {
        return try {
            val snapshot = db.child("solicitudes_amistad").get().await()
            snapshot.children.mapNotNull { child ->
                val requestId = child.key ?: return@mapNotNull null
                val receptorUid = child.child("receptor_uid").getValue(String::class.java) ?: ""
                val estado = child.child("estado").getValue(String::class.java) ?: ""
                // Solo las pendientes que son PARA este usuario
                if (receptorUid != uid || estado != "pendiente") return@mapNotNull null

                FriendRequestModel(
                    requestId = requestId,
                    emisorUid = child.child("emisor_uid").getValue(String::class.java) ?: "",
                    emisorUsername = child.child("emisor_username").getValue(String::class.java) ?: "",
                    receptorUid = receptorUid,
                    estado = estado,
                    fecha = child.child("fecha").getValue(Long::class.java) ?: 0L
                )
            }
        } catch (e: Exception) {
            println("❌ Error getPendingRequests: ${e.message}")
            emptyList()
        }
    }

    actual suspend fun acceptFriendRequest(
        requestId: String,
        myUid: String,
        friendUid: String,
        myUsername: String,
        friendUsername: String
    ): Boolean {
        return try {
            val now = Clock.System.now().toEpochMilliseconds()
            // 1. Actualizar estado de la solicitud
            db.child("solicitudes_amistad").child(requestId)
                .child("estado").setValue("aceptado").await()
            // 2. Crear amistad en AMBAS direcciones
            db.child("amistades").child(myUid).child(friendUid)
                .setValue(mapOf("desde" to now, "username" to friendUsername)).await()
            db.child("amistades").child(friendUid).child(myUid)
                .setValue(mapOf("desde" to now, "username" to myUsername)).await()
            true
        } catch (e: Exception) {
            println("❌ Error acceptFriendRequest: ${e.message}")
            false
        }
    }

    actual suspend fun declineFriendRequest(requestId: String): Boolean {
        return try {
            db.child("solicitudes_amistad").child(requestId)
                .child("estado").setValue("rechazado").await()
            true
        } catch (e: Exception) {
            println("❌ Error declineFriendRequest: ${e.message}")
            false
        }
    }

    actual suspend fun hasPendingRequest(fromUid: String, toUid: String): Boolean {
        return try {
            val snapshot = db.child("solicitudes_amistad").get().await()
            snapshot.children.any { child ->
                val emisor = child.child("emisor_uid").getValue(String::class.java) ?: ""
                val receptor = child.child("receptor_uid").getValue(String::class.java) ?: ""
                val estado = child.child("estado").getValue(String::class.java) ?: ""
                emisor == fromUid && receptor == toUid && estado == "pendiente"
            }
        } catch (e: Exception) { false }
    }

    // ── BÚSQUEDA DE USUARIOS ─────────────────────────────────────────────

    actual suspend fun searchUsers(query: String, currentUid: String): List<UserSearchModel> {
        return try {
            val lowerQuery = query.trim().lowercase()
            val snapshot = db.child("usuarios").get().await()
            snapshot.children.mapNotNull { child ->
                val uid = child.key ?: return@mapNotNull null
                if (uid == currentUid) return@mapNotNull null

                val info = child.child("informacion")
                val username = info.child("username").getValue(String::class.java) ?: ""
                val correo = info.child("correo").getValue(String::class.java) ?: ""
                val descripcion = info.child("descripcion").getValue(String::class.java) ?: ""
                val avatarId = info.child("avatar_id").getValue(String::class.java) ?: ""

                // Busca por username, uid o correo
                val matches = username.lowercase().contains(lowerQuery) ||
                        uid.lowercase().contains(lowerQuery) ||
                        correo.lowercase().contains(lowerQuery)

                if (matches && username.isNotBlank()) {
                    UserSearchModel(
                        uid = uid,
                        username = username,
                        description = descripcion,
                        avatarId = avatarId
                    )
                } else null
            }.take(20) // Máximo 20 resultados
        } catch (e: Exception) {
            println("❌ Error searchUsers: ${e.message}")
            emptyList()
        }
    }

    actual suspend fun getUserProfile(uid: String): UserSearchModel? {
        return try {
            val info = db.child("usuarios").child(uid).child("informacion").get().await()
            UserSearchModel(
                uid = uid,
                username = info.child("username").getValue(String::class.java) ?: "",
                description = info.child("descripcion").getValue(String::class.java) ?: "",
                avatarId = info.child("avatar_id").getValue(String::class.java) ?: ""
            )
        } catch (e: Exception) {
            println("❌ Error getUserProfile: ${e.message}")
            null
        }
    }
}
