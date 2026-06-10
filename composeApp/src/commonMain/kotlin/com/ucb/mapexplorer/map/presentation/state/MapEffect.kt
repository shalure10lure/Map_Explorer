package com.ucb.mapexplorer.map.presentation.state

import com.ucb.mapexplorer.dangerzone.domain.model.ZonaPeligrosaModel

sealed interface MapEffect {
    data class ShowError(val message: String) : MapEffect
    data class ShowSnackbar(val message: String) : MapEffect

    /** Nuevo tile descubierto — la UI puede mostrar animación/haptic. */
    data class NewTileDiscovered(val tileX: Int, val tileY: Int) : MapEffect

    /** Centra el mapa en la posición del usuario. */
    data object CenterMapOnUser : MapEffect

    data class CenterMapOnLocation(val lat: Double, val lon: Double) : MapEffect

    /** Dispara una alerta de zona peligrosa con vibración y notificación. */
    data class DangerZoneAlertTriggered(val zona: ZonaPeligrosaModel) : MapEffect
}
