package com.ucb.mapexplorer.map.domain.model

import kotlinx.datetime.Clock

data class UserLocationModel(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float? = null,
    val speed: Float? = null,
    val bearing: Float? = null,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds()
)
