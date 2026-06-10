package com.ucb.mapexplorer.map.data.service

import com.ucb.mapexplorer.map.domain.model.UserLocationModel
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreLocation.*
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
actual class LocalitationService actual constructor() {

    actual fun observeLocation(): Flow<UserLocationModel> = callbackFlow {

        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {

            override fun locationManager(
                manager: CLLocationManager,
                didUpdateLocations: List<*>
            ) {
                val location = didUpdateLocations.lastOrNull() as? CLLocation ?: return

                // useContents requiere @OptIn(ExperimentalForeignApi::class)
                val lat = location.coordinate.useContents { latitude }
                val lon = location.coordinate.useContents { longitude }

                trySend(
                    UserLocationModel(
                        latitude  = lat,
                        longitude = lon,
                        accuracy  = location.horizontalAccuracy.toFloat(),
                        speed     = location.speed.toFloat().coerceAtLeast(0f),
                        bearing   = location.course.toFloat()
                    )
                )
            }

            override fun locationManager(
                manager: CLLocationManager,
                didFailWithError: platform.Foundation.NSError
            ) {
                println("iOS CoreLocation error: ${didFailWithError.localizedDescription}")
            }
        }

        val locationManager = CLLocationManager().apply {
            this.delegate = delegate
            desiredAccuracy = kCLLocationAccuracyBest
            distanceFilter  = 10.0
            requestWhenInUseAuthorization()
            startUpdatingLocation()
        }

        awaitClose {
            locationManager.stopUpdatingLocation()
            locationManager.delegate = null
        }
    }
}