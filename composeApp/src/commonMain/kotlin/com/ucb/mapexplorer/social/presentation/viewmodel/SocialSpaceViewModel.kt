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

    private var friendUidSet: Set<String>? = null

    init { loadPublications() }

    private fun loadPublications() {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update { it.copy(isLoading = true) }
            try {
                val myUid = Session.uid ?: ""

                // CAMBIO 1: Cargar lista de amigos UNA SOLA VEZ en paralelo con publicaciones
                // Antes: isFriendUseCase se llamaba por CADA publicación (N llamadas a Firebase)
                // Ahora: UNA sola llamada para obtener todos los amigos
                val friendsDeferred = async {
                    if (myUid.isNotBlank()) {
                        getFriendsUseCase(myUid).map { it.uid }.toSet()
                    } else emptySet()
                }
                val pubsDeferred = async { getAllPublicationsUseCase() }

                // Esperar ambas en paralelo
                val friendSet = friendsDeferred.await()
                val pubs = pubsDeferred.await()
                friendUidSet = friendSet

                // CAMBIO 2: Mostrar publicaciones inmediatamente sin avatar
                // (avatar se carga en background y actualiza el estado)
                val postsBasic = pubs.map { pub ->
                    SocialPost(
                        id = pub.id,
                        authorUid = pub.uid,
                        userName = pub.userName,
                        locationName = pub.locationName,
                        rating = pub.rating,
                        category = pub.category,
                        categoryIcon = pub.categoryIcon,
                        userExperience = pub.experience,
                        // CAMBIO: lookup O(1) en lugar de llamada Firebase por post
                        isFriend = pub.uid == myUid || friendSet.contains(pub.uid),
                        imageUrl = pub.imageUrl,
                        avatarId = "" ,
                        lugarId        = pub.lugarId,
                        latitude       = pub.latitude,
                        longitude      = pub.longitude
                    )
                }

                // Mostrar posts SIN esperar avatares (experiencia inmediata)
                _state.update { it.copy(posts = postsBasic, isLoading = false) }

                // CAMBIO 3: Cargar avatares en background solo para los primeros 10 visibles
                // Los avatares no son críticos para la funcionalidad
                loadAvatarsInBackground(pubs.take(10), myUid)

            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }
    private fun loadAvatarsInBackground(
        pubs: List<com.ucb.mapexplorer.publication.domain.model.PublicationModel>,
        myUid: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Obtener solo UIDs únicos para no duplicar llamadas
                val uniqueUids = pubs.map { it.uid }.distinct()

                val avatarMap = uniqueUids.map { uid ->
                    async {
                        try {
                            val profile = getProfileUseCase(uid)
                            uid to (profile?.avatarConfig?.toId() ?: "")
                        } catch (_: Exception) {
                            uid to ""
                        }
                    }
                }.awaitAll().toMap()

                // Actualizar posts con avatares sin cambiar isLoading
                _state.update { current ->
                    current.copy(
                        posts = current.posts.map { post ->
                            post.copy(avatarId = avatarMap[post.authorUid] ?: post.avatarId)
                        }
                    )
                }
            } catch (_: Exception) { }
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

            is SocialSpaceEvent.OnViewPlaceDetail ->
                viewModelScope.launch {
                    _effect.emit(SocialSpaceEffect.NavigateToPlaceDetail(event.lugarId))
                }
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