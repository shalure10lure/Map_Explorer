package com.ucb.mapexplorer.savedPlaces.presentation.state



sealed interface SavedPlacesEvent {
    data object OnBackClick : SavedPlacesEvent
    data class OnPlaceClick(val lugarId: String) : SavedPlacesEvent
    data class OnRemoveGuardado(val lugarId: String) : SavedPlacesEvent
}