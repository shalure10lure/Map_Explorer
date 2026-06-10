package com.ucb.mapexplorer.friendProfile.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.core.session.Session
import com.ucb.mapexplorer.friendProfile.presentation.state.FriendProfileEffect
import com.ucb.mapexplorer.friendProfile.presentation.state.FriendProfileEvent
import com.ucb.mapexplorer.friendProfile.presentation.state.FriendProfileUIState
import com.ucb.mapexplorer.friends.domain.usecase.GetFriendsUseCase
import com.ucb.mapexplorer.friends.domain.usecase.GetUserProfileUseCase
import com.ucb.mapexplorer.friends.domain.usecase.RemoveFriendUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FriendProfileViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getFriendsUseCase: GetFriendsUseCase,
    private val removeFriendUseCase: RemoveFriendUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FriendProfileUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<FriendProfileEffect>()
    val effect = _effect.asSharedFlow()

    fun loadFriend(friendUid: String) {
        _state.update { it.copy(friendUid = friendUid, isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            // Obtener perfil del amigo
            val profile = getUserProfileUseCase(friendUid)
            // Obtener amigos del amigo (para mostrar lista)
            val friendsList = getFriendsUseCase(friendUid)
            // Obtener mis amigos para calcular amigos en común
            val myUid = Session.uid ?: ""
            val myFriends = getFriendsUseCase(myUid)
            val myFriendUids = myFriends.map { it.uid }.toSet()
            val mutual = friendsList.filter { it.uid in myFriendUids }.map { it.username }

            _state.update {
                it.copy(
                    friendUid = friendUid,
                    friendName = profile?.username ?: friendUid,
                    description = profile?.description ?: "",
                    avatarId = profile?.avatarId ?: "",
                    friendsList = friendsList,
                    mutualFriends = mutual,
                    isLoading = false
                )
            }
        }
    }

    fun onEvent(event: FriendProfileEvent) {
        when (event) {
            FriendProfileEvent.OnBackClick ->
                viewModelScope.launch { _effect.emit(FriendProfileEffect.NavigateBack) }
            FriendProfileEvent.OnBackToProfileClick ->
                viewModelScope.launch { _effect.emit(FriendProfileEffect.NavigateToOwnProfile) }
            FriendProfileEvent.OnRemoveFriendClick ->
                _state.update { it.copy(showRemoveDialog = true) }
            FriendProfileEvent.OnDismissRemoveDialog ->
                _state.update { it.copy(showRemoveDialog = false) }
            FriendProfileEvent.OnConfirmRemove -> removeFriend()
        }
    }

    private fun removeFriend() {
        val myUid = Session.uid ?: return
        val friendUid = _state.value.friendUid
        _state.update { it.copy(isRemoving = true, showRemoveDialog = false) }
        viewModelScope.launch(Dispatchers.IO) {
            val ok = removeFriendUseCase(myUid, friendUid)
            if (ok) {
                _effect.emit(FriendProfileEffect.ShowToast("Amistad eliminada"))
                _effect.emit(FriendProfileEffect.FriendRemoved)
            } else {
                _effect.emit(FriendProfileEffect.ShowToast("Error al eliminar amistad"))
            }
            _state.update { it.copy(isRemoving = false) }
        }
    }
}
