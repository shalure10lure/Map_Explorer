package com.ucb.mapexplorer.publication.domain.model


data class PublicationModel(
    val id: String,
    val uid: String,
    val userName: String,
    val lugarId: String,
    val locationName: String,
    val category: String,
    val categoryIcon: String,
    val imageUrl: String?,
    val rating: Int,           // 1-5 estrellas del usuario
    val experience: String,    // Texto de opinión
    val publishedAt: Long,
    val latitude: Double,
    val longitude: Double
)