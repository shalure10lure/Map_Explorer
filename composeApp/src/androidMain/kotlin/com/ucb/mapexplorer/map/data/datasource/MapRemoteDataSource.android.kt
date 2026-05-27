package com.ucb.mapexplorer.map.data.datasource

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

}