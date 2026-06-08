package com.ucb.mapexplorer.social.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.friends.domain.usecase.GetFriendsUseCase
import com.ucb.mapexplorer.friends.domain.usecase.IsFriendUseCase
import com.ucb.mapexplorer.friends.domain.usecase.SendFriendRequestUseCase
import com.ucb.mapexplorer.core.session.Session
import com.ucb.mapexplorer.publication.domain.usecase.GetAllPublicationsUseCase
import com.ucb.mapexplorer.profile.domain.usecase.GetProfileUseCase
import com.ucb.mapexplorer.social.presentation.state.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SocialSpaceViewModel(
    private val getAllPublicationsUseCase: GetAllPublicationsUseCase,
    private val getFriendsUseCase: GetFriendsUseCase,
    private val isFriendUseCase: IsFriendUseCase,
    private val sendFriendRequestUseCase: SendFriendRequestUseCase,
    private val getProfileUseCase: GetProfileUseCase
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
                val myUid   = Session.uid ?: ""
                val pubs    = getAllPublicationsUseCase()

                // Cargamos avatar + estado de amistad en paralelo
                val posts = pubs.map { pub ->
                    async {
                        val isFriend = if (myUid.isNotBlank() && pub.uid != myUid)
                            isFriendUseCase(myUid, pub.uid) else true

                        // Obtener avatarId del publicador
                        val profile   = runCatching { getProfileUseCase(pub.uid) }.getOrNull()
                        val avatarId  = profile?.avatarConfig?.toId() ?: ""

                        SocialPost(
                            id            = pub.id,
                            authorUid     = pub.uid,
                            userName      = pub.userName,
                            locationName  = pub.locationName,
                            rating        = pub.rating,
                            category      = pub.category,
                            categoryIcon  = pub.categoryIcon,
                            userExperience = pub.experience,
                            isFriend      = isFriend,
                            imageUrl      = pub.imageUrl,
                            avatarId      = avatarId
                        )
                    }
                }.awaitAll()

                _state.update { it.copy(posts = posts, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onEvent(event: SocialSpaceEvent) {
        when (event) {
            is SocialSpaceEvent.OnSearchQueryChanged ->
                _state.update { it.copy(searchQuery = event.query) }

            SocialSpaceEvent.OnBackClick ->
                viewModelScope.launch { _effect.emit(SocialSpaceEffect.NavigateBack) }

            SocialSpaceEvent.OnMessageClick ->
                viewModelScope.launch { _effect.emit(SocialSpaceEffect.NavigateToMessages) }

            is SocialSpaceEvent.OnAddFriendClick -> sendRequest(event.authorUid)

            is SocialSpaceEvent.OnViewOnMapClick -> { /* navegar al mapa */ }
        }
    }

    private fun sendRequest(toUid: String) {
        val myUid = Session.uid ?: return
        viewModelScope.launch(Dispatchers.IO) {
            val myProfile   = runCatching { getProfileUseCase(myUid) }.getOrNull()
            val myUsername  = myProfile?.name ?: myUid
            val ok = sendFriendRequestUseCase(myUid, toUid, myUsername)
            if (ok) {
                _effect.emit(SocialSpaceEffect.ShowToast("¡Solicitud enviada! 🎉"))
                // Actualizar el post para marcar solicitud enviada
                _state.update { s ->
                    s.copy(posts = s.posts.map { p ->
                        if (p.authorUid == toUid) p.copy(requestSent = true) else p
                    })
                }
            }
        }
    }
}