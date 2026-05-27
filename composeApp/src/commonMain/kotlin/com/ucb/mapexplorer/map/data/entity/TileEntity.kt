package com.ucb.mapexplorer.map.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room para tiles descubiertos.
 *
 * CORRECCIÓN respecto al código original:
 * - El índice único incluye [uid, tileX, tileY] porque CADA USUARIO
 *   tiene su propio mapa. Sin el uid en el índice, dos usuarios en el
 *   mismo tile colisionarían y se sobreescribirían.
 * - Se eliminó porcentajeExplorado (siempre 100 para un tile visitado,
 *   no aporta valor en esta etapa).
 * - sincronizado es Boolean → Int para compatibilidad Room/SQLite.
 */
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
    val ultimoIngreso: Long,
    val sincronizado: Boolean = false
)
