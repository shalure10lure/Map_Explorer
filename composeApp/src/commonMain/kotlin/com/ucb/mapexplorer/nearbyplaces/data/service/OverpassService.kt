package com.ucb.mapexplorer.nearbyplaces.data.service


internal const val OVERPASS_URL = "https://overpass-api.de/api/interpreter"

/**
 * Query Overpass que SOLO trae nodos/ways/relations que:
 * 1. Tienen tag "name" (nombre real del lugar)
 * 2. Son de categorías relevantes y visuales
 *
 * Usamos "out center" para que ways/relations devuelvan coordenadas lat/lon.
 */
internal fun buildOverpassQuery(lat: Double, lon: Double, radius: Int): String = """
    [out:json][timeout:30];
    (
      nwr["amenity"~"restaurant|cafe|bar|fast_food|pub|hotel|bank|stadium|sports_centre|hospital|pharmacy|school|university|cinema|theatre|place_of_worship"]["name"](around:$radius,$lat,$lon);
      nwr["tourism"~"attraction|museum|viewpoint|hotel|hostel|gallery|information"]["name"](around:$radius,$lat,$lon);
      nwr["leisure"~"park|garden|playground|sports_centre|stadium"]["name"](around:$radius,$lat,$lon);
      nwr["shop"~"mall|supermarket|department_store"]["name"](around:$radius,$lat,$lon);
      nwr["historic"~"monument|memorial|castle"]["name"](around:$radius,$lat,$lon);
    );
    out body center qt;
""".trimIndent()