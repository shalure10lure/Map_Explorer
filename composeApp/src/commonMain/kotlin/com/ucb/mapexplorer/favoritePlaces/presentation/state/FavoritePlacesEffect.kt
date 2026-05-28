package com.ucb.mapexplorer.favoritePlaces.presentation.state

import com.ucb.mapexplorer.savedPlaces.presentation.state.Place


sealed interface FavoritePlacesEffect {
    data object NavigateBack : FavoritePlacesEffect
    data class NavigateToPlaceDetail(val place: Place) : FavoritePlacesEffect
}