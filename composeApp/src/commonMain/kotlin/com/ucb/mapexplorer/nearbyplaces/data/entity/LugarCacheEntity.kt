package com.ucb.mapexplorer.nearbyplaces.data.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lugares_cache")
data class LugarCacheEntity(
    @PrimaryKey
    val lugarId: String,
    val nombre: String,
    val categoria: String,
    val descripcion: String?,
    val latitud: Double,
    val longitud: Double,
    val rating: Float,
    val imagenUrl: String?,
    val iconoCategoria: String?,
    val actualizadoEn: Long
)