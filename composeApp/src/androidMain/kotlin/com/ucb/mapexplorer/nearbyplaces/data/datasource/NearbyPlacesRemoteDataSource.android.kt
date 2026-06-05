package com.ucb.mapexplorer.nearbyplaces.data.datasource

import com.google.firebase.database.FirebaseDatabase
import com.ucb.mapexplorer.nearbyplaces.data.dto.OverpassResponseDto
import com.ucb.mapexplorer.nearbyplaces.data.service.OVERPASS_URL
import com.ucb.mapexplorer.nearbyplaces.data.service.buildOverpassQuery
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.HttpTimeout // 🟢 NUEVO IMPORT PARA KTOR 3
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

actual class NearbyPlacesRemoteDataSource actual constructor() {

    // 🟢 Configuración adaptada y robusta para Ktor 3
    private val client = HttpClient(OkHttp) { // 🟢 Cambia Android por OkHttp
        install(HttpTimeout) {
            connectTimeoutMillis = 15_000
            requestTimeoutMillis = 25_000
            socketTimeoutMillis = 25_000
        }
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val firebaseDb = FirebaseDatabase.getInstance()

    actual suspend fun fetchNearbyPlaces(
        lat: Double,
        lon: Double,
        radius: Int
    ): OverpassResponseDto {
        return try {
            val query = buildOverpassQuery(lat, lon, radius)
            val response = client.submitForm(
                url = OVERPASS_URL,
                formParameters = parameters {
                    append("data", query)
                }
            )
            json.decodeFromString(response.bodyAsText())
        } catch (e: Exception) {
            println("❌ Overpass error (Android): ${e.message}")
            OverpassResponseDto(elements = emptyList())
        }
    }

    actual suspend fun saveLugarDescubierto(
        uid: String,
        lugarId: String,
        nombre: String,
        categoria: String,
        lat: Double,
        lon: Double
    ) {
        try {
            val now = Clock.System.now().toEpochMilliseconds()
            firebaseDb.reference
                .child("usuarios")
                .child(uid)
                .child("exploracion")
                .child("lugares_descubiertos")
                .child(lugarId)
                .setValue(mapOf(
                    "nombre" to nombre,
                    "categoria" to categoria,
                    "latitud" to lat,
                    "longitud" to lon,
                    "descubierto_en" to now,
                    "sincronizado" to true
                )).await()
        } catch (e: Exception) {
            println("❌ Firebase error (saveLugarDescubierto): ${e.message}")
        }
    }

    actual suspend fun saveLugarVisitado(
        uid: String,
        lugarId: String,
        nombre: String,
        categoria: String
    ) {
        try {
            val now = Clock.System.now().toEpochMilliseconds()
            firebaseDb.reference
                .child("usuarios")
                .child(uid)
                .child("exploracion")
                .child("lugares_visitados")
                .child(lugarId)
                .setValue(mapOf(
                    "nombre" to nombre,
                    "categoria" to categoria,
                    "ultima_visita" to now,
                    "sincronizado" to true
                )).await()
        } catch (e: Exception) {
            println("❌ Firebase error (saveLugarVisitado): ${e.message}")
        }
    }
}