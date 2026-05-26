package com.ucb.mapexplorer.profile.favoritePlaces.presentation.state

import com.ucb.mapexplorer.profile.savedPlaces.presentation.state.Place


sealed interface FavoritePlacesEffect {
    data object NavigateBack : FavoritePlacesEffect
    data class NavigateToPlaceDetail(val place: Place) : FavoritePlacesEffect
}