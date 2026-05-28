package com.ucb.mapexplorer.nearbyplaces.presentation.state


sealed interface NearbyPlacesEvent {
    data class OnLoadPlaces(val lat: Double, val lon: Double) : NearbyPlacesEvent
    data class OnSelectPlace(val placeId: String) : NearbyPlacesEvent
    data object OnDismissDetail : NearbyPlacesEvent
    data class OnSearchQueryChanged(val query: String) : NearbyPlacesEvent
    data class OnCategorySelected(val category: String?) : NearbyPlacesEvent
    data object OnDismissError : NearbyPlacesEvent
    data object OnRetry : NearbyPlacesEvent
}