package com.ucb.mapexplorer.nearbyplaces.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class OverpassResponseDto(
    val elements: List<OverpassElementDto> = emptyList()
)
