package com.ucb.mapexplorer.nearbyplaces.data.service

internal const val OVERPASS_URL = "https://overpass-api.de/api/interpreter"
// Versión ultra-compatible si tu Dto no soporta leer sub-objetos "center"
internal fun buildOverpassQuery(lat: Double, lon: Double, radius: Int): String = """
    [out:json][timeout:30];
    (
      nwr["amenity"~"restaurant|cafe|bar|fast_food|hotel|pub|bank|atm|stadium|sports_centre"](around:${'$'}radius,${'$'}lat,${'$'}lon);
      nwr["tourism"~"attraction|museum|viewpoint|hotel|hostel|gallery|information"](around:${'$'}radius,${'$'}lat,${'$'}lon);
      nwr["leisure"~"park|garden|playground|sports_centre|stadium"](around:${'$'}radius,${'$'}lat,${'$'}lon);
      nwr["shop"~"mall|supermarket|department_store"](around:${'$'}radius,${'$'}lat,${'$'}lon);
    );
    out body qt; // ⚡ qt (quadtile) hace que el servidor procese la localización de manera ultra veloz
""".trimIndent()