package com.ucb.mapexplorer.onboarding.data.datasource

import io.ktor.client.*
import io.ktor.client.engine.darwin.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.*

actual class RemoteConfigDataSource actual constructor() {

    // ── CONFIG — replace with your actual values ───────────────────────────
    private val PROJECT_NUMBER = "1089371268035"   // numeric, e.g. "123456789"
    private val APP_ID         = "1:1089371268035:ios:70fa497353e1d973281376"                 // from google-services
    private val API_KEY        = "AIzaSyBXUodAhGhp-4MXXJXFel2_49-Ojlcp_9c"           // Web API key

    private val FIREBASE_DB_URL = "https://mapexplorer07-default-rtdb.firebaseio.com"
    // ── END CONFIG ─────────────────────────────────────────────────────────

    private val client = HttpClient(Darwin) {
        install(HttpTimeout) { requestTimeoutMillis = 20_000 }
    }
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    actual suspend fun fetchOnboardingJson(): String {
        return try {
            fetchFromRemoteConfig()
        } catch (e: Exception) {
            println("iOS RemoteConfig error: ${e.message}")
            defaultOnboardingJson()
        }
    }

    private suspend fun fetchFromRemoteConfig(): String {
        // Firebase Remote Config REST API
        val url = "https://firebaseremoteconfig.googleapis.com/v1/projects/$PROJECT_NUMBER/remoteConfig"
        val response = client.get(url) {
            header("Authorization", "key=$API_KEY")
            header("Accept-Encoding", "gzip")
        }
        val body = response.bodyAsText()
        val root = json.parseToJsonElement(body).jsonObject
        val parameters = root["parameters"]?.jsonObject ?: return defaultOnboardingJson()
        val onboardingParam = parameters["onboarding_config"]?.jsonObject
            ?: return defaultOnboardingJson()
        val defaultValue = onboardingParam["defaultValue"]?.jsonObject
            ?: return defaultOnboardingJson()
        return defaultValue["value"]?.jsonPrimitive?.content ?: defaultOnboardingJson()
    }

    private fun defaultOnboardingJson(): String = """
        [
          {
            "id": 1,
            "title": {"es": "Bienvenido a MapExplorer", "en": "Welcome to MapExplorer"},
            "description": {"es": "Descubre el mundo explorando tu entorno", "en": "Discover the world by exploring your surroundings"},
            "image_url": {"es": "https://i.imgur.com/placeholder1.png", "en": "https://i.imgur.com/placeholder1.png"}
          },
          {
            "id": 2,
            "title": {"es": "Desbloquea tiles", "en": "Unlock tiles"},
            "description": {"es": "Camina por nuevas zonas para revelar el mapa", "en": "Walk through new areas to reveal the map"},
            "image_url": {"es": "https://i.imgur.com/placeholder2.png", "en": "https://i.imgur.com/placeholder2.png"}
          },
          {
            "id": 3,
            "title": {"es": "Lugares cercanos", "en": "Nearby places"},
            "description": {"es": "Descubre restaurantes, parques y más", "en": "Discover restaurants, parks and more"},
            "image_url": {"es": "https://i.imgur.com/placeholder3.png", "en": "https://i.imgur.com/placeholder3.png"}
          }
        ]
    """.trimIndent()
}
