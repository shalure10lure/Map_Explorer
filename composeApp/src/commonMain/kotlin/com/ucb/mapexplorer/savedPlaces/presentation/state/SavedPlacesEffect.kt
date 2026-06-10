package com.ucb.mapexplorer.savedPlaces.presentation.state

sealed interface SavedPlacesEffect {
    data object NavigateBack : SavedPlacesEffect
    data class NavigateToPlaceDetail(val lugarId: String) : SavedPlacesEffect
}