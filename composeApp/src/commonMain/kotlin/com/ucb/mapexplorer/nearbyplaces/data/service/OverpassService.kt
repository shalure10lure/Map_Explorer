package com.ucb.mapexplorer.nearbyplaces.data.service

internal const val OVERPASS_URL = "https://overpass-api.de/api/interpreter"

internal const val DEFAULT_RADIUS = 200


internal fun buildOverpassQuery(lat: Double, lon: Double, radius: Int = DEFAULT_RADIUS): String = """
    [out:json][timeout:15];
    (
      nwr["amenity"~"restaurant|cafe|bar|fast_food|pub|food_court|ice_cream"]["name"](around:$radius,$lat,$lon);
      nwr["amenity"~"hotel|hostel|guest_house"]["name"](around:$radius,$lat,$lon);
      nwr["amenity"~"bank|atm|money_transfer"]["name"](around:$radius,$lat,$lon);
      nwr["amenity"~"hospital|clinic|dentist|pharmacy|veterinary"]["name"](around:$radius,$lat,$lon);
      nwr["amenity"~"school|university|college|library"]["name"](around:$radius,$lat,$lon);
      nwr["amenity"~"cinema|theatre|arts_centre"]["name"](around:$radius,$lat,$lon);
      nwr["amenity"~"stadium|sports_centre|gym|swimming_pool"]["name"](around:$radius,$lat,$lon);
      nwr["amenity"~"place_of_worship"]["name"](around:$radius,$lat,$lon);
      nwr["amenity"~"marketplace|post_office|police|fire_station"]["name"](around:$radius,$lat,$lon);
      nwr["amenity"~"parking"]["name"](around:$radius,$lat,$lon);

      nwr["tourism"~"attraction|museum|viewpoint|gallery|information"]["name"](around:$radius,$lat,$lon);
      nwr["tourism"~"hotel|hostel|camp_site"]["name"](around:$radius,$lat,$lon);

      nwr["leisure"~"park|garden|playground|sports_centre|stadium|fitness_centre"]["name"](around:$radius,$lat,$lon);
      nwr["leisure"~"spa|sauna|bowling_alley"]["name"](around:$radius,$lat,$lon);

      nwr["shop"~"mall|supermarket|department_store|convenience"]["name"](around:$radius,$lat,$lon);
      nwr["shop"~"bakery|butcher|clothes|electronics|books|hardware"]["name"](around:$radius,$lat,$lon);
      nwr["shop"~"hairdresser|beauty|optician"]["name"](around:$radius,$lat,$lon);

      nwr["historic"~"monument|memorial|castle|ruins|archaeological_site"]["name"](around:$radius,$lat,$lon);

      nwr["natural"~"peak|waterfall|hot_spring"]["name"](around:$radius,$lat,$lon);
    );
    out body center qt;
""".trimIndent()