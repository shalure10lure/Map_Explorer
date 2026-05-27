package com.ucb.mapexplorer.nearbyplaces.data.datasource

internal const val OVERPASS_URL = "https://overpass-api.de/api/interpreter"

internal fun buildOverpassQuery(lat: Double, lon: Double, radius: Int): String = """
    [out:json][timeout:25];
    (
      node["amenity"~"restaurant|cafe|bar|fast_food|hotel|pub"](around:$radius,$lat,$lon);
      node["tourism"~"attraction|museum|viewpoint|hotel|hostel|gallery"](around:$radius,$lat,$lon);
      node["leisure"~"park|garden|playground|sports_centre"](around:$radius,$lat,$lon);
      node["shop"~"mall|supermarket|department_store"](around:$radius,$lat,$lon);
      way["amenity"~"restaurant|cafe|bar|fast_food"](around:$radius,$lat,$lon);
      way["tourism"](around:$radius,$lat,$lon);
      way["leisure"~"park|garden"](around:$radius,$lat,$lon);
    );
    out center;
""".trimIndent()