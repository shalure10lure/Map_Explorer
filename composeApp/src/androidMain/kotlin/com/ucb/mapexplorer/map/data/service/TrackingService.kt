package com.ucb.mapexplorer.map.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
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

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Importante: startForeground debe llamarse lo antes posible dentro de onStartCommand
        startForeground(1, createNotification("Iniciando servicio de exploración..."))

        when (intent?.action) {
            ACTION_START -> {
                observeConnectivity()
            }
            ACTION_STOP -> {
                stopService()
            }
        }
        return START_STICKY
    }

    private fun stopService() {
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

    private fun createNotification(content: String) = NotificationCompat.Builder(this, "tracking_channel")
        .setContentTitle("Map Explorer")
        .setContentText(content)
        .setSmallIcon(android.R.drawable.ic_menu_mylocation)
        .setOngoing(true)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .build()

    private fun updateNotification(content: String) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, createNotification(content))
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "tracking_channel",
                "Seguimiento de Ruta",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Mantiene el rastreo de tiles activos en segundo plano"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
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
