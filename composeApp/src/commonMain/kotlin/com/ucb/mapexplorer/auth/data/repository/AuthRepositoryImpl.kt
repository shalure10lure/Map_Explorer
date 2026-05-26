package com.ucb.mapexplorer.auth.data.repository

import com.ucb.mapexplorer.auth.data.dao.AuthDao
import com.ucb.mapexplorer.auth.data.datasource.FirebaseManager
import com.ucb.mapexplorer.auth.data.dto.UserDto
import com.ucb.mapexplorer.auth.data.entity.UserEntity
import com.ucb.mapexplorer.auth.data.mapper.toDto
import com.ucb.mapexplorer.auth.domain.model.UserModel
import com.ucb.mapexplorer.auth.domain.repository.AuthRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class AuthRepositoryImpl(
    private val firebase: FirebaseManager,
    private val localDb: AuthDao
) : AuthRepository {

    override suspend fun login(email: String, password: String): Boolean {
        return try {
            val key = safeKey(email)
            val json = firebase.getData("users/$key") ?: return false
            val dto = Json.decodeFromString<UserDto>(json)

            if (dto.password == password) {
                localDb.insert(UserEntity(dto.email, dto.username, dto.description))
                return true
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun register(user: UserModel): Boolean {
        return try {
            val key = safeKey(user.email)
            val dto = user.toDto()
            val json = Json.encodeToString(dto)

            // 1. Guardar en Firebase (Lo principal)
            firebase.saveData("users/$key", json)

            // 2. Guardar sesión local de forma segura
            try {
                localDb.insert(UserEntity(dto.email, dto.username, dto.description))
            } catch (e: Exception) {
                println("Error local (ignorable): ${e.message}")
            }

            true // Retornamos true porque el registro en la nube fue exitoso
        } catch (e: Exception) {
            println("Error real en registro: ${e.message}")
            false
        }
    }
}

private fun safeKey(email: String): String {
    return email.replace(".", "_").replace("@", "_")
}