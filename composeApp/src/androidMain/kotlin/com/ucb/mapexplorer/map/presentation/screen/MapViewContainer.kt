package com.ucb.mapexplorer.map.presentation.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.*
import android.content.Context
import com.ucb.mapexplorer.core.utils.TileUtils
import com.ucb.mapexplorer.map.presentation.state.MapUIState
import com.ucb.mapexplorer.navigation.NavRoute
import com.ucb.mapexplorer.profile.domain.model.AvatarConfigModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay

@SuppressLint("MissingPermission")
@Composable
actual fun MapViewContainer(
    modifier: Modifier,
    state: MapUIState,
    navController: NavController,
    onLocationChanged: (Double, Double) -> Unit
) {
    val context = LocalContext.current

    // ── PERMISOS sin librería externa ─────────────────────────────────────
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { hasLocationPermission = it }

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
        if (!hasLocationPermission) permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    val currentLat      = rememberUpdatedState(state.userLat)
    val currentLon      = rememberUpdatedState(state.userLng)
    val discoveredTiles = rememberUpdatedState(state.discoveredTiles)
    val nearbyPlaces    = rememberUpdatedState(state.nearbyPlacesInMap)
    val avatarConfig    = rememberUpdatedState(state.avatarConfig)

    // ── MapView ───────────────────────────────────────────────────────────
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(20.0)
            setBackgroundColor(Color.BLACK)
            overlayManager.tilesOverlay.setColorFilter(
                ColorMatrixColorFilter(
                    ColorMatrix().apply { setSaturation(0.3f) }
                )
            )
        }
    }


    // ── Marcador del usuario (AVATAR) ─────────────────────────────────────
    val userMarker = remember {
        Marker(mapView).apply {
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            title = "Tú"
        }
    }

    // ── Actualización del Avatar Dinámico ─────────────────────────────────
    LaunchedEffect(avatarConfig.value) {
        // Ejecutar la renderización de las capas del avatar en segundo plano para no trabar la UI
        val bitmap = avatarBitmap(context, avatarConfig.value)
        userMarker.icon = BitmapDrawable(context.resources, bitmap)
        mapView.invalidate()
    }


