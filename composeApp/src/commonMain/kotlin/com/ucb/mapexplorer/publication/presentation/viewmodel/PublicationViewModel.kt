package com.ucb.mapexplorer.publication.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ucb.mapexplorer.core.session.Session
import com.ucb.mapexplorer.nearbyplaces.domain.model.PlaceModel
import com.ucb.mapexplorer.nearbyplaces.domain.usecase.GetPlaceDetailUseCase
import com.ucb.mapexplorer.publication.domain.model.PublicationModel
import com.ucb.mapexplorer.publication.domain.usecase.PublishExperienceUseCase
import com.ucb.mapexplorer.publication.presentation.state.PublicationEffect
import com.ucb.mapexplorer.publication.presentation.state.PublicationEvent
import com.ucb.mapexplorer.publication.presentation.state.PublicationUIState
import com.ucb.mapexplorer.profile.domain.usecase.GetProfileUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class PublicationViewModel(
    private val publishExperienceUseCase: PublishExperienceUseCase,
    private val getPlaceDetailUseCase: GetPlaceDetailUseCase,
    private val getProfileUseCase: GetProfileUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PublicationUIState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<PublicationEffect>()
    val effect = _effect.asSharedFlow()

    private var userName: String = ""

    fun loadPlace(placeId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val place = getPlaceDetailUseCase(placeId)
            val uid = Session.uid ?: return@launch
            val profile = getProfileUseCase(uid)
            userName = profile?.name ?: "Usuario"
            _state.update { it.copy(place = place) }
        }
    }

    fun onEvent(event: PublicationEvent) {
        when (event) {
            is PublicationEvent.OnRatingSelected ->
                _state.update { it.copy(rating = event.stars) }

            is PublicationEvent.OnExperienceChanged ->
                _state.update { it.copy(experienceText = event.text) }

            PublicationEvent.OnPublishClick -> publish()

            PublicationEvent.OnCancelClick ->
                viewModelScope.launch { _effect.emit(PublicationEffect.NavigateBack) }

            PublicationEvent.OnDismissError ->
                _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun publish() {
        val state = _state.value
        val place = state.place ?: return
        val uid   = Session.uid ?: return

        if (state.rating == 0) {
            _state.update { it.copy(errorMessage = "Por favor selecciona una calificación") }
            return
        }
        if (state.experienceText.isBlank()) {
            _state.update { it.copy(errorMessage = "Escribe tu experiencia antes de publicar") }
            return
        }

        _state.update { it.copy(isPublishing = true) }

        viewModelScope.launch(Dispatchers.IO) {
            val publication = PublicationModel(
                id           = "",
                uid          = uid,
                userName     = userName,
                lugarId      = place.id,
                locationName = place.name,
                category     = place.category,
                categoryIcon = place.categoryIcon,
                imageUrl     = place.imageUrl,
                rating       = state.rating,
                experience   = state.experienceText,
                publishedAt  = Clock.System.now().toEpochMilliseconds(),
                latitude     = place.latitude,
                longitude    = place.longitude
            )

            val success = publishExperienceUseCase(publication)
            if (success) {
                _state.update { it.copy(isPublishing = false, isSuccess = true) }
                _effect.emit(PublicationEffect.PublishedSuccessfully)
            } else {
                _state.update { it.copy(isPublishing = false, errorMessage = "Error al publicar, intenta de nuevo") }
            }
        }
    }
}