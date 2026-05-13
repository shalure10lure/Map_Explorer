package com.ucb.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class AppColors(

    val primary: Color,
    val primaryVariant: Color,
    val secondary: Color,

    val background: Color,
    val surface: Color,
    val botcolor:Color,

    val textPrimary: Color,
    val textSecondary: Color,

    val border: Color,
    val error: Color,
    val success: Color,

    val isLight: Boolean

)
val LightPalette = AppColors(
    // ROJO VIBRANTE (El de los botones "SIGUIENTE" y "COMENZAR")
    primary = Color(0xFFD32F2F),
    // ROJO OSCURO (Para estados presionados o variantes)
    primaryVariant = Color(0xFFB71C1C),
    // GRIS OSCURO (El color de los botones "Sign In" y "Crear cuenta")
    secondary = Color(0xFF2C2C2C),

    // FONDOS (Limpios como en tu Figma)
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFFFFFF),
    botcolor = Color(0xFF2C2C2C),

    // TEXTOS
    textPrimary = Color(0xFF212121),   // Casi negro para lectura clara
    textSecondary = Color(0xFF757575), // Gris para "Forgot password?"

    // BORDES (Para los inputs de Email/Password)
    border = Color(0xFFE0E0E0),

    // ESTADOS
    error = Color(0xFFD32F2F),
    success = Color(0xFF388E3C),
    isLight = true
)

val DarkPalette = AppColors(
    primary = Color(0xFFFF5252),      // Rojo más brillante para que resalte en oscuro
    primaryVariant = Color(0xFFD32F2F),
    secondary = Color(0xFFE0E0E0),    // Los botones secundarios pasan a ser claros

    background = Color(0xFF121212),   // Fondo oscuro profundo
    surface = Color(0xFF1E1E1E),      // Superficies un poco más claras
    botcolor = Color(0xFFE0E0E0),

    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFBDBDBD),

    border = Color(0xFF333333),
    error = Color(0xFFCF6679),
    success = Color(0xFF81C784),
    isLight = false
)

val HighContrastPalette = AppColors(
    // AMARILLO PURO (Máxima visibilidad)
    primary = Color(0xFFFFFF00),
    primaryVariant = Color(0xFFFFEB3B),
    // BLANCO para elementos secundarios
    secondary = Color(0xFFFFFFFF),

    // FONDO NEGRO TOTAL
    background = Color(0xFF000000),
    surface = Color(0xFF000000),
    botcolor = Color(0xFFFFFF00),

    // TEXTOS (Blanco puro sobre Negro)
    textPrimary = Color(0xFFFFFFFF),
    textSecondary = Color(0xFFFFFF00), // Amarillo para destacar

    // BORDES MUY MARCADOS
    border = Color(0xFFFFFFFF),

    // ESTADOS
    error = Color(0xFFFF1744),
    success = Color(0xFF00E676),
    isLight = false
)

