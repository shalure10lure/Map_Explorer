package com.ucb.mapexplorer.dangerzone.data.repository

import com.ucb.mapexplorer.dangerzone.data.dao.ZonaPeligrosaDao
import com.ucb.mapexplorer.dangerzone.data.datasource.DangerZoneRemoteDataSource
import com.ucb.mapexplorer.dangerzone.data.entity.ZonaPeligrosaEntity
import com.ucb.mapexplorer.dangerzone.domain.model.ZonaPeligrosaModel
import com.ucb.mapexplorer.dangerzone.domain.model.toNivelPeligro
import com.ucb.mapexplorer.dangerzone.domain.repository.DangerZoneRepository
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.time.Clock


class DangerZoneRepositoryImpl(
    private val dao: ZonaPeligrosaDao,
    private val remote: DangerZoneRemoteDataSource
) : DangerZoneRepository {

    override suspend fun syncZonas() {
        try {
            val zonas = remote.getZonasActivas()
            val entities = zonas.map { z ->
                ZonaPeligrosaEntity(
                    zonaId = z.zonaId,
                    nombre = z.nombre,
                    descripcion = z.descripcion,
                    latitud = z.latitud,
                    longitud = z.longitud,
                    radio = z.radio,
                    nivel = z.nivel.name.lowercase(),
                    tipo = z.tipo,
                    activa = z.activa,
                    actualizadaEn = Clock.System.now().toEpochMilliseconds()
                )
            }
            dao.clearAll()
            dao.insertAll(entities)
        } catch (e: Exception) {
            println("❌ DangerZone sync failed: ${e.message}")
        }
    }

    override suspend fun getZonasActivas(): List<ZonaPeligrosaModel> =
        dao.getZonasActivas().map { it.toModel() }

    override suspend fun getZonasCerca(
        lat: Double, lon: Double, radioMetros: Double
    ): List<ZonaPeligrosaModel> {
        val delta = radioMetros / 111_000.0
        val zonas = dao.getZonasCerca(
            minLat = lat - delta, maxLat = lat + delta,
            minLon = lon - delta, maxLon = lon + delta
        )
        return zonas
            .map { it.toModel() }
            .filter { haversine(lat, lon, it.latitud, it.longitud) <= (it.radio + radioMetros) }
    }

    private fun ZonaPeligrosaEntity.toModel() = ZonaPeligrosaModel(
        zonaId      = zonaId,
        nombre      = nombre,
        descripcion = descripcion,
        latitud     = latitud,
        longitud    = longitud,
        radio       = radio,
        nivel       = nivel.toNivelPeligro(),
        tipo        = tipo,
        activa      = activa
    )

    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r    = 6371000.0
        val dLat = (lat2 - lat1) * PI / 180.0
        val dLon = (lon2 - lon1) * PI / 180.0
        val a    = sin(dLat / 2).pow(2) +
                cos(lat1 * PI / 180.0) * cos(lat2 * PI / 180.0) * sin(dLon / 2).pow(2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}
