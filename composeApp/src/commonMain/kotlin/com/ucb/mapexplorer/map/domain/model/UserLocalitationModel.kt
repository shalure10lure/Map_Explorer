package com.ucb.mapexplorer.map.domain.model

data class UserLocationModel(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null,
    val speed: Float? = null,
    val bearing: Float? = null,
    val timestamp: Long = System.currentTimeMillis()
)