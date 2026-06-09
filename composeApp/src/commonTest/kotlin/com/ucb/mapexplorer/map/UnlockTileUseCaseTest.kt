package com.ucb.mapexplorer.map

import com.ucb.mapexplorer.core.utils.TileUtils
import com.ucb.mapexplorer.map.domain.model.TileModel
import com.ucb.mapexplorer.map.domain.model.UserLocationModel
import com.ucb.mapexplorer.map.domain.repository.MapRepository
import com.ucb.mapexplorer.map.domain.usecase.UnlockTileUseCase
import io.mockative.Mock
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class UnlockTileUseCaseTest {

    @Mock
    val mapRepository = mock(classOf<MapRepository>())

    private val useCase = UnlockTileUseCase(mapRepository)

    // ── TEST 1: Convierte coordenadas al tile correcto ────────────────────
    @Test
    fun `latLng se convierte al tile correcto con zoom 20`() {
        val lat = -17.3936
        val lon = -66.1570
        val (tileX, tileY) = TileUtils.latLngToTile(lat, lon)
        // Con zoom 20, el tile de Cochabamba debe ser > 300000
        assert(tileX > 300_000)
        assert(tileY > 300_000)
    }

    // ── TEST 2: El UseCase guarda el tile cuando la ubicación cambia ──────
    @Test
    fun `unlock tile llama al repositorio con el tile correcto`() = runTest {
        val uid = "usuario_test"
        val location = UserLocationModel(
            latitude  = -17.3936,
            longitude = -66.1570,
            accuracy  = 5f,
            speed     = 0f,
            bearing   = 0f
        )

        val (expectedX, expectedY) = TileUtils.latLngToTile(
            location.latitude, location.longitude
        )

        coEvery { mapRepository.saveTile(uid, any()) }.returns(Unit)

        useCase(uid, location)

        // Verifica que se llamó al repositorio con el tile correcto
        coVerify { mapRepository.saveTile(uid, any()) }
            .wasInvoked(exactly = 1)
    }
}