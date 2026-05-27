package com.ucb.mapexplorer.map.data.service

import android.annotation.SuppressLint
import android.content.Context
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.ucb.mapexplorer.map.domain.model.UserLocationModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual class LocalitationService actual constructor() : KoinComponent {

    private val context: Context by inject()

    @SuppressLint("MissingPermission")
    actual fun observeLocation(): Flow<UserLocationModel> = callbackFlow {
        val client = LocationServices.getFusedLocationProviderClient(context)

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5_000L          // intervalo: 5 segundos
        )
            .setMinUpdateDistanceMeters(10f)   // solo emitir si movió ≥10m
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    trySend(
                        UserLocationModel(
                            latitude  = loc.latitude,
                            longitude = loc.longitude,
                            accuracy  = loc.accuracy,
                            speed     = loc.speed,
                            bearing   = loc.bearing
                        )
                    )
                }
            }
        }

        // Obtener el Executor del hilo principal para procesar los callbacks de ubicación.
        // Esto soluciona el error "invalid null looper" al ser llamado desde Coroutines.
        val executor = ContextCompat.getMainExecutor(context)
        
        try {
            client.requestLocationUpdates(request, executor, callback)
                .addOnFailureListener { e ->
                    close(e)
                }
        } catch (e: Exception) {
            close(e)
        }

        awaitClose {
            client.removeLocationUpdates(callback)
        }
    }
}
