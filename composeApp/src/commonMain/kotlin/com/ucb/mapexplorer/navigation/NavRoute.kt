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
    object Explanation4: NavRoute()
}
