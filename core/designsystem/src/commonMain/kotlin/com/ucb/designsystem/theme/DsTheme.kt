import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.ucb.designsystem.theme.AppColors
import com.ucb.designsystem.theme.DarkPalette
import com.ucb.designsystem.theme.HighContrastPalette
import com.ucb.designsystem.theme.LightPalette

enum class ThemeMode {
    LIGHT,
    DARK,
    HIGH_CONTRAST
}
val LocalColors = staticCompositionLocalOf { LightPalette }
internal val LocalTypography = staticCompositionLocalOf { DefaultTypography }
object AppTheme {
    val colors: AppColors @Composable get() = LocalColors.current
    val typography: Typography @Composable get() = LocalTypography.current
}


@Composable
fun DsTheme(
    mode: ThemeMode = if (isSystemInDarkTheme()) ThemeMode.DARK else ThemeMode.LIGHT, highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = when (mode) {
        ThemeMode.LIGHT -> LightPalette
        ThemeMode.DARK -> DarkPalette
        ThemeMode.HIGH_CONTRAST -> HighContrastPalette
    }
    CompositionLocalProvider(LocalColors provides colors,
        LocalTypography provides DefaultTypography) {
        content()
    }
}
