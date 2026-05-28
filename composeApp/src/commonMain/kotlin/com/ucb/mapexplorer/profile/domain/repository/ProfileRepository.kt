package com.ucb.mapexplorer.profile.domain.repository

import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel
import kotlinx.coroutines.flow.Flow

data class ProfileModel(
    val uid: String,
    val name: String,
    val email: String,
    val description: String,
    val avatarConfig: AvatarConfigModel,
    val level: Int = 1
)

interface ProfileRepository {
    /** Observa los cambios del perfil en tiempo real. */
    fun observeProfile(uid: String): Flow<ProfileModel?>
    
    /** Obtiene el perfil actual una sola vez. */
    suspend fun getProfile(uid: String): ProfileModel?
    
    /** Actualiza el perfil y notifica a los observadores. */
    suspend fun updateProfile(profile: ProfileModel): Boolean
}
