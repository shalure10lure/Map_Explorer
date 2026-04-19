package com.ucb.mapexplorer.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoute {

    @Serializable
    object Dollar: NavRoute()


}
