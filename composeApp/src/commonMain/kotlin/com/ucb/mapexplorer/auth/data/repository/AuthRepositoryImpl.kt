package com.ucb.mapexplorer.auth.data.repository

import com.ucb.mapexplorer.auth.data.dao.AuthDao
import com.ucb.mapexplorer.auth.data.datasource.FirebaseManager
import com.ucb.mapexplorer.auth.data.dto.UserDto
import com.ucb.mapexplorer.auth.data.entity.UserEntity
import com.ucb.mapexplorer.auth.data.mapper.toDto
import com.ucb.mapexplorer.auth.data.mapper.toEntity
import com.ucb.mapexplorer.auth.domain.model.UserModel
import com.ucb.mapexplorer.auth.domain.repository.AuthRepository
import kotlinx.serialization.json.Json

class AuthRepositoryImpl(
    private val firebase: FirebaseManager,
    private val localDb: AuthDao
) : AuthRepository {

    override suspend fun login(email: String, password: String): Boolean {

        val key = safeKey(email)
        val json = firebase.getData("users/$key") ?: return false
        val dto = Json.decodeFromString<UserDto>(json)

        if (dto.password == password) {
            // Guardamos en Room
            localDb.insert(UserEntity(dto.email, dto.username, dto.description))
            return true
        }
        return false
    }

    override suspend fun register(user: UserModel): Boolean {
        return try {

            val key = safeKey(user.email)

            val exist = firebase.getData("users/$key")
            if (exist != null) return false

            val dto = user.toDto()
            val json = Json.encodeToString(dto)
            firebase.saveData("users/$key", json)

            localDb.insert(UserEntity(dto.email, dto.username, dto.description))

            true
        } catch (e: Exception) {
            false
        }
    }
}
private fun safeKey(email: String): String {
    return email.replace(".", "_").replace("@", "_")
}