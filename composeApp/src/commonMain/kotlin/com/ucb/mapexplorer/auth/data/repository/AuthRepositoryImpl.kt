package com.ucb.mapexplorer.auth.data.repository

import com.ucb.mapexplorer.auth.data.datasource.FirebaseManager
import com.ucb.mapexplorer.auth.data.dto.UserDto
import com.ucb.mapexplorer.auth.data.mapper.toDto
import com.ucb.mapexplorer.auth.domain.model.UserModel
import com.ucb.mapexplorer.auth.domain.repository.AuthRepository
import kotlinx.serialization.json.Json

class AuthRepositoryImpl(
    private val firebase: FirebaseManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): Boolean {

        val json = firebase.getData("users/$email") ?: return false

        val dto = Json.decodeFromString<UserDto>(json)

        return dto.password == password
    }

    private fun safeKey(email: String): String {
        return email.replace(".", "_").replace("@", "_")
    }

    override suspend fun register(user: UserModel): Boolean {
        return try {

            val key = safeKey(user.email)

            val exist = firebase.getData("users/$key")
            if (exist != null) return false

            val json = Json.encodeToString(user.toDto())
            firebase.saveData("users/$key", json)

            true
        } catch (e: Exception) {
            false
        }
    }
}