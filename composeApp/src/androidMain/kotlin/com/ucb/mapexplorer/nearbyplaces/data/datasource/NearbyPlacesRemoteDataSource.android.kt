package com.ucb.mapexplorer.nearbyplaces.data.datasource

import com.google.firebase.database.FirebaseDatabase
import com.ucb.mapexplorer.nearbyplaces.data.dto.OverpassResponseDto
import com.ucb.mapexplorer.nearbyplaces.data.service.OVERPASS_URL
import com.ucb.mapexplorer.nearbyplaces.data.service.buildOverpassQuery
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

actual class NearbyPlacesRemoteDataSource actual constructor() {

    private val client = HttpClient(OkHttp) {
        install(HttpTimeout) {
            connectTimeoutMillis = 15_000
            requestTimeoutMillis = 25_000
            socketTimeoutMillis  = 25_000
        }
    }

    private val json = Json { ignoreUnknownKeys = true; isLenient = true }
    private val firebaseDb = FirebaseDatabase.getInstance()

    actual suspend fun fetchNearbyPlaces(lat: Double, lon: Double, radius: Int): OverpassResponseDto {
        return try {
            val query = buildOverpassQuery(lat, lon, radius)
            val response = client.submitForm(
                url = OVERPASS_URL,
                formParameters = parameters { append("data", query) }
            )
            json.decodeFromString(response.bodyAsText())
        } catch (e: Exception) {
            println("❌ Overpass error: ${e.message}")
            OverpassResponseDto(elements = emptyList())
        }
    }

    actual suspend fun saveLugarDescubierto(
        uid: String, lugarId: String, nombre: String,
        categoria: String, lat: Double, lon: Double
    ) {
        try {
            val now = Clock.System.now().toEpochMilliseconds()
            val safeId = lugarId.replace(Regex("[.#\$\\[\\]/]"), "_")
            firebaseDb.reference
                .child("usuarios").child(uid).child("exploracion")
                .child("lugares_descubiertos").child(safeId)
                .setValue(mapOf(
                    "nombre" to nombre, "categoria" to categoria,
                    "latitud" to lat, "longitud" to lon,
                    "descubierto_en" to now, "sincronizado" to true
                )).await()
        } catch (e: Exception) {
            println("❌ Firebase error (saveLugarDescubierto): ${e.message}")
        }
    }

    actual suspend fun saveLugarVisitado(
        uid: String, lugarId: String, nombre: String, categoria: String
    ) {
        try {
            val now = Clock.System.now().toEpochMilliseconds()
            val safeId = lugarId.replace(Regex("[.#\$\\[\\]/]"), "_")
            firebaseDb.reference
                .child("usuarios").child(uid).child("exploracion")
                .child("lugares_visitados").child(safeId)
                .setValue(mapOf(
                    "nombre" to nombre, "categoria" to categoria,
                    "ultima_visita" to now, "sincronizado" to true
                )).await()
        } catch (e: Exception) {
            println("❌ Firebase error (saveLugarVisitado): ${e.message}")
        }
    }

    // ── NUEVOS: Favoritos y Guardados en Firebase ─────────────────────────

    actual suspend fun saveFavorito(
        uid: String, lugarId: String, nombre: String,
        categoria: String, lat: Double, lon: Double
    ) {
        try {
            val now = Clock.System.now().toEpochMilliseconds()
            val safeId = lugarId.replace(Regex("[.#\$\\[\\]/]"), "_")
            firebaseDb.reference
                .child("usuarios").child(uid).child("mas_opciones")
                .child("lugares_favoritos").child(safeId)
                .setValue(mapOf(
                    "nombre" to nombre, "categoria" to categoria,
                    "latitud" to lat, "longitud" to lon,
                    "agregado_en" to now
                )).await()
        } catch (e: Exception) {
            println("❌ Firebase error (saveFavorito): ${e.message}")
        }
    }

    actual suspend fun removeFavorito(uid: String, lugarId: String) {
        try {
            val safeId = lugarId.replace(Regex("[.#\$\\[\\]/]"), "_")
            firebaseDb.reference
                .child("usuarios").child(uid).child("mas_opciones")
                .child("lugares_favoritos").child(safeId)
                .removeValue().await()
        } catch (e: Exception) {
            println("❌ Firebase error (removeFavorito): ${e.message}")
        }
    }

    actual suspend fun saveGuardado(
        uid: String, lugarId: String, nombre: String,
        categoria: String, lat: Double, lon: Double
    ) {
        try {
            val now = Clock.System.now().toEpochMilliseconds()
            val safeId = lugarId.replace(Regex("[.#\$\\[\\]/]"), "_")
            firebaseDb.reference
                .child("usuarios").child(uid).child("mas_opciones")
                .child("ver_lugares_favoritos").child(safeId)
                .setValue(mapOf(
                    "nombre" to nombre, "categoria" to categoria,
                    "latitud" to lat, "longitud" to lon,
                    "guardado_en" to now
                )).await()
        } catch (e: Exception) {
            println("❌ Firebase error (saveGuardado): ${e.message}")
        }
    }

    actual suspend fun removeGuardado(uid: String, lugarId: String) {
        try {
            val safeId = lugarId.replace(Regex("[.#\$\\[\\]/]"), "_")
            firebaseDb.reference
                .child("usuarios").child(uid).child("mas_opciones")
                .child("ver_lugares_favoritos").child(safeId)
                .removeValue().await()
        } catch (e: Exception) {
            println("❌ Firebase error (removeGuardado): ${e.message}")
        }
    }
}