package com.ucb.mapexplorer.nearbyplaces.data.entity


import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Lugares GUARDADOS (🔖) por el usuario — lista "Quiero ir".
 * Diferente a favoritos: favorito = "me encantó", guardado = "quiero visitarlo".
 */
@Entity(
    tableName = "lugares_guardados",
    indices = [Index(value = ["uid", "lugarId"], unique = true)]
)
data class LugarGuardadoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uid: String,
    val lugarId: String,
    val nombre: String,
    val categoria: String,
    val latitud: Double,
    val longitud: Double,
    val iconoCategoria: String,
    val guardadoEn: Long,
    val sincronizado: Boolean = false
)