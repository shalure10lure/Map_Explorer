package com.ucb.mapexplorer.nearbyplaces.data.mapper

import com.ucb.mapexplorer.nearbyplaces.data.dto.OverpassElementDto
import com.ucb.mapexplorer.nearbyplaces.data.entity.LugarCacheEntity
import com.ucb.mapexplorer.nearbyplaces.domain.model.PlaceModel
import kotlin.math.*

/**
 * Convierte un elemento Overpass a entidad de Room.
 *
 * REGLA ANTI-DUPLICADOS: devuelve null si el elemento no tiene nombre real
 * (los duplicados en BD venían de lugares sin nombre que se guardaban con
 * distintos IDs de nodo OSM pero representaban el mismo tipo de lugar).
 *
 * El lugarId usa el patrón "{type}_{id}" de OSM que es ESTABLE entre
 * sesiones — Room aplica OnConflictStrategy.REPLACE así que los re-insertos
 * actualizan en lugar de crear duplicados.
 */
fun OverpassElementDto.toEntity(now: Long): LugarCacheEntity? {
    val lat  = lat ?: center?.lat ?: return null
    val lon  = lon ?: center?.lon ?: return null

    // Solo procesamos lugares con nombre real — FILTRO ANTI-DUPLICADOS
    val name = tags["name"] ?: tags["name:es"] ?: return null
    if (name.isBlank()) return null

    val categoria  = resolveCategory(tags)
    val descripcion = buildDescription(tags, categoria)
    val imageUrl   = resolveImageUrl(tags)

    return LugarCacheEntity(
        lugarId        = "${type}_$id",   // ID estable de OSM — sin duplicados en Room
        nombre         = name,
        categoria      = categoria,
        descripcion    = descripcion,
        latitud        = lat,
        longitud       = lon,
        rating         = resolveRating(tags),
        imagenUrl      = imageUrl,
        iconoCategoria = resolveCategoryEmoji(tags),
        actualizadoEn  = now
    )
}

fun LugarCacheEntity.toModel(userLat: Double = 0.0, userLon: Double = 0.0): PlaceModel = PlaceModel(
    id             = lugarId,
    name           = nombre,
    category       = categoria,
    categoryIcon   = iconoCategoria ?: "📍",
    description    = descripcion ?: "",
    latitude       = latitud,
    longitude      = longitud,
    rating         = rating,
    imageUrl       = imagenUrl,
    distanceMeters = if (userLat != 0.0 && userLon != 0.0)
        haversine(userLat, userLon, latitud, longitud)
    else 0.0,
    tags           = emptyMap()
)

// ── Descripción enriquecida ───────────────────────────────────────────────────
private fun buildDescription(tags: Map<String, String>, categoria: String): String {
    val parts = mutableListOf<String>()

    // Descripción directa
    tags["description"]?.takeIf { it.isNotBlank() }?.let { parts.add(it) }

    // Horario
    tags["opening_hours"]?.takeIf { it.isNotBlank() }?.let { parts.add("Horario: $it") }

    // Teléfono
    tags["phone"]?.takeIf { it.isNotBlank() }?.let { parts.add("Tel: $it") }
    tags["contact:phone"]?.takeIf { it.isNotBlank() && tags["phone"] == null }
        ?.let { parts.add("Tel: $it") }

    // Dirección
    val street = tags["addr:street"] ?: ""
    val number = tags["addr:housenumber"] ?: ""
    val addr   = listOf(street, number).filter { it.isNotBlank() }.joinToString(" ")
    if (addr.isNotBlank()) parts.add(addr)

    // Website (solo si es corto)
    tags["website"]?.takeIf { it.isNotBlank() && it.length < 60 }?.let { parts.add(it) }
    tags["contact:website"]?.takeIf { it.isNotBlank() && it.length < 60 && tags["website"] == null }
        ?.let { parts.add(it) }

    // Si no encontramos nada, generamos descripción por tipo de lugar
    if (parts.isEmpty()) {
        parts.add(buildFallbackDescription(categoria, tags))
    }

    return parts.joinToString(" · ")
}

