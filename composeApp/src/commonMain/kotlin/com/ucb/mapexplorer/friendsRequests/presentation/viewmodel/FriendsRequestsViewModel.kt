package com.ucb.mapexplorer.friendsRequests.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.core.session.Session
import com.ucb.mapexplorer.friends.domain.model.FriendRequestModel
import com.ucb.mapexplorer.friends.domain.usecase.AcceptFriendRequestUseCase
import com.ucb.mapexplorer.friends.domain.usecase.DeclineFriendRequestUseCase
import com.ucb.mapexplorer.friends.domain.usecase.GetPendingRequestsUseCase
import com.ucb.mapexplorer.friends.domain.usecase.GetUserProfileUseCase
import com.ucb.mapexplorer.friendsRequests.presentation.state.FriendsRequestsEffect
import com.ucb.mapexplorer.friendsRequests.presentation.state.FriendsRequestsEvent
import com.ucb.mapexplorer.friendsRequests.presentation.state.FriendsRequestsUIState
import com.ucb.mapexplorer.profile.domain.usecase.GetProfileUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FriendsRequestsViewModel(
    private val getPendingRequestsUseCase: GetPendingRequestsUseCase,
    private val acceptFriendRequestUseCase: AcceptFriendRequestUseCase,
    private val declineFriendRequestUseCase: DeclineFriendRequestUseCase,
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FriendsRequestsUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<FriendsRequestsEffect>()
    val effect = _effect.asSharedFlow()

    init { loadRequests() }

    fun onEvent(event: FriendsRequestsEvent) {
        when (event) {
            FriendsRequestsEvent.OnBackClick ->
                viewModelScope.launch { _effect.emit(FriendsRequestsEffect.NavigateBack) }
            FriendsRequestsEvent.OnLoadRequests -> loadRequests()
            is FriendsRequestsEvent.OnAcceptClick -> accept(event.request)
            is FriendsRequestsEvent.OnDeclineClick -> decline(event.request)
        }
    }

    private fun loadRequests() {
        val uid = Session.uid ?: return
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch(Dispatchers.IO) {
            val requests = getPendingRequestsUseCase(uid)
            _state.update { it.copy(requests = requests, isLoading = false) }
        }
    }

    private fun accept(request: FriendRequestModel) {
        val myUid = Session.uid ?: return
        viewModelScope.launch(Dispatchers.IO) {
            // Obtener mi username
            val myProfile = getProfileUseCase(myUid)
            val myUsername = myProfile?.name ?: myUid

            val ok = acceptFriendRequestUseCase(
                requestId      = request.requestId,
                myUid          = myUid,
                friendUid      = request.emisorUid,
                myUsername     = myUsername,
                friendUsername = request.emisorUsername
            )
            if (ok) {
                _effect.emit(FriendsRequestsEffect.ShowToast("¡Ahora son amigos con ${request.emisorUsername}! 🎉"))
            }
            loadRequests() // Recargar lista
        }
    }

    private fun decline(request: FriendRequestModel) {
        viewModelScope.launch(Dispatchers.IO) {
            declineFriendRequestUseCase(request.requestId)
            _effect.emit(FriendsRequestsEffect.ShowToast("Solicitud rechazada"))
            loadRequests()
        }
    }
}
