package com.ucb.mapexplorer.auth.data.datasource

import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.*

/**
 * iOS implementation of FirebaseManager using Firebase REST API.
 * Replace FIREBASE_URL with your actual Firebase Realtime Database URL.
 */
actual class FirebaseManager actual constructor() {

    private val FIREBASE_URL = "https://mapexplorer07-default-rtdb.firebaseio.com"
    private val client = HttpClient(Darwin) {
        install(HttpTimeout) {
            connectTimeoutMillis = 15_000
            requestTimeoutMillis = 20_000
        }
    }

    actual suspend fun saveData(path: String, value: String) {
        try {
            val jsonValue = if (value.startsWith("{") || value.startsWith("[") ||
                value == "true" || value == "false" ||
                value.toDoubleOrNull() != null) {
                value
            } else {
                "\"$value\""
            }
            client.put("$FIREBASE_URL/$path.json") {
                contentType(ContentType.Application.Json)
                setBody(jsonValue)
            }
        } catch (e: Exception) {
            println("iOS Firebase saveData error: ${e.message}")
        }
    }

    actual suspend fun getData(path: String): String? {
        return try {
            val response = client.get("$FIREBASE_URL/$path.json")
            val body = response.bodyAsText()
            if (body == "null") null else body
        } catch (e: Exception) {
            println("iOS Firebase getData error: ${e.message}")
            null
        }
    }
}
