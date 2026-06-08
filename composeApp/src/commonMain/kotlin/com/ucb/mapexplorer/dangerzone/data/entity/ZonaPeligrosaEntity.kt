package com.ucb.mapexplorer.dangerzone.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Zona peligrosa cacheada localmente.
 * Se sincroniza desde Firebase, solo lectura en Room.
 */
@Entity(tableName = "zonas_peligrosas")
data class ZonaPeligrosaEntity(
    @PrimaryKey
    val zonaId: String,
    val nombre: String,
    val descripcion: String,
    val latitud: Double,
    val longitud: Double,
    val radio: Double,           // radio en metros
    val nivel: String,           // "bajo", "medio", "alto", "critico"
    val tipo: String,            // "robo", "accidente", "inundacion", etc.
    val activa: Boolean = true,
    val actualizadaEn: Long
)
