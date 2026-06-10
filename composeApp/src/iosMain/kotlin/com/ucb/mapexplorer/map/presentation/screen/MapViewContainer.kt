package com.ucb.mapexplorer.map.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.UIKitView
import androidx.navigation.NavController
import com.ucb.mapexplorer.map.presentation.state.MapUIState
import com.ucb.mapexplorer.navigation.NavRoute
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.*
import platform.MapKit.*
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun MapViewContainer(
    modifier: Modifier,
    state: MapUIState,
    navController: NavController,
    onLocationChanged: (Double, Double) -> Unit
) {
    val nearbyPlaces = state.nearbyPlacesInMap
    val userLat      = state.userLat
    val userLng      = state.userLng

    // Mantener referencia al mapa para actualizaciones imperativas
    var mapViewRef by remember { mutableStateOf<MKMapView?>(null) }

    // Location manager para GPS real en dispositivo
    val locationManager = remember { CLLocationManager() }

    val locationDelegate = remember {
        object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManager(
                manager: CLLocationManager,
                didUpdateLocations: List<*>
            ) {
                val loc = didUpdateLocations.lastOrNull() as? CLLocation ?: return
                // useContents requiere ExperimentalForeignApi
                val lat = loc.coordinate.useContents { latitude }
                val lon = loc.coordinate.useContents { longitude }
                onLocationChanged(lat, lon)
            }
        }
    }

    LaunchedEffect(Unit) {
        locationManager.delegate = locationDelegate
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.distanceFilter  = 10.0
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()
    }

    DisposableEffect(Unit) {
        onDispose {
            locationManager.stopUpdatingLocation()
            locationManager.delegate = null
        }
    }

    Box(modifier = modifier) {
        UIKitView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                MKMapView().apply {
                    showsUserLocation = true

                    // Centrar en La Paz como punto inicial
                    val defaultRegion = MKCoordinateRegionMakeWithDistance(
                        CLLocationCoordinate2DMake(-16.5, -68.15),
                        2000.0, 2000.0
                    )
                    setRegion(defaultRegion, animated = false)
                    mapViewRef = this
                }
            },
            update = { mapView ->
                // Centrar en usuario cuando llega GPS válido
                if (userLat != 0.0 && userLng != 0.0) {
                    val userCoord = CLLocationCoordinate2DMake(userLat, userLng)
                    val region = MKCoordinateRegionMakeWithDistance(userCoord, 500.0, 500.0)
                    mapView.setRegion(region, animated = true)

                    // Eliminar marcador de usuario previo
                    val toRemove = mapView.annotations.filterIsInstance<MKPointAnnotation>()
                        .filter { annotation ->
                            // En KN los properties de MKPointAnnotation son var accesibles
                            annotation.title() == "Tú"
                        }
                    if (toRemove.isNotEmpty()) {
                        mapView.removeAnnotations(toRemove)
                    }

                    // Agregar marcador del usuario
                    // En Kotlin/Native, MKPointAnnotation usa setters estilo ObjC
                    val userAnnotation = MKPointAnnotation()
                    userAnnotation.setCoordinate(userCoord)
                    userAnnotation.setTitle("Tú")
                    mapView.addAnnotation(userAnnotation)
                }

                // Eliminar marcadores de lugares previos (todos excepto "Tú")
                val placeAnnotations = mapView.annotations
                    .filterIsInstance<MKPointAnnotation>()
                    .filter { it.title() != "Tú" }
                if (placeAnnotations.isNotEmpty()) {
                    mapView.removeAnnotations(placeAnnotations)
                }

                // Agregar marcadores de lugares cercanos
                nearbyPlaces.forEach { lugar ->
                    val annotation = MKPointAnnotation()
                    annotation.setCoordinate(
                        CLLocationCoordinate2DMake(lugar.latitude, lugar.longitude)
                    )
                    annotation.setTitle(lugar.name)
                    annotation.setSubtitle(lugar.category)
                    mapView.addAnnotation(annotation)
                }
            }
        )

        // Indicador de carga mientras no hay GPS
        if (state.isLoadingLocation) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = androidx.compose.ui.graphics.Color(0xFFE53935)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Obteniendo tu ubicación...",
                        color = androidx.compose.ui.graphics.Color.White,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}