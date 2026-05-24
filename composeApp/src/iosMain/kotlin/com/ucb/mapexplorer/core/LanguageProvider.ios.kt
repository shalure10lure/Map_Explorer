package com.ucb.mapexplorer.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue

actual object LocalAppLocale {
    @Composable
    actual fun provides(languageCode: String): ProvidedValue<*> {
        // En iOS, el cambio dinámico de recursos de sistema es limitado sin reinicio,
        // pero proveemos el valor para mantener la consistencia del estado.
        return LocalAppLanguage provides AppLanguage.fromCode(languageCode)
    }
}
