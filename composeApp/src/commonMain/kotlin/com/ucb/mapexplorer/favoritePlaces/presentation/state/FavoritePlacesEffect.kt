package com.ucb.mapexplorer.favoritePlaces.presentation.state

sealed interface FavoritePlacesEffect {
    data object NavigateBack : FavoritePlacesEffect
    data class NavigateToPlaceDetail(val lugarId: String) : FavoritePlacesEffect
}


