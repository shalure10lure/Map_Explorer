package com.ucb.mapexplorer.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoute {

    @Serializable
    object Login: NavRoute()

    @Serializable
    object Register: NavRoute()

    @Serializable
    object Map: NavRoute()

    // 📍 NearbyPlaces recibe lat/lon como parámetros
    @Serializable
    data class NearbyPlaces(val lat: Double, val lon: Double)

    // 🖼️ PlaceDetail recibe el id del lugar
    @Serializable
    data class PlaceDetail(val placeId: String)
    @Serializable
    object Explanation1: NavRoute()

    @Serializable
    object Explanation2: NavRoute()

    @Serializable
    object Explanation3: NavRoute()

    @Serializable
    object Explanation4: NavRoute()
}