private fun buildFallbackDescription(categoria: String, tags: Map<String, String>): String {
    return when (categoria) {
        "Restaurante"      -> {
            val cuisine = tags["cuisine"]?.split(";")?.firstOrNull()
                ?.replaceFirstChar { it.uppercase() }
            if (cuisine != null) "Restaurante de $cuisine" else "Gastronomía local"
        }
        "Cafetería"        -> "Café y bebidas"
        "Bar"              -> "Bar y entretenimiento"
        "Hotel"            -> "Alojamiento"
        "Hospital"         -> "Centro de salud y emergencias"
        "Farmacia"         -> "Medicamentos y salud"
        "Escuela"          -> "Centro educativo"
        "Universidad"      -> "Educación superior"
        "Museo"            -> "Museo y cultura"
        "Parque"           -> "Espacio verde y recreación"
        "Jardín"           -> "Jardín botánico"
        "Estadio"          -> "Instalaciones deportivas"
        "Cine"             -> "Entretenimiento y cine"
        "Teatro"           -> "Arte escénico y teatro"
        "Supermercado"     -> "Compras y abastecimiento"
        "Centro Comercial" -> "Centro comercial"
        "Banco"            -> "Servicios bancarios y financieros"
        "Monumento"        -> "Sitio histórico y patrimonial"
        "Mirador"          -> "Punto de vista panorámico"
        "Iglesia"          -> "Lugar de culto y fe"
        else               -> "$categoria en esta zona"
    }
}

// ── URL de imagen desde Wikimedia Commons (tag image o wikimedia_commons) ──────
// Overpass no provee imágenes directas, pero muchos nodos OSM tienen el tag
// "image" con una URL directa, o "wikimedia_commons" con el nombre del archivo.
private fun resolveImageUrl(tags: Map<String, String>): String? {
    // 1. URL directa (la más confiable)
    val direct = tags["image"] ?: tags["contact:photo"]
    if (!direct.isNullOrBlank() && (direct.startsWith("http://") || direct.startsWith("https://"))) {
        return direct
    }

    // 2. Archivo de Wikimedia Commons → construir URL de thumbnail
    val wikimedia = tags["wikimedia_commons"]
    if (!wikimedia.isNullOrBlank()) {
        // Formato: "File:Nombre_del_archivo.jpg"
        val filename = wikimedia.removePrefix("File:").replace(" ", "_")
        if (filename.isNotBlank()) {
            // URL estándar de thumbnail de Wikimedia Commons (320px)
            val encoded = filename
            return "https://commons.wikimedia.org/wiki/Special:FilePath/${encoded}?width=640"
        }
    }

    // Sin imagen disponible en OSM → devuelve null (se mostrará el emoji placeholder)
    return null
}

private fun resolveRating(tags: Map<String, String>): Float {
    return when {
        tags["tourism"] == "attraction"                              -> 4.5f
        tags["tourism"] == "museum"                                  -> 4.2f
        tags["leisure"] == "park"                                    -> 4.3f
        tags["amenity"] == "hospital"                                -> 4.0f
        tags["amenity"] in listOf("restaurant", "fast_food")        -> 4.0f
        tags["amenity"] == "cafe"                                    -> 4.1f
        tags["historic"] != null                                     -> 4.4f
        else                                                         -> 4.0f
    }
}

