package com.ucb.mapexplorer.nearbyplaces.domain.model

/**
 * Modelo de dominio unificado para favoritos y guardados.
 * Contiene los campos mínimos necesarios para mostrar en lista.
 */
data class LugarSavedModel(
    val lugarId: String,
    val nombre: String,
    val categoria: String,
    val iconoCategoria: String,
    val latitud: Double,
    val longitud: Double,
    val guardadoEn: Long          // agregadoEn o guardadoEn según tipo
)