// ── 🎯 MANEJAR "VER EN EL MAPA" (Centrado de cámara) ──
    LaunchedEffect(state.cameraTarget) {
        state.cameraTarget?.let { (lat, lon) ->
            val targetPoint = GeoPoint(lat, lon)
            mapView.controller.animateTo(targetPoint)
            mapView.controller.setZoom(18.5) // Un poco más de zoom para ver el detalle
        }
    }

    // ── Objetos de dibujo ─────────────────────────────────────────────────
    val fogPaint = remember { Paint().apply { color = Color.BLACK; alpha = 245 } }
    val holePaint = remember {
        Paint().apply {
            isAntiAlias = true
            xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
        }
    }
    val baseShader = remember {
        RadialGradient(0f, 0f, 1f, Color.BLACK, Color.TRANSPARENT, Shader.TileMode.CLAMP)
    }
    val shaderMatrix = remember { Matrix() }
    val pixelBuffer = remember { Point() }

    // ── Fog of War Overlay ────────────────────────────────────────────────
    val fogOverlay = remember {
        object : Overlay() {
            override fun draw(canvas: Canvas, map: MapView, shadow: Boolean) {
                if (shadow) return

                if (currentLat.value == 0.0 && currentLon.value == 0.0) {
                    canvas.drawRect(
                        0f, 0f,
                        map.width.toFloat(), map.height.toFloat(),
                        fogPaint
                    )
                    return
                }

                val saveCount = canvas.saveLayer(
                    0f, 0f,
                    map.width.toFloat(), map.height.toFloat(),
                    null
                )

                canvas.drawRect(
                    0f, 0f,
                    map.width.toFloat(), map.height.toFloat(),
                    fogPaint
                )

                val projection = map.projection
                val visibleBounds = map.boundingBox

                // Luz en posición actual
                drawGlow(
                    canvas, projection,
                    GeoPoint(currentLat.value, currentLon.value),
                    radiusMeters = 100f,
                    alpha = 255
                )

                // Abrir tiles ya descubiertos
                discoveredTiles.value.forEach { tile ->
                    val (lat, lon) = TileUtils.tileToCenterLatLng(
                        tile.tileX,
                        tile.tileY,
                        TileUtils.ZOOM
                    )
                    val geoPoint = GeoPoint(lat, lon)
                    if (visibleBounds.contains(geoPoint)) {
                        drawGlow(
                            canvas, projection,
                            geoPoint,
                            radiusMeters = 100f,
                            alpha = 230
                        )
                    }
                }

                canvas.restoreToCount(saveCount)
            }

            private fun drawGlow(
                canvas: Canvas,
                proj: org.osmdroid.views.Projection,
                pt: GeoPoint,
                radiusMeters: Float,
                alpha: Int = 255
            ) {
                proj.toPixels(pt, pixelBuffer)
                val radiusPx = proj.metersToPixels(radiusMeters)
                if (radiusPx <= 0f) return
                shaderMatrix.reset()
                shaderMatrix.postScale(radiusPx, radiusPx)
                shaderMatrix.postTranslate(
                    pixelBuffer.x.toFloat(),
                    pixelBuffer.y.toFloat()
                )
                baseShader.setLocalMatrix(shaderMatrix)
                holePaint.shader = baseShader
                holePaint.alpha = alpha
                canvas.drawCircle(
                    pixelBuffer.x.toFloat(),
                    pixelBuffer.y.toFloat(),
                    radiusPx,
                    holePaint
                )
            }
        }
    }

    // ── Agregar overlays ──────────────────────────────────────────────────
    LaunchedEffect(Unit) {
        if (!mapView.overlays.contains(fogOverlay)) mapView.overlays.add(fogOverlay)
        if (!mapView.overlays.contains(userMarker)) mapView.overlays.add(userMarker)
    }

    // ── Marcadores de lugares cercanos (con click → detalle) ──────────────
    val placeMarkers = remember { mutableStateListOf<Marker>() }

    LaunchedEffect(nearbyPlaces.value) {
        // Quitar marcadores viejos
        placeMarkers.forEach { mapView.overlays.remove(it) }
        placeMarkers.clear()

        nearbyPlaces.value.forEach { lugar ->
            val marker = Marker(mapView).apply {
                position  = GeoPoint(lugar.latitude, lugar.longitude)
                title     = lugar.name
                snippet   = lugar.category
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                icon = BitmapDrawable(context.resources, emojiBitmap(lugar.categoryIcon, 100))

                // ← CLICK en marcador → navega al detalle del lugar
                setOnMarkerClickListener { _, _ ->
                    navController.navigate(NavRoute.PlaceDetail(placeId = lugar.id))
                    true
                }
            }
            placeMarkers.add(marker)
            // Agregar ENCIMA del fog pero debajo del avatar del usuario
            val userMarkerIndex = mapView.overlays.indexOf(userMarker)
            if (userMarkerIndex >= 0) {
                mapView.overlays.add(userMarkerIndex, marker)
            } else {
                mapView.overlays.add(marker)
            }
        }
        mapView.invalidate()
    }

    // ── GPS — solo si tiene permiso ───────────────────────────────────────
    val locationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationCallback = remember {
        object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { onLocationChanged(it.latitude, it.longitude) }
            }
        }
    }

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val req = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2_000L)
                .setMinUpdateDistanceMeters(5f)
                .build()
            locationClient.requestLocationUpdates(req, locationCallback, null)
        }
    }

    // ── Centrar mapa ──────────────────────────────────────────────────────
    var isCentered by remember { mutableStateOf(false) }

    LaunchedEffect(state.userLat, state.userLng) {
        if (state.userLat != 0.0) {
            val point = GeoPoint(state.userLat, state.userLng)
            userMarker.position = point
            if (!isCentered) {
                mapView.controller.setCenter(point)
                mapView.controller.setZoom(20.0)
                isCentered = true
            } else {
                mapView.controller.animateTo(point)
            }
            mapView.invalidate()
        }
    }

    LaunchedEffect(state.discoveredTiles.size) {
        mapView.invalidate()
    }

    AndroidView(factory = { mapView }, modifier = modifier)
}
private fun avatarBitmap(context: Context, config: AvatarConfigModel): Bitmap {
    val size = 180
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)

    // Fondo circular suavizado
    val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FEF7FF")
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, bgPaint)

    // Borde blanco
    val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 8f
    }
    canvas.drawCircle(size / 2f, size / 2f, size / 2f - 4f, borderPaint)

    // FUNCIÓN INTERNA CORREGIDA
    fun drawLayer(resName: String?) {
        if (resName.isNullOrBlank()) return

        // CORRECCIÓN: Usamos 'context.resources' explícitamente
        val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)

        if (resId != 0) {
            // CORRECCIÓN: Pasamos el 'resId' real en lugar de la variable 'r' cortada
            ContextCompat.getDrawable(context, resId)?.let { drawable ->
                drawable.setBounds(15, 15, size - 15, size - 15)
                drawable.draw(canvas)
            }
        }
    }

    // Dibujar en orden de capas
    drawLayer(config.body.resourceName)
    drawLayer(config.hat.resourceName)
    drawLayer(config.accessory.resourceName)

    return bmp
}

private fun emojiBitmap(emoji: String, sizePx: Int = 120): Bitmap {
    val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)

    val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        setShadowLayer(4f, 0f, 2f, Color.argb(80, 0, 0, 0))
    }
    canvas.drawRoundRect(RectF(4f, 4f, sizePx - 4f, sizePx - 4f), 16f, 16f, bgPaint)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = sizePx * 0.52f
        textAlign = Paint.Align.CENTER
    }
    canvas.drawText(emoji, sizePx / 2f, sizePx * 0.72f, paint)
    return bmp
}