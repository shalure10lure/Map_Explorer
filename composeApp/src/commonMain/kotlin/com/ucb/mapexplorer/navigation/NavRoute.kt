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

    @Serializable
    object Explanation1: NavRoute()

    @Serializable
    object Explanation2: NavRoute()

    @Serializable
    object Explanation3: NavRoute()

    @Serializable
    object Explanation4 : NavRoute()

    @Serializable
    object SocialSpace : NavRoute()

    @Serializable
    object Profile : NavRoute()

    @Serializable
    object EditProfile : NavRoute()
    @Serializable
    object Main : NavRoute()

    @Serializable
    object Onboarding : NavRoute()

    @Serializable
    object NearbyPlaces : NavRoute()

    @Serializable
    object FavoritePlaces : NavRoute()
    @Serializable
    object SavedPlaces : NavRoute()
    @Serializable
    data class PlaceDetail(val placeId: String,val type: String = "OSM" ) : NavRoute()

    @Serializable
    data class GuideMap(
        val userLat: Double,
        val userLon: Double,
        val destLat: Double,
        val destLon: Double,
        val placeName: String
    ) : NavRoute()


    @Serializable
    data class Publication(val placeId: String) : NavRoute()

    @Serializable
    object FriendsRequests : NavRoute()

    @Serializable
    data class FriendProfile(val friendUid: String) : NavRoute()
    @Serializable
    object SearchUser : NavRoute()
}
