package com.ucb.mapexplorer.auth.presentation.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.auth.domain.model.UserModel
import com.ucb.mapexplorer.auth.domain.usecase.RegisterUseCase
import com.ucb.mapexplorer.auth.presentation.register.state.*
import kotlinx.coroutines.flow.*
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

            // ── PASO 1 ──────────────────────────────────
            is RegisterEvent.OnUsernameChanged ->
                _state.update { it.copy(username = event.value) }

            is RegisterEvent.OnEmailChanged ->
                _state.update { it.copy(email = event.value) }

            is RegisterEvent.OnPasswordChanged ->
                _state.update { it.copy(password = event.value) }

            is RegisterEvent.OnConfirmPasswordChanged ->
                _state.update { it.copy(confirmPassword = event.value) }

            RegisterEvent.OnNextStep -> validateStep1()

            RegisterEvent.OnClickLogin ->
                emit(RegisterEffect.NavigateToLogin)

            // ── PASO 2 ──────────────────────────────────
            is RegisterEvent.OnDescriptionChanged ->
                _state.update { it.copy(description = event.value) }

            is RegisterEvent.OnAgeChanged ->
                _state.update { it.copy(age = event.value) }

            is RegisterEvent.OnBodySelected ->
                _state.update { it.copy(
                    avatarConfig = it.avatarConfig.copy(body = event.body)
                )}

            is RegisterEvent.OnHatSelected ->
                _state.update { it.copy(
                    avatarConfig = it.avatarConfig.copy(hat = event.hat)
                )}

            is RegisterEvent.OnAccessorySelected ->
                _state.update { it.copy(
                    avatarConfig = it.avatarConfig.copy(accessory = event.acc)
                )}

            is RegisterEvent.OnAvatarTabSelected ->
                _state.update { it.copy(selectedTab = event.tab) }

            RegisterEvent.OnBackStep ->
                _state.update { it.copy(currentStep = 1) }

            RegisterEvent.OnClick -> register()
        }
    }

    private fun validateStep1() {
        val s = _state.value
        when {
            s.username.isBlank() || s.email.isBlank() || s.password.isBlank() ->
                emit(RegisterEffect.ShowError("Completa todos los campos"))

            s.password.length < 8 ->
                emit(RegisterEffect.ShowError("La contraseña debe tener al menos 8 caracteres"))

            s.password != s.confirmPassword ->
                emit(RegisterEffect.ShowError("Las contraseñas no coinciden"))

            else -> _state.update { it.copy(currentStep = 2) }
        }
    }

    private fun register() {
        val s = _state.value
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val result = registerUseCase(
                UserModel(
                    username    = s.username,
                    email       = s.email,
                    password    = s.password,
                    description = s.description,
                    // Guardamos la config del avatar como string serializado
                    photoUrl    = s.avatarConfig.toId()
                )
            )

            if (result) {
                emit(RegisterEffect.NavigateToHome)
            } else {
                emit(RegisterEffect.ShowError("Error al registrar usuario"))
            }
            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun emit(effect: RegisterEffect) {
        viewModelScope.launch { _effect.emit(effect) }
    }
}