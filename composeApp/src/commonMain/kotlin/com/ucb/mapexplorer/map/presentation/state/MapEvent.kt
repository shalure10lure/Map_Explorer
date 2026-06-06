package com.ucb.mapexplorer.map.presentation.state

import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel

sealed interface MapEvent {
    /** Carga inicial: tiles guardados + arranca GPS. */
    data object OnLoadMap : MapEvent

    /** Actualización de GPS desde la capa de UI. */
    data class OnLocationUpdated(val latitude: Double, val longitude: Double) : MapEvent

    /** Cierra el diálogo/snackbar de error. */
    data object OnDismissError : MapEvent

    /** El usuario pide centrar el mapa en su posición. */
    data object OnCenterOnUser : MapEvent

    /** Actualización del avatar desde el perfil. */
    data class OnAvatarUpdated(val config: AvatarConfigModel) : MapEvent
}
