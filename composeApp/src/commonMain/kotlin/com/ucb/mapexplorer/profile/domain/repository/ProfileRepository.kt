package com.ucb.mapexplorer.profile.domain.repository

import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel
import com.ucb.mapexplorer.profile.domain.model.ProfileModel
import kotlinx.coroutines.flow.Flow



interface ProfileRepository {
    /** Observa los cambios del perfil en tiempo real. */
    fun observeProfile(uid: String): Flow<ProfileModel?>
    
    /** Obtiene el perfil actual una sola vez. */
    suspend fun getProfile(uid: String): ProfileModel?
    
    /** Actualiza el perfil y notifica a los observadores. */
    suspend fun updateProfile(profile: ProfileModel): Boolean
}
