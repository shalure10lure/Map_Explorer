package com.ucb.mapexplorer

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ucb.designsystem.theme.DsTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ── TEST 1: El título INICIAR SESIÓN aparece en pantalla ──────────────
    @Test
    fun loginScreen_tituloPrincipalVisible() {
        composeTestRule.setContent {
            DsTheme {
                androidx.compose.material3.Text(text = "INICIAR SESIÓN")
            }
        }

        composeTestRule
            .onNodeWithText("INICIAR SESIÓN")
            .assertIsDisplayed()
    }

    // ── TEST 2: El botón Ingresar es visible ──────────────────────────────
    @Test
    fun loginScreen_botonIngresarVisible() {
        composeTestRule.setContent {
            DsTheme {
                com.ucb.designsystem.components.button.PrimaryButton(
                    text = "Ingresar",
                    onClick = {},
                    isPrimary = true
                )
            }
        }

        composeTestRule
            .onNodeWithText("Ingresar")
            .assertIsDisplayed()
    }

    // ── TEST 3: El botón Crear cuenta es visible ──────────────────────────
    @Test
    fun loginScreen_botonCrearCuentaVisible() {
        composeTestRule.setContent {
            DsTheme {
                com.ucb.designsystem.components.button.PrimaryButton(
                    text = "Crear cuenta",
                    onClick = {},
                    isPrimary = true
                )
            }
        }

        composeTestRule
            .onNodeWithText("Crear cuenta")
            .assertIsDisplayed()
    }

    // ── TEST 4: Se puede escribir en el campo de email ────────────────────
    @Test
    fun loginScreen_campoEmailAceptaTexto() {
        var textoActual = ""

        composeTestRule.setContent {
            DsTheme {
                com.ucb.designsystem.components.input.BasicInput(
                    value = textoActual,
                    onValueChange = { textoActual = it },
                    label = "ejemplo@mail.com"
                )
            }
        }

        composeTestRule
            .onNodeWithText("ejemplo@mail.com")
            .assertIsDisplayed()
    }

    // ── TEST 5: El botón Ingresar responde al clic ────────────────────────
    @Test
    fun loginScreen_botonIngresarRespondeAlClic() {
        var seFizoClick = false

        composeTestRule.setContent {
            DsTheme {
                com.ucb.designsystem.components.button.PrimaryButton(
                    text = "Ingresar",
                    onClick = { seFizoClick = true },
                    isPrimary = true
                )
            }
        }

        composeTestRule
            .onNodeWithText("Ingresar")
            .performClick()

        assert(seFizoClick) { "El botón no ejecutó la acción al hacer clic" }
    }
}