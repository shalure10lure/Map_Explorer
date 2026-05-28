package com.ucb.mapexplorer

import android.content.Context
import android.os.Build
import java.util.Locale

object AppContext {
    lateinit var value: Context
}

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun getSystemLanguageCode(): String = Locale.getDefault().language

actual fun saveLanguageSetting(code: String) {
    val prefs = AppContext.value.getSharedPreferences("settings", Context.MODE_PRIVATE)
    prefs.edit().putString("language_code", code).apply()
}

actual fun getLanguageSetting(): String? {
    val prefs = AppContext.value.getSharedPreferences("settings", Context.MODE_PRIVATE)
    return prefs.getString("language_code", null)
}

actual fun saveThemeSetting(isDark: Boolean) {
    val prefs = AppContext.value.getSharedPreferences("settings", Context.MODE_PRIVATE)
    prefs.edit().putBoolean("is_dark_theme", isDark).apply()
}

actual fun getThemeSetting(): Boolean? {
    val prefs = AppContext.value.getSharedPreferences("settings", Context.MODE_PRIVATE)
    return if (prefs.contains("is_dark_theme")) {
        prefs.getBoolean("is_dark_theme", false)
    } else null
}
