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
// Al final del archivo, después de getThemeSetting()
actual fun saveSessionUid(uid: String) {
    val prefs = AppContext.value.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val expiresAt = System.currentTimeMillis() + (2 * 24 * 60 * 60 * 1000L) // 2 días en ms
    prefs.edit()
        .putString("session_uid", uid)
        .putLong("session_expires_at", expiresAt)
        .apply()
}

actual fun getSessionUid(): String? {
    val prefs = AppContext.value.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val expiresAt = prefs.getLong("session_expires_at", 0L)
    if (System.currentTimeMillis() > expiresAt) {
        // Sesión expirada — limpiar
        prefs.edit().remove("session_uid").remove("session_expires_at").apply()
        return null
    }
    return prefs.getString("session_uid", null)
}

actual fun clearSessionUid() {
    val prefs = AppContext.value.getSharedPreferences("settings", Context.MODE_PRIVATE)
    prefs.edit().remove("session_uid").remove("session_expires_at").apply()
}
