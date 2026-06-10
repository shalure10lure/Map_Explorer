package com.ucb.mapexplorer.map.data.datasource

import androidx.core.view.children
import androidx.work.await
import com.google.firebase.database.FirebaseDatabase

import com.ucb.mapexplorer.map.domain.model.TileModel
import kotlinx.coroutines.tasks.await

actual class MapRemoteDataSource actual constructor() {

    private val firebaseDb = FirebaseDatabase.getInstance()

    actual suspend fun syncTile(
        uid: String,
        tile: TileModel
    ) {
        try {
            val ref = firebaseDb.reference
                .child("usuarios")
                .child(uid)
                .child("exploracion")
                .child("tiles_descubiertos")
                .child("${tile.tileX}_${tile.tileY}")

            ref.setValue(
                mapOf(
                    "descubierto_en"  to tile.discoveredAt,
                    "veces_visitado"  to tile.visitCount,
                    "ultimo_ingreso"  to tile.lastVisited,
                    "sincronizado"    to true
                )
            ).await()
            println("✅ Tile sincronizado: ${tile.tileX}_${tile.tileY} para $uid")


        } catch (e: Exception) {
            println("Firebase error: ${e.message}")
        }
    }
    actual suspend fun getAllVisitedTiles(uid: String): List<TileModel> {
        return try {
            val snapshot = firebaseDb.reference
                .child("usuarios")
                .child(uid)
                .child("exploracion")
                .child("tiles_descubiertos")
                .get()
                .await()

            snapshot.children.mapNotNull { child ->
                // CLAVE DEL CAMBIO: Extraer coordenadas del nombre del nodo (ej: "331428_575588")
                val nodeName = child.key ?: return@mapNotNull null
                val coordinates = nodeName.split("_")

                // Si el nombre no tiene el formato correcto (ej: "trst"), lo ignoramos
                if (coordinates.size < 2) return@mapNotNull null

                val x = coordinates[0].toIntOrNull() ?: 0
                val y = coordinates[1].toIntOrNull() ?: 0

                TileModel(
                    tileX = x,
                    tileY = y,
                    discoveredAt = child.child("descubierto_en").getValue(Long::class.java) ?: 0L,
                    // Como en tu Firebase no veo "veces_visitado", ponemos 1 por defecto
                    visitCount = child.child("veces_visitado").getValue(Int::class.java) ?: 1,
                    lastVisited = child.child("ultimo_ingreso").getValue(Long::class.java) ?: 0L
                )
            }
        } catch (e: Exception) {
            println("❌ Error descargando tiles de Firebase: ${e.message}")
            emptyList()
        }

    }

}