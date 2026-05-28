package com.ucb.mapexplorer.nearbyplaces.presentation.state


sealed interface NearbyPlacesEffect {
    data class ShowError(val message: String) : NearbyPlacesEffect
    data class NavigateToMap(val lat: Double, val lon: Double) : NearbyPlacesEffect
    data object SharePlace : NearbyPlacesEffect
}