package com.ucb.mapexplorer.map.domain.model


/**
 * Modelo de dominio para un tile del mapa descubierto.
 * Usamos coordenadas de tile OSM estándar (zoom 17) para un balance
 * entre granularidad (~76m x 76m por tile) y rendimiento de BD.
 *
 * ZOOM 17 es el recomendado para "Fog of War" peatonal:
 *   - Suficientemente pequeño para sentir que se "descubre" zona a zona.
 *   - No tan pequeño que requiera miles de tiles por cuadra.
 */
data class TileModel(
    val tileX: Int,
    val tileY: Int,
    val discovered: Boolean = true,
    val discoveredAt: Long,
    val visitCount: Int,
    val lastVisited: Long
) {
    val key: String
        get() = "${tileX}_${tileY}"

    companion object {
        const val ZOOM = 17
    }
}
