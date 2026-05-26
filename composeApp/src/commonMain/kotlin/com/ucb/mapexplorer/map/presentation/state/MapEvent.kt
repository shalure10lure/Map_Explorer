package com.ucb.mapexplorer.map.presentation.state

import com.ucb.mapexplorer.map.domain.model.PlaceModel

sealed interface MapEvent {
    // carga inicial del mapa
    data object OnLoadMap : MapEvent

    // fuerza refresco de ubicación
    data object OnRefreshLocation : MapEvent


    //data class OnPlaceClicked( val place: PlaceModel ) : MapEvent

    //data object OnDismissPlaceDetail : MapEvent
}