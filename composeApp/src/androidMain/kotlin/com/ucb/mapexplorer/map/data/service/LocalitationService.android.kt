package com.ucb.mapexplorer.map.data.service

import com.ucb.mapexplorer.map.domain.model.UserLocationModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


actual class LocalitationService actual constructor() {

    actual fun observeLocation(): Flow<UserLocationModel> = flow {

        // aquí luego conectar GPS Android real
        emit(
            UserLocationModel(
                latitude = 0.0,
                longitude = 0.0
            )
        )
    }
}