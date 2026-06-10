package com.ucb.mapexplorer.nearbyplaces.data.datasource

import com.ucb.mapexplorer.nearbyplaces.data.dto.OverpassResponseDto
import com.ucb.mapexplorer.nearbyplaces.data.service.OVERPASS_URL
import com.ucb.mapexplorer.nearbyplaces.data.service.buildOverpassQuery
import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.*

actual class NearbyPlacesRemoteDataSource actual constructor() {

    private val FIREBASE_URL = "https://mapexplorer07-default-rtdb.firebaseio.com"
    private val client = HttpClient(Darwin) {
        install(HttpTimeout) {
            connectTimeoutMillis = 15_000
            requestTimeoutMillis = 25_000
            socketTimeoutMillis  = 25_000
        }
    }
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    actual suspend fun fetchNearbyPlaces(lat: Double, lon: Double, radius: Int): OverpassResponseDto {
        return try {
            val query = buildOverpassQuery(lat, lon, radius)
            val response = client.submitForm(
                url = OVERPASS_URL,
                formParameters = parameters { append("data", query) }
            )
            json.decodeFromString(response.bodyAsText())
        } catch (e: Exception) {
            println("iOS Overpass error: ${e.message}")
            OverpassResponseDto(elements = emptyList())
        }
    }
    actual suspend fun fetchByQuery(query: String): OverpassResponseDto {
        return try {
            val response = client.submitForm(
                url = OVERPASS_URL,
                formParameters = parameters { append("data", query) }
            )
            json.decodeFromString(response.bodyAsText())
        } catch (e: Exception) {
            println("iOS Overpass fetchByQuery error: ${e.message}")
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
            val body = """{"nombre":"$nombre","categoria":"$categoria","latitud":$lat,"longitud":$lon,"descubierto_en":$now,"sincronizado":true}"""
            client.put("$FIREBASE_URL/usuarios/$uid/exploracion/lugares_descubiertos/$safeId.json") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        } catch (e: Exception) {
            println("iOS saveLugarDescubierto error: ${e.message}")
        }
    }

    actual suspend fun saveLugarVisitado(
        uid: String, lugarId: String, nombre: String, categoria: String
    ) {
        try {
            val now = Clock.System.now().toEpochMilliseconds()
            val safeId = lugarId.replace(Regex("[.#\$\\[\\]/]"), "_")
            val body = """{"nombre":"$nombre","categoria":"$categoria","ultima_visita":$now,"sincronizado":true}"""
            client.put("$FIREBASE_URL/usuarios/$uid/exploracion/lugares_visitados/$safeId.json") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        } catch (e: Exception) {
            println("iOS saveLugarVisitado error: ${e.message}")
        }
    }

    actual suspend fun saveFavorito(
        uid: String, lugarId: String, nombre: String,
        categoria: String, lat: Double, lon: Double
    ) {
        try {
            val now = Clock.System.now().toEpochMilliseconds()
            val safeId = lugarId.replace(Regex("[.#\$\\[\\]/]"), "_")
            val body = """{"nombre":"$nombre","categoria":"$categoria","latitud":$lat,"longitud":$lon,"agregado_en":$now}"""
            client.put("$FIREBASE_URL/usuarios/$uid/mas_opciones/lugares_favoritos/$safeId.json") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        } catch (e: Exception) {
            println("iOS saveFavorito error: ${e.message}")
        }
    }

    actual suspend fun removeFavorito(uid: String, lugarId: String) {
        try {
            val safeId = lugarId.replace(Regex("[.#\$\\[\\]/]"), "_")
            client.delete("$FIREBASE_URL/usuarios/$uid/mas_opciones/lugares_favoritos/$safeId.json")
        } catch (e: Exception) {
            println("iOS removeFavorito error: ${e.message}")
        }
    }

    actual suspend fun saveGuardado(
        uid: String, lugarId: String, nombre: String,
        categoria: String, lat: Double, lon: Double
    ) {
        try {
            val now = Clock.System.now().toEpochMilliseconds()
            val safeId = lugarId.replace(Regex("[.#\$\\[\\]/]"), "_")
            val body = """{"nombre":"$nombre","categoria":"$categoria","latitud":$lat,"longitud":$lon,"guardado_en":$now}"""
            client.put("$FIREBASE_URL/usuarios/$uid/mas_opciones/ver_lugares_favoritos/$safeId.json") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        } catch (e: Exception) {
            println("iOS saveGuardado error: ${e.message}")
        }
    }

    actual suspend fun removeGuardado(uid: String, lugarId: String) {
        try {
            val safeId = lugarId.replace(Regex("[.#\$\\[\\]/]"), "_")
            client.delete("$FIREBASE_URL/usuarios/$uid/mas_opciones/ver_lugares_favoritos/$safeId.json")
        } catch (e: Exception) {
            println("iOS removeGuardado error: ${e.message}")
        }
    }
}
