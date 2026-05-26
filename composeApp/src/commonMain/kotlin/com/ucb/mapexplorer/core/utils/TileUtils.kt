package com.ucb.mapexplorer.core.utils

import kotlin.math.floor

object TileUtils {

    private const val TILE_SIZE = 0.0005

    fun latLngToTile(
        lat: Double,
        lon: Double
    ): Pair<Int, Int> {

        val tileX = floor(lon / TILE_SIZE).toInt()
        val tileY = floor(lat / TILE_SIZE).toInt()

        return Pair(tileX, tileY)
    }
}