package com.ucb.mapexplorer.profile

import com.ucb.mapexplorer.profile.domain.model.AvatarAccessory
import com.ucb.mapexplorer.profile.domain.model.AvatarBody
import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel
import com.ucb.mapexplorer.profile.domain.model.AvatarHat
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AvatarConfigModelTest {

    // ── TEST 1: toId genera el ID correcto
    @Test
    fun `toId genera string con formato correcto`() {
        val config = AvatarConfigModel(
            body      = AvatarBody.GATO,
            hat       = AvatarHat.BANANA,
            accessory = AvatarAccessory.BOOBA
        )
        val id = config.toId()
        assertEquals("GATO|BANANA|BOOBA", id)
    }

    // ── TEST 2: fromId reconstruye el modelo correctamente
    @Test
    fun `fromId reconstruye el mismo modelo`() {
        val original = AvatarConfigModel(
            body      = AvatarBody.PATO,
            hat       = AvatarHat.HOJA,
            accessory = AvatarAccessory.CAKE
        )
        val id          = original.toId()
        val reconstructed = AvatarConfigModel.fromId(id)

        assertEquals(original.body, reconstructed.body)
        assertEquals(original.hat, reconstructed.hat)
        assertEquals(original.accessory, reconstructed.accessory)
    }

    // ── TEST 3: fromId con NONE funciona
    @Test
    fun `fromId con NONE en sombrero y accesorio funciona`() {
        val config = AvatarConfigModel(
            body      = AvatarBody.GALLINA,
            hat       = AvatarHat.NONE,
            accessory = AvatarAccessory.NONE
        )
        val reconstructed = AvatarConfigModel.fromId(config.toId())
        assertEquals(AvatarHat.NONE, reconstructed.hat)
        assertEquals(AvatarAccessory.NONE, reconstructed.accessory)
    }

    // ── TEST 4: fromId con ID inválido usa defaults
    @Test
    fun `fromId con ID inválido retorna defaults`() {
        val config = AvatarConfigModel.fromId("INVALIDO|INVALIDO|INVALIDO")
        // Debe usar los defaults del companion object
        assertNotNull(config)
        assertEquals(AvatarBody.GATO, config.body)
    }

    // ── TEST 5: El modelo por defecto es GATO sin nada
    @Test
    fun `modelo por defecto tiene valores correctos`() {
        val config = AvatarConfigModel()
        assertEquals(AvatarBody.GATO, config.body)
        assertEquals(AvatarHat.NONE, config.hat)
        assertEquals(AvatarAccessory.NONE, config.accessory)
    }
}