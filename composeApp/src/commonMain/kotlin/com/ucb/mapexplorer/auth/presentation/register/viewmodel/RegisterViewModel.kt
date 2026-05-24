package com.ucb.mapexplorer.auth.presentation.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.auth.domain.model.UserModel
import com.ucb.mapexplorer.auth.domain.usecase.RegisterUseCase
import com.ucb.mapexplorer.auth.presentation.register.state.RegisterEffect
import com.ucb.mapexplorer.auth.presentation.register.state.RegisterEvent
import com.ucb.mapexplorer.auth.presentation.register.state.RegisterUIState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<RegisterEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: RegisterEvent) {
        when (event) {

            is RegisterEvent.OnUsernameChanged -> {
                _state.update { it.copy(username = event.value) }
            }

            is RegisterEvent.OnEmailChanged -> {
                _state.update { it.copy(email = event.value) }
            }

            is RegisterEvent.OnPasswordChanged -> {
                _state.update { it.copy(password = event.value) }
            }

            is RegisterEvent.OnConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPassword = event.value) }
            }

            is RegisterEvent.OnDescriptionChanged -> {
                _state.update { it.copy(description = event.value) }
            }

            RegisterEvent.OnClick -> register()

            RegisterEvent.OnClickLogin -> {
                emit(RegisterEffect.NavigateToLogin)
            }
        }
    }

    private fun register() {

        val s = _state.value

        if (s.username.isBlank() || s.email.isBlank() || s.password.isBlank()) {
            emit(RegisterEffect.ShowError("Campos obligatorios vacíos"))
            return
        }

        if (s.password != s.confirmPassword) {
            emit(RegisterEffect.ShowError("Las contraseñas no coinciden"))
            return
        }

        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {

            val result = registerUseCase(
                UserModel(
                    username = s.username,
                    email = s.email,
                    password = s.password,
                    description = s.description
                )
            )

            if (result) {
                // CAMBIO: Ahora navega directamente al Home (Mapa)
                emit(RegisterEffect.NavigateToHome)
            } else {
                emit(RegisterEffect.ShowError("Error al registrar usuario"))
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun emit(effect: RegisterEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
}