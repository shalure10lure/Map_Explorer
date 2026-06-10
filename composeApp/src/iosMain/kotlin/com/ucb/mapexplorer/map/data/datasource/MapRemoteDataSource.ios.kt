package com.ucb.mapexplorer.map.data.datasource

import com.ucb.mapexplorer.map.domain.model.TileModel
import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

actual class MapRemoteDataSource actual constructor() {

    private val FIREBASE_URL = "https://mapexplorer07-default-rtdb.firebaseio.com"
    private val client = HttpClient(Darwin) {
        install(HttpTimeout) { requestTimeoutMillis = 20_000 }
    }
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    actual suspend fun syncTile(uid: String, tile: TileModel) {
        try {
            val body = """{"descubierto_en":${tile.discoveredAt},"veces_visitado":${tile.visitCount},"ultimo_ingreso":${tile.lastVisited},"sincronizado":true}"""
            client.put("$FIREBASE_URL/usuarios/$uid/exploracion/tiles_descubiertos/${tile.tileX}_${tile.tileY}.json") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }
        } catch (e: Exception) {
            println("iOS syncTile error: ${e.message}")
        }
    }

    actual suspend fun getAllVisitedTiles(uid: String): List<TileModel> {
        return try {
            val response = client.get("$FIREBASE_URL/usuarios/$uid/exploracion/tiles_descubiertos.json")
            val body = response.bodyAsText()
            if (body == "null") return emptyList()

            val root = json.parseToJsonElement(body).jsonObject
            root.entries.mapNotNull { (nodeName, element) ->
                val coords = nodeName.split("_")
                if (coords.size < 2) return@mapNotNull null
                val x = coords[0].toIntOrNull() ?: return@mapNotNull null
                val y = coords[1].toIntOrNull() ?: return@mapNotNull null
                val obj = element.jsonObject
                TileModel(
                    tileX       = x,
                    tileY       = y,
                    discoveredAt = obj["descubierto_en"]?.jsonPrimitive?.long ?: 0L,
                    visitCount  = obj["veces_visitado"]?.jsonPrimitive?.int ?: 1,
                    lastVisited = obj["ultimo_ingreso"]?.jsonPrimitive?.long ?: 0L
                )
            }
        } catch (e: Exception) {
            println("iOS getAllVisitedTiles error: ${e.message}")
            emptyList()
        }
    }
}
