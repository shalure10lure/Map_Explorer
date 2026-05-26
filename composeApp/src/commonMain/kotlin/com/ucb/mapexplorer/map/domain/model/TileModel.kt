package com.ucb.mapexplorer.map.domain.model

import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.exp

data class TileModel(
    val tileX: Int,
    val tileY: Int,
    val discovered: Boolean,
    val discoveredAt: Long,
    val visitCount: Int,
    val explorationPercentage: Float,
    val lastVisited: Long
) {
    val key: String
        get() = "${tileX}_${tileY}"
    // centro geográfico del tile
    // centro real del tile
    val centerLat: Double
        get() = (tileX * TILE_SIZE) + (TILE_SIZE / 2)

    val centerLng: Double
        get() = (tileY * TILE_SIZE) + (TILE_SIZE / 2)

    companion object {
        private const val TILE_SIZE  = 262144.0
    }
}