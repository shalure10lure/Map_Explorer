package com.ucb.mapexplorer.core

import androidx.compose.runtime.staticCompositionLocalOf
import com.ucb.designsystem.theme.ThemeMode

val LocalThemeController = staticCompositionLocalOf<((ThemeMode) -> Unit)> { {} }
val LocalThemeMode = staticCompositionLocalOf { ThemeMode.LIGHT }
