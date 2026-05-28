package com.ucb.mapexplorer.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

actual object LocalAppLocale {
    @Composable
    actual fun provides(languageCode: String): ProvidedValue<*> {
        val configuration = LocalConfiguration.current
        val context = LocalContext.current
        val locale = Locale(languageCode)
        
        Locale.setDefault(locale)
        configuration.setLocale(locale)
        
        // Actualizar recursos de Android para que stringResource funcione
        context.resources.updateConfiguration(configuration, context.resources.displayMetrics)
        
        return LocalConfiguration provides configuration
    }
}
