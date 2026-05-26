package com.ucb.mapexplorer.map.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tiles_descubiertos",
    indices = [
        Index(value = ["uid", "tileX", "tileY"], unique = true)
    ]
)
data class TileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uid: String,
    val tileX: Int,
    val tileY: Int,
    val descubiertoEn: Long,
    val vecesVisitado: Int,
    val porcentajeExplorado: Float,
    val ultimoIngreso: Long,
    val sincronizado: Boolean
)
