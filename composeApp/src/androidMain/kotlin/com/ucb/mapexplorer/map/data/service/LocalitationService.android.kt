package com.ucb.mapexplorer.map.data.service

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.*
import com.ucb.mapexplorer.map.domain.model.UserLocationModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

actual class LocalitationService actual constructor() {

    // El contexto se inyecta via Koin (ver MapModule.kt)
    private lateinit var context: Context

    fun init(ctx: Context) {
        context = ctx
    }

    @SuppressLint("MissingPermission")
    actual fun observeLocation(): Flow<UserLocationModel> = callbackFlow {
        val client = LocationServices.getFusedLocationProviderClient(context)

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2_000L          // intervalo: 2 segundos
        )
            .setMinUpdateDistanceMeters(5f)   // solo emitir si movió ≥5m
            .setMaxUpdateDelayMillis(5_000L)
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

        client.requestLocationUpdates(request, callback, null)

        awaitClose {
            client.removeLocationUpdates(callback)
        }
    }
}
