package com.ucb.mapexplorer.map.data.service


import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
/*
class OverpassService(
    private val client: HttpClient = HttpClient()
) {

    suspend fun getNearbyPlaces(
        latitude: Double,
        longitude: Double
    ): List<PlaceDto> {

        val query = """
            [out:json];
            (
              node(around:500,$latitude,$longitude)[name];
            );
            out body;
        """.trimIndent()

        return try {
            client.post("https://overpass-api.de/api/interpreter") {
                contentType(ContentType.Text.Plain)
                setBody(query)
            }.body()

        } catch (e: Exception) {
            println("Overpass error: ${e.message}")
            emptyList()
        }
    }
}*/