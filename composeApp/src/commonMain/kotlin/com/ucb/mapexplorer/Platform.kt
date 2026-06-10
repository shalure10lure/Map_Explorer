package com.ucb.mapexplorer

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun getSystemLanguageCode(): String

expect fun saveLanguageSetting(code: String)
expect fun getLanguageSetting(): String?

expect fun saveThemeSetting(isDark: Boolean)
expect fun getThemeSetting(): Boolean?
expect fun saveSessionUid(uid: String)
expect fun getSessionUid(): String?
expect fun clearSessionUid()

/**
 * Dispara una notificación local y vibración para alertas de peligro.
 */
expect fun triggerDangerNotification(title: String, message: String)

/**
 * Dispara una notificación local para eventos de amistad.
 */
expect fun triggerFriendNotification(title: String, message: String)
