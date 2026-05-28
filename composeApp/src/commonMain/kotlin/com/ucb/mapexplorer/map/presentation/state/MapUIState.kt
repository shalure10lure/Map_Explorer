package com.ucb.mapexplorer.map.presentation.state

import com.ucb.mapexplorer.map.domain.model.TileModel

data class MapUIState(
    /** Lista de tiles descubiertos por el usuario (leída de Room). */
    val discoveredTiles: List<TileModel> = emptyList(),

    /** Latitud actual del GPS. 0.0 = aún sin ubicación. */
    val userLat: Double = 0.0,

    /** Longitud actual del GPS. */
    val userLng: Double = 0.0,

    /** Si estamos esperando la primera ubicación GPS. */
    val isLoadingLocation: Boolean = true,

    /** Cargando tiles desde la BD. */
    val isLoadingTiles: Boolean = false,

    /** Mensaje de error para mostrar (null = sin error). */
    val errorMessage: String? = null,

    /** Tiles totales descubiertos. */
    val totalTilesUnlocked: Int = 0,

    /** Nivel del usuario (calculado en base a tiles). */
    val level: Int = 1,

    /** Experiencia acumulada. */
    val experience: Int = 0
)

