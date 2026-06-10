package com.ucb.mapexplorer.dangerzone.data.datasource


import com.google.firebase.database.FirebaseDatabase
import com.ucb.mapexplorer.dangerzone.domain.model.ZonaPeligrosaModel
import com.ucb.mapexplorer.dangerzone.domain.model.toNivelPeligro
import kotlinx.coroutines.tasks.await

actual class DangerZoneRemoteDataSource actual constructor() {

    private val db = FirebaseDatabase.getInstance().reference

    actual suspend fun getZonasActivas(): List<ZonaPeligrosaModel> {
        return try {
            val snapshot = db.child("zonas_peligrosas").get().await()
            snapshot.children.mapNotNull { child ->
                val zonaId      = child.key ?: return@mapNotNull null
                val nombre      = child.child("nombre").getValue(String::class.java) ?: ""
                val descripcion = child.child("descripcion").getValue(String::class.java) ?: ""
                val lat         = child.child("latitud").getValue(Double::class.java) ?: return@mapNotNull null
                val lon         = child.child("longitud").getValue(Double::class.java) ?: return@mapNotNull null
                val radio       = child.child("radio").getValue(Double::class.java) ?: 100.0
                val nivel       = child.child("nivel").getValue(String::class.java) ?: "medio"
                val tipo        = child.child("tipo").getValue(String::class.java) ?: "peligro"
                val activa      = child.child("activa").getValue(Boolean::class.java) ?: true

                if (!activa) return@mapNotNull null

                ZonaPeligrosaModel(
                    zonaId      = zonaId,
                    nombre      = nombre,
                    descripcion = descripcion,
                    latitud     = lat,
                    longitud    = lon,
                    radio       = radio,
                    nivel       = nivel.toNivelPeligro(),
                    tipo        = tipo,
                    activa      = activa
                )
            }
        } catch (e: Exception) {
            println("❌ Error cargando zonas peligrosas: ${e.message}")
            emptyList()
        }
    }
}
