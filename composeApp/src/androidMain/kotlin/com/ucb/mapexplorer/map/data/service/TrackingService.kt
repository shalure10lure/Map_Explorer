package com.ucb.mapexplorer.map.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.ucb.mapexplorer.core.session.Session
import com.ucb.mapexplorer.core.utils.ConnectivityObserver
import com.ucb.mapexplorer.map.domain.usecase.GetCurrentLocationUseCase
import com.ucb.mapexplorer.map.domain.usecase.UnlockTileUseCase
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.koin.android.ext.android.inject

class TrackingService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    private val getCurrentLocationUseCase: GetCurrentLocationUseCase by inject()
    private val unlockTileUseCase: UnlockTileUseCase by inject()
    private val connectivityObserver: ConnectivityObserver by inject()

    private var trackingJob: Job? = null
    private var connectivityJob: Job? = null
    private var isNetworkAvailable = false

    private val channelId = "tracking_channel"
    private val notificationId = 1
    private val stopNotificationId = 2

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Importante: startForeground debe llamarse lo antes posible dentro de onStartCommand
        startForeground(notificationId, createNotification("Iniciando servicio de exploración..."))

        when (intent?.action) {
            ACTION_START -> {
                vibratePhone()
                observeConnectivity()
            }
            ACTION_STOP -> {
                stopService()
            }
        }
        return START_STICKY
    }

    private fun stopService() {
        sendStopNotification()
        stopForeground(true)
        stopSelf()
    }

    private fun observeConnectivity() {
        if (connectivityJob?.isActive == true) return
        
        connectivityJob = connectivityObserver.observe()
            .onEach { status ->
                isNetworkAvailable = status == ConnectivityObserver.Status.Available
                if (isNetworkAvailable) {
                    startTracking()
                    updateNotification("Rastreo activo - Explorando el mundo")
                } else {
                    stopTracking()
                    updateNotification("Pausado: Esperando conexión a internet")
                }
            }
            .catch { e ->
                updateNotification("Error de red: ${e.message}")
            }
            .launchIn(serviceScope)
    }

    private fun startTracking() {
        if (trackingJob?.isActive == true) return

        trackingJob = serviceScope.launch {
            try {
                getCurrentLocationUseCase()
                    .collect { location ->
                        val uid = Session.uid
                        if (!uid.isNullOrEmpty() && isNetworkAvailable) {
                            unlockTileUseCase(uid, location)
                        }
                    }
            } catch (e: Exception) {
                println("TrackingService: Error en flujo de ubicación: ${e.message}")
                // Intentar reiniciar tras un breve delay si falla
                delay(5000)
                trackingJob = null
                startTracking()
            }
        }
    }

    private fun stopTracking() {
        trackingJob?.cancel()
        trackingJob = null
    }

    private fun createNotification(content: String) = NotificationCompat.Builder(this, channelId)
        .setContentTitle("Map Explorer")
        .setContentText(content)
        .setSmallIcon(android.R.drawable.ic_menu_mylocation)
        .setOngoing(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .build()

    private fun updateNotification(content: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, createNotification(content))
    }

    private fun sendStopNotification() {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val stopNotification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Map Explorer")
            .setContentText("El rastreo de tu recorrido ha finalizado.")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        vibratePhone()
        notificationManager.notify(stopNotificationId, stopNotification)
    }

    private fun vibratePhone() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Seguimiento de Ruta",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Mantiene el rastreo de tiles activos en segundo plano"
                enableVibration(true)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopService()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}
