package com.ucb.mapexplorer.profile.savedPlaces.presentation.state



sealed interface SavedPlacesEvent {
    data object OnBackClick : SavedPlacesEvent
    data class OnPlaceClick(val place: Place) : SavedPlacesEvent
}
