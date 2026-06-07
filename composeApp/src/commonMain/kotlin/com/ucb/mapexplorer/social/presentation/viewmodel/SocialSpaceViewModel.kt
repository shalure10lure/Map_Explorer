package com.ucb.mapexplorer.social.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.publication.domain.usecase.GetAllPublicationsUseCase
import com.ucb.mapexplorer.social.presentation.state.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SocialSpaceViewModel(
    private val getAllPublicationsUseCase: GetAllPublicationsUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(SocialSpaceState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<SocialSpaceEffect>()
    val effect = _effect.asSharedFlow()

    init { loadPublications() }
    private fun loadPublications() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }
            try {
                val pubs = getAllPublicationsUseCase()
                val posts = pubs.map { pub ->
                    SocialPost(
                        id             = pub.id,
                        userName       = pub.userName,
                        locationName   = pub.locationName,
                        rating         = pub.rating,
                        category       = pub.category,
                        userExperience = pub.experience,
                        isFriend       = false,
                        imageUrl       = pub.imageUrl,
                        categoryIcon   = pub.categoryIcon
                    )
                }
                _state.update { it.copy(posts = posts, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
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

}
