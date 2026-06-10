package com.ucb.mapexplorer.dangerzone.domain.repository

import com.ucb.mapexplorer.dangerzone.domain.model.ZonaPeligrosaModel

interface DangerZoneRepository {
    suspend fun getZonasActivas(): List<ZonaPeligrosaModel>
    suspend fun getZonasCerca(lat: Double, lon: Double, radioMetros: Double = 300.0): List<ZonaPeligrosaModel>
    suspend fun syncZonas()
}
