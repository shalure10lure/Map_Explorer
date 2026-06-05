package com.ucb.mapexplorer.map.presentation.screen

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.*
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.*
import com.ucb.mapexplorer.core.utils.TileUtils
import com.ucb.mapexplorer.map.presentation.state.MapUIState
import com.ucb.mapexplorer.navigation.NavRoute
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
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
    }

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    // ── Estados actualizados para el overlay ──────────────────────────────
    val currentLat = rememberUpdatedState(state.userLat)
    val currentLon = rememberUpdatedState(state.userLng)
    val discoveredTiles = rememberUpdatedState(state.discoveredTiles)

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


    // ── Marcador del usuario ──────────────────────────────────────────────
    val userMarker = remember {
        Marker(mapView).apply {
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            title = "Tú"
        }
    }

// ── 🎨 GENERADOR DE BITMAP PARA EL AVATAR ──
    val userAvatarBitmap = remember(state.avatarConfig) {
        val size = 180 // Tamaño del marcador en pixeles
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Función auxiliar para dibujar capas
        fun drawLayer(resName: String?) {
            if (resName.isNullOrBlank()) return
            val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
            if (resId != 0) {
                val drawable = ContextCompat.getDrawable(context, resId)
                drawable?.let {
                    it.setBounds(0, 0, size, size)
                    it.draw(canvas)
                }
            }
        }

        // Dibujamos en orden: Cuerpo -> Sombrero -> Accesorio
        drawLayer(state.avatarConfig.body.resourceName)
        drawLayer(state.avatarConfig.hat.resourceName)
        drawLayer(state.avatarConfig.accessory.resourceName)

        bitmap
    }

// Actualizamos el icono del marcador cada vez que el bitmap cambie
    LaunchedEffect(userAvatarBitmap) {
        userMarker.icon = android.graphics.drawable.BitmapDrawable(context.resources, userAvatarBitmap)
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

    // ── 🌟 PINTRAR LUGARES CERCANOS FILTRADOS POR TILE DISPONIBLE 🌟 ──
    // Se ejecuta de manera limpia cada vez que cambia el listado en el state de tu ViewModel
    val placeMarkers = remember { mutableStateListOf<Marker>() }

    LaunchedEffect(state.nearbyPlacesInMap) {
        // 1. Limpiamos los marcadores antiguos de lugares del mapa para no duplicar
        placeMarkers.forEach { mapView.overlays.remove(it) }
        placeMarkers.clear()

        // 2. Recorremos tu lista limpia de lugares permitidos por tus Tiles descubiertos
        state.nearbyPlacesInMap.forEach { lugar ->
            val placeMarker = Marker(mapView).apply {
                position = GeoPoint(lugar.latitude, lugar.longitude)
                title = lugar.name
                snippet = lugar.category // Ej: "Estadio"

                // Colocamos el anclaje abajo en el centro para que flote bien
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

                // Convertir el emoji en un canvas legible e independiente para OsmDroid
                val paint = Paint().apply {
                    textSize = 90f // Tamaño del Emoji ideal para el mapa
                    textAlign = Paint.Align.CENTER
                }
                val bitmap = Bitmap.createBitmap(140, 140, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                // Usamos el campo correspondiente a tu emoji de categoría (Ej: lugar.categoryIcon o lugar.emoji)
                canvas.drawText(lugar.categoryIcon, 70f, 100f, paint)

                icon = android.graphics.drawable.BitmapDrawable(context.resources, bitmap)

                // 🎯 REGLA PRINCIPAL: Al presionar el Emoji, te manda a los Detalles (image_8cb5db.png)
                setOnMarkerClickListener { marker, _ ->
                    // Usamos la ruta tipada definida en tus rutas de Jetpack Navigation
                    navController.navigate(NavRoute.PlaceDetail(placeId = lugar.id))
                    true
                }
            }

            placeMarkers.add(placeMarker)
            mapView.overlays.add(placeMarker)
        }
        mapView.invalidate() // Forzar refresco visual
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