package com.ucb.mapexplorer.editProfile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.core.session.Session
import com.ucb.mapexplorer.editProfile.presentation.state.EditProfileEffect
import com.ucb.mapexplorer.editProfile.presentation.state.EditProfileEvent
import com.ucb.mapexplorer.editProfile.presentation.state.EditProfileUIState
import com.ucb.mapexplorer.profile.domain.repository.ProfileModel
import com.ucb.mapexplorer.profile.domain.usecase.GetProfileUseCase
import com.ucb.mapexplorer.profile.domain.usecase.UpdateProfileUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(EditProfileUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<EditProfileEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadCurrentProfile()
    }

    private fun loadCurrentProfile() {
        val uid = Session.uid ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val profile = getProfileUseCase(uid)
            profile?.let { data ->
                _state.update { it.copy(
                    name = data.name,
                    description = data.description,
                    email = data.email,
                    avatarConfig = data.avatarConfig,
                    isLoading = false
                )}
            } ?: _state.update { it.copy(isLoading = false) }
        }
    }

    fun onEvent(event: EditProfileEvent) {
        when (event) {
            is EditProfileEvent.OnNameChange -> _state.update { it.copy(name = event.name) }
            is EditProfileEvent.OnDescriptionChange -> _state.update { it.copy(description = event.description) }
            is EditProfileEvent.OnEmailChange -> _state.update { it.copy(email = event.email) }
            is EditProfileEvent.OnBodySelected -> _state.update { it.copy(avatarConfig = it.avatarConfig.copy(body = event.body)) }
            is EditProfileEvent.OnHatSelected -> _state.update { it.copy(avatarConfig = it.avatarConfig.copy(hat = event.hat)) }
            is EditProfileEvent.OnAccessorySelected -> _state.update { it.copy(avatarConfig = it.avatarConfig.copy(accessory = event.acc)) }
            is EditProfileEvent.OnTabSelected -> _state.update { it.copy(selectedTab = event.tab) }
            EditProfileEvent.OnSaveClick -> save()
            EditProfileEvent.OnCancelClick -> viewModelScope.launch { _effect.emit(EditProfileEffect.NavigateBack) }
        }
    }

    private fun save() {
        val uid = Session.uid ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val success = updateProfileUseCase(
                ProfileModel(
                    uid = uid,
                    name = _state.value.name,
                    email = _state.value.email,
                    description = _state.value.description,
                    avatarConfig = _state.value.avatarConfig
                )
            )
            if (success) {
                _state.update { it.copy(isLoading = false, isSaved = true) }
                // Solo emitimos un efecto para evitar el error de navegación (blank screen)
                _effect.emit(EditProfileEffect.ShowSaveSuccess)
            } else {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
}