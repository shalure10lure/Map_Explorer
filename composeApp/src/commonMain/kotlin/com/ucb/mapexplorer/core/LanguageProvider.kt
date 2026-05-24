package com.ucb.mapexplorer.core

import androidx.compose.runtime.*

enum class AppLanguage(val code: String) {
    SPANISH("es"),
    ENGLISH("en");

    companion object {
        fun fromCode(code: String): AppLanguage {
            val normalized = code.lowercase().trim()
            return when {
                normalized.startsWith("en") -> ENGLISH
                normalized.startsWith("es") -> SPANISH
                else -> SPANISH
            }
        }
    }
}

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.SPANISH }
val LocalLanguageController = staticCompositionLocalOf<((AppLanguage) -> Unit)> { {} }

expect object LocalAppLocale {
    @Composable
    fun provides(languageCode: String): ProvidedValue<*>
}
