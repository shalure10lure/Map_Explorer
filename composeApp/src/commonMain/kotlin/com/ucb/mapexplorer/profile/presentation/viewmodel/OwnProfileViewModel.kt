package com.ucb.mapexplorer.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.core.session.Session
import com.ucb.mapexplorer.profile.domain.usecase.ObserveProfileUseCase
import com.ucb.mapexplorer.profile.presentation.state.OwnProfileEffect
import com.ucb.mapexplorer.profile.presentation.state.OwnProfileEvent
import com.ucb.mapexplorer.profile.presentation.state.OwnProfileUIState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class OwnProfileViewModel(
    private val observeProfileUseCase: ObserveProfileUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(OwnProfileUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<OwnProfileEffect>()
    val effect = _effect.asSharedFlow()

    private var observeJob: Job? = null

    init {
        loadProfile()
    }

    /**
     * Inicia la observación del perfil del usuario.
     * Se llama al iniciar el ViewModel y cuando la pantalla se vuelve activa.
     */
    fun loadProfile() {
        val uid = Session.uid ?: return
        
        // Cancelamos observación previa si existe para evitar duplicados
        observeJob?.cancel()
        
        _state.update { it.copy(isLoading = true) }

        observeJob = observeProfileUseCase(uid)
            .onEach { profile ->
                profile?.let { data ->
                    _state.update { it.copy(
                        userName = data.name,
                        description = data.description,
                        email = data.email,
                        avatarConfig = data.avatarConfig,
                        isLoading = false
                    )}
                } ?: _state.update { it.copy(isLoading = false) }
            }
            .catch { _state.update { it.copy(isLoading = false) } }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: OwnProfileEvent) {
        when (event) {
            OwnProfileEvent.OnBackClick -> {
                viewModelScope.launch { _effect.emit(OwnProfileEffect.NavigateBack) }
            }
            OwnProfileEvent.OnEditProfileClick -> {
                viewModelScope.launch { _effect.emit(OwnProfileEffect.NavigateToEditProfile) }
            }
            OwnProfileEvent.OnViewRequestsClick -> {
                viewModelScope.launch { _effect.emit(OwnProfileEffect.NavigateToRequests) }
            }
            is OwnProfileEvent.OnFriendClick -> {
                viewModelScope.launch { _effect.emit(OwnProfileEffect.NavigateToFriendProfile(event.friendName)) }
            }
        }
    }
}
