package com.ucb.mapexplorer.profile.data.repository

import com.ucb.mapexplorer.auth.data.dao.AuthDao
import com.ucb.mapexplorer.auth.data.datasource.FirebaseManager
import com.ucb.mapexplorer.auth.data.entity.UserEntity
import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel
import com.ucb.mapexplorer.profile.domain.repository.ProfileModel
import com.ucb.mapexplorer.profile.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onStart

class ProfileRepositoryImpl(
    private val firebase: FirebaseManager,
    private val localDb: AuthDao
) : ProfileRepository {

    private val profileUpdates = MutableSharedFlow<ProfileModel>(replay = 1)

    override fun observeProfile(uid: String): Flow<ProfileModel?> {
        return profileUpdates
            .filter { it.uid == uid }
            .onStart {
                getProfile(uid)?.let { emit(it) }
            }
    }

    override suspend fun getProfile(uid: String): ProfileModel? {
        return try {
            val username = firebase.getData("usuarios/$uid/informacion/username")?.trim('"') ?: ""
            val correo = firebase.getData("usuarios/$uid/informacion/correo")?.trim('"') ?: ""
            val descripcion = firebase.getData("usuarios/$uid/informacion/descripcion")?.trim('"') ?: ""
            val avatarId = firebase.getData("usuarios/$uid/informacion/avatar_id")?.trim('"') ?: ""
            
            val profile = ProfileModel(
                uid = uid,
                name = username,
                email = correo,
                description = descripcion,
                avatarConfig = if (avatarId.isNotEmpty()) AvatarConfigModel.fromId(avatarId) else AvatarConfigModel()
            )
            
            localDb.insert(UserEntity(correo, username, descripcion))
            profile
        } catch (e: Exception) {
            val local = localDb.getUserByEmail(uid.replace("_", "."))
            local?.let {
                ProfileModel(
                    uid = uid,
                    name = it.username,
                    email = it.email,
                    description = it.description ?: "", // Corregido: de descripcion a description
                    avatarConfig = AvatarConfigModel()
                )
            }
        }
    }

    override suspend fun updateProfile(profile: ProfileModel): Boolean {
        return try {
            val uid = profile.uid
            firebase.saveData("usuarios/$uid/informacion/username", profile.name)
            firebase.saveData("usuarios/$uid/informacion/descripcion", profile.description)
            firebase.saveData("usuarios/$uid/informacion/avatar_id", profile.avatarConfig.toId())
            
            localDb.insert(UserEntity(profile.email, profile.name, profile.description))
            profileUpdates.emit(profile)
            true
        } catch (e: Exception) {
            println("Error updating profile: ${e.message}")
            false
        }
    }
}
