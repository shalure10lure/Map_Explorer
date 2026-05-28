package com.ucb.mapexplorer.social.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.social.presentation.state.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SocialSpaceViewModel : ViewModel() {

    private val _state = MutableStateFlow(SocialSpaceState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SocialSpaceEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadMockPosts()
    }

    fun onEvent(event: SocialSpaceEvent) {
        when (event) {
            is SocialSpaceEvent.OnSearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
            }
            SocialSpaceEvent.OnBackClick -> {
                viewModelScope.launch { _effect.emit(SocialSpaceEffect.NavigateBack) }
            }
            SocialSpaceEvent.OnMessageClick -> {
                viewModelScope.launch { _effect.emit(SocialSpaceEffect.NavigateToMessages) }
            }
            is SocialSpaceEvent.OnAddFriendClick -> {
                // Lógica de agregar amigo (UI dummy)
            }
            is SocialSpaceEvent.OnViewOnMapClick -> {
                // Lógica de ver en mapa
            }
        }
    }

    private fun loadMockPosts() {
        _state.update { 
            it.copy(
                posts = listOf(
                    SocialPost(
                        id = "1",
                        userName = "Personaje Unknown User",
                        locationName = "Punto de Encuentro",
                        rating = 5,
                        category = "Restaurante",
                        userExperience = "Experiencia del usuario sobre el sitio que visito",
                        isFriend = false
                    ),
                    SocialPost(
                        id = "2",
                        userName = "Amigo1",
                        locationName = "Punto de Encuentro",
                        rating = 4,
                        category = "Restaurante",
                        userExperience = "Me encantó este lugar, muy recomendado!",
                        isFriend = true
                    )
                )
            )
        }
    }
}
