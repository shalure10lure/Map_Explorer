package com.ucb.mapexplorer.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.clearSessionUid
import com.ucb.mapexplorer.core.session.Session
import com.ucb.mapexplorer.friends.domain.usecase.GetFriendsUseCase
import com.ucb.mapexplorer.profile.domain.model.ProfileModel
import com.ucb.mapexplorer.profile.domain.usecase.ObserveProfileUseCase
import com.ucb.mapexplorer.profile.presentation.state.OwnProfileEffect
import com.ucb.mapexplorer.profile.presentation.state.OwnProfileEvent
import com.ucb.mapexplorer.profile.presentation.state.OwnProfileUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
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
    private val observeProfileUseCase: ObserveProfileUseCase,
    private val getFriendsUseCase: GetFriendsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(OwnProfileUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<OwnProfileEffect>()
    val effect = _effect.asSharedFlow()

    private var observeJob: Job? = null
    private var friendsJob: Job? = null
    companion object {
        private var cachedProfile: ProfileModel? = null
        private var cachedFriends: List<Pair<String, String>>? = null // username -> uid
        private var cachedUid: String? = null

        fun invalidateCache() {
            cachedProfile = null
            cachedFriends = null
            cachedUid = null
        }
    }

    init {
        loadProfile()
    }

    fun loadProfile() {
        val uid = Session.uid ?: return

        // CAMBIO 1: Mostrar datos del caché inmediatamente sin esperar Firebase
        if (cachedProfile != null && cachedUid == uid) {
            val profile = cachedProfile!!
            _state.update { current ->
                current.copy(
                    userName = profile.name,
                    description = profile.description,
                    email = profile.email,
                    avatarConfig = profile.avatarConfig,
                    isLoading = false
                )
            }
            // Restaurar amigos del caché también
            cachedFriends?.let { friends ->
                _state.update { current ->
                    current.copy(
                        friends = friends.map { it.first },
                        friendUids = friends.associate { it.first to it.second }
                    )
                }
            }
        } else {
            // Solo mostrar loading si no hay caché
            _state.update { it.copy(isLoading = true) }
        }

        // CAMBIO 2: Siempre observar Firebase en background para mantener datos frescos
        observeJob?.cancel()
        observeJob = observeProfileUseCase(uid)
            .onEach { profile ->
                profile?.let { data ->
                    // Actualizar caché
                    cachedProfile = data
                    cachedUid = uid
                    _state.update {
                        it.copy(
                            userName = data.name,
                            description = data.description,
                            email = data.email,
                            avatarConfig = data.avatarConfig,
                            isLoading = false
                        )
                    }
                } ?: _state.update { it.copy(isLoading = false) }
            }
            .catch { _state.update { it.copy(isLoading = false) } }
            .launchIn(viewModelScope)

        //  Cargar amigos solo si no están en caché
        if (cachedFriends == null || cachedUid != uid) {
            loadFriends(uid)
        }
    }
    private fun loadFriends(uid: String) {
        friendsJob?.cancel()
        friendsJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val friends = getFriendsUseCase(uid)
                val friendPairs = friends.map { f -> f.username to f.uid }
                cachedFriends = friendPairs
                _state.update {
                    it.copy(
                        friends = friends.map { f -> f.username },
                        friendUids = friends.associate { f -> f.username to f.uid }
                    )
                }
            } catch (_: Exception) { }
        }
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
            is OwnProfileEvent.OnFriendClick ->
                viewModelScope.launch {
                    _effect.emit(OwnProfileEffect.NavigateToFriendProfile(event.friendName))
                }
            OwnProfileEvent.OnLogoutClick -> {
                viewModelScope.launch {
                    // Limpiar cachés al cerrar sesión
                    invalidateCache()
                    clearSessionUid()
                    Session.uid = null
                    _effect.emit(OwnProfileEffect.NavigateToLogin)
                }
            }

        }
    }
    override fun onCleared() {
        super.onCleared()
        observeJob?.cancel()
        friendsJob?.cancel()
    }

}
