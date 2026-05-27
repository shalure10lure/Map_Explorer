package com.ucb.mapexplorer.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoute {
    @Serializable
    object Login : NavRoute()

    @Serializable
    object Register : NavRoute()

    @Serializable
    object Map : NavRoute()

    @Serializable
    object Explanation1 : NavRoute()

    @Serializable
    object Explanation2 : NavRoute()

    @Serializable
    object Explanation3 : NavRoute()

    @Serializable
    object Explanation4 : NavRoute()

    @Serializable
    object SocialSpace : NavRoute()

    @Serializable
    object Profile : NavRoute()

    @Serializable
    object NearbyPlaces : NavRoute()

    @Serializable
    data class PlaceDetail(val placeId: String) : NavRoute()
}
