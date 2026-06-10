package com.ucb.mapexplorer.nearbyplaces.data.dto

import kotlinx.serialization.Serializable


@Serializable
data class OverpassCenterDto(
    val lat: Double,
    val lon: Double
)