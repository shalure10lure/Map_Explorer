package com.ucb.mapexplorer.onboarding.utils

import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual fun getDeviceLanguage(): String {
    val lang = NSLocale.currentLocale.languageCode ?: "es"
    return when {
        lang.startsWith("es") -> "es"
        lang.startsWith("fr") -> "fr"
        else -> "en"
    }
}
