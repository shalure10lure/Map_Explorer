package com.ucb.mapexplorer.publication.data.datasource

import com.ucb.mapexplorer.publication.domain.model.PublicationModel
import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

actual class PublicationRemoteDataSource actual constructor() {

    private val FIREBASE_URL = "https://mapexplorer07-default-rtdb.firebaseio.com"

    private val client = HttpClient(Darwin) {
        install(HttpTimeout) { requestTimeoutMillis = 20_000 }
    }
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    actual suspend fun publish(publication: PublicationModel): Boolean {
        return try {
            val imageUrlJson = if (publication.imageUrl.isNullOrBlank()) "\"\""
            else "\"${publication.imageUrl}\""
            val body = """
                {
                  "uid":"${publication.uid}",
                  "userName":"${publication.userName}",
                  "lugarId":"${publication.lugarId}",
                  "locationName":"${publication.locationName.replace("\"","\\\"")}",
                  "category":"${publication.category}",
                  "categoryIcon":"${publication.categoryIcon}",
                  "imageUrl":$imageUrlJson,
                  "rating":${publication.rating},
                  "experience":"${publication.experience.replace("\"","\\\"")}",
                  "publishedAt":${publication.publishedAt},
                  "latitude":${publication.latitude},
                  "longitude":${publication.longitude}
                }
            """.trimIndent()

            client.post("$FIREBASE_URL/publicaciones.json") {
                contentType(ContentType.Application.Json)
                setBody(body)
            }

            // También guardar rating
            val safeId = publication.lugarId.replace(Regex("[.#\$\\[\\]/]"), "_")
            client.put("$FIREBASE_URL/ratings/$safeId/${publication.uid}.json") {
                contentType(ContentType.Application.Json)
                setBody("${publication.rating}")
            }
            true
        } catch (e: Exception) {
            println("iOS publish error: ${e.message}")
            false
        }
    }

    actual suspend fun getAllPublications(): List<PublicationModel> {
        return try {
            val response = client.get("$FIREBASE_URL/publicaciones.json")
            val body = response.bodyAsText()
            if (body == "null") return emptyList()

            val root = json.parseToJsonElement(body).jsonObject
            root.entries.mapNotNull { (pubId, element) ->
                try {
                    val obj = element.jsonObject
                    PublicationModel(
                        id           = pubId,
                        uid          = obj["uid"]?.jsonPrimitive?.content ?: "",
                        userName     = obj["userName"]?.jsonPrimitive?.content ?: "",
                        lugarId      = obj["lugarId"]?.jsonPrimitive?.content ?: "",
                        locationName = obj["locationName"]?.jsonPrimitive?.content ?: "",
                        category     = obj["category"]?.jsonPrimitive?.content ?: "",
                        categoryIcon = obj["categoryIcon"]?.jsonPrimitive?.content ?: "📍",
                        imageUrl     = obj["imageUrl"]?.jsonPrimitive?.content?.takeIf { it.isNotBlank() },
                        rating       = obj["rating"]?.jsonPrimitive?.int ?: 0,
                        experience   = obj["experience"]?.jsonPrimitive?.content ?: "",
                        publishedAt  = obj["publishedAt"]?.jsonPrimitive?.long ?: 0L,
                        latitude     = obj["latitude"]?.jsonPrimitive?.double ?: 0.0,
                        longitude    = obj["longitude"]?.jsonPrimitive?.double ?: 0.0
                    )
                } catch (e: Exception) { null }
            }.sortedByDescending { it.publishedAt }
        } catch (e: Exception) {
            println("iOS getAllPublications error: ${e.message}")
            emptyList()
        }
    }

    actual suspend fun getPlaceAverageRating(lugarId: String): Float {
        return try {
            val safeId = lugarId.replace(Regex("[.#\$\\[\\]/]"), "_")
            val response = client.get("$FIREBASE_URL/ratings/$safeId.json")
            val body = response.bodyAsText()
            if (body == "null") return 0f
            val obj = json.parseToJsonElement(body).jsonObject
            val ratings = obj.values.mapNotNull { it.jsonPrimitive.intOrNull?.toFloat() }
            if (ratings.isEmpty()) 0f else ratings.average().toFloat()
        } catch (e: Exception) { 0f }
    }
}
