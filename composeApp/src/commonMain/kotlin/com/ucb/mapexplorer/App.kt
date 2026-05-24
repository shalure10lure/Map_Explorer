package com.ucb.mapexplorer

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ucb.designsystem.theme.AppTheme
import com.ucb.designsystem.theme.DsTheme
import com.ucb.designsystem.theme.ThemeMode
import com.ucb.mapexplorer.core.*
import com.ucb.mapexplorer.navigation.AppNavHost

@Composable
@Preview
fun App() {
    // 1. Cargamos el idioma guardado O el del sistema
    var language by remember { 
        val savedCode = getLanguageSetting()
        val initialLanguage = if (savedCode != null) {
            AppLanguage.fromCode(savedCode)
        } else {
            AppLanguage.fromCode(getSystemLanguageCode())
        }
        mutableStateOf(initialLanguage) 
    }

    // 2. Cargamos el tema guardado O el del sistema
    val systemIsDark = isSystemInDarkTheme()
    var themeMode by remember {
        val savedTheme = getThemeSetting()
        val initialTheme = if (savedTheme != null) {
            if (savedTheme) ThemeMode.DARK else ThemeMode.LIGHT
        } else {
            if (systemIsDark) ThemeMode.DARK else ThemeMode.LIGHT
        }
        mutableStateOf(initialTheme)
    }

    // 3. Forzamos el Locale en la plataforma
    val localeProvidedValue = LocalAppLocale.provides(language.code)

    CompositionLocalProvider(
        LocalAppLanguage provides language,
        LocalLanguageController provides { newLanguage -> 
            language = newLanguage 
            saveLanguageSetting(newLanguage.code)
        },
        LocalThemeMode provides themeMode,
        LocalThemeController provides { newTheme ->
            themeMode = newTheme
            saveThemeSetting(newTheme == ThemeMode.DARK)
        },
        localeProvidedValue
    ) {
        key(language, themeMode) {
            DsTheme(mode = themeMode) {
                // Box raíz para asegurar que el fondo del tema se aplique en toda la aplicación
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(AppTheme.colors.background)
                ) {
                    AppNavHost()
                }
            }
        }
    }
}
