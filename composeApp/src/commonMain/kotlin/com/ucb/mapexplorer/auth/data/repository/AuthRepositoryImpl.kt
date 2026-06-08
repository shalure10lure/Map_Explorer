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
            val storedPassword = firebase.getData("usuarios/$uid/informacion/password")
                ?.trim('"') ?: return false

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

            // 1. Verificar que el email no esté ya registrado
            val existingPassword = firebase.getData("usuarios/$uid/informacion/password")
            if (!existingPassword.isNullOrBlank()) {
                println("❌ Registro fallido: email ya en uso")
                return false
            }

            // 2. Verificar que el username no esté ya en uso
            val usernameKey = user.username.trim().lowercase().replace(" ", "_")
            val existingUid = firebase.getData("usernames/$usernameKey")
            if (!existingUid.isNullOrBlank()) {
                println("❌ Registro fallido: username ya en uso")
                return false
            }

            // Guarda bajo usuarios/{uid}/informacion/
            firebase.saveData("usuarios/$uid/informacion/username",   user.username)
            firebase.saveData("usuarios/$uid/informacion/correo",     user.email)
            firebase.saveData("usuarios/$uid/informacion/password",   user.password)
            firebase.saveData("usuarios/$uid/informacion/descripcion",user.description ?: "")
            firebase.saveData("usuarios/$uid/informacion/edad",          user.age.toString())
            firebase.saveData("usuarios/$uid/informacion/fecha_creacion", now.toString())

            if (!user.photoUrl.isNullOrEmpty()) {
                firebase.saveData("usuarios/$uid/informacion/avatar_id", user.photoUrl)
            }
            // También registra el username como único en el nodo usernames
            firebase.saveData("usernames/$uid", user.username)

            // 5. Estadísticas iniciales
            firebase.saveData("usuarios/$uid/estadisticas/nivel",              "1")
            firebase.saveData("usuarios/$uid/estadisticas/experiencia",        "0")
            firebase.saveData("usuarios/$uid/estadisticas/tiles_descubiertos", "0")
            firebase.saveData("usuarios/$uid/estadisticas/lugares_visitados",  "0")


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