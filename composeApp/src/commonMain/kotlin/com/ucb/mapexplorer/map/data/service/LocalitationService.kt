package com.ucb.mapexplorer.map.data.service

import com.ucb.mapexplorer.map.domain.model.UserLocationModel
import kotlinx.coroutines.flow.Flow


expect class LocalitationService() {

    fun observeLocation(): Flow<UserLocationModel>
}