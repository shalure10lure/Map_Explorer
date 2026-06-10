package com.ucb.mapexplorer.friends.data.datasource

import com.ucb.mapexplorer.friends.domain.model.FriendModel
import com.ucb.mapexplorer.friends.domain.model.FriendRequestModel
import com.ucb.mapexplorer.friends.domain.model.UserSearchModel
import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.*

actual class FriendsRemoteDataSource actual constructor() {

    private val FIREBASE_URL = "https://mapexplorer07-default-rtdb.firebaseio.com"
    private val client = HttpClient(Darwin) {
        install(HttpTimeout) { requestTimeoutMillis = 20_000 }
    }
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    // ── helpers ────────────────────────────────────────────────────────────

    private suspend fun getStr(path: String): String? {
        return try {
            val r = client.get("$FIREBASE_URL/$path.json").bodyAsText()
            if (r == "null") null else r.trim('"')
        } catch (e: Exception) { null }
    }

    private suspend fun getNode(path: String): JsonObject? {
        return try {
            val r = client.get("$FIREBASE_URL/$path.json").bodyAsText()
            if (r == "null") null
            else json.parseToJsonElement(r).jsonObject
        } catch (e: Exception) { null }
    }

    private suspend fun putValue(path: String, body: String) {
        try {
            client.put("$FIREBASE_URL/$path.json") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        } catch (e: Exception) { println("iOS Firebase PUT error: ${e.message}") }
    }

    private suspend fun deleteNode(path: String) {
        try { client.delete("$FIREBASE_URL/$path.json") }
        catch (e: Exception) { println("iOS Firebase DELETE error: ${e.message}") }
    }

    // ── AMIGOS ─────────────────────────────────────────────────────────────

    actual suspend fun getFriends(uid: String): List<FriendModel> {
        return try {
            val node = getNode("amistades/$uid") ?: return emptyList()
            node.entries.mapNotNull { (friendUid, _) ->
                val info = getNode("usuarios/$friendUid/informacion") ?: return@mapNotNull null
                FriendModel(
                    uid         = friendUid,
                    username    = info["username"]?.jsonPrimitive?.content ?: friendUid,
                    description = info["descripcion"]?.jsonPrimitive?.content ?: "",
                    avatarId    = info["avatar_id"]?.jsonPrimitive?.content ?: "",
                    desde       = node[friendUid]?.jsonObject?.get("desde")?.jsonPrimitive?.long ?: 0L
                )
            }
        } catch (e: Exception) {
            println("iOS getFriends error: ${e.message}")
            emptyList()
        }
    }

    actual suspend fun isFriend(myUid: String, otherUid: String): Boolean {
        return try {
            val r = client.get("$FIREBASE_URL/amistades/$myUid/$otherUid.json").bodyAsText()
            r != "null"
        } catch (e: Exception) { false }
    }

    actual suspend fun removeFriend(myUid: String, friendUid: String): Boolean {
        return try {
            deleteNode("amistades/$myUid/$friendUid")
            deleteNode("amistades/$friendUid/$myUid")
            true
        } catch (e: Exception) { false }
    }

    // ── SOLICITUDES ────────────────────────────────────────────────────────

    actual suspend fun sendFriendRequest(fromUid: String, toUid: String, fromUsername: String): Boolean {
        return try {
            if (hasPendingRequest(fromUid, toUid)) return false
            val now = Clock.System.now().toEpochMilliseconds()
            // Usamos push via POST
            val body = """{"emisor_uid":"$fromUid","emisor_username":"$fromUsername","receptor_uid":"$toUid","estado":"pendiente","fecha":$now}"""
            client.post("$FIREBASE_URL/solicitudes_amistad.json") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
            true
        } catch (e: Exception) {
            println("iOS sendFriendRequest error: ${e.message}")
            false
        }
    }

    actual suspend fun getPendingRequests(uid: String): List<FriendRequestModel> {
        return try {
            val node = getNode("solicitudes_amistad") ?: return emptyList()
            node.entries.mapNotNull { (requestId, element) ->
                val obj = element.jsonObject
                val receptorUid = obj["receptor_uid"]?.jsonPrimitive?.content ?: ""
                val estado = obj["estado"]?.jsonPrimitive?.content ?: ""
                if (receptorUid != uid || estado != "pendiente") return@mapNotNull null
                FriendRequestModel(
                    requestId      = requestId,
                    emisorUid      = obj["emisor_uid"]?.jsonPrimitive?.content ?: "",
                    emisorUsername = obj["emisor_username"]?.jsonPrimitive?.content ?: "",
                    receptorUid    = receptorUid,
                    estado         = estado,
                    fecha          = obj["fecha"]?.jsonPrimitive?.long ?: 0L
                )
            }
        } catch (e: Exception) {
            println("iOS getPendingRequests error: ${e.message}")
            emptyList()
        }
    }

    actual suspend fun acceptFriendRequest(
        requestId: String, myUid: String, friendUid: String,
        myUsername: String, friendUsername: String
    ): Boolean {
        return try {
            val now = Clock.System.now().toEpochMilliseconds()
            putValue("solicitudes_amistad/$requestId/estado", "\"aceptado\"")
            putValue("amistades/$myUid/$friendUid",
                """{"desde":$now,"username":"$friendUsername"}""")
            putValue("amistades/$friendUid/$myUid",
                """{"desde":$now,"username":"$myUsername"}""")
            true
        } catch (e: Exception) { false }
    }

    actual suspend fun declineFriendRequest(requestId: String): Boolean {
        return try {
            putValue("solicitudes_amistad/$requestId/estado", "\"rechazado\"")
            true
        } catch (e: Exception) { false }
    }

    actual suspend fun hasPendingRequest(fromUid: String, toUid: String): Boolean {
        return try {
            val node = getNode("solicitudes_amistad") ?: return false
            node.values.any { element ->
                val obj = element.jsonObject
                obj["emisor_uid"]?.jsonPrimitive?.content == fromUid &&
                        obj["receptor_uid"]?.jsonPrimitive?.content == toUid &&
                        obj["estado"]?.jsonPrimitive?.content == "pendiente"
            }
        } catch (e: Exception) { false }
    }

    actual suspend fun startObservingRequests(uid: String) {
        // iOS real-time stubs if using REST. 
        // En iOS con REST puro no hay addChildEventListener nativo sin SDK de Firebase.
    }

    // ── BÚSQUEDA ───────────────────────────────────────────────────────────

    actual suspend fun searchUsers(query: String, currentUid: String): List<UserSearchModel> {
        return try {
            val lowerQuery = query.trim().lowercase()
            val node = getNode("usuarios") ?: return emptyList()
            node.entries.mapNotNull { (uid, element) ->
                if (uid == currentUid) return@mapNotNull null
                val info = element.jsonObject["informacion"]?.jsonObject ?: return@mapNotNull null
                val username = info["username"]?.jsonPrimitive?.content ?: ""
                val correo   = info["correo"]?.jsonPrimitive?.content ?: ""
                val desc     = info["descripcion"]?.jsonPrimitive?.content ?: ""
                val avatarId = info["avatar_id"]?.jsonPrimitive?.content ?: ""

                val matches = username.lowercase().contains(lowerQuery) ||
                        uid.lowercase().contains(lowerQuery) ||
                        correo.lowercase().contains(lowerQuery)
                if (matches && username.isNotBlank())
                    UserSearchModel(uid, username, desc, avatarId)
                else null
            }.take(20)
        } catch (e: Exception) {
            println("iOS searchUsers error: ${e.message}")
            emptyList()
        }
    }

    actual suspend fun getUserProfile(uid: String): UserSearchModel? {
        return try {
            val info = getNode("usuarios/$uid/informacion") ?: return null
            UserSearchModel(
                uid         = uid,
                username    = info["username"]?.jsonPrimitive?.content ?: "",
                description = info["descripcion"]?.jsonPrimitive?.content ?: "",
                avatarId    = info["avatar_id"]?.jsonPrimitive?.content ?: ""
            )
        } catch (e: Exception) { null }
    }
}
