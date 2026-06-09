package com.ucb.mapexplorer.map.presentation.screen

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.UIKitView
import com.ucb.designsystem.theme.AppTheme
import kotlinx.cinterop.ExperimentalForeignApi
import platform.CoreLocation.*
import platform.MapKit.*
import kotlin.math.*

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun GuideMapScreen(
    userLat: Double,
    userLon: Double,
    destLat: Double,
    destLon: Double,
    placeName: String,
    onBack: () -> Unit
) {
    val distanceMeters = remember {
        haversineMetersIos(userLat, userLon, destLat, destLon)
    }
    val durationMin = remember { (distanceMeters / 80).toInt().coerceAtLeast(1) }

    // Formatear distancia sin usar String.format (no disponible en KN)
    val distanceText = remember(distanceMeters) {
        if (distanceMeters < 1000) {
            "${distanceMeters.toInt()} m"
        } else {
            val km = distanceMeters / 1000.0
            // Redondear a 1 decimal manualmente
            val rounded = (km * 10).toLong().toDouble() / 10.0
            "$rounded km"
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        UIKitView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                MKMapView().apply {
                    val midLat = (userLat + destLat) / 2
                    val midLon = (userLon + destLon) / 2
                    val spanDegrees = maxOf(
                        abs(destLat - userLat) * 2.5,
                        abs(destLon - userLon) * 2.5,
                        0.005
                    )
                    val region = MKCoordinateRegionMake(
                        CLLocationCoordinate2DMake(midLat, midLon),
                        MKCoordinateSpanMake(spanDegrees, spanDegrees)
                    )
                    setRegion(region, animated = false)

                    // Marcador del usuario — usar setters estilo ObjC
                    val userAnnotation = MKPointAnnotation()
                    userAnnotation.setCoordinate(CLLocationCoordinate2DMake(userLat, userLon))
                    userAnnotation.setTitle("Tu posición")
                    addAnnotation(userAnnotation)

                    // Marcador del destino
                    val destAnnotation = MKPointAnnotation()
                    destAnnotation.setCoordinate(CLLocationCoordinate2DMake(destLat, destLon))
                    destAnnotation.setTitle(placeName)
                    addAnnotation(destAnnotation)

                    showsUserLocation = true
                }
            },
            update = { _ -> }
        )

        // Header flotante
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

        // Card de info abajo
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
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🚶", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = distanceText,
                            style = AppTheme.typography.headlineLarge.copy(fontSize = 20.sp),
                            color = AppTheme.colors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Distancia",
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colors.textSecondary
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier
                            .height(48.dp)
                            .width(1.dp),
                        color = AppTheme.colors.border.copy(alpha = 0.4f)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⏱️", fontSize = 24.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$durationMin min",
                            style = AppTheme.typography.headlineLarge.copy(fontSize = 20.sp),
                            color = AppTheme.colors.textPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Caminando",
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppTheme.colors.primary
                    )
                ) {
                    Text(
                        "Volver al lugar",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun haversineMetersIos(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r    = 6371000.0
    val dLat = (lat2 - lat1) * PI / 180.0
    val dLon = (lon2 - lon1) * PI / 180.0
    val a    = sin(dLat / 2).pow(2) +
            cos(lat1 * PI / 180.0) * cos(lat2 * PI / 180.0) * sin(dLon / 2).pow(2)
    return r * 2 * atan2(sqrt(a), sqrt(1 - a))
}