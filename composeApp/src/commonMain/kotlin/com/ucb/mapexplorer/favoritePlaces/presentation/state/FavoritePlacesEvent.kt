package com.ucb.mapexplorer.favoritePlaces.presentation.state

import com.ucb.mapexplorer.savedPlaces.presentation.state.Place


sealed interface FavoritePlacesEvent {
    data object OnBackClick : FavoritePlacesEvent
    data class OnPlaceClick(val place: Place) : FavoritePlacesEvent
}