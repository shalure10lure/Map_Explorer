package com.ucb.mapexplorer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
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
        prefs.edit().remove("session_uid").remove("session_expires_at").apply()
        return null
    }
    return prefs.getString("session_uid", null)
}

actual fun clearSessionUid() {
    val prefs = AppContext.value.getSharedPreferences("settings", Context.MODE_PRIVATE)
    prefs.edit().remove("session_uid").remove("session_expires_at").apply()
}

actual fun triggerDangerNotification(title: String, message: String) {
    val context = AppContext.value
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channelId = "danger_alerts_channel"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Alertas de Peligro",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones para zonas peligrosas cercanas"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 200, 500)
        }
        notificationManager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, channelId)
        .setContentTitle(title)
        .setContentText(message)
        .setSmallIcon(android.R.drawable.ic_dialog_alert)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(System.currentTimeMillis().toInt(), notification)

    // Vibrar explícitamente también por si la app está en primer plano
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        vibratorManager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 200, 500), -1))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(longArrayOf(0, 500, 200, 500), -1)
    }
}
