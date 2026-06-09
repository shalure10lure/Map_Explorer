package com.ucb.mapexplorer.dangerzone.domain.usecase

import com.ucb.mapexplorer.dangerzone.domain.model.ZonaPeligrosaModel
import com.ucb.mapexplorer.dangerzone.domain.repository.DangerZoneRepository

class CheckDangerZoneUseCase(private val repository: DangerZoneRepository) {
    /**
     * Retorna la zona más peligrosa en la que está el usuario, o null si está seguro.
     * @param radioUsuario metros extra de margen para detectar la zona antes de entrar.
     */
    suspend operator fun invoke(
        lat: Double, lon: Double, radioUsuario: Double = 10.0
    ): ZonaPeligrosaModel? {
        return repository.getZonasCerca(lat, lon, radioUsuario)
            .maxByOrNull { it.nivel.ordinal }
    }
}