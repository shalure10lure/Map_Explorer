package com.ucb.mapexplorer.map.presentation.screen

import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.ucb.designsystem.theme.AppTheme
import org.osmdroid.bonuspack.routing.OSRMRoadManager
import org.osmdroid.bonuspack.routing.Road
import org.osmdroid.bonuspack.routing.RoadManager
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.*

@Composable
actual  fun GuideMapScreen(
    userLat: Double,
    userLon: Double,
    destLat: Double,
    destLon: Double,
    placeName: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var routeInfo by remember { mutableStateOf<RouteInfo?>(null) }
    var isLoadingRoute by remember { mutableStateOf(true) }

    val mapView = remember {
        MapView(context).apply {
            Configuration.getInstance().userAgentValue = context.packageName
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(16.0)
        }
    }

    // Calcular ruta con OSRM
    LaunchedEffect(Unit) {
        try {
            val road = withContext(Dispatchers.IO) {
                val roadManager = OSRMRoadManager(context, context.packageName)
                roadManager.setMean(OSRMRoadManager.MEAN_BY_FOOT)
                val waypoints = ArrayList<GeoPoint>().apply {
                    add(GeoPoint(userLat, userLon))
                    add(GeoPoint(destLat, destLon))
                }
                roadManager.getRoad(waypoints)
            }

            if (road.mStatus == Road.STATUS_OK) {
                // Dibujar ruta en el mapa
                val roadOverlay = RoadManager.buildRoadOverlay(road)
                roadOverlay.outlinePaint.apply {
                    color = android.graphics.Color.parseColor("#E53935")
                    strokeWidth = 14f
                    strokeCap = Paint.Cap.ROUND
                    strokeJoin = Paint.Join.ROUND
                    isAntiAlias = true
                }
                mapView.overlays.clear()
                mapView.overlays.add(roadOverlay)

                // Marcador origen (usuario)
                val originMarker = Marker(mapView).apply {
                    position = GeoPoint(userLat, userLon)
                    title = "Tu posición"
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
                }
                mapView.overlays.add(originMarker)

                // Marcador destino
                val destMarker = Marker(mapView).apply {
                    position = GeoPoint(destLat, destLon)
                    title = placeName
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                }
                mapView.overlays.add(destMarker)

                // Centrar mapa en medio de la ruta
                val midLat = (userLat + destLat) / 2
                val midLon = (userLon + destLon) / 2
                mapView.controller.animateTo(GeoPoint(midLat, midLon))

                // Ajustar zoom para ver toda la ruta
                val dist = haversineMeters(userLat, userLon, destLat, destLon)
                val zoom = when {
                    dist < 300  -> 18.0
                    dist < 800  -> 17.0
                    dist < 2000 -> 16.0
                    dist < 5000 -> 15.0
                    else        -> 14.0
                }
                mapView.controller.setZoom(zoom)
                mapView.invalidate()

                routeInfo = RouteInfo(
                    distanceKm   = road.mLength,
                    durationMin  = (road.mDuration / 60).toInt()
                )
            }
        } catch (e: Exception) {
            // Fallback: línea recta si OSRM falla
            val line = Polyline().apply {
                addPoint(GeoPoint(userLat, userLon))
                addPoint(GeoPoint(destLat, destLon))
                outlinePaint.apply {
                    color = android.graphics.Color.parseColor("#E53935")
                    strokeWidth = 10f
                    isAntiAlias = true
                }
            }
            mapView.overlays.add(line)
            val dist = haversineMeters(userLat, userLon, destLat, destLon)
            routeInfo = RouteInfo(
                distanceKm  = dist / 1000.0,
                durationMin = (dist / 80).toInt() // ~80m/min caminando
            )
            mapView.controller.animateTo(GeoPoint((userLat+destLat)/2, (userLon+destLon)/2))
            mapView.controller.setZoom(16.0)
            mapView.invalidate()
        } finally {
            isLoadingRoute = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { mapView }, modifier = Modifier.fillMaxSize())

        // ── Header flotante ────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = AppTheme.colors.textPrimary,
                    modifier = Modifier.size(22.dp).clickable { onBack() }
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Guiándote a",
                        style = AppTheme.typography.bodySmall,
                        color = AppTheme.colors.textSecondary
                    )
                    Text(
                        text = placeName,
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
        }

        // ── Info de ruta flotante abajo ────────────────────────────────────
        if (isLoadingRoute) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            color = AppTheme.colors.primary,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            "Calculando ruta peatonal...",
                            style = AppTheme.typography.bodyMedium,
                            color = AppTheme.colors.textSecondary
                        )
                    }
                }
            }
        } else {
            routeInfo?.let { info ->
                Card(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
                    elevation = CardDefaults.cardElevation(12.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            RouteStatItem(
                                emoji = "🚶",
                                value = if (info.distanceKm < 1.0)
                                    "${(info.distanceKm * 1000).toInt()} m"
                                else
                                    "${"%.1f".format(info.distanceKm)} km",
                                label = "Distancia"
                            )
                            Divider(
                                modifier = Modifier
                                    .height(48.dp)
                                    .width(1.dp),
                                color = AppTheme.colors.border.copy(alpha = 0.4f)
                            )
                            RouteStatItem(
                                emoji = "⏱️",
                                value = "${info.durationMin} min",
                                label = "Caminando"
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppTheme.colors.primary
                            )
                        ) {
                            Text(
                                "Volver al lugar",
                                color = androidx.compose.ui.graphics.Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RouteStatItem(emoji: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = AppTheme.typography.headlineLarge.copy(fontSize = 20.sp),
            color = AppTheme.colors.textPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.textSecondary
        )
    }
}

private data class RouteInfo(val distanceKm: Double, val durationMin: Int)

private fun haversineMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371000.0
    val dLat = (lat2 - lat1) * PI / 180.0
    val dLon = (lon2 - lon1) * PI / 180.0
    val a = sin(dLat/2).pow(2) +
            cos(lat1 * PI/180.0) * cos(lat2 * PI/180.0) * sin(dLon/2).pow(2)
    return r * 2 * atan2(sqrt(a), sqrt(1-a))
}