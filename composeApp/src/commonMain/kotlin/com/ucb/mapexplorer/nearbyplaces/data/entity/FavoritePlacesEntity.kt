package com.ucb.mapexplorer.nearbyplaces.data.entity


import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Lugares marcados como FAVORITOS (❤️) por el usuario.
 * Se almacenan localmente en Room Y se sincronizan con Firebase.
 * La FK hacia lugares_cache permite JOIN directo para obtener todos los datos.
 */
@Entity(
    tableName = "lugares_favoritos",
    indices = [Index(value = ["uid", "lugarId"], unique = true)]
)
data class LugarFavoritoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uid: String,
    val lugarId: String,
    val nombre: String,
    val categoria: String,
    val latitud: Double,
    val longitud: Double,
    val iconoCategoria: String,
    val agregadoEn: Long,
    val sincronizado: Boolean = false
)