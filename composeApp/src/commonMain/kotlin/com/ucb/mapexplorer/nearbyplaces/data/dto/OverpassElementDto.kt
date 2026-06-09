package com.ucb.mapexplorer.nearbyplaces.data.dto

import kotlinx.serialization.Serializable


@Serializable
data class OverpassElementDto(
    val id: Long,
    val type: String,
    val lat: Double? = null,
    val lon: Double? = null,
    val tags: Map<String, String> = emptyMap(),
    val center: OverpassCenterDto? = null
)
