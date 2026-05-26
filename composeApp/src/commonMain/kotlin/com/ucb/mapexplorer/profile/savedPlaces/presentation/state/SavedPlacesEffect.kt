package com.ucb.mapexplorer.profile.savedPlaces.presentation.state




sealed interface SavedPlacesEffect {
    data object NavigateBack : SavedPlacesEffect
    data class NavigateToPlaceDetail(val place: Place) : SavedPlacesEffect
}
