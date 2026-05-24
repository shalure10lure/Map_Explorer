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
