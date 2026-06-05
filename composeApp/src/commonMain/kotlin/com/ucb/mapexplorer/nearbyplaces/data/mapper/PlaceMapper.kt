package com.ucb.mapexplorer.nearbyplaces.data.mapper

import com.ucb.mapexplorer.nearbyplaces.data.dto.OverpassElementDto
import com.ucb.mapexplorer.nearbyplaces.data.entity.LugarCacheEntity
import com.ucb.mapexplorer.nearbyplaces.domain.model.PlaceModel
import kotlin.math.*

fun OverpassElementDto.toEntity(now: Long): LugarCacheEntity? {
    val lat = lat ?: center?.lat ?: return null
    val lon = lon ?: center?.lon ?: return null

    // CORRECCIÓN: Si no tiene nombre tag['name'], resolvemos un nombre alternativo basado en sus etiquetas
    val categoriaResuelta = resolveCategory(tags)
    val name = tags["name"] ?: tags["name:es"] ?: "$categoriaResuelta Cercana"

    return LugarCacheEntity(
        lugarId = "${type}_$id",
        nombre = name,
        categoria = categoriaResuelta,
        descripcion = tags["description"] ?: tags["tourism"] ?: tags["amenity"],
        latitud = lat,
        longitud = lon,
        rating = 4.0f,
        imagenUrl = null,
        iconoCategoria = resolveCategoryEmoji(tags),
        actualizadoEn = now
    )
}

fun LugarCacheEntity.toModel(userLat: Double = 0.0, userLon: Double = 0.0): PlaceModel = PlaceModel(
    id = lugarId,
    name = nombre,
    category = categoria,
    categoryIcon = iconoCategoria ?: "📍",
    description = descripcion ?: "",
    latitude = latitud,
    longitude = longitud,
    rating = rating,
    imageUrl = imagenUrl,
    distanceMeters = if (userLat != 0.0 && userLon != 0.0) haversine(userLat, userLon, latitud, longitud) else 0.0,
    tags = emptyMap()
)

private fun resolveCategory(tags: Map<String, String>): String = when {
    tags["amenity"] in listOf("restaurant", "fast_food", "bar", "pub") -> "Restaurante"
    tags["amenity"] == "cafe" -> "Cafetería"
    tags["amenity"] == "hotel" -> "Hotel"
    tags["tourism"] == "attraction" -> "Turismo"
    tags["tourism"] == "museum" -> "Museo"
    tags["tourism"] == "viewpoint" -> "Mirador"
    tags["tourism"] in listOf("hotel", "hostel") -> "Hotel"
    tags["leisure"] == "park" -> "Parque"
    tags["leisure"] == "garden" -> "Jardín"
    tags["leisure"] == "playground" -> "Juegos"
    tags["shop"] == "mall" -> "Centro Comercial"
    tags["shop"] == "supermarket" -> "Supermercado"
    else -> tags["amenity"]?.replaceFirstChar { it.uppercase() }
        ?: tags["tourism"]?.replaceFirstChar { it.uppercase() }
        ?: tags["leisure"]?.replaceFirstChar { it.uppercase() }
        ?: "Lugar"
}

private fun resolveCategoryEmoji(tags: Map<String, String>): String = when {
    tags["amenity"] in listOf("restaurant", "fast_food") -> "🍽️"
    tags["amenity"] == "cafe" -> "☕"
    tags["amenity"] in listOf("bar", "pub") -> "🍺"
    tags["amenity"] == "hotel" -> "🏨"
    tags["tourism"] == "attraction" -> "🎯"
    tags["tourism"] == "museum" -> "🏛️"
    tags["tourism"] == "viewpoint" -> "🔭"
    tags["tourism"] in listOf("hotel", "hostel") -> "🏨"
    tags["leisure"] == "park" -> "🌳"
    tags["leisure"] == "garden" -> "🌸"
    tags["leisure"] == "playground" -> "🎡"
    tags["shop"] == "mall" -> "🛍️"
    tags["shop"] == "supermarket" -> "🛒"
    else -> "📍"
}

fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371000.0 // Radio de la tierra en metros
    val dLat = (lat2 - lat1) * PI / 180.0
    val dLon = (lon2 - lon1) * PI / 180.0
    val a = sin(dLat / 2).pow(2) + cos(lat1 * PI / 180.0) * cos(lat2 * PI / 180.0) * sin(dLon / 2).pow(2)
    return r * 2 * atan2(sqrt(a), sqrt(1 - a))
}