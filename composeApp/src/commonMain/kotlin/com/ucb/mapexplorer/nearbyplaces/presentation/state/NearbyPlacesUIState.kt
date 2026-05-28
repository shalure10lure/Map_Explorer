package com.ucb.mapexplorer.nearbyplaces.presentation.state


import com.ucb.mapexplorer.nearbyplaces.domain.model.PlaceModel

data class NearbyPlacesUIState(
    val places: List<PlaceModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedPlace: PlaceModel? = null,
    val userLat: Double = 0.0,
    val userLon: Double = 0.0,
    val searchQuery: String = "",
    val selectedCategory: String? = null,
    val radiusMeters: Int = 1500 // Rango de búsqueda por defecto: 1.5km
) {
    val filteredPlaces: List<PlaceModel>
        get() {
            var list = places
            if (searchQuery.isNotBlank()) {
                list = list.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                            it.category.contains(searchQuery, ignoreCase = true)
                }
            }
            selectedCategory?.let { cat -> list = list.filter { it.category == cat } }
            
            // Siempre ordenamos por distancia para mostrar los más cercanos primero
            return list.sortedBy { it.distanceMeters }
        }

    val categories: List<String>
        get() = places.map { it.category }.distinct().sorted()
}
