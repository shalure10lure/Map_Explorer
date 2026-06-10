package com.ucb.mapexplorer.savedPlaces.presentation.state

import com.ucb.mapexplorer.nearbyplaces.domain.model.LugarSavedModel

data class SavedPlacesUIState(
    val guardados: List<LugarSavedModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

