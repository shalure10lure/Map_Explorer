package com.ucb.mapexplorer.nearbyplaces.data.datasource



import com.google.firebase.database.FirebaseDatabase
import com.ucb.mapexplorer.nearbyplaces.data.dto.OverpassResponseDto
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

actual class NearbyPlacesRemoteDataSource actual constructor() {

    // ── Ktor para Overpass API ────────────────────────────────────────────
    private val client = HttpClient(Android) {
        engine {
            connectTimeout = 15_000
            socketTimeout  = 25_000
        }
    }

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    // ── Firebase para persistencia del usuario ────────────────────────────
    private val firebaseDb = FirebaseDatabase.getInstance()

    // ─────────────────────────────────────────────────────────────────────
    // 1. Overpass API → lugares cercanos
    // ─────────────────────────────────────────────────────────────────────
    actual suspend fun fetchNearbyPlaces(
        lat: Double,
        lon: Double,
        radius: Int
    ): OverpassResponseDto {
        return try {
            val query = buildOverpassQuery(lat, lon, radius)
            val response: HttpResponse = client.post(OVERPASS_URL) {
                contentType(ContentType.Application.FormUrlEncoded)
                setBody("data=${query.encodeURLParameter()}")
            }
            json.decodeFromString(response.bodyAsText())
        } catch (e: Exception) {
            println("Overpass error (Android): ${e.message}")
            OverpassResponseDto(elements = emptyList())
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // 2. Firebase → guardar lugar descubierto
    //    usuarios/{uid}/exploracion/lugares_descubiertos/{lugarId}
    // ─────────────────────────────────────────────────────────────────────
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
                .setValue(
                    mapOf(
                        "nombre"          to nombre,
                        "categoria"       to categoria,
                        "latitud"         to lat,
                        "longitud"        to lon,
                        "descubierto_en"  to now,
                        "sincronizado"    to true
                    )
                ).await()
            println("✅ Lugar descubierto guardado: $lugarId para $uid")
        } catch (e: Exception) {
            println("Firebase error (saveLugarDescubierto): ${e.message}")
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // 3. Firebase → guardar lugar visitado (cuando el usuario abre detalle)
    //    usuarios/{uid}/exploracion/lugares_visitados/{lugarId}
    // ─────────────────────────────────────────────────────────────────────
    actual suspend fun saveLugarVisitado(
        uid: String,
        lugarId: String,
        nombre: String,
        categoria: String
    ) {
        try {
            val now = Clock.System.now().toEpochMilliseconds()
            val ref = firebaseDb.reference
                .child("usuarios")
                .child(uid)
                .child("exploracion")
                .child("lugares_visitados")
                .child(lugarId)

            ref.setValue(
                mapOf(
                    "nombre"        to nombre,
                    "categoria"     to categoria,
                    "ultima_visita" to now,
                    "sincronizado"  to true
                )
            ).await()
            println("✅ Lugar visitado guardado: $lugarId para $uid")
        } catch (e: Exception) {
            println("Firebase error (saveLugarVisitado): ${e.message}")
        }
    }
}