package com.ucb.mapexplorer.auth.data.repository

import com.ucb.mapexplorer.auth.data.dao.AuthDao
import com.ucb.mapexplorer.auth.data.datasource.FirebaseManager
import com.ucb.mapexplorer.auth.data.entity.UserEntity
import com.ucb.mapexplorer.auth.domain.model.UserModel
import com.ucb.mapexplorer.auth.domain.repository.AuthRepository
import kotlinx.datetime.Clock

class AuthRepositoryImpl(
    private val firebase: FirebaseManager,
    private val localDb: AuthDao
) : AuthRepository {

    override suspend fun login(email: String, password: String): Boolean {
        return try {
            val uid = safeKey(email)

            // Lee la contraseña directamente del campo
            val storedPassword = firebase.getData(
                "usuarios/$uid/informacion/password"
            ) ?: return false

            if (storedPassword.trim('"') == password) {
                // Guarda sesión local
                val username = firebase.getData(
                    "usuarios/$uid/informacion/username"
                )?.trim('"') ?: ""
                val descripcion = firebase.getData(
                    "usuarios/$uid/informacion/descripcion"
                )?.trim('"')

                localDb.insert(UserEntity(email, username, descripcion))
                true
            } else {
                false
            }
        } catch (e: Exception) {
            println("Error login: ${e.message}")
            false
        }
    }

    override suspend fun register(user: UserModel): Boolean {
        return try {
            val uid = safeKey(user.email)
            val now = Clock.System.now().toEpochMilliseconds()

            // Guarda bajo usuarios/{uid}/informacion/
            firebase.saveData("usuarios/$uid/informacion/username",   user.username)
            firebase.saveData("usuarios/$uid/informacion/correo",     user.email)
            firebase.saveData("usuarios/$uid/informacion/password",   user.password)
            firebase.saveData("usuarios/$uid/informacion/descripcion",user.description ?: "")
            firebase.saveData("usuarios/$uid/informacion/fecha_creacion", now.toString())

            // También registra el username como único en el nodo usernames
            firebase.saveData("usernames/$uid", user.username)

            // Guarda sesión local
            try {
                localDb.insert(UserEntity(user.email, user.username, user.description))
            } catch (e: Exception) {
                println("Error local (ignorable): ${e.message}")
            }

            true
        } catch (e: Exception) {
            println("Error registro: ${e.message}")
            false
        }
    }
}

private fun safeKey(email: String): String =
    email.trim().lowercase()
        .replace("@", "_")
        .replace(".", "_")