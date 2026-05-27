package com.ucb.mapexplorer.core.utils

import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.tan
import kotlin.math.atan
import kotlin.math.sinh
import kotlin.math.pow

/**
 * Utilidades para conversión entre coordenadas geográficas y tiles OSM.
 *
 * Usamos ZOOM=17 que da tiles de ~76m x ~76m en latitudes medias.
 * Es el zoom ideal para "Fog of War" peatonal:
 *   - Cada paso nuevo descubre 1 tile
 *   - No genera miles de registros por km recorrido
 *
 * IMPORTANTE: El sistema de tiles anterior (TILE_SIZE = 0.0005°) era
 * inconsistente con el sistema de renderizado OSM en MapViewContainer.
 * Ahora ambos usan el mismo sistema de tiles estándar OSM.
 */
object TileUtils {

    const val ZOOM = 20

    /**
     * Convierte coordenadas GPS a índices de tile OSM.
     * @return Pair(tileX, tileY)
     */
    fun latLngToTile(lat: Double, lon: Double, zoom: Int = ZOOM): Pair<Int, Int> {
        val tileX = lonToTileX(lon, zoom)
        val tileY = latToTileY(lat, zoom)
        return Pair(tileX, tileY)
    }

    /**
     * Convierte un tile OSM al centro geográfico (lat, lon).
     * Útil para renderizar el "hueco" de niebla en el mapa.
     */
    fun tileToCenterLatLng(tileX: Int, tileY: Int, zoom: Int = ZOOM): Pair<Double, Double> {
        val n = 2.0.pow(zoom)
        val lon = (tileX + 0.5) / n * 360.0 - 180.0
        val latRad = atan(sinh(PI * (1 - 2 * (tileY + 0.5) / n)))
        val lat = latRad * 180.0 / PI
        return Pair(lat, lon)
    }

    fun lonToTileX(lon: Double, zoom: Int = ZOOM): Int {
        return floor((lon + 180.0) / 360.0 * 2.0.pow(zoom.toDouble())).toInt()
    }

    fun latToTileY(lat: Double, zoom: Int = ZOOM): Int {
        val latRad = lat * PI / 180.0
        return floor(
            (1.0 - ln(tan(latRad) + 1.0 / kotlin.math.cos(latRad)) / PI) / 2.0 * 2.0.pow(zoom.toDouble())
        ).toInt()
    }
}
