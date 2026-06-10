package com.ucb.mapexplorer.favoritePlaces.presentation.state


import com.ucb.mapexplorer.nearbyplaces.domain.model.LugarSavedModel

data class FavoritePlacesUIState(
    val favoritos: List<LugarSavedModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)



