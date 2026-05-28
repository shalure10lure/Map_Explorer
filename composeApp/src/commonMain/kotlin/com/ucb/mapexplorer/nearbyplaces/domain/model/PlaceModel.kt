package com.ucb.mapexplorer.nearbyplaces.domain.model

data class PlaceModel(
    val id: String,
    val name: String,
    val category: String,
    val categoryIcon: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val rating: Float,
    val imageUrl: String?,
    val distanceMeters: Double,
    val tags: Map<String, String>
)