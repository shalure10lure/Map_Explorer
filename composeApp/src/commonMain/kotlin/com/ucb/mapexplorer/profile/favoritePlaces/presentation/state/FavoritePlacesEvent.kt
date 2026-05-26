package com.ucb.mapexplorer.profile.favoritePlaces.presentation.state

import com.ucb.mapexplorer.profile.savedPlaces.presentation.state.Place


sealed interface FavoritePlacesEvent {
    data object OnBackClick : FavoritePlacesEvent
    data class OnPlaceClick(val place: Place) : FavoritePlacesEvent
}