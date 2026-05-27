package com.ucb.mapexplorer.map.presentation.state

sealed interface MapEffect {
    data class ShowError(val message: String) : MapEffect
    data class ShowSnackbar(val message: String) : MapEffect

    /** Nuevo tile descubierto — la UI puede mostrar animación/haptic. */
    data class NewTileDiscovered(val tileX: Int, val tileY: Int) : MapEffect

    /** Centra el mapa en la posición del usuario. */
    data object CenterMapOnUser : MapEffect
}
