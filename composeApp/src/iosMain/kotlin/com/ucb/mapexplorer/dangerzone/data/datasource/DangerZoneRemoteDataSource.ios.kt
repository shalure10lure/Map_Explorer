package com.ucb.mapexplorer.dangerzone.data.datasource

import com.ucb.mapexplorer.dangerzone.domain.model.ZonaPeligrosaModel
import com.ucb.mapexplorer.dangerzone.domain.model.toNivelPeligro
import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.*

actual class DangerZoneRemoteDataSource actual constructor() {

    private val FIREBASE_URL = "https://mapexplorer07-default-rtdb.firebaseio.com"
    private val client = HttpClient(Darwin) {
        install(HttpTimeout) {
            requestTimeoutMillis = 20_000
        }
    }
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    actual suspend fun getZonasActivas(): List<ZonaPeligrosaModel> {
        return try {
            val response = client.get("$FIREBASE_URL/zonas_peligrosas.json")
            val body = response.bodyAsText()
            if (body == "null") return emptyList()

            val root = json.parseToJsonElement(body).jsonObject
            root.entries.mapNotNull { (zonaId, element) ->
                try {
                    val obj = element.jsonObject
                    val activa = obj["activa"]?.jsonPrimitive?.booleanOrNull ?: true
                    if (!activa) return@mapNotNull null

                    ZonaPeligrosaModel(
                        zonaId      = zonaId,
                        nombre      = obj["nombre"]?.jsonPrimitive?.content ?: "",
                        descripcion = obj["descripcion"]?.jsonPrimitive?.content ?: "",
                        latitud     = obj["latitud"]?.jsonPrimitive?.double ?: return@mapNotNull null,
                        longitud    = obj["longitud"]?.jsonPrimitive?.double ?: return@mapNotNull null,
                        radio       = obj["radio"]?.jsonPrimitive?.double ?: 100.0,
                        nivel       = (obj["nivel"]?.jsonPrimitive?.content ?: "medio").toNivelPeligro(),
                        tipo        = obj["tipo"]?.jsonPrimitive?.content ?: "peligro",
                        activa      = activa
                    )
                } catch (e: Exception) { null }
            }
        } catch (e: Exception) {
            println("iOS DangerZone error: ${e.message}")
            emptyList()
        }
    }
}
