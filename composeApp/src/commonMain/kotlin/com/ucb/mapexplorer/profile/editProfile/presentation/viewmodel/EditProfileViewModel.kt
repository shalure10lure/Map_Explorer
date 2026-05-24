package com.ucb.mapexplorer.profile.editProfile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.ucb.mapexplorer.profile.editProfile.presentation.state.*

class EditProfileViewModel : ViewModel() {
    private val _state = MutableStateFlow(EditProfileUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<EditProfileEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: EditProfileEvent) {
        when (event) {
            is EditProfileEvent.OnDescriptionChange -> {
                _state.value = _state.value.copy(description = event.description)
            }
            is EditProfileEvent.OnEmailChange -> {
                _state.value = _state.value.copy(email = event.email)
            }
            EditProfileEvent.OnCancelClick -> {
                viewModelScope.launch { _effect.emit(EditProfileEffect.NavigateBack) }
            }
            EditProfileEvent.OnSaveClick -> {
                // Aquí iría la lógica de guardado real
                viewModelScope.launch { 
                    _effect.emit(EditProfileEffect.ShowSaveSuccess)
                    _effect.emit(EditProfileEffect.NavigateBack)
                }
            }
        }
    }
}
