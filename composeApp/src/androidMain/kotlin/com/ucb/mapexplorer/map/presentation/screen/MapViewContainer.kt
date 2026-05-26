package com.ucb.mapexplorer.map.presentation.screen

import android.graphics.*
import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay
import com.ucb.mapexplorer.map.presentation.state.MapUIState

@Composable
actual fun MapViewContainer(
    modifier: Modifier,
    state: MapUIState
) {
    val context = LocalContext.current

    Configuration.getInstance().userAgentValue = context.packageName

    val fogOverlay = remember { FogOfWarOverlay(state) }

    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)
            controller.setZoom(17.0)

            val initial = GeoPoint(-17.3936, -66.1570)
            controller.setCenter(initial)

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            overlays.add(fogOverlay)
        }
    }

    // actualizar overlay cuando cambia el estado
    LaunchedEffect(state) {
        fogOverlay.updateState(state)
        mapView.invalidate()
    }

    // mover cámara con la ubicación del usuario
    LaunchedEffect(state.currentLocation) {
        state.currentLocation?.let { location ->
            val point = GeoPoint(location.latitude, location.longitude)
            mapView.controller.animateTo(point)
        }
    }

    DisposableEffect(mapView) {
        onDispose { mapView.onDetach() }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
}

class FogOfWarOverlay(
    private var state: MapUIState
) : Overlay() {

    private val fogPaint = Paint().apply {
        color = Color.argb(220, 0, 0, 0) // fondo más oscuro
        style = Paint.Style.FILL
    }

    private val clearPaint = Paint().apply {
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        isAntiAlias = true
    }

    private val blurPaint = Paint().apply {
        color = Color.TRANSPARENT
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        maskFilter = BlurMaskFilter(80f, BlurMaskFilter.Blur.NORMAL)
        isAntiAlias = true
    }

    fun updateState(newState: MapUIState) {
        state = newState
    }

    override fun draw(canvas: Canvas, mapView: MapView, shadow: Boolean) {
        if (shadow) return

        val checkpoint = canvas.saveLayer(
            0f, 0f,
            canvas.width.toFloat(),
            canvas.height.toFloat(),
            null
        )

        // niebla total
        canvas.drawRect(
            0f, 0f,
            canvas.width.toFloat(),
            canvas.height.toFloat(),
            fogPaint
        )

        // tiles descubiertos
        state.discoveredTiles.forEach { tile ->
            val geoPoint = GeoPoint(tile.centerLat, tile.centerLng)
            val point = android.graphics.Point()
            mapView.projection.toPixels(geoPoint, point)

            // halo difuso
            canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), 240f, blurPaint)
            // centro limpio
            canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), 160f, clearPaint)
        }

        // ubicación actual
        state.currentLocation?.let { location ->
            val geoPoint = GeoPoint(location.latitude, location.longitude)
            val point = android.graphics.Point()
            mapView.projection.toPixels(geoPoint, point)

            canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), 280f, blurPaint)
            canvas.drawCircle(point.x.toFloat(), point.y.toFloat(), 180f, clearPaint)
        }

        canvas.restoreToCount(checkpoint)
    }
}