private fun resolveCategory(tags: Map<String, String>): String = when {
    tags["amenity"] in listOf("restaurant", "fast_food") -> "Restaurante"
    tags["amenity"] == "cafe"                             -> "Cafetería"
    tags["amenity"] in listOf("bar", "pub")               -> "Bar"
    tags["amenity"] == "hotel"                            -> "Hotel"
    tags["amenity"] == "hospital"                         -> "Hospital"
    tags["amenity"] == "pharmacy"                         -> "Farmacia"
    tags["amenity"] == "bank"                             -> "Banco"
    tags["amenity"] == "atm"                              -> "Cajero"
    tags["amenity"] == "school"                           -> "Escuela"
    tags["amenity"] == "university"                       -> "Universidad"
    tags["amenity"] == "cinema"                           -> "Cine"
    tags["amenity"] == "theatre"                          -> "Teatro"
    tags["amenity"] in listOf("stadium", "sports_centre") -> "Estadio"
    tags["amenity"] == "place_of_worship"                 -> "Iglesia"
    tags["tourism"] == "attraction"                       -> "Atracción"
    tags["tourism"] == "museum"                           -> "Museo"
    tags["tourism"] == "viewpoint"                        -> "Mirador"
    tags["tourism"] in listOf("hotel", "hostel")          -> "Hotel"
    tags["tourism"] == "gallery"                          -> "Galería"
    tags["leisure"] in listOf("park", "garden")           -> "Parque"
    tags["leisure"] == "playground"                       -> "Juegos"
    tags["leisure"] in listOf("stadium", "sports_centre") -> "Estadio"
    tags["shop"] == "mall"                                -> "Centro Comercial"
    tags["shop"] == "supermarket"                         -> "Supermercado"
    tags["historic"] in listOf("monument", "memorial")    -> "Monumento"
    tags["historic"] == "castle"                          -> "Castillo"
    else -> tags["amenity"]?.replaceFirstChar { it.uppercase() }
        ?: tags["tourism"]?.replaceFirstChar { it.uppercase() }
        ?: tags["leisure"]?.replaceFirstChar { it.uppercase() }
        ?: "Lugar"
}

fun resolveCategoryEmoji(tags: Map<String, String>): String = when {
    tags["amenity"] in listOf("restaurant", "fast_food") -> "🍽️"
    tags["amenity"] == "cafe"                             -> "☕"
    tags["amenity"] in listOf("bar", "pub")               -> "🍺"
    tags["amenity"] == "hotel"                            -> "🏨"
    tags["amenity"] == "hospital"                         -> "🏥"
    tags["amenity"] == "pharmacy"                         -> "💊"
    tags["amenity"] == "bank"                             -> "🏦"
    tags["amenity"] == "atm"                              -> "🏧"
    tags["amenity"] == "school"                           -> "🏫"
    tags["amenity"] == "university"                       -> "🎓"
    tags["amenity"] == "cinema"                           -> "🎬"
    tags["amenity"] == "theatre"                          -> "🎭"
    tags["amenity"] in listOf("stadium", "sports_centre") -> "🏟️"
    tags["amenity"] == "place_of_worship"                 -> "⛪"
    tags["tourism"] == "attraction"                       -> "🎯"
    tags["tourism"] == "museum"                           -> "🏛️"
    tags["tourism"] == "viewpoint"                        -> "🔭"
    tags["tourism"] in listOf("hotel", "hostel")          -> "🏨"
    tags["tourism"] == "gallery"                          -> "🖼️"
    tags["leisure"] in listOf("park", "garden")           -> "🌳"
    tags["leisure"] == "playground"                       -> "🎡"
    tags["leisure"] in listOf("stadium", "sports_centre") -> "🏟️"
    tags["shop"] == "mall"                                -> "🛍️"
    tags["shop"] == "supermarket"                         -> "🛒"
    tags["historic"] in listOf("monument", "memorial")    -> "🗿"
    tags["historic"] == "castle"                          -> "🏰"
    else                                                  -> "📍"
}

fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r    = 6371000.0
    val dLat = (lat2 - lat1) * PI / 180.0
    val dLon = (lon2 - lon1) * PI / 180.0
    val a = sin(dLat / 2).pow(2) +
            cos(lat1 * PI / 180.0) * cos(lat2 * PI / 180.0) * sin(dLon / 2).pow(2)
    return r * 2 * atan2(sqrt(a), sqrt(1 - a))
}