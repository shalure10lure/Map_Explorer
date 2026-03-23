package com.ucb.mapexplorer.map.presentation

import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.views.MapView
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint

@Composable
actual fun MapViewContainer() {

    AndroidView(factory = { context ->

        val map = MapView(context)

        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val controller = map.controller
        controller.setZoom(18.0)

        val startPoint = GeoPoint(-17.3895, -66.1568)
        controller.setCenter(startPoint)

        map.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        map
    })
}