package com.ucb.mapexplorer.map.presentation.state

import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel

sealed interface MapEvent {
    data object OnLoadMap : MapEvent
    data class OnLocationUpdated(val latitude: Double, val longitude: Double) : MapEvent
    data object OnDismissError : MapEvent
    data object OnCenterOnUser : MapEvent
    data class OnAvatarUpdated(val config: AvatarConfigModel) : MapEvent
    data object OnDismissDangerAlert: MapEvent
}
