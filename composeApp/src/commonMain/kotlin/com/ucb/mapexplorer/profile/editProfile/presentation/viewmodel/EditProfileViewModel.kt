package com.ucb.mapexplorer.profile.editProfile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel
import com.ucb.mapexplorer.profile.editProfile.presentation.state.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EditProfileViewModel : ViewModel() {

    private val _state = MutableStateFlow(EditProfileUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<EditProfileEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: EditProfileEvent) {
        when (event) {

            is EditProfileEvent.OnNameChange ->
                _state.update { it.copy(name = event.name) }

            is EditProfileEvent.OnDescriptionChange ->
                _state.update { it.copy(description = event.description) }

            is EditProfileEvent.OnEmailChange ->
                _state.update { it.copy(email = event.email) }

            // Cambios de avatar — actualiza solo la parte correspondiente
            is EditProfileEvent.OnBodySelected ->
                _state.update { it.copy(
                    avatarConfig = it.avatarConfig.copy(body = event.body)
                )}

            is EditProfileEvent.OnHatSelected ->
                _state.update { it.copy(
                    avatarConfig = it.avatarConfig.copy(hat = event.hat)
                )}

            is EditProfileEvent.OnAccessorySelected ->
                _state.update { it.copy(
                    avatarConfig = it.avatarConfig.copy(accessory = event.acc)
                )}

            is EditProfileEvent.OnTabSelected ->
                _state.update { it.copy(selectedTab = event.tab) }

            EditProfileEvent.OnSaveClick -> save()

            EditProfileEvent.OnCancelClick ->
                viewModelScope.launch { _effect.emit(EditProfileEffect.NavigateBack) }
        }
    }

    private fun save() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // Aquí irá la lógica real de guardado en Firebase
            _state.update { it.copy(isLoading = false, isSaved = true) }
            _effect.emit(EditProfileEffect.ShowSaveSuccess)
            _effect.emit(EditProfileEffect.NavigateBack)
        }
    }
}