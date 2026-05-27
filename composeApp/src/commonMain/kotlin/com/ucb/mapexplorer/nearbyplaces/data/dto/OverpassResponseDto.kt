package com.ucb.mapexplorer.nearbyplaces.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class OverpassResponseDto(
    val elements: List<OverpassElementDto> = emptyList()
)

@Serializable
data class OverpassElementDto(
    val id: Long,
    val type: String,
    val lat: Double? = null,
    val lon: Double? = null,
    val tags: Map<String, String> = emptyMap(),
    val center: OverpassCenterDto? = null
)

@Serializable
data class OverpassCenterDto(
    val lat: Double,
    val lon: Double
)