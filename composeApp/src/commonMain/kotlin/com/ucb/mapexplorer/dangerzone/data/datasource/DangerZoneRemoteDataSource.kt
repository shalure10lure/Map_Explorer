package com.ucb.mapexplorer.dangerzone.data.datasource

import com.ucb.mapexplorer.dangerzone.domain.model.ZonaPeligrosaModel

expect class DangerZoneRemoteDataSource() {
    suspend fun getZonasActivas(): List<ZonaPeligrosaModel>
}
