package com.ucb.mapexplorer.onboarding.utils


import java.util.Locale

actual fun getDeviceLanguage(): String {
    val lang = Locale.getDefault().language
    return when (lang) {
        "es" -> "es"
        "fr" -> "fr"
        else -> "en"
    }
}