package com.ucb.mapexplorer.profile.savedPlaces.presentation.state

data class Place(
    val name: String,
    val description: String,
    val rating: Float
)

data class SavedPlacesUIState(
    val places: List<Place> = listOf(
        Place("Nombre", "Descripción Sitio", 4f),
        Place("Nombre", "Descripción Sitio", 5f),
        Place("Nombre", "Descripción Sitio", 3f),
        Place("Nombre", "Descripción Sitio", 4.5f)
    )
)
