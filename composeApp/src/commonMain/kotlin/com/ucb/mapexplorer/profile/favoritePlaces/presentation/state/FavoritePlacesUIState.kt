package com.ucb.mapexplorer.profile.favoritePlaces.presentation.state

import com.ucb.mapexplorer.profile.savedPlaces.presentation.state.Place

data class FavoritePlacesUIState(
    val favoritePlaces: List<Place> = listOf(
        Place("Nombre", "Descripción Sitio", 5f),
        Place("Nombre", "Descripción Sitio", 4.5f),
        Place("Nombre", "Descripción Sitio", 5f)
    )
)


