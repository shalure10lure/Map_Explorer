package com.ucb.mapexplorer.publication.presentation.state

import com.ucb.mapexplorer.nearbyplaces.domain.model.PlaceModel

data class PublicationUIState(
    val place: PlaceModel? = null,
    val rating: Int = 0,
    val experienceText: String = "",
    val isPublishing: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)