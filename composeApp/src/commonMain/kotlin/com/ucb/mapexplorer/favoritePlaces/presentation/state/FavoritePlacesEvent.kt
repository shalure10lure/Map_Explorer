package com.ucb.mapexplorer.favoritePlaces.presentation.state

sealed interface FavoritePlacesEvent {
    data object OnBackClick : FavoritePlacesEvent
    data class OnPlaceClick(val lugarId: String) : FavoritePlacesEvent
    data class OnRemoveFavorito(val lugarId: String) : FavoritePlacesEvent
}
