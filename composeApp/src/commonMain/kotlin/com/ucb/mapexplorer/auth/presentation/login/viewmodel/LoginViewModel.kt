package com.ucb.mapexplorer.auth.presentation.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.auth.domain.usecase.LoginUseCase
import com.ucb.mapexplorer.auth.presentation.login.state.LoginEffect
import com.ucb.mapexplorer.auth.presentation.login.state.LoginEvent
import com.ucb.mapexplorer.auth.presentation.login.state.LoginUIState
import com.ucb.mapexplorer.core.session.Session
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
): ViewModel() {

    private val _state = MutableStateFlow(LoginUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<LoginEffect>()
    val effect = _effect.asSharedFlow()

    fun onEvent(event: LoginEvent) {
        when(event) {

            is LoginEvent.OnEmailChanged -> {
                _state.update {
                    it.copy(email = event.value)
                }
            }

            is LoginEvent.OnPasswordChanged -> {
                _state.update {
                    it.copy(password = event.value)
                }
            }

            LoginEvent.OnClick -> {
                login()
            }
            LoginEvent.OnClickRegister -> {
                emit(LoginEffect.NavigateToRegister)
            }
        }
    }
    private fun emit(effect: LoginEffect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }
    private fun login() {

        val email = _state.value.email
        val password = _state.value.password

        if (email.isBlank() || password.isBlank()) {
            emit(LoginEffect.ShowError("Campos vacíos"))
            return
        }

        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {

            val user = loginUseCase(email, password)

            if (user) {
                Session.uid = safeKey(email)  // ← "jr_gmail_com" en vez de "jr@gmail.com"
                emit(LoginEffect.NavigateToHome)
            }else {
                emit(LoginEffect.ShowError("Credenciales incorrectas"))
            }

            _state.update { it.copy(isLoading = false) }
        }

    }
    private fun safeKey(email: String): String =
        email.trim().lowercase()
            .replace("@", "_")
            .replace(".", "_")



